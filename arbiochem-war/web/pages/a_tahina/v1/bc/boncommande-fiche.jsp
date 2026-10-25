<%@page import="a_tahina.bc.*"%>
<%@page import="a_tahina.fronthelper.bc.*"%>
<%@page import="a_tahina.fronthelper.br.BRInsertFH"%>
<%@page import="a_tahina.page.*"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
  try {
    AtahinaPageConsulte pc = BCmdFicheFH.getPageConsulte(request, session);
    String lien = pc.getLien();
    BonCommandePoussinLib bc = (BonCommandePoussinLib) pc.getBase();
    String id = bc.getTuppleID();
    int etat = bc.getEtat();

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
    <a href="<%= lien %>?but=<%= BCmdFicheFH.PAGE_LISTE %>"><i class="fa fa-angle-left"></i></a>
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
              <% out.println(pc.getBoutonsWorkflow(id, etat, BCmdFicheFH.PAGE_ACTUEL,
                                                   BCmdFicheFH.CLASSE, BCmdFicheFH.PAGE_MODIF)); %>
              <% if (etat >= 11) { %>
                <a class="btn btn-secondary pull-right" style="margin-right:10px"
                   href="<%= lien %>?but=<%= BRInsertFH.PAGE_ACTUEL %>&idbc=<%= id %>">
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
            <a href="<%= lien %>?but=<%= BCmdFicheFH.PAGE_ACTUEL %>&id=<%= id %>&tab=inc/bc-liste-detail">
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
     out.println(AtahinaUtil.erreurHtml(e));
   } %>
