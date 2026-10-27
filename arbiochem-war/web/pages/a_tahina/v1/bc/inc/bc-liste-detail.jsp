<%@page import="a_tahina.page.AtahinaUtil"%>
<%@page import="a_tahina.fronthelper.bc.BCmdFicheFH"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    out.println(BCmdFicheFH.ongletDetail(request, session, request.getParameter("idMere")));
} catch (Exception e) {
    out.println(AtahinaUtil.erreurHtml(e));
} %>
