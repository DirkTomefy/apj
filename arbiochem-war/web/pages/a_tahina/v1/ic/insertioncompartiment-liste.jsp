<%@page import="a_tahina.core.page.AtahinaUtil"%>
<%@page import="a_tahina.core.page.AtahinaPageRecherche"%>
<%@page import="a_tahina.ic.fh.ICFH"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    AtahinaPageRecherche pr = new ICFH().getPageRechercheForList(request, session);
    out.println(pr.getHtmlListe());
} catch (Exception e) {
    out.println(AtahinaUtil.erreurHtml(e));
} %>
