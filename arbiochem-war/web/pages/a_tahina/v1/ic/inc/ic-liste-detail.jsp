<%@page import="a_tahina.core.page.AtahinaUtil"%>
<%@page import="a_tahina.ic.fh.ICFH"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    out.println(ICFH.ongletDetail(request, session, request.getParameter("idMere")));
} catch (Exception e) {
    out.println(AtahinaUtil.erreurHtml(e));
} %>
