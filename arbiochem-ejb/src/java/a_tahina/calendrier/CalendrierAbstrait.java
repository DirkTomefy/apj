package a_tahina.calendrier;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import a_tahina.calendrier.helper.Indicateur;
import a_tahina.calendrier.helper.Mois;

public abstract class CalendrierAbstrait {

    // ==================================================================
    // Champs communs
    // ==================================================================
    protected int         anneeAffichee;
    protected LocalDate   debut;
    protected LocalDate   fin;
    protected List<Mois>  moisList = new ArrayList<>();
    protected int[]       lignes;

    // ==================================================================
    // Constructeur : lit les parametres + init structure
    // ==================================================================
    protected CalendrierAbstrait(HttpServletRequest request) {
        initPeriode(request);
        initMois();
        initLignes();
    }

    // ==================================================================
    // Contrat minimal de la sous-classe
    // ==================================================================
    public abstract String getTitre();
    public abstract String getLibelleTotal();
    protected abstract void charger() throws Exception;
    protected abstract void simuler() throws Exception;

    /**
     * Indicateurs affiches en pied de tableau, sous la ligne de total.
     * Aucun par defaut : le pied reste identique a l'ancien comportement.
     */
    public List<Indicateur> getIndicateurs() {
        return Collections.emptyList();
    }

    /** A appeler dans le constructeur de la sous-classe APRES super(request). */
    protected final void initAll() throws Exception {
        charger();
        simuler();
    }

    // ==================================================================
    // Initialisation generique
    // ==================================================================
    private void initPeriode(HttpServletRequest request) {
        debut = LocalDate.now();
        fin   = debut.plusDays(60);

        try {
            String p = request.getParameter("dateDebut");
            if (p != null && !p.isEmpty()) debut = LocalDate.parse(p);
        } catch (Exception ignore) {}

        try {
            String p = request.getParameter("dateFin");
            if (p != null && !p.isEmpty()) fin = LocalDate.parse(p);
        } catch (Exception ignore) {}

        if (fin.isBefore(debut)) fin = debut;

        anneeAffichee = debut.getYear();
        try {
            String p = request.getParameter("annee");
            if (p != null && !p.isEmpty()) anneeAffichee = Integer.parseInt(p);
        } catch (Exception ignore) {}
    }

    private void initMois() {
        for (int m = 1; m <= 12; m++) {
            LocalDate ref = LocalDate.of(anneeAffichee, m, 1);
            String nom = ref.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH);
            String libelle = capitalize(nom) + " " + anneeAffichee;
            String nomCourt = ref.getMonth().getDisplayName(TextStyle.SHORT, Locale.FRENCH);
            String libelleCourt = capitalize(nomCourt).replace(".", "").toUpperCase()
                                  + " " + anneeAffichee;
            moisList.add(new Mois(anneeAffichee, m, libelle, libelleCourt));
        }
    }

    private void initLignes() {
        lignes = new int[31];
        for (int i = 0; i < 31; i++) lignes[i] = i + 1;
    }

    // ==================================================================
    // Helpers communs
    // ==================================================================
    public void ajouterCellule(LocalDate date, CelluleCalendrierAbstraite cell) {
        if (date == null || date.getYear() != anneeAffichee) return;
        Mois mois = moisList.get(date.getMonthValue() - 1);
        mois.ajouterCellule(date.getDayOfMonth(), cell);
    }

    protected String libelleJourSemaine(LocalDate date) {
        return date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.FRENCH)
                   .replace(".", "") + ".";
    }

    protected boolean estDimanche(LocalDate date) {
        return date.getDayOfWeek().getValue() == 7;
    }

    protected static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    // ==================================================================
    // Accesseurs JSP
    // ==================================================================
    public List<Mois> getMoisList()      { return moisList; }
    public int[]      getListeLignes()   { return lignes; }
    public LocalDate  getDebut()         { return debut; }
    public LocalDate  getFin()           { return fin; }
    public int        getAnneeAffichee() { return anneeAffichee; }

    public CelluleCalendrierAbstraite getCellule(int jour, Mois mois) {
        return mois.getCellule(jour);
    }

    public double getTotalMois(Mois mois) {
        double t = 0;
        for (CelluleCalendrierAbstraite c : mois.getCellules().values()) {
            t += c.getValeurNumerique();
        }
        return t;
    }

    /** Somme, sur le mois, de l'indicateur de cle donnee (valeurs negatives possibles). */
    public double getTotalIndicateur(Mois mois, String cle) {
        double t = 0;
        for (CelluleCalendrierAbstraite c : mois.getCellules().values()) {
            Double v = c.getIndicateurs().get(cle);
            if (v != null) t += v;
        }
        return t;
    }

    // ==================================================================
    // CSS
    // ==================================================================
    protected String getCss() {
        return "<style>\n"
            + ".table-container { max-width:100%; max-height:80vh; overflow:auto; border:1px solid #ccc; margin-top:10px; }\n"
            + "table.cal { border-collapse:collapse; width:100%; table-layout:auto; min-width:fit-content; }\n"
            + "table.cal th, table.cal td { border:1px solid #999; padding:5px; font-size:12px; text-align:right; }\n"
            + "table.cal th { background:#f2f2f2; text-align:center; position:sticky; top:0; z-index:2; }\n"
            + "table.cal td.jour-cell { text-align:center; }\n"
            + "table.cal td.jour { text-align:left; position:sticky; left:0; background:#fff; z-index:3; min-width:80px; max-width:80px; white-space:nowrap; }\n"
            + ".total { font-weight:bold; background:#eef; }\n"
            + ".bold { font-weight:bold; }\n"
            + ".rouge   { background:#fbb; color:#800; font-weight:bold; }\n"
            + ".vert    { background:#cfc; color:#060; }\n"
            + ".jaune   { background:#ffd; color:#880; }\n"
            + ".orange  { background:#fed; color:#850; }\n"
            + ".bleu    { background:#cef; color:#006; }\n"
            + ".violet  { background:#ecf; color:#606; }\n"
            + ".gris    { background:#eee; color:#333; }\n"
            + ".noir    { background:#333; color:#fff; }\n"
            + ".blanc   { background:#fff; color:#000; }\n"
            + ".rose    { background:#fce; color:#800; }\n"
            + ".dimanche{ background:#fdd; }\n"
            + "</style>\n";
    }

    // ==================================================================
    // Generation HTML
    // ==================================================================
    public String generateHtml() {
        StringBuilder sb = new StringBuilder();

        sb.append(getCss());

        sb.append("<div class=\"table-container\">\n");
        sb.append("  <table class=\"cal\">\n");

        // En-tete
        sb.append("    <thead>\n      <tr>\n")
          .append("        <th class=\"jour\">Jours</th>\n");
        for (Mois m : moisList) {
            sb.append("        <th class=\"jour-cell\">Jour</th>\n")
              .append("        <th class=\"jour-cell\">")
              .append(esc(m.getLibelleCourt())).append("</th>\n");
        }
        sb.append("      </tr>\n    </thead>\n");

        // Corps
        sb.append("    <tbody>\n");
        for (int jour : lignes) {
            sb.append("      <tr>\n")
              .append("        <td class=\"jour\">").append(jour).append("</td>\n");

            for (Mois m : moisList) {
                boolean jourExiste = false;
                try { LocalDate.of(m.getAnnee(), m.getMois(), jour); jourExiste = true; }
                catch (Exception ignore) { jourExiste = false; }

                if (!jourExiste) {
                    sb.append("        <td class=\"jour-cell\">-</td>\n")
                      .append("        <td class=\"jour-cell\">-</td>\n");
                    continue;
                }

                CelluleCalendrierAbstraite c = getCellule(jour, m);
                if (c == null || !c.isAffichee()) {
                    sb.append("        <td class=\"jour-cell\">-</td>\n")
                      .append("        <td class=\"jour-cell\">-</td>\n");
                    continue;
                }

                String css   = c.getCssClass() == null ? "" : c.getCssClass();
                String style = c.getStyle();
                String styleAttr = (style == null || style.isEmpty())
                                   ? "" : " style=\"" + esc(style) + "\"";

                sb.append("        <td class=\"jour-cell ").append(css).append("\"").append(styleAttr).append(">")
                  .append(esc(c.getLibelleCourt())).append("</td>\n")
                  .append("        <td class=\"jour-cell ").append(css).append("\"").append(styleAttr).append(">")
                  .append(esc(c.getValeurAffichee())).append("</td>\n");
            }
            sb.append("      </tr>\n");
        }
        sb.append("    </tbody>\n");

        // Pied : ligne de total puis une ligne par indicateur
        sb.append("    <tfoot>\n      <tr class=\"total\">\n")
          .append("        <td class=\"jour\">").append(esc(getLibelleTotal())).append("</td>\n");
        for (Mois m : moisList) {
            sb.append("        <td class=\"jour-cell\" colspan=\"2\">")
              .append(fmt(getTotalMois(m))).append("</td>\n");
        }
        sb.append("      </tr>\n");

        for (Indicateur ind : getIndicateurs()) {
            sb.append("      <tr class=\"total\">\n")
              .append("        <td class=\"jour\">").append(esc(ind.getLibelle())).append("</td>\n");
            for (Mois m : moisList) {
                double v = getTotalIndicateur(m, ind.getCle());
                String cls = "";
                if (ind.isColorSigne()) {
                    if (v < -0.5)     cls = " rouge";
                    else if (v > 0.5) cls = " vert";
                }
                sb.append("        <td class=\"jour-cell").append(cls).append("\" colspan=\"2\">")
                  .append(fmtSigne(v)).append("</td>\n");
            }
            sb.append("      </tr>\n");
        }
        sb.append("    </tfoot>\n");

        sb.append("  </table>\n</div>\n");
        return sb.toString();
    }

    // ==================================================================
    // Formatage
    // ==================================================================
    protected String fmt(double v) {
        if (v <= 0) return "-";
        return String.format("%,.0f", v);
    }

    /** Comme fmt, mais les valeurs negatives sont affichees (ex: benefice negatif). */
    protected String fmtSigne(double v) {
        if (Math.abs(v) < 0.5) return "-";
        return String.format("%,.0f", v);
    }

    protected String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}