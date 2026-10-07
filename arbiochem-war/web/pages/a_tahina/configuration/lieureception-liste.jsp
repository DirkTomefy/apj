<%@page import="affichage.PageRecherche"%>
<%@page import="a_tahina.configuration.LieuReception"%>
<% try {
    LieuReception t = new LieuReception();
    String[] listeCrt  = {"id", "val", "desce"};
    String[] listeInt  = {};
    String[] libEntete = {"id", "val", "desce"};

    PageRecherche pr = new PageRecherche(t, request, listeCrt, listeInt, 3,
                                          libEntete, libEntete.length);
    pr.setTitre("Liste des lieux de réception");
    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));
    pr.setApres("a_tahina/configuration/lieureception-liste.jsp");
    pr.setNpp(50);

    pr.getFormu().getChamp("id").setLibelle("Id");
    pr.getFormu().getChamp("val").setLibelle("Lieu");
    pr.getFormu().getChamp("desce").setLibelle("Description");

    pr.creerObjetPage(libEntete, null);

    pr.getTableau().setLien(new String[]{
        pr.getLien() + "?but=a_tahina/configuration/lieureception-fiche.jsp"
    });
    pr.getTableau().setColonneLien(new String[]{"id"});
    pr.getTableau().setLibelleAffiche(new String[]{"Id", "Lieu", "Description"});

    pr.getFormu().setAnotherButton(
        "<a class='btn btn-primary pull-right btn-small' " +
        "href='module.jsp?but=a_tahina/configuration/lieureception-saisie.jsp'>" +
        "<i class='material-symbols-rounded'>add</i> Ajouter un lieu</a>");
%>
<div class="content-wrapper">
  <section class="content-header"><h1><%= pr.getTitre() %></h1></section>
  <section class="content">
    <form action="<%= pr.getLien() %>?but=<%= pr.getApres() %>"
          method="post" name="filtre">
      <% out.println(pr.getFormu().getHtmlEnsemble()); %>
    </form>
    <% out.println(pr.getTableauRecap().getHtml()); %>
    <br>
    <% out.println(pr.getTableau().getHtml());
       out.println(pr.getBasPage()); %>
  </section>
</div>
<% } catch (Exception e) { e.printStackTrace(); } %>