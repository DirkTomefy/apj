package a_tahina.prevision.fh;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.core.GeneraticFH;
import a_tahina.prevision.calendrier.CalendrierPoussin;

public class PrevisionPoussinFH extends GeneraticFH {

    public static final String PAGE_ACTUEL = "a_tahina/v1/prevision/prevision-poussin.jsp";

    @Override
    public CalendrierPoussin getCalendrier(HttpServletRequest request,
                                           HttpSession session) throws Exception {
        return new CalendrierPoussin(request);
    }
}
