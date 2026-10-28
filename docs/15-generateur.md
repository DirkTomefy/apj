# 15 — Générateur de code (`apj_gen.py`)

À partir d'un fichier `spec.json`, `apj_gen.py` écrit **toute la plomberie** d'un module :
SQL (tables, séquences, vues, menu), ORM Java, front helper (FH) et JSP (liste, saisie, fiche).
Le code produit est celui du projet : `orm/` + `fh/` (`extends GeneraticFH`) + `AtahinaChampHelper`.

```
python3 apj_gen.py spec.json                     # écrit dans le projet
python3 apj_gen.py spec.json --apercu            # liste les fichiers sans rien écrire
python3 apj_gen.py spec.json --seulement sql     # sql | java | jsp
python3 apj_gen.py spec.json --out /tmp/essai    # écrit ailleurs
python3 apj_gen.py spec.json --force             # écrase aussi les fichiers modifiés à la main
```

Ensuite : `./import_sql.sh data/gen/<module>/<module>.sql`, compiler, **redémarrer WildFly**.

## Écrire le `spec.json`

```json
{
  "module": "stock",
  "menu": { "titre": "Stock", "icone": "inventory_2", "role": "resp" },
  "entites": [
    {
      "nom": "Article",
      "type": "simple",
      "titre": "Articles",
      "champs": {
        "code":      { "libelle": "Code", "taille": 30, "obligatoire": true },
        "categorie": { "libelle": "Catégorie", "reference": "Categorie" },
        "pu":        { "libelle": "Prix unitaire", "type": "nombre", "defaut": 0, "min": 0 }
      }
    }
  ]
}
```

Les accents sont permis partout (ils sont convertis en `&eacute;`… dans le HTML et le menu).

### Niveau module

| Clé | Rôle | Défaut |
|---|---|---|
| `module` | **obligatoire**, un mot en minuscules → package `a_tahina.<module>` | — |
| `menu.titre` | crée un menu racine ; absent = les entités sont au premier niveau | aucun |
| `menu.icone`, `menu.role`, `menu.id`, `menu.rang` | icône Material, rôle qui voit le menu, id du menu racine, rang | `folder`, `resp`, `MENU<MODULE>`, `100` |
| `package` | racine Java | `a_tahina` |
| `pages` | dossier des JSP sous `pages/` | `a_tahina/v1/<module>` |

### Une entité

| Clé | Rôle | Défaut |
|---|---|---|
| `nom` | **obligatoire**, `MajusculeInitiale` (ex `BonSortie`) | — |
| `type` | `simple`, `avec_etat`, `maitre`, `detail` (voir ci-dessous) | `simple` |
| `titre` | pluriel, pour les menus et listes | `<nom>s` |
| `singulier` | pour « Saisie … », « Fiche … » | `<nom>` en mots |
| `table` | nom SQL (**24 caractères max**) | `<MODULE>_<NOM_EN_MAJUSCULES>` |
| `prefixe` | début des identifiants (`ART000001`), 1 à 8 lettres, unique | 3 premières lettres |
| `icone` | icône du menu | `list` |
| `menu` | `false` = pas d'entrée de menu | `true` |
| `detail` / `maitre` | lien maître ↔ lignes | — |
| `lignes` | nombre de lignes affichées en saisie (maître) | `10` |
| `champs` | `{ "nomchamp": { … } }` | — |

| `type` | Ce que ça donne |
|---|---|
| `simple` | table, liste, saisie, fiche |
| `avec_etat` | + colonne `etat`, filtre d'état en liste, boutons **Viser / Annuler** sur la fiche |
| `maitre` | `avec_etat` + lignes : saisie maître/détail, onglet « Détails » sur la fiche ; donne `"detail"` |
| `detail` | les lignes d'un maître (pas de page à lui) ; donne `"maitre"` ; `idmere` est ajouté tout seul |

`id` et `etat` sont ajoutés automatiquement : ne les déclare pas.

### Un champ

Le **nom** est aussi celui de la colonne et de l'attribut Java : minuscules et chiffres, **sans
underscore** (`qtemax`, pas `qte_max`), pas de mot réservé Oracle (`date` → `daty`).

| Clé | Rôle | Défaut |
|---|---|---|
| `libelle` | texte affiché | nom du champ |
| `type` | `texte`, `entier`, `nombre`, `date` | `texte` |
| `taille` | longueur d'un texte | `255` |
| `obligatoire` | `NOT NULL` + contrôle dans l'ORM (message clair) | `false` |
| `defaut` | valeur par défaut (SQL, ORM et formulaire) ; pour une date : `"aujourdhui"` | — |
| `min`, `max` | bornes d'un `entier` / `nombre` (contrainte `CHECK` + contrôle ORM) | — |
| `reference` | clé étrangère, voir ci-dessous | — |
| `dictionnaire` | table `id / val / desce` (ex `"SEXE"`) → liste déroulante | — |
| `liste`, `fiche`, `saisie` | `false` = absent de la liste / de la fiche / du formulaire | `true` |
| `recherche` | critère de recherche dans la liste | texte et références : oui |
| `intervalle` | critère min/max (dates) | dates : oui |

**Références** (clé étrangère + liste déroulante + colonne `<champ>lib` dans les vues) :

```json
"categorie": { "reference": "Categorie" }                       // autre entité du spec.json
"categorie": { "reference": "Categorie", "affiche": "nom" }     // champ montré (défaut : 1er texte)
"magasin":   { "reference": { "table": "MAGASIN2", "classe": "magasin.Magasin",
                              "affiche": "val", "id": "id" } }   // table de l'application de base
"destination": { "dictionnaire": "LIEURECEPTION" }              // dictionnaire id / val / desce
```

## Ce qui est généré

| Fichier | Contenu |
|---|---|
| `data/gen/<module>/<module>.sql` | prérequis, séquences, fonctions `GETSEQ…`, tables, contraintes, index, vues `<TABLE>_LIB`, menu + droits |
| `data/gen/<module>/<module>-drop.sql` | désinstallation (données comprises) |
| `…/orm/<Nom>.java`, `<Nom>Lib.java` | ORM de la table et de la vue |
| `…/fh/<Nom>FH.java` | `getPageRechercheForList`, `getPageConsulte`, `getPageInsert[Multiple]`, `ongletDetail` |
| `pages/…/<nom>/<nom>-liste\|saisie\|fiche.jsp` (+ `inc/<nom>-detail.jsp`) | écrans |

Le SQL suit les règles du projet : `SET DEFINE OFF`, contrôle des prérequis (tables de l'application
de base, `ETAT`…), refus d'une seconde installation, menu sans doublon, noms de colonnes = attributs de l'ORM.

## Modifier le code généré sans le perdre

Chaque fichier généré commence par la marque `@generated apj_gen.py`. Pour **garder tes
modifications** (règle métier dans l'ORM, bouton en plus sur la fiche…), **supprime cette ligne** :
le générateur ignorera ce fichier (il l'annonce), sauf avec `--force`. Ajoute des colonnes en
modifiant le `spec.json` puis en régénérant les fichiers que tu n'as pas modifiés.

## Erreurs fréquentes

Le générateur s'arrête avec un message qui nomme l'entité et le champ fautifs : nom avec underscore,
mot réservé Oracle, table de plus de 24 caractères, référence vers une entité inconnue, préfixe en
double, maître sans détail, références circulaires, `min` sur un texte, libellé de menu trop long.
