package a_tahina.calendrier;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

import javax.servlet.http.HttpServletRequest;

import bean.CGenUtil;
import bean.ClassMAPTable;

public abstract class Calendrier<T extends ClassMAPTable> {

    private static final DateTimeFormatter FORMAT_APJ = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final T critere;
    private final String colonneDate;
    private final String contextPath;

    private LocalDate debut;
    private LocalDate fin;
    private int annee;
    private boolean periodeExplicite;

    private String titre = "";
    private ToDoubleFunction<T> valeur;
    private String libelleTotal;
    private ToDoubleFunction<T> total;
    private final List<Indicateur<T>> indicateurs = new ArrayList<Indicateur<T>>();
    private final List<Style<T>> styles = new ArrayList<Style<T>>();

    private Map<LocalDate, T> jours;

    protected Calendrier(HttpServletRequest request, T critere, String colonneDate) {
        this.critere = critere;
        this.colonneDate = colonneDate;
        this.contextPath = request.getContextPath() == null ? "" : request.getContextPath();
        lirePeriode(request);
    }

    // ------------------------------------------------------------------
    // Declarations
    // ------------------------------------------------------------------
    protected void titre(String titre)                      { this.titre = titre; }
    protected void valeur(ToDoubleFunction<T> valeur)       { this.valeur = valeur; }

    protected void total(String libelle, ToDoubleFunction<T> total) {
        this.libelleTotal = libelle;
        this.total = total;
    }

    protected void indicateur(String libelle, ToDoubleFunction<T> valeur) {
        indicateurs.add(new Indicateur<T>(libelle, valeur, false));
    }

    protected void indicateurSigne(String libelle, ToDoubleFunction<T> valeur) {
        indicateurs.add(new Indicateur<T>(libelle, valeur, true));
    }

    protected void style(String classeCss, String legende, Predicate<T> regle) {
        styles.add(new Style<T>(classeCss, legende, regle));
    }

    // ------------------------------------------------------------------
    // Periode
    // ------------------------------------------------------------------
    private void lirePeriode(HttpServletRequest request) {
        debut = date(request.getParameter("dateDebut"), LocalDate.now());
        fin   = date(request.getParameter("dateFin"), debut.plusDays(60));
        if (fin.isBefore(debut)) fin = debut;
        periodeExplicite = present(request, "dateDebut") || present(request, "dateFin");

        annee = debut.getYear();
        try {
            if (present(request, "annee")) annee = Integer.parseInt(request.getParameter("annee").trim());
        } catch (NumberFormatException ignore) {
        }
    }

    private static boolean present(HttpServletRequest r, String nom) {
        String p = r.getParameter(nom);
        return p != null && !p.trim().isEmpty();
    }

    private static LocalDate date(String valeur, LocalDate defaut) {
        try {
            return (valeur == null || valeur.trim().isEmpty()) ? defaut : LocalDate.parse(valeur.trim());
        } catch (Exception e) {
            return defaut;
        }
    }

    public LocalDate getDebut()     { return debut; }
    public LocalDate getFin()       { return fin; }
    public int getAnneeAffichee()   { return annee; }
    public String getTitre()        { return titre; }

    // ------------------------------------------------------------------
    // Donnees : CGenUtil.rechercher avec un intervalle sur la colonne date
    // ------------------------------------------------------------------
    private Map<LocalDate, T> jours() throws Exception {
        if (jours != null) return jours;
        LocalDate d1 = periodeExplicite ? debut : LocalDate.of(annee, 1, 1);
        LocalDate d2 = periodeExplicite ? fin   : LocalDate.of(annee, 12, 31);

        Object[] lignes = CGenUtil.rechercher(critere,
                new String[] { colonneDate },
                new String[] { d1.format(FORMAT_APJ), d2.format(FORMAT_APJ) },
                " order by " + colonneDate);

        jours = new HashMap<LocalDate, T>();
        if (lignes != null) {
            for (Object o : lignes) {
                @SuppressWarnings("unchecked")
                T ligne = (T) o;
                Object d = CGenUtil.getValeurFieldByMethod(ligne, colonneDate);
                if (d instanceof java.sql.Date) jours.put(((java.sql.Date) d).toLocalDate(), ligne);
            }
        }
        return jours;
    }

    // ------------------------------------------------------------------
    // Acces pour le rendu
    // ------------------------------------------------------------------
    T jour(LocalDate date) throws Exception {
        if (date.getYear() != annee) return null;
        return jours().get(date);
    }

    double valeurCase(T jour)       { return valeur == null ? 0 : valeur.applyAsDouble(jour); }
    String getLibelleTotal()        { return libelleTotal; }
    List<Indicateur<T>> getIndicateurs() { return Collections.unmodifiableList(indicateurs); }
    List<Style<T>> getStyles()      { return Collections.unmodifiableList(styles); }
    String getContextPath()         { return contextPath; }

    String classeCss(T jour) {
        for (Style<T> s : styles) {
            if (s.regle.test(jour)) return s.classe;
        }
        return "";
    }

    double totalMois(int mois) throws Exception {
        return somme(mois, total);
    }

    double somme(int mois, ToDoubleFunction<T> f) throws Exception {
        if (f == null) return 0;
        double t = 0;
        for (Map.Entry<LocalDate, T> e : jours().entrySet()) {
            if (e.getKey().getYear() == annee && e.getKey().getMonthValue() == mois) t += f.applyAsDouble(e.getValue());
        }
        return t;
    }

    public String generateHtml() throws Exception {
        return new CalendrierHtml<T>(this).rendu();
    }

    // ------------------------------------------------------------------
    static final class Indicateur<T> {
        final String libelle;
        final ToDoubleFunction<T> valeur;
        final boolean signe;

        Indicateur(String libelle, ToDoubleFunction<T> valeur, boolean signe) {
            this.libelle = libelle;
            this.valeur = valeur;
            this.signe = signe;
        }
    }

    static final class Style<T> {
        final String classe;
        final String legende;
        final Predicate<T> regle;

        Style(String classe, String legende, Predicate<T> regle) {
            this.classe = classe;
            this.legende = legende;
            this.regle = regle;
        }
    }
}
