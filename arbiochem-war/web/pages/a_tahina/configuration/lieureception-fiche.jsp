<%@page import="affichage.PageConsulte"%>
<%@page import="a_tahina.configuration.LieuReception"%>
<% try {
    user.UserEJB u = (user.UserEJB) session.getValue("u");
    String lien = (String) session.getValue("lien");

    LieuReception t = new LieuReception();
    PageConsulte pc = new PageConsulte(t, request, u);
    pc.setTitre("Fiche d'un lieu de réception");
    String id = pc.getBase().getTuppleID();

    pc.getChampByName("id").setLibelle("Id");
    pc.getChampByName("val").setLibelle("Lieu");
    pc.getChampByName("desce").setLibelle("Description");

    String classe = "a_tahina.configuration.LieuReception";
%>
<div class="content-wrapper">
  <h1 class="box-title">
    <a href="<%= lien %>?but=a_tahina/configuration/lieureception-liste.jsp">
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
             href="<%= lien %>?but=a_tahina/configuration/lieureception-saisie.jsp&id=<%= id %>&acte=update">
            Modifier</a>
          <a class="btn btn-danger pull-left"
             href="<%= lien %>?but=apresTarif.jsp&id=<%= id %>&acte=delete
                    &bute=a_tahina/configuration/lieureception-liste.jsp&classe=<%= classe %>">
            Supprimer</a>
        </div>
      </div></div></div>
    </div>
  </div>
</div>
<% } catch (Exception e) { e.printStackTrace(); } %>