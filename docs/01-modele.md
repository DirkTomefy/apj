# 01 — Créer une classe modèle (ORM)

Toutes les classes métier héritent de `bean.ClassMAPTable` ou d'une de ses
sous-classes.

## Hiérarchie

```
ClassMAPTable
   └ ClassUser        → + iduser
       └ ClassEtat    → + etat (workflow)
           └ ClassMere → + filles
           └ ClassFille → + lien mère
```

## 1.1. Objet simple

```java
package monpackage;

import bean.ClassMAPTable;
import java.sql.Connection;

public class Produit extends ClassMAPTable {
    private String id;
    private String libelle;
    private double pu;

    public Produit() {
        setNomTable("PRODUIT");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        preparePk("PRD", "GETSEQ_PRODUIT");
        setId(makePK(c));
    }

    @Override public String getTuppleID()       { return id; }
    @Override public String getAttributIDName() { return "id"; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public double getPu() { return pu; }
    public void setPu(double pu) { this.pu = pu; }
}
```

## 1.2. Objet avec état

```java
public class MaCommande extends ClassEtat {
    private String id;
    @Override public int getEtat()       { return etat; }
    @Override public void setEtat(int e) { this.etat = e; }
}
```

| Valeur | Signification |
|---|---|
| 1  | Créé(e) |
| 11 | Traité(e) / Validé(e) |
| 0  | Annulé(e) |
| 9  | Clôturé(e) |

## 1.3. Maître / Fille

```java
public class BonDeCommande extends ClassMere {
    public BonDeCommande() {
        setNomTable("BONDECOMMANDE");
        setLiaisonFille("idbc");
        setNomClasseFille("vente.BonDeCommandeFille");
    }
    @Override public String getNomClasseFille() { return "vente.BonDeCommandeFille"; }
    @Override public String getLiaisonFille()   { return "idbc"; }
}

public class BonDeCommandeFille extends ClassFille {
    public BonDeCommandeFille() throws Exception {
        setNomTable("BONDECOMMANDE_FILLE");
        setNomClasseMere("vente.BonDeCommande");
        setLiaisonMere("idbc");
    }
    @Override public String getNomClasseMere() { return "vente.BonDeCommande"; }
    @Override public String getLiaisonMere()   { return "idbc"; }
}
```

## 1.4. Objet « Cpl » (vue SQL)

`XxxCpl extends Xxx` **ne stocke rien**. Lit une **vue SQL** avec libellés.

```java
public class BonDeCommandeCpl extends BonDeCommande {
    private String idclientlib;
    public BonDeCommandeCpl() {
        setNomTable("BONDECOMMANDE_CLIENT_CPL");
    }
    // getters / setters
}
```

## 1.5. Dictionnaire — `TypeObjet`

```java
TypeObjet t = new TypeObjet();
t.setNomTable("MODEPAIEMENT");
TypeObjet[] tous = t.get();
TypeObjet   un   = TypeObjet.getById("ESP", tous);
```

## 1.6. Annotations

- `@LibelleAffichage("Désignation")` — libellé par défaut
- `@AiTabDesc("...")` / `@AiColDesc("...")` — pour le chatbot

---

## Détails des signatures

### `ClassMAPTable` — cycle de vie

| Méthode | Signature | Rôle |
|---|---|---|
| `construirePK` | `void construirePK(Connection c)` | Construit la PK (à surcharger) |
| `preparePk` | `void preparePk(String indicePK, String fonct)` | Définit préfixe + séquence |
| `makePK` | `String makePK(Connection c)` | Génère la PK |
| `getTuppleID` | `String getTuppleID()` | Retourne la PK (abstrait) |
| `getAttributIDName` | `String getAttributIDName()` | Nom Java de la PK (abstrait) |
| `setNomTable` | `void setNomTable(String)` | Force le nom SQL |
| `getNomTable` | `String getNomTable()` | Retourne le nom SQL |
| `setNomTableSelect` | `void setNomTableSelect(String)` | Table pour `getById` |
| `setMode` | `void setMode(String)` | `"modif"` active les validations |
| `getMode` | `String getMode()` | Mode courant |

### `ClassMAPTable` — CRUD

| Méthode | Signature | Rôle |
|---|---|---|
| `createObject` | `ClassMAPTable createObject(String u, Connection c)` | Insère + historique + fichiers |
| `updateObject` | `int updateObject(String u, Connection c)` | Modifie |
| `deleteToTableWithHisto` | `int deleteToTableWithHisto(String u, Connection c)` | Supprime + historique |
| `getById` | `ClassMAPTable getById(String id, String table, Connection c)` | Charge par PK |

### `ClassMAPTable` — utilitaires

| Méthode | Signature | Rôle |
|---|---|---|
| `setValChamp` | `void setValChamp(String nom, Object val)` | Affecte par nom |
| `setValChamp` | `void setValChamp(Field f, Object val)` | Affecte par Field |
| `getValInsert` | `String getValInsert(String nomCol)` | Lit par nom |
| `getValeur` | `Object getValeur(String nomChamp)` | Lit la valeur brute |
| `getFieldList` | `Field[] getFieldList()` | Liste des champs (réflexion) |
| `getFieldByName` | `Field getFieldByName(String name)` | Field par nom |
| `getTypeMAPField` | `int getTypeMAPField(Field f)` | Type MAP (0..35) |
| `toJson` | `String toJson()` | Sérialise en JSON |
| `dupliquer` | `ClassMAPTable dupliquer(String u, Connection c)` | Duplique |

### `ClassEtat` — workflow

| Méthode | Signature | Rôle |
|---|---|---|
| `validerObject` | `Object validerObject(String u, Connection c)` | Valide (11) |
| `annuler` | `int annuler(String u, Connection c)` | Annule (0) |
| `annulerVisa` | `void annulerVisa(String u, Connection c)` | Retour en créé |
| `cloturerObject` | `Object cloturerObject(String u, Connection c)` | Clôture (9) |
| `payerObject` | `Object payerObject(String u, Connection c)` | Paie |
| `viser` | `void viser(UserEJB user, Connection c)` | Validation hiérarchique |
| `updateEtat` | `int updateEtat(int etat, String id, Connection c)` | Change l'état |
| `chaineEtat` | `String chaineEtat()` | Rend HTML coloré |

### `ClassMere` / `ClassFille`

| Méthode | Classe | Rôle |
|---|---|---|
| `setLiaisonFille` | `ClassMere` | Nom de la FK dans la fille |
| `setNomClasseFille` | `ClassMere` | Classe Java de la fille |
| `getNomClasseFille` | `ClassMere` | Retourne la classe |
| `getLiaisonFille` | `ClassMere` | Retourne la FK |
| `setLiaisonMere` | `ClassFille` | Nom de la FK côté mère |
| `setNomClasseMere` | `ClassFille` | Classe Java de la mère |
| `getNomClasseMere` | `ClassFille` | Retourne la classe |
| `getLiaisonMere` | `ClassFille` | Retourne la FK |
| `getFille` | `ClassMere` | Charge les filles |
| `setFille` | `ClassMere` | Affecte un tableau de filles |

### Types `TYPE_CLASS_MAP_*`

| Constante | Type Java |
|---|---|
| 0 | `boolean` |
| 1 | `byte` |
| 2 | `short` |
| 3 | `int` |
| 4 | `long` |
| 5 | `float` |
| 6 | `double` |
| 10 | `String` |
| 21 | `java.sql.Date` |
| 22 | `java.sql.Time` |
| 31 | `Blob` |
| 32 | `Clob` |
| 33 | `Integer` |
| 35 | `Timestamp` |