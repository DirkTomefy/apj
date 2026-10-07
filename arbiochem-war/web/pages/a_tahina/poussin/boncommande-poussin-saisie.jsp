<%@page import="affichage.PageInsertMultiple"%>
<%@page import="a_tahina.fronthelper.BonCommandePoussinFH"%>
<% try {
    int nombreLigne = 10;
    PageInsertMultiple pi = BonCommandePoussinFH.getPageInsertMultiple(
                                request, session, nombreLigne);
%>
<div class="content-wrapper">

  <h1>Saisie de bon de commande poussin</h1>

  <form action="<%= pi.getLien() %>?but=apresMultiple.jsp" method="post">
    <% out.println(pi.getFormu().getHtmlInsert()); %>
    <% out.println(pi.getFormufle().getHtmlTableauInsert()); %>

    <input type="hidden" name="acte"        value="insert" />
    <input type="hidden" name="bute"
           value="a_tahina/poussin/boncommande-poussin-fiche.jsp" />
    <input type="hidden" name="classe"
           value="a_tahina.poussin.BonCommandePoussin" />
    <input type="hidden" name="classefille"
           value="a_tahina.poussin.BonCommandePoussinFille" />
    <input type="hidden" name="nombreLigne" value="<%= nombreLigne %>" />
    <input type="hidden" name="colonneMere" value="idmere" />
    <input type="hidden" name="nomtable"
           value="BONCOMMANDE_POUSSIN_FILLE" />
  </form>
</div>
<% } catch(Exception e){ e.printStackTrace(); } %>