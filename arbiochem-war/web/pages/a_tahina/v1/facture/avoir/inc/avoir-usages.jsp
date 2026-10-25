<%@page import="a_tahina.page.AtahinaUtil"%>
<%@page import="a_tahina.page.AtahinaPageRecherche"%>
<%@page import="a_tahina.facture.AthAvoirUsage"%>
<%@page import="a_tahina.fronthelper.facture.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    String idMere = request.getParameter("idMere");
    String[] libEntete = {"idfacture", "montant", "daty"};

    AtahinaPageRecherche pr = new AtahinaPageRecherche(new AthAvoirUsage(), request, session,
            AvoirFH.PAGE_FICHE + "&id=" + AtahinaUtil.url(idMere) + "&tab=inc/avoir-usages",
            "", new String[]{}, new String[]{}, libEntete);
    pr.filtreEgal("idavoir", idMere);
    pr.setNpp(10);
    pr.construire("Facture", "Montant d&eacute;duit", "Date");
    pr.lienFiche("idfacture", FactureFH.PAGE_FICHE, "id");
    out.println(pr.getHtmlOnglet("Cet avoir n'a pas encore &eacute;t&eacute; utilis&eacute;"));
} catch (Exception e) {
    out.println(AtahinaUtil.erreurHtml(e));
} %>
