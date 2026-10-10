<%@page import="affichage.PageInsert"%>
<%@page import="a_tahina.fronthelper.br.BRInsertFH"%>
<%@page import="user.UserEJB"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
  // ---- Garde-fou session ----
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

    // Titre et lien retour conditionnels
    String titre  = (idbc != null)
                    ? "R&eacute;ception du bon de commande " + idbc
                    : "Nouvelle r&eacute;ception";
    String retour = (idbc != null)
                    ? lien + "?but=" + BRInsertFH.PAGE_BC_FICHE + "&id=" + idbc
                    : lien + "?but=" + BRInsertFH.PAGE_LISTE;
%>
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

            <%-- Le framework génère le formulaire ET le bouton Enregistrer --%>
            <% out.println(pi.getFormu().getHtmlInsert()); %>

            <%-- Paramètres cachés pour apresTarif.jsp --%>
            <input type="hidden" name="acte"   value="insert" />
            <input type="hidden" name="bute"   value="<%= bute %>" />
            <input type="hidden" name="classe"
                   value="a_tahina.br.BonReceptionPoussin" />
            <% if (idbc != null) { %>
              <input type="hidden" name="idbc" value="<%= idbc %>" />
            <% } %>

          </form>

          <%-- Bouton Annuler uniquement (pas de doublon de Enregistrer) --%>
          <div class="box-footer" style="border-top:none; background:transparent; padding-top:0;">
            <a href="<%= retour %>" class="btn btn-secondary pull-left">
              Annuler
            </a>
          </div>

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