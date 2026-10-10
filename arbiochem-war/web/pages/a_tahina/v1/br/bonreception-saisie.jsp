<%@page import="affichage.PageInsert"%>
<%@page import="a_tahina.fronthelper.br.BRInsertFH"%>
<%@page import="user.UserEJB"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
  UserEJB u = (UserEJB) session.getValue("u");
  if (u == null) {
      response.sendRedirect(request.getContextPath() + "/login.jsp");
      return;
  }
  try {
    PageInsert pi = BRInsertFH.getPageInsert(request, session);

    String idbc   = BRInsertFH.getIdbc(request);
    String bute   = BRInsertFH.getButeRetour(request);
    String lien   = pi.getLien();

    String titre  = (idbc != null) ? "R&eacute;ception du BC " + idbc : "Nouvelle r&eacute;ception";
    String retour = (idbc != null) ? lien + "?but=" + BRInsertFH.PAGE_BC_FICHE + "&id=" + idbc
                                   : lien + "?but=" + BRInsertFH.PAGE_LISTE;
%>
<style>
  #br .form-group   { margin-bottom: 18px; }
  #br label         { font-weight: 500; margin-bottom: 6px; display: block; }
  #br .form-control { padding: 8px 12px; min-height: 38px; }
  #br .box-body     { padding: 24px 28px; }
</style>

<div class="content-wrapper">
  <h1 class="box-title">
    <a href="<%= retour %>"><i class="fa fa-angle-left"></i></a>
    <%= titre %>
  </h1>

  <div class="row m-0">
    <div class="col-md-2"></div>
    <div class="col-md-8">
      <div class="box box-primary">
        <div class="box-body">
          <form action="<%= lien %>?but=apresTarif.jsp"
                method="post" name="br" id="br">
            <% out.println(pi.getFormu().getHtmlInsert()); %>
            <input type="hidden" name="acte"   value="insert" />
            <input type="hidden" name="bute"   value="<%= bute %>" />
            <input type="hidden" name="classe"
                   value="a_tahina.br.BonReceptionPoussin" />
            <% if (idbc != null) { %>
              <input type="hidden" name="idbc" value="<%= idbc %>" />
            <% } %>
          </form>
        </div>
        <div class="box-footer" style="background:#fafafa; padding:15px 28px;">
          <button type="submit" form="br" class="btn btn-primary pull-right">
            <i class="fa fa-save"></i> Enregistrer
          </button>
          <a href="<%= retour %>" class="btn btn-secondary pull-left">
            <i class="fa fa-times"></i> Annuler
          </a>
          <div style="clear:both;"></div>
        </div>
      </div>
    </div>
  </div>
</div>
<% } catch (Exception e) {
     out.println("<pre style='color:red'>");
     e.printStackTrace(new java.io.PrintWriter(out));
     out.println("</pre>");
   } %>