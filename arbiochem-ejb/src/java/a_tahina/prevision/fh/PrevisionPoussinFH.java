package a_tahina.prevision.fh;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.core.GeneraticFH;
import a_tahina.core.page.AtahinaPageRecherche;
import a_tahina.core.page.AtahinaPageConsulte;
import a_tahina.core.page.AtahinaPageInsertMultiple;
import a_tahina.core.page.AtahinaPageInsert;
import a_tahina.calendrier.child.CalendrierPoussin;


public class PrevisionPoussinFH extends GeneraticFH {

    public static final String PAGE_ACTUEL = "a_tahina/v1/prevision/prevision-poussin.jsp";

    @Override
    public CalendrierPoussin getCalendrier(HttpServletRequest request,
                                           HttpSession session) throws Exception {
        return new CalendrierPoussin(request);
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
    public AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                        HttpSession session) throws Exception {
        throw nonSupporte("getPageRechercheForList");
    }
}
