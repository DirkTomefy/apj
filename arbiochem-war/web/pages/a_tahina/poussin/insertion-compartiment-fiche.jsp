<%@page import="affichage.PageConsulte"%>
<%@page import="a_tahina.poussin.InsertionCompartimentLib"%>
<%@page import="java.util.*"%>
<% try {
    user.UserEJB u = (user.UserEJB) session.getValue("u");
    String lien = (String) session.getValue("lien");

    InsertionCompartimentLib t = new InsertionCompartimentLib();
    t.setNomTable("INSERTION_COMPARTIMENT_LIB");

    PageConsulte pc = new PageConsulte(t, request, u);
    pc.setTitre("Fiche d'une insertion en compartiment");
    String id = pc.getBase().getTuppleID();

    pc.getChampByName("id").setLibelle("Id");
    pc.getChampByName("daty").setLibelle("Date");
    pc.getChampByName("reference").setLibelle("Référence");
    pc.getChampByName("idlieulib").setLibelle("Lieu");
    pc.getChampByName("idbonreceptionlib").setLibelle("BR origine");
    pc.getChampByName("etatlib").setLibelle("État");
    pc.getChampByName("remarque").setLibelle("Remarque");

    pc.getChampByName("idlieu").setVisible(false);
    pc.getChampByName("idbonreception").setVisible(false);

    String pageActuel = "a_tahina/poussin/insertion-compartiment-fiche.jsp";
    String classe = "a_tahina.poussin.InsertionCompartiment";

    Map<String,String> map = new HashMap<>();
    map.put("inc/insertion-compartiment-details", "");
    String tab = request.getParameter("tab");
    if (tab == null) tab = "inc/insertion-compartiment-details";
    map.put(tab, "active");
    tab = tab + ".jsp";

    int etat = ((InsertionCompartimentLib) pc.getBase()).getEtat();
%>
<div class="content-wrapper">
  <h1 class="box-title">
    <a href="<%= lien %>?but=a_tahina/poussin/insertion-compartiment-liste.jsp">
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
            <a class="btn btn-primary pull-right"
               href="<%= lien %>?but=apresTarif.jsp&acte=valider&id=<%= id %>
                     &bute=<%= pageActuel %>&classe=<%= classe %>">Viser</a>
            <a class="btn btn-secondary pull-right"
               href="<%= lien %>?but=a_tahina/poussin/insertion-compartiment-saisie.jsp&id=<%= id %>&acte=update">
              Modifier</a>
            <a class="btn btn-danger pull-left"
               href="<%= lien %>?but=apresTarif.jsp&acte=annuler&id=<%= id %>
                     &bute=<%= pageActuel %>&classe=<%= classe %>">Annuler</a>
          <% } %>
        </div>
      </div></div></div>
    </div>
  </div>

  <div class="row m-0">
    <div class="col-md-12 nopadding">
      <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
          <li class="<%= map.get("inc/insertion-compartiment-details") %>">
            <a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=inc/insertion-compartiment-details">
              Détails</a></li>
        </ul>
        <div class="tab-content">
          <jsp:include page="<%= tab %>">
            <jsp:param name="idMere" value="<%= id %>" />
          </jsp:include>
        </div>
      </div>
    </div>
  </div>
</div>
<% } catch (Exception e) { e.printStackTrace(); } %>