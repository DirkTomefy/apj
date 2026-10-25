<%@page import="a_tahina.page.AtahinaUtil"%>
<%@page import="a_tahina.page.AtahinaPageRecherche"%>
<%@page import="a_tahina.fronthelper.ic.ICListeFH"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    AtahinaPageRecherche pr = ICListeFH.getPageRechercheForList(request, session);
    out.println(pr.getHtmlListe());
} catch (Exception e) {
    out.println(AtahinaUtil.erreurHtml(e));
} %>
