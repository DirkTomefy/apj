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

  <%-- JS : reste = qte - maty - very --%>
  <script>
    function getVal(name) {
      var el = document.getElementsByName(name)[0];
      return el ? (parseFloat(el.value) || 0) : 0;
    }

    function recalcReste(i) {
      var qte   = getVal('qte_'   + i);
      var maty  = getVal('maty_'  + i);
      var very  = getVal('very_'  + i);
      var reste = qte - maty - very;
      if (reste < 0) reste = 0;

      var resteEl = document.getElementsByName('reste_' + i)[0];
      if (resteEl) resteEl.value = reste.toFixed(2);
    }

    window.addEventListener('DOMContentLoaded', function() {
      for (var i = 0; i < <%= nbLignesTotal %>; i++) {
        recalcReste(i);
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