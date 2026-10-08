<%@page import="affichage.PageInsertMultiple"%>
<%@page import="a_tahina.fronthelper.BonReceptionPoussinFH"%>
<% try {
    int nombreLigne = 10;
    PageInsertMultiple pi = BonReceptionPoussinFH.getPageInsertMultiple(
                                request, session, nombreLigne);
%>
<div class="content-wrapper">
  <h1>Saisie de bon de réception poussin</h1>

  <form action="<%= pi.getLien() %>?but=apresMultiple.jsp" method="post">
    <% out.println(pi.getFormu().getHtmlInsert()); %>
    <% out.println(pi.getFormufle().getHtmlTableauInsert()); %>

    <input type="hidden" name="acte"        value="insert" />
    <input type="hidden" name="bute"
           value="a_tahina/poussin/bonreception-poussin-fiche.jsp" />
    <input type="hidden" name="classe"
           value="a_tahina.poussin.BonReceptionPoussin" />
    <input type="hidden" name="classefille"
           value="a_tahina.poussin.BonReceptionPoussinFille" />
    <input type="hidden" name="nombreLigne" value="<%= nombreLigne %>" />
    <input type="hidden" name="colonneMere" value="idmere" />
    <input type="hidden" name="nomtable"
           value="BONRECEPTION_POUSSIN_FILLE" />
  </form>
</div>
<% } catch(Exception e){ e.printStackTrace(); } %>