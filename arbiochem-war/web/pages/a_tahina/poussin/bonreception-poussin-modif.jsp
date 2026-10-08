<%@page import="affichage.PageUpdateMultiple"%>
<%@page import="a_tahina.fronthelper.BonReceptionPoussinFH"%>
<% try {
    String id = request.getParameter("id");
    int nbNouvellesLignes = 3;

    PageUpdateMultiple pu = BonReceptionPoussinFH.getPageUpdateMultiple(
                                request, session, id, nbNouvellesLignes);
    int nbLignesTotal = pu.getNombreLigne();
%>
<div class="content-wrapper">
  <h1>Modification de bon de réception poussin</h1>

  <%--  JS : very = qte - maty --%>
  <script>
    function recalcVery(i) {
      var qteEl  = document.getElementsByName('qte_'  + i)[0];
      var matyEl = document.getElementsByName('maty_' + i)[0];
      var veryEl = document.getElementsByName('very_' + i)[0];
      if (!qteEl || !matyEl || !veryEl) return;

      var qte  = parseFloat(qteEl.value)  || 0;
      var maty = parseFloat(matyEl.value) || 0;
      var very = qte - maty;
      if (very < 0) very = 0;

      veryEl.value = very.toFixed(2);
    }

    window.addEventListener('DOMContentLoaded', function() {
      for (var i = 0; i < <%= nbLignesTotal %>; i++) {
        recalcVery(i);
      }
    });
  </script>

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
    <input type="hidden" name="nombreLigne" value="<%= nbLignesTotal %>" />
    <input type="hidden" name="colonneMere" value="idmere" />
    <input type="hidden" name="nomtable"
           value="BONRECEPTION_POUSSIN_FILLE" />
  </form>
</div>
<% } catch(Exception e){ e.printStackTrace(); } %>