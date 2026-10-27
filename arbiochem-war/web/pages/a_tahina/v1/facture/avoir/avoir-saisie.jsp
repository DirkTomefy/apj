<%@page import="a_tahina.page.*"%>
<%@page import="a_tahina.fronthelper.facture.AvoirFH"%>
<%@page import="a_tahina.fronthelper.facture.FactureFH"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    String idfacture = AvoirFH.getIdfacture(request);
    if (idfacture == null) {
        // Etape 1 : choisir la facture visee a laquelle rattacher l'avoir
%>
<div class="content-wrapper">
  <h1>Nouvel avoir</h1>
  <div class="box"><div class="box-body">
    <% out.println(AvoirFH.getHtmlChoixFacture(session)); %>
  </div></div>
</div>
<%
    } else {
        // Etape 2 : lignes de la facture a retourner
        AtahinaPageInsertMultiple pi = new AvoirFH().getPageInsertMultiple(request, session);
%>
<div class="content-wrapper">
  <h1>
    <a href="<%= pi.getLien() %>?but=<%= FactureFH.PAGE_FICHE %>&id=<%= AtahinaUtil.url(idfacture) %>"><i class="fa fa-angle-left"></i></a>
    Nouvel avoir sur la facture <%= AtahinaUtil.esc(idfacture) %>
  </h1>
  <p>Cochez les lignes &agrave; retourner et indiquez la quantit&eacute;. Le cr&eacute;dit sera d&eacute;duit
     des prochaines factures du client (dans la limite de son plafond).</p>

  <form action="<%= pi.getLien() %>?but=apresMultiple.jsp" method="post">
    <% out.println(pi.getFormu().getHtmlInsert()); %>
    <% out.println(pi.getFormufle().getHtmlTableauInsert()); %>

    <input type="hidden" name="acte"        value="insert" />
    <input type="hidden" name="bute"        value="<%= AvoirFH.PAGE_FICHE %>" />
    <input type="hidden" name="classe"      value="<%= AvoirFH.CLASSE %>" />
    <input type="hidden" name="classefille" value="<%= AvoirFH.CLASSE_FILLE %>" />
    <input type="hidden" name="nombreLigne" value="<%= pi.getNbLignes() %>" />
    <input type="hidden" name="colonneMere" value="idmere" />
    <input type="hidden" name="nomtable"    value="ATH_AVOIR_FILLE" />
  </form>
</div>
<%
    }
} catch (Exception e) {
    out.println(AtahinaUtil.erreurHtml(e));
} %>
