-- ============================================================================
--  MODULE POUSSIN — Script de création final
--  Base : Oracle 11g+
--  Date : 2026-10-07
-- ============================================================================
--  PRÉREQUIS (doivent exister) :
--    SEXE, FOURNISSEUR, AS_INGREDIENTS, LOT, COMPARTIMENT,
--    DEVISE, MODEPAIEMENT, MAGASIN2, MENUDYNAMIQUE, usermenu
-- ============================================================================


-- ============================================================================
-- 1. SÉQUENCES
-- ============================================================================
CREATE SEQUENCE GETSEQLIEURECEPTION      START WITH 1;
CREATE SEQUENCE GETSEQBCPOUSSIN          START WITH 1;
CREATE SEQUENCE GETSEQBCPOUSSINFILLE     START WITH 1;
CREATE SEQUENCE GETSEQBRPOUSSIN          START WITH 1;
CREATE SEQUENCE GETSEQBRPOUSSINFILLE     START WITH 1;
CREATE SEQUENCE GETSEQINSERTIONCOMP      START WITH 1;
CREATE SEQUENCE GETSEQINSERTIONCOMPFILLE START WITH 1;


-- ============================================================================
-- 2. TABLES
-- ============================================================================

CREATE TABLE LIEURECEPTION (
    id     VARCHAR2(30) PRIMARY KEY,
    val    VARCHAR2(100),
    desce  VARCHAR2(255)
);

CREATE TABLE BONCOMMANDE_POUSSIN (
    id              VARCHAR2(30) PRIMARY KEY,
    daty            DATE,
    reference       VARCHAR2(100),
    iddmdachat      VARCHAR2(30),
    idfournisseur   VARCHAR2(30),
    modepaiement    VARCHAR2(30),
    iddevise        VARCHAR2(10),
    idmagasin       VARCHAR2(30),
    datelimite      DATE,
    etat            NUMBER(2) DEFAULT 1,
    remarque        VARCHAR2(500)
);

CREATE TABLE BONCOMMANDE_POUSSIN_FILLE (
    id            VARCHAR2(30) PRIMARY KEY,
    idmere        VARCHAR2(30),
    idproduit     VARCHAR2(30),
    sexe          VARCHAR2(10),
    quantite      NUMBER(15,2),
    pu            NUMBER(15,2),
    iddevise      VARCHAR2(10),
    tauxdechange  NUMBER(15,4) DEFAULT 1,
    remise        NUMBER(5,2)  DEFAULT 0,
    tva           NUMBER(5,2)  DEFAULT 0,
    montant       NUMBER(15,2)
);

CREATE TABLE BONRECEPTION_POUSSIN (
    id                        VARCHAR2(30) PRIMARY KEY,
    daty                      DATE,
    reference                 VARCHAR2(100),
    idlieureception           VARCHAR2(30),
    idboncommandepoussin      VARCHAR2(30),
    idbonreceptionprecedent   VARCHAR2(30),
    est_final                 NUMBER(1) DEFAULT 0,
    idfournisseur             VARCHAR2(30),
    etat                      NUMBER(2) DEFAULT 1,
    remarque                  VARCHAR2(500)
);

CREATE TABLE BONRECEPTION_POUSSIN_FILLE (
    id                VARCHAR2(30) PRIMARY KEY,
    idmere            VARCHAR2(30),
    idproduit         VARCHAR2(30),
    sexe              VARCHAR2(10),
    quantite          NUMBER(15,2) DEFAULT 0,
    nbmort            NUMBER(15,2) DEFAULT 0,
    nbperdue          NUMBER(15,2) DEFAULT 0,
    pu                NUMBER(15,2),
    iddevise          VARCHAR2(10),
    tauxdechange      NUMBER(15,4) DEFAULT 1,
    idbcfilleorigine  VARCHAR2(30)
);

CREATE TABLE INSERTION_COMPARTIMENT (
    id              VARCHAR2(30) PRIMARY KEY,
    daty            DATE,
    reference       VARCHAR2(100),
    idlieu          VARCHAR2(30),
    idbonreception  VARCHAR2(30),
    etat            NUMBER(2) DEFAULT 1,
    remarque        VARCHAR2(500)
);

CREATE TABLE INSERTION_COMPARTIMENT_FILLE (
    id                    VARCHAR2(30) PRIMARY KEY,
    idmere                VARCHAR2(30),
    idbcfilleorigine      VARCHAR2(30),
    idbrcfilleorigine     VARCHAR2(30),
    idcompartiment        VARCHAR2(30),
    quantitefinale        NUMBER(15,2),
    sexe                  VARCHAR2(10),
    sante                 NUMBER(2),
    idlot                 VARCHAR2(30),
    est_final             NUMBER(1) DEFAULT 0
);


-- ============================================================================
-- 3. CONTRAINTES CHECK
-- ============================================================================
ALTER TABLE BONCOMMANDE_POUSSIN          ADD CONSTRAINT ck_bcp_etat  CHECK (etat IN (0,1,11,9));
ALTER TABLE BONRECEPTION_POUSSIN         ADD CONSTRAINT ck_brp_etat  CHECK (etat IN (0,1,11,9));
ALTER TABLE INSERTION_COMPARTIMENT       ADD CONSTRAINT ck_ic_etat   CHECK (etat IN (0,1,11,9));

ALTER TABLE BONRECEPTION_POUSSIN         ADD CONSTRAINT ck_brp_fin   CHECK (est_final IN (0,1));
ALTER TABLE INSERTION_COMPARTIMENT_FILLE ADD CONSTRAINT ck_icf_fin   CHECK (est_final IN (0,1));

ALTER TABLE INSERTION_COMPARTIMENT_FILLE ADD CONSTRAINT ck_icf_sante CHECK (sante BETWEEN 1 AND 10);

ALTER TABLE BONCOMMANDE_POUSSIN_FILLE    ADD CONSTRAINT ck_bcpf_sx   CHECK (sexe IN ('0','1'));
ALTER TABLE BONRECEPTION_POUSSIN_FILLE   ADD CONSTRAINT ck_brpf_sx   CHECK (sexe IN ('0','1'));
ALTER TABLE INSERTION_COMPARTIMENT_FILLE ADD CONSTRAINT ck_icf_sx    CHECK (sexe IN ('0','1'));

ALTER TABLE BONCOMMANDE_POUSSIN_FILLE    ADD CONSTRAINT ck_bcpf_qte  CHECK (quantite >= 0);
ALTER TABLE BONRECEPTION_POUSSIN_FILLE   ADD CONSTRAINT ck_brpf_qte  CHECK (quantite >= 0 AND nbmort >= 0 AND nbperdue >= 0);
ALTER TABLE INSERTION_COMPARTIMENT_FILLE ADD CONSTRAINT ck_icf_qte   CHECK (quantitefinale >= 0);

ALTER TABLE BONCOMMANDE_POUSSIN_FILLE    ADD CONSTRAINT ck_bcpf_rem  CHECK (remise BETWEEN 0 AND 100);
ALTER TABLE BONCOMMANDE_POUSSIN_FILLE    ADD CONSTRAINT ck_bcpf_tva  CHECK (tva    BETWEEN 0 AND 100);
ALTER TABLE BONCOMMANDE_POUSSIN_FILLE    ADD CONSTRAINT ck_bcpf_tx   CHECK (tauxdechange > 0);
ALTER TABLE BONRECEPTION_POUSSIN_FILLE   ADD CONSTRAINT ck_brpf_tx   CHECK (tauxdechange > 0);


-- ============================================================================
-- 4. CLÉS ÉTRANGÈRES
-- ============================================================================

-- BC Poussin (mère)
ALTER TABLE BONCOMMANDE_POUSSIN ADD CONSTRAINT fk_bcp_frn FOREIGN KEY (idfournisseur) REFERENCES FOURNISSEUR(id);
ALTER TABLE BONCOMMANDE_POUSSIN ADD CONSTRAINT fk_bcp_dev FOREIGN KEY (iddevise)     REFERENCES DEVISE(id);

-- BC Poussin (fille)
ALTER TABLE BONCOMMANDE_POUSSIN_FILLE ADD CONSTRAINT fk_bcpf_mr  FOREIGN KEY (idmere)    REFERENCES BONCOMMANDE_POUSSIN(id);
ALTER TABLE BONCOMMANDE_POUSSIN_FILLE ADD CONSTRAINT fk_bcpf_prd FOREIGN KEY (idproduit) REFERENCES AS_INGREDIENTS(id);
ALTER TABLE BONCOMMANDE_POUSSIN_FILLE ADD CONSTRAINT fk_bcpf_sx  FOREIGN KEY (sexe)      REFERENCES SEXE(id);

-- BR Poussin (mère)
ALTER TABLE BONRECEPTION_POUSSIN ADD CONSTRAINT fk_brp_lr  FOREIGN KEY (idlieureception)         REFERENCES LIEURECEPTION(id);
ALTER TABLE BONRECEPTION_POUSSIN ADD CONSTRAINT fk_brp_bc  FOREIGN KEY (idboncommandepoussin)    REFERENCES BONCOMMANDE_POUSSIN(id);
ALTER TABLE BONRECEPTION_POUSSIN ADD CONSTRAINT fk_brp_bp  FOREIGN KEY (idbonreceptionprecedent) REFERENCES BONRECEPTION_POUSSIN(id);
ALTER TABLE BONRECEPTION_POUSSIN ADD CONSTRAINT fk_brp_frn FOREIGN KEY (idfournisseur)           REFERENCES FOURNISSEUR(id);

-- BR Poussin (fille)
ALTER TABLE BONRECEPTION_POUSSIN_FILLE ADD CONSTRAINT fk_brpf_mr  FOREIGN KEY (idmere)           REFERENCES BONRECEPTION_POUSSIN(id);
ALTER TABLE BONRECEPTION_POUSSIN_FILLE ADD CONSTRAINT fk_brpf_prd FOREIGN KEY (idproduit)        REFERENCES AS_INGREDIENTS(id);
ALTER TABLE BONRECEPTION_POUSSIN_FILLE ADD CONSTRAINT fk_brpf_sx  FOREIGN KEY (sexe)             REFERENCES SEXE(id);
ALTER TABLE BONRECEPTION_POUSSIN_FILLE ADD CONSTRAINT fk_brpf_bcf FOREIGN KEY (idbcfilleorigine) REFERENCES BONCOMMANDE_POUSSIN_FILLE(id);

-- Insertion compartiment (mère)
ALTER TABLE INSERTION_COMPARTIMENT ADD CONSTRAINT fk_ic_lr FOREIGN KEY (idlieu)         REFERENCES LIEURECEPTION(id);
ALTER TABLE INSERTION_COMPARTIMENT ADD CONSTRAINT fk_ic_br FOREIGN KEY (idbonreception) REFERENCES BONRECEPTION_POUSSIN(id);

-- Insertion compartiment (fille)
ALTER TABLE INSERTION_COMPARTIMENT_FILLE ADD CONSTRAINT fk_icf_mr  FOREIGN KEY (idmere)            REFERENCES INSERTION_COMPARTIMENT(id);
ALTER TABLE INSERTION_COMPARTIMENT_FILLE ADD CONSTRAINT fk_icf_bcf FOREIGN KEY (idbcfilleorigine)  REFERENCES BONCOMMANDE_POUSSIN_FILLE(id);
ALTER TABLE INSERTION_COMPARTIMENT_FILLE ADD CONSTRAINT fk_icf_brf FOREIGN KEY (idbrcfilleorigine) REFERENCES BONRECEPTION_POUSSIN_FILLE(id);
ALTER TABLE INSERTION_COMPARTIMENT_FILLE ADD CONSTRAINT fk_icf_cmp FOREIGN KEY (idcompartiment)    REFERENCES COMPARTIMENT(id);
ALTER TABLE INSERTION_COMPARTIMENT_FILLE ADD CONSTRAINT fk_icf_lot FOREIGN KEY (idlot)             REFERENCES LOT(id);
ALTER TABLE INSERTION_COMPARTIMENT_FILLE ADD CONSTRAINT fk_icf_sx  FOREIGN KEY (sexe)              REFERENCES SEXE(id);


-- ============================================================================
-- 5. CONTRAINTES UNIQUE
-- ============================================================================
ALTER TABLE BONCOMMANDE_POUSSIN    ADD CONSTRAINT uk_bcp_ref UNIQUE (reference);
ALTER TABLE BONRECEPTION_POUSSIN   ADD CONSTRAINT uk_brp_ref UNIQUE (reference);
ALTER TABLE INSERTION_COMPARTIMENT ADD CONSTRAINT uk_ic_ref  UNIQUE (reference);


-- ============================================================================
-- 6. INDEX
-- ============================================================================
CREATE INDEX idx_bcp_daty ON BONCOMMANDE_POUSSIN (daty);
CREATE INDEX idx_bcp_frn  ON BONCOMMANDE_POUSSIN (idfournisseur);
CREATE INDEX idx_bcp_etat ON BONCOMMANDE_POUSSIN (etat);
CREATE INDEX idx_bcpf_mr  ON BONCOMMANDE_POUSSIN_FILLE (idmere);
CREATE INDEX idx_bcpf_prd ON BONCOMMANDE_POUSSIN_FILLE (idproduit);

CREATE INDEX idx_brp_daty ON BONRECEPTION_POUSSIN (daty);
CREATE INDEX idx_brp_bc   ON BONRECEPTION_POUSSIN (idboncommandepoussin);
CREATE INDEX idx_brp_bp   ON BONRECEPTION_POUSSIN (idbonreceptionprecedent);
CREATE INDEX idx_brp_etat ON BONRECEPTION_POUSSIN (etat);
CREATE INDEX idx_brp_lr   ON BONRECEPTION_POUSSIN (idlieureception);
CREATE INDEX idx_brpf_mr  ON BONRECEPTION_POUSSIN_FILLE (idmere);
CREATE INDEX idx_brpf_prd ON BONRECEPTION_POUSSIN_FILLE (idproduit);
CREATE INDEX idx_brpf_bcf ON BONRECEPTION_POUSSIN_FILLE (idbcfilleorigine);

CREATE INDEX idx_ic_daty  ON INSERTION_COMPARTIMENT (daty);
CREATE INDEX idx_ic_br    ON INSERTION_COMPARTIMENT (idbonreception);
CREATE INDEX idx_ic_etat  ON INSERTION_COMPARTIMENT (etat);
CREATE INDEX idx_icf_mr   ON INSERTION_COMPARTIMENT_FILLE (idmere);
CREATE INDEX idx_icf_cmp  ON INSERTION_COMPARTIMENT_FILLE (idcompartiment);
CREATE INDEX idx_icf_brf  ON INSERTION_COMPARTIMENT_FILLE (idbrcfilleorigine);


-- ============================================================================
-- 7. VUES
-- ============================================================================
CREATE OR REPLACE VIEW BONCOMMANDE_POUSSIN_LIB AS
SELECT bcp.*,
       f.nom  AS fournisseurlib,
       mp.val AS modepaiementlib,
       d.val  AS iddeviselib,
       m.val  AS idmagasinlib,
       CASE bcp.etat WHEN 0 THEN 'Annule' WHEN 1 THEN 'Cree'
                     WHEN 11 THEN 'Vise' WHEN 9 THEN 'Cloture'
                     ELSE 'Autre' END AS etatlib
FROM BONCOMMANDE_POUSSIN bcp
LEFT JOIN FOURNISSEUR  f  ON f.id  = bcp.idfournisseur
LEFT JOIN MODEPAIEMENT mp ON mp.id = bcp.modepaiement
LEFT JOIN DEVISE       d  ON d.id  = bcp.iddevise
LEFT JOIN MAGASIN2     m  ON m.id  = bcp.idmagasin;

CREATE OR REPLACE VIEW BONCOMMANDE_POUSSIN_FILLE_LIB AS
SELECT bcpf.*,
       i.libelle AS produitlib,
       s.val     AS sexelib,
       (bcpf.quantite * bcpf.pu) AS montantht,
       (bcpf.quantite * bcpf.pu * (1 - NVL(bcpf.remise,0)/100)) AS montanthtnet,
       (bcpf.quantite * bcpf.pu * (1 - NVL(bcpf.remise,0)/100)
            * (1 + NVL(bcpf.tva,0)/100)) AS montantttc
FROM BONCOMMANDE_POUSSIN_FILLE bcpf
LEFT JOIN AS_INGREDIENTS i ON i.id = bcpf.idproduit
LEFT JOIN SEXE           s ON s.id = bcpf.sexe;

CREATE OR REPLACE VIEW BONRECEPTION_POUSSIN_LIB AS
SELECT brp.*,
       lr.val AS idlieureceptionlib,
       f.nom  AS fournisseurlib,
       CASE brp.etat WHEN 0 THEN 'Annule' WHEN 1 THEN 'Cree'
                     WHEN 11 THEN 'Vise' WHEN 9 THEN 'Cloture'
                     ELSE 'Autre' END AS etatlib,
       CASE brp.est_final WHEN 1 THEN 'Oui' ELSE 'Non' END AS est_finallib
FROM BONRECEPTION_POUSSIN brp
LEFT JOIN LIEURECEPTION lr ON lr.id = brp.idlieureception
LEFT JOIN FOURNISSEUR   f  ON f.id  = brp.idfournisseur;

CREATE OR REPLACE VIEW BONRECEPTION_POUSSIN_FILLE_LIB AS
SELECT brpf.*,
       i.libelle AS produitlib,
       s.val     AS sexelib,
       (brpf.quantite + NVL(brpf.nbmort,0) + NVL(brpf.nbperdue,0)) AS quantitetotale
FROM BONRECEPTION_POUSSIN_FILLE brpf
LEFT JOIN AS_INGREDIENTS i ON i.id = brpf.idproduit
LEFT JOIN SEXE           s ON s.id = brpf.sexe;

CREATE OR REPLACE VIEW INSERTION_COMPARTIMENT_LIB AS
SELECT ic.*,
       lr.val        AS idlieulib,
       brp.reference AS idbonreceptionlib,
       CASE ic.etat WHEN 0 THEN 'Annule' WHEN 1 THEN 'Cree'
                    WHEN 11 THEN 'Vise' WHEN 9 THEN 'Cloture'
                    ELSE 'Autre' END AS etatlib
FROM INSERTION_COMPARTIMENT ic
LEFT JOIN LIEURECEPTION        lr  ON lr.id  = ic.idlieu
LEFT JOIN BONRECEPTION_POUSSIN brp ON brp.id = ic.idbonreception;

CREATE OR REPLACE VIEW INSERTION_COMPARTIMENT_FILLE_LIB AS
SELECT icf.*,
       c.libelle AS compartimentlib,
       s.val     AS sexelib,
       CASE icf.est_final WHEN 1 THEN 'Oui' ELSE 'Non' END AS est_finallib,
       CASE WHEN icf.sante >= 9 THEN 'Excellent'
            WHEN icf.sante >= 7 THEN 'Bon'
            WHEN icf.sante >= 5 THEN 'Moyen'
            WHEN icf.sante >= 3 THEN 'Faible'
            ELSE 'Critique' END AS santelib
FROM INSERTION_COMPARTIMENT_FILLE icf
LEFT JOIN COMPARTIMENT c ON c.id = icf.idcompartiment
LEFT JOIN SEXE         s ON s.id = icf.sexe;


-- ============================================================================
-- 8. DONNÉES DE BASE — LIEURECEPTION
-- ============================================================================
INSERT INTO LIEURECEPTION VALUES ('LR001', 'Couvoir',          'Couvoir principal');
INSERT INTO LIEURECEPTION VALUES ('LR002', 'Import',           'Import Port Aeroport');
INSERT INTO LIEURECEPTION VALUES ('LR003', 'Selection interne','Selection interne ferme');
INSERT INTO LIEURECEPTION VALUES ('LR004', 'Ferme',            'Batiment elevage');
INSERT INTO LIEURECEPTION VALUES ('LR005', 'Quarantaine',      'Zone de quarantaine');
COMMIT;


-- ============================================================================
-- 9. MENU
-- ============================================================================

-- 9.1. Menu principal
INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON','Poussins','egg',
        'module.jsp?but=ferme/poussin/boncommande-poussin-liste.jsp',
        30, 1, NULL);

-- 9.2. Sous-menu : Bon de commande
INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON_BC','Bons de commande','shopping_cart',
        'module.jsp?but=ferme/poussin/boncommande-poussin-liste.jsp',
        1, 2, 'MENUPOUSSON');

-- 9.3. Sous-menu : Bon de réception
INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON_BR','Bons de reception','inventory',
        'module.jsp?but=ferme/poussin/bonreception-poussin-liste.jsp',
        2, 2, 'MENUPOUSSON');

-- 9.4. Sous-menu : Insertion compartiment
INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON_IC','Insertions compartiment','grid_view',
        'module.jsp?but=ferme/poussin/insertion-compartiment-liste.jsp',
        3, 2, 'MENUPOUSSON');

COMMIT;


-- ============================================================================
-- 10. VÉRIFICATION FINALE
-- ============================================================================

-- 10.1. Tables (doit retourner 7)
SELECT table_name FROM user_tables
WHERE table_name IN (
    'LIEURECEPTION','BONCOMMANDE_POUSSIN','BONCOMMANDE_POUSSIN_FILLE',
    'BONRECEPTION_POUSSIN','BONRECEPTION_POUSSIN_FILLE',
    'INSERTION_COMPARTIMENT','INSERTION_COMPARTIMENT_FILLE'
);

-- 10.2. Séquences (doit retourner 7)
SELECT sequence_name FROM user_sequences
WHERE sequence_name IN (
    'GETSEQLIEURECEPTION','GETSEQBCPOUSSIN','GETSEQBCPOUSSINFILLE',
    'GETSEQBRPOUSSIN','GETSEQBRPOUSSINFILLE',
    'GETSEQINSERTIONCOMP','GETSEQINSERTIONCOMPFILLE'
);

-- 10.3. Vues (doit retourner 6)
SELECT view_name FROM user_views
WHERE view_name LIKE '%POUSSIN%'
   OR view_name LIKE 'INSERTION_COMPARTIMENT%';

-- 10.4. Lieux de réception (doit retourner 5)
SELECT * FROM LIEURECEPTION;

-- 10.5. Menu (doit retourner 4)
SELECT id, libelle, niveau, id_pere FROM MENUDYNAMIQUE
WHERE id LIKE 'MENUPOUSSON%'
ORDER BY niveau, rang;


-- ============================================================================
-- FIN DU SCRIPT
-- ============================================================================