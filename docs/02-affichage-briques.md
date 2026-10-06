# 02 — Briques d'affichage

| Classe | Sert à |
|---|---|
| `Champ` | 1 input HTML |
| `Liste` | 1 `<select>` |
| `Formulaire` | groupe de `Champ` (entête) |
| `FormuFille` | groupe de `Champ` (ligne de détail) |
| `PageRecherche` | LISTE |
| `PageConsulte` | FICHE |
| `PageInsert` | SAISIE simple |
| `PageInsertMultiple` | SAISIE m/d |
| `PageUpdateMultiple` | MODIF m/d |

## 2.1. `Champ`

```java
Champ c = formu.getChamp("reference");
c.setLibelle("Référence");
c.setVisible(false);
c.setDefaut("AR");
c.setCss("form-control");
c.setTypeData(ConstanteAffichage.typeDaty);
c.setIsDateHeure(true);
c.setAutre("readonly");
c.setLien(lien + "?but=autre.jsp", "id=");
```

### Autocomplete (voir [14](14-autocomplete.md))

```java
c.setPageAppelComplete("pkg.Classe", "id", "TABLE");
c.setPageAppelCompleteAWhere("pkg.Classe", "id", "TABLE",
    "a;b", "x;y", " and actif = 1");
c.setPageAppelInsert("page.jsp", "retour1;retour2", "url1;url2");
c.setPageAppel("choix/page.jsp", "retour1;retour2");
```

### Méthodes statiques (sur `Champ[]` ou `Champ`)

```java
Champ.setVisible(champs, false);
Champ.setDefaut(champs, "");
Champ.setAutre(champs, "readonly");
Champ.setPageAppelComplete(champFille, ...);
Champ.setPageAppelInsert(champFille, ...);
Champ.setRecherchePrecis(champs, true);
```

## 2.2. `Liste`

```java
Liste[] liste = new Liste[2];
liste[0] = new Liste("idDevise", new Devise(), "val", "id");
liste[0].setDefaut("AR");
liste[1] = new Liste("modepaiement", new ModePaiement(), "val", "id");
liste[1].setApresW(" and actif = 1");

pi.getFormu().changerEnChamp(liste);
```

## 2.3. `Formulaire`

```java
Formulaire f = pi.getFormu();
f.getChamp("reference").setLibelle("Référence");
f.changerEnChamp(liste);
f.makeHtmlInsertTabIndex();
f.getHtmlInsert();
f.getHtmlEnsemble();
f.setAnotherButton("<a class='btn'>…</a>");
```

## 2.4. `FormuFille`

```java
FormuFille ff = pi.getFormufle();
ff.getChampFille("produit");   // template
ff.getChampMulitple("id");     // répété
ff.getChamp("produit_0");      // ligne 0
ff.setColOrdre(new String[]{"produit","quantite","pu"});
ff.makeHtmlInsertTableauIndex();
ff.getHtmlTableauInsert();
```

---

## Détails des signatures

### `Champ` — instance

| Méthode | Signature | Rôle |
|---|---|---|
| `setLibelle` | `void setLibelle(String)` | Libellé affiché |
| `getLibelle` | `String getLibelle()` | Retourne le libellé |
| `setNom` | `void setNom(String)` | Nom technique |
| `getNom` | `String getNom()` | Retourne le nom |
| `setValeur` | `void setValeur(String)` | Valeur affichée |
| `getValeur` | `String getValeur()` | Valeur (non échappée) |
| `getValeurLibelle` | `String getValeurLibelle()` | Libellé (autocomplete) |
| `setDefaut` | `void setDefaut(String)` | Défaut |
| `getDefaut` | `String getDefaut()` | Retourne le défaut |
| `setCss` | `void setCss(String)` | Classe CSS |
| `setType` | `void setType(String)` | Type HTML |
| `setTypeData` | `void setTypeData(String)` | Type logique (`typeDaty`) |
| `setVisible` | `void setVisible(boolean)` | Cache |
| `getVisible` | `boolean getVisible()` | État |
| `setAutre` | `void setAutre(String)` | Attributs HTML bruts |
| `getAutre` | `String getAutre()` | Retourne |
| `setIsDateHeure` | `void setIsDateHeure(boolean)` | Force `datetime-local` |
| `setPhoto` | `void setPhoto(boolean)` | Input file |
| `setLien` | `void setLien(String page, String prefixe)` | Rend cliquable |
| `setPageAppel` | `void setPageAppel(String page, String retour)` | Popup choix |
| `setPageAppelComplete` | `void setPageAppelComplete(String classe, String valeur, String table)` | Autocomplete |
| `setPageAppelComplete` | `void setPageAppelComplete(String classe, String valeur, String table, String retour, String mapping)` | Autocomplete + mapping |
| `setPageAppelCompleteAWhere` | `void setPageAppelCompleteAWhere(String classe, String valeur, String table, String retour, String mapping, String aWhere)` | Autocomplete + WHERE |
| `setPageAppelInsert` | `void setPageAppelInsert(String page, String retour, String url)` | Bouton « + » |
| `setEstMultiple` | `void setEstMultiple(boolean)` | Multi-sélection |
| `setOptions` | `void setOptions(String[] valeurs, String[] libelles)` | Options multi |
| `findAutocompleteDisplayValue` | `void findAutocompleteDisplayValue(Connection c)` | Recharge le libellé |
| `setUseMotCle` | `void setUseMotCle(boolean)` | Utilise `getMotCles()` |
| `setAc_affiche` | `void setAc_affiche(String)` | Colonne affichée |
| `setAc_valeur` | `void setAc_valeur(String)` | Colonne valeur |
| `setAc_nomTable` | `void setAc_nomTable(String)` | Table |
| `setAc_aWhere` | `void setAc_aWhere(String)` | Filtre |
| `getHtml` | `String getHtml()` | HTML du champ |
| `getHtmlTableau` | `String getHtmlTableau(int i)` | HTML pour ligne i |
| `getHtmlInsert` | `String getHtmlInsert()` | HTML formulaire insert |
| `setFormulaire` | `void setFormulaire(Formulaire)` | Lie au formulaire |
| `setObjet` | `void setObjet(ClassMAPTable)` | Lie à l'objet |
| `copierConfiguration` | `Champ copierConfiguration(String nouveauNom)` | Clone |

### `Champ` — statiques

| Méthode | Signature | Rôle |
|---|---|---|
| `setLibelle` | `void setLibelle(Champ[], String)` | Idem sur tous |
| `setVisible` | `void setVisible(Champ[], boolean)` | Idem |
| `setDefaut` | `void setDefaut(Champ[], String)` | Idem |
| `setCss` | `void setCss(Champ[], String)` | Idem |
| `setAutre` | `void setAutre(Champ[], String)` | Idem |
| `setTaille` | `void setTaille(Champ[], int)` | Idem |
| `setTypeData` | `void setTypeData(Champ[], String)` | Idem |
| `setRecherchePrecis` | `void setRecherchePrecis(Champ[], boolean)` | Idem |
| `setPageAppelComplete` | `void setPageAppelComplete(Champ, ...)` | Sur une fille |
| `setPageAppelInsert` | `void setPageAppelInsert(Champ, ...)` | Sur une fille |
| `setPageAppel` | `void setPageAppel(Champ[], String, String)` | Sur plusieurs |
| `setAutoCompleteDependante` | `void setAutoCompleteDependante(Champ[], Champ[], ...)` | Dépendance |

### `Liste` — constructeur

```java
Liste(String nomChamp, ClassMAPTable objet, String colonneLibelle, String colonneValeur)
```

| Méthode | Signature | Rôle |
|---|---|---|
| `setDefaut` | `void setDefaut(String)` | Option sélectionnée |
| `setApresW` | `void setApresW(String)` | Filtre SQL |
| `setValeur_ac` | `void setValeur_ac(String)` | JSON interne |

### `Formulaire` — méthodes

| Méthode | Signature | Rôle |
|---|---|---|
| `getChamp` | `Champ getChamp(String nom)` | Champ par nom |
| `getChamps` | `Champ[] getChamps()` | Tous les champs |
| `changerEnChamp` | `void changerEnChamp(Liste[] listes)` | Remplace par `<select>` |
| `makeHtmlInsertTabIndex` | `void makeHtmlInsertTabIndex()` | Calcule les tabindex |
| `getHtmlInsert` | `String getHtmlInsert()` | HTML formulaire |
| `getHtmlEnsemble` | `String getHtmlEnsemble()` | HTML + boutons |
| `setAnotherButton` | `void setAnotherButton(String)` | Bouton additionnel |

### `FormuFille` — méthodes

| Méthode | Signature | Rôle |
|---|---|---|
| `getChamp` | `Champ getChamp(String nom)` | Champ précis (`x_0`) |
| `getChampFille` | `Champ getChampFille(String nom)` | Template (toutes lignes) |
| `getChampMulitple` | `Champ getChampMulitple(String nom)` | Répété (typo historique) |
| `setColOrdre` | `void setColOrdre(String[])` | Ordre sauvegardé |
| `makeHtmlInsertTableauIndex` | `void makeHtmlInsertTableauIndex()` | Tabindex |
| `getHtmlTableauInsert` | `String getHtmlTableauInsert()` | HTML du tableau |
| `getHtmlTableau` | `String getHtmlTableau(int i)` | HTML ligne i |