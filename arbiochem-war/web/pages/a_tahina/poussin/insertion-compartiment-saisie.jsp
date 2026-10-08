<%@page import="affichage.PageInsertMultiple"%>
<%@page import="a_tahina.fronthelper.InsertionCompartimentFH"%>
<% try {
    int nombreLigne = 10;
    PageInsertMultiple pi = InsertionCompartimentFH.getPageInsertMultiple(
                                request, session, nombreLigne);
%>
<div class="content-wrapper">
  <h1>Saisie insertion en compartiment</h1>

  <%--  JS : valeurCompartiment = qte * pu --%>
  <script>
    function recalcValeur(i) {
      var qteEl = document.getElementsByName('qte_' + i)[0];
      var puEl  = document.getElementsByName('pu_'  + i)[0];
      var valEl = document.getElementsByName('valeurCompartiment_' + i)[0];
      if (!qteEl || !puEl || !valEl) return;

      var qte = parseFloat(qteEl.value) || 0;
      var pu  = parseFloat(puEl.value)  || 0;
      valEl.value = (qte * pu).toFixed(2);
    }

    window.addEventListener('DOMContentLoaded', function() {
      for (var i = 0; i < <%= nombreLigne %>; i++) {
        recalcValeur(i);
      }
    });
  </script>

  <form action="<%= pi.getLien() %>?but=apresMultiple.jsp" method="post">
    <% out.println(pi.getFormu().getHtmlInsert()); %>
    <% out.println(pi.getFormufle().getHtmlTableauInsert()); %>

    <input type="hidden" name="acte"        value="insert" />
    <input type="hidden" name="bute"
           value="a_tahina/poussin/insertion-compartiment-fiche.jsp" />
    <input type="hidden" name="classe"
           value="a_tahina.poussin.InsertionCompartiment" />
    <input type="hidden" name="classefille"
           value="a_tahina.poussin.InsertionCompartimentFille" />
    <input type="hidden" name="nombreLigne" value="<%= nombreLigne %>" />
    <input type="hidden" name="colonneMere" value="idmere" />
    <input type="hidden" name="nomtable"
           value="INSERTION_COMPARTIMENT_FILLE" />
  </form>
</div>
<% } catch(Exception e){ e.printStackTrace(); } %>