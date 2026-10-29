# Documentation APJ — Guide d'utilisation du framework

Bienvenue. Cette documentation explique **comment créer un nouvel écran**
avec le framework APJ, sans entrer dans la logique métier.

## Sommaire

| # | Fichier | Contenu |
|---|---|---|
| 01 | [01-modele.md](01-modele.md) | Créer une classe **modèle** (ORM) |
| 02 | [02-affichage-briques.md](02-affichage-briques.md) | Briques : `Champ`, `Liste`, `Formulaire`, `FormuFille` |
| 03 | [03-liste.md](03-liste.md) | Écran **LISTE** |
| 04 | [04-fiche.md](04-fiche.md) | Écran **FICHE** |
| 05 | [05-saisie-simple.md](05-saisie-simple.md) | Écran **SAISIE simple** |
| 06 | [06-saisie-multiple.md](06-saisie-multiple.md) | Écran **SAISIE maître/détail** |
| 07 | [07-modif-multiple.md](07-modif-multiple.md) | Écran **MODIF maître/détail** |
| 08 | [08-onglets.md](08-onglets.md) | **Onglets** dans une fiche |
| 09 | [09-actions.md](09-actions.md) | **Action personnalisée** (`apresXxx.jsp`) |
| 10 | [10-menu.md](10-menu.md) | Déclarer un **menu** |
| 11 | [11-conventions-pieges.md](11-conventions-pieges.md) | Conventions & pièges |
| 12 | [12-exemples-complets.md](12-exemples-complets.md) | **Exemple complet** de A à Z |
| 13 | [13-terme-complique.md](13-terme-complique.md) | **Glossaire** |
| 14 | [14-autocomplete.md](14-autocomplete.md) | **Autocomplete** |
| 15 | [15-generateur.md](15-generateur.md) | **Générateur** `apj_gen.py` (SQL, ORM, FH, JSP depuis un `spec.json`) |
| 16 | [16-conventions.md](16-conventions.md) | **Conventions `a_tahina`** : où va le code, exceptions, héritage |

## Parcours conseillé

- 🟢 **Débutant** : `01 → 02 → 03 → 04 → 05 → 13`
- 🟡 **Intermédiaire** : `06 → 07 → 08 → 11`
- 🔴 **Avancé** : `09 → 12 → 14`

## Vue d'ensemble

```
[Modèle]                          [Affichage]                [Action / JSP]

ClassMAPTable                     Champ                      apresTarif.jsp
   └ ClassUser                    Liste                      apresMultiple.jsp
       └ ClassEtat                Formulaire                 apresXxx.jsp
           └ ClassMere            FormuFille
               └ XxxCpl           PageRecherche      → LISTE
           └ ClassFille           PageConsulte       → FICHE
               └ XxxCpl           PageInsert         → SAISIE simple
                                  PageInsertMultiple → SAISIE m/d
                                  PageUpdateMultiple → MODIF m/d
```

## URL universelle

**Ouvrir** : `module.jsp?but=<chemin/page.jsp>[&id=...][&tab=...]`
**Action** : `module.jsp?but=apresTarif.jsp&acte=<action>&classe=<pkg.Classe>&bute=<retour>`

## Règle d'or

> Dès qu'un écran a plus de 20 lignes de configuration dans un JSP,
> il doit **sortir du JSP** et devenir une **classe qui étend la classe
> du framework**. Voir [12-exemples-complets.md](12-exemples-complets.md).

---

## Détails des signatures

### Convention de nommage des fichiers

| Préfixe | Type |
|---|---|
| `00-` | Index |
| `01-` à `09-` | Guides par type d'écran |
| `10-` à `11-` | Menu et conventions |
| `12-` | Exemple complet |
| `13-` | Glossaire |
| `14-` | Autocomplete |
| `15-` | Générateur de code |
| `16-` | (réservé) |

### Classes racines (hiérarchie)

| Classe | Parent | Rôle |
|---|---|---|
| `ClassMAPTable` | — | ORM : CRUD, historique, synchro |
| `ClassUser` | `ClassMAPTable` | + `iduser` |
| `ClassEtat` | `ClassUser` | + `etat` (workflow) |
| `ClassMere` | `ClassEtat` | + filles (détail) |
| `ClassFille` | `ClassMAPTable` | + lien vers la mère |

### Classes d'affichage (par écran)

| Classe | Écran produit |
|---|---|
| `PageRecherche` | LISTE |
| `PageConsulte` | FICHE |
| `PageInsert` | SAISIE simple |
| `PageInsertMultiple` | SAISIE maître/détail |
| `PageUpdateMultiple` | MODIF maître/détail |

### JSP d'action génériques

| Fichier | Usage |
|---|---|
| `apresTarif.jsp` | Objet unique |
| `apresMultiple.jsp` | Maître/détail |
| `apresPopup.jsp` | Retour popup |
| `apresSpecifique.jsp` | Actions ponctuelles |

### Paramètres d'URL universels

| Param | Rôle |
|---|---|
| `but` | Page à ouvrir |
| `bute` | Page de retour après action |
| `id` | PK de l'objet |
| `acte` | Action (`insert`, `update`, `valider`, …) |
| `classe` | Classe Java (avec package) |
| `classefille` | Classe Java de la fille |
| `nomtable` | Force une table/vue |
| `tab` | Onglet actif |
| `nombreLigne` | Nb lignes du tableau m/d |
| `colonneMere` | FK dans la fille |