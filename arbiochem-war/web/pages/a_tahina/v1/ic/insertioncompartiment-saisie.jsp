<%@page import="a_tahina.core.page.AtahinaPageInsertMultiple"%>
<%@page import="a_tahina.ic.fh.ICFH"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    int NB_LIGNES = 10;
    AtahinaPageInsertMultiple pi = new ICFH().getPageInsertMultiple(request, session, NB_LIGNES);
    String idbr = ICFH.getIdbr(request);
    String lien = pi.getLien();

    String titre  = (idbr != null) ? "Insertion compartiment pour le BR " + a_tahina.core.page.AtahinaUtil.esc(idbr)
                                   : "Nouvelle insertion compartiment";
    String retour = (idbr != null) ? lien + "?but=" + ICFH.PAGE_BR_FICHE + "&id=" + idbr
                                   : lien + "?but=" + ICFH.PAGE_LISTE;
%>
<div class="content-wrapper">
  <h1><%= titre %></h1>

  <form action="<%= lien %>?but=apresMultiple.jsp" method="post" name="ic" id="ic">
    <% out.println(pi.getFormu().getHtmlInsert()); %>
    <% out.println(pi.getFormufle().getHtmlTableauInsert()); %>

    <input type="hidden" name="acte"         value="insert" />
    <input type="hidden" name="bute"         value="<%= ICFH.PAGE_FICHE %>" />
    <input type="hidden" name="classe"       value="<%= ICFH.CLASSE %>" />
    <input type="hidden" name="classefille"  value="<%= ICFH.CLASSE_FILLE %>" />
    <input type="hidden" name="nombreLigne"  value="<%= NB_LIGNES %>" />
    <input type="hidden" name="colonneMere"  value="idmere" />
    <input type="hidden" name="nomtable"     value="INSERTION_COMPARTIMENT_FILLE" />
    <% if (idbr != null) { %>
      <input type="hidden" name="idbr" value="<%= idbr %>" />
    <% } %>
  </form>
</div>
<% } catch (Exception e) {
     out.println(a_tahina.core.page.AtahinaUtil.erreurHtml(e));
   } %>