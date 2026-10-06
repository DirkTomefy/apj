<%@page import="affichage.PageInsert"%>
<%@page import="atahina.Compartiment"%>
<%
try {
    user.UserEJB u = (user.UserEJB) session.getValue("u");

    Compartiment t = new Compartiment();
    PageInsert p = new PageInsert(t, request);

    // Libellés
    p.getFormu().getChamp("libelle").setLibelle("Libellé");
    p.getFormu().getChamp("capacite").setLibelle("Capacité");
%>
<div class="content-wrapper">
    <h1>Saisie d'un compartiment</h1>

    <form action="<%= p.getLien() %>?but=apresTarif.jsp" method="post">

        <%-- ✅ la bonne méthode --%>
        <% out.println(p.getFormu().getHtmlInsert()); %>

        <input type="hidden" name="acte"     value="insert" />
        <input type="hidden" name="bute"     value="a_tahina/compartiment-fiche.jsp" />
        <input type="hidden" name="classe"   value="atahina.Compartiment" />
        <input type="hidden" name="nomtable" value="COMPARTIMENT" />
    </form>
</div>
<%
} catch (Exception e) {
    e.printStackTrace();
%>
    <script>alert('<%= e.getMessage() %>'); history.back();</script>
<%
}
%>