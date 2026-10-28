# Installation SQL : module Poussin + Facture / Avoir

`final.sql` cree **tout** en une fois (tables, contraintes, index, sequences, fonctions `GETSEQ`,
vues, donnees de reference, menu). Les noms de colonnes sont ceux des attributs des classes ORM
Java de `a_tahina` (insensible a la casse, les underscores comptent).

## Importer

Depuis la racine du projet, base Oracle lancee (`tomefy-oracle-11-start`) :

```
./import_sql.sh data/final/final.sql
```

Sans le script (SQL*Plus connecte au schema de l'application) : `@data/final/final.sql`

Ensuite : **redemarrer WildFly** (menus et roles sont mis en cache).

| Fichier | Role |
|---|---|
| `final.sql` | installation complete (s'arrete a la premiere erreur) |
| `drop.sql` | desinstalle le module **et ses donnees** (garde `ETAT`, les colonnes de `SOUCHE`, le role `resp` et les utilisateurs) |
| `exemple-facture.sql` | facultatif : un client a 40 %, deux produits |
| `correctifs-base-existante.sql` | seulement pour une base deja installee par les anciens scripts |

Reinstaller de zero : `drop.sql` puis `final.sql`. Lancer `final.sql` deux fois est refuse avec un
message clair (`Deja installe`).

## Prerequis (controles au debut de `final.sql`)

Tables de l'application de base : `SEXE`, `MAGASIN2`, `AS_INGREDIENTS`, `FOURNISSEUR`, `SOUCHE`,
`MENUDYNAMIQUE`, `USERMENU`, `ROLES`, `UTILISATEUR`, `UTILISATEURVALIDE`, `PARAMCRYPT`, les sequences
`SEQUSERMENU`, `SEQ_UTILISATEUR` et la fonction `GETSEQPARAMCRYPT`. Il manque un objet : le script le dit et
s'arrete avant de creer quoi que ce soit.

## Connexion apres l'installation

`final.sql` cree, s'ils n'existent pas, le role **`resp`** (rang 6), l'utilisateur et sa ligne de
chiffrement :

| Login | Mot de passe a saisir | Role |
|---|---|---|
| `resp` | `Resp2026` | `resp` |

**Changer le mot de passe** apres la premiere connexion. L'application ne stocke pas le mot de passe
saisi : elle le chiffre avec la ligne `PARAMCRYPT` de l'utilisateur (niveau 4, sens 1) et compare
avec `UTILISATEUR.PWDUSER` (`naol2022` ici). Sans ligne `PARAMCRYPT`, la connexion echoue
(« Pas de cryptage associe »). Pour un autre login ou mot de passe, modifier la section 7 de
`final.sql` avant l'import, ou le changer depuis l'application. Le role `resp` voit les menus
Poussins, Prevision poussin et Facturation.

## Ce que le script fait de particulier

- `SOUCHE` recoit 5 colonnes (jours d'eclosion, duree de vie, semaine de debut de ponte, taux
  d'eclosion, ratio de femelles), seulement si elles manquent.
- `ETAT` est creee seulement si elle n'existe pas ; les accents passent par `UNISTR` (aucun
  probleme de jeu de caracteres du client).
- `CONFIG_PREVISION` : planning de ponte de `SOUCHE001` (72 semaines) si cette souche existe.
  Pour une autre souche, ajouter ses lignes (`idsouche`, `semaine`, `nbjour`).
- Compartiments PARQUET1 a PARQUET4 : a adapter a la ferme.
- Les constantes du calendrier (horizon, valeurs par defaut d'une souche) sont dans la vue
  `V_CAL_PARAM` ; les regles de calcul sont decrites en tete de la section des vues.
- Le menu (Poussins, Prevision poussin, Facturation) est insere sans doublon (`WHERE NOT EXISTS`), avec `SET DEFINE OFF` pour que `&eacute;`
  reste intact.

## Verification faite

- Les **30 classes ORM** `a_tahina` correspondent aux colonnes (attributs <-> colonnes, dans les deux
  sens) sur un schema installe par `final.sql`.
- Le flux complet tourne sur ce schema : BC -> BR -> insertion compartiment -> lots -> calendrier,
  puis facture -> avoir -> deduction plafonnee a 40 %.
- Les anciens dossiers `v1`, `v2`, `v2-fix`, `v3` rejoues dans l'ordre donnent **la meme structure**
  (voir `../README.md`).
