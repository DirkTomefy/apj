<%@page import="a_tahina.core.page.*"%>
<%@page import="a_tahina.facture.orm.*"%>
<%@page import="a_tahina.facture.fh.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    AtahinaPageConsulte pc = new ClientFH().getPageConsulte(request, session);
    String lien = pc.getLien();
    AthClientSolde o = (AthClientSolde) pc.getBase();
    String id = o.getTuppleID();

    String[] tabs   = {"inc/client-factures", "inc/client-avoirs"};
    String[] labels = {"Factures", "Avoirs"};
    String tab = request.getParameter("tab");
    if (!java.util.Arrays.asList(tabs).contains(tab)) tab = tabs[0];   // onglets connus uniquement
    String tabJsp = tab + ".jsp";
%>
<div class="content-wrapper">
  <h1>
    <a href="<%= lien %>?but=<%= ClientFH.PAGE_LISTE %>"><i class="fa fa-angle-left"></i></a>
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
              <a class="btn btn-primary pull-right" href="<%= lien %>?but=<%= FactureFH.PAGE_SAISIE %>">Nouvelle facture</a>
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
              <a href="<%= lien %>?but=<%= ClientFH.PAGE_FICHE %>&id=<%= AtahinaUtil.url(id) %>&tab=<%= tabs[i] %>"><%= labels[i] %></a>
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
