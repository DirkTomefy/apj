<%@page import="a_tahina.ic.*"%>
<%@page import="a_tahina.fronthelper.ic.*"%>
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
    PageConsulte pc = ICFicheFH.getPageConsulte(request, session);
    String lien = (String) session.getValue("lien");
    InsertionCompartimentLib ic = (InsertionCompartimentLib) pc.getBase();

    String id   = ic.getTuppleID();
    int    etat = ic.getEtat();
    String idbr = ic.getIdbonreception();

    String tab = request.getParameter("tab");
    if (tab == null || tab.isEmpty()) tab = "inc/ic-liste-detail";
    String tabJsp = tab + ".jsp";
%>
<div class="content-wrapper">
  <h1 class="box-title">
    <a href="<%= lien %>?but=<%= ICFicheFH.PAGE_LISTE %>">
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
                   href="<%= lien %>?but=apresTarif.jsp&acte=valider&id=<%= id %>&bute=<%= ICFicheFH.PAGE_ACTUEL %>&classe=<%= ICFicheFH.CLASSE %>">
                  <i class="fa fa-check"></i> Viser
                </a>
                <a class="btn btn-danger pull-left"
                   href="<%= lien %>?but=apresTarif.jsp&acte=annuler&id=<%= id %>&bute=<%= ICFicheFH.PAGE_ACTUEL %>&classe=<%= ICFicheFH.CLASSE %>">
                  <i class="fa fa-times"></i> Annuler
                </a>
              <% } %>
              <% if (idbr != null) { %>
                <a class="btn btn-info pull-left" style="margin-left:8px"
                   href="<%= lien %>?but=<%= ICFicheFH.PAGE_BR_FICHE %>&id=<%= idbr %>">
                  <i class="fa fa-truck"></i> Voir le BR
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
          <li class="<%= "inc/ic-liste-detail".equals(tab) ? "active" : "" %>">
            <a href="<%= lien %>?but=<%= ICFicheFH.PAGE_ACTUEL %>&id=<%= id %>&tab=inc/ic-liste-detail">
              D&eacute;tails
            </a>
          </li>
          <li class="<%= "inc/ic-liste-lots".equals(tab) ? "active" : "" %>">
            <a href="<%= lien %>?but=<%= ICFicheFH.PAGE_ACTUEL %>&id=<%= id %>&tab=inc/ic-liste-lots">
              Lots cr&eacute;&eacute;s
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