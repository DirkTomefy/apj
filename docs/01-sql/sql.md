# Guide SQL — Module APJ

Documentation des commandes SQL utilisées dans le projet.
Base : **Oracle 11g+**.

## Table des matières

1. [Créer une table](#1-créer-une-table)
2. [Ajouter une colonne](#2-ajouter-une-colonne)
3. [Renommer une table ou une colonne](#3-renommer-une-table-ou-une-colonne)
4. [Supprimer une colonne](#4-supprimer-une-colonne)
5. [Modifier une colonne](#5-modifier-une-colonne)
6. [INSERT — Insérer des données](#6-insert--insérer-des-données)
7. [UPDATE — Modifier des données](#7-update--modifier-des-données)
8. [DELETE — Supprimer des données](#8-delete--supprimer-des-données)
9. [Contraintes (PK, FK, CHECK, UNIQUE)](#9-contraintes-pk-fk-check-unique)
10. [Index](#10-index)
11. [Séquences](#11-séquences)
12. [Fonctions `getseq` (APJ)](#12-fonctions-getseq-apj)
13. [Vues](#13-vues)
14. [Menu](#14-menu)
15. [Vérifications utiles](#15-vérifications-utiles)

---

## 1. Créer une table

```sql
CREATE TABLE MA_TABLE (
    id        VARCHAR2(30) PRIMARY KEY,
    libelle   VARCHAR2(100),
    quantite  NUMBER(15,2),
    daty      DATE,
    etat      NUMBER(2) DEFAULT 1,
    actif     NUMBER(1) DEFAULT 1
);
```

**Types courants :**

| Type | Usage | Exemple |
|---|---|---|
| `VARCHAR2(n)` | Chaîne | `VARCHAR2(100)` |
| `NUMBER(n)` | Entier | `NUMBER(10)` |
| `NUMBER(n,m)` | Décimal | `NUMBER(15,2)` |
| `DATE` | Date (jj/mm/aaaa) | `DATE` |
| `TIMESTAMP` | Date + heure | `TIMESTAMP` |
| `CLOB` | Texte long | `CLOB` |
| `BLOB` | Binaire (image, PDF) | `BLOB` |

**Conventions projet :**
- PK : `VARCHAR2(30)`, préfixe + numéro (`BCP000001`)
- Montants : `NUMBER(15,2)`
- Quantités : `NUMBER(15,2)`
- États : `NUMBER(2)` avec `DEFAULT 1`
- Booléens : `NUMBER(1)` (0 ou 1)
- Dates : `DATE`

---

## 2. Ajouter une colonne

### Ajouter une colonne simple

```sql
ALTER TABLE MA_TABLE ADD nouvelle_colonne VARCHAR2(100);
```

### Ajouter avec valeur par défaut

```sql
ALTER TABLE MA_TABLE ADD etat NUMBER(2) DEFAULT 1;
```

### Ajouter avec NOT NULL (si table vide)

```sql
ALTER TABLE MA_TABLE ADD code VARCHAR2(30) NOT NULL;
```

### Ajouter avec NOT NULL (table non vide)

```sql
-- 1. Ajouter sans NOT NULL
ALTER TABLE MA_TABLE ADD code VARCHAR2(30);

-- 2. Remplir les valeurs NULL
UPDATE MA_TABLE SET code = 'DEFAUT' WHERE code IS NULL;

-- 3. Ajouter la contrainte
ALTER TABLE MA_TABLE MODIFY code NOT NULL;
```

### Ajouter plusieurs colonnes d'un coup

```sql
ALTER TABLE MA_TABLE ADD (
    colonne1 VARCHAR2(100),
    colonne2 NUMBER(15,2),
    colonne3 DATE
);
```

### Ajouter une FK après ajout de colonne

```sql
ALTER TABLE MA_TABLE ADD CONSTRAINT fk_mt_ref
    FOREIGN KEY (idref) REFERENCES AUTRE_TABLE(id);
```

---

## 3. Renommer une table ou une colonne

### Renommer une table

```sql
ALTER TABLE ANCIENNE_TABLE RENAME TO NOUVELLE_TABLE;
```

### Renommer une colonne

```sql
ALTER TABLE MA_TABLE RENAME COLUMN ancien_nom TO nouveau_nom;
```

**Exemple concret :**

```sql
-- Renommer une colonne "desc" (mot réservé) en "descri"
ALTER TABLE BONCOMMANDE_POUSSIN RENAME COLUMN desc TO descri;
```

⚠️ **Mots réservés SQL à éviter comme noms de colonnes :**
`DESC`, `ASC`, `ORDER`, `GROUP`, `SELECT`, `FROM`, `WHERE`, `TABLE`, `DATE`, `NUMBER`, `LEVEL`, `SIZE`, `USER`, `COMMENT`.

---

## 4. Supprimer une colonne

### Supprimer une colonne simple

```sql
ALTER TABLE MA_TABLE DROP COLUMN nom_colonne;
```

### Supprimer plusieurs colonnes

```sql
ALTER TABLE MA_TABLE DROP (colonne1, colonne2);
```

### Vérifier avant suppression

```sql
SELECT column_name FROM user_tab_columns
WHERE table_name = 'MA_TABLE';
```

⚠️ **Oracle ne rollback pas un `DROP COLUMN`.** Sauvegarder les données avant.

### Supprimer aussi les contraintes associées

```sql
-- 1. Trouver les contraintes qui utilisent cette colonne
SELECT constraint_name FROM user_cons_columns
WHERE table_name = 'MA_TABLE' AND column_name = 'NOM_COLONNE';

-- 2. Supprimer les contraintes
ALTER TABLE MA_TABLE DROP CONSTRAINT nom_contrainte;

-- 3. Puis supprimer la colonne
ALTER TABLE MA_TABLE DROP COLUMN nom_colonne;
```

---

## 5. Modifier une colonne

### Changer le type / la taille

```sql
ALTER TABLE MA_TABLE MODIFY libelle VARCHAR2(500);
```

⚠️ Possible seulement si la table est vide ou si on agrandit.

### Ajouter une valeur par défaut

```sql
ALTER TABLE MA_TABLE MODIFY etat DEFAULT 1;
```

### Ajouter NOT NULL

```sql
ALTER TABLE MA_TABLE MODIFY code NOT NULL;
```

### Retirer NOT NULL

```sql
ALTER TABLE MA_TABLE MODIFY code NULL;
```

---

## 6. INSERT — Insérer des données

### Insertion simple

```sql
INSERT INTO MA_TABLE (id, libelle, quantite)
VALUES ('MT001', 'Premier enregistrement', 100);
```

### Insertion sans lister les colonnes (ordre de la table)

```sql
INSERT INTO MA_TABLE VALUES ('MT002', 'Deuxième', 200, SYSDATE, 1, 1);
```

### Insertion avec plusieurs lignes (Oracle 11g+)

```sql
INSERT ALL
    INTO MA_TABLE (id, libelle, quantite) VALUES ('MT003', 'Troisième', 300)
    INTO MA_TABLE (id, libelle, quantite) VALUES ('MT004', 'Quatrième', 400)
SELECT * FROM DUAL;
```

### Insertion avec SELECT

```sql
INSERT INTO MA_TABLE_ARCHIVE
SELECT * FROM MA_TABLE WHERE daty < SYSDATE - 365;
```

### Insertion d'une date

```sql
INSERT INTO MA_TABLE (id, daty)
VALUES ('MT005', TO_DATE('07/10/2026', 'DD/MM/YYYY'));
```

### Insertion d'un timestamp

```sql
INSERT INTO MA_TABLE (id, dateheure)
VALUES ('MT006', TO_TIMESTAMP('07/10/2026 14:30:00', 'DD/MM/YYYY HH24:MI:SS'));
```

### Insertion avec séquence (Oracle classique)

```sql
INSERT INTO MA_TABLE (id, libelle)
VALUES (SEQ_MATABLE.NEXTVAL, 'Auto');
```

### Insertion avec fonction APJ (projet)

```sql
INSERT INTO MA_TABLE (id, libelle)
VALUES ('MT' || GETSEQMATABLE, 'Auto via fonction');
```

### Valider la transaction

```sql
COMMIT;
```

---

## 7. UPDATE — Modifier des données

### Update simple

```sql
UPDATE MA_TABLE SET libelle = 'Nouveau libellé' WHERE id = 'MT001';
```

### Update plusieurs colonnes

```sql
UPDATE MA_TABLE
SET libelle = 'Modifié',
    quantite = 150,
    etat = 11
WHERE id = 'MT001';
```

### Update avec calcul

```sql
UPDATE MA_TABLE SET quantite = quantite + 10 WHERE id = 'MT001';
```

### Update toutes les lignes (attention !)

```sql
UPDATE MA_TABLE SET etat = 1;
```

⚠️ **Sans `WHERE`, toutes les lignes sont modifiées.**

### Update avec sous-requête

```sql
UPDATE MA_TABLE
SET libelle = (SELECT nom FROM AUTRE_TABLE WHERE id = 'AT001')
WHERE id = 'MT001';
```

### Update avec CASE

```sql
UPDATE MA_TABLE
SET etat = CASE
    WHEN quantite > 100 THEN 11
    WHEN quantite > 0   THEN 1
    ELSE 0
END;
```

### Update avec jointure (Oracle)

```sql
UPDATE MA_TABLE mt
SET (libelle_autre) = (
    SELECT at.nom FROM AUTRE_TABLE at WHERE at.id = mt.idref
)
WHERE EXISTS (SELECT 1 FROM AUTRE_TABLE at WHERE at.id = mt.idref);
```

### Valider la transaction

```sql
COMMIT;
```

---

## 8. DELETE — Supprimer des données

### Delete avec condition

```sql
DELETE FROM MA_TABLE WHERE id = 'MT001';
```

### Delete plusieurs lignes

```sql
DELETE FROM MA_TABLE WHERE etat = 0;
```

### Delete toutes les lignes

```sql
DELETE FROM MA_TABLE;
```

⚠️ Plus lent que `TRUNCATE` mais permet le `ROLLBACK`.

### Truncate (rapide, non rollbackable)

```sql
TRUNCATE TABLE MA_TABLE;
```

### Delete avec sous-requête

```sql
DELETE FROM MA_TABLE
WHERE idref IN (SELECT id FROM AUTRE_TABLE WHERE etat = 0);
```

### Delete avec EXISTS

```sql
DELETE FROM MA_TABLE mt
WHERE EXISTS (
    SELECT 1 FROM AUTRE_TABLE at
    WHERE at.id = mt.idref AND at.etat = 0
);
```

### Tester avant de supprimer

```sql
-- 1. Compter ce qu'on va supprimer
SELECT COUNT(*) FROM MA_TABLE WHERE etat = 0;

-- 2. Vérifier les lignes
SELECT * FROM MA_TABLE WHERE etat = 0;

-- 3. Puis supprimer
DELETE FROM MA_TABLE WHERE etat = 0;
```

### Valider la transaction

```sql
COMMIT;
```

---

## 9. Contraintes (PK, FK, CHECK, UNIQUE)

### Clé primaire

```sql
-- À la création
CREATE TABLE MA_TABLE (
    id VARCHAR2(30) PRIMARY KEY,
    ...
);

-- Après création
ALTER TABLE MA_TABLE ADD CONSTRAINT pk_ma_table PRIMARY KEY (id);
```

### Clé étrangère (FK)

```sql
ALTER TABLE MA_TABLE ADD CONSTRAINT fk_mt_ref
    FOREIGN KEY (idref) REFERENCES AUTRE_TABLE(id);
```

**Avec ON DELETE CASCADE :**

```sql
ALTER TABLE MA_TABLE ADD CONSTRAINT fk_mt_ref
    FOREIGN KEY (idref) REFERENCES AUTRE_TABLE(id)
    ON DELETE CASCADE;
```

### Contrainte UNIQUE

```sql
ALTER TABLE MA_TABLE ADD CONSTRAINT uk_mt_code UNIQUE (code);
```

### Contrainte CHECK

```sql
ALTER TABLE MA_TABLE ADD CONSTRAINT ck_mt_etat
    CHECK (etat IN (0, 1, 11, 9));
```

**CHECK sur plage :**

```sql
ALTER TABLE MA_TABLE ADD CONSTRAINT ck_mt_qte CHECK (quantite >= 0);
```

**CHECK conditionnel :**

```sql
ALTER TABLE BONRECEPTION_POUSSIN ADD CONSTRAINT ck_brp_magasin
    CHECK (typereception = 'TR01' OR idmagasin IS NOT NULL);
```

### Voir les contraintes d'une table

```sql
SELECT constraint_name, constraint_type, search_condition
FROM user_constraints
WHERE table_name = 'MA_TABLE';

-- Avec les colonnes
SELECT a.constraint_name, a.constraint_type, c.column_name
FROM user_constraints a
JOIN user_cons_columns c ON a.constraint_name = c.constraint_name
WHERE a.table_name = 'MA_TABLE';
```

### Supprimer une contrainte

```sql
ALTER TABLE MA_TABLE DROP CONSTRAINT nom_contrainte;
```

### Désactiver / réactiver une contrainte

```sql
ALTER TABLE MA_TABLE DISABLE CONSTRAINT nom_contrainte;
ALTER TABLE MA_TABLE ENABLE  CONSTRAINT nom_contrainte;
```

---

## 10. Index

### Créer un index

```sql
CREATE INDEX idx_mt_daty ON MA_TABLE (daty);
```

### Index composite

```sql
CREATE INDEX idx_mt_ref_etat ON MA_TABLE (idref, etat);
```

### Index unique

```sql
CREATE UNIQUE INDEX uk_mt_code ON MA_TABLE (code);
```

### Voir les index d'une table

```sql
SELECT index_name, column_name
FROM user_ind_columns
WHERE table_name = 'MA_TABLE';
```

### Supprimer un index

```sql
DROP INDEX idx_mt_daty;
```

### Quand créer un index ?

| Colonne | Index recommandé |
|---|---|
| PK | automatique |
| FK | oui (Oracle ne le fait pas tout seul) |
| Colonnes filtrées souvent (`daty`, `etat`) | oui |
| Colonnes de tri | oui |
| Colonnes rarement utilisées en WHERE | non |

---

## 11. Séquences

### Créer une séquence

```sql
CREATE SEQUENCE SEQ_MATABLE START WITH 1;
```

**Avec options :**

```sql
CREATE SEQUENCE SEQ_MATABLE
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;
```

### Utiliser une séquence

```sql
-- Valeur suivante
SELECT SEQ_MATABLE.NEXTVAL FROM DUAL;

-- Valeur courante (dernière générée)
SELECT SEQ_MATABLE.CURRVAL FROM DUAL;
```

### Dans un INSERT

```sql
INSERT INTO MA_TABLE (id, libelle)
VALUES ('MT' || SEQ_MATABLE.NEXTVAL, 'Auto');
```

### Voir les séquences existantes

```sql
SELECT sequence_name FROM user_sequences;
```

### Supprimer une séquence

```sql
DROP SEQUENCE SEQ_MATABLE;
```

---

## 12. Fonctions `getseq` (APJ)

**En APJ, on n'appelle pas les séquences directement.** On crée une **fonction** qui expose la séquence.

### Le pattern

```sql
-- 1. La séquence interne
CREATE SEQUENCE SEQ_MATABLE START WITH 1;

-- 2. La fonction APJ
CREATE OR REPLACE FUNCTION GETSEQMATABLE RETURN NUMBER IS
    v NUMBER;
BEGIN
    SELECT SEQ_MATABLE.NEXTVAL INTO v FROM DUAL;
    RETURN v;
END;
/
```

### Utilisation

```sql
-- Test direct
SELECT GETSEQMATABLE FROM DUAL;    -- 1
SELECT GETSEQMATABLE FROM DUAL;    -- 2

-- Dans un INSERT
INSERT INTO MA_TABLE (id, libelle)
VALUES ('MT' || GETSEQMATABLE, 'Auto');
```

### Pourquoi une fonction ?

- `Utilitaire.getMaxSeq(nom, c)` fait un `SELECT nom FROM DUAL`
- Cela **ne marche pas** avec une séquence (qui a besoin de `.NEXTVAL`)
- On wrappe la séquence dans une fonction pour uniformiser

### Convention de nommage

| Type | Préfixe | Exemple |
|---|---|---|
| Séquence interne | `SEQ_` | `SEQ_MATABLE` |
| Fonction APJ | `GETSEQ` | `GETSEQMATABLE` |

### En Java

```java
@Override
public void construirePK(Connection c) throws Exception {
    this.preparePk("MT", "GETSEQMATABLE");   // ← la fonction
    this.setId(makePK(c));
}
```

### Voir les fonctions existantes

```sql
SELECT object_name FROM user_objects
WHERE object_type = 'FUNCTION'
  AND object_name LIKE 'GETSEQ%';
```

### Supprimer une fonction

```sql
DROP FUNCTION GETSEQMATABLE;
```

---

## 13. Vues

### Créer une vue

```sql
CREATE OR REPLACE VIEW MA_TABLE_LIB AS
SELECT t.id, t.libelle, t.quantite,
       r.nom AS reflib,
       CASE t.etat WHEN 0 THEN 'Annule'
                   WHEN 1 THEN 'Cree'
                   WHEN 11 THEN 'Vise'
                   ELSE 'Autre' END AS etatlib
FROM MA_TABLE t
LEFT JOIN AUTRE_TABLE r ON r.id = t.idref;
```

### Utilisation

```sql
SELECT * FROM MA_TABLE_LIB WHERE etatlib = 'Vise';
```

### Supprimer une vue

```sql
DROP VIEW MA_TABLE_LIB;
```

### Voir les vues existantes

```sql
SELECT view_name FROM user_views;
```

---

## 14. Menu

### Insérer un menu principal

```sql
INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUXXX', 'Nom affiché', 'egg',
        'module.jsp?but=chemin/page-liste.jsp', 50, 1, NULL);
```

### Insérer un sous-menu

```sql
INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUXXX_YYY', 'Sous-menu', 'shopping_cart',
        'module.jsp?but=chemin/page.jsp', 1, 2, 'MENUXXX');
```

### Rattacher un menu à un rôle

```sql
INSERT INTO usermenu (id, IDMENU, idrole, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUXXX', 'resp', 0);
```

### Supprimer proprement (usermenu d'abord, puis MENUDYNAMIQUE)

```sql
-- 1. usermenu
DELETE FROM usermenu WHERE IDMENU LIKE 'MENUXXX%';

-- 2. Sous-menus (niveau 2)
DELETE FROM MENUDYNAMIQUE WHERE id LIKE 'MENUXXX%' AND id <> 'MENUXXX';

-- 3. Menu principal
DELETE FROM MENUDYNAMIQUE WHERE id = 'MENUXXX';

COMMIT;
```

### Voir les menus existants

```sql
SELECT id, libelle, niveau, id_pere FROM MENUDYNAMIQUE
WHERE id LIKE 'MENU%'
ORDER BY niveau, rang;
```

---

## 15. Vérifications utiles

### Existence d'une table

```sql
SELECT table_name FROM user_tables WHERE table_name = 'MA_TABLE';
```

### Structure d'une table

```sql
DESC MA_TABLE;
-- ou
SELECT column_name, data_type, data_length, nullable
FROM user_tab_columns WHERE table_name = 'MA_TABLE';
```

### Compter les lignes

```sql
SELECT COUNT(*) FROM MA_TABLE;
```

### Existence d'une séquence

```sql
SELECT sequence_name FROM user_sequences WHERE sequence_name = 'SEQ_MATABLE';
```

### Existence d'une fonction

```sql
SELECT object_name, object_type, status FROM user_objects
WHERE object_name = 'GETSEQMATABLE';
```

### Voir toutes les tables d'un module

```sql
SELECT table_name FROM user_tables
WHERE table_name LIKE 'MONMODULE%';
```

### Toutes les FK pointant vers une table

```sql
SELECT c.table_name, c.constraint_name
FROM user_constraints c
WHERE c.constraint_type = 'R'
  AND c.r_constraint_name IN (
      SELECT constraint_name FROM user_constraints
      WHERE table_name = 'MA_TABLE' AND constraint_type = 'P'
  );
```

### Vérifier si une colonne existe

```sql
SELECT column_name FROM user_tab_columns
WHERE table_name = 'MA_TABLE' AND column_name = 'NOUVELLE_COLONNE';
```

### Trouver les objets d'un module à dropper

```sql
-- Tables
SELECT table_name FROM user_tables WHERE table_name LIKE '%POUSSIN%';

-- Vues
SELECT view_name FROM user_views WHERE view_name LIKE '%POUSSIN%';

-- Séquences
SELECT sequence_name FROM user_sequences WHERE sequence_name LIKE '%POUSSIN%';

-- Fonctions
SELECT object_name FROM user_objects
WHERE object_type = 'FUNCTION' AND object_name LIKE '%POUSSIN%';
```

---

## Annexes

### Convention de nommage du projet

| Type d'objet | Préfixe | Exemple |
|---|---|---|
| Table métier | `NOM_MODULE` | `BONCOMMANDE_POUSSIN` |
| Table fille | `NOM_MODULE_FILLE` | `BONCOMMANDE_POUSSIN_FILLE` |
| Vue avec libellés | `NOM_MODULE_LIB` | `BONCOMMANDE_POUSSIN_LIB` |
| Séquence | `SEQ_NOM` | `SEQ_BCPOUSSIN` |
| Fonction APJ | `GETSEQNOM` | `GETSEQBCPOUSSIN` |
| Contrainte PK | `pk_` | `pk_bcp` |
| Contrainte FK | `fk_` | `fk_bcpf_mere` |
| Contrainte CHECK | `ck_` | `ck_bcp_etat` |
| Contrainte UNIQUE | `uk_` | `uk_bcp_ref` |
| Index | `idx_` | `idx_bcp_daty` |

### Vocabulaire malgache courant

| Mot | Signification |
|---|---|
| `daty` | date |
| `val` | valeur (libellé) |
| `desce` | description |
| `valiny` | réponse / résultat |
| `maty` | mort |
| `very` | perdu |
| `marary` | malade |
| `sain` | sain |

### États standards

| Valeur | Signification |
|---|---|
| 0 | Annulé |
| 1 | Créé |
| 9 | Clôturé |
| 11 | Visé / Validé |

### Commandes d'exécution SQL*Plus

| Commande | Effet |
|---|---|
| `SET SERVEROUTPUT ON;` | Affiche les `DBMS_OUTPUT` |
| `SET LINESIZE 200;` | Largeur d'affichage |
| `SET PAGESIZE 100;` | Hauteur de page |
| `SPOOL fichier.txt;` | Redirige la sortie |
| `SPOOL OFF;` | Arrête la redirection |
| `COMMIT;` | Valide la transaction |
| `ROLLBACK;` | Annule la transaction |
| `@script.sql` | Exécute un fichier |
| `SHOW USER;` | Affiche l'utilisateur connecté |
| `EXIT;` | Quitter |