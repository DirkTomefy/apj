package a_tahina.calendrier.child;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import a_tahina.calendrier.CalendrierAbstrait;
import a_tahina.prevision.CalendrierPoussinJour;
import a_tahina.prevision.Lot;
import bean.CGenUtil;


/**
 * Calendrier poussin base sur la vue SQL CALENDRIER_POUSSIN_RAW.
 *
 * Le calendrier lit directement la vue pre-calculee (qui applique
 * les regles metier : duree d'incubation, taux d'eclosion, pertes).
 *
 * Le formulaire "declarer une perte" est dans le JSP, alimente par
 * la liste des lots femelles.
 */
public class CalendrierPoussin extends CalendrierAbstrait {

    private List<Lot> lotsFemelles = new ArrayList<>();

    public CalendrierPoussin(HttpServletRequest request) throws Exception {
        super(request);
        initAll();
    }

    // ------------------------------------------------------------------
    // Libelles
    // ------------------------------------------------------------------
    @Override public String getTitre()        { return "Prevision globale des poussins"; }
    @Override public String getLibelleTotal() { return "TOTAL POUSSINS"; }

    // ------------------------------------------------------------------
    // Chargement : lots pour le formulaire + calendrier via la vue
    // ------------------------------------------------------------------
    @Override
    protected void charger() throws Exception {
        // 1. Liste des lots femelles (pour le formulaire "declarer une perte")
        Object[] arrLots = CGenUtil.rechercher(new Lot(), null, null,
                " AND sexe = '0' ORDER BY id ");
        if (arrLots != null) {
            for (Object o : arrLots) {
                lotsFemelles.add((Lot) o);
            }
        }
    }

    // ------------------------------------------------------------------
    // Simulation : lecture directe de la vue
    // ------------------------------------------------------------------
    @Override
    protected void simuler() throws Exception {
        String where = " AND daty BETWEEN TO_DATE('" + debut + "','YYYY-MM-DD')"
                     + " AND TO_DATE('" + fin + "','YYYY-MM-DD')";

        Object[] arr = CGenUtil.rechercher(new CalendrierPoussinJour(), null, null, where);
        if (arr == null) return;

        for (Object o : arr) {
            CalendrierPoussinJour r = (CalendrierPoussinJour) o;
            if (r.getDaty() == null) continue;

            LocalDate d = r.getDaty().toLocalDate();
            CellulePoussin cell = new CellulePoussin(
                    libelleJourSemaine(d),
                    r.getPoussins(),
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