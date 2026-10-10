<%@page import="a_tahina.br.*"%>
<%@page import="a_tahina.fronthelper.br.*"%>
<%@page import="affichage.*"%>
<%@page import="user.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
  UserEJB u = (UserEJB) session.getValue("u");
  if (u == null) {
      response.sendRedirect(request.getContextPath() + "/login.jsp");
      return;
  }
  try {
    PageConsulte pc = BRFicheFH.getPageConsulte(request, session);
    String lien = (String) session.getValue("lien");
    BonReceptionPoussinLib br = (BonReceptionPoussinLib) pc.getBase();

    String id    = br.getTuppleID();
    int    etat  = br.getEtat();
    String idbc  = br.getIdboncommande();

    String tab = request.getParameter("tab");
    if (tab == null || tab.isEmpty()) tab = "inc/br-liste-detail";
    String tabJsp = tab + ".jsp";
%>
<div class="content-wrapper">
  <h1 class="box-title">
    <a href="<%= lien %>?but=<%= BRFicheFH.PAGE_LISTE %>">
      <i class="fa fa-angle-left"></i>
    </a>
    <%= pc.getTitre() %> &nbsp;
    <small style="color:#888;">#<%= id %></small>
  </h1>

  <!-- FICHE MÈRE -->
  <div class="row m-0">
    <div class="col-md-2"></div>
    <div class="col-md-8">
      <div class="box-fiche">
        <div class="box">
          <div class="box-body">
            <% out.println(pc.getHtml()); %>
            <br/>

            <div class="box-footer" style="background:#fafafa; padding:15px 20px;">
              <% if (etat < 11) { %>
                <a class="btn btn-primary pull-right" style="margin-right:8px"
                   href="<%= lien %>?but=apresTarif.jsp&acte=valider&id=<%= id %>&bute=<%= BRFicheFH.PAGE_ACTUEL %>&classe=<%= BRFicheFH.CLASSE %>">
                  <i class="fa fa-check"></i> Viser
                </a>
                <a class="btn btn-secondary pull-right" style="margin-right:8px"
                   href="<%= lien %>?but=<%= BRFicheFH.PAGE_MODIF %>&id=<%= id %>&acte=update">
                  <i class="fa fa-pencil"></i> Modifier
                </a>
                <a class="btn btn-danger pull-left"
                   href="<%= lien %>?but=apresTarif.jsp&acte=annuler&id=<%= id %>&bute=<%= BRFicheFH.PAGE_ACTUEL %>&classe=<%= BRFicheFH.CLASSE %>">
                  <i class="fa fa-times"></i> Annuler
                </a>
              <% } %>

              <% if (etat >= 11 && idbc != null) { %>
                <a class="btn btn-warning pull-right" style="margin-right:8px"
                   href="<%= lien %>?but=<%= BRFicheFH.PAGE_SAISIE %>&idbc=<%= idbc %>&idbcf=<%= br.getIdboncommandefille() %>">
                  <i class="fa fa-refresh"></i> R&eacute;ceptionner &agrave; nouveau
                </a>
              <% } %>

              <% if (idbc != null) { %>
                <a class="btn btn-info pull-left" style="margin-left:8px"
                   href="<%= lien %>?but=<%= BRFicheFH.PAGE_BC_FICHE %>&id=<%= idbc %>">
                  <i class="fa fa-shopping-cart"></i> Voir le BC
                </a>
              <% } %>
              <div style="clear:both;"></div>
            </div>
            <br/>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- ONGLETS -->
  <div class="row m-0">
    <div class="col-md-12 nopadding">
      <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
          <li class="<%= "inc/br-liste-detail".equals(tab) ? "active" : "" %>">
            <a href="<%= lien %>?but=<%= BRFicheFH.PAGE_ACTUEL %>&id=<%= id %>&tab=inc/br-liste-detail">
              D&eacute;tails
            </a>
          </li>
        </ul>
        <div class="tab-content">
          <jsp:include page="<%= tabJsp %>">
            <jsp:param name="idMere" value="<%= id %>" />
          </jsp:include>
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