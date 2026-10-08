<%@page import="java.util.Map"%>
<%@page import="affichage.PageUpdateMultiple"%>
<%@page import="a_tahina.fronthelper.InsertionCompartimentFH"%>
<% try {
    String id = request.getParameter("id");
    int nbNouvellesLignes = 3;

    PageUpdateMultiple pu = InsertionCompartimentFH.getPageUpdateMultiple(
                                request, session, id, nbNouvellesLignes);
    int nbLignesTotal = pu.getNombreLigne();

    // ⚡ Mapping idbcpf → pu
    Map<String, Double> puParBc = InsertionCompartimentFH.getPuParBC(request);
%>
<div class="content-wrapper">
  <h1>Modification insertion en compartiment</h1>

  <script>
    var puParBC = {
      <% for (Map.Entry<String, Double> e : puParBc.entrySet()) { %>
        '<%= e.getKey() %>': <%= e.getValue() %>,
      <% } %>
    };

    function majPu(i) {
      var sel  = document.getElementsByName('idbcpf_' + i)[0];
      var puEl = document.getElementsByName('pu_' + i)[0];
      if (!sel || !puEl) return;

      var val = puParBC[sel.value];
      if (val !== undefined && val !== null) {
        puEl.value = val;
        recalcValeur(i);
      }
    }

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
      for (var i = 0; i < <%= nbLignesTotal %>; i++) {
        recalcValeur(i);
      }
    });
  </script>

  <form action="<%= pu.getLien() %>?but=apresMultiple.jsp&id=<%= id %>"
        method="post">
    <% out.println(pu.getFormu().getHtmlInsert()); %>
    <% out.println(pu.getFormufle().getHtmlTableauInsert()); %>

    <input type="hidden" name="acte"        value="updateInsert" />
    <input type="hidden" name="bute"
           value="a_tahina/poussin/insertion-compartiment-fiche.jsp?id=<%= id %>" />
    <input type="hidden" name="classe"
           value="a_tahina.poussin.InsertionCompartiment" />
    <input type="hidden" name="classefille"
           value="a_tahina.poussin.InsertionCompartimentFille" />
    <input type="hidden" name="nombreLigne" value="<%= nbLignesTotal %>" />
    <input type="hidden" name="colonneMere" value="idmere" />
    <input type="hidden" name="nomtable"
           value="INSERTION_COMPARTIMENT_FILLE" />
  </form>
</div>
<% } catch(Exception e){ e.printStackTrace(); } %>