package a_tahina.fronthelper.compartiment;

import static a_tahina.page.AtahinaChamp.champ;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.ic.SituationCompartiment;
import a_tahina.page.AtahinaPageRecherche;

public class SituationCompListFH {

    public static final String PAGE_ACTUEL = "a_tahina/v1/compartiment/situation.jsp";

    public static AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                               HttpSession session) throws Exception {
        return new AtahinaPageRecherche(new SituationCompartiment(), request, session,
                PAGE_ACTUEL, "Situation par compartiment",
                champ("compid",           "Compartiment").recherche(),
                champ("qtetheorique",     "Qt&eacute; th&eacute;orique"),
                champ("qteactuelle",      "Qt&eacute; actuelle"),
                champ("pu",               "Prix unitaire"),
                champ("qtebc",            "Qt&eacute; BC"),
                champ("sommeqteactuelle", "Somme qt&eacute; actuelle"),
                champ("valeurunitaire",   "Valeur unitaire"))
            .construire();
    }
}
