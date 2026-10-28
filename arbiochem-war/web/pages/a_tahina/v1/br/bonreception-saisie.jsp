<%@page import="a_tahina.core.page.AtahinaPageInsert"%>
<%@page import="a_tahina.br.fh.BRFH"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    AtahinaPageInsert pi = new BRFH().getPageInsert(request, session);

    String idbc   = BRFH.getIdbc(request);
    String bute   = BRFH.getButeRetour(request);
    String lien   = pi.getLien();

    String titre = (idbc != null) ? "R&eacute;ception du BC " + a_tahina.core.page.AtahinaUtil.esc(idbc)
                                  : "Nouvelle r&eacute;ception";
%>
<div class="content-wrapper">
  <h1><%= titre %></h1>

  <form action="<%= lien %>?but=apresTarif.jsp" method="post" name="br" id="br">
    <% out.println(pi.getFormu().getHtmlInsert()); %>

    <input type="hidden" name="acte"   value="insert" />
    <input type="hidden" name="bute"   value="<%= bute %>" />
    <input type="hidden" name="classe" value="<%= BRFH.CLASSE %>" />
    <% if (idbc != null) { %>
      <input type="hidden" name="idbc" value="<%= idbc %>" />
    <% } %>
  </form>
</div>
<% } catch (Exception e) {
     out.println(a_tahina.core.page.AtahinaUtil.erreurHtml(e));
   } %>