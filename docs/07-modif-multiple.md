# 07 — Créer un écran MODIF maître/détail

**Fichier** : `pages/monpackage/monobjet-modif.jsp`

```jsp
<%@page import="bean.CGenUtil"%>
<%@page import="affichage.PageUpdateMultiple"%>
<%@page import="affichage.Liste"%>
<%@page import="monpackage.MonObjet"%>
<%@page import="monpackage.MonObjetFille"%>
<% try {
    user.UserEJB u = (user.UserEJB) session.getValue("u");

    // 1) Objets
    MonObjet      mere  = new MonObjet();
    MonObjetFille fille = new MonObjetFille();
    fille.setNomTable("MONOBJET_FILLE");
    fille.setIdbc(request.getParameter("id")); // FK

    // 2) Charger les filles existantes
    MonObjetFille[] listef = (MonObjetFille[])
        CGenUtil.rechercher(fille, null, null, "");

    // 3) PageUpdateMultiple(mere, fille, lignesExistantes, request, u, nbNouvellesLignes)
    PageUpdateMultiple pi = new PageUpdateMultiple(
        mere, fille, listef, request, u, 2);
    pi.setLien((String) session.getValue("lien"));

    // 4) Personnalisation (identique à PageInsertMultiple)
    Liste[] liste = new Liste[1];
    liste[0] = new Liste("modepaiement", new ModePaiement(), "val", "id");
    pi.getFormu().changerEnChamp(liste);

    pi.getFormu().getChamp("fournisseur").setPageAppelComplete(
        "faturefournisseur.Fournisseur", "id", "FOURNISSEUR");
    pi.getFormu().getChamp("fournisseur").setAutre("readonly");
    pi.getFormu().getChamp("etat").setVisible(false);

    // 5) Lignes existantes : champs en lecture seule
    for (int i = 0; i < pi.getNombreLigne(); i++) {
        pi.getFormufle().getChamp("id_" + i).setAutre("readonly");
    }

    // 6) Libellés des colonnes
    pi.getFormufle().getChamp("produit_0").setLibelle("Produit");
    pi.getFormufle().getChamp("quantite_0").setLibelle("Quantité");
    pi.getFormufle().getChamp("pu_0").setLibelle("P.U.");

    // 7) Préparation
    MonObjet o = (MonObjet) pi.getBase();
    pi.preparerDataFormu();
%>
<div class="content-wrapper">
  <h1>Modification</h1>

  <form action="<%= pi.getLien() %>?but=apresMultiple.jsp&id=<%= request.getParameter("id") %>"
        method="post">
    <% pi.getFormu().makeHtmlInsertTabIndex();
       pi.getFormufle().makeHtmlInsertTableauIndex();

       out.println(pi.getFormu().getHtmlInsert());
       out.println(pi.getFormufle().getHtmlTableauInsert()); %>

    <input type="hidden" name="acte"
           value="updateInsert" />
    <input type="hidden" name="bute"
           value="monpackage/monobjet-fiche.jsp?id=<%= request.getParameter("id") %>" />
    <input type="hidden" name="classe"
           value="monpackage.MonObjet" />
    <input type="hidden" name="classefille"
           value="monpackage.MonObjetFille" />
    <input type="hidden" name="nombreLigne"
           value="<%= pi.getNombreLigne() %>" />
    <input type="hidden" name="colonneMere"
           value="idmere" />
  </form>
</div>
<% } catch(Exception e){ e.printStackTrace(); } %>
```

## Différences avec `PageInsertMultiple`

| Aspect | Insert | Update |
|---|---|---|
| Classe | `PageInsertMultiple` | `PageUpdateMultiple` |
| Lignes passées | aucune | `listef` (recherche préalable) |
| `acte` | `insert` | `updateInsert` |
| Recharge | non | charge chaque ligne existante |

## Bonnes pratiques

- Mettre `readonly` sur les `id_<i>` pour empêcher la modification de PK.
- Toujours appeler `preparerDataFormu()` avant d'accéder aux champs `_0`.
- Le paramètre `nbNouvellesLignes` (dernier argument de
  `PageUpdateMultiple`) contrôle combien de lignes vides sont ajoutées.
- Pour supprimer une ligne : cocher + `acte=deleteFille`.
