<%@page import="a_tahina.page.AtahinaUtil"%>
<%@page import="a_tahina.page.AtahinaPageRecherche"%>
<%@page import="a_tahina.ic.InsertionCompFilleLib"%>
<%@page import="a_tahina.fronthelper.ic.ICFicheFH"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    String idMere = request.getParameter("idMere");

    String[] libEntete = {"compartimentlib", "qte", "pu", "sexelib", "santelib"};

    AtahinaPageRecherche pr = new AtahinaPageRecherche(new InsertionCompFilleLib(), request, session,
            ICFicheFH.PAGE_ACTUEL + "&id=" + AtahinaUtil.url(idMere) + "&tab=inc/ic-liste-detail",
            "", new String[]{}, new String[]{}, libEntete);
    pr.filtreEgal("idmere", idMere);
    pr.setNpp(10);
    pr.construire("Compartiment", "Qt&eacute;", "P.U.", "Sexe", "Sant&eacute;");

    out.println(pr.getHtmlOnglet("Aucune ligne trouv&eacute;e"));
} catch (Exception e) {
    out.println(AtahinaUtil.erreurHtml(e));
} %>
