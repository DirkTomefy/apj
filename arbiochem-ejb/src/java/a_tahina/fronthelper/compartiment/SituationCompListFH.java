package a_tahina.fronthelper.compartiment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.ic.SituationCompartiment;
import affichage.PageRecherche;

public class SituationCompListFH {

    public static final String PAGE_ACTUEL = "a_tahina/v1/compartiment/situation.jsp";

    // -------- Config --------
    public static void prepareGlobaleInf(PageRecherche pr, HttpSession session) {
        pr.setTitre("Situation par compartiment");
        pr.setUtilisateur((user.UserEJB) session.getValue("u"));
        pr.setLien((String) session.getValue("lien"));
        pr.setApres(PAGE_ACTUEL);
        pr.setNpp(50);
    }

    public static void prepareLibelleChamp(PageRecherche pr) {
        pr.getFormu().getChamp("compid").setLibelle("Compartiment");
    }

    // -------- Page de recherche --------
    public static PageRecherche getPageRechercheForList(HttpServletRequest request,
                                                        HttpSession session) throws Exception {

        SituationCompartiment f = new SituationCompartiment();
        f.setNomTable("SITUATION_COMPARTIMENT");

        // Criteres de recherche : uniquement la colonne texte
        String[] listeCrt  = { "compid" };
        String[] listeInt  = { };
        // Colonnes a afficher (noms = champs Java, sans underscore)
        String[] libEntete = {
                "compid",
                "qtetheorique",
                "qteactuelle",
                "pu",
                "qtebc",
                "sommeqteactuelle",
                "valeurunitaire"
        };

        PageRecherche pr = new PageRecherche(f, request, listeCrt, listeInt, 3,
                libEntete, libEntete.length);

        prepareGlobaleInf(pr, session);
        prepareLibelleChamp(pr);

        pr.creerObjetPage(libEntete, null);

        // pr.getTableau().setLibelleAffiche(new String[]{
        //         "Compartiment",
        //         "Qte theorique",
        //         "Qte actuelle",
        //         "Prix unitaire",
        //         "Qte BC",
        //         "Somme qte actuelle",
        //         "Valeur unitaire"
        // });

        return pr;
    }
}