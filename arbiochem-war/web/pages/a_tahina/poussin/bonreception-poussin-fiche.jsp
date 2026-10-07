<%@page import="affichage.PageConsulte"%>
<%@page import="a_tahina.poussin.BonReceptionPoussinLib"%>
<%@page import="java.util.*"%>
<% try {
    user.UserEJB u = (user.UserEJB) session.getValue("u");
    String lien = (String) session.getValue("lien");

    BonReceptionPoussinLib t = new BonReceptionPoussinLib();
    t.setNomTable("BONRECEPTION_POUSSIN_LIB");

    PageConsulte pc = new PageConsulte(t, request, u);
    pc.setTitre("Fiche d'un bon de réception poussin");
    String id = pc.getBase().getTuppleID();

    pc.getChampByName("id").setLibelle("Id");
    pc.getChampByName("daty").setLibelle("Date");
    pc.getChampByName("reference").setLibelle("Référence");
    pc.getChampByName("idlieureceptionlib").setLibelle("Lieu de réception");
    pc.getChampByName("fournisseurlib").setLibelle("Fournisseur");
    pc.getChampByName("est_finallib").setLibelle("Final");
    pc.getChampByName("etatlib").setLibelle("État");
    pc.getChampByName("remarque").setLibelle("Remarque");

    pc.getChampByName("idlieureception").setVisible(false);
    pc.getChampByName("idboncommandepoussin").setVisible(false);
    pc.getChampByName("idbonreceptionprecedent").setVisible(false);
    pc.getChampByName("idfournisseur").setVisible(false);

    String pageActuel = "a_tahina/poussin/bonreception-poussin-fiche.jsp";
    String classe = "a_tahina.poussin.BonReceptionPoussin";

    Map<String,String> map = new HashMap<>();
    map.put("inc/bonreception-poussin-details", "");
    String tab = request.getParameter("tab");
    if (tab == null) tab = "inc/bonreception-poussin-details";
    map.put(tab, "active");
    tab = tab + ".jsp";

    int etat = ((BonReceptionPoussinLib) pc.getBase()).getEtat();
%>
<div class="content-wrapper">
  <h1 class="box-title">
    <a href="<%= lien %>?but=a_tahina/poussin/bonreception-poussin-liste.jsp">
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
               href="<%= lien %>?but=a_tahina/poussin/bonreception-poussin-saisie.jsp&id=<%= id %>&acte=update">
              Modifier</a>
            <a class="btn btn-danger pull-left"
               href="<%= lien %>?but=apresTarif.jsp&acte=annuler&id=<%= id %>
                     &bute=<%= pageActuel %>&classe=<%= classe %>">Annuler</a>
          <% } %>
          <a class="btn btn-secondary pull-right"
             href="<%= lien %>?but=a_tahina/poussin/insertion-compartiment-saisie.jsp&idbonreception=<%= id %>"
             style="margin-right: 8px;">
            Insérer en compartiment
          </a>
        </div>
      </div></div></div>
    </div>
  </div>

  <div class="row m-0">
    <div class="col-md-12 nopadding">
      <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
          <li class="<%= map.get("inc/bonreception-poussin-details") %>">
            <a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=inc/bonreception-poussin-details">
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