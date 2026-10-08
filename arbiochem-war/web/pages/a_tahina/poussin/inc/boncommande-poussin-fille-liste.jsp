<%@page import="affichage.PageRecherche"%>
<%@page import="a_tahina.fronthelper.BonCommandePoussinFH"%>
<% try {
    String idMere = request.getParameter("idMere");
    if (idMere == null || idMere.trim().isEmpty()) {
        idMere = request.getParameter("id");
    }

    PageRecherche pr = BonCommandePoussinFH.getPageRechercheForFille(
                            request, session, idMere);
%>
<div class="tab-pane active">

  <%-- Tableau des lignes filles --%>
  <% out.println(pr.getTableau().getHtml()); %>

  <br>

  <%-- Pagination --%>
  <% out.println(pr.getBasPage()); %>

</div>
<% } catch(Exception e){ e.printStackTrace(); } %>