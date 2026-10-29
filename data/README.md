# Scripts SQL

| Dossier | A utiliser ? |
|---|---|
| **`final/`** | **oui** : installation complete en un fichier (`final/final.sql`), desinstallation, exemple |
| `v1`, `v2`, `v2-fix`, `v3`, `v4` | historique, dans l'ordre ci-dessous ; rejouables et equivalents a `final.sql` |
| `deprecated/` | anciens essais, ne pas lancer |
| `drop.sql` | ancien script de suppression (obsolete) : utiliser `final/drop.sql` |

## Ordre de l'historique (base deja installee ou installation pas a pas)

```
v1/            init, addfournisseur, role-et-utilisateur, menu, etat, situationcomp
v2/            init, config, rename_date_eclosion, fix_view_eclosion, fix_all_columns,
               ratio_femmelle, view-1, view-2, fix_bon_reception_souche,
               migration_decl_perte_lot, calendrier_poussin_vues_v2
v2-fix/        01-parametres, 02-vues-calendrier, 03-nettoyage-optionnel, 05-index-et-cle-etrangere, 06-menu-prevision
               (04-verification : controle seulement)
v3/            facture_avoir, menu_facture_avoir, menu_avoir_saisie
v4/            01-vente-oeuf-poulet, 02-supprimer-vues-calendrier
               (le calendrier est calcule en Java : a_tahina.prevision.simulation)
```

Chaque fichier se lance avec `./import_sql.sh data/<dossier>/<fichier>.sql`.

## Regles pour ecrire un script ici

- `SET DEFINE OFF` en tete : sinon `&eacute;` est pris pour une variable (le menu affichait `r;ception`).
- Pas de `PROMPT ... -` : un tiret en fin de ligne est une continuation et avale la ligne suivante.
- Pas de commentaire apres un `DEFAULT` (Oracle le garde dans la valeur par defaut).
- Accents dans les donnees : `UNISTR('Cr\00e9\00e9')`, pas de lettres accentuees directes.
- Colonnes = attributs de l'ORM : pas d'underscore (`qteinit`, pas `qte_init`).
