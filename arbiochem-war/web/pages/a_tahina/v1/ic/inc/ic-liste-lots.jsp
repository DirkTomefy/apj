<%@page import="a_tahina.page.AtahinaUtil"%>
<%@page import="a_tahina.page.AtahinaPageRecherche"%>
<%@page import="a_tahina.prevision.LotLib"%>
<%@page import="a_tahina.fronthelper.ic.ICFicheFH"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    String idMere = request.getParameter("idMere");

    String[] libEntete = {"id", "produitlib", "souchelibr", "qteinit", "pu",
                          "qteActuelle", "sexelib", "santelib", "compartimentlib"};

    AtahinaPageRecherche pr = new AtahinaPageRecherche(new LotLib(), request, session,
            ICFicheFH.PAGE_ACTUEL + "&id=" + AtahinaUtil.url(idMere) + "&tab=inc/ic-liste-lots",
            "", new String[]{}, new String[]{}, libEntete);
    // lots issus des lignes de cette insertion
    pr.filtre(AtahinaUtil.dansSelect("idinsertcompfille", "INSERTION_COMPARTIMENT_FILLE", "id", "idmere", idMere));
    pr.setNpp(20);
    pr.construire("Id", "Produit", "Souche", "Qt&eacute;", "P.U.",
                  "Qt&eacute; actuelle", "Sexe", "Sant&eacute;", "Compartiment");

    out.println(pr.getHtmlOnglet("Aucun lot cr&eacute;&eacute;"));
} catch (Exception e) {
    out.println(AtahinaUtil.erreurHtml(e));
} %>
