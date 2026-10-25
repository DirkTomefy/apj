<%@page import="a_tahina.page.AtahinaUtil"%>
<%@page import="a_tahina.page.AtahinaPageRecherche"%>
<%@page import="a_tahina.facture.AthAvoirLib"%>
<%@page import="a_tahina.fronthelper.facture.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    String idMere = request.getParameter("idMere");
    String[] libEntete = {"id", "daty", "idfacturelib", "montantttc", "montantutilise", "reste", "etatlib"};

    AtahinaPageRecherche pr = new AtahinaPageRecherche(new AthAvoirLib(), request, session,
            ClientFH.PAGE_FICHE + "&id=" + AtahinaUtil.url(idMere) + "&tab=inc/client-avoirs",
            "", new String[]{}, new String[]{}, libEntete);
    pr.filtreEgal("idclient", idMere);
    pr.setNpp(10);
    pr.construire("Id", "Date", "Facture", "Montant TTC", "D&eacute;j&agrave; utilis&eacute;", "Reste", "&Eacute;tat");
    pr.lienFiche("id", AvoirFH.PAGE_FICHE);
    out.println(pr.getHtmlOnglet("Aucun avoir"));
} catch (Exception e) {
    out.println(AtahinaUtil.erreurHtml(e));
} %>
