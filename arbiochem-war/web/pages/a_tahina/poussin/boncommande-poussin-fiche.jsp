<%@page import="affichage.PageConsulte"%>
<%@page import="a_tahina.fronthelper.BonCommandePoussinFH"%>
<%@page import="a_tahina.poussin.BonCommandePoussinLib"%>
<% try {
    String lien = (String) session.getValue("lien");

    // 1) Construire la PageConsulte via le FH
    PageConsulte pc = BonCommandePoussinFH.getPageConsulte(request, session);

    BonCommandePoussinLib o = (BonCommandePoussinLib) pc.getBase();
    String id     = o.getTuppleID();
    int    etat   = o.getEtat();

    // 2) Préparer les onglets
    java.util.Map<String,String> map = new java.util.HashMap<>();
    map.put("inc/boncommande-poussin-fille-liste", "");
    map.put("inc/boncommande-poussin-historique", "");

    String tab = request.getParameter("tab");
    if (tab == null) tab = "inc/boncommande-poussin-fille-liste";
    map.put(tab, "active");
    String pageIncluse = tab + ".jsp";

    // 3) Chemins / classes
    String pageActuel = "a_tahina/poussin/boncommande-poussin-fiche.jsp";
    String pageModif  = "a_tahina/poussin/boncommande-poussin-modif.jsp";
    String classe     = "a_tahina.poussin.BonCommandePoussin";
%>
<div class="content-wrapper">
  <h1 class="box-title">
    <a href="<%= lien %>?but=a_tahina/poussin/boncommande-poussin-liste.jsp">
      <i class="fa fa-angle-left"></i></a>
    <%= pc.getTitre() %>
  </h1>

  <div class="row m-0">
    <div class="col-md-6">
      <div class="box-fiche"><div class="box"><div class="box-body">

        <% out.println(pc.getHtml()); %>

        <br>
        <div class="box-footer">
          <% if (etat < 11) { %>
            <%-- Bouton Viser --%>
            <a class="btn btn-primary pull-right"
               href="<%= lien %>?but=apresTarif.jsp&acte=valider&id=<%= id %>
                     &bute=<%= pageActuel %>&classe=<%= classe %>">Viser</a>

            <%-- Bouton Modifier --%>
            <a class="btn btn-secondary pull-right"
               href="<%= lien %>?but=<%= pageModif %>&id=<%= id %>&acte=update">
               Modifier</a>

            <%-- Bouton Annuler --%>
            <a class="btn btn-danger pull-left"
               href="<%= lien %>?but=apresTarif.jsp&acte=annuler&id=<%= id %>
                     &bute=<%= pageActuel %>&classe=<%= classe %>">Annuler</a>
          <% } %>

          <% if (etat >= 11) { %>
            <%-- Annuler visa --%>
            <a class="btn btn-danger pull-left"
               href="<%= lien %>?but=apresTarif.jsp&acte=annulerVisa&id=<%= id %>
                     &bute=<%= pageActuel %>&classe=<%= classe %>">Annuler Visa</a>

            <%-- Clôturer --%>
            <a class="btn btn-warning pull-right"
               href="<%= lien %>?but=apresTarif.jsp&acte=cloturer&id=<%= id %>
                     &bute=<%= pageActuel %>&classe=<%= classe %>">Clôturer</a>
          <% } %>
        </div>

      </div></div></div>
    </div>
  </div>

  <%-- Onglets --%>
  <div class="row m-0">
    <div class="col-md-12 nopadding">
      <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
          <li class="<%= map.get("inc/boncommande-poussin-fille-liste") %>">
            <a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>
                     &tab=inc/boncommande-poussin-fille-liste">Détails</a>
          </li>
          <li class="<%= map.get("inc/boncommande-poussin-historique") %>">
            <a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>
                     &tab=inc/boncommande-poussin-historique">Historique</a>
          </li>
        </ul>
        <div class="tab-content">
          <jsp:include page="<%= pageIncluse %>">
            <jsp:param name="idMere" value="<%= id %>" />
          </jsp:include>
        </div>
      </div>
    </div>
  </div>
</div>
<% } catch(Exception e){ e.printStackTrace(); } %>