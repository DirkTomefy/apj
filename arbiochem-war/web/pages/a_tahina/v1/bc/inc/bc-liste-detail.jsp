<%@page import="a_tahina.core.page.AtahinaUtil"%>
<%@page import="a_tahina.bc.fh.BCmdFH"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    out.println(BCmdFH.ongletDetail(request, session, request.getParameter("idMere")));
} catch (Exception e) {
    out.println(AtahinaUtil.erreurHtml(e));
} %>
