<%@page import="a_tahina.core.page.AtahinaUtil"%>
<%@page import="a_tahina.facture.fh.AvoirFH"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    out.println(AvoirFH.ongletUsages(request, session, request.getParameter("idMere")));
} catch (Exception e) {
    out.println(AtahinaUtil.erreurHtml(e));
} %>
