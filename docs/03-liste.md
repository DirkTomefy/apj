# 03 — Créer un écran LISTE

**Fichier** : `pages/monpackage/monobjet-liste.jsp`

```jsp
<%@page import="affichage.PageRecherche"%>
<%@page import="affichage.Liste"%>
<%@page import="bean.TypeObjet"%>
<%@page import="monpackage.MonObjetCpl"%>
<% try {
    // 1) Objet « Cpl » qui lit une vue SQL avec les libellés
    MonObjetCpl f = new MonObjetCpl();
    f.setNomTable("MONOBJET_CPL");

    // 2) Colonnes du tableau et critères de recherche
    String[] listeCrt  = {"id","daty","libelle","etatlib"};  // champs filtrables
    String[] listeInt  = {"daty"};                           // champs intervalle
    String[] libEntete = {"id","daty","libelle","etatlib"};  // colonnes du tableau

    // 3) Construction
    PageRecherche pr = new PageRecherche(f, request, listeCrt, listeInt, 3,
                                          libEntete, libEntete.length);
    pr.setTitre("Liste des objets");
    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));
    pr.setApres("monpackage/monobjet-liste.jsp");
    pr.setNpp(50);

    // 4) Filtres supplémentaires (hors formulaire standard)
    String awhere = "";
    if (request.getParameter("etat") != null
        && !request.getParameter("etat").isEmpty())
        awhere += " and etat = " + request.getParameter("etat");
    pr.setAWhere(awhere);

    // 5) Personnalisation des libellés
    pr.getFormu().getChamp("libelle").setLibelle("Libellé");
    pr.getFormu().getChamp("daty1").setLibelle("Date min");
    pr.getFormu().getChamp("daty2").setLibelle("Date max");
    pr.getFormu().getChamp("daty1").setDefaut(utilitaire.Utilitaire.dateDuJour());
    pr.getFormu().getChamp("daty2").setDefaut(utilitaire.Utilitaire.dateDuJour());

    // 6) Un champ → Liste déroulante
    Liste[] liste = new Liste[1];
    liste[0] = new Liste("etatlib", new TypeObjet("ETAT"), "val", "val");
    pr.getFormu().changerEnChamp(liste);

    // 7) Exécution
    pr.creerObjetPage(libEntete, null);

    // 8) Liens et libellés du tableau
    String[] lienTableau = { pr.getLien() + "?but=monpackage/monobjet-fiche.jsp" };
    String[] colonneLien = { "id" };
    pr.getTableau().setLien(lienTableau);
    pr.getTableau().setColonneLien(colonneLien);
    pr.getTableau().setLibelleAffiche(new String[]{
        "Id","Date","Libellé","État"
    });

    // 9) Lien « + » pour déplier un détail
    pr.getTableau().setLienFille("monpackage/inc/monobjet-liste-detail.jsp&id=");

    // 10) Menu contextuel clic droit
    Map<String,String> lienTab = new HashMap<>();
    lienTab.put("modifier",
        pr.getLien() + "?but=monpackage/monobjet-modif.jsp");
    pr.getTableau().setLienClicDroite(lienTab);

    // 11) Bouton « + Ajouter »
    pr.getFormu().setAnotherButton(
        "<a class='btn btn-primary pull-right btn-small' " +
        "href='module.jsp?but=monpackage/monobjet-saisie.jsp'>" +
        "<i class='material-symbols-rounded'>add</i> Saisir</a>");
%>
<script>
    function changerDesignation() { document.filtre.submit(); }
</script>

<div class="content-wrapper">
  <section class="content-header">
    <h1><%= pr.getTitre() %></h1>
  </section>
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
<% } catch(Exception e){ e.printStackTrace(); } %>