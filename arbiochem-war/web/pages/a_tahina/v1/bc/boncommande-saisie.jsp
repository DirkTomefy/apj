<%@page import="a_tahina.core.page.*"%>
<%@page import="a_tahina.bc.fh.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    int nombreLigne = BCmdInsertFH.NOMBRE_LIGNE;
    AtahinaPageInsertMultiple pi = new BCmdInsertFH().getPageInsertMultiple(request, session, nombreLigne);
%>
<div class="content-wrapper">
  <h1>Saisie de bon de commande poussin</h1>

  <form action="<%= pi.getLien() %>?but=apresMultiple.jsp" method="post">
    <% out.println(pi.getFormu().getHtmlInsert()); %>
    <% out.println(pi.getFormufle().getHtmlTableauInsert()); %>

    <input type="hidden" name="acte"        value="insert" />
    <input type="hidden" name="bute"        value="<%= BCmdFicheFH.PAGE_ACTUEL %>" />
    <input type="hidden" name="classe"      value="<%= BCmdFicheFH.CLASSE %>" />
    <input type="hidden" name="classefille" value="<%= BCmdFicheFH.CLASSE_FILLE %>" />
    <input type="hidden" name="nombreLigne" value="<%= nombreLigne %>" />
    <input type="hidden" name="colonneMere" value="idmere" />
    <input type="hidden" name="nomtable"    value="BONCOMMANDE_POUSSIN_FILLE" />
  </form>
</div>
<% } catch (Exception e) {
     out.println(AtahinaUtil.erreurHtml(e));
   } %>
