<%@page import="affichage.PageRecherche"%>
<%@page import="affichage.Liste"%>
<%@page import="ferme.configuration.BatimentLib"%>
<%@page import="a_tahina.configuration.CompartimentLib"%>
<% try {
    CompartimentLib t = new CompartimentLib();
    t.setNomTable("COMPARTIMENT_LIB");

    String[] listeCrt  = {"id", "libelle", "batimentlib"};
    String[] listeInt  = {};
    String[] libEntete = {"id", "libelle", "capacite", "batimentlib"};

    PageRecherche pr = new PageRecherche(t, request, listeCrt, listeInt, 3,
                                          libEntete, libEntete.length);
    pr.setTitre("Liste des compartiments");
    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));
    pr.setApres("a_tahina/configuration/compartiment-liste.jsp");
    pr.setNpp(50);

    pr.getFormu().getChamp("libelle").setLibelle("Libellé");
    pr.getFormu().getChamp("batimentlib").setLibelle("Bâtiment");

    Liste[] liste = new Liste[1];
    liste[0] = new Liste("batimentlib", new BatimentLib(), "nombatiment", "id");
    pr.getFormu().changerEnChamp(liste);

    pr.creerObjetPage(libEntete, null);

    pr.getTableau().setLien(new String[]{
        pr.getLien() + "?but=a_tahina/configuration/compartiment-fiche.jsp"
    });
    pr.getTableau().setColonneLien(new String[]{"id"});
    pr.getTableau().setLibelleAffiche(new String[]{
        "Id", "Libellé", "Capacité", "Bâtiment"
    });

    pr.getFormu().setAnotherButton(
        "<a class='btn btn-primary pull-right btn-small' " +
        "href='module.jsp?but=a_tahina/configuration/compartiment-saisie.jsp'>" +
        "<i class='material-symbols-rounded'>add</i> Saisir un compartiment</a>");
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