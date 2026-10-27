<%@page import="a_tahina.page.AtahinaPageInsertMultiple"%>
<%@page import="a_tahina.fronthelper.ic.ICInsertFH"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    int NB_LIGNES = 10;
    AtahinaPageInsertMultiple pi = new ICInsertFH().getPageInsertMultiple(request, session, NB_LIGNES);
    String idbr = ICInsertFH.getIdbr(request);
    String lien = pi.getLien();

    String titre  = (idbr != null) ? "Insertion compartiment pour le BR " + a_tahina.page.AtahinaUtil.esc(idbr)
                                   : "Nouvelle insertion compartiment";
    String retour = (idbr != null) ? lien + "?but=" + ICInsertFH.PAGE_BR_FICHE + "&id=" + idbr
                                   : lien + "?but=" + ICInsertFH.PAGE_LISTE;
%>
<div class="content-wrapper">
  <h1><%= titre %></h1>

  <form action="<%= lien %>?but=apresMultiple.jsp" method="post" name="ic" id="ic">
    <% out.println(pi.getFormu().getHtmlInsert()); %>
    <% out.println(pi.getFormufle().getHtmlTableauInsert()); %>

    <input type="hidden" name="acte"         value="insert" />
    <input type="hidden" name="bute"         value="<%= ICInsertFH.PAGE_IC_FICHE %>" />
    <input type="hidden" name="classe"       value="<%= ICInsertFH.CLASSE %>" />
    <input type="hidden" name="classefille"  value="<%= ICInsertFH.CLASSE_FILLE %>" />
    <input type="hidden" name="nombreLigne"  value="<%= NB_LIGNES %>" />
    <input type="hidden" name="colonneMere"  value="idmere" />
    <input type="hidden" name="nomtable"     value="INSERTION_COMPARTIMENT_FILLE" />
    <% if (idbr != null) { %>
      <input type="hidden" name="idbr" value="<%= idbr %>" />
    <% } %>
  </form>
</div>
<% } catch (Exception e) {
     out.println(a_tahina.page.AtahinaUtil.erreurHtml(e));
   } %>