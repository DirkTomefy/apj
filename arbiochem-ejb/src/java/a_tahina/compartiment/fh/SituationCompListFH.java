package a_tahina.compartiment.fh;

import static a_tahina.core.page.AtahinaChampHelper.champ;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.calendrier.Calendrier;
import a_tahina.core.GeneraticFH;
import a_tahina.core.page.AtahinaPageConsulte;
import a_tahina.core.page.AtahinaPageInsertMultiple;
import a_tahina.core.page.AtahinaPageInsert;
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

    @Override
    public AtahinaPageInsert getPageInsert(HttpServletRequest request,
                                           HttpSession session) throws Exception {
        throw nonSupporte("getPageInsert");
    }

    @Override
    public AtahinaPageInsertMultiple getPageInsertMultiple(HttpServletRequest request,
                                                           HttpSession session,
                                                           int nombreLigne) throws Exception {
        throw nonSupporte("getPageInsertMultiple");
    }

    @Override
    public AtahinaPageConsulte getPageConsulte(HttpServletRequest request,
                                               HttpSession session) throws Exception {
        throw nonSupporte("getPageConsulte");
    }

    @Override
    public Calendrier<?> getCalendrier(HttpServletRequest request,
                                            HttpSession session) throws Exception {
        throw nonSupporte("getCalendrier");
    }
}
