<%@page import="affichage.PageRecherche"%>
<%@page import="a_tahina.fronthelper.br.BRListeFH"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    PageRecherche pr = BRListeFH.getPageRechercheForList(request, session);
%>
<div class="content-wrapper">
  <h1><%= pr.getTitre() %></h1>
  <form action="<%= pr.getLien() %>?but=<%= pr.getApres() %>" method="post" name="filtre">
    <% out.println(pr.getFormu().getHtmlEnsemble()); %>
  </form>
  <% out.println(pr.getTableauRecap().getHtml()); %>
  <% out.println(pr.getTableau().getHtml()); %>
  <% out.println(pr.getBasPage()); %>
</div>
<% } catch(Exception e) {
     out.println("<pre style='color:red'>");
     e.printStackTrace(new java.io.PrintWriter(out));
     out.println("</pre>");
   } %>