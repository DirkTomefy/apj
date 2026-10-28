SET DEFINE OFF
SET SQLBLANKLINES ON
SET SERVEROUTPUT ON

-- ============================================================================
--  MODULE FACTURE / AVOIR (a_tahina.facture)
--
--  Tout est prefixe ATH_ pour ne pas se melanger avec FACTURE, CLIENT,
--  PRODUIT, AVOIRFC... deja presents dans la base.
--  Les colonnes n'ont pas d'underscore : elles portent le meme nom que les
--  attributs de l'ORM Java.
--
--  Regles metier (appliquees cote Java, pas en SQL) :
--   - un avoir se fait sur une facture visee (etat 11) ; il compte dans le
--     solde du client quand il est vise lui aussi ;
--   - qte d'une ligne d'avoir <= ATH_FACTURE_FILLE_LIB.qtedisponible ;
--   - au visa d'une facture, le solde du client est deduit automatiquement
--     (avoirs les plus anciens d'abord) : une ligne ATH_AVOIR_USAGE par avoir
--     consomme + ATH_FACTURE.montantavoir ; l'avoir peut etre applique a une
--     facture qui ne contient pas les produits retournes ;
--   - la deduction est PLAFONNEE : montant deduit = MIN(solde du client,
--     TTC de la facture x ATH_CLIENT.pourcentageavoir / 100). Une facture ne
--     peut donc jamais etre payee entierement par avoir (pourcentage < 100) ;
--   - annuler une facture visee supprime ses lignes ATH_AVOIR_USAGE
--     (le credit est restitue).
-- ============================================================================

PROMPT ============================================================
PROMPT 1. SEQUENCES
PROMPT ============================================================

CREATE SEQUENCE SEQATHCLIENT        START WITH 1;
CREATE SEQUENCE SEQATHPRODUIT       START WITH 1;
CREATE SEQUENCE SEQATHFACTURE       START WITH 1;
CREATE SEQUENCE SEQATHFACTUREFILLE  START WITH 1;
CREATE SEQUENCE SEQATHAVOIR         START WITH 1;
CREATE SEQUENCE SEQATHAVOIRFILLE    START WITH 1;
CREATE SEQUENCE SEQATHAVOIRUSAGE    START WITH 1;

PROMPT ============================================================
PROMPT 2. FONCTIONS APJ (GETSEQ...)
PROMPT ============================================================

CREATE OR REPLACE FUNCTION GETSEQATHCLIENT RETURN NUMBER IS
    v NUMBER;
BEGIN
    SELECT SEQATHCLIENT.NEXTVAL INTO v FROM DUAL;
    RETURN v;
END;
/

CREATE OR REPLACE FUNCTION GETSEQATHPRODUIT RETURN NUMBER IS
    v NUMBER;
BEGIN
    SELECT SEQATHPRODUIT.NEXTVAL INTO v FROM DUAL;
    RETURN v;
END;
/

CREATE OR REPLACE FUNCTION GETSEQATHFACTURE RETURN NUMBER IS
    v NUMBER;
BEGIN
    SELECT SEQATHFACTURE.NEXTVAL INTO v FROM DUAL;
    RETURN v;
END;
/

CREATE OR REPLACE FUNCTION GETSEQATHFACTUREFILLE RETURN NUMBER IS
    v NUMBER;
BEGIN
    SELECT SEQATHFACTUREFILLE.NEXTVAL INTO v FROM DUAL;
    RETURN v;
END;
/

CREATE OR REPLACE FUNCTION GETSEQATHAVOIR RETURN NUMBER IS
    v NUMBER;
BEGIN
    SELECT SEQATHAVOIR.NEXTVAL INTO v FROM DUAL;
    RETURN v;
END;
/

CREATE OR REPLACE FUNCTION GETSEQATHAVOIRFILLE RETURN NUMBER IS
    v NUMBER;
BEGIN
    SELECT SEQATHAVOIRFILLE.NEXTVAL INTO v FROM DUAL;
    RETURN v;
END;
/

CREATE OR REPLACE FUNCTION GETSEQATHAVOIRUSAGE RETURN NUMBER IS
    v NUMBER;
BEGIN
    SELECT SEQATHAVOIRUSAGE.NEXTVAL INTO v FROM DUAL;
    RETURN v;
END;
/

PROMPT ============================================================
PROMPT 3. TABLES
PROMPT ============================================================

-- Client
CREATE TABLE ATH_CLIENT (
    id        VARCHAR2(30) PRIMARY KEY,
    nom       VARCHAR2(200) NOT NULL,
    contact   VARCHAR2(100),
    adresse   VARCHAR2(300),
    mail      VARCHAR2(150),
    -- pourcentageavoir : % du TTC d'une facture payable par avoir.
    -- (pas de commentaire apres DEFAULT : Oracle le garde dans la valeur par defaut)
    pourcentageavoir NUMBER(5,2) DEFAULT 40
);

-- Produit
CREATE TABLE ATH_PRODUIT (
    id        VARCHAR2(30) PRIMARY KEY,
    libelle   VARCHAR2(200) NOT NULL,
    unite     VARCHAR2(30),
    pu        NUMBER(15,2) DEFAULT 0,      -- prix de vente par defaut
    actif     NUMBER(1) DEFAULT 1
);

-- Facture (mere)
--   montantavoir : credit deduit sur cette facture, fige au visa
CREATE TABLE ATH_FACTURE (
    id            VARCHAR2(30) PRIMARY KEY,
    daty          DATE,
    reference     VARCHAR2(100),
    idclient      VARCHAR2(30) NOT NULL,
    etat          NUMBER(2) DEFAULT 1,
    remarque      VARCHAR2(500),
    montantavoir  NUMBER(15,2) DEFAULT 0
);

-- Facture (fille) : tva = pourcentage de la ligne (0 par defaut)
CREATE TABLE ATH_FACTURE_FILLE (
    id          VARCHAR2(30) PRIMARY KEY,
    idmere      VARCHAR2(30) NOT NULL,
    idproduit   VARCHAR2(30) NOT NULL,
    qte         NUMBER(15,2),
    pu          NUMBER(15,2),
    tva         NUMBER(5,2) DEFAULT 0
);

-- Avoir (mere) : idclient est repris de la facture (calcul du solde client)
CREATE TABLE ATH_AVOIR (
    id          VARCHAR2(30) PRIMARY KEY,
    daty        DATE,
    reference   VARCHAR2(100),
    idfacture   VARCHAR2(30) NOT NULL,
    idclient    VARCHAR2(30) NOT NULL,
    etat        NUMBER(2) DEFAULT 1,
    motif       VARCHAR2(300),
    remarque    VARCHAR2(500)
);

-- Avoir (fille) : pointe une ligne de facture ; pu et tva sont repris de cette ligne
CREATE TABLE ATH_AVOIR_FILLE (
    id              VARCHAR2(30) PRIMARY KEY,
    idmere          VARCHAR2(30) NOT NULL,
    idfacturefille  VARCHAR2(30) NOT NULL,
    qte             NUMBER(15,2),
    pu              NUMBER(15,2),
    tva             NUMBER(5,2) DEFAULT 0
);

-- Consommation d'un avoir par une facture (tracabilite + restitution a l'annulation)
CREATE TABLE ATH_AVOIR_USAGE (
    id          VARCHAR2(30) PRIMARY KEY,
    idavoir     VARCHAR2(30) NOT NULL,
    idfacture   VARCHAR2(30) NOT NULL,
    montant     NUMBER(15,2),
    daty        DATE
);

PROMPT ============================================================
PROMPT 4. CONTRAINTES CHECK
PROMPT ============================================================

ALTER TABLE ATH_CLIENT
    ADD CONSTRAINT ck_athc_pctavoir CHECK (pourcentageavoir >= 0 AND pourcentageavoir <= 100);

ALTER TABLE ATH_PRODUIT
    ADD CONSTRAINT ck_athp_pu CHECK (pu >= 0);

ALTER TABLE ATH_FACTURE
    ADD CONSTRAINT ck_athf_etat CHECK (etat IN (0,1,11,9));
ALTER TABLE ATH_FACTURE
    ADD CONSTRAINT ck_athf_avoir CHECK (montantavoir >= 0);

ALTER TABLE ATH_FACTURE_FILLE
    ADD CONSTRAINT ck_athff_val CHECK (qte >= 0 AND pu >= 0);
ALTER TABLE ATH_FACTURE_FILLE
    ADD CONSTRAINT ck_athff_tva CHECK (tva >= 0 AND tva <= 100);

ALTER TABLE ATH_AVOIR
    ADD CONSTRAINT ck_atha_etat CHECK (etat IN (0,1,11,9));

ALTER TABLE ATH_AVOIR_FILLE
    ADD CONSTRAINT ck_athaf_val CHECK (qte >= 0 AND pu >= 0);
ALTER TABLE ATH_AVOIR_FILLE
    ADD CONSTRAINT ck_athaf_tva CHECK (tva >= 0 AND tva <= 100);

ALTER TABLE ATH_AVOIR_USAGE
    ADD CONSTRAINT ck_athau_montant CHECK (montant > 0);

PROMPT ============================================================
PROMPT 5. CLES ETRANGERES
PROMPT ============================================================

ALTER TABLE ATH_FACTURE
    ADD CONSTRAINT fk_athf_client FOREIGN KEY (idclient) REFERENCES ATH_CLIENT(id);

ALTER TABLE ATH_FACTURE_FILLE
    ADD CONSTRAINT fk_athff_mere FOREIGN KEY (idmere) REFERENCES ATH_FACTURE(id);
ALTER TABLE ATH_FACTURE_FILLE
    ADD CONSTRAINT fk_athff_produit FOREIGN KEY (idproduit) REFERENCES ATH_PRODUIT(id);

ALTER TABLE ATH_AVOIR
    ADD CONSTRAINT fk_atha_facture FOREIGN KEY (idfacture) REFERENCES ATH_FACTURE(id);
ALTER TABLE ATH_AVOIR
    ADD CONSTRAINT fk_atha_client FOREIGN KEY (idclient) REFERENCES ATH_CLIENT(id);

ALTER TABLE ATH_AVOIR_FILLE
    ADD CONSTRAINT fk_athaf_mere FOREIGN KEY (idmere) REFERENCES ATH_AVOIR(id);
ALTER TABLE ATH_AVOIR_FILLE
    ADD CONSTRAINT fk_athaf_ff FOREIGN KEY (idfacturefille) REFERENCES ATH_FACTURE_FILLE(id);

ALTER TABLE ATH_AVOIR_USAGE
    ADD CONSTRAINT fk_athau_avoir FOREIGN KEY (idavoir) REFERENCES ATH_AVOIR(id);
ALTER TABLE ATH_AVOIR_USAGE
    ADD CONSTRAINT fk_athau_facture FOREIGN KEY (idfacture) REFERENCES ATH_FACTURE(id);

PROMPT ============================================================
PROMPT 6. INDEX
PROMPT ============================================================

CREATE INDEX idx_athf_client   ON ATH_FACTURE (idclient);
CREATE INDEX idx_athf_daty     ON ATH_FACTURE (daty);
CREATE INDEX idx_athff_mere    ON ATH_FACTURE_FILLE (idmere);
CREATE INDEX idx_athff_produit ON ATH_FACTURE_FILLE (idproduit);
CREATE INDEX idx_atha_facture  ON ATH_AVOIR (idfacture);
CREATE INDEX idx_atha_client   ON ATH_AVOIR (idclient);
CREATE INDEX idx_athaf_mere    ON ATH_AVOIR_FILLE (idmere);
CREATE INDEX idx_athaf_ff      ON ATH_AVOIR_FILLE (idfacturefille);
CREATE INDEX idx_athau_avoir   ON ATH_AVOIR_USAGE (idavoir);
CREATE INDEX idx_athau_facture ON ATH_AVOIR_USAGE (idfacture);

PROMPT ============================================================
PROMPT 7. VUES
PROMPT ============================================================

-- Lignes de facture : montants + quantite deja retournee / encore retournable.
-- Un avoir annule (etat 0) ne compte pas dans les retours.
CREATE OR REPLACE VIEW ATH_FACTURE_FILLE_LIB AS
SELECT ff.id, ff.idmere, ff.idproduit, ff.qte, ff.pu, ff.tva,
       p.libelle                                   AS produitlib,
       ROUND(ff.qte * ff.pu, 2)                    AS montantht,
       ROUND(ff.qte * ff.pu * ff.tva / 100, 2)     AS montanttva,
       ROUND(ff.qte * ff.pu, 2)
         + ROUND(ff.qte * ff.pu * ff.tva / 100, 2) AS montantttc,
       NVL(r.qteretournee, 0)                      AS qteretournee,
       ff.qte - NVL(r.qteretournee, 0)             AS qtedisponible
FROM ATH_FACTURE_FILLE ff
LEFT JOIN ATH_PRODUIT p ON p.id = ff.idproduit
LEFT JOIN (SELECT af.idfacturefille, SUM(af.qte) AS qteretournee
           FROM ATH_AVOIR_FILLE af
           JOIN ATH_AVOIR a ON a.id = af.idmere
           WHERE a.etat <> 0
           GROUP BY af.idfacturefille) r ON r.idfacturefille = ff.id;
/

-- Facture avec libelle client, totaux et net a payer
CREATE OR REPLACE VIEW ATH_FACTURE_LIB AS
SELECT f.id, f.daty, f.reference, f.idclient, f.etat, f.remarque,
       NVL(f.montantavoir, 0)                        AS montantavoir,
       c.nom                                         AS idclientlib,
       NVL(t.montantht, 0)                           AS montantht,
       NVL(t.montanttva, 0)                          AS montanttva,
       NVL(t.montantttc, 0)                          AS montantttc,
       NVL(c.pourcentageavoir, 0)                    AS pourcentageavoir,
       ROUND(NVL(t.montantttc, 0) * NVL(c.pourcentageavoir, 0) / 100, 2) AS plafondavoir,
       NVL(t.montantttc, 0) - NVL(f.montantavoir, 0) AS netapayer,
       CASE f.etat
            WHEN 0  THEN 'Annule'
            WHEN 1  THEN 'Cree'
            WHEN 11 THEN 'Vise'
            WHEN 9  THEN 'Cloture'
            ELSE 'Autre'
       END AS etatlib
FROM ATH_FACTURE f
LEFT JOIN ATH_CLIENT c ON c.id = f.idclient
LEFT JOIN (SELECT idmere,
                  SUM(montantht)  AS montantht,
                  SUM(montanttva) AS montanttva,
                  SUM(montantttc) AS montantttc
           FROM ATH_FACTURE_FILLE_LIB
           GROUP BY idmere) t ON t.idmere = f.id;
/

-- Lignes d'avoir : montants calcules avec pu / tva repris de la ligne de facture
CREATE OR REPLACE VIEW ATH_AVOIR_FILLE_LIB AS
SELECT af.id, af.idmere, af.idfacturefille, af.qte, af.pu, af.tva,
       p.libelle                                   AS produitlib,
       ff.qte                                      AS qtefacturee,
       ROUND(af.qte * af.pu, 2)                    AS montantht,
       ROUND(af.qte * af.pu * af.tva / 100, 2)     AS montanttva,
       ROUND(af.qte * af.pu, 2)
         + ROUND(af.qte * af.pu * af.tva / 100, 2) AS montantttc
FROM ATH_AVOIR_FILLE af
LEFT JOIN ATH_FACTURE_FILLE ff ON ff.id = af.idfacturefille
LEFT JOIN ATH_PRODUIT       p  ON p.id  = ff.idproduit;
/

-- Avoir avec totaux, montant deja utilise et reste a appliquer
-- (reste = 0 tant que l'avoir n'est pas vise)
CREATE OR REPLACE VIEW ATH_AVOIR_LIB AS
SELECT a.id, a.daty, a.reference, a.idfacture, a.idclient, a.etat,
       a.motif, a.remarque,
       NVL(f.reference, f.id)                        AS idfacturelib,
       c.nom                                         AS idclientlib,
       NVL(t.montantht, 0)                           AS montantht,
       NVL(t.montanttva, 0)                          AS montanttva,
       NVL(t.montantttc, 0)                          AS montantttc,
       NVL(u.montantutilise, 0)                      AS montantutilise,
       CASE WHEN a.etat = 11
            THEN NVL(t.montantttc, 0) - NVL(u.montantutilise, 0)
            ELSE 0
       END                                           AS reste,
       CASE a.etat
            WHEN 0  THEN 'Annule'
            WHEN 1  THEN 'Cree'
            WHEN 11 THEN 'Vise'
            WHEN 9  THEN 'Cloture'
            ELSE 'Autre'
       END AS etatlib
FROM ATH_AVOIR a
LEFT JOIN ATH_FACTURE f ON f.id = a.idfacture
LEFT JOIN ATH_CLIENT  c ON c.id = a.idclient
LEFT JOIN (SELECT idmere,
                  SUM(montantht)  AS montantht,
                  SUM(montanttva) AS montanttva,
                  SUM(montantttc) AS montantttc
           FROM ATH_AVOIR_FILLE_LIB
           GROUP BY idmere) t ON t.idmere = a.id
LEFT JOIN (SELECT idavoir, SUM(montant) AS montantutilise
           FROM ATH_AVOIR_USAGE
           GROUP BY idavoir) u ON u.idavoir = a.id;
/

-- Solde de credit par client (avoirs vises - credit deja consomme)
CREATE OR REPLACE VIEW ATH_CLIENT_SOLDE AS
SELECT c.id, c.nom, c.pourcentageavoir,
       NVL(s.totalavoir, 0)                         AS totalavoir,
       NVL(s.totalutilise, 0)                       AS totalutilise,
       NVL(s.totalavoir, 0) - NVL(s.totalutilise, 0) AS solde
FROM ATH_CLIENT c
LEFT JOIN (SELECT idclient,
                  SUM(montantttc)     AS totalavoir,
                  SUM(montantutilise) AS totalutilise
           FROM ATH_AVOIR_LIB
           WHERE etat = 11
           GROUP BY idclient) s ON s.idclient = c.id;
/

PROMPT ============================================================
PROMPT 8. DONNEES D'EXEMPLE (via les fonctions GETSEQ, comme le fait l'ORM)
PROMPT ============================================================

INSERT INTO ATH_CLIENT (id, nom, contact, adresse, pourcentageavoir)
VALUES ('ACL' || LPAD(GETSEQATHCLIENT, 6, '0'), 'Client Exemple', '034 00 000 00', 'Antananarivo', 40);

INSERT INTO ATH_PRODUIT (id, libelle, unite, pu)
VALUES ('APR' || LPAD(GETSEQATHPRODUIT, 6, '0'), 'Coca-Cola', 'bouteille', 2500);

INSERT INTO ATH_PRODUIT (id, libelle, unite, pu)
VALUES ('APR' || LPAD(GETSEQATHPRODUIT, 6, '0'), 'Poussin', 'piece', 1200);

COMMIT;

PROMPT ============================================================
PROMPT 9. VERIFICATION
PROMPT ============================================================

SELECT object_type, COUNT(*) AS nb
FROM user_objects
WHERE object_name LIKE 'ATH\_%' ESCAPE '\' OR object_name LIKE 'SEQATH%' OR object_name LIKE 'GETSEQATH%'
GROUP BY object_type
ORDER BY object_type;

SELECT status, COUNT(*) AS nb
FROM user_objects
WHERE (object_name LIKE 'ATH\_%' ESCAPE '\' OR object_name LIKE 'GETSEQATH%')
  AND object_type IN ('VIEW', 'FUNCTION')
GROUP BY status;

-- ============================================================================
--  ANNULATION (a decommenter pour tout supprimer ; filles avant meres)
-- ============================================================================
-- DROP VIEW ATH_CLIENT_SOLDE;
-- DROP VIEW ATH_AVOIR_LIB;
-- DROP VIEW ATH_AVOIR_FILLE_LIB;
-- DROP VIEW ATH_FACTURE_LIB;
-- DROP VIEW ATH_FACTURE_FILLE_LIB;
-- DROP TABLE ATH_AVOIR_USAGE;
-- DROP TABLE ATH_AVOIR_FILLE;
-- DROP TABLE ATH_AVOIR;
-- DROP TABLE ATH_FACTURE_FILLE;
-- DROP TABLE ATH_FACTURE;
-- DROP TABLE ATH_PRODUIT;
-- DROP TABLE ATH_CLIENT;
-- DROP FUNCTION GETSEQATHCLIENT;
-- DROP FUNCTION GETSEQATHPRODUIT;
-- DROP FUNCTION GETSEQATHFACTURE;
-- DROP FUNCTION GETSEQATHFACTUREFILLE;
-- DROP FUNCTION GETSEQATHAVOIR;
-- DROP FUNCTION GETSEQATHAVOIRFILLE;
-- DROP FUNCTION GETSEQATHAVOIRUSAGE;
-- DROP SEQUENCE SEQATHCLIENT;
-- DROP SEQUENCE SEQATHPRODUIT;
-- DROP SEQUENCE SEQATHFACTURE;
-- DROP SEQUENCE SEQATHFACTUREFILLE;
-- DROP SEQUENCE SEQATHAVOIR;
-- DROP SEQUENCE SEQATHAVOIRFILLE;
-- DROP SEQUENCE SEQATHAVOIRUSAGE;
