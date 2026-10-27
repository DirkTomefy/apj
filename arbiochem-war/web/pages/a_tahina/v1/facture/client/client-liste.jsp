<%@page import="a_tahina.core.page.AtahinaUtil"%>
<%@page import="a_tahina.core.page.AtahinaPageRecherche"%>
<%@page import="a_tahina.facture.fh.ClientFH"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    AtahinaPageRecherche pr = new ClientFH().getPageRechercheForList(request, session);
    out.println(pr.getHtmlListe());
} catch (Exception e) {
    out.println(AtahinaUtil.erreurHtml(e));
} %>
