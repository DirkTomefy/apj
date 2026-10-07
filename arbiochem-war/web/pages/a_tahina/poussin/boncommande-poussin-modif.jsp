<%@page import="affichage.PageUpdateMultiple"%>
<%@page import="a_tahina.fronthelper.BonCommandePoussinFH"%>
<% try {
    String id = request.getParameter("id");
    int nbNouvellesLignes = 3;

    PageUpdateMultiple pu = BonCommandePoussinFH.getPageUpdateMultiple(
                                request, session, id, nbNouvellesLignes);
%>
<div class="content-wrapper">
  <h1>Modification de bon de commande poussin</h1>

  <form action="<%= pu.getLien() %>?but=apresMultiple.jsp&id=<%= id %>"
        method="post">
    <% out.println(pu.getFormu().getHtmlInsert()); %>
    <% out.println(pu.getFormufle().getHtmlTableauInsert()); %>

    <input type="hidden" name="acte"        value="updateInsert" />
    <input type="hidden" name="bute"
           value="a_tahina/poussin/boncommande-poussin-fiche.jsp?id=<%= id %>" />
    <input type="hidden" name="classe"
           value="a_tahina.poussin.BonCommandePoussin" />
    <input type="hidden" name="classefille"
           value="a_tahina.poussin.BonCommandePoussinFille" />
    <input type="hidden" name="nombreLigne" value="<%= pu.getNombreLigne() %>" />
    <input type="hidden" name="colonneMere" value="idmere" />
    <input type="hidden" name="nomtable"
           value="BONCOMMANDE_POUSSIN_FILLE" />
  </form>
</div>
<% } catch(Exception e){ e.printStackTrace(); } %>