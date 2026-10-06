# 05 — Créer un écran SAISIE simple

**Fichier** : `pages/monpackage/monobjet-saisie.jsp`

```jsp
<%@page import="affichage.PageInsert"%>
<%@page import="affichage.Liste"%>
<%@page import="monpackage.MonObjet"%>
<% try {
    user.UserEJB u = (user.UserEJB) session.getValue("u");

    // 1) Objet + PageInsert
    MonObjet t = new MonObjet();
    PageInsert p = new PageInsert(t, request);

    // 2) Personnalisation du formulaire
    // (PageInsert expose getFormu() dans la plupart des versions)
    // p.getFormu().getChamp("libelle").setLibelle("Libellé");

    // 3) Remplacer un champ par une Liste
    // Liste[] liste = new Liste[1];
    // liste[0] = new Liste("modepaiement", new ModePaiement(), "val", "id");
    // p.getFormu().changerEnChamp(liste);

    // 4) Autocomplete
    // p.getFormu().getChamp("fournisseur")
    //     .setPageAppelComplete("faturefournisseur.Fournisseur","id","FOURNISSEUR");

    // 5) Objet avec les valeurs du formulaire
    MonObjet obj = (MonObjet) p.getObjectAvecValeur();
%>
<div class="content-wrapper">
  <h1>Saisie d'un objet</h1>

  <form action="<%= p.getLien() %>?but=apresTarif.jsp" method="post">
  <% out.println(p.getFormu().getHtmlInsert()); %>

    <input type="hidden" name="acte"     value="insert" />
    <input type="hidden" name="bute"     value="monpackage/monobjet-fiche.jsp" />
    <input type="hidden" name="classe"   value="monpackage.MonObjet" />
    <input type="hidden" name="nomtable" value="MONOBJET" />
  </form>
</div>
<% } catch(Exception e){ e.printStackTrace(); } %>
```

## Champs cachés obligatoires

| Nom | Rôle |
|---|---|
| `acte` | Action : `insert`, `update`, `delete`, `valider`, `annuler`, … |
| `bute` | Page de redirection après l'action |
| `classe` | Nom Java complet (avec package) du modèle |
| `nomtable` | (Optionnel) Force une autre table/vue |

## Actions possibles (`apresTarif.jsp`)

| `acte` | Effet |
|---|---|
| `insert` | Crée l'objet |
| `update` | Modifie |
| `delete` | Supprime (avec historique) |
| `valider` | Passe à l'état validé (11) |
| `annuler` | Passe à l'état annulé (0) |
| `annulerVisa` | Annule le visa |
| `cloturer` | Passe à l'état clôturé (9) |
| `payer` | Passe à l'état payé |
| `dupliquer` | Duplique (avec filles) |
| `savevalider` | Insère + valide |

## Retour après action

`apresTarif.jsp` fait un `document.location.replace(...)` vers :
```
lien?but=<bute>&valeur=<val>&id=<id>
```
Le `id` renvoyé dépend de l'action.
