<%@page import="affichage.PageUpdateMultiple"%>
<%@page import="a_tahina.fronthelper.BonReceptionPoussinFH"%>
<% try {
    String id = request.getParameter("id");
    int nbNouvellesLignes = 3;

    PageUpdateMultiple pu = BonReceptionPoussinFH.getPageUpdateMultiple(
                                request, session, id, nbNouvellesLignes);
%>
<div class="content-wrapper">
  <h1>Modification de bon de réception poussin</h1>

  <form action="<%= pu.getLien() %>?but=apresMultiple.jsp&id=<%= id %>"
        method="post">
    <% out.println(pu.getFormu().getHtmlInsert()); %>
    <% out.println(pu.getFormufle().getHtmlTableauInsert()); %>

    <input type="hidden" name="acte"        value="updateInsert" />
    <input type="hidden" name="bute"
           value="a_tahina/poussin/bonreception-poussin-fiche.jsp?id=<%= id %>" />
    <input type="hidden" name="classe"
           value="a_tahina.poussin.BonReceptionPoussin" />
    <input type="hidden" name="classefille"
           value="a_tahina.poussin.BonReceptionPoussinFille" />
    <input type="hidden" name="nombreLigne" value="<%= pu.getNombreLigne() %>" />
    <input type="hidden" name="colonneMere" value="idmere" />
    <input type="hidden" name="nomtable"
           value="BONRECEPTION_POUSSIN_FILLE" />
  </form>
</div>
<% } catch(Exception e){ e.printStackTrace(); } %>