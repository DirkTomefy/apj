# 16 — Conventions du module `a_tahina`

Une page pour savoir **où mettre quoi** et **quelle erreur lever**. Le reste
est libre.

## 16.1. Où va le code

| Couche | Rôle | Exemple |
|---|---|---|
| **SQL** (`data/`) | Tables, vues `_LIB`, contraintes | `ATH_FACTURE_FILLE_LIB` |
| **ORM** (`<module>/orm/`) | Une classe par table (+ `XxxLib` par vue). Contrôles dans `controler()`, règles du cycle de vie dans `createObject`, `validerObject`, `controlerAnnulationVisa` | `AthFacture` |
| **Service** (`<module>/orm/XxxService`) | Règle métier qui touche plusieurs tables | `AthAvoirService`, `AthVenteService` |
| **Calcul pur** (sans base) | Algorithme testable seul ; un chargeur lit la base à côté | `Elevage` + `ChargeurElevage` |
| **FH** (`<module>/fh/XxxFH`) | Déclare les écrans (`champ(...)`). **Aucune règle métier** | `FactureFH` |
| **JSP** | Une dizaine de lignes : appelle le FH, affiche, `catch` → `AtahinaUtil.erreurHtml(e)` | `facture-liste.jsp` |

Règles :
- Attributs ORM = colonnes de la table (insensible à la casse, les `_` comptent).
- Une classe par fichier, pas de classe imbriquée.
- Visibilité du package par défaut : n'écrire `public` que pour ce qui sert ailleurs.

## 16.2. Exceptions

```
Exception                      (imposée par APJ : ses méthodes déclarent "throws Exception")
 └─ AtahinaException           erreur métier : le message est montré tel quel à l'utilisateur
     ├─ SaisieException        donnée saisie invalide (getChamp() = nom du champ, facultatif)
     └─ StockException         stock insuffisant (getManques() = ventes non servies)
```

| Situation | Lever |
|---|---|
| Champ vide, valeur hors bornes, code inconnu | `SaisieException` (via `Verif`) |
| Objet introuvable, action interdite dans cet état (« Impossible d'annuler le visa… ») | `AtahinaException` |
| Stock insuffisant au visa | `StockException` (déjà levée par `AthVenteService`) |
| Panne SQL, bogue | **rien** : laisser remonter l'exception d'origine |

- Messages en français, sans accent dans le code Java (ou en entités HTML dans les FH).
- On n'avale jamais une exception (`catch` vide) ; si on l'enveloppe, on garde la cause : `new AtahinaException(msg, e)`.
- `AtahinaUtil.erreurHtml(e)` affiche le message seul pour une `AtahinaException` (même enveloppée dans une autre), et « Erreur technique : … » sans pile d'appels sinon ; la pile part dans le journal WildFly.

### `Verif` : une ligne au lieu d'un `if/throw`

```java
Verif.obligatoire(this.idclient, "Le client");                         // "Le client est obligatoire."
Verif.existe(new AthClient().getById(id, null, c), "Client introuvable : " + id);
Verif.verifier(l.getQte() > 0, "La quantite doit etre superieure a 0.");
Sexe.obligatoire(this.sexe);                                           // '0' ou '1', sinon SaisieException
TypeVente.obligatoire(l.getTypevente());                               // OEUF ou POULET
```

## 16.3. Héritage, interfaces, enums

- **Héritage** seulement quand il est imposé (`ClassMAPTable`, `ClassMere`,
  `ClassFille`) ou que c'est un vrai « est un » (`XxxLib extends Xxx` : la vue
  est la table plus des libellés). Pas de classe intermédiaire « pour plus tard ».
- **`GeneraticFH`** : toutes ses méthodes ont une version par défaut qui lève
  `nonSupporte`. Un FH ne redéfinit **que** les écrans qu'il propose.
- **Interface** seulement s'il existe (ou existera bientôt) au moins deux
  implémentations. Sinon, une classe suffit.
- **Enum** pour tout code fixe (`Sexe`, `TypeVente`, `Gestion`). La base garde
  ses chaînes (`'0'`, `'OEUF'`) : on convertit avec `depuis()` /
  `obligatoire()` et on réécrit avec `code()`.

## 16.4. Points d'extension existants

| Besoin | Où |
|---|---|
| Nouvel écran CRUD | `spec.json` + `python3 apj_gen.py spec.json` (voir [15](15-generateur.md)), puis coder la logique métier |
| Nouveau calendrier | `extends a_tahina.core.calendrier.Calendrier<T>` (dans le package du module, ex. `a_tahina.prevision.calendrier.CalendrierPoussin`) : déclarations dans le constructeur (`valeur`, `total`, `indicateur`, `style`) ; données d'une vue (constructeur avec critère) ou calculées (redéfinir `charger(debut, fin)`) |
| Nouvelle simulation jour par jour | `extends a_tahina.core.simulation.Simulation<J, R extends ResultatSimulation<J>>` : `preparer()` (renvoie un résultat neuf), `debut()`, `fin()`, `simulerJour(d)`, et facultatif `verifierJour(veille, jour)` (invariants, activés par `verifierInvariants(true)` en test) ; `simuler(jusqua)` simule jusqu'à la date demandée, même au-delà de `fin()` (`simuler()` sans date s'arrête à `fin()`) ; `FileEvenements<T>` pour consommer des événements datés (`jusquA(d)`) ; lecture de la base : `extends Chargeur<S>` (`nouvelleSimulation()`, `remplir(s, c)`, helper typé `lire(new Xxx(), c, where)`) ; exemple : `Elevage` + `ChargeurElevage` |
| Ordre de sortie du stock | `Elevage.GESTION_OEUF`, `Elevage.GESTION_POUSSIN` (`Gestion.FIFO` / `Gestion.LIFO`) |
| Paramètres de ponte par défaut | constantes `DEF_*` de `Elevage` ; par souche : table `SOUCHE` ; par semaine : `CONFIG_PREVISION` |
| Nouvelle option de champ | `AtahinaChampHelper` |
