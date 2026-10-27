<%@page import="a_tahina.core.page.*"%>
<%@page import="a_tahina.facture.fh.ProduitFH"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    AtahinaPageInsert pi = new ProduitFH().getPageInsert(request, session);
%>
<div class="content-wrapper">
  <h1><%= pi.getTitre() %></h1>

  <form action="<%= pi.getLien() %>?but=apresTarif.jsp" method="post">
    <% out.println(pi.getFormu().getHtmlInsert()); %>

    <input type="hidden" name="acte"   value="insert" />
    <input type="hidden" name="bute"   value="<%= ProduitFH.PAGE_LISTE %>" />
    <input type="hidden" name="classe" value="<%= ProduitFH.CLASSE %>" />
  </form>
</div>
<% } catch (Exception e) {
     out.println(AtahinaUtil.erreurHtml(e));
   } %>
