<%@page import="a_tahina.page.AtahinaUtil"%>
<%@page import="a_tahina.fronthelper.facture.ClientFH"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    out.println(ClientFH.ongletFactures(request, session, request.getParameter("idMere")));
} catch (Exception e) {
    out.println(AtahinaUtil.erreurHtml(e));
} %>
