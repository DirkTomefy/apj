<%@page import="a_tahina.page.AtahinaUtil"%>
<%@page import="a_tahina.page.AtahinaPageRecherche"%>
<%@page import="a_tahina.facture.AthAvoirUsage"%>
<%@page import="a_tahina.fronthelper.facture.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    String idMere = request.getParameter("idMere");
    String[] libEntete = {"idavoir", "montant", "daty"};

    AtahinaPageRecherche pr = new AtahinaPageRecherche(new AthAvoirUsage(), request, session,
            FactureFH.PAGE_FICHE + "&id=" + AtahinaUtil.url(idMere) + "&tab=inc/facture-usages",
            "", new String[]{}, new String[]{}, libEntete);
    pr.filtreEgal("idfacture", idMere);
    pr.setNpp(10);
    pr.construire("Avoir", "Montant d&eacute;duit", "Date");
    pr.lienFiche("idavoir", AvoirFH.PAGE_FICHE, "id");
    out.println(pr.getHtmlOnglet("Aucun avoir d&eacute;duit sur cette facture"));
} catch (Exception e) {
    out.println(AtahinaUtil.erreurHtml(e));
} %>
