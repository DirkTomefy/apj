<%@page import="affichage.PageInsert"%>
<%@page import="a_tahina.configuration.LieuReception"%>
<% try {
    user.UserEJB u = (user.UserEJB) session.getValue("u");

    LieuReception s = new LieuReception();
    PageInsert pi = new PageInsert(s, request, u);
    pi.setLien((String) session.getValue("lien"));

    String titre = "Création d'un lieu de réception";
    if (request.getParameter("acte") != null && request.getParameter("acte").equals("update"))
        titre = "Modification d'un lieu de réception";
    pi.setTitre(titre);

    pi.getFormu().getChamp("val").setLibelle("Lieu");
    pi.getFormu().getChamp("desce").setLibelle("Description");

    pi.preparerDataFormu();
    pi.getFormu().makeHtmlInsertTabIndex();

    String classe = "a_tahina.configuration.LieuReception";
    String apres  = "a_tahina/configuration/lieureception-fiche.jsp";
%>
<div class="content-wrapper">
  <h1><%= pi.getTitre() %></h1>
  <form action="<%= pi.getLien() %>?but=apresTarif.jsp" method="post"
        name="LIEURECEPTION" id="LIEURECEPTION">
    <% out.println(pi.getFormu().getHtmlInsert());
       out.println(pi.getHtmlAddOnPopup()); %>

    <input name="acte"     type="hidden" value="insert" />
    <input name="bute"     type="hidden" value="<%= apres %>" />
    <input name="classe"   type="hidden" value="<%= classe %>" />
    <input name="nomtable" type="hidden" value="LIEURECEPTION" />
  </form>
</div>
<% } catch (Exception e) { e.printStackTrace(); } %>