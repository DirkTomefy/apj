<%@page import="a_tahina.ic.orm.*"%>
<%@page import="a_tahina.compartiment.orm.*"%>
<%@page import="a_tahina.ic.fh.*"%>
<%@page import="a_tahina.core.page.*"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    AtahinaPageConsulte pc = new ICFH().getPageConsulte(request, session);
    String lien = pc.getLien();
    InsertionCompartimentLib ic = (InsertionCompartimentLib) pc.getBase();

    String id   = ic.getTuppleID();
    int    etat = ic.getEtat();
    String idbr = ic.getIdbonreception();

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
    <a href="<%= lien %>?but=<%= ICFH.PAGE_LISTE %>"><i class="fa fa-angle-left"></i></a>
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
              <%-- pas de page de modification pour une insertion : pageModif = null --%>
              <% out.println(pc.getBoutonsWorkflow(id, etat, ICFH.PAGE_FICHE, ICFH.CLASSE, null)); %>
              <% if (idbr != null) { %>
                <a class="btn btn-info pull-left" style="margin-left:10px"
                   href="<%= lien %>?but=<%= ICFH.PAGE_BR_FICHE %>&id=<%= idbr %>">Voir le BR</a>
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
            <a href="<%= lien %>?but=<%= ICFH.PAGE_FICHE %>&id=<%= id %>&tab=inc/ic-liste-detail">D&eacute;tails</a>
          </li>
          <li class="<%= map.get("inc/ic-liste-lots") %>">
            <a href="<%= lien %>?but=<%= ICFH.PAGE_FICHE %>&id=<%= id %>&tab=inc/ic-liste-lots">Lots cr&eacute;&eacute;s</a>
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
