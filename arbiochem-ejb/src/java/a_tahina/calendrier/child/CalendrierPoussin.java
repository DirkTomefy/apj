package a_tahina.calendrier.child;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import a_tahina.calendrier.CalendrierAbstrait;
import a_tahina.prevision.CalendrierPoussinCumulJour;
import a_tahina.prevision.Lot;
import bean.CGenUtil;


/**
 * Calendrier poussin base sur la vue SQL CALENDRIER_POUSSIN_CUMUL
 * (elle-meme construite sur CALENDRIER_POUSSIN_RAW).
 *
 * Chaque case affiche le cumul de poussins a la date ; le pied de tableau
 * donne les naissances du mois. Les regles metier (incubation, taux
 * d'eclosion, pertes) sont appliquees par la vue.
 *
 * Periode chargee :
 *  - si dateDebut / dateFin sont passes en parametre : cette periode ;
 *  - sinon : toute l'annee affichee.
 * Le cumul est calcule par la vue depuis le tout premier jour : il reste
 * exact meme si seule une partie de la periode est affichee.
 *
 * Le formulaire "declarer une perte" est dans le JSP, alimente par
 * la liste des lots femelles.
 */
public class CalendrierPoussin extends CalendrierAbstrait {

    private List<Lot> lotsFemelles = new ArrayList<>();
    private boolean periodeExplicite;

    public CalendrierPoussin(HttpServletRequest request) throws Exception {
        super(request);
        this.periodeExplicite = hasParam(request, "dateDebut") || hasParam(request, "dateFin");
        initAll();
    }

    private static boolean hasParam(HttpServletRequest request, String nom) {
        String p = request.getParameter(nom);
        return p != null && !p.isEmpty();
    }

    // ------------------------------------------------------------------
    // Libelles
    // ------------------------------------------------------------------
    @Override public String getTitre()        { return "Prevision globale des poussins (cumul)"; }
    @Override public String getLibelleTotal() { return "NAISSANCES DU MOIS"; }

    // ------------------------------------------------------------------
    // Chargement : lots pour le formulaire
    // ------------------------------------------------------------------
    @Override
    protected void charger() throws Exception {
        Object[] arrLots = CGenUtil.rechercher(new Lot(), null, null,
                " AND sexe = '0' ORDER BY id ");
        if (arrLots != null) {
            for (Object o : arrLots) {
                lotsFemelles.add((Lot) o);
            }
        }
    }

    // ------------------------------------------------------------------
    // Simulation : lecture directe de la vue cumul
    // ------------------------------------------------------------------
    @Override
    protected void simuler() throws Exception {
        LocalDate d1 = periodeExplicite ? debut : LocalDate.of(anneeAffichee, 1, 1);
        LocalDate d2 = periodeExplicite ? fin   : LocalDate.of(anneeAffichee, 12, 31);

        String where = " AND daty BETWEEN TO_DATE('" + d1 + "','YYYY-MM-DD')"
                     + " AND TO_DATE('" + d2 + "','YYYY-MM-DD')";

        Object[] arr = CGenUtil.rechercher(new CalendrierPoussinCumulJour(), null, null, where);
        if (arr == null) return;

        for (Object o : arr) {
            CalendrierPoussinCumulJour r = (CalendrierPoussinCumulJour) o;
            if (r.getDaty() == null) continue;

            LocalDate d = r.getDaty().toLocalDate();
            CellulePoussin cell = new CellulePoussin(
                    libelleJourSemaine(d),
                    r.getPoussins(),
                    r.getPoussinscumules(),
                    r.getPertes(),
                    estDimanche(d));
            ajouterCellule(d, cell);
        }
    }

    // ------------------------------------------------------------------
    // Accesseurs JSP
    // ------------------------------------------------------------------
    public List<Lot> getLotsFemelles() { return lotsFemelles; }
}