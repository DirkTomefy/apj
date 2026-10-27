<%@page import="a_tahina.core.page.AtahinaUtil"%>
<%@page import="a_tahina.br.fh.BRFicheFH"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    out.println(BRFicheFH.ongletDetail(request, session, request.getParameter("idMere")));
} catch (Exception e) {
    out.println(AtahinaUtil.erreurHtml(e));
} %>
