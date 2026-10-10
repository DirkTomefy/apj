<%@page import="a_tahina.br.*"%>
<%@page import="affichage.*"%>
<%@page import="user.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
  try {
    UserEJB u = (UserEJB) session.getValue("u");
    String lien   = (String) session.getValue("lien");
    String idMere = request.getParameter("idMere");

    BonReceptionPoussinLib t = new BonReceptionPoussinLib();
    t.setNomTable("BONRECEPTION_POUSSIN_LIB");

    String[] listeCrt  = { "id" };
    String[] listeInt  = {};
    String[] libEntete = { "id", "idboncommandelib", "idlieureceptionlib",
                           "qtApresCumule", "maty", "very" };

    PageRecherche pr = new PageRecherche(t, request, listeCrt, listeInt, 3,
                                          libEntete, libEntete.length);
    pr.setUtilisateur(u);
    pr.setLien(lien);
    pr.setApres("a_tahina/v1/br/bonreception-fiche.jsp&id=" + idMere
              + "&tab=inc/br-liste-detail");
    pr.setAWhere(" and id = '" + idMere + "'");
    pr.setNpp(10);

    pr.creerObjetPage(libEntete, null);

    pr.getTableau().setLibelleAffiche(new String[]{
        "Id BR", "Bon de commande", "Lieu",
        "Qt&eacute; re&ccedil;ue", "Maty", "Very"
    });
%>
<div class="box-body">
  <%
    if (pr.getTableau().getHtml() != null) {
        out.println(pr.getTableau().getHtml());
    } else {
  %>
      <center><h4>Aucune donn&eacute;e trouv&eacute;e</h4></center>
  <% } %>
</div>
<% } catch (Exception e) {
     out.println("<pre style='color:red'>");
     e.printStackTrace(new java.io.PrintWriter(out));
     out.println("</pre>");
   } %>