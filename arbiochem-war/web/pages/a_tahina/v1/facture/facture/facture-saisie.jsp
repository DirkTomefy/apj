<%@page import="a_tahina.core.page.*"%>
<%@page import="a_tahina.facture.fh.FactureFH"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    int nombreLigne = FactureFH.NOMBRE_LIGNE;
    AtahinaPageInsertMultiple pi = new FactureFH().getPageInsertMultiple(request, session, nombreLigne);
%>
<div class="content-wrapper">
  <h1>Saisie d'une facture</h1>

  <form action="<%= pi.getLien() %>?but=apresMultiple.jsp" method="post">
    <% out.println(pi.getFormu().getHtmlInsert()); %>
    <% out.println(pi.getFormufle().getHtmlTableauInsert()); %>

    <input type="hidden" name="acte"        value="insert" />
    <input type="hidden" name="bute"        value="<%= FactureFH.PAGE_FICHE %>" />
    <input type="hidden" name="classe"      value="<%= FactureFH.CLASSE %>" />
    <input type="hidden" name="classefille" value="<%= FactureFH.CLASSE_FILLE %>" />
    <input type="hidden" name="nombreLigne" value="<%= nombreLigne %>" />
    <input type="hidden" name="colonneMere" value="idmere" />
    <input type="hidden" name="nomtable"    value="ATH_FACTURE_FILLE" />
  </form>
</div>
<% } catch (Exception e) {
     out.println(AtahinaUtil.erreurHtml(e));
   } %>
