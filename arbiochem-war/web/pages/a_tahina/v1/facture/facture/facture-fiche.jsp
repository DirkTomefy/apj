<%@page import="a_tahina.core.page.*"%>
<%@page import="a_tahina.facture.orm.*"%>
<%@page import="a_tahina.facture.fh.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    AtahinaPageConsulte pc = new FactureFH().getPageConsulte(request, session);
    String lien = pc.getLien();
    AthFactureLib o = (AthFactureLib) pc.getBase();
    String id = o.getTuppleID();
    int etat = o.getEtat();

    String[] tabs   = {"inc/facture-detail", "inc/facture-avoirs", "inc/facture-usages"};
    String[] labels = {"D&eacute;tails", "Avoirs", "Avoirs d&eacute;duits"};
    String tab = request.getParameter("tab");
    if (!java.util.Arrays.asList(tabs).contains(tab)) tab = tabs[0];   // onglets connus uniquement
    String tabJsp = tab + ".jsp";
%>
<div class="content-wrapper">
  <h1>
    <a href="<%= lien %>?but=<%= FactureFH.PAGE_LISTE %>"><i class="fa fa-angle-left"></i></a>
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
              <% out.println(pc.getBoutonsWorkflow(id, etat, FactureFH.PAGE_FICHE, FactureFH.CLASSE, null)); %>
              <% if (etat == 11) { %>
                <a class="btn btn-success pull-right" style="margin-right:10px"
                   href="<%= lien %>?but=<%= AvoirFH.PAGE_SAISIE %>&idfacture=<%= AtahinaUtil.url(id) %>">Cr&eacute;er un avoir</a>
                <a class="btn btn-warning pull-left"
                   href="<%= pc.lienActe("annulerVisa", id, FactureFH.PAGE_FICHE, FactureFH.CLASSE) %>">Annuler le visa</a>
              <% } %>
              <a class="btn btn-info pull-left" style="margin-left:10px"
                 href="<%= lien %>?but=<%= ClientFH.PAGE_FICHE %>&id=<%= AtahinaUtil.url(o.getIdclient()) %>">Voir le client</a>
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
              <a href="<%= lien %>?but=<%= FactureFH.PAGE_FICHE %>&id=<%= AtahinaUtil.url(id) %>&tab=<%= tabs[i] %>"><%= labels[i] %></a>
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
