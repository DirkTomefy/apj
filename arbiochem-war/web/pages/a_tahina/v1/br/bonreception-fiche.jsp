<%@page import="a_tahina.br.*"%>
<%@page import="a_tahina.fronthelper.br.*"%>
<%@page import="affichage.*"%>
<%@page import="user.*"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    PageConsulte pc = BRFicheFH.getPageConsulte(request, session);
    String lien = (String) session.getValue("lien");
    BonReceptionPoussinLib br = (BonReceptionPoussinLib) pc.getBase();

    String id    = br.getTuppleID();
    int    etat  = br.getEtat();
    String idbc  = br.getIdboncommande();

    String pageActuel = BRFicheFH.PAGE_ACTUEL;

    String tab = request.getParameter("tab");
    Map<String,String> map = new HashMap<String,String>();
    map.put("inc/br-liste-detail", "");
    if (tab == null) tab = "inc/br-liste-detail";
    map.put(tab, "active");
    String tabJsp = tab + ".jsp";
%>
<div class="content-wrapper">
  <h1>
    <a href="<%= lien %>?but=<%= BRFicheFH.PAGE_LISTE %>"><i class="fa fa-angle-left"></i></a>
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
                   href="<%= lien %>?but=apresTarif.jsp&acte=valider&id=<%= id %>&bute=<%= pageActuel %>&classe=<%= BRFicheFH.CLASSE %>">Viser</a>
                <a class="btn btn-secondary pull-right" style="margin-right:10px"
                   href="<%= lien %>?but=<%= BRFicheFH.PAGE_MODIF %>&id=<%= id %>&acte=update">Modifier</a>
                <a class="btn btn-danger pull-left"
                   href="<%= lien %>?but=apresTarif.jsp&acte=annuler&id=<%= id %>&bute=<%= pageActuel %>&classe=<%= BRFicheFH.CLASSE %>">Annuler</a>
              <% } %>
              <% if (etat >= 11 && idbc != null) { %>
                <a class="btn btn-warning pull-right" style="margin-right:10px"
                   href="<%= lien %>?but=<%= BRFicheFH.PAGE_SAISIE %>&idbc=<%= idbc %>&idbcf=<%= br.getIdboncommandefille() %>">R&eacute;ceptionner &agrave; nouveau</a>
              <% } %>
              <% if (etat >= 11) { %>
                <a class="btn btn-success pull-right" style="margin-right:10px"
                   href="<%= lien %>?but=a_tahina/v1/ic/insertioncompartiment-saisie.jsp&idbr=<%= id %>">Ins&eacute;rer en compartiment</a>
              <% } %>
              <% if (idbc != null) { %>
                <a class="btn btn-info pull-left" style="margin-left:10px"
                   href="<%= lien %>?but=<%= BRFicheFH.PAGE_BC_FICHE %>&id=<%= idbc %>">Voir le BC</a>
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
          <li class="<%= map.get("inc/br-liste-detail") %>">
            <a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=inc/br-liste-detail">D&eacute;tails</a>
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