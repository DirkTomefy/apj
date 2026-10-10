<%@page import="affichage.PageInsertMultiple"%>
<%@page import="a_tahina.fronthelper.bc.BCmdInsertFH"%>

<% try {  
    int nombreLigne = 10;
    PageInsertMultiple pi = BCmdInsertFH.getPageInsertMultiple(request , session , nombreLigne);
%>
<div class="content-wrapper">
  <h1>Saisie de bon de commande poussin</h1>

  <form action="<%= pi.getLien() %>?but=apresMultiple.jsp" method="post">
    <% out.println(pi.getFormu().getHtmlInsert()); %>
    <% out.println(pi.getFormufle().getHtmlTableauInsert()); %>

    <input type="hidden" name="acte"        value="insert" />
    <input type="hidden" name="bute"
           value="a_tahina/v1/bc/boncommande-fiche.jsp" />
    <input type="hidden" name="classe"
           value="a_tahina.poussin.BonCommandePoussin" />
    <input type="hidden" name="classefille"
           value="a_tahina.poussin.BonCommandePoussinFille" />
    <input type="hidden" name="nombreLigne" value="<%= nombreLigne %>" />
    <input type="hidden" name="colonneMere" value="idmere" />
    <input type="hidden" name="nomtable"
           value="BONCOMMANDE_POUSSIN_FILLE" />
  </form>

<% } catch(Exception e) { %>
iy
<% } %>

