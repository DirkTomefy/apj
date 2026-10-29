package a_tahina.compartiment.fh;

import static a_tahina.core.page.AtahinaChampHelper.champ;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.core.GeneraticFH;
import a_tahina.compartiment.orm.SituationCompartiment;
import a_tahina.core.page.AtahinaPageRecherche;

public class SituationCompListFH extends GeneraticFH {

    public static final String PAGE_ACTUEL = "a_tahina/v1/compartiment/situation.jsp";

    @Override
    public AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                        HttpSession session) throws Exception {
        return liste(new SituationCompartiment(), request, session,
                PAGE_ACTUEL, "Situation par compartiment",
                champ("compid",           "Compartiment").recherche(),
                champ("qtetheorique",     "Qt&eacute; th&eacute;orique"),
                champ("qteactuelle",      "Qt&eacute; actuelle"),
                champ("pu",               "Prix unitaire"),
                champ("qtebc",            "Qt&eacute; BC"),
                champ("sommeqteactuelle", "Somme qt&eacute; actuelle"),
                champ("valeurunitaire",   "Valeur unitaire"));
    }
}
