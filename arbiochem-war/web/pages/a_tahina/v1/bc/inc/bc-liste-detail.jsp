<%@page import="affichage.PageRecherche"%>
<%@page import="a_tahina.bc.BonCommandePoussinFilleLib"%>
<% try {
    String idMere = request.getParameter("idMere");
    if (idMere == null) idMere = request.getParameter("id");

    BonCommandePoussinFilleLib t = new BonCommandePoussinFilleLib();
    t.setNomTable("BONCOMMANDE_POUSSIN_FILLE_LIB");

    String[] listeCrt  = {};
    String[] listeInt  = {};
    String[] libEntete = {"idproduitlib", "souchelibr", "qte", "pu",
                          "montant", "fournisseurlib"};

    PageRecherche pr = new PageRecherche(t, request, listeCrt, listeInt, 3,
                                          libEntete, libEntete.length);
    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));
    pr.setApres("a_tahina/v1/bc/boncommande-fiche.jsp&id=" + idMere
              + "&tab=inc/bc-liste-detail");
    pr.setAWhere(" and idmere = '" + idMere + "'");
    pr.setNpp(10);

    pr.creerObjetPage(libEntete, null);

    pr.getTableau().setLibelleAffiche(new String[]{
        "Produit", "Souche", "Qt&eacute;", "P.U.",
        "Montant", "Fournisseur"
    });
%>
<div class="box-body">
  <%
    if (pr.getTableau().getHtml() != null) {
        out.println(pr.getTableau().getHtml());
    } else {
  %>
      <center><h4>Aucune ligne trouv&eacute;e</h4></center>
  <% } %>
</div>
<% } catch (Exception e) {
     out.println("<pre style='color:red'>");
     e.printStackTrace(new java.io.PrintWriter(out));
     out.println("</pre>");
   } %>