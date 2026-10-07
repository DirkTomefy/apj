<%@page import="affichage.PageConsulte"%>
<%@page import="a_tahina.configuration.CompartimentLib"%>
<% try {
    user.UserEJB u = (user.UserEJB) session.getValue("u");
    String lien = (String) session.getValue("lien");

    CompartimentLib t = new CompartimentLib();
    t.setNomTable("COMPARTIMENT_LIB");

    PageConsulte pc = new PageConsulte(t, request, u);
    pc.setTitre("Fiche d'un compartiment");
    String id = pc.getBase().getTuppleID();

    pc.getChampByName("id").setLibelle("Id");
    pc.getChampByName("libelle").setLibelle("Libellé");
    pc.getChampByName("capacite").setLibelle("Capacité");
    pc.getChampByName("batimentlib").setLibelle("Bâtiment");
    pc.getChampByName("idbatiment").setVisible(false);

    String classe = "a_tahina.configuration.Compartiment";
%>
<div class="content-wrapper">
  <h1 class="box-title">
    <a href="<%= lien %>?but=a_tahina/configuration/compartiment-liste.jsp">
      <i class="fa fa-angle-left"></i></a>
    <%= pc.getTitre() %>
  </h1>

  <div class="row m-0">
    <div class="col-md-6">
      <div class="box-fiche"><div class="box"><div class="box-body">
        <% out.println(pc.getHtml()); %>
        <br>
        <div class="box-footer">
          <a class="btn btn-secondary pull-right"
             href="<%= lien %>?but=a_tahina/configuration/compartiment-saisie.jsp&id=<%= id %>&acte=update">
            Modifier</a>
          <a class="btn btn-danger pull-left"
             href="<%= lien %>?but=apresTarif.jsp&id=<%= id %>&acte=delete
                    &bute=a_tahina/configuration/compartiment-liste.jsp&classe=<%= classe %>">
            Supprimer</a>
        </div>
      </div></div></div>
    </div>
  </div>
</div>
<% } catch (Exception e) { e.printStackTrace(); } %>