<%@page import="affichage.PageInsert"%>
<%@page import="a_tahina.fronthelper.br.BRInsertFH"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    PageInsert pi = BRInsertFH.getPageInsert(request, session);

    String idbc   = BRInsertFH.getIdbc(request);
    String bute   = BRInsertFH.getButeRetour(request);
    String lien   = pi.getLien();

    String titre = (idbc != null) ? "R&eacute;ception du BC " + idbc
                                  : "Nouvelle r&eacute;ception";
%>
<div class="content-wrapper">
  <h1><%= titre %></h1>

  <form action="<%= lien %>?but=apresTarif.jsp" method="post" name="br" id="br">
    <% out.println(pi.getFormu().getHtmlInsert()); %>

    <input type="hidden" name="acte"   value="insert" />
    <input type="hidden" name="bute"   value="<%= bute %>" />
    <input type="hidden" name="classe" value="a_tahina.br.BonReceptionPoussin" />
    <% if (idbc != null) { %>
      <input type="hidden" name="idbc" value="<%= idbc %>" />
    <% } %>
  </form>
</div>
<% } catch (Exception e) {
     out.println("<pre style='color:red'>");
     e.printStackTrace(new java.io.PrintWriter(out));
     out.println("</pre>");
   } %>