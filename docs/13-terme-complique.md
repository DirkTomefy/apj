# 13 — Glossaire des termes compliqués

## 1. Termes malgaches

| Mot | Traduction | Utilisation |
|---|---|---|
| **daty** | date | Nom de colonne de dates |
| **valiny** | réponse / résultat | Variable de retour |
| **bute** | destination | Paramètre URL de redirection |
| **but** | idem | Paramètre URL principal |
| **val** | valeur | Colonne générique `TypeObjet` |
| **desce** | description | Colonne `TypeObjet` |
| **tupple** | *tuple* (typo) | `getTuppleID()` |
| **motsClesss** | mots-clés (3 s, typo) | Critères de recherche |
| **tana** | ici / là-bas | Log de debug |
| **tafiditra ato** | on entre ici | Log de debug |
| **vita** | fini | Log |
| **miditra** | ça entre | Log |
| **tahina** | (prénom) | Exemple INSERT menu |
| **socobis** | nom d'appli | URL |

## 2. Termes du framework

| Terme | Sens |
|---|---|
| **ClassMAPTable** | Racine ORM |
| **ClassUser** | + `iduser` |
| **ClassEtat** | + `etat` (workflow) |
| **ClassMere** | Objet maître (avec filles) |
| **ClassFille** | Objet détail |
| **ClassIA** | Interface chatbot |
| **Cpl** | Vue SQL complétée (avec libellés) |
| **CplLib** | Cpl + libellé principal |
| **FilleCpl** | Cpl d'une fille |
| **MereCpl** | Cpl d'une mère |
| **MereTraite** | Vue retraitée |
| **ResteA…** | Vue calculant un reste |
| **`_LIB`** | Suffixe de vue avec libellés |
| **`_VISEE`** | Vue filtrée sur état visé |

## 3. Services

| Terme | Sens |
|---|---|
| **CGenUtil** | Recherche / `makeWhere` / réflexion |
| **UtilDB** | Connexion + properties |
| **Utilitaire** | Helpers (dates, chaînes) |
| **UtilitaireFormule** | Remplace `[formules]` |
| **TypeObjet** | Dictionnaire `id / val / desce` |
| **AdminGen** | Recherche générique |
| **ListeColonneTable** | Cache des champs/colonnes |

## 4. Historique / synchro

| Terme | Sens |
|---|---|
| **MapHistorique** | Table `HISTORIQUE` (actions) |
| **Historique_valeur** | Table `HISTORIQUE_VALEUR_LIB` (valeurs) |
| **HistoInsert** | Indexation recherche |
| **estHistorise** | Historise en détail |
| **estIndexable** | Indexe pour la recherche |
| **REQUETE_A_ENVOYER** | Table de synchro |
| **histoSync** | Constante table synchro |
| **isMirroring** | Miroir (toujours `false`) |

## 5. Workflow

| Terme | Sens |
|---|---|
| **etat** | 1=créé, 11=validé, 0=annulé, 9=clôturé |
| **viser** | Valider hiérarchiquement |
| **validerObject** | Passe à 11 |
| **annulerVisa** | Retour en 1 |
| **RoleValidation** | Rôles + états + ordre |
| **isHierarchique** | Validation multi-rôles |
| **getPreviousRole** | Rôle précédent |

## 6. Session

| Terme | Sens |
|---|---|
| **UserEJB** | Utilisateur session |
| **UserEJBBean** | Variante EJB |
| **MapUtilisateur** | Vue simplifiée |
| **getTuppleID()** | PK utilisateur |
| **getRefuser()** | Login/ref |

## 7. Termes d'affichage

| Terme | Sens |
|---|---|
| **PageRecherche** | Écran LISTE |
| **PageConsulte** | Écran FICHE |
| **PageInsert** | SAISIE simple |
| **PageInsertMultiple** | SAISIE m/d |
| **PageUpdateMultiple** | MODIF m/d |
| **Page** | Classe batch update |
| **Champ** | 1 input |
| **Liste** | `<select>` |
| **Formulaire** | Groupe de champs (entête) |
| **FormuFille** | Groupe de champs (fille) |
| **getChampFille** | Template (toutes lignes) |
| **getChampMulitple** | Répété (typo) |
| **preparerDataFormu** | Prépare les données |
| **setColOrdre** | Ordre des colonnes |
| **setAnotherButton** | Bouton additionnel |

## 8. Autocomplete

| Terme | Sens |
|---|---|
| **setPageAppel** | Popup de choix |
| **setPageAppelComplete** | Autocomplete jQuery |
| **setPageAppelCompleteAWhere** | + WHERE |
| **setPageAppelInsert** | Bouton « + » |
| **ac_affiche** | Colonne affichée |
| **ac_valeur** | Colonne valeur |
| **ac_nomTable** | Table |
| **ac_aWhere** | Filtre |
| **ac_classeMapping** | Classe Java |
| **useMotCle** | Utilise `getMotCles()` |
| **isPropre** | Autocomplete « propre » |
| **champRetour** | Champs à remplir |
| **champRetourMapping** | Mapping retour |
| **champUrl** | Paramètres popup |
| **fetchAutocomplete** | Fonction JS globale |

## 9. Paramètres d'URL

| Param | Sens |
|---|---|
| **but** | Page à ouvrir |
| **bute** | Redirection après action |
| **id** | PK objet |
| **idMere** | PK mère (onglet) |
| **acte** | Action |
| **classe** | Classe Java |
| **classefille** | Classe Java fille |
| **nomtable** | Force table/vue |
| **nombreLigne** | Nb lignes m/d |
| **colonneMere** | FK dans la fille |
| **tab** | Onglet actif |
| **rajoutLien** | Paramètres à propager |
| **champReturn** | Retour popup |
| **champUrl** | Envoi popup |
| **action** | Sous-action |
| **type** | Type bouton |
| **prefixe** | Préfixe PK |
| **procedure** | Séquence |
| **refuser** | Référence utilisateur |
| **idrole** | Rôle |
| **interdit** | 0=autorisé, 1=interdit |

## 10. Actions `acte=`

| Action | Effet |
|---|---|
| `insert` | Insère |
| `insertWithAction` | Insère + action |
| `insertFille` | Filles seules (idMere requis) |
| `insertFilleSeul` | Filles sans mère |
| `insertSansControle` | Sans `controler()` |
| `insertMereLierFille` | Mère + rattache filles |
| `update` | Modifie |
| `updateInsert` | Mère + filles |
| `updateSansControle` | Sans `controler()` |
| `updateMultiple` | Batch |
| `updatevalider` | Update + valide |
| `updateMvt` | + validation caisse |
| `delete` | Supprime |
| `deleteMultiple` | Lot |
| `deleteFille` | Filles cochées |
| `valider` | État 11 |
| `validerTous` | Toute la table |
| `validerMultiple` | Lot |
| `annuler` | État 0 |
| `annulerVisa` | Retour en 1 |
| `cloturer` | État 9 |
| `payer` | Payé |
| `dupliquer` | Duplique |
| `interview` | Interviewé |
| `custom` | Perso (`acteDetail`) |
| `savevalider` | Insère + valide |

## 11. JSP d'action

| Fichier | Rôle |
|---|---|
| **apresTarif.jsp** | Objet unique |
| **apresMultiple.jsp** | Maître/détail |
| **apresPopup.jsp** | Retour popup |
| **apresSpecifique.jsp** | Actions ponctuelles |
| **module.jsp** | Layout principal |

## 12. Termes techniques

| Terme | Sens |
|---|---|
| **POJO / Bean** | Classe Java simple |
| **ORM** | Objet ↔ Table SQL |
| **Réflexion** | Java : `getMethod`, `invoke` |
| **Field** | Champ Java |
| **PK / FK** | Clé primaire / étrangère |
| **CRUD** | Create / Read / Update / Delete |
| **Batch** | Plusieurs requêtes en un appel |
| **Commit / Rollback** | Transaction |
| **AutoCommit** | JDBC auto-commit |
| **PreparedStatement** | JDBC paramétré |
| **ResultSet** | Curseur SQL |
| **Base64** | Encodage binaire |
| **CSV / JSON** | Formats d'échange |
| **XSS** | Injection HTML/JS |
| **SQL Injection** | Injection SQL |

## 13. Termes JSP

| Terme | Sens |
|---|---|
| **scriptlet** | `<% ... %>` |
| **expression** | `<%= ... %>` |
| **directive** | `<%@ ... %>` |
| **jsp:include** | Inclusion |
| **jsp:param** | Paramètre d'inclusion |
| **forward / redirect** | Côté serveur / client |
| **popup** | Fenêtre modale |
| **onglet / tab** | Sous-page |
| **inc/** | Dossier des JSP inclus |

## 14. Termes métier

| Terme | Sens |
|---|---|
| **BC** | Bon de Commande |
| **BL** | Bon de Livraison |
| **FF** | Facture Fournisseur |
| **OF** | Ordre de Fabrication |
| **dmdachat** | Demande d'achat |
| **proforma** | Facture provisoire |
| **acompte** | Paiement partiel |
| **STC** | Solde de Tout Compte |
| **TVA / HT / TTC** | Taxes |
| **pu** | Prix Unitaire |
| **ristourne** | Remise fin de période |
| **remise** | Réduction immédiate |
| **carton** | Emballage |
| **ecriture** | Écriture comptable |

## 15. Termes trompeurs

| Terme | Ce qu'on croit | Réalité |
|---|---|---|
| **Tupple** | tuple | Faute pour « tuple » = PK |
| **Cpl** | Classe | Suffixe de convention |
| **getValeur()** | Valeur affichée | Valeur brute non échappée |
| **mode "modif"** | Édition | Validation active |
| **colonneMere** | Colonne mère | FK dans la fille |
| **Page** | Écran complet | Classe technique batch |

## 16. Suffixes

| Suffixe | Sens |
|---|---|
| `Cpl` | Complété |
| `CplLib` | Cpl + libellé |
| `Mere` | Maître |
| `Fille` | Détail |
| `MereTraite` | Retraité |
| `ResteA...` | Reste calculé |
| `_LIB` | Libellés |
| `_VISEE` | Filtré visé |
| `_CPL` | Vue CPL |
| `_CPL_LIB` | CPL + libellés |
| `_RESTE` | Reste |
| `Fiche / Liste / Saisie / Modif` | Type d'écran |

---

## Détails des signatures

### Convention de nommage Java (framework)

| Préfixe | Sens | Exemple |
|---|---|---|
| `get*` / `set*` | Getter / Setter JavaBean | `getLibelle()`, `setLibelle(String)` |
| `is*` | Booléen | `isVisible()`, `isPhoto()` |
| `getIs*` | Booléen (mélange) | `getIsHierarchique()` |
| `getEst*` | Booléen (mélange) | `getEstIndexable()` |
| `set*Direct` | Setter direct (sans validation) | `setValeurDirect(String)` |
| `construire*` | Génère une valeur | `construirePK(c)` |
| `make*` | Construit une chaîne / HTML | `makePK(c)`, `makeHtml()` |
| `get*Par*` | Recherche par critère | `getById`, `getChampByName` |
| `find*` | Recherche / extraction | `findAutocompleteDisplayValue` |
| `*Batch` | Traitement par lot | `insertToTableBatch` |
| `*WithHisto` | Avec historique | `updateToTableWithHisto` |
| `*Multiple` | Sur plusieurs objets | `updateObjectMultiple` |
| `*Cpl` / `*CplLib` | Vue complétée | `BonDeCommandeCpl` |

### Types Java autorisés (types mappés)

| Type Java | Constante MAP |
|---|---|
| `boolean` | 0 |
| `byte` | 1 |
| `short` | 2 |
| `int` | 3 |
| `long` | 4 |
| `float` | 5 |
| `double` | 6 |
| `String` | 10 |
| `Date` (java.sql) | 21 |
| `Time` (java.sql) | 22 |
| `Blob` | 31 |
| `Clob` | 32 |
| `Integer` | 33 |
| `Number` | 34 |
| `Timestamp` | 35 |

### Signatures des méthodes fréquentes

```java
// Construire la PK
preparePk(String indicePK, String nomSequence)
makePK(Connection c) : String
makePK() : String

// Charger un objet
getById(String id, String nomTable, Connection c) : ClassMAPTable

// Écrire
createObject(String u, Connection c) : ClassMAPTable
updateObject(String u, Connection c) : int
deleteToTableWithHisto(String u, Connection c) : int

// Valider
validerObject(String u, Connection c) : Object
validerObjectMultiple(ClassEtat[] objs) : void

// Historique
insertToTableWithHisto(String u, Connection c) : int
updateToTableWithHisto(String u, Connection c) : int

// Filles
getFille() : ClassMAPTable[]
setFille(ClassMAPTable[] filles) : void
getFille(String nomTable, Connection c, String aWhere) : ClassMAPTable[]

// XML / JSON / CSV
toJson() : String
toJsonStandard(ClassMAPTable[]) : String
appendCsv(CSVPrinter, String[]) : void
saveCsv(String filename, String[] entetesAffiche, String[] entete) : void
```