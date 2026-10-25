<%@page import="a_tahina.page.AtahinaUtil"%>
<%@page import="a_tahina.page.AtahinaPageRecherche"%>
<%@page import="a_tahina.facture.AthFactureLib"%>
<%@page import="a_tahina.fronthelper.facture.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    String idMere = request.getParameter("idMere");
    String[] libEntete = {"id", "daty", "reference", "montantttc", "montantavoir", "netapayer", "etatlib"};

    AtahinaPageRecherche pr = new AtahinaPageRecherche(new AthFactureLib(), request, session,
            ClientFH.PAGE_FICHE + "&id=" + AtahinaUtil.url(idMere) + "&tab=inc/client-factures",
            "", new String[]{}, new String[]{}, libEntete);
    pr.filtreEgal("idclient", idMere);
    pr.setNpp(10);
    pr.construire("Id", "Date", "R&eacute;f&eacute;rence", "Total TTC", "Avoir d&eacute;duit", "Net &agrave; payer", "&Eacute;tat");
    pr.lienFiche("id", FactureFH.PAGE_FICHE);
    out.println(pr.getHtmlOnglet("Aucune facture"));
} catch (Exception e) {
    out.println(AtahinaUtil.erreurHtml(e));
} %>
