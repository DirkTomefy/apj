<%@page import="affichage.PageRecherche"%>
<%@page import="a_tahina.fronthelper.InsertionCompartimentFH"%>
<% try {
    String idMere = request.getParameter("idMere");
    if (idMere == null || idMere.trim().isEmpty()) {
        idMere = request.getParameter("id");
    }
    PageRecherche pr = InsertionCompartimentFH.getPageRechercheForFille(
                            request, session, idMere);
%>
<div class="tab-pane active">
  <% out.println(pr.getTableau().getHtml()); %>
  <br>
  <% out.println(pr.getBasPage()); %>
</div>
<% } catch(Exception e){ e.printStackTrace(); } %>