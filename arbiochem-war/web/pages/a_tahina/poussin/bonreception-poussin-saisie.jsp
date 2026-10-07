<%@page import="affichage.PageInsertMultiple"%>
<%@page import="affichage.Liste"%>
<%@page import="affichage.Champ"%>
<%@page import="bean.TypeObjet"%>
<%@page import="a_tahina.configuration.LieuReception"%>
<%@page import="a_tahina.poussin.BonReceptionPoussin"%>
<%@page import="a_tahina.poussin.BonReceptionPoussinFille"%>
<%@page import="a_tahina.poussin.BonReceptionPoussinLib"%>
<%@page import="a_tahina.poussin.BonCommandePoussinLib"%>
<% try {
    user.UserEJB u = (user.UserEJB) session.getValue("u");

    BonReceptionPoussin mere = new BonReceptionPoussin();
    BonReceptionPoussinFille fille = new BonReceptionPoussinFille();

    int nombreLigne = 10;
    PageInsertMultiple pi = new PageInsertMultiple(mere, fille, request, nombreLigne, u);
    pi.setLien((String) session.getValue("lien"));

    // ------------- EN-TÊTE -------------
    pi.getFormu().getChamp("daty").setLibelle("Date");
    pi.getFormu().getChamp("reference").setLibelle("Référence");
    pi.getFormu().getChamp("idlieureception").setLibelle("Lieu de réception");
    pi.getFormu().getChamp("idboncommandepoussin").setLibelle("Bon de commande");
    pi.getFormu().getChamp("idbonreceptionprecedent").setLibelle("BR antécédent");
    pi.getFormu().getChamp("fournisseur").setLibelle("Fournisseur");
    pi.getFormu().getChamp("remarque").setLibelle("Remarque");
    pi.getFormu().getChamp("est_final").setLibelle("Final");
    pi.getFormu().getChamp("etat").setVisible(false);
    pi.getFormu().getChamp("daty").setDefaut(utilitaire.Utilitaire.dateDuJour());

    pi.getFormu().getChamp("fournisseur").setPageAppelComplete(
        "faturefournisseur.Fournisseur", "id", "FOURNISSEUR");

    Liste[] listes = new Liste[3];
    listes[0] = new Liste("idlieureception", new LieuReception(), "val", "id");
    listes[1] = new Liste("idboncommandepoussin", new BonCommandePoussinLib(), "reference", "id");
    listes[1].setApresW(" and etat = 11");
    listes[2] = new Liste("idbonreceptionprecedent", new BonReceptionPoussinLib(), "reference", "id");
    pi.getFormu().changerEnChamp(listes);

    String idBc = request.getParameter("idboncommandepoussin");
    if (idBc != null && !idBc.isEmpty()) {
        pi.getFormu().getChamp("idboncommandepoussin").setDefaut(idBc);
    }
    String idBrp = request.getParameter("idbonreceptionprecedent");
    if (idBrp != null && !idBrp.isEmpty()) {
        pi.getFormu().getChamp("idbonreceptionprecedent").setDefaut(idBrp);
    }

    // ------------- DÉTAIL -------------
    pi.getFormufle().getChamp("idproduit_0").setLibelle("Produit");
    pi.getFormufle().getChamp("sexe_0").setLibelle("Sexe");
    pi.getFormufle().getChamp("quantite_0").setLibelle("Qté reçue");
    pi.getFormufle().getChamp("nbmort_0").setLibelle("Nb mort");
    pi.getFormufle().getChamp("nbperdue_0").setLibelle("Nb perdu");

    pi.getFormufle().getChampMulitple("id").setVisible(false);
    pi.getFormufle().getChampMulitple("idmere").setVisible(false);
    pi.getFormufle().getChampMulitple("pu").setVisible(false);
    pi.getFormufle().getChampMulitple("iddevise").setVisible(false);
    pi.getFormufle().getChampMulitple("tauxdechange").setVisible(false);

    Champ.setPageAppelComplete(pi.getFormufle().getChampFille("idproduit"),
        "produits.IngredientsLib", "id", "AS_INGREDIENTS_LIB", "", "");

    Liste[] listeSexe = new Liste[1];
    listeSexe[0] = new Liste("sexe", new TypeObjet("SEXE"), "val", "id");
    pi.getFormufle().changerEnChamp(listeSexe);

    pi.preparerDataFormu();

    for (int i = 0; i < nombreLigne; i++) {
        pi.getFormufle().getChamp("quantite_" + i).setDefaut("0");
        pi.getFormufle().getChamp("nbmort_" + i).setDefaut("0");
        pi.getFormufle().getChamp("nbperdue_" + i).setDefaut("0");
    }

    pi.getFormufle().setColOrdre(new String[]{
        "idproduit", "sexe", "quantite", "nbmort", "nbperdue"
    });

    pi.getFormu().makeHtmlInsertTabIndex();
    pi.getFormufle().makeHtmlInsertTableauIndex();

    String acte = request.getParameter("acte");
    boolean isUpdate = (acte != null && acte.equalsIgnoreCase("update"));

    String classe      = "a_tahina.poussin.BonReceptionPoussin";
    String classefille = "a_tahina.poussin.BonReceptionPoussinFille";
    String bute        = "a_tahina/poussin/bonreception-poussin-fiche.jsp";
    String colonneMere = "idmere";
%>
<div class="content-wrapper">
  <h1><%= isUpdate ? "Modification" : "Saisie" %> d'un bon de réception poussin</h1>
  <form class="container" action="<%= pi.getLien() %>?but=apresMultiple.jsp" method="post">
    <% out.println(pi.getFormu().getHtmlInsert()); %>
    <% out.println(pi.getFormufle().getHtmlTableauInsert()); %>

    <input name="acte"        type="hidden" value="<%= isUpdate ? "updateInsert" : "insert" %>" />
    <input name="bute"        type="hidden" value="<%= bute %>" />
    <input name="classe"      type="hidden" value="<%= classe %>" />
    <input name="classefille" type="hidden" value="<%= classefille %>" />
    <input name="nombreLigne" type="hidden" value="<%= nombreLigne %>" />
    <input name="colonneMere" type="hidden" value="<%= colonneMere %>" />
    <input name="nomtable"    type="hidden" value="BONRECEPTION_POUSSIN_FILLE" />
  </form>
</div>
<% } catch (Exception e) { e.printStackTrace(); } %>