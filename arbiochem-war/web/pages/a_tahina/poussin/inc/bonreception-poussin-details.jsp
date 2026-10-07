<%@page import="affichage.PageRecherche"%>
<%@page import="a_tahina.poussin.BonReceptionPoussinFilleLib"%>
<% try {
    String idMere = request.getParameter("idMere");
    if (idMere == null) idMere = request.getParameter("id");

    BonReceptionPoussinFilleLib t = new BonReceptionPoussinFilleLib();
    String[] listeCrt  = {};
    String[] listeInt  = {};
    String[] libEntete = {"id", "produitlib", "sexelib", "quantite",
                          "nbmort", "nbperdue", "quantitetotale"};

    PageRecherche pr = new PageRecherche(t, request, listeCrt, listeInt, 3,
                                          libEntete, libEntete.length);
    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));
    pr.setApres("a_tahina/poussin/bonreception-poussin-fiche.jsp&id=" + idMere
              + "&tab=inc/bonreception-poussin-details");
    pr.setAWhere(" and idmere = '" + idMere + "'");
    pr.setNpp(10);

    pr.creerObjetPage(libEntete, null);

    pr.getTableau().setLibelleAffiche(new String[]{
        "Id", "Produit", "Sexe", "Qté reçue", "Nb mort", "Nb perdu", "Qté totale"
    });
%>
<div class="box-body">
  <%
    if (pr.getTableau().getHtml() != null) {
        out.println(pr.getTableau().getHtml());
    } else {
  %>
      <center><h4>Aucune donnée trouvée</h4></center>
  <% } %>
</div>
<% } catch (Exception e) { e.printStackTrace(); } %>