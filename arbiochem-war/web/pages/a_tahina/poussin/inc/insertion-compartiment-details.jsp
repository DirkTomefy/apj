<%@page import="affichage.PageRecherche"%>
<%@page import="a_tahina.poussin.InsertionCompartimentFilleLib"%>
<% try {
    String idMere = request.getParameter("idMere");
    if (idMere == null) idMere = request.getParameter("id");

    InsertionCompartimentFilleLib t = new InsertionCompartimentFilleLib();
    String[] listeCrt  = {};
    String[] listeInt  = {};
    String[] libEntete = {"id", "idbrcfilleorigine", "compartimentlib",
                          "quantitefinale", "sexelib", "sante", "santelib", "est_finallib"};

    PageRecherche pr = new PageRecherche(t, request, listeCrt, listeInt, 3,
                                          libEntete, libEntete.length);
    pr.setUtilisateur((user.UserEJB) session.getValue("u"));
    pr.setLien((String) session.getValue("lien"));
    pr.setApres("a_tahina/poussin/insertion-compartiment-fiche.jsp&id=" + idMere
              + "&tab=inc/insertion-compartiment-details");
    pr.setAWhere(" and idmere = '" + idMere + "'");
    pr.setNpp(10);

    pr.creerObjetPage(libEntete, null);

    pr.getTableau().setLibelleAffiche(new String[]{
        "Id", "Ligne BR origine", "Compartiment", "Qté finale",
        "Sexe", "Santé", "État santé", "Final"
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