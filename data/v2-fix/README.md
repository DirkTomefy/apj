# Calendrier poussin : vues SQL maintenables (v2-fix)

Remplace les vues de `data/v2/2026-10-19-03:04-calendrier_poussin_vues_v2.sql`
avec les **memes noms, colonnes et resultats**. Les constantes metier ne sont
plus en dur dans les vues.

## Ordre d'execution

```
./import_sql.sh data/v2-fix/01-parametres.sql
./import_sql.sh data/v2-fix/02-vues-calendrier.sql
./import_sql.sh data/v2-fix/04-verification.sql
./import_sql.sh data/v2-fix/03-nettoyage-optionnel.sql   # optionnel
```

## Ou changer quoi

| Je veux changer...                                   | Fichier / vue                          |
|------------------------------------------------------|----------------------------------------|
| une valeur par defaut de souche (21, 85, 50, 72...)  | `01-parametres.sql` -> `V_CAL_PARAM`   |
| l'horizon du calendrier (2000 jours)                 | `01-parametres.sql` -> `V_CAL_PARAM`   |
| les parametres d'une souche precise                  | table `SOUCHE` (donnees, pas de SQL)   |
| le planning de ponte par semaine                     | table `CONFIG_PREVISION` (donnees)     |
| une regle de calcul (CA, cout, generations, cumuls)  | `02-vues-calendrier.sql` (regles en tete du fichier) |
| les couleurs / la legende du calendrier             | `arbiochem-war/web/assets/css/a_tahina/calendrier.css` et `CalendrierPoussin.java` |

Les vues ne stockent rien : relancer le fichier modifie suffit, le calendrier
est recalcule a l'affichage suivant.

## Chaine de calcul

```
LOT, SOUCHE, CONFIG_PREVISION, DECL_PERTE_LOT, BONCOMMANDE_POUSSIN_FILLE, INSERTION_COMPARTIMENT_FILLE
  -> V_CAL_PARAM, V_CAL_SOUCHE                       (parametres)
  -> V_CAL_LOT_PU, V_CAL_LOT_INFO, V_CAL_INITIAUX, V_CAL_PERTES
  -> V_CAL_GENERATIONS                               (ponte gen 0, 1, 2)
  -> CALENDRIER_POUSSIN_RAW                          (un jour = une ligne)
  -> CALENDRIER_POUSSIN_CUMUL                        (lu par CalendrierPoussin.java)
```
