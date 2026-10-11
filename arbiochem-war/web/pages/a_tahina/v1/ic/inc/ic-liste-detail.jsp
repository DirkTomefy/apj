<%@page import="a_tahina.ic.*"%>
<%@page import="affichage.*"%>
<%@page import="user.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    UserEJB u = (UserEJB) session.getValue("u");
    String lien   = (String) session.getValue("lien");
    String idMere = request.getParameter("idMere");

    InsertionCompFilleLib t = new InsertionCompFilleLib();
    t.setNomTable("INSERTION_COMP_FILLE_LIB");

    String[] listeCrt  = {};
    String[] listeInt  = {};
    String[] libEntete = { "compartimentlib", "qte", "pu",
                           "sexelib", "santelib" };

    PageRecherche pr = new PageRecherche(t, request, listeCrt, listeInt, 3,
                                          libEntete, libEntete.length);
    pr.setUtilisateur(u);
    pr.setLien(lien);
    pr.setApres("a_tahina/v1/ic/insertioncompartiment-fiche.jsp&id=" + idMere
              + "&tab=inc/ic-liste-detail");
    pr.setAWhere(" and idmere = '" + idMere + "'");
    pr.setNpp(10);

    pr.creerObjetPage(libEntete, null);

    pr.getTableau().setLibelleAffiche(new String[]{
        "Compartiment", "Qt&eacute;", "P.U.", "Sexe", "Sant&eacute;"
    });

    if (pr.getTableau().getHtml() != null) {
        out.println(pr.getTableau().getHtml());
    } else {
        out.println("<center><h4>Aucune ligne trouv&eacute;e</h4></center>");
    }
} catch (Exception e) {
    out.println("<pre style='color:red'>");
    e.printStackTrace(new java.io.PrintWriter(out));
    out.println("</pre>");
} %>