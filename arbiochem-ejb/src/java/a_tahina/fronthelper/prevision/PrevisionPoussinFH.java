package a_tahina.fronthelper.prevision;

import a_tahina.fronthelper.GeneraticFH;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.calendrier.child.CalendrierPoussin;

public class PrevisionPoussinFH extends GeneraticFH {

    public static final String PAGE_ACTUEL = "a_tahina/v1/prevision/prevision-poussin.jsp";

    public static CalendrierPoussin getCalendrier(HttpServletRequest request,
                                                  HttpSession session) throws Exception {
        return new CalendrierPoussin(request);
    }
}