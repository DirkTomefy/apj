<%@page import="a_tahina.page.AtahinaUtil"%>
<%@page import="a_tahina.page.AtahinaPageRecherche"%>
<%@page import="a_tahina.br.BonReceptionPoussinLib"%>
<%@page import="a_tahina.fronthelper.br.BRFicheFH"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    String idMere = request.getParameter("idMere");

    String[] libEntete = {"id", "idboncommandelib", "idlieureceptionlib", "qtApresCumule", "maty", "very"};

    AtahinaPageRecherche pr = new AtahinaPageRecherche(new BonReceptionPoussinLib(), request, session,
            BRFicheFH.PAGE_ACTUEL + "&id=" + AtahinaUtil.url(idMere) + "&tab=inc/br-liste-detail",
            "", new String[]{"id"}, new String[]{}, libEntete);
    pr.filtreEgal("id", idMere);
    pr.setNpp(10);
    pr.construire("Id BR", "Bon de commande", "Lieu", "Qt&eacute; re&ccedil;ue", "Maty", "Very");
%>
<div class="box-body">
  <% out.println(pr.getHtmlOnglet("Aucune donn&eacute;e trouv&eacute;e")); %>
</div>
<% } catch (Exception e) {
    out.println(AtahinaUtil.erreurHtml(e));
} %>
