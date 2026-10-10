<%@page import="affichage.PageRecherche"%>
<%@page import="a_tahina.fronthelper.ic.ICListeFH"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    PageRecherche pr = ICListeFH.getPageRechercheForList(request, session);
%>
<div class="content-wrapper">
  <section class="content-header">
    <h1><%= pr.getTitre() %></h1>
  </section>
  <section class="content">
    <form action="<%= pr.getLien() %>?but=<%= pr.getApres() %>"
          method="post" name="filtre">
      <% out.println(pr.getFormu().getHtmlEnsemble()); %>
    </form>
    <% out.println(pr.getTableauRecap().getHtml()); %>
    <br>
    <% out.println(pr.getTableau().getHtml());
       out.println(pr.getBasPage()); %>
  </section>
</div>
<% } catch(Exception e) {
     out.println("<pre style='color:red'>");
     e.printStackTrace(new java.io.PrintWriter(out));
     out.println("</pre>");
   } %>