<%@page import="affichage.PageRecherche"%>
<%@page import="a_tahina.poussin.InsertionCompartimentLib"%>
<% try {
    InsertionCompartimentLib t = new InsertionCompartimentLib();
    String[] listeCrt  = {"id", "reference", "daty"};
    String[] listeInt  = {"daty"};
    String[] libEntete = {"id", "daty", "reference", "idlieulib", "idbonreceptionlib", "etatlib"};

    PageRecherche pr = new PageRecherche(t, request, listeCrt, listeInt, 3,
                                          libEntete, libEntete.length);
    pr.setTitre("Liste des insertions en compartiment");
    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));
    pr.setApres("a_tahina/poussin/insertion-compartiment-liste.jsp");
    pr.setNpp(50);

    pr.getFormu().getChamp("reference").setLibelle("Référence");
    pr.getFormu().getChamp("daty1").setLibelle("Date min");
    pr.getFormu().getChamp("daty2").setLibelle("Date max");

    pr.creerObjetPage(libEntete, null);

    pr.getTableau().setLien(new String[]{
        pr.getLien() + "?but=a_tahina/poussin/insertion-compartiment-fiche.jsp"
    });
    pr.getTableau().setColonneLien(new String[]{"id"});
    pr.getTableau().setLibelleAffiche(new String[]{
        "Id", "Date", "Référence", "Lieu", "BR origine", "État"
    });
    pr.getTableau().setLienFille("a_tahina/poussin/inc/insertion-compartiment-details.jsp&id=");

    pr.getFormu().setAnotherButton(
        "<a class='btn btn-primary pull-right btn-small' " +
        "href='module.jsp?but=a_tahina/poussin/insertion-compartiment-saisie.jsp'>" +
        "<i class='material-symbols-rounded'>add</i> Nouvelle insertion</a>");
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