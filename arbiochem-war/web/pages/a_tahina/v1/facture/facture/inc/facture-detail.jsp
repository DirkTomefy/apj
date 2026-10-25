<%@page import="a_tahina.page.AtahinaUtil"%>
<%@page import="a_tahina.page.AtahinaPageRecherche"%>
<%@page import="a_tahina.facture.AthFactureFilleLib"%>
<%@page import="a_tahina.fronthelper.facture.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    String idMere = request.getParameter("idMere");
    String[] libEntete = {"produitlib", "qte", "pu", "tva", "montantht", "montantttc", "qteretournee", "qtedisponible"};

    AtahinaPageRecherche pr = new AtahinaPageRecherche(new AthFactureFilleLib(), request, session,
            FactureFH.PAGE_FICHE + "&id=" + AtahinaUtil.url(idMere) + "&tab=inc/facture-detail",
            "", new String[]{}, new String[]{}, libEntete);
    pr.filtreEgal("idmere", idMere);
    pr.setNpp(10);
    pr.construire("Produit", "Qt&eacute;", "P.U.", "TVA %", "Total HT", "Total TTC", "Qt&eacute; retourn&eacute;e", "Encore retournable");
    out.println(pr.getHtmlOnglet("Aucune ligne"));
} catch (Exception e) {
    out.println(AtahinaUtil.erreurHtml(e));
} %>
