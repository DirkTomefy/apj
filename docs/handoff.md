

# HANDOFF — Projet `projet-standard` (module `a_tahina` : Poussins, Facturation/Avoirs, Calendrier, Générateur)

## 1. Contexte du projet

**Objectif.** Application ERP Java de BICI Madagascar (dépôt GitLab `bici-madagascar/projet-standard`, copie de travail `…/akoho/projet-standard - Copie`). Le travail de ces sessions porte sur le sous-ensemble **`a_tahina`** (responsable fonctionnel : « MR TAHINA », rôle `resp`), qui gère une **filière avicole** :
- commande de poussins (bon de commande), réception, mise en compartiment, lots, déclaration de pertes ;
- **calendrier de prévision** de naissances sur 3 générations (poules pondeuses → poussins), avec CA et bénéfice ;
- **facturation clients avec avoirs** (retours de marchandise, crédit déduit des factures suivantes avec plafond) ;
- un **générateur de code** (`apj_gen.py`) qui produit SQL, ORM, FH et JSP pour de nouveaux modules.

**Public / cas d'usage.** Utilisateurs métier de la ferme (saisie et suivi), le développeur (`DirkTomefy`, `dirktomefy@gmail.com`) qui crée de nouveaux écrans, et une collègue qui doit pouvoir installer la base facilement (`data/final/final.sql`).

**Type de projet.** Application web Java EE multi-modules (EJB + WAR, pages JSP) sur le framework interne **APJ**, avec base Oracle.

**Stack.**
- Java **8** (Temurin 1.8.0_504 ; compilation `-source 8 -target 8`), JSP, WildFly **10** (le `Dockerfile` installe 10.1.0.Final ; le dépôt contient aussi un WildFly 10.0.0.Final dans `build-file/E_/…`).
- Oracle **11g** dans un conteneur **podman** (`tomefy-oracle-11-sql`, ports 1521 et 8085), schéma `ARBIOCHEMSTANDARD`, service `EE.oracle.docker`.
- Framework **APJ** : uniquement des jars compilés, **sans sources** : `build-file/lib/apj-core.jar` (214 classes : `affichage`, `bean`, `utilitaire`, `constante`…) et `apj-core2.jar` (151 classes : `utilisateur`, `menu`, `web`, `historique`…). Décompilation avec CFR : `java -jar ~/Documents/cfr.jar <jar> --outputdir <dossier scratch>` (**jamais dans le projet**).
- Build : Ant (`build.xml`, cibles `clean, init, compile, buildEjbJar, compileWar, copieProperties, deploy`). Lib dans `build-file/lib/*.jar` (ojdbc6-11, jasperreports, poi, itext…).
- Python 3 pour `apj_gen.py` (stdlib seule).
- Docs APJ navigables : `docs/apj-doc/index.html` (API des jars) et `docs/00-index.md` à `docs/15-generateur.md` (guide d'utilisation du framework).

## 2. État du projet : TERMINÉ ✅ (avec réserves)

### Livré
1. **Module Poussins** : bons de commande (maître/fille), bons de réception (cumuls, pertes, date d'éclosion), insertion en compartiment (crée les lots), déclaration de pertes, situation des compartiments.
2. **Refactoring de l'affichage** : classes génériques `Atahina*` + DSL `champ("nom","Libellé")…` ; FH par entité héritant de `GeneraticFH`.
3. **Calendrier maintenable** : `Calendrier<T>` générique, rendu CSS par classes, vues SQL paramétrées (`V_CAL_PARAM`).
4. **Facturation / Avoirs** : clients, produits, factures (TVA par ligne), avoirs partiels, crédit déduit au visa avec **plafond par client** (`ATH_CLIENT.pourcentageavoir`, 40 % par défaut).
5. **SQL final unique** `data/final/final.sql` (+ `drop.sql`, `exemple-facture.sql`, `correctif/correctifs-base-existante.sql`) créant aussi le rôle `resp`, l'utilisateur `resp`, le menu.
6. **Générateur** `apj_gen.py` + `spec.json` (syntaxe française) + `docs/15-generateur.md`.
7. Historique SQL `data/v1, v2, v2-fix, v3` **rejouable** dans l'ordre (même structure que `final.sql`).

### Vérifié (et comment)
Aucune suite de tests n'existe dans le dépôt. Les vérifications ont été faites avec des **harness jetables** (perdus : ils étaient dans le scratchpad), à reconstruire au besoin (voir §6).
- **Compilation** `javac` de tout `a_tahina` : OK.
- **ORM ↔ base** : pour les 30 classes ORM `a_tahina`, attributs déclarés = colonnes (dans les deux sens), sur la vraie base et sur un schéma neuf installé par `final.sql`.
- **Flux métier Java sur Oracle** (transaction annulée) : BC → BR → insertion compartiment → lots → calendrier → situation ; facture → avoir → déduction plafonnée. Cas de ton exemple à 40 % : vente 2 de 200 000 → déduit 80 000 (solde 20 000) ; vente 3 de 100 000 → déduit 20 000 (solde 0).
- **Pages (côté Java)** : toutes les listes, saisies, fiches et onglets des FH (appel direct, requête/session simulées).
- **Connexion réelle** : `historique.UtilisateurUtil.testeValide("resp","Resp2026")` accepté, mauvais mot de passe refusé.
- **SQL** : `final.sql` sur schéma jetable : 0 erreur, relance refusée, `drop.sql` complet, réinstallation OK. Chaîne historique rejouée : 0 erreur.
- **Générateur** : module `stock` généré (23 fichiers) : SQL installé/désinstallé, Java compilé, validations, maître/détail, pages et ORM↔colonnes OK ; protection des fichiers modifiés (`@generated`), `--apercu`, erreurs de spec.

### NON vérifié (à ne pas supposer)
- **Aucun déploiement sur WildFly**, aucun `ant deploy` lancé pendant ces sessions. Les **JSP n'ont jamais été exécutées** dans un conteneur de servlets (seules leurs FH l'ont été) ; leur syntaxe copie des JSP existantes.
- **Cases à cocher des saisies maître/détail** (`ids`) : non testées en navigateur.
- La base de dev `ARBIOCHEMSTANDARD` n'a **pas** reçu le correctif `correctifs-base-existante.sql` (voir §7).

### Environnement
Local uniquement : podman (Oracle), WildFly local. Pas d'URL publique. Branche `master`, dépôt propre, ~96 commits, PR fusionnées jusqu'à `#7` (`apjgen-fix`).

**Accès application après installation SQL** : login `resp` / mot de passe `Resp2026` (à changer).

## 3. Architecture & fichiers clés

### Arborescence commentée
```
projet-standard - Copie/
├── apj_gen.py                      Générateur (SQL, ORM, FH, JSP) ; voir docs/15-generateur.md
├── spec.json                       Exemple de spec (module "stock")
├── import_sql.sh                   ./import_sql.sh <fichier.sql> : exécute via podman exec sqlplus (stdin)
├── build.xml, Dockerfile, build-*.xml   Build Ant / image WildFly
├── build-file/lib/                 Jars : apj-core.jar, apj-core2.jar, ojdbc6-11.jar, …
├── build-file/arbiochem_jar/apj.properties   Connexion JDBC (url, user, password, apj.debug)
├── docs/                           Guide du framework (00-index … 15-generateur) + apj-doc/ (API navigable)
├── data/
│   ├── final/                      ★ INSTALLATION : final.sql, drop.sql, exemple-facture.sql, README.md,
│   │                               correctif/correctifs-base-existante.sql
│   ├── v1, v2, v2-fix, v3/         Historique rejouable (ordre dans data/README.md)
│   ├── deprecated/, drop.sql       Obsolètes (drop.sql porte la mention OBSOLETE)
│   └── gen/<module>/               Sortie SQL du générateur (n'existe pas tant qu'il n'a pas tourné)
├── arbiochem-ejb/src/java/
│   ├── a_tahina/
│   │   ├── core/GeneraticFH.java          Base des FH : ecrans par defaut nonSupporte, helpers liste/consulte/onglet
│   │   ├── core/page/                     AtahinaPageRecherche|Consulte|Insert|InsertMultiple,
│   │   │                                  AtahinaChampHelper (DSL champ(...)), AtahinaFormulaire, AtahinaUtil
│   │   ├── core/calendrier/Calendrier.java, CalendrierHtml.java ; prevision/calendrier/CalendrierPoussin.java
│   │   ├── bc|br|ic|lot|compartiment|fournisseur|prevision|facture/  chacun avec orm/ et (sauf lot, fournisseur) fh/
│   │   └── facture/orm/AthAvoirService.java   Règle du crédit plafonné
│   ├── user/UserEJBBean.java              Session utilisateur (code de l'application de base)
│   └── resources/text*.properties         Bundles i18n (requis par PageRecherche)
└── arbiochem-war/web/
    ├── pages/a_tahina/v1/{bc,br,ic,compartiment,facture/{client,produit,facture,avoir},prevision}/   JSP + inc/
    ├── pages/{module,apresTarif,apresMultiple}.jsp   Pages de l'application de base (routage, actions génériques)
    └── assets/css/a_tahina/calendrier.css            Couleurs du calendrier (variables CSS)
```

### Rôle des classes clés
- **ORM** : une classe par table (`extends ClassMAPTable | ClassEtat | ClassMere | ClassFille`) et une classe `…Lib` par vue (mêmes colonnes + libellés calculés). Convention **`Xxx` = table, `XxxLib` = vue `XXX_LIB`**.
- **FH** (`…/fh/XxxFH.java extends GeneraticFH`) : configure les écrans en déclarant les champs ; **ne contient aucune logique métier**.
- **JSP** : fines (≈10 lignes) ; instancient `new XxxFH().getPageXxx(request, session)` et affichent.
- **Logique métier** : dans les ORM (`controler`, `createObject`, `validerObject`, `controlerAnnulationVisa`) et `AthAvoirService`.

### Schéma de la base (module)
Colonnes **sans underscore** (= attributs de l'ORM) ; identifiants `prefixe + 6 chiffres` via `GETSEQxxx`.
- Dictionnaires : `SANTEPOUSSIN`, `LIEURECEPTION`, `COMPARTIMENT(id, nom, capacite)`, `ETAT(id,val,desce)` (table commune).
- `CONFIG_PREVISION(id, idsouche, semaine, nbjour)` : nombre de pontes par semaine et par souche (`UNIQUE(idsouche,semaine)`).
- `BONCOMMANDE_POUSSIN(id,daty,reference,descri,idmagasin,etat,remarque)` ← `BONCOMMANDE_POUSSIN_FILLE(id,idmere,idproduit,idsouche,qte,pu,fournisseur)`.
- `BONRECEPTION_POUSSIN(id,daty,reference,idboncommande,idboncommandefille,idbrprecedent,idlieureception,idmagasin,etat,qtavantcumule,qtaprescumule,maty,very,remarque,dateeclosion)`.
- `INSERTION_COMPARTIMENT(id,daty,reference,idbonreception,etat,remarque)` ← `INSERTION_COMPARTIMENT_FILLE(id,idmere,idcompartiment,qte,pu,valeurcompartiment,sexe,idsante)`.
- `LOT(id,idbonreception,idproduit,idsouche,qteinit,pu,sexe,idsante,idinsertcompfille,idboncommande,qteactuelle,dateeclosion)`.
- `DECL_PERTE_LOT(id,sexe,qtemort,qtevery,daty,reference,remarque,idcompartiment)` (+ `FK_DPL_COMP` dans `final.sql`).
- Facturation : `ATH_CLIENT(…, pourcentageavoir)`, `ATH_PRODUIT`, `ATH_FACTURE(…, montantavoir)` ← `ATH_FACTURE_FILLE(…, tva)`, `ATH_AVOIR(…, idfacture, idclient)` ← `ATH_AVOIR_FILLE(idfacturefille, qte, pu, tva)`, `ATH_AVOIR_USAGE(idavoir, idfacture, montant)` (traçabilité de la consommation du crédit).
- Colonnes ajoutées à `SOUCHE` (table de l'application de base) : `NB_JOURS_ECLOSION`, `DUREE_VIE_SEMAINES`, `SEMAINE_DEBUT_PONTE`, `TAUX_ECLOSION`, `RATIO_FEMELLE` (**ce sont les seules colonnes avec underscore** ; elles appartiennent à `ferme.configuration.Souche`, pas à notre ORM).
- Vues : 10 `*_LIB`/`SITUATION_COMPARTIMENT`, 16 vues calendrier (`V_CAL_*`, `CALENDRIER_POUSSIN_RAW`, `CALENDRIER_POUSSIN_CUMUL`), 5 vues facture (`ATH_*_LIB`, `ATH_CLIENT_SOLDE`).
- États : `1` créé, `11` visé, `0` annulé, `9` clôturé. Sexe : `'0'` femelle, `'1'` mâle.

### Routes / URL
- Ouverture d'une page : `module.jsp?but=<chemin relatif à pages/>[&id=…][&tab=inc/xxx]`. Ex : `module.jsp?but=a_tahina/v1/facture/facture/facture-fiche.jsp&id=AFC000001`.
- Actions génériques : `apresTarif.jsp?acte=insert|update|delete|valider|annuler|annulerVisa&classe=<classe ORM>&bute=<page de retour>` (objet simple) ; `apresMultiple.jsp` avec `classe`, `classefille`, `nombreLigne`, `colonneMere=idmere`, `nomtable` (maître/détail).
- Création d'un avoir : `…/avoir/avoir-saisie.jsp` (étape 1 : choix d'une facture **visée** ; étape 2 : `?idfacture=…`).

### Flux principaux
1. **Écran liste** : JSP → `FH.getPageRechercheForList` → `AtahinaPageRecherche` (critères, intervalles, colonnes déduits de la déclaration `champ(...)`) → `CGenUtil.rechercher` sur la **vue** `_LIB`.
2. **Saisie** : JSP → FH → `AtahinaPageInsert[Multiple]` ; POST vers `apresTarif/apresMultiple` → `ORM.createObject` (qui appelle `controler`, `construirePK`, insère mère + filles, écrit `HISTORIQUE`).
3. **Visa facture** : `apresTarif acte=valider` → `AthFacture.validerObject` → `AthAvoirService.appliquer` (verrouille le client `FOR UPDATE`, consomme les avoirs visés du plus ancien au plus récent, écrit `ATH_AVOIR_USAGE`, fixe `montantavoir`). **Annuler le visa** → `controlerAnnulationVisa` → `restituer`.
4. **Calendrier** : `prevision-poussin.jsp` → `PrevisionPoussinFH.getCalendrier` → `CalendrierPoussin` → `CGenUtil.rechercher` sur `CALENDRIER_POUSSIN_CUMUL` → `CalendrierHtml` (classes CSS, aucune couleur en Java).
5. **Connexion** : mot de passe saisi chiffré par `Utilitaire.cryptWord(mot, niveau, sens)` avec la ligne `PARAMCRYPT` de l'utilisateur, comparé à `UTILISATEUR.PWDUSER`, via la vue `UTILISATEURVALIDE`.

## 4. Décisions techniques & justifications

**Architecture**
- **FH par entité + DSL déclaratif** (`champ("daty","Date").intervalle()`). *Pourquoi* : les FH d'origine avaient une dizaine de méthodes statiques répétitives ; retirer un champ = supprimer une ligne. Un champ non déclaré est **caché mais posté** (valeur par défaut de l'objet : ex `etat = 1`) en saisie/fiche ; en lignes de détail il **n'est pas posté** (le modèle le calcule, ex `pu`, `tva` d'un avoir).
- **Ordre d'application automatique** (`AtahinaPageInsert*.preparer()`) : listes → chargement → libellés/défauts/masquage/ordre. *Pourquoi* : `Formulaire.changerEnChamp` **remplace** le champ et fait perdre libellé/défaut posés avant.
- **Un ORM par table + un `Lib` par vue**, avec les champs de vue **uniquement** dans le `Lib`. *Pourquoi* : règle de l'utilisateur « attributs ORM = colonnes de la table ».
- **Pas de SQL manuel** : tout passe par `CGenUtil.rechercher(objet, colInt, valInt, [connexion,] apresWhere)` ou les vues. Filtres dynamiques via `AtahinaUtil.egal/dans/dansSelect` (échappement `'`→`''`, identifiants validés). Exceptions assumées dans `AthAvoirService` (`DELETE …USAGE`, `SELECT … FOR UPDATE`) car le framework n'offre ni suppression ciblée ni verrou.
- **Règle métier du crédit dans un service** (`AthAvoirService`) et **dans les vues** (`ATH_AVOIR_LIB.reste`, `ATH_FACTURE_LIB.plafondavoir`) : le plafond et le solde se lisent depuis la base, le service ne fait que consommer.
- **Calendrier** : calcul lourd en **vues Oracle** (rapide, testable en SQL), Java ne fait que lire et colorer. Constantes uniquement dans `V_CAL_PARAM`/`V_CAL_SOUCHE`.
- **Couleurs** dans `assets/css/a_tahina/calendrier.css` (variables `--cal-*`), états déclarés par `style("classe","légende", j -> condition)` (**la première règle vraie gagne**).
- **SQL d'installation unique** plutôt que la chaîne d'historique : la chaîne contenait des renommages faits à la main hors scripts (ex `LOT.qte` → `qteinit`). Elle a été réparée et reste équivalente, mais `final.sql` est la référence.

**Librairies / outils**
- CFR pour décompiler APJ (pas de sources). Python stdlib pour le générateur (pas de Jinja : gabarits simples avec marqueurs `@FH@`).
- `UNISTR('Cr\00e9\00e9')` pour les accents en base (le client SQL*Plus est ASCII).

**Règles à ne PAS casser**
1. **Noms de colonnes = attributs de l'ORM**, insensibles à la casse, **les underscores comptent** (`qteinit` ≠ `qte_init`). Le générateur l'impose.
2. Toute modification SQL doit être **signalée à l'utilisateur avant** (il l'a demandé) ; Java/JSP peuvent être corrigés directement.
3. Scripts SQL : `SET DEFINE OFF` ; jamais de `PROMPT … -` (tiret final = continuation, avale la ligne suivante) ; pas de commentaire après un `DEFAULT` (Oracle le garde dans la valeur par défaut) ; ASCII seulement dans les scripts (accents via `UNISTR` ou `&eacute;`).
4. Ne jamais écrire un mot de passe en clair dans `UTILISATEUR.PWDUSER` : il doit être **pré-chiffré** et accompagné d'une ligne `PARAMCRYPT`.
5. Les attributs/champs `etat`: **un int `etat` propre à chaque sous-classe** (`getEtat/setEtat`) comme dans les ORM existants.

**Pièges rencontrés**
- `SQLBLANKLINES` : un `SELECT` avec ligne vide coupe la commande → `SET SQLBLANKLINES ON`.
- `&eacute;` sans `DEFINE OFF` devient une variable de substitution (en base : « Bons de r;ception »).
- `CGenUtil.makeWhereIntervalle` n'accepte les bornes que au format **`dd/MM/yyyy`** (sinon `AND 1>2`, aucune ligne).
- `DECL_PERTE_LOT` utilise `QTEMORT` (pas `qtematy`) : `qtematy` est l'étape intermédiaire de l'historique.
- Le framework écrit l'historique par `GETSEQHISTORIQUE`/`HISTORIQUE` et lit les colonnes par la vue **`listeColonneTable`** : un schéma de test les exige.
- Exécution `sqlplus` : `UNISTR` seulement si la base est AL32UTF8 (elle l'est).
- Sécurité : la protection de Claude Code **refuse `rm -rf $VARIABLE`** : générer dans un dossier neuf plutôt que supprimer.

## 5. Conventions & style de code

- **Java** : classes ORM compactes (getters/setters sur une ligne, `@Override public String getAttributIDName() { return "id"; }`), packages `a_tahina.<module>.orm` et `a_tahina.<module>.fh`, FH nommés `XxxFH`, vues `XxxLib`, constantes `DOSSIER`, `PAGE_LISTE`, `PAGE_FICHE`, `PAGE_SAISIE`, `CLASSE` (`CLASSE_FILLE`, `NOMBRE_LIGNE` pour un maître).
- **Commentaires** : l'utilisateur a retiré les Javadoc/commentaires explicatifs (commit « no comment »). Ne garder que les **séparateurs de section** existants ; ne pas en ajouter. Le code généré porte une seule ligne `// @generated apj_gen.py (…)`.
- **Labels dans les FH/JSP** : entités HTML (`R&eacute;f&eacute;rence`) ou UTF-8 (sources compilés en UTF-8).
- **Noms SQL** : tables en MAJUSCULES, colonnes minuscules sans underscore, séquence `SEQ_<TABLE>`, fonction `GETSEQ<TABLE>`, contraintes `ck_/fk_/idx_<prefixe>_<champ>` (≤ 30 caractères), vues `<TABLE>_LIB`, menus `MENU<MODULE>[_<ENTITE>[_ADD|_LIST]]`.
- **Commits** : `[feat] : message en français` ou `[fix] : message`, auteur `DirkTomefy`, travail par branche + PR fusionnée (`Merge pull request #N from DirkTomefy/<branche>`). Ajouter la ligne `Co-Authored-By` demandée par le harnais. **Ne committer que si l'utilisateur le demande.**
- **Réponses attendues** : **français**, ton direct. Le style « caveman » (très concis, réponse d'abord) est activé par un hook de session ; exceptions en prose complète : avertissements de sécurité, actions irréversibles, ordres d'étapes, **tout ce qui est persisté** (code, commentaires, docs, commits). Toujours signaler d'abord les problèmes SQL, vérifier avant de déclarer « fait », et dire clairement ce qui n'a pas été testé.
- Lorsque tu produis un document pour quelqu'un d'autre (ex : script pour la collègue), indiquer l'audience visée en début de message.

## 6. Installation & lancement (reproductible de zéro)

**Prérequis** : Linux, Java 8, Ant, podman, Python 3, WildFly 10, image `oracle11-backup:latest` (conteneur `tomefy-oracle-11-sql`). Les scripts Oracle sont dans `~/.oracle-11/bin/` (**le dossier s'appelle `.oracle-11`, avec un tiret**) : `tomefy-oracle-11-start|stop|logs|bash|sysdba|init|reset-password`. Variables dans `~/.oracle-11/.env` : `CONTAINER_NAME=tomefy-oracle-11-sql`, `CONTAINER_PORT=1521`, `HOST_PORT=1521`, `DATA_DIR=$HOME/.oracle-11/data`, `IMPORT_DIR=$HOME/.oracle-11/import`, `ORACLE_WEB_MANAGER_PORT=8085`.

**Variables / configuration** (pas de `.env` applicatif ; la config est dans des fichiers) — équivalent `.env.example` :
```
# build-file/arbiochem_jar/apj.properties  (copie dans arbiochem-ejb/src/java/apj.properties)
apj.connection.url=jdbc:oracle:thin:@127.0.0.1:1521:EE
apj.connection.user=arbiochemstandard
apj.connection.password=arbiochemstandard
apj.debug=true
```
`import_sql.sh` contient en dur : `CONTAINER=tomefy-oracle-11-sql`, `DB_USER=ARBIOCHEMSTANDARD`, `DB_PASS=arbiochemstandard`, `DB_HOST=127.0.0.1`, `DB_PORT=1521`, `DB_SERVICE=EE.oracle.docker`. *(Identifiants de dev committés : ne pas réutiliser en production.)*

**Installer la base**
```bash
tomefy-oracle-11-start                         # démarre le conteneur
./import_sql.sh data/final/final.sql           # installe tout (tables, vues, menu, rôle/utilisateur resp)
# réinstaller de zéro :  ./import_sql.sh data/final/drop.sql  puis  final.sql
# optionnel :           ./import_sql.sh data/final/exemple-facture.sql
# base de dev déjà installée par les anciens scripts : ./import_sql.sh data/final/correctif/correctifs-base-existante.sql
```
Prérequis SQL (contrôlés par le script) : tables `SEXE, MAGASIN2, AS_INGREDIENTS, FOURNISSEUR, SOUCHE, MENUDYNAMIQUE, USERMENU, ROLES, UTILISATEUR, UTILISATEURVALIDE, PARAMCRYPT`, séquences `SEQUSERMENU, SEQ_UTILISATEUR`, fonction `GETSEQPARAMCRYPT`.

**Compiler (vérification rapide, sans Ant)**
```bash
CP=$(ls build-file/lib/*.jar | tr '\n' ':')
javac -encoding UTF-8 -source 8 -target 8 -nowarn -cp "$CP" -sourcepath arbiochem-ejb/src/java \
  -d /tmp/out $(find arbiochem-ejb/src/java/a_tahina -name '*.java')
```
**Build / déploiement** : `ant deploy` (cible finale de `build.xml`, enchaîne clean → compile → EJB jar → WAR → copie des properties → déploiement). **Non exécuté pendant ces sessions** : vérifier les chemins de `build.xml` avant. Puis **redémarrer WildFly** (menus et rôles sont mis en cache).

**Tests** : pas de suite. Pour valider une modification :
1. Compiler (ci-dessus).
2. Si SQL changé : créer un **schéma Oracle jetable** (`CREATE USER` via `sys/oracle as sysdba` dans le conteneur), y copier les tables externes minimales (`SEXE, MAGASIN2, …`, `MENUDYNAMIQUE`, `USERMENU`, `ETAT`, `HISTORIQUE` + `SEQ_HISTORIQUE` + `GETSEQHISTORIQUE`, et la vue `LISTECOLONNETABLE` = `select 'a' as owner, ua.* from USER_TAB_COLUMNS ua`), y jouer le SQL, puis `DROP USER … CASCADE`.
3. Harness Java : `javac` avec `-sourcepath arbiochem-ejb/src/java`, classpath contenant `build-file/arbiochem_jar` (apj.properties **et** fichiers de conf), `arbiochem-ejb/src/java/resources` (bundle `text`) et un dossier avec un `apj.properties` pointant le schéma jetable **placé en premier** ; `HttpServletRequest`/`HttpSession` simulés par `java.lang.reflect.Proxy` (`getSession` renvoie la session ; `getValue("u")` = `new user.UserEJBBean()` ; `getValue("lien")` = `"module.jsp"`) ; utilisateur d'historique `"1"` (numérique obligatoire).
4. Contrôle ORM↔base : comparer `bean.ListeColonneTable.getFieldListeHeritage(obj)` (champs déclarés dans `a_tahina.*`, hors statiques) à `user_tab_columns`, dans les deux sens.

## 7. Maintenance & points de vigilance

**Dépendances sensibles** : jars APJ **sans sources** (toute évolution du framework = décompiler) ; `ojdbc6-11.jar` ; Java 8 (pas de syntaxe plus récente) ; Oracle 11g (pas de `FETCH FIRST`, pas d'identité, `LISTAGG` ok).

**Zones à ne pas toucher sans précaution**
- `data/final/final.sql` : généré à partir de la base réelle ; `data/v1…v3` : équivalents ; garder les deux en phase si le schéma change.
- `AthAvoirService` + `AthFacture.validerObject/controlerAnnulationVisa` + `AthAvoir.controler` : cohérence crédit/usages.
- `Calendrier*` et vues `V_CAL_*` (équivalence vérifiée par `MINUS` avec l'ancienne version).
- `core/page/*` : tous les écrans en dépendent.
- Aucun fichier du framework APJ n'est modifiable (jars).

**Limitations connues (assumées ou non traitées)**
- **Base de dev `ARBIOCHEMSTANDARD` non corrigée** : accents de `ETAT.desce` corrompus (`??`), libellés de menu « Bons de r;ception » / « Saisir une r;ception », défaut de `ATH_CLIENT.pourcentageavoir` contenant un commentaire, `FK_DPL_COMP` et `IDX_LOT_SEXE_DATY` à ajouter. → correctifs prêts dans `data/final/correctif/`, **non exécutés**.
- **Vues invalides** dans la base de dev, non utilisées : `BCPF_VIVANTS_LIB`, `DECL_PERTE_LOT_LIB`, `CONFIG_PREVISION_LIB` (volontairement absentes de `final.sql`).
- **Menus orphelins** en base : `MENU_TEST*`, `MENUATAHINA*` (pages `a_tahina/v1/test/…` inexistantes).
- **Boutons « Modifier »** de la fiche BC/BR pointent vers `boncommande-modif.jsp` / `bonreception-modif.jsp`, **qui n'existent pas**.
- **Page de saisie du BC ≈ 30 Mo de HTML** : chaque ligne embarque la liste complète des ingrédients (comportement du framework). Piste : autocomplete (`docs/14-autocomplete.md`).
- **Cumuls de BR** : les BR annulés (`etat = 0`) comptent encore dans le « BR précédent » (question métier non tranchée). Les avoirs non annulés bloquent les quantités retournables.
- Calendrier : le filtre de dates repose sur du texte `dd/MM/yyyy` (format de date de session Oracle).
- **Chiffrement des mots de passe** très faible (décalage de caractères, insensible à la casse : `Resp2026` ≡ `resp2022`) — c'est le système de l'application de base. Identifiants de base en clair dans le dépôt.
- Le générateur ne produit pas de règles métier complexes ni de pages de modification.

**TODO / refactors optionnels**
- Exécuter `correctifs-base-existante.sql` sur la base de dev.
- Compléter `README.md` racine (encore le gabarit GitLab).
- Retirer les menus orphelins ; supprimer les trois vues invalides.
- Pages de modification (`*-modif.jsp`) ou retirer le bouton.

## 8. Pistes d'évolutions futures
- Autocomplete sur les listes d'articles/ingrédients (gain de taille de page majeur).
- Pages de modification pour BC/BR/IC/facture/avoir ; avoir total « en un clic » (écarté pour l'instant) ; paiement des factures (reste après avoir) ; stock lié aux avoirs (non géré).
- Calendrier : autres calendriers via `Calendrier<T>` (il suffit d'une vue + déclarations) ; filtre par souche.
- Générateur : champs calculés, validations personnalisées, pages de modification, génération de tests ; `--apercu` du SQL contre une base.
- Tests automatisés (le harness jetable pourrait devenir un vrai module de tests).
- Points d'extension existants : `GeneraticFH` (méthodes à implémenter), `Calendrier<T>` (`valeur/total/indicateur/indicateurSigne/style`), `AtahinaChampHelper` (nouvelles options de champ), `V_CAL_PARAM` (constantes), `PARAMCRYPT`.

## 9. Snippets critiques

**1. Importer un script SQL (exécution via stdin, pas de `podman cp`)** — `import_sql.sh` :
```bash
podman exec -i "${CONTAINER}" \
    sqlplus -S "${DB_USER}/${DB_PASS}@//${DB_HOST}:${DB_PORT}/${DB_SERVICE}" \
    < "$FILE"
```

**2. Déclarer un écran maître/détail** (`facture/fh/FactureFH.java`) — chaque ligne `champ(...)` = un champ ; l'ordre est celui de l'écran :
```java
return new AtahinaPageInsertMultiple(new AthFacture(), new AthFactureFille(), request, session, nombreLigne)
    .champs(
        champ("daty",      "Date").defautAujourdhui(),
        champ("reference", "R&eacute;f&eacute;rence"),
        champ("idclient",  "Client").liste(new AthClient(), "nom", "id"),
        champ("remarque",  "Remarque"))
    .colonnes(
        champ("idproduit", "Produit").liste(new AthProduit(), "libelle", "id", " and actif = 1"),
        champ("qte",       "Qt&eacute;").defaut("1"),
        champ("pu",        "P.U. (0 = prix du produit)").defaut("0"),
        champ("tva",       "TVA %").defaut("0"))
    .preparer();
```

**3. Un calendrier = des déclarations** (`prevision/calendrier/CalendrierPoussin.java`) :
```java
super(request, new CalendrierPoussinCumulJour(), "daty");
valeur(CalendrierPoussinCumulJour::getPoussinscumules);
total("NAISSANCES", CalendrierPoussinCumulJour::getPoussins);
indicateur("CA", CalendrierPoussinCumulJour::getCa);
indicateurSigne("BENEFICE", CalendrierPoussinCumulJour::getBenefice);
style("cal-perte",     "Perte déclarée",   j -> j.getPertes() > 0);   // 1re règle vraie gagne
style("cal-naissance", "Naissances",       j -> j.getPoussins() > 0);
style("cal-vide",      "Aucune naissance", j -> true);
```
Les couleurs sont les variables `--cal-perte-fond`… de `arbiochem-war/web/assets/css/a_tahina/calendrier.css`.

**4. Règle de l'avoir plafonné** (`facture/orm/AthAvoirService.java`) — montant déduit = min(plafond, solde), consommé du plus ancien avoir au plus récent :
```java
public static double montantDeduit(double plafond, double solde) {
    return arrondi(Math.max(0, Math.min(plafond, solde)));
}
// appliquer(...) : plafond = f.getPlafondavoir() (= TTC x pourcentageavoir / 100, vue ATH_FACTURE_LIB)
//                  solde   = somme des 'reste' des ATH_AVOIR_LIB visés du client, triés daty, id
```

**5. Création du compte de connexion** (`data/final/final.sql`, section 7) — mot de passe pré-chiffré + ligne `PARAMCRYPT` obligatoire :
```sql
INSERT INTO UTILISATEUR (REFUSER, LOGINUSER, PWDUSER, NOMUSER, ADRUSER, TELUSER, IDROLE)
SELECT SEQ_UTILISATEUR.NEXTVAL, 'resp', 'naol2022', 'Responsable poussin et facturation', NULL, NULL, 'resp'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM UTILISATEUR WHERE LOGINUSER = 'resp');
INSERT INTO PARAMCRYPT (ID, NIVEAU, CROISSANTE, IDUTILISATEUR)
SELECT 'CRY' || LPAD(GETSEQPARAMCRYPT, 6, '0'), 4, 1, TO_CHAR(u.REFUSER)
FROM UTILISATEUR u WHERE u.LOGINUSER = 'resp'
  AND NOT EXISTS (SELECT 1 FROM PARAMCRYPT p WHERE p.IDUTILISATEUR = TO_CHAR(u.REFUSER));
```
(`Utilitaire.cryptWord("Resp2026", 4, 1)` donne `naol2022`.)

**6. En-tête et garde des scripts SQL** (modèle de `final.sql` et du générateur) :
```sql
SET DEFINE OFF
SET SQLBLANKLINES ON
SET SERVEROUTPUT ON
SET FEEDBACK OFF
WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK
-- puis un bloc PL/SQL 'exiger(table, colonnes)' et un refus si le module est déjà installé
```

**7. Constantes du calendrier** (une seule vue à modifier) :
```sql
CREATE OR REPLACE VIEW V_CAL_PARAM AS
SELECT 2000 AS horizon_jours, 21 AS def_nb_jours_eclosion, 21 AS def_semaine_debut_ponte,
       85 AS def_taux_eclosion, 50 AS def_ratio_femelle, 72 AS def_duree_vie_semaines
FROM dual;
```

**8. Helpers de FH** (`core/GeneraticFH.java`) — l'ossature d'un FH :
```java
protected static AtahinaPageRecherche liste(ClassMAPTable objet, HttpServletRequest request, HttpSession session,
                                            String pageActuelle, String titre, AtahinaChampHelper... champs) throws Exception {
    return new AtahinaPageRecherche(objet, request, session, pageActuelle, titre, champs).construire();
}
protected static String onglet(ClassMAPTable objet, HttpServletRequest request, HttpSession session,
        String pageFiche, String idMere, String tab, String colonneMere, String messageVide,
        AtahinaChampHelper... colonnes) throws Exception { /* tableau filtré par la mère */ }
```

**9. Un champ dans le `spec.json`** (générateur) :
```json
"categorie": { "libelle": "Catégorie", "reference": "Categorie" },
"magasin":   { "libelle": "Magasin",
               "reference": { "table": "MAGASIN2", "classe": "magasin.Magasin", "affiche": "val" } },
"pu":        { "libelle": "Prix", "type": "nombre", "defaut": 0, "min": 0 }
```
Commande : `python3 apj_gen.py spec.json --apercu` puis `python3 apj_gen.py spec.json`.

**10. Recherche par l'exemple avec intervalle de dates** (format `dd/MM/yyyy` obligatoire) :
```java
Object[] lignes = CGenUtil.rechercher(critere,
        new String[] { "daty" }, new String[] { "01/01/2026", "31/12/2026" }, " order by daty");
```

## 10. Instructions pour le prochain Claude

**Comportement**
- Répondre en **français**, direct, réponse d'abord, sans remplissage ; le hook « caveman » de session s'applique au chat, **pas** au code, aux docs ni aux commits (ceux-ci en prose normale).
- **Vérifier avant d'affirmer** : compiler, puis tester sur un schéma Oracle jetable ; dire explicitement ce qui n'a pas été testé (JSP, déploiement). L'utilisateur croit le projet « 0 bug » : ne le confirme pas, signale les réserves du §2 et §7.
- Pour tout nouveau module : **commencer par `spec.json` + `apj_gen.py`**, puis ne coder à la main que la logique métier (en retirant la marque `@generated` des fichiers modifiés).
- Ajouter du code dans le même style que l'existant (compact, sans commentaires explicatifs).

**À ne PAS faire**
- Ne pas modifier les jars APJ ni les sources de l'application de base sans demande.
- Ne pas exécuter de SQL destructif sur `ARBIOCHEMSTANDARD` (drop, truncate, correctifs) **sans accord explicite** ; ne pas modifier un fichier SQL de `data/` sans le signaler d'abord.
- Ne pas committer ni pousser sans demande ; ne pas créer de documents hors demande ; ne pas utiliser `rm -rf $VARIABLE`.
- Ne pas casser les règles du §4 (noms de colonnes, `DEFINE OFF`, `PROMPT … -`, chiffrement des mots de passe).
- Ne pas supposer que les harness de test existent encore : ils étaient dans un dossier temporaire.

**Questions à poser en début de session**
1. L'application a-t-elle été **déployée sur WildFly** depuis ? Les écrans (saisie maître/détail, avoir en deux étapes, calendrier) ont-ils été vus en navigateur ? Y a-t-il des retours ?
2. Le correctif `data/final/correctif/correctifs-base-existante.sql` a-t-il été appliqué à la base de dev (accents `ETAT`, libellés de menu, `FK_DPL_COMP`) ?
3. La collègue a-t-elle installé `final.sql` ? Sur quelle base (prérequis `PARAMCRYPT`, `ROLES`… présents) ?
4. Doit-on (a) ajouter des pages de modification, (b) traiter la page BC de 30 Mo (autocomplete), (c) trancher la question des BR annulés dans les cumuls, (d) recréer un exemple de spec pour l'ancien module `test` ?
5. Le mot de passe initial `resp` / `Resp2026` doit-il être changé ou le login renommé (par exemple `tahina`) ?
6. Veux-tu que je relance l'ensemble des vérifications (compilation, ORM↔base, flux métier) sur l'état actuel de `master` avant de commencer ?

---

Copie ce document dans un fichier HANDOFF.md à la racine du projet, puis colle-le au début de la nouvelle session.