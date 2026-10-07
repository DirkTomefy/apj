<%@page import="affichage.PageInsertMultiple"%>
<%@page import="affichage.Liste"%>
<%@page import="affichage.Champ"%>
<%@page import="bean.TypeObjet"%>
<%@page import="a_tahina.configuration.CompartimentLib"%>
<%@page import="a_tahina.configuration.LieuReception"%>
<%@page import="a_tahina.poussin.InsertionCompartiment"%>
<%@page import="a_tahina.poussin.InsertionCompartimentFille"%>
<%@page import="a_tahina.poussin.BonReceptionPoussinLib"%>
<% try {
    user.UserEJB u = (user.UserEJB) session.getValue("u");

    InsertionCompartiment mere = new InsertionCompartiment();
    InsertionCompartimentFille fille = new InsertionCompartimentFille();

    int nombreLigne = 10;
    PageInsertMultiple pi = new PageInsertMultiple(mere, fille, request, nombreLigne, u);
    pi.setLien((String) session.getValue("lien"));

    // ------------- EN-TÊTE -------------
    pi.getFormu().getChamp("daty").setLibelle("Date");
    pi.getFormu().getChamp("reference").setLibelle("Référence");
    pi.getFormu().getChamp("idlieu").setLibelle("Lieu");
    pi.getFormu().getChamp("idbonreception").setLibelle("Bon de réception");
    pi.getFormu().getChamp("remarque").setLibelle("Remarque");
    pi.getFormu().getChamp("etat").setVisible(false);
    pi.getFormu().getChamp("daty").setDefaut(utilitaire.Utilitaire.dateDuJour());

    Liste[] listes = new Liste[2];
    listes[0] = new Liste("idlieu", new LieuReception(), "val", "id");
    listes[1] = new Liste("idbonreception", new BonReceptionPoussinLib(), "reference", "id");
    pi.getFormu().changerEnChamp(listes);

    String idBr = request.getParameter("idbonreception");
    if (idBr != null && !idBr.isEmpty()) {
        pi.getFormu().getChamp("idbonreception").setDefaut(idBr);
    }

    // ------------- DÉTAIL -------------
    pi.getFormufle().getChamp("idbrcfilleorigine_0").setLibelle("Ligne BR");
    pi.getFormufle().getChamp("idcompartiment_0").setLibelle("Compartiment");
    pi.getFormufle().getChamp("quantitefinale_0").setLibelle("Qté finale");
    pi.getFormufle().getChamp("sexe_0").setLibelle("Sexe");
    pi.getFormufle().getChamp("sante_0").setLibelle("Santé (1-10)");
    pi.getFormufle().getChamp("est_final_0").setLibelle("Final");

    pi.getFormufle().getChampMulitple("id").setVisible(false);
    pi.getFormufle().getChampMulitple("idmere").setVisible(false);
    pi.getFormufle().getChampMulitple("idbcfilleorigine").setVisible(false);
    pi.getFormufle().getChampMulitple("idlot").setVisible(false);

    Liste[] listeCmp = new Liste[1];
    listeCmp[0] = new Liste("idcompartiment", new CompartimentLib(), "libelle", "id");
    pi.getFormufle().changerEnChamp(listeCmp);

    Liste[] listeSexe = new Liste[1];
    listeSexe[0] = new Liste("sexe", new TypeObjet("SEXE"), "val", "id");
    pi.getFormufle().changerEnChamp(listeSexe);

    pi.preparerDataFormu();

    for (int i = 0; i < nombreLigne; i++) {
        pi.getFormufle().getChamp("sante_" + i).setDefaut("10");
        pi.getFormufle().getChamp("est_final_" + i).setDefaut("0");
    }

    pi.getFormufle().setColOrdre(new String[]{
        "idbrcfilleorigine", "idcompartiment", "quantitefinale", "sexe", "sante", "est_final"
    });

    pi.getFormu().makeHtmlInsertTabIndex();
    pi.getFormufle().makeHtmlInsertTableauIndex();

    String acte = request.getParameter("acte");
    boolean isUpdate = (acte != null && acte.equalsIgnoreCase("update"));

    String classe      = "a_tahina.poussin.InsertionCompartiment";
    String classefille = "a_tahina.poussin.InsertionCompartimentFille";
    String bute        = "a_tahina/poussin/insertion-compartiment-fiche.jsp";
    String colonneMere = "idmere";
%>
<div class="content-wrapper">
  <h1><%= isUpdate ? "Modification" : "Saisie" %> d'une insertion en compartiment</h1>
  <form class="container" action="<%= pi.getLien() %>?but=apresMultiple.jsp" method="post">
    <% out.println(pi.getFormu().getHtmlInsert()); %>
    <% out.println(pi.getFormufle().getHtmlTableauInsert()); %>

    <input name="acte"        type="hidden" value="<%= isUpdate ? "updateInsert" : "insert" %>" />
    <input name="bute"        type="hidden" value="<%= bute %>" />
    <input name="classe"      type="hidden" value="<%= classe %>" />
    <input name="classefille" type="hidden" value="<%= classefille %>" />
    <input name="nombreLigne" type="hidden" value="<%= nombreLigne %>" />
    <input name="colonneMere" type="hidden" value="<%= colonneMere %>" />
    <input name="nomtable"    type="hidden" value="INSERTION_COMPARTIMENT_FILLE" />
  </form>
</div>
<% } catch (Exception e) { e.printStackTrace(); } %>