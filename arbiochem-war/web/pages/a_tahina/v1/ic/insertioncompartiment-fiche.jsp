<%@page import="a_tahina.ic.*"%>
<%@page import="a_tahina.fronthelper.ic.*"%>
<%@page import="affichage.*"%>
<%@page import="user.*"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    PageConsulte pc = ICFicheFH.getPageConsulte(request, session);
    String lien = (String) session.getValue("lien");
    InsertionCompartimentLib ic = (InsertionCompartimentLib) pc.getBase();

    String id   = ic.getTuppleID();
    int    etat = ic.getEtat();
    String idbr = ic.getIdbonreception();

    String pageActuel = ICFicheFH.PAGE_ACTUEL;

    String tab = request.getParameter("tab");
    Map<String,String> map = new HashMap<String,String>();
    map.put("inc/ic-liste-detail", "");
    map.put("inc/ic-liste-lots",   "");
    if (tab == null) tab = "inc/ic-liste-detail";
    map.put(tab, "active");
    String tabJsp = tab + ".jsp";
%>
<div class="content-wrapper">
  <h1>
    <a href="<%= lien %>?but=<%= ICFicheFH.PAGE_LISTE %>"><i class="fa fa-angle-left"></i></a>
    <%= pc.getTitre() %> #<%= id %>
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
                   href="<%= lien %>?but=apresTarif.jsp&acte=valider&id=<%= id %>&bute=<%= pageActuel %>&classe=<%= ICFicheFH.CLASSE %>">Viser</a>
                <a class="btn btn-danger pull-left"
                   href="<%= lien %>?but=apresTarif.jsp&acte=annuler&id=<%= id %>&bute=<%= pageActuel %>&classe=<%= ICFicheFH.CLASSE %>">Annuler</a>
              <% } %>
              <% if (idbr != null) { %>
                <a class="btn btn-info pull-left" style="margin-left:10px"
                   href="<%= lien %>?but=<%= ICFicheFH.PAGE_BR_FICHE %>&id=<%= idbr %>">Voir le BR</a>
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
          <li class="<%= map.get("inc/ic-liste-detail") %>">
            <a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=inc/ic-liste-detail">D&eacute;tails</a>
          </li>
          <li class="<%= map.get("inc/ic-liste-lots") %>">
            <a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=inc/ic-liste-lots">Lots cr&eacute;&eacute;s</a>
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