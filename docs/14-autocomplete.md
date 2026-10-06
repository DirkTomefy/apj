# 14 — Autocomplete

> Cette version du fichier ne contient que la section **Détails des
> signatures**. Le contenu principal du chapitre (livré dans un message
> précédent) est à placer **avant** cette section.

## Détails des signatures

### `Champ` — autocomplete (instance)

| Méthode | Signature | Rôle |
|---|---|---|
| `setPageAppelComplete` | `void setPageAppelComplete(String classe, String valeur, String table)` | Simple |
| `setPageAppelComplete` | `void setPageAppelComplete(String classe, String valeur, String table, String retour, String mapping)` | + mapping |
| `setPageAppelCompleteAWhere` | `void setPageAppelCompleteAWhere(String classe, String valeur, String table, String retour, String mapping, String aWhere)` | + WHERE |
| `setPageAppelCompleteInsert` | `void setPageAppelCompleteInsert(String classe, String valeur, String table, String page, String retour, String url)` | + bouton « + » |
| `setPageAppelInsert` | `void setPageAppelInsert(String page, String retour, String url)` | Bouton « + » |
| `setPageAppel` | `void setPageAppel(String page, String retour)` | Popup choix |
| `setPageAppel` | `void setPageAppel(String page, String retour, String pageInsert)` | Popup + insert |
| `setAutocompleteDynamique` | `void setAutocompleteDynamique(String classe, String affiche, String valeur, String table)` | Dynamique |
| `setUseMotCle` | `void setUseMotCle(boolean)` | `getMotCles()` |
| `setAc_classeMapping` | `void setAc_classeMapping(String)` | Classe Java |
| `setAc_affiche` | `void setAc_affiche(String)` | Colonne affichée |
| `setAc_valeur` | `void setAc_valeur(String)` | Colonne valeur |
| `setAc_nomTable` | `void setAc_nomTable(String)` | Table |
| `setAc_aWhere` | `void setAc_aWhere(String)` | Filtre |
| `setChampRetour` | `void setChampRetour(String)` | `nom;nomlibelle` |
| `setChampRetourMapping` | `void setChampRetourMapping(String)` | Mapping multiple |
| `setChampUrl` | `void setChampUrl(String)` | Paramètres popup |
| `setPropre` | `void setPropre(boolean)` | `fetchAutocomplete` propre |
| `setChampDependant` | `void setChampDependant(String)` | Champ dont on dépend |
| `setJsonData` | `void setJsonData(String)` | Données JSON inline |
| `findAutocompleteDisplayValue` | `void findAutocompleteDisplayValue(Connection c)` | Recharge le libellé |
| `getAutocompleteDynamiqueJs` | `String getAutocompleteDynamiqueJs()` | Génère le JS |

### `Champ` — autocomplete (statique)

| Méthode | Signature | Rôle |
|---|---|---|
| `setPageAppelComplete` | `void setPageAppelComplete(Champ[] champs, String classe, String valeur, String table)` | Sur tous les champs |
| `setPageAppelComplete` | `void setPageAppelComplete(Champ[] champs, String classe, String valeur, String table, String retour, String mapping)` | + mapping |
| `setPageAppelCompleteAWhere` | `void setPageAppelCompleteAWhere(Champ[] champs, ..., String aWhere)` | + WHERE |
| `setPageAppelCompletePropre` | `void setPageAppelCompletePropre(Champ[] champs, ...)` | Version « propre » |
| `setPageAppelInsert` | `void setPageAppelInsert(Champ[] champs, String page, String retour, String url)` | Bouton « + » |
| `setAutoCompleteDependante` | `void setAutoCompleteDependante(Champ[] champs, Champ[] champDependante, String mapping, String nomTable, String filtre, String retour, String mappingRetour)` | Dépendante |
| `setAutoCompleteDependanteValeur` | `void setAutoCompleteDependanteValeur(Champ[] champs, Champ[] champDependante, ...)` | Idem + valeur |

### Format du retour JSON (`fetchAutocomplete`)

```json
[
  {
    "label":  "Fournisseur ABC",     // affiché dans l'input visible
    "value":  "F001",                // écrit dans l'input caché
    "retour": "20;4011;401100"       // valeurs des champs dépendants (séparateur ;)
  }
]
```

| Clé | Sens | Destination |
|---|---|---|
| `label` | Libellé | `<nom>libelle` (input visible) |
| `value` | ID | `<nom>` (input caché) |
| `retour` | Colonnes retour | Split `;` → champs dépendants |

### Signature de `fetchAutocomplete` (JS global)

```javascript
fetchAutocomplete(
    request,      // objet jQuery UI
    response,     // objet jQuery UI
    affiche,      // colonne affichée
    valeur,       // colonne valeur
    recherche,    // colonne de recherche
    nomTable,     // table SQL
    classeMapping,// classe Java
    useMotCle,    // "true" ou "false"
    champRetour,  // "nom;nomlibelle"
    aWhere        // " and actif = 1"
);
```

### Mapping `champRetour` / `champRetourMapping`

```
champRetour         = "pu;taux;compte_achat;compte_achat;libelle"
champRetourMapping  = "pu;tauxDeChange;compte;comptelibelle;produitLib"

Sélection :
    pu (colonne 1)   → pu (champ 1)
    taux (colonne 2) → tauxDeChange (champ 2)
    ...
```

### Contrat de `setPageAppelInsert` (bouton « + »)

```java
setPageAppelInsert(
    String pageAppelInsert,   // ex : "fournisseur/fournisseur-saisie.jsp"
    String champReturn,       // ex : "fournisseur;fournisseurlibelle"
    String champUrl           // ex : "id;nom"
);
```

- `pageAppelInsert` : page ouverte dans la popup.
- `champReturn` : champs de la page **courante** à remplir.
- `champUrl` : champs de la page **popup** à envoyer (séparés par `;`).

### HTML généré (structure)

```html
<div class="d-flex input-with-addons">
  <!-- Visible -->
  <input id="<nom>libelle" name="<nom>libelle" type="text" />
  <!-- Caché -->
  <input id="<nom>" name="<nom>" type="hidden" />
  <!-- Loupe -->
  <button id="<nom>searchBtn">…</button>
  <!-- (optionnel) Bouton « + » -->
  <button onclick="pagePopUp(...)">+</button>
</div>
```

### Événements JS

| Événement | Effet |
|---|---|
| `Tab` (input visible) | Déclenche `autocomplete('search')` |
| Clic loupe | Idem |
| `input` | Reset le champ caché + désactive |
| `select` | Remplit libellé + ID + retour + focus suivant |
| `change` sur `<nom>` | Déclenche `onchange` du champ |