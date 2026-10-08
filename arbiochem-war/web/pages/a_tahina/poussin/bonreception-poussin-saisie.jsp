<%@page import="affichage.PageInsertMultiple"%>
<%@page import="a_tahina.fronthelper.BonReceptionPoussinFH"%>
<% try {
    int nombreLigne = 10;
    PageInsertMultiple pi = BonReceptionPoussinFH.getPageInsertMultiple(
                                request, session, nombreLigne);
%>
<div class="content-wrapper">
  <h1>Saisie de bon de réception poussin</h1>

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

    // Recalcul initial de toutes les lignes au chargement
    window.addEventListener('DOMContentLoaded', function() {
      for (var i = 0; i < <%= nombreLigne %>; i++) {
        recalcVery(i);
      }
    });
  </script>

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