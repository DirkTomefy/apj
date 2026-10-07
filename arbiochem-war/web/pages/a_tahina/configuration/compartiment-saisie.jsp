<%@page import="affichage.PageInsert"%>
<%@page import="a_tahina.fronthelper.CompartimentFH"%>
<% try {
    PageInsert pi = CompartimentFH.getPageInsert(request);
%>
<div class="content-wrapper">
  <h1>Saisie de compartiment</h1>

  <form action="<%= pi.getLien() %>?but=apresTarif.jsp" method="post">
    <% out.println(pi.getHtmlInsert()); %>

    <input type="hidden" name="acte"     value="insert" />
    <input type="hidden" name="bute"
           value="a_tahina/configuration/compartiment-liste.jsp" />
    <input type="hidden" name="classe"
           value="a_tahina.configuration.Compartiment" />
    <input type="hidden" name="nomtable" value="COMPARTIMENT" />
  </form>
</div>
<% } catch(Exception e){ e.printStackTrace(); } %>