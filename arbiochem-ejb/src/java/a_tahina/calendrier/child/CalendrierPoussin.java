package a_tahina.calendrier.child;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import a_tahina.calendrier.CalendrierAbstrait;
import a_tahina.calendrier.helper.Indicateur;
import a_tahina.prevision.CalendrierPoussinCumulJour;
import bean.CGenUtil;

public class CalendrierPoussin extends CalendrierAbstrait {

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
    @Override public String getLibelleTotal() { return "NAISSANCES"; }

    /** Lignes de pied de tableau en plus des naissances : CA et benefice du mois. */
    @Override
    public List<Indicateur> getIndicateurs() {
        return Arrays.asList(
                new Indicateur(CellulePoussin.IND_CA,       "CA",       false),
                new Indicateur(CellulePoussin.IND_BENEFICE, "BENEFICE", true)
            );
    }

    // ------------------------------------------------------------------
    // Chargement : rien a precharger, tout vient de la vue cumul (cf. simuler)
    // ------------------------------------------------------------------
    @Override
    protected void charger() {}

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
                    r.getCa(),
                    r.getBenefice(),
                    estDimanche(d));
            ajouterCellule(d, cell);
        }
    }
}
