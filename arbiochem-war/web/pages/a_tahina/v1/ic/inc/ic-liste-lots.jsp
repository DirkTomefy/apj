<%@page import="a_tahina.lot.*"%>
<%@page import="a_tahina.ic.*"%>
<%@page import="affichage.*"%>
<%@page import="user.*"%>
<%@page import="bean.*"%>
<%@page import="java.sql.Connection"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    UserEJB u = (UserEJB) session.getValue("u");
    String lien   = (String) session.getValue("lien");
    String idMere = request.getParameter("idMere");

    // Récupérer les filles pour filtrer les lots
    Connection c = new utilitaire.UtilDB().GetConn();
    InsertionCompartimentFille crit = new InsertionCompartimentFille();
    InsertionCompartimentFille[] filles =
        (InsertionCompartimentFille[]) CGenUtil.rechercher(
            crit, null, null, c, " AND idmere = '" + idMere + "'");
    c.close();

    StringBuilder in = new StringBuilder();
    for (int i = 0; i < filles.length; i++) {
        if (i > 0) in.append(",");
        in.append("'").append(filles[i].getId()).append("'");
    }

    LotLib t = new LotLib();
    t.setNomTable("LOT_LIB");

    String[] listeCrt  = {};
    String[] listeInt  = {};
    String[] libEntete = { "id", "produitlib", "souchelibr", "qte",
                           "pu", "qteActuelle", "sexelib", "santelib",
                           "compartimentlib" };

    PageRecherche pr = new PageRecherche(t, request, listeCrt, listeInt, 3,
                                          libEntete, libEntete.length);
    pr.setUtilisateur(u);
    pr.setLien(lien);
    pr.setApres("a_tahina/v1/ic/insertioncompartiment-fiche.jsp&id=" + idMere
              + "&tab=inc/ic-liste-lots");

    if (in.length() > 0) {
        pr.setAWhere(" and idinsertcompfille in (" + in + ")");
    } else {
        pr.setAWhere(" and 1<0");
    }
    pr.setNpp(20);

    pr.creerObjetPage(libEntete, null);

    pr.getTableau().setLibelleAffiche(new String[]{
        "Id", "Produit", "Souche", "Qt&eacute;", "P.U.",
        "Qt&eacute; actuelle", "Sexe", "Sant&eacute;", "Compartiment"
    });

    if (pr.getTableau().getHtml() != null) {
        out.println(pr.getTableau().getHtml());
    } else {
        out.println("<center><h4>Aucun lot cr&eacute;&eacute;</h4></center>");
    }
} catch (Exception e) {
    out.println("<pre style='color:red'>");
    e.printStackTrace(new java.io.PrintWriter(out));
    out.println("</pre>");
} %>