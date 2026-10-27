<%@page import="a_tahina.core.page.*"%>
<%@page import="a_tahina.facture.orm.*"%>
<%@page import="a_tahina.facture.fh.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    AtahinaPageConsulte pc = new AvoirFH().getPageConsulte(request, session);
    String lien = pc.getLien();
    AthAvoirLib o = (AthAvoirLib) pc.getBase();
    String id = o.getTuppleID();
    int etat = o.getEtat();

    String[] tabs   = {"inc/avoir-detail", "inc/avoir-usages"};
    String[] labels = {"Lignes retourn&eacute;es", "D&eacute;ductions"};
    String tab = request.getParameter("tab");
    if (!java.util.Arrays.asList(tabs).contains(tab)) tab = tabs[0];   // onglets connus uniquement
    String tabJsp = tab + ".jsp";
%>
<div class="content-wrapper">
  <h1>
    <a href="<%= lien %>?but=<%= AvoirFH.PAGE_LISTE %>"><i class="fa fa-angle-left"></i></a>
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
              <%-- etat < 11 : Viser / Annuler ; pas de page de modification --%>
              <% out.println(pc.getBoutonsWorkflow(id, etat, AvoirFH.PAGE_FICHE, AvoirFH.CLASSE, null)); %>
              <% if (etat == 11 && o.getMontantutilise() <= 0) { %>
                <a class="btn btn-warning pull-left"
                   href="<%= pc.lienActe("annulerVisa", id, AvoirFH.PAGE_FICHE, AvoirFH.CLASSE) %>">Annuler le visa</a>
              <% } %>
              <a class="btn btn-info pull-left" style="margin-left:10px"
                 href="<%= lien %>?but=<%= FactureFH.PAGE_FICHE %>&id=<%= AtahinaUtil.url(o.getIdfacture()) %>">Voir la facture</a>
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
          <% for (int i = 0; i < tabs.length; i++) { %>
            <li class="<%= tabs[i].equals(tab) ? "active" : "" %>">
              <a href="<%= lien %>?but=<%= AvoirFH.PAGE_FICHE %>&id=<%= AtahinaUtil.url(id) %>&tab=<%= tabs[i] %>"><%= labels[i] %></a>
            </li>
          <% } %>
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
     out.println(AtahinaUtil.erreurHtml(e));
   } %>
