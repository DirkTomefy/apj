<%@page import="affichage.PageInsertMultiple"%>
<%@page import="affichage.Liste"%>
<%@page import="affichage.Champ"%>
<%@page import="bean.TypeObjet"%>
<%@page import="caisse.Devise"%>
<%@page import="magasin.Magasin"%>
<%@page import="faturefournisseur.ModePaiement"%>
<%@page import="a_tahina.poussin.BonCommandePoussin"%>
<%@page import="a_tahina.poussin.BonCommandePoussinFille"%>
<% try {
    user.UserEJB u = (user.UserEJB) session.getValue("u");

    BonCommandePoussin mere = new BonCommandePoussin();
    BonCommandePoussinFille fille = new BonCommandePoussinFille();

    int nombreLigne = 10;
    PageInsertMultiple pi = new PageInsertMultiple(mere, fille, request, nombreLigne, u);
    pi.setLien((String) session.getValue("lien"));

    // ------------- EN-TÊTE -------------
    pi.getFormu().getChamp("daty").setLibelle("Date");
    pi.getFormu().getChamp("reference").setLibelle("Référence");
    pi.getFormu().getChamp("datelimite").setLibelle("Date limite");
    pi.getFormu().getChamp("idfournisseur").setLibelle("Fournisseur");
    pi.getFormu().getChamp("modepaiement").setLibelle("Mode de paiement");
    pi.getFormu().getChamp("iddevise").setLibelle("Devise");
    pi.getFormu().getChamp("idmagasin").setLibelle("Magasin");
    pi.getFormu().getChamp("remarque").setLibelle("Remarque");
    pi.getFormu().getChamp("etat").setVisible(false);
    pi.getFormu().getChamp("daty").setDefaut(utilitaire.Utilitaire.dateDuJour());

    pi.getFormu().getChamp("idfournisseur").setPageAppelComplete(
        "faturefournisseur.Fournisseur", "id", "FOURNISSEUR");
    pi.getFormu().getChamp("idfournisseur").setPageAppelInsert(
        "fournisseur/fournisseur-saisie.jsp",
        "idfournisseur;idfournisseurlibelle", "id;nom");

    Liste[] listes = new Liste[3];
    listes[0] = new Liste("iddevise", new Devise(), "val", "id");
    listes[0].setDefaut("AR");
    listes[1] = new Liste("modepaiement", new ModePaiement(), "val", "id");
    listes[2] = new Liste("idmagasin", new Magasin(), "val", "id");
    listes[2].setApresW(" and actif = 1");
    pi.getFormu().changerEnChamp(listes);

    // ------------- DÉTAIL -------------
    pi.getFormufle().getChamp("idproduit_0").setLibelle("Produit");
    pi.getFormufle().getChamp("sexe_0").setLibelle("Sexe");
    pi.getFormufle().getChamp("quantite_0").setLibelle("Quantité");
    pi.getFormufle().getChamp("pu_0").setLibelle("P.U.");
    pi.getFormufle().getChamp("remise_0").setLibelle("Remise (%)");
    pi.getFormufle().getChamp("tva_0").setLibelle("TVA (%)");

    pi.getFormufle().getChampMulitple("id").setVisible(false);
    pi.getFormufle().getChampMulitple("idmere").setVisible(false);
    pi.getFormufle().getChampMulitple("iddevise").setVisible(false);
    pi.getFormufle().getChampMulitple("montant").setVisible(false);

    Champ.setPageAppelComplete(pi.getFormufle().getChampFille("idproduit"),
        "produits.IngredientsLib", "id", "AS_INGREDIENTS_LIB",
        "pu", "pu");

    Liste[] listeSexe = new Liste[1];
    listeSexe[0] = new Liste("sexe", new TypeObjet("SEXE"), "val", "id");
    pi.getFormufle().changerEnChamp(listeSexe);

    Champ.setAutre(pi.getFormufle().getChampFille("quantite"),
                   "onchange='calcMontant()'");
    Champ.setAutre(pi.getFormufle().getChampFille("pu"),
                   "onchange='calcMontant()'");

    pi.preparerDataFormu();

    for (int i = 0; i < nombreLigne; i++) {
        pi.getFormufle().getChamp("quantite_" + i).setDefaut("0");
        pi.getFormufle().getChamp("remise_" + i).setDefaut("0");
        pi.getFormufle().getChamp("tva_" + i).setDefaut("0");
    }

    pi.getFormufle().setColOrdre(new String[]{
        "idproduit", "sexe", "quantite", "pu", "remise", "tva"
    });

    pi.getFormu().makeHtmlInsertTabIndex();
    pi.getFormufle().makeHtmlInsertTableauIndex();

    String acte = request.getParameter("acte");
    boolean isUpdate = (acte != null && acte.equalsIgnoreCase("update"));

    String classe      = "a_tahina.poussin.BonCommandePoussin";
    String classefille = "a_tahina.poussin.BonCommandePoussinFille";
    String bute        = "a_tahina/poussin/boncommande-poussin-fiche.jsp";
    String colonneMere = "idmere";
%>
<div class="content-wrapper">
  <h1><%= isUpdate ? "Modification" : "Saisie" %> d'un bon de commande poussin</h1>
  <form class="container" action="<%= pi.getLien() %>?but=apresMultiple.jsp" method="post">
    <% out.println(pi.getFormu().getHtmlInsert()); %>
    <div class="row col-md-12">
      <h3>Total : <span id="total">0</span></h3>
    </div>
    <% out.println(pi.getFormufle().getHtmlTableauInsert()); %>

    <input name="acte"        type="hidden" value="<%= isUpdate ? "updateInsert" : "insert" %>" />
    <input name="bute"        type="hidden" value="<%= bute %>" />
    <input name="classe"      type="hidden" value="<%= classe %>" />
    <input name="classefille" type="hidden" value="<%= classefille %>" />
    <input name="nombreLigne" type="hidden" value="<%= nombreLigne %>" />
    <input name="colonneMere" type="hidden" value="<%= colonneMere %>" />
    <input name="nomtable"    type="hidden" value="BONCOMMANDE_POUSSIN_FILLE" />
  </form>
</div>
<script>
function calcMontant() {
    let total = 0;
    document.querySelectorAll('input[id^="pu_"]').forEach((el, i) => {
        const pu  = parseFloat(document.getElementById('pu_'+i)?.value) || 0;
        const qte = parseFloat(document.getElementById('quantite_'+i)?.value) || 0;
        total += pu * qte;
    });
    const el = document.getElementById('total');
    if (el) el.textContent = total.toLocaleString('fr-FR');
}
document.addEventListener('DOMContentLoaded', calcMontant);
</script>
<% } catch (Exception e) { e.printStackTrace(); } %>