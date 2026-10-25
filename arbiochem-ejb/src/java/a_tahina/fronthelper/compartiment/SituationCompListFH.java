package a_tahina.fronthelper.compartiment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.ic.SituationCompartiment;
import a_tahina.page.AtahinaPageRecherche;

public class SituationCompListFH {

    public static final String PAGE_ACTUEL = "a_tahina/v1/compartiment/situation.jsp";

    public static AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                               HttpSession session) throws Exception {
        // Criteres de recherche : uniquement la colonne texte
        String[] listeCrt  = { "compid" };
        String[] listeInt  = { };
        String[] libEntete = { "compid", "qtetheorique", "qteactuelle", "pu",
                               "qtebc", "sommeqteactuelle", "valeurunitaire" };

        AtahinaPageRecherche pr = new AtahinaPageRecherche(new SituationCompartiment(), request, session,
                PAGE_ACTUEL, "Situation par compartiment", listeCrt, listeInt, libEntete);

        return pr.libelle("compid", "Compartiment")
                 .construire();
    }
}
