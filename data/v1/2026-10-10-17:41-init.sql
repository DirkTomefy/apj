-- ============================================================================
--  MODULE POUSSIN - Script d'initialisation complet
--  Base : Oracle
--  Date : 2026-10-10
-- ============================================================================

SET SERVEROUTPUT ON;

-- ============================================================================
-- 1. DICTIONNAIRES
-- ============================================================================

CREATE TABLE SANTEPOUSSIN (
    id     VARCHAR2(30) PRIMARY KEY,
    val    VARCHAR2(100),
    desce  VARCHAR2(255)
);

CREATE TABLE LIEURECEPTION (
    id     VARCHAR2(30) PRIMARY KEY,
    val    VARCHAR2(100),
    desce  VARCHAR2(255)
);

CREATE TABLE COMPARTIMENT (
    id        VARCHAR2(30) PRIMARY KEY,
    nom       VARCHAR2(100),
    capacite  NUMBER(10)
);


INSERT INTO SANTEPOUSSIN  VALUES ('SP01', 'SAIN',         'Poussin en bonne sante');
INSERT INTO SANTEPOUSSIN  VALUES ('SP02', 'MARARY',       'Poussin malade');
INSERT INTO LIEURECEPTION VALUES ('TR01', 'Aeroport',     'Reception a l aeroport');
INSERT INTO LIEURECEPTION VALUES ('TR02', 'Intermediaire','Reception lieu intermediaire');
INSERT INTO LIEURECEPTION VALUES ('TR03', 'Magasin',      'Reception en magasin');
INSERT INTO COMPARTIMENT (id, nom, capacite) VALUES ('CMP01', 'C1', 2000);
INSERT INTO COMPARTIMENT (id, nom, capacite) VALUES ('CMP02', 'C2', 2000);
INSERT INTO COMPARTIMENT (id, nom, capacite) VALUES ('CMP03', 'C3', 3000);
INSERT INTO COMPARTIMENT (id, nom, capacite) VALUES ('CMP04', 'C4', 4000);
COMMIT;

-- ============================================================================
-- 2. SÉQUENCES
-- ============================================================================

CREATE SEQUENCE SEQ_BCPOUSSIN           START WITH 1;
CREATE SEQUENCE SEQ_BCPOUSSINFILLE      START WITH 1;
CREATE SEQUENCE SEQ_BRPOUSSIN           START WITH 1;
CREATE SEQUENCE SEQ_COMPARTIMENT        START WITH 1;
CREATE SEQUENCE SEQ_INSERTIONCOMP       START WITH 1;
CREATE SEQUENCE SEQ_INSERTIONCOMPFILLE  START WITH 1;
CREATE SEQUENCE SEQ_SANTEPOUSSIN        START WITH 1;
CREATE SEQUENCE SEQ_LIEURECEPTION       START WITH 1;
CREATE SEQUENCE SEQ_LOT                 START WITH 1;

-- ============================================================================
-- 3. FONCTIONS APJ
-- ============================================================================

CREATE OR REPLACE FUNCTION GETSEQBCPOUSSIN RETURN NUMBER IS
    v NUMBER;
BEGIN
    SELECT SEQ_BCPOUSSIN.NEXTVAL INTO v FROM DUAL;
    RETURN v;
END;
/

CREATE OR REPLACE FUNCTION GETSEQBCPOUSSINFILLE RETURN NUMBER IS
    v NUMBER;
BEGIN
    SELECT SEQ_BCPOUSSINFILLE.NEXTVAL INTO v FROM DUAL;
    RETURN v;
END;
/

CREATE OR REPLACE FUNCTION GETSEQBRPOUSSIN RETURN NUMBER IS
    v NUMBER;
BEGIN
    SELECT SEQ_BRPOUSSIN.NEXTVAL INTO v FROM DUAL;
    RETURN v;
END;
/

CREATE OR REPLACE FUNCTION GETSEQINSERTIONCOMP RETURN NUMBER IS
    v NUMBER;
BEGIN
    SELECT SEQ_INSERTIONCOMP.NEXTVAL INTO v FROM DUAL;
    RETURN v;
END;
/

CREATE OR REPLACE FUNCTION GETSEQINSERTIONCOMPFILLE RETURN NUMBER IS
    v NUMBER;
BEGIN
    SELECT SEQ_INSERTIONCOMPFILLE.NEXTVAL INTO v FROM DUAL;
    RETURN v;
END;
/

CREATE OR REPLACE FUNCTION GETSEQCOMPARTIMENT RETURN NUMBER IS
    v NUMBER;
BEGIN
    SELECT SEQ_COMPARTIMENT.NEXTVAL INTO v FROM DUAL;
    RETURN v;
END;
/

CREATE OR REPLACE FUNCTION GETSEQSANTEPOUSSIN RETURN NUMBER IS
    v NUMBER;
BEGIN
    SELECT SEQ_SANTEPOUSSIN.NEXTVAL INTO v FROM DUAL;
    RETURN v;
END;
/

CREATE OR REPLACE FUNCTION GETSEQLIEURECEPTION RETURN NUMBER IS
    v NUMBER;
BEGIN
    SELECT SEQ_LIEURECEPTION.NEXTVAL INTO v FROM DUAL;
    RETURN v;
END;
/

CREATE OR REPLACE FUNCTION GETSEQLOT RETURN NUMBER IS
    v NUMBER;
BEGIN
    SELECT SEQ_LOT.NEXTVAL INTO v FROM DUAL;
    RETURN v;
END;
/



-- ============================================================================
-- 4. TABLES MÉTIER
-- ============================================================================

-- Bon de commande poussin (mère)
CREATE TABLE BONCOMMANDE_POUSSIN (
    id           VARCHAR2(30) PRIMARY KEY,
    daty         DATE,
    reference    VARCHAR2(100),
    descri       VARCHAR2(500),
    idmagasin    VARCHAR2(30),
    etat         NUMBER(2) DEFAULT 1,
    remarque     VARCHAR2(500)
);

-- Bon de commande poussin (fille)
CREATE TABLE BONCOMMANDE_POUSSIN_FILLE (
    id           VARCHAR2(30) PRIMARY KEY,
    idmere       VARCHAR2(30),
    idproduit    VARCHAR2(30),
    idsouche     VARCHAR2(50),
    qte          NUMBER(15,2),
    pu           NUMBER(15,2)
);

-- Bon de réception poussin (mère)
CREATE TABLE BONRECEPTION_POUSSIN (
    id                 VARCHAR2(30) PRIMARY KEY,
    daty               DATE,
    reference          VARCHAR2(100),
    idboncommande      VARCHAR2(30),
    idboncommandefille VARCHAR2(30),
    idbrprecedent      VARCHAR2(30),
    idlieureception    VARCHAR2(30),
    idmagasin          VARCHAR2(30),
    etat               NUMBER(2) DEFAULT 1,
    qt_avant_cumule    NUMBER(15,2),
    qt_apres_cumule    NUMBER(15,2),
    maty               NUMBER(15,2) DEFAULT 0,
    very               NUMBER(15,2) DEFAULT 0,
    remarque           VARCHAR2(500)
);

-- Insertion compartiment (mère)
CREATE TABLE INSERTION_COMPARTIMENT (
    id               VARCHAR2(30) PRIMARY KEY,
    daty             DATE,
    reference        VARCHAR2(100),
    idbonreception   VARCHAR2(30),
    etat             NUMBER(2) DEFAULT 1,
    remarque         VARCHAR2(500)
);

-- Insertion compartiment (fille)
CREATE TABLE INSERTION_COMPARTIMENT_FILLE (
    id                    VARCHAR2(30) PRIMARY KEY,
    idmere                VARCHAR2(30),
    idcompartiment        VARCHAR2(30),
    qte                   NUMBER(15,2),
    pu                    NUMBER(15,2),
    valeurCompartiment    NUMBER(15,2),
    sexe                  VARCHAR2(10),
    idsante               VARCHAR2(30)
);

-- Lot
CREATE TABLE LOT (
    id                    VARCHAR2(30) PRIMARY KEY,
    idbonreception        VARCHAR2(30),
    idproduit             VARCHAR2(30),
    idsouche              VARCHAR2(50),
    qte                   NUMBER(15,2),
    pu                    NUMBER(15,2),
    sexe                  VARCHAR2(10),
    idsante               VARCHAR2(30),
    idinsertcompfille     VARCHAR2(30),
    idboncommande         VARCHAR2(30),
    qte_actuelle          NUMBER(15,2)
);

-- ============================================================================
-- 5. CONTRAINTES CHECK
-- ============================================================================

ALTER TABLE BONCOMMANDE_POUSSIN
    ADD CONSTRAINT ck_bcp_etat CHECK (etat IN (0,1,11,9));

ALTER TABLE BONCOMMANDE_POUSSIN_FILLE
    ADD CONSTRAINT ck_bcpf_qte CHECK (qte >= 0 AND pu >= 0);

ALTER TABLE BONRECEPTION_POUSSIN
    ADD CONSTRAINT ck_brp_etat CHECK (etat IN (0,1,11,9));

ALTER TABLE BONRECEPTION_POUSSIN
    ADD CONSTRAINT ck_brp_maty CHECK (maty >= 0);

ALTER TABLE BONRECEPTION_POUSSIN
    ADD CONSTRAINT ck_brp_very CHECK (very >= 0);

ALTER TABLE INSERTION_COMPARTIMENT
    ADD CONSTRAINT ck_ic_etat CHECK (etat IN (0,1,11,9));

ALTER TABLE INSERTION_COMPARTIMENT_FILLE
    ADD CONSTRAINT ck_icf_qte CHECK (qte >= 0 AND pu >= 0);

ALTER TABLE INSERTION_COMPARTIMENT_FILLE
    ADD CONSTRAINT ck_icf_sexe CHECK (sexe IN ('0','1'));

ALTER TABLE LOT
    ADD CONSTRAINT ck_lot_qte CHECK (qte >= 0 AND pu >= 0);

ALTER TABLE LOT
    ADD CONSTRAINT ck_lot_qte_actuelle CHECK (qte_actuelle >= 0);

-- ============================================================================
-- 6. CLÉS ÉTRANGÈRES
-- ============================================================================

-- BC
ALTER TABLE BONCOMMANDE_POUSSIN_FILLE
    ADD CONSTRAINT fk_bcpf_mere FOREIGN KEY (idmere)
        REFERENCES BONCOMMANDE_POUSSIN(id);

-- IC
ALTER TABLE INSERTION_COMPARTIMENT
    ADD CONSTRAINT fk_ic_br FOREIGN KEY (idbonreception)
        REFERENCES BONRECEPTION_POUSSIN(id);

ALTER TABLE INSERTION_COMPARTIMENT_FILLE
    ADD CONSTRAINT fk_icf_mere FOREIGN KEY (idmere)
        REFERENCES INSERTION_COMPARTIMENT(id);

ALTER TABLE INSERTION_COMPARTIMENT_FILLE
    ADD CONSTRAINT fk_icf_comp FOREIGN KEY (idcompartiment)
        REFERENCES COMPARTIMENT(id);

ALTER TABLE INSERTION_COMPARTIMENT_FILLE
    ADD CONSTRAINT fk_icf_sexe FOREIGN KEY (sexe)
        REFERENCES SEXE(id);

ALTER TABLE INSERTION_COMPARTIMENT_FILLE
    ADD CONSTRAINT fk_icf_sant FOREIGN KEY (idsante)
        REFERENCES SANTEPOUSSIN(id);

-- BR
ALTER TABLE BONRECEPTION_POUSSIN
    ADD CONSTRAINT fk_brp_bc FOREIGN KEY (idboncommande)
        REFERENCES BONCOMMANDE_POUSSIN(id);

ALTER TABLE BONRECEPTION_POUSSIN
    ADD CONSTRAINT fk_brp_bcf FOREIGN KEY (idboncommandefille)
        REFERENCES BONCOMMANDE_POUSSIN_FILLE(id);

ALTER TABLE BONRECEPTION_POUSSIN
    ADD CONSTRAINT fk_brp_prec FOREIGN KEY (idbrprecedent)
        REFERENCES BONRECEPTION_POUSSIN(id);

ALTER TABLE BONRECEPTION_POUSSIN
    ADD CONSTRAINT fk_brp_lieu FOREIGN KEY (idlieureception)
        REFERENCES LIEURECEPTION(id);

-- LOT
ALTER TABLE LOT
    ADD CONSTRAINT fk_lot_br FOREIGN KEY (idbonreception)
        REFERENCES BONRECEPTION_POUSSIN(id);

ALTER TABLE LOT
    ADD CONSTRAINT fk_lot_icf FOREIGN KEY (idinsertcompfille)
        REFERENCES INSERTION_COMPARTIMENT_FILLE(id);

ALTER TABLE LOT
    ADD CONSTRAINT fk_lot_bc FOREIGN KEY (idboncommande)
        REFERENCES BONCOMMANDE_POUSSIN(id);

ALTER TABLE LOT
    ADD CONSTRAINT fk_lot_sante FOREIGN KEY (idsante)
        REFERENCES SANTEPOUSSIN(id);


-- ============================================================================
--  CLÉS ÉTRANGÈRES vers MAGASIN2
-- ============================================================================

ALTER TABLE BONCOMMANDE_POUSSIN
    ADD CONSTRAINT fk_bcp_magasin FOREIGN KEY (idmagasin)
        REFERENCES MAGASIN2(id);

ALTER TABLE BONRECEPTION_POUSSIN
    ADD CONSTRAINT fk_brp_magasin FOREIGN KEY (idmagasin)
        REFERENCES MAGASIN2(id);

-- ============================================================================
-- 7. INDEX
-- ============================================================================

CREATE INDEX idx_bcp_daty      ON BONCOMMANDE_POUSSIN (daty);
CREATE INDEX idx_bcp_ref       ON BONCOMMANDE_POUSSIN (reference);
CREATE INDEX idx_bcpf_mr       ON BONCOMMANDE_POUSSIN_FILLE (idmere);
CREATE INDEX idx_bcpf_prd      ON BONCOMMANDE_POUSSIN_FILLE (idproduit);

CREATE INDEX idx_brp_daty      ON BONRECEPTION_POUSSIN (daty);
CREATE INDEX idx_brp_bc        ON BONRECEPTION_POUSSIN (idboncommande);
CREATE INDEX idx_brp_bcf       ON BONRECEPTION_POUSSIN (idboncommandefille);
CREATE INDEX idx_brp_prec      ON BONRECEPTION_POUSSIN (idbrprecedent);

CREATE INDEX idx_ic_br         ON INSERTION_COMPARTIMENT (idbonreception);
CREATE INDEX idx_icf_mr        ON INSERTION_COMPARTIMENT_FILLE (idmere);
CREATE INDEX idx_icf_cmp       ON INSERTION_COMPARTIMENT_FILLE (idcompartiment);

CREATE INDEX idx_lot_br        ON LOT (idbonreception);
CREATE INDEX idx_lot_bc        ON LOT (idboncommande);
CREATE INDEX idx_lot_icf       ON LOT (idinsertcompfille);


-- ============================================================================
-- 8. VUES
-- ============================================================================

-- Vues dictionnaires
CREATE OR REPLACE VIEW COMPARTIMENT_LIB AS
SELECT c.id, c.nom, c.capacite
FROM COMPARTIMENT c;
/

CREATE OR REPLACE VIEW SANTEPOUSSIN_LIB AS
SELECT s.id, s.val, s.desce
FROM SANTEPOUSSIN s;
/

CREATE OR REPLACE VIEW LIEURECEPTION_LIB AS
SELECT l.id, l.val, l.desce
FROM LIEURECEPTION l;
/

-- Vue BC (mère)
CREATE OR REPLACE VIEW BONCOMMANDE_POUSSIN_LIB AS
SELECT bcp.id, bcp.daty, bcp.reference, bcp.descri,
       bcp.idmagasin, bcp.etat, bcp.remarque,
       m.val AS idmagasinlib,
       CASE bcp.etat
            WHEN 0  THEN 'Annule'
            WHEN 1  THEN 'Cree'
            WHEN 11 THEN 'Vise'
            WHEN 9  THEN 'Cloture'
            ELSE 'Autre'
       END AS etatlib
FROM BONCOMMANDE_POUSSIN bcp
LEFT JOIN MAGASIN2 m ON m.id = bcp.idmagasin;
/

-- Vue BC (fille)
CREATE OR REPLACE VIEW BONCOMMANDE_POUSSIN_FILLE_LIB AS
SELECT bcpf.id, bcpf.idmere, bcpf.idproduit, bcpf.idsouche,
       bcpf.qte, bcpf.pu,
       i.libelle AS produitlib,
       s.val     AS souchelibr,
       (bcpf.qte * bcpf.pu) AS montant,
       (bcpf.id || ' - ' || NVL(i.libelle, '?')) AS idproduitlib
FROM BONCOMMANDE_POUSSIN_FILLE bcpf
LEFT JOIN AS_INGREDIENTS i ON i.id = bcpf.idproduit
LEFT JOIN SOUCHE         s ON s.id = bcpf.idsouche;
/

-- Vue BR (mère)
CREATE OR REPLACE VIEW BONRECEPTION_POUSSIN_LIB AS
SELECT brp.id, brp.daty, brp.reference,
       brp.idboncommande, brp.idboncommandefille, brp.idbrprecedent,
       brp.idlieureception, brp.idmagasin, brp.etat,
       brp.qt_avant_cumule, brp.qt_apres_cumule,
       brp.maty, brp.very, brp.remarque,
       bc.reference     AS idboncommandelib,
       bcf.idproduit    AS idboncommandefilleproduit,
       brq.reference    AS idbrprecedentlib,
       lr.val           AS idlieureceptionlib,
       m.val            AS idmagasinlib,
       i.libelle        AS produitlib,
       s.val            AS souchelibr,
       CASE brp.etat
            WHEN 0  THEN 'Annule'
            WHEN 1  THEN 'Cree'
            WHEN 11 THEN 'Vise'
            WHEN 9  THEN 'Cloture'
            ELSE 'Autre'
       END AS etatlib
FROM BONRECEPTION_POUSSIN brp
LEFT JOIN BONCOMMANDE_POUSSIN        bc  ON bc.id  = brp.idboncommande
LEFT JOIN BONCOMMANDE_POUSSIN_FILLE  bcf ON bcf.id = brp.idboncommandefille
LEFT JOIN BONRECEPTION_POUSSIN       brq ON brq.id = brp.idbrprecedent
LEFT JOIN LIEURECEPTION              lr  ON lr.id  = brp.idlieureception
LEFT JOIN MAGASIN2                   m   ON m.id   = brp.idmagasin
LEFT JOIN AS_INGREDIENTS             i   ON i.id   = bcf.idproduit
LEFT JOIN SOUCHE                     s   ON s.id   = bcf.idsouche;
/

-- Vue IC (mère)
CREATE OR REPLACE VIEW INSERTION_COMPARTIMENT_LIB AS
SELECT ic.id, ic.daty, ic.reference, ic.idbonreception,
       ic.etat, ic.remarque,
       brp.reference AS idbonreceptionlib,
       CASE ic.etat
            WHEN 0  THEN 'Annule'
            WHEN 1  THEN 'Cree'
            WHEN 11 THEN 'Vise'
            WHEN 9  THEN 'Cloture'
            ELSE 'Autre'
       END AS etatlib
FROM INSERTION_COMPARTIMENT ic
LEFT JOIN BONRECEPTION_POUSSIN brp ON brp.id = ic.idbonreception;
/

-- Vue IC (fille)
CREATE OR REPLACE VIEW INSERTION_COMP_FILLE_LIB AS
SELECT icf.id, icf.idmere, icf.idcompartiment, icf.qte, icf.pu,
       icf.valeurCompartiment, icf.sexe, icf.idsante,
       c.nom    AS compartimentlib,
       s.val    AS sexelib,
       sp.val   AS santelib
FROM INSERTION_COMPARTIMENT_FILLE icf
LEFT JOIN COMPARTIMENT c  ON c.id  = icf.idcompartiment
LEFT JOIN SEXE         s  ON s.id  = icf.sexe
LEFT JOIN SANTEPOUSSIN sp ON sp.id = icf.idsante;
/

-- Vue LOT
CREATE OR REPLACE VIEW LOT_LIB AS
SELECT l.id, l.idbonreception, l.idproduit, l.idsouche,
       l.qte, l.pu, l.sexe, l.idsante,
       l.idinsertcompfille, l.idboncommande, l.qte_actuelle,
       brp.reference  AS idbonreceptionlib,
       bc.reference   AS idboncommandelib,
       i.libelle      AS produitlib,
       s.val          AS souchelibr,
       sp.val         AS santelib,
       sx.val         AS sexelib,
       c.nom          AS compartimentlib,
       (l.qte * l.pu) AS valeurTotale,
       CASE
            WHEN l.qte > 0
            THEN l.pu * (l.qte / l.qte_actuelle)
            ELSE 0
       END AS valeurUnitaire
FROM LOT l
LEFT JOIN BONRECEPTION_POUSSIN       brp ON brp.id = l.idbonreception
LEFT JOIN BONCOMMANDE_POUSSIN        bc  ON bc.id  = l.idboncommande
LEFT JOIN AS_INGREDIENTS             i   ON i.id   = l.idproduit
LEFT JOIN SOUCHE                     s   ON s.id   = l.idsouche
LEFT JOIN SANTEPOUSSIN               sp  ON sp.id  = l.idsante
LEFT JOIN SEXE                       sx  ON sx.id  = l.sexe
LEFT JOIN INSERTION_COMPARTIMENT_FILLE icf ON icf.id = l.idinsertcompfille
LEFT JOIN COMPARTIMENT               c   ON c.id   = icf.idcompartiment;
/


COMMIT;

-- ============================================================================
-- FIN
-- ============================================================================