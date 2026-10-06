# 06 — Créer un écran SAISIE maître/détail

**Fichier** : `pages/monpackage/monobjet-saisie.jsp`

```jsp
<%@page import="affichage.PageInsertMultiple"%>
<%@page import="affichage.Liste"%>
<%@page import="monpackage.MonObjet"%>
<%@page import="monpackage.MonObjetFille"%>
<% try {
    user.UserEJB u = (user.UserEJB) session.getValue("u");

    // 1) Objets
    MonObjet      mere  = new MonObjet();
    MonObjetFille fille = new MonObjetFille();
    fille.setNomTable("MONOBJET_FILLE_CPL"); // vue avec libellés

    int nombreLigne = 10;

    // 2) PageInsertMultiple(mere, fille, request, nbLignes, u)
    PageInsertMultiple pi = new PageInsertMultiple(mere, fille, request,
                                                    nombreLigne, u);
    pi.setLien((String) session.getValue("lien"));

    // 3) Champs « Liste » de l'en-tête
    Liste[] liste = new Liste[1];
    liste[0] = new Liste("idDevise", new caisse.Devise(), "val", "id");
    liste[0].setDefaut("AR");
    pi.getFormu().changerEnChamp(liste);

    // 4) Libellés en-tête
    pi.getFormu().getChamp("reference").setLibelle("Référence");
    pi.getFormu().getChamp("daty").setLibelle("Date");
    pi.getFormu().getChamp("etat").setVisible(false); // géré côté serveur

    // 5) Autocomplete en-tête
    pi.getFormu().getChamp("fournisseur").setPageAppelComplete(
        "faturefournisseur.Fournisseur", "id", "Fournisseur",
        "taxe", "taxe");
    pi.getFormu().getChamp("fournisseur").setPageAppelInsert(
        "fournisseur/fournisseur-saisie.jsp",
        "fournisseur;fournisseurlibelle", "id;nom");

    // 6) Autocomplete sur toutes les lignes de détail
    Champ.setPageAppelComplete(pi.getFormufle().getChampFille("produit"),
        "produits.IngredientsLib", "id", "AS_INGREDIENTS_LIB_ACHAT",
        "pu;taux;compte_achat;compte_achat;libelle",
        "pu;tauxDeChange;compte;comptelibelle;produitLib");
    Champ.setPageAppelInsert(pi.getFormufle().getChampFille("produit"),
        "produits/as-ingredients-saisie.jsp", "id;libelle");

    // 7) Libellés et visibilités des colonnes de détail
    pi.getFormufle().getChamp("produit_0").setLibelle("Produit");
    pi.getFormufle().getChamp("quantite_0").setLibelle("Quantité");
    pi.getFormufle().getChamp("pu_0").setLibelle("P.U.");
    pi.getFormufle().getChampMulitple("id").setVisible(false);
    pi.getFormufle().getChampMulitple("idbc").setVisible(false);

    // 8) Callbacks JS sur toutes les lignes
    Champ.setAutre(pi.getFormufle().getChampFille("pu"),
                   "onchange='calculerMontantV2()'");
    Champ.setAutre(pi.getFormufle().getChampFille("quantite"),
                   "onchange='calculerMontantV2()'");

    // 9) Préparer les données (OBLIGATOIRE avant d'accéder aux champs)
    pi.preparerDataFormu();

    // 10) Défauts ligne par ligne
    for (int i = 0; i < nombreLigne; i++) {
        pi.getFormufle().getChamp("quantite_" + i).setDefaut("0");
        pi.getFormufle().getChamp("idDevise_" + i).setDefaut("AR");
    }

    // 11) Colonnes réellement sauvegardées (ordre HTML)
    pi.getFormufle().setColOrdre(new String[]{
        "produit", "produitLib", "quantite", "pu", "tva", "remise"
    });

    // 12) Génération HTML
    pi.getFormu().makeHtmlInsertTabIndex();
    pi.getFormufle().makeHtmlInsertTableauIndex();

    String classeMere   = "monpackage.MonObjet";
    String classeFille  = "monpackage.MonObjetFille";
    String butApresPost = "monpackage/monobjet-fiche.jsp";
    String colonneMere  = "idmere";
%>
<div class="content-wrapper">
  <h1>Saisie d'un objet</h1>

  <form action="<%= pi.getLien() %>?but=apresMultiple.jsp" method="post">
    <% out.println(pi.getFormu().getHtmlInsert()); %>
    <% out.println(pi.getFormufle().getHtmlTableauInsert()); %>

    <input type="hidden" name="acte"        value="insert" />
    <input type="hidden" name="bute"        value="<%= butApresPost %>" />
    <input type="hidden" name="classe"      value="<%= classeMere %>" />
    <input type="hidden" name="classefille" value="<%= classeFille %>" />
    <input type="hidden" name="nombreLigne" value="<%= nombreLigne %>" />
    <input type="hidden" name="colonneMere" value="<%= colonneMere %>" />
    <input type="hidden" name="nomtable"    value="MONOBJET_FILLE" />
  </form>
</div>
<% } catch(Exception e){ e.printStackTrace(); } %>
```

## Champs cachés obligatoires (maître/détail)

| Nom | Rôle |
|---|---|
| `acte` | `insert`, `updateInsert`, `insertFille`, `deleteFille`, … |
| `bute` | Page de retour |
| `classe` | Classe Java de la **mère** |
| `classefille` | Classe Java de la **fille** |
| `nombreLigne` | Nb de lignes du tableau |
| `colonneMere` | FK dans la fille (ex: `idbc`) |
| `nomtable` | (Optionnel) Table de la fille |

## Actions (`apresMultiple.jsp`)

| `acte` | Effet |
|---|---|
| `insert` | Insère mère + filles |
| `insertFille` | Insère uniquement les filles (`idMere` requis) |
| `insertFilleSeul` | Insère les filles sans mère |
| `updateInsert` | Met à jour mère + filles |
| `deleteFille` | Supprime les filles cochées |
| `interview` | Passe à l'état interviewé |
| `insertSansControle` | Idem `insert` sans `controler()` |
| `updateSansControle` | Idem `updateInsert` sans `controler()` |
| `delete` | Supprime la mère + filles |
