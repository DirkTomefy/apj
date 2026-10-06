# 12 — Exemple complet de bout en bout

**Domaine fonctionnel** : *Livraison urbaine*.
Une **tournée de livraison** (maître) contient plusieurs **colis** (détail).

Ce document crée **toutes les classes et tous les JSP**, de A à Z.

## Principe : le patron d'extension

On n'instancie **jamais** directement `PageRecherche`, `PageConsulte`,
`PageInsertMultiple` ou `PageUpdateMultiple` dans les JSP.

À la place, on crée **une classe dédiée par écran**. Cette classe hérite de
la classe du framework et contient toute la configuration.

**Avantages :**

| | Avantage | Détail |
|---|---|---|
| 🎯 | Configuration centralisée | Colonnes, libellés, liens, JS : tout est dans une classe Java. |
| 📄 | JSP ultra-courts | 5 à 20 lignes, lisibles par un non-initié. |
| ♻️ | Réutilisable | La même page Java peut servir à plusieurs JSP (mobile, desktop, version simplifiée). |
| 🧪 | Testable | La configuration est du code Java, testable sans JSP. |
| 🔧 | Refactorable | Changer une colonne = 1 ligne Java, pas 15 JSP. |

## Sommaire

1. [Cahier des charges](#1-cahier-des-charges)
2. [Schéma SQL](#2-schéma-sql)
3. [Classes modèle](#3-classes-modèle)
4. [Classes « Page » étendues](#4-classes--page--étendues)
5. [JSP](#5-jsp-très-courts-grâce-aux-classes-étendues)
6. [Action personnalisée](#6-action-personnalisée-si-besoin)
7. [Menu](#7-menu)
8. [Arborescence](#8-récapitulatif-de-larborescence)
9. [Pourquoi ce patron ?](#9-pourquoi-ce-patron-)
10. [Points à vérifier avant de copier](#10-points-à-vérifier-avant-de-copier)

---

## 1. Cahier des charges

### 1.1. Tables

#### `LIVRAISON_URBAINE` (maître)

| Colonne | Type | Description |
|---|---|---|
| `id` | VARCHAR2(30) | PK, générée par séquence |
| `daty` | DATE | Date de la tournée |
| `livreur` | VARCHAR2(30) | FK vers `LIVREUR` |
| `vehicule` | VARCHAR2(30) | FK vers `VEHICULE` |
| `zone` | VARCHAR2(30) | Zone de livraison |
| `etat` | NUMBER(2) | 1 = créé, 11 = validé, 0 = annulé, 9 = clôturé |
| `remarque` | VARCHAR2(500) | Remarque libre |

#### `LIVRAISON_URBAINE_FILLE` (détail / colis)

| Colonne | Type | Description |
|---|---|---|
| `id` | VARCHAR2(30) | PK |
| `idmere` | VARCHAR2(30) | FK vers `LIVRAISON_URBAINE` |
| `destinataire` | VARCHAR2(100) | Nom du destinataire |
| `adresse` | VARCHAR2(200) | Adresse de livraison |
| `poids` | NUMBER(10,2) | Poids en kg |
| `frais` | NUMBER(15,2) | Frais de livraison |
| `statut` | VARCHAR2(20) | `A_LIVRER`, `LIVRE`, `REFUSE` |

### 1.2. Vues CPL

| Vue | Contenu |
|---|---|
| `LIVRAISON_URBAINE_CPL` | Maître + libellés (livreur, véhicule, zone, état) |
| `LIVRAISON_URBAINE_FILLE_CPL` | Détail + libellés (statut en clair, montant) |

### 1.3. Écrans à produire

| # | Écran | Type | Classe Java |
|---|---|---|---|
| 1 | Liste | `PageRecherche` étendue | `LivraisonUrbaineListePage` |
| 2 | Fiche | `PageConsulte` étendue | `LivraisonUrbaineFichePage` |
| 3 | Saisie m/d | `PageInsertMultiple` étendue | `LivraisonUrbaineSaisiePage` |
| 4 | Modif m/d | `PageUpdateMultiple` étendue | `LivraisonUrbaineModifPage` |

---

## 2. Schéma SQL

```sql
-- Séquences
CREATE SEQUENCE GETSEQ_LIVRAISON_URBAINE         START WITH 1;
CREATE SEQUENCE GETSEQ_LIVRAISON_URBAINE_FILLE   START WITH 1;

-- Table maître
CREATE TABLE LIVRAISON_URBAINE (
    id           VARCHAR2(30) PRIMARY KEY,
    daty         DATE,
    livreur      VARCHAR2(30),
    vehicule     VARCHAR2(30),
    zone         VARCHAR2(30),
    etat         NUMBER(2) DEFAULT 1,
    remarque     VARCHAR2(500)
);

-- Table détail
CREATE TABLE LIVRAISON_URBAINE_FILLE (
    id              VARCHAR2(30) PRIMARY KEY,
    idmere          VARCHAR2(30),
    destinataire    VARCHAR2(100),
    adresse         VARCHAR2(200),
    poids           NUMBER(10,2),
    frais           NUMBER(15,2),
    statut          VARCHAR2(20),
    CONSTRAINT fk_lu_fille_mere
        FOREIGN KEY (idmere) REFERENCES LIVRAISON_URBAINE(id)
);

-- Vue maître avec libellés joints
CREATE OR REPLACE VIEW LIVRAISON_URBAINE_CPL AS
SELECT lu.*,
       l.nom             AS livreurlib,
       v.immatriculation AS vehiculelib,
       z.val             AS zonelib,
       CASE lu.etat WHEN 1  THEN 'Créé(e)'
                    WHEN 11 THEN 'Validé(e)'
                    WHEN 0  THEN 'Annulé(e)'
                    WHEN 9  THEN 'Clôturé(e)'
                    ELSE 'Autre' END AS etatlib
FROM   LIVRAISON_URBAINE lu
LEFT JOIN LIVREUR   l ON l.id = lu.livreur
LEFT JOIN VEHICULE  v ON v.id = lu.vehicule
LEFT JOIN ZONE      z ON z.id = lu.zone;

-- Vue détail avec libellés
CREATE OR REPLACE VIEW LIVRAISON_URBAINE_FILLE_CPL AS
SELECT luf.*,
       CASE luf.statut WHEN 'A_LIVRER' THEN 'À livrer'
                       WHEN 'LIVRE'    THEN 'Livré'
                       WHEN 'REFUSE'   THEN 'Refusé'
                       ELSE luf.statut END AS statutlib
FROM   LIVRAISON_URBAINE_FILLE luf;
```

---

## 3. Classes modèle

Package : `logistique`

### 3.1. `LivraisonUrbaine` — maître

Tournée de livraison urbaine.
C'est la classe **maître** : elle possède des colis (`LivraisonUrbaineFille`).

```java
package logistique;

import bean.ClassMere;
import java.sql.Connection;
import java.sql.Date;

/**
 * Tournée de livraison urbaine.
 * Classe maître : possède des colis (LivraisonUrbaineFille).
 */
public class LivraisonUrbaine extends ClassMere {

    private String id;
    private Date   daty;
    private String livreur;
    private String vehicule;
    private String zone;
    private int    etat = 1;
    private String remarque;

    public LivraisonUrbaine() {
        setNomTable("LIVRAISON_URBAINE");
        setLiaisonFille("idmere");
        setNomClasseFille("logistique.LivraisonUrbaineFille");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        preparePk("LU", "GETSEQ_LIVRAISON_URBAINE");
        setId(makePK(c));
    }

    @Override public String getTuppleID()       { return id; }
    @Override public String getAttributIDName() { return "id"; }

    @Override public String getNomClasseFille() { return "logistique.LivraisonUrbaineFille"; }
    @Override public String getLiaisonFille()   { return "idmere"; }

    // ---- Getters / Setters ----

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Date getDaty() { return daty; }
    public void setDaty(Date daty) { this.daty = daty; }

    public String getLivreur() { return livreur; }
    public void setLivreur(String livreur) throws Exception {
        if (getMode().equals("modif") && (livreur == null || livreur.isEmpty()))
            throw new Exception("Le livreur est obligatoire");
        this.livreur = livreur;
    }

    public String getVehicule() { return vehicule; }
    public void setVehicule(String vehicule) { this.vehicule = vehicule; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public int getEtat() { return etat; }
    public void setEtat(int etat) { this.etat = etat; }

    public String getRemarque() { return remarque; }
    public void setRemarque(String remarque) { this.remarque = remarque; }
}
```

### 3.2. `LivraisonUrbaineFille` — détail

Colis rattaché à une tournée de livraison.

```java
package logistique;

import bean.ClassFille;
import java.sql.Connection;

/**
 * Colis rattaché à une tournée de livraison.
 */
public class LivraisonUrbaineFille extends ClassFille {

    private String id;
    private String idmere;
    private String destinataire;
    private String adresse;
    private double poids;
    private double frais;
    private String statut;

    public LivraisonUrbaineFille() throws Exception {
        setNomTable("LIVRAISON_URBAINE_FILLE");
        setNomClasseMere("logistique.LivraisonUrbaine");
        setLiaisonMere("idmere");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        preparePk("LUF", "GETSEQ_LIVRAISON_URBAINE_FILLE");
        setId(makePK(c));
    }

    @Override public String getTuppleID()       { return id; }
    @Override public String getAttributIDName() { return "id"; }

    @Override public String getNomClasseMere() { return "logistique.LivraisonUrbaine"; }
    @Override public String getLiaisonMere()   { return "idmere"; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdmere() { return idmere; }
    public void setIdmere(String idmere) { this.idmere = idmere; }

    public String getDestinataire() { return destinataire; }
    public void setDestinataire(String destinataire) { this.destinataire = destinataire; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public double getPoids() { return poids; }
    public void setPoids(double poids) throws Exception {
        if (getMode().equals("modif") && poids <= 0)
            throw new Exception("Le poids doit être strictement positif");
        this.poids = poids;
    }

    public double getFrais() { return frais; }
    public void setFrais(double frais) { this.frais = frais; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
```

### 3.3. `LivraisonUrbaineCpl` — vue maître

Vue complétée du maître : elle ajoute les libellés joints.

> ⚠️ **Lecture seule** (liste, fiche). Ne jamais écrire dessus.

```java
package logistique;

/**
 * Vue complétée du maître : ajoute les libellés joints.
 * NE SERT QU'À LA LECTURE (liste, fiche). Ne jamais écrire dessus.
 */
public class LivraisonUrbaineCpl extends LivraisonUrbaine {

    private String livreurlib;
    private String vehiculelib;
    private String zonelib;
    private String etatlib;

    public LivraisonUrbaineCpl() {
        setNomTable("LIVRAISON_URBAINE_CPL");
    }

    public String getLivreurlib() { return livreurlib; }
    public void setLivreurlib(String v) { this.livreurlib = v; }

    public String getVehiculelib() { return vehiculelib; }
    public void setVehiculelib(String v) { this.vehiculelib = v; }

    public String getZonelib() { return zonelib; }
    public void setZonelib(String v) { this.zonelib = v; }

    public String getEtatlib() { return etatlib; }
    public void setEtatlib(String v) { this.etatlib = v; }
}
```

### 3.4. `LivraisonUrbaineFilleCpl` — vue détail

```java
package logistique;

public class LivraisonUrbaineFilleCpl extends LivraisonUrbaineFille {

    private String statutlib;
    private double montantTtc;

    public LivraisonUrbaineFilleCpl() throws Exception {
        setNomTable("LIVRAISON_URBAINE_FILLE_CPL");
    }

    public String getStatutlib() { return statutlib; }
    public void setStatutlib(String v) { this.statutlib = v; }

    public double getMontantTtc() { return montantTtc; }
    public void setMontantTtc(double v) { this.montantTtc = v; }
}
```

---

## 4. Classes « Page » étendues

> **Emplacement recommandé** : `src/java/logistique/affichage/`
> Ce package est séparé du modèle pour ne pas mélanger ORM et présentation.

| Classe | Hérite de | Rôle |
|---|---|---|
| `LivraisonUrbaineListePage` | `PageRecherche` | Écran LISTE |
| `LivraisonUrbaineFichePage` | `PageConsulte` | Écran FICHE |
| `LivraisonUrbaineSaisiePage` | `PageInsertMultiple` | Écran SAISIE m/d |
| `LivraisonUrbaineModifPage` | `PageUpdateMultiple` | Écran MODIF m/d |

### 4.1. `LivraisonUrbaineListePage extends PageRecherche`

Écran **LISTE** des tournées de livraison.
Il étend `PageRecherche` pour centraliser la configuration.

```java
package logistique.affichage;

import affichage.Liste;
import affichage.PageRecherche;
import bean.TypeObjet;
import logistique.LivraisonUrbaineCpl;
import user.UserEJB;

import javax.servlet.http.HttpServletRequest;

/**
 * Écran LISTE des tournées de livraison.
 * Étend PageRecherche pour centraliser la configuration.
 */
public class LivraisonUrbaineListePage extends PageRecherche {

    // ---- Configuration statique, réutilisable depuis n'importe quel JSP ----
    public static final String[] CRITERES         = {
        "id", "daty", "livreurlib", "zonelib", "vehiculelib"
    };
    public static final String[] INTERVALLES      = { "daty" };
    public static final String[] COLONNES         = {
        "id", "daty", "livreurlib", "vehiculelib", "zonelib", "etatlib"
    };
    public static final String[] COLONNES_AFFICHE = {
        "Id", "Date", "Livreur", "Véhicule", "Zone", "État"
    };

    private final HttpServletRequest request;

    public LivraisonUrbaineListePage(LivraisonUrbaineCpl f,
                                     HttpServletRequest request,
                                     UserEJB u,
                                     String lien) throws Exception {
        super(f, request, CRITERES, INTERVALLES, 3, COLONNES, COLONNES.length);
        this.request = request;
        configurer(f, u, lien);
    }

    private void configurer(LivraisonUrbaineCpl f, UserEJB u, String lien)
            throws Exception {

        // ---------- Entête ----------
        setTitre("Liste des tournées de livraison");
        setUtilisateur(u);
        setLien(lien);
        setApres("logistique/livraison-liste.jsp");
        setNpp(50);

        // ---------- Libellés du formulaire ----------
        getFormu().getChamp("livreurlib").setLibelle("Livreur");
        getFormu().getChamp("zonelib").setLibelle("Zone");
        getFormu().getChamp("vehiculelib").setLibelle("Véhicule");
        getFormu().getChamp("daty1").setLibelle("Date min");
        getFormu().getChamp("daty2").setLibelle("Date max");

        // ---------- Zone en Liste déroulante ----------
        TypeObjet zone = new TypeObjet();
        zone.setNomTable("ZONE");

        Liste[] liste = new Liste[1];
        liste[0] = new Liste("zonelib", zone, "val", "id");
        getFormu().changerEnChamp(liste);

        // ---------- Filtres additionnels via URL ----------
        StringBuilder awhere = new StringBuilder();
        String etat = request.getParameter("etat");
        if (etat != null && etat.matches("\\d+"))
            awhere.append(" and etat = ").append(etat);
        setAWhere(awhere.toString());

        // ---------- Exécution ----------
        creerObjetPage(COLONNES, null);

        // ---------- Tableau : liens + libellés ----------
        getTableau().setLien(new String[]{
            lien + "?but=logistique/livraison-fiche.jsp"
        });
        getTableau().setColonneLien(new String[]{ "id" });
        getTableau().setLibelleAffiche(COLONNES_AFFICHE);
        getTableau().setLienFille("logistique/inc/livraison-detail.jsp&id=");

        // ---------- Bouton « + Saisir » ----------
        getFormu().setAnotherButton(
            "<a class='btn btn-primary pull-right btn-small' " +
            "href='module.jsp?but=logistique/livraison-saisie.jsp'>" +
            "<i class='material-symbols-rounded'>add</i> " +
            "Nouvelle tournée</a>");
    }
}
```

### 4.2. `LivraisonUrbaineFichePage extends PageConsulte`

Écran **FICHE** d'une tournée.
Il étend `PageConsulte` pour centraliser les libellés **et** les URLs d'action.

```java
package logistique.affichage;

import affichage.PageConsulte;
import logistique.LivraisonUrbaineCpl;
import user.UserEJB;

import javax.servlet.http.HttpServletRequest;

/**
 * Écran FICHE d'une tournée.
 * Étend PageConsulte pour centraliser les libellés et les URLs d'action.
 */
public class LivraisonUrbaineFichePage extends PageConsulte {

    public LivraisonUrbaineFichePage(LivraisonUrbaineCpl f,
                                     HttpServletRequest request,
                                     UserEJB u,
                                     String lien) throws Exception {
        super(f, request, u);
        configurer(f, u, lien);
    }

    private void configurer(LivraisonUrbaineCpl f, UserEJB u, String lien)
            throws Exception {

        setTitre("Fiche d'une tournée de livraison");
        getBase(); // charge l'objet depuis request.getParameter("id")

        // ---------- Libellés ----------
        getChampByName("id").setLibelle("Id");
        getChampByName("daty").setLibelle("Date");
        getChampByName("livreurlib").setLibelle("Livreur");
        getChampByName("vehiculelib").setLibelle("Véhicule");
        getChampByName("zonelib").setLibelle("Zone");
        getChampByName("etatlib").setLibelle("État");
        getChampByName("remarque").setLibelle("Remarque");

        // ---------- Masquer les colonnes techniques ----------
        getChampByName("livreur").setVisible(false);
        getChampByName("vehicule").setVisible(false);
        getChampByName("zone").setVisible(false);
        getChampByName("etat").setVisible(false);

        // ---------- Lien hypertexte sur le livreur ----------
        getChampByName("livreur").setLien(
            lien + "?but=logistique/livreur-fiche.jsp", "id=");
    }

    // ---------- Helpers pour le JSP ----------

    public String getUrlModifier(String lien) throws Exception {
        return lien + "?but=logistique/livraison-modif.jsp"
             + "&id=" + getBase().getTuppleID() + "&acte=update";
    }

    public String getUrlValider(String lien, String pageActuel) throws Exception {
        return actionUrl(lien, "valider", pageActuel);
    }

    public String getUrlAnnuler(String lien, String pageActuel) throws Exception {
        return actionUrl(lien, "annuler", pageActuel);
    }

    public String getUrlAnnulerVisa(String lien, String pageActuel) throws Exception {
        return actionUrl(lien, "annulerVisa", pageActuel);
    }

    public String getUrlCloturer(String lien, String pageActuel) throws Exception {
        return actionUrl(lien, "cloturer", pageActuel);
    }

    private String actionUrl(String lien, String acte, String pageActuel)
            throws Exception {
        return lien + "?but=apresTarif.jsp"
             + "&acte=" + acte
             + "&classe=logistique.LivraisonUrbaine"
             + "&bute=" + pageActuel
             + "&id=" + getBase().getTuppleID();
    }
}
```

### 4.3. `LivraisonUrbaineSaisiePage extends PageInsertMultiple`

Écran **SAISIE** maître/détail.
Il étend `PageInsertMultiple` : toute la configuration est ici.

```java
package logistique.affichage;

import affichage.Champ;
import affichage.Liste;
import affichage.PageInsertMultiple;
import logistique.LivraisonUrbaine;
import logistique.LivraisonUrbaineFille;
import user.UserEJB;

import javax.servlet.http.HttpServletRequest;

/**
 * Écran SAISIE maître/détail.
 * Étend PageInsertMultiple : toute la configuration est ici.
 */
public class LivraisonUrbaineSaisiePage extends PageInsertMultiple {

    public static final int NB_LIGNES = 8;

    public LivraisonUrbaineSaisiePage(LivraisonUrbaine mere,
                                      LivraisonUrbaineFille fille,
                                      HttpServletRequest request,
                                      UserEJB u,
                                      String lien) throws Exception {
        super(mere, fille, request, NB_LIGNES, u);
        configurer(mere, u, lien);
    }

    private void configurer(LivraisonUrbaine mere, UserEJB u, String lien)
            throws Exception {

        setLien(lien);

        // =================== EN-TÊTE ===================

        // Liste déroulante : livreur
        Liste[] liste = new Liste[2];
        liste[0] = new Liste("livreur",
                             new Livreur(), "nom", "id");
        liste[0].setApresW(" and actif = 1");

        // Liste déroulante : véhicule
        liste[1] = new Liste("vehicule",
                             new Vehicule(), "immatriculation", "id");

        getFormu().changerEnChamp(liste);

        // Libellés
        getFormu().getChamp("daty").setLibelle("Date");
        getFormu().getChamp("livreur").setLibelle("Livreur");
        getFormu().getChamp("vehicule").setLibelle("Véhicule");
        getFormu().getChamp("zone").setLibelle("Zone");
        getFormu().getChamp("remarque").setLibelle("Remarque");
        getFormu().getChamp("etat").setVisible(false);

        // Autocomplete sur la zone (recherche SQL directe)
        getFormu().getChamp("zone").setPageAppelComplete(
            "logistique.Zone", "id", "ZONE",
            "zone;zonelib", "id;val");

        // Bouton « + Ajouter » à côté du livreur
        getFormu().getChamp("livreur").setPageAppelInsert(
            "logistique/livreur-saisie.jsp",
            "livreur;livreurlibelle", "id;nom");

        // =================== DÉTAIL ===================

        // Libellés des colonnes
        getFormufle().getChamp("destinataire_0").setLibelle("Destinataire");
        getFormufle().getChamp("adresse_0").setLibelle("Adresse");
        getFormufle().getChamp("poids_0").setLibelle("Poids (kg)");
        getFormufle().getChamp("frais_0").setLibelle("Frais");
        getFormufle().getChamp("statut_0").setLibelle("Statut");

        // Colonnes techniques masquées
        getFormufle().getChampMulitple("id").setVisible(false);
        getFormufle().getChampMulitple("idmere").setVisible(false);

        // Callback JS sur toutes les lignes
        Champ.setAutre(getFormufle().getChampFille("poids"),
                       "onchange='calculerTotal()'");
        Champ.setAutre(getFormufle().getChampFille("frais"),
                       "onchange='calculerTotal()'");

        // Préparation OBLIGATOIRE avant d'accéder aux champs _0
        preparerDataFormu();

        // Défauts ligne par ligne
        for (int i = 0; i < NB_LIGNES; i++) {
            getFormufle().getChamp("poids_" + i).setDefaut("0");
            getFormufle().getChamp("frais_" + i).setDefaut("0");
            getFormufle().getChamp("statut_" + i).setDefaut("A_LIVRER");
        }

        // Ordre des colonnes dans le tableau HTML
        getFormufle().setColOrdre(new String[]{
            "destinataire", "adresse", "poids", "frais", "statut"
        });

        // Génération des tabindex
        getFormu().makeHtmlInsertTabIndex();
        getFormufle().makeHtmlInsertTableauIndex();
    }
}
```

### 4.4. `LivraisonUrbaineModifPage extends PageUpdateMultiple`

Écran **MODIF** maître/détail.
Il étend `PageUpdateMultiple` et **pré-charge** les lignes existantes.

```java
package logistique.affichage;

import affichage.Champ;
import affichage.Liste;
import affichage.PageUpdateMultiple;
import bean.CGenUtil;
import logistique.LivraisonUrbaine;
import logistique.LivraisonUrbaineFille;
import user.UserEJB;

import javax.servlet.http.HttpServletRequest;

/**
 * Écran MODIF maître/détail.
 * Étend PageUpdateMultiple : on pré-charge les lignes existantes.
 */
public class LivraisonUrbaineModifPage extends PageUpdateMultiple {

    /** Nombre de nouvelles lignes vides ajoutées à la fin du tableau. */
    public static final int NB_NOUVELLES_LIGNES = 2;

    public LivraisonUrbaineModifPage(LivraisonUrbaine mere,
                                     LivraisonUrbaineFille fille,
                                     HttpServletRequest request,
                                     UserEJB u,
                                     String lien) throws Exception {
        super(mere, fille,
              chargerLignes(fille, request),
              request, u, NB_NOUVELLES_LIGNES);
        configurer(mere, u, lien);
    }

    /** Charge les lignes existantes rattachées à la mère. */
    private static LivraisonUrbaineFille[] chargerLignes(
            LivraisonUrbaineFille fille,
            HttpServletRequest request) throws Exception {

        String idMere = request.getParameter("id");
        fille.setNomTable("LIVRAISON_URBAINE_FILLE");
        fille.setIdmere(idMere);

        return (LivraisonUrbaineFille[])
            CGenUtil.rechercher(fille, null, null, "");
    }

    private void configurer(LivraisonUrbaine mere, UserEJB u, String lien)
            throws Exception {

        setLien(lien);

        // =================== EN-TÊTE ===================

        Liste[] liste = new Liste[2];
        liste[0] = new Liste("livreur", new Livreur(), "nom", "id");
        liste[1] = new Liste("vehicule", new Vehicule(), "immatriculation", "id");
        getFormu().changerEnChamp(liste);

        getFormu().getChamp("daty").setLibelle("Date");
        getFormu().getChamp("livreur").setLibelle("Livreur");
        getFormu().getChamp("vehicule").setLibelle("Véhicule");
        getFormu().getChamp("zone").setLibelle("Zone");
        getFormu().getChamp("remarque").setLibelle("Remarque");
        getFormu().getChamp("etat").setVisible(false);

        // =================== DÉTAIL ===================

        // Les PK de lignes sont en lecture seule
        for (int i = 0; i < getNombreLigne(); i++) {
            getFormufle().getChamp("id_" + i).setAutre("readonly");
        }

        getFormufle().getChamp("destinataire_0").setLibelle("Destinataire");
        getFormufle().getChamp("adresse_0").setLibelle("Adresse");
        getFormufle().getChamp("poids_0").setLibelle("Poids (kg)");
        getFormufle().getChamp("frais_0").setLibelle("Frais");
        getFormufle().getChamp("statut_0").setLibelle("Statut");

        Champ.setAutre(getFormufle().getChampFille("frais"),
                       "onchange='calculerTotal()'");

        preparerDataFormu();
        getFormu().makeHtmlInsertTabIndex();
        getFormufle().makeHtmlInsertTableauIndex();
    }
}
```

---

## 5. JSP (très courts grâce aux classes étendues)

Dossier : `archiochem-war/web/pages/logistique/`

### 5.1. `livraison-liste.jsp`

```jsp
<%@page import="affichage.*" %>
<%@page import="logistique.*" %>
<%@page import="logistique.affichage.*" %>
<% try {
    UserEJB u = (UserEJB) session.getValue("u");
    String lien = (String) session.getValue("lien");

    LivraisonUrbaineCpl f  = new LivraisonUrbaineCpl();
    LivraisonUrbaineListePage pr =
        new LivraisonUrbaineListePage(f, request, u, lien);
%>
<div class="content-wrapper">
  <section class="content-header">
    <h1><%= pr.getTitre() %></h1>
  </section>
  <section class="content">
    <form action="<%= pr.getLien() %>?but=<%= pr.getApres() %>"
          method="post" name="filtre">
      <% out.println(pr.getFormu().getHtmlEnsemble()); %>
    </form>

    <% out.println(pr.getTableauRecap().getHtml()); %>
    <br>
    <% out.println(pr.getTableau().getHtml());
       out.println(pr.getBasPage()); %>
  </section>
</div>
<% } catch (Exception e) {
       e.printStackTrace(); %>
       <script>alert('<%= e.getMessage() %>'); history.back();</script>
<% } %>
```

### 5.2. `livraison-fiche.jsp`

```jsp
<%@page import="java.util.*" %>
<%@page import="logistique.*" %>
<%@page import="logistique.affichage.*" %>
<% try {
    UserEJB u = (UserEJB) session.getValue("u");
    String lien = (String) session.getValue("lien");

    LivraisonUrbaineCpl f = new LivraisonUrbaineCpl();
    f.setNomTable("LIVRAISON_URBAINE_CPL");

    LivraisonUrbaineFichePage pc =
        new LivraisonUrbaineFichePage(f, request, u, lien);

    String id   = pc.getBase().getTuppleID();
    int    etat = pc.getBase().getEtat();
    String pageActuel = "logistique/livraison-fiche.jsp";

    // Gestion des onglets
    Map<String,String> map = new HashMap<>();
    map.put("inc/livraison-detail", "");
    String tab = request.getParameter("tab");
    if (tab == null) tab = "inc/livraison-detail";
    map.put(tab, "active");
    tab = tab + ".jsp";
%>
<div class="content-wrapper">
  <h1 class="box-title">
    <a href="<%= lien %>?but=logistique/livraison-liste.jsp">
      <i class="fa fa-angle-left"></i></a>
    <%= pc.getTitre() %>
  </h1>

  <div class="row m-0">
    <div class="col-md-6">
      <div class="box-fiche"><div class="box"><div class="box-body">
        <% out.println(pc.getHtml()); %>
        <br>
        <div class="box-footer">
          <% if (etat < 11) { %>
            <a class="btn btn-primary pull-right"
               href="<%= pc.getUrlValider(lien, pageActuel) %>">Viser</a>
            <a class="btn btn-secondary pull-right"
               href="<%= pc.getUrlModifier(lien) %>">Modifier</a>
            <a class="btn btn-danger pull-left"
               href="<%= pc.getUrlAnnuler(lien, pageActuel) %>">Annuler</a>
          <% } else { %>
            <a class="btn btn-danger pull-left"
               href="<%= pc.getUrlAnnulerVisa(lien, pageActuel) %>">Annuler Visa</a>
            <a class="btn btn-warning pull-right"
               href="<%= pc.getUrlCloturer(lien, pageActuel) %>">Clôturer</a>
          <% } %>
        </div>
      </div></div></div>
    </div>
  </div>

  <div class="row m-0">
    <div class="col-md-12 nopadding">
      <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
          <li class="<%= map.get("inc/livraison-detail") %>">
            <a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>
                     &tab=inc/livraison-detail">Colis</a></li>
        </ul>
        <div class="tab-content">
          <jsp:include page="<%= tab %>">
            <jsp:param name="idMere" value="<%= id %>" />
          </jsp:include>
        </div>
      </div>
    </div>
  </div>
</div>
<% } catch (Exception e) {
       e.printStackTrace(); %>
       <script>alert('<%= e.getMessage() %>'); history.back();</script>
<% } %>
```

### 5.3. `livraison-saisie.jsp`

```jsp
<%@page import="logistique.*" %>
<%@page import="logistique.affichage.*" %>
<% try {
    UserEJB u = (UserEJB) session.getValue("u");
    String lien = (String) session.getValue("lien");

    LivraisonUrbaine      mere  = new LivraisonUrbaine();
    LivraisonUrbaineFille fille = new LivraisonUrbaineFille();
    fille.setNomTable("LIVRAISON_URBAINE_FILLE_CPL");

    LivraisonUrbaineSaisiePage pi =
        new LivraisonUrbaineSaisiePage(mere, fille, request, u, lien);
%>
<div class="content-wrapper">
  <h1>Nouvelle tournée de livraison</h1>
  <div class="box-body">
    <form class="container"
          action="<%= pi.getLien() %>?but=apresMultiple.jsp"
          method="post">

      <% out.println(pi.getFormu().getHtmlInsert()); %>
      <% out.println(pi.getFormufle().getHtmlTableauInsert()); %>

      <input type="hidden" name="acte"        value="insert" />
      <input type="hidden" name="bute"        value="logistique/livraison-fiche.jsp" />
      <input type="hidden" name="classe"      value="logistique.LivraisonUrbaine" />
      <input type="hidden" name="classefille" value="logistique.LivraisonUrbaineFille" />
      <input type="hidden" name="nombreLigne" value="<%= LivraisonUrbaineSaisiePage.NB_LIGNES %>" />
      <input type="hidden" name="colonneMere" value="idmere" />
      <input type="hidden" name="nomtable"    value="LIVRAISON_URBAINE_FILLE" />
    </form>
  </div>
</div>

<script>
function calculerTotal() {
    let total = 0;
    document.querySelectorAll('input[id^="frais_"]').forEach(el => {
        total += parseFloat(el.value) || 0;
    });
    const zone = document.getElementById('totalColis');
    if (zone) zone.textContent = total.toLocaleString('fr-FR');
}
document.addEventListener('DOMContentLoaded', calculerTotal);
</script>
<% } catch (Exception e) {
       e.printStackTrace(); %>
       <script>alert('<%= e.getMessage() %>'); history.back();</script>
<% } %>
```

### 5.4. `livraison-modif.jsp`

```jsp
<%@page import="logistique.*" %>
<%@page import="logistique.affichage.*" %>
<% try {
    UserEJB u = (UserEJB) session.getValue("u");
    String lien = (String) session.getValue("lien");

    LivraisonUrbaine      mere  = new LivraisonUrbaine();
    LivraisonUrbaineFille fille = new LivraisonUrbaineFille();

    LivraisonUrbaineModifPage pi =
        new LivraisonUrbaineModifPage(mere, fille, request, u, lien);
%>
<div class="content-wrapper">
  <h1>Modification de la tournée</h1>
  <div class="box-body">
    <form class="container"
          action="<%= pi.getLien() %>?but=apresMultiple.jsp&id=<%= request.getParameter("id") %>"
          method="post">

      <% out.println(pi.getFormu().getHtmlInsert()); %>
      <% out.println(pi.getFormufle().getHtmlTableauInsert()); %>

      <input type="hidden" name="acte"        value="updateInsert" />
      <input type="hidden" name="bute"        value="logistique/livraison-fiche.jsp?id=<%= request.getParameter("id") %>" />
      <input type="hidden" name="classe"      value="logistique.LivraisonUrbaine" />
      <input type="hidden" name="classefille" value="logistique.LivraisonUrbaineFille" />
      <input type="hidden" name="nombreLigne" value="<%= pi.getNombreLigne() %>" />
      <input type="hidden" name="colonneMere" value="idmere" />
      <input type="hidden" name="nomtable"    value="LIVRAISON_URBAINE_FILLE" />
    </form>
  </div>
</div>
<% } catch (Exception e) {
       e.printStackTrace(); %>
       <script>alert('<%= e.getMessage() %>'); history.back();</script>
<% } %>
```

### 5.5. `inc/livraison-detail.jsp` (onglet)

Cet onglet affiche les colis de la tournée.

> ⚠️ Il faut **toujours** filtrer par la mère (`setAWhere`), sinon l'onglet
> affiche toute la table.

```jsp
<%@page import="affichage.*" %>
<%@page import="logistique.*" %>
<% try {
    String idMere = request.getParameter("idMere");
    if (idMere == null) idMere = request.getParameter("id");

    UserEJB u    = (UserEJB) session.getValue("u");
    String lien  = (String) session.getValue("lien");

    LivraisonUrbaineFilleCpl f = new LivraisonUrbaineFilleCpl();
    f.setNomTable("LIVRAISON_URBAINE_FILLE_CPL");
    f.setIdmere(idMere);

    String[] listeCrt  = { "destinataire", "statutlib" };
    String[] libEntete = { "id", "destinataire", "adresse",
                           "poids", "frais", "statutlib" };

    PageRecherche pr = new PageRecherche(f, request, listeCrt, null, 10,
                                          libEntete, libEntete.length);
    pr.setLien(lien);
    pr.setApres("logistique/livraison-fiche.jsp&id=" + idMere
                + "&tab=inc/livraison-detail");

    // ⚠️ ESSENTIEL : filtrer par la mère
    pr.setAWhere(" and idmere = '" + idMere + "'");

    pr.creerObjetPage(libEntete, null);
    pr.getTableau().setLibelleAffiche(new String[]{
        "Id", "Destinataire", "Adresse", "Poids (kg)", "Frais", "Statut"
    });
%>
<div class="tab-pane active">
  <form action="<%= pr.getLien() %>?but=<%= pr.getApres() %>" method="post">
    <% out.println(pr.getFormu().getHtmlEnsemble()); %>
  </form>

  <% out.println(pr.getTableau().getHtml());
     out.println(pr.getBasPage()); %>
</div>
<% } catch (Exception e) { e.printStackTrace(); } %>
```

---

## 6. Action personnalisée (si besoin)

### 6.1. `apresAffecterLivreur.jsp`

Exemple : affecter un livreur à une tournée.

Fichier : `pages/logistique/apresAffecterLivreur.jsp`

```jsp
<%@page import="user.UserEJB" %>
<%@page import="logistique.LivraisonUrbaine" %>
<%@page import="bean.CGenUtil" %>
<%
try {
    UserEJB u   = (UserEJB) session.getValue("u");
    String lien = (String) session.getValue("lien");
    String id   = request.getParameter("id");
    String livreur = request.getParameter("livreur");
    String bute = request.getParameter("bute");

    LivraisonUrbaine lu = (LivraisonUrbaine)
        new LivraisonUrbaine().getById(id, "LIVRAISON_URBAINE", null);
    if (lu == null) throw new Exception("Tournée introuvable : " + id);

    lu.setLivreur(livreur);
    lu.updateObject(u.getUser().getTuppleID(), null);
%>
<script>
    document.location.replace("<%= lien %>?but=<%= bute %>&id=<%= id %>");
</script>
<%
} catch (Exception e) {
    e.printStackTrace(); %>
    <script>
        alert('<%= new String(e.getMessage().getBytes(), "UTF-8") %>');
        history.back();
    </script>
<% } %>
```

---

## 7. Menu

```sql
-- Menu principal
INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES (
    'MENULIVRAISON',
    'Livraisons urbaines',
    'local_shipping',
    'module.jsp?but=logistique/livraison-liste.jsp',
    20, 1, NULL
);

-- Sous-menu : saisie
INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES (
    'MENULIVRAISON_SAISIE',
    'Nouvelle tournée',
    'add',
    'module.jsp?but=logistique/livraison-saisie.jsp',
    1, 2, 'MENULIVRAISON'
);

-- Association au rôle
INSERT INTO usermenu (id, IDMENU, idrole, INTERDIT)
VALUES (sequsermenu.nextval, 'MENULIVRAISON',        'resp', 0);

INSERT INTO usermenu (id, IDMENU, idrole, INTERDIT)
VALUES (sequsermenu.nextval, 'MENULIVRAISON_SAISIE', 'resp', 0);
```

> ⚠️ Redémarrer **WildFly 10** après les `INSERT` sur `roles` / `usermenu`.

---

## 8. Récapitulatif de l'arborescence

```
src/java/logistique/
├── LivraisonUrbaine.java               ← modèle maître
├── LivraisonUrbaineFille.java          ← modèle détail
├── LivraisonUrbaineCpl.java            ← vue CPL maître (lecture seule)
├── LivraisonUrbaineFilleCpl.java       ← vue CPL détail (lecture seule)
└── affichage/
    ├── LivraisonUrbaineListePage.java        ← extends PageRecherche
    ├── LivraisonUrbaineFichePage.java        ← extends PageConsulte
    ├── LivraisonUrbaineSaisiePage.java       ← extends PageInsertMultiple
    └── LivraisonUrbaineModifPage.java        ← extends PageUpdateMultiple

archiochem-war/web/pages/logistique/
├── livraison-liste.jsp
├── livraison-fiche.jsp
├── livraison-saisie.jsp
├── livraison-modif.jsp
├── apresAffecterLivreur.jsp
└── inc/
    └── livraison-detail.jsp
```

---

## 9. Pourquoi ce patron ?

| Sans extension | Avec extension |
|---|---|
| 100+ lignes de config dans chaque JSP | JSP de 10–15 lignes |
| Copier/coller dès qu'on veut une variante | 1 sous-classe par variante |
| Config impossible à tester | Config testable unitairement |
| Refactor = chercher dans 30 JSP | Refactor = 1 fichier Java |
| Risque d'incohérence entre 2 fiches | Configuration centralisée |

> 🎓 **Règle d'or** : dès qu'un écran a plus de 20 lignes de configuration
> (`setLibelle`, `changerEnChamp`, `setPageAppelComplete`, …), il doit
> **sortir du JSP** et devenir une classe qui étend la classe du framework.

---

## 10. Points à vérifier avant de copier

Le code ci-dessus est conservé tel quel. Ces points sont à contrôler
quand vous l'adaptez à votre projet.

| # | Où | Point à vérifier |
|---|---|---|
| 1 | Tous les JSP | `UserEJB` est utilisé sans import. Ajouter `<%@page import="user.UserEJB" %>`. |
| 2 | `SaisiePage`, `ModifPage` | `Livreur` et `Vehicule` ne sont pas importés. Ajouter leurs imports (ou leur package). |
| 3 | Vue `LIVRAISON_URBAINE_FILLE_CPL` | La classe `LivraisonUrbaineFilleCpl` a un champ `montantTtc`, mais la vue ne fournit pas cette colonne. Ajouter la colonne dans la vue ou retirer le champ. |
| 4 | `livraison-saisie.jsp` | `calculerTotal()` met à jour l'élément `#totalColis`, qui n'existe pas dans la page. Ajouter un `<span id="totalColis">` pour afficher le total. |
| 5 | `FichePage` | Le champ `livreur` est masqué (`setVisible(false)`) **puis** reçoit un lien. Le lien n'est pas visible. Afficher `livreurlib` à la place. |
| 6 | `ListePage` | La liste `zonelib` utilise `("val", "id")`, alors que la colonne filtrée est un libellé. Voir le chapitre 03, où l'exemple utilise `("val", "val")`. |
| 7 | `inc/livraison-detail.jsp` | `idMere` est concaténé dans le SQL. Échapper les quotes (`idMere.replace("'", "''")`). |
| 8 | `apresAffecterLivreur.jsp` | Le code modifie **une seule** tournée (celle de `id`), même si l'exemple parle de « toutes les tournées en attente ». |
| 9 | `PageRecherche` | `setAWhere` doit être appelé **avant** `creerObjetPage(...)` (c'est déjà le cas dans l'exemple). |