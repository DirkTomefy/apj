<%@page import="a_tahina.bc.*"%>
<%@page import="a_tahina.fronthelper.bc.*"%>
<%@page import="affichage.*"%>
<%@page import="user.*"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
  UserEJB u = (UserEJB) session.getValue("u");
  try {
    PageConsulte pc = BCmdFicheFH.getPageConsulte(request, session);
    String lien = (String) session.getValue("lien");
    BonCommandePoussinLib bc = (BonCommandePoussinLib) pc.getBase();
    String id = bc.getTuppleID();
    int etat = bc.getEtat();

    String pageActuel = BCmdFicheFH.PAGE_ACTUEL;
    String pageModif  = BCmdFicheFH.PAGE_MODIF;
    String pageListe  = BCmdFicheFH.PAGE_LISTE;
    String classe     = BCmdFicheFH.CLASSE;

    // Onglet actif
    String tab = request.getParameter("tab");
    if (tab == null) tab = "inc/bc-liste-detail";

    Map<String,String> map = new HashMap<String,String>();
    map.put("inc/bc-liste-detail", "");
    map.put(tab, "active");

    String tabJsp = tab + ".jsp";
%>
<div class="content-wrapper">
  <h1 class="box-title">
    <a href="<%= lien %>?but=<%= pageListe %>"><i class="fa fa-angle-left"></i></a>
    <%= pc.getTitre() %>
  </h1>

  <div class="row m-0">
    <div class="col-md-3"></div>
    <div class="col-md-6">
      <div class="box-fiche">
        <div class="box">
          <div class="box-body">
            <% out.println(pc.getHtml()); %>
            <br/>
            <div class="box-footer">
              <% if (etat < 11) { %>
                <a class="btn btn-primary pull-right" style="margin-right:10px"
                   href="<%= lien %>?but=apresTarif.jsp&acte=valider&id=<%= id %>&bute=<%= pageActuel %>&classe=<%= classe %>">
                  Viser
                </a>
                <a class="btn btn-secondary pull-right" style="margin-right:10px"
                   href="<%= lien %>?but=<%= pageModif %>&id=<%= id %>&acte=update">
                  Modifier
                </a>
                <a class="btn btn-danger pull-left"
                   href="<%= lien %>?but=apresTarif.jsp&acte=annuler&id=<%= id %>&bute=<%= pageActuel %>&classe=<%= classe %>">
                  Annuler
                </a>
              <% } %>
              <% if (etat >= 11) { %>
                <a class="btn btn-secondary pull-right" style="margin-right:10px"
                   href="<%= lien %>?but=a_tahina/v1/br/bonreception-saisie.jsp&idbc=<%= id %>">
                  R&eacute;ceptionner
                </a>
              <% } %>
            </div>
            <br/>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div class="row m-0">
    <div class="col-md-12 nopadding">
      <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
          <li class="<%= map.get("inc/bc-liste-detail") %>">
            <a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=inc/bc-liste-detail">
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