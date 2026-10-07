<%@page import="affichage.PageConsulte"%>
<%@page import="a_tahina.poussin.BonCommandePoussinLib"%>
<%@page import="java.util.*"%>
<% try {
    user.UserEJB u = (user.UserEJB) session.getValue("u");
    String lien = (String) session.getValue("lien");

    BonCommandePoussinLib t = new BonCommandePoussinLib();
    t.setNomTable("BONCOMMANDE_POUSSIN_LIB");

    PageConsulte pc = new PageConsulte(t, request, u);
    pc.setTitre("Fiche d'un bon de commande poussin");
    String id = pc.getBase().getTuppleID();

    pc.getChampByName("id").setLibelle("Id");
    pc.getChampByName("daty").setLibelle("Date");
    pc.getChampByName("reference").setLibelle("Référence");
    pc.getChampByName("fournisseurlib").setLibelle("Fournisseur");
    pc.getChampByName("modepaiementlib").setLibelle("Mode de paiement");
    pc.getChampByName("iddeviselib").setLibelle("Devise");
    pc.getChampByName("idmagasinlib").setLibelle("Magasin");
    pc.getChampByName("datelimite").setLibelle("Date limite");
    pc.getChampByName("etatlib").setLibelle("État");
    pc.getChampByName("remarque").setLibelle("Remarque");

    pc.getChampByName("idfournisseur").setVisible(false);
    pc.getChampByName("modepaiement").setVisible(false);
    pc.getChampByName("iddevise").setVisible(false);
    pc.getChampByName("idmagasin").setVisible(false);
    pc.getChampByName("iddmdachat").setVisible(false);

    String pageActuel = "a_tahina/poussin/boncommande-poussin-fiche.jsp";
    String classe = "a_tahina.poussin.BonCommandePoussin";

    Map<String,String> map = new HashMap<>();
    map.put("inc/boncommande-poussin-details", "");
    String tab = request.getParameter("tab");
    if (tab == null) tab = "inc/boncommande-poussin-details";
    map.put(tab, "active");
    tab = tab + ".jsp";

    int etat = ((BonCommandePoussinLib) pc.getBase()).getEtat();
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
            <a class="btn btn-primary pull-right"
               href="<%= lien %>?but=apresTarif.jsp&acte=valider&id=<%= id %>
                     &bute=<%= pageActuel %>&classe=<%= classe %>">Viser</a>
            <a class="btn btn-secondary pull-right"
               href="<%= lien %>?but=a_tahina/poussin/boncommande-poussin-saisie.jsp&id=<%= id %>&acte=update">
              Modifier</a>
            <a class="btn btn-danger pull-left"
               href="<%= lien %>?but=apresTarif.jsp&acte=annuler&id=<%= id %>
                     &bute=<%= pageActuel %>&classe=<%= classe %>">Annuler</a>
          <% } else { %>
            <a class="btn btn-danger pull-left"
               href="<%= lien %>?but=apresTarif.jsp&acte=annulerVisa&id=<%= id %>
                     &bute=<%= pageActuel %>&classe=<%= classe %>">Annuler Visa</a>
          <% } %>
        </div>
      </div></div></div>
    </div>
  </div>

  <div class="row m-0">
    <div class="col-md-12 nopadding">
      <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
          <li class="<%= map.get("inc/boncommande-poussin-details") %>">
            <a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>&tab=inc/boncommande-poussin-details">
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