<%@page import="affichage.PageInsertMultiple"%>
<%@page import="a_tahina.fronthelper.ic.ICInsertFH"%>
<%@page import="user.UserEJB"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
  UserEJB u = (UserEJB) session.getValue("u");
  if (u == null) {
      response.sendRedirect(request.getContextPath() + "/login.jsp");
      return;
  }
  try {
    int NB_LIGNES = 10;
    PageInsertMultiple pi = ICInsertFH.getPageInsertMultiple(request, session, NB_LIGNES);

    String idbr = ICInsertFH.getIdbr(request);
    String lien = pi.getLien();

    String titre;
    String retour;
    if (idbr != null) {
        titre  = "Insertion compartiment pour le BR " + idbr;
        retour = lien + "?but=" + ICInsertFH.PAGE_BR_FICHE + "&id=" + idbr;
    } else {
        titre  = "Nouvelle insertion compartiment";
        retour = lien + "?but=" + ICInsertFH.PAGE_LISTE;
    }
%>
<style>
  #ic .form-group   { margin-bottom: 18px; }
  #ic label         { font-weight: 500; margin-bottom: 6px; display: block; }
  #ic .form-control { padding: 8px 12px; min-height: 38px; }
</style>

<div class="content-wrapper">
  <h1 class="box-title">
    <a href="<%= retour %>"><i class="fa fa-angle-left"></i></a>
    <%= titre %>
  </h1>

  <div class="row m-0">
    <div class="col-md-1"></div>
    <div class="col-md-10">
      <div class="box box-primary">
        <div class="box-body">
          <form action="<%= lien %>?but=apresMultiple.jsp"
                method="post" name="ic" id="ic">

            <% out.println(pi.getFormu().getHtmlInsert()); %>
            <br>
            <% out.println(pi.getFormufle().getHtmlTableauInsert()); %>

            <input type="hidden" name="acte"         value="insert" />
            <input type="hidden" name="bute"
                   value="<%= ICInsertFH.PAGE_IC_FICHE %>" />
            <input type="hidden" name="classe"
                   value="<%= ICInsertFH.CLASSE %>" />
            <input type="hidden" name="classefille"
                   value="<%= ICInsertFH.CLASSE_FILLE %>" />
            <input type="hidden" name="nombreLigne"  value="<%= NB_LIGNES %>" />
            <input type="hidden" name="colonneMere"  value="idmere" />
            <input type="hidden" name="nomtable"
                   value="INSERTION_COMPARTIMENT_FILLE" />
            <% if (idbr != null) { %>
              <input type="hidden" name="idbr" value="<%= idbr %>" />
            <% } %>
          </form>
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