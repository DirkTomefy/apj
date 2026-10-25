<%@page import="a_tahina.page.AtahinaUtil"%>
<%@page import="a_tahina.page.AtahinaPageRecherche"%>
<%@page import="a_tahina.bc.BonCommandePoussinFilleLib"%>
<%@page import="a_tahina.fronthelper.bc.BCmdFicheFH"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    String idMere = request.getParameter("idMere");
    if (idMere == null) idMere = request.getParameter("id");

    String[] libEntete = {"idproduitlib", "souchelibr", "qte", "pu", "montant", "fournisseurlib"};

    AtahinaPageRecherche pr = new AtahinaPageRecherche(new BonCommandePoussinFilleLib(), request, session,
            BCmdFicheFH.PAGE_ACTUEL + "&id=" + AtahinaUtil.url(idMere) + "&tab=inc/bc-liste-detail",
            "", new String[]{}, new String[]{}, libEntete);
    pr.filtreEgal("idmere", idMere);
    pr.setNpp(10);
    pr.construire("Produit", "Souche", "Qt&eacute;", "P.U.", "Montant", "Fournisseur");
%>
<div class="box-body">
  <% out.println(pr.getHtmlOnglet("Aucune ligne trouv&eacute;e")); %>
</div>
<% } catch (Exception e) {
    out.println(AtahinaUtil.erreurHtml(e));
} %>
