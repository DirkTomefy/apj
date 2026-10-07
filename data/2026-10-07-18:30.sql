-- ============================================================================
--  MODULE POUSSIN — Script d'initialisation complet
--  Base : Oracle
--  Date : 2026-10-07
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

CREATE TABLE TYPERECEPTION (
    id     VARCHAR2(30) PRIMARY KEY,
    val    VARCHAR2(100),
    desce  VARCHAR2(255)
);

INSERT INTO SANTEPOUSSIN  VALUES ('SP01', 'SAIN',         'Poussin en bonne sante');
INSERT INTO SANTEPOUSSIN  VALUES ('SP02', 'MARARY',       'Poussin malade');
INSERT INTO TYPERECEPTION VALUES ('TR01', 'Aeroport',     'Reception a l aeroport');
INSERT INTO TYPERECEPTION VALUES ('TR02', 'Intermediaire','Reception lieu intermediaire');
INSERT INTO TYPERECEPTION VALUES ('TR03', 'Magasin',      'Reception en magasin');
COMMIT;


-- ============================================================================
-- 2. SÉQUENCES
-- ============================================================================

CREATE SEQUENCE SEQ_BCPOUSSIN          START WITH 1;
CREATE SEQUENCE SEQ_BCPOUSSINFILLE     START WITH 1;
CREATE SEQUENCE SEQ_BRPOUSSIN          START WITH 1;
CREATE SEQUENCE SEQ_BRPOUSSINFILLE     START WITH 1;
CREATE SEQUENCE SEQ_INSERTIONCOMP      START WITH 1;
CREATE SEQUENCE SEQ_INSERTIONCOMPFILLE START WITH 1;
CREATE SEQUENCE SEQ_COMPARTIMENT       START WITH 1;
CREATE SEQUENCE SEQ_SANTEPOUSSIN       START WITH 1;
CREATE SEQUENCE SEQ_TYPERECEPTION      START WITH 1;


-- ============================================================================
-- 3. FONCTIONS APJ
-- ============================================================================

CREATE OR REPLACE FUNCTION GETSEQBCPOUSSIN          RETURN NUMBER IS v NUMBER; BEGIN SELECT SEQ_BCPOUSSIN.NEXTVAL          INTO v FROM DUAL; RETURN v; END; /
CREATE OR REPLACE FUNCTION GETSEQBCPOUSSINFILLE     RETURN NUMBER IS v NUMBER; BEGIN SELECT SEQ_BCPOUSSINFILLE.NEXTVAL     INTO v FROM DUAL; RETURN v; END; /
CREATE OR REPLACE FUNCTION GETSEQBRPOUSSIN          RETURN NUMBER IS v NUMBER; BEGIN SELECT SEQ_BRPOUSSIN.NEXTVAL          INTO v FROM DUAL; RETURN v; END; /
CREATE OR REPLACE FUNCTION GETSEQBRPOUSSINFILLE     RETURN NUMBER IS v NUMBER; BEGIN SELECT SEQ_BRPOUSSINFILLE.NEXTVAL     INTO v FROM DUAL; RETURN v; END; /
CREATE OR REPLACE FUNCTION GETSEQINSERTIONCOMP      RETURN NUMBER IS v NUMBER; BEGIN SELECT SEQ_INSERTIONCOMP.NEXTVAL      INTO v FROM DUAL; RETURN v; END; /
CREATE OR REPLACE FUNCTION GETSEQINSERTIONCOMPFILLE RETURN NUMBER IS v NUMBER; BEGIN SELECT SEQ_INSERTIONCOMPFILLE.NEXTVAL INTO v FROM DUAL; RETURN v; END; /
CREATE OR REPLACE FUNCTION GETSEQCOMPARTIMENT       RETURN NUMBER IS v NUMBER; BEGIN SELECT SEQ_COMPARTIMENT.NEXTVAL       INTO v FROM DUAL; RETURN v; END; /
CREATE OR REPLACE FUNCTION GETSEQSANTEPOUSSIN       RETURN NUMBER IS v NUMBER; BEGIN SELECT SEQ_SANTEPOUSSIN.NEXTVAL       INTO v FROM DUAL; RETURN v; END; /
CREATE OR REPLACE FUNCTION GETSEQTYPERECEPTION      RETURN NUMBER IS v NUMBER; BEGIN SELECT SEQ_TYPERECEPTION.NEXTVAL      INTO v FROM DUAL; RETURN v; END; /


-- ============================================================================
-- 4. TABLES MÉTIER
-- ============================================================================

-- Bon de commande poussin (mère)
CREATE TABLE BONCOMMANDE_POUSSIN (
    id           VARCHAR2(30) PRIMARY KEY,
    daty         DATE,
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
    id                VARCHAR2(30) PRIMARY KEY,
    daty              DATE,
    reference         VARCHAR2(100),
    idboncommande     VARCHAR2(30),
    idbrprecedent     VARCHAR2(30),
    typereception     VARCHAR2(30),
    idmagasin         VARCHAR2(30),
    etat              NUMBER(2) DEFAULT 1,
    remarque          VARCHAR2(500)
);

-- Bon de réception poussin (fille)
CREATE TABLE BONRECEPTION_POUSSIN_FILLE (
    id                VARCHAR2(30) PRIMARY KEY,
    idmere            VARCHAR2(30),
    idproduit         VARCHAR2(30),
    idsouche          VARCHAR2(50),
    qte               NUMBER(15,2) DEFAULT 0,
    pu                NUMBER(15,2),
    maty              NUMBER(15,2) DEFAULT 0,
    very              NUMBER(15,2) DEFAULT 0
);

-- Compartiment
CREATE TABLE COMPARTIMENT (
    id        VARCHAR2(30) PRIMARY KEY,
    nom       VARCHAR2(100),
    capacite  NUMBER(10)
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
    valeur_compartiment   NUMBER(15,2),
    sexe                  VARCHAR2(10),
    idsante               VARCHAR2(30)
);


-- ============================================================================
-- 5. CONTRAINTES CHECK
-- ============================================================================

ALTER TABLE BONCOMMANDE_POUSSIN    ADD CONSTRAINT ck_bcp_etat  CHECK (etat IN (0,1,11,9));
ALTER TABLE BONRECEPTION_POUSSIN   ADD CONSTRAINT ck_brp_etat  CHECK (etat IN (0,1,11,9));
ALTER TABLE INSERTION_COMPARTIMENT ADD CONSTRAINT ck_ic_etat   CHECK (etat IN (0,1,11,9));

ALTER TABLE BONCOMMANDE_POUSSIN_FILLE    ADD CONSTRAINT ck_bcpf_qte CHECK (qte >= 0 AND pu >= 0);
ALTER TABLE BONRECEPTION_POUSSIN_FILLE   ADD CONSTRAINT ck_brpf_qte CHECK (qte >= 0 AND maty >= 0 AND very >= 0);
ALTER TABLE INSERTION_COMPARTIMENT_FILLE ADD CONSTRAINT ck_icf_qte  CHECK (qte >= 0);

ALTER TABLE INSERTION_COMPARTIMENT_FILLE ADD CONSTRAINT ck_icf_sexe CHECK (sexe IN ('0','1'));

ALTER TABLE BONRECEPTION_POUSSIN ADD CONSTRAINT ck_brp_magasin
    CHECK (typereception = 'TR01' OR idmagasin IS NOT NULL);

ALTER TABLE BONRECEPTION_POUSSIN ADD CONSTRAINT ck_brp_origine
    CHECK (idboncommande IS NOT NULL OR idbrprecedent IS NOT NULL
           OR typereception = 'TR01');


-- ============================================================================
-- 6. CLÉS ÉTRANGÈRES
-- ============================================================================

ALTER TABLE BONCOMMANDE_POUSSIN       ADD CONSTRAINT fk_bcp_magasin  FOREIGN KEY (idmagasin) REFERENCES MAGASIN2(id);
ALTER TABLE BONCOMMANDE_POUSSIN_FILLE ADD CONSTRAINT fk_bcpf_mere    FOREIGN KEY (idmere)    REFERENCES BONCOMMANDE_POUSSIN(id);
ALTER TABLE BONCOMMANDE_POUSSIN_FILLE ADD CONSTRAINT fk_bcpf_produit FOREIGN KEY (idproduit) REFERENCES AS_INGREDIENTS(id);
ALTER TABLE BONCOMMANDE_POUSSIN_FILLE ADD CONSTRAINT fk_bcpf_souche  FOREIGN KEY (idsouche)  REFERENCES SOUCHE(id);

ALTER TABLE BONRECEPTION_POUSSIN ADD CONSTRAINT fk_brp_bc      FOREIGN KEY (idboncommande) REFERENCES BONCOMMANDE_POUSSIN(id);
ALTER TABLE BONRECEPTION_POUSSIN ADD CONSTRAINT fk_brp_prec    FOREIGN KEY (idbrprecedent) REFERENCES BONRECEPTION_POUSSIN(id);
ALTER TABLE BONRECEPTION_POUSSIN ADD CONSTRAINT fk_brp_type    FOREIGN KEY (typereception) REFERENCES TYPERECEPTION(id);
ALTER TABLE BONRECEPTION_POUSSIN ADD CONSTRAINT fk_brp_magasin FOREIGN KEY (idmagasin)     REFERENCES MAGASIN2(id);

ALTER TABLE BONRECEPTION_POUSSIN_FILLE ADD CONSTRAINT fk_brpf_mere   FOREIGN KEY (idmere)    REFERENCES BONRECEPTION_POUSSIN(id);
ALTER TABLE BONRECEPTION_POUSSIN_FILLE ADD CONSTRAINT fk_brpf_prod   FOREIGN KEY (idproduit) REFERENCES AS_INGREDIENTS(id);
ALTER TABLE BONRECEPTION_POUSSIN_FILLE ADD CONSTRAINT fk_brpf_souche FOREIGN KEY (idsouche)  REFERENCES SOUCHE(id);

ALTER TABLE INSERTION_COMPARTIMENT       ADD CONSTRAINT fk_ic_br    FOREIGN KEY (idbonreception) REFERENCES BONRECEPTION_POUSSIN(id);
ALTER TABLE INSERTION_COMPARTIMENT_FILLE ADD CONSTRAINT fk_icf_mere FOREIGN KEY (idmere)         REFERENCES INSERTION_COMPARTIMENT(id);
ALTER TABLE INSERTION_COMPARTIMENT_FILLE ADD CONSTRAINT fk_icf_comp FOREIGN KEY (idcompartiment) REFERENCES COMPARTIMENT(id);
ALTER TABLE INSERTION_COMPARTIMENT_FILLE ADD CONSTRAINT fk_icf_sexe FOREIGN KEY (sexe)          REFERENCES SEXE(id);
ALTER TABLE INSERTION_COMPARTIMENT_FILLE ADD CONSTRAINT fk_icf_sant FOREIGN KEY (idsante)       REFERENCES SANTEPOUSSIN(id);


-- ============================================================================
-- 7. INDEX
-- ============================================================================

CREATE INDEX idx_bcp_daty   ON BONCOMMANDE_POUSSIN (daty);
CREATE INDEX idx_bcpf_mr    ON BONCOMMANDE_POUSSIN_FILLE (idmere);
CREATE INDEX idx_bcpf_prd   ON BONCOMMANDE_POUSSIN_FILLE (idproduit);
CREATE INDEX idx_bcpf_souch ON BONCOMMANDE_POUSSIN_FILLE (idsouche);

CREATE INDEX idx_brp_daty   ON BONRECEPTION_POUSSIN (daty);
CREATE INDEX idx_brp_bc     ON BONRECEPTION_POUSSIN (idboncommande);
CREATE INDEX idx_brp_prec   ON BONRECEPTION_POUSSIN (idbrprecedent);
CREATE INDEX idx_brp_type   ON BONRECEPTION_POUSSIN (typereception);
CREATE INDEX idx_brpf_mr    ON BONRECEPTION_POUSSIN_FILLE (idmere);
CREATE INDEX idx_brpf_souch ON BONRECEPTION_POUSSIN_FILLE (idsouche);

CREATE INDEX idx_ic_daty    ON INSERTION_COMPARTIMENT (daty);
CREATE INDEX idx_ic_br      ON INSERTION_COMPARTIMENT (idbonreception);
CREATE INDEX idx_icf_mr     ON INSERTION_COMPARTIMENT_FILLE (idmere);
CREATE INDEX idx_icf_cmp    ON INSERTION_COMPARTIMENT_FILLE (idcompartiment);


-- ============================================================================
-- 8. VUES
-- ============================================================================

CREATE OR REPLACE VIEW BONCOMMANDE_POUSSIN_LIB AS
SELECT bcp.id, bcp.daty, bcp.descri, bcp.idmagasin, bcp.etat, bcp.remarque,
       m.val AS idmagasinlib,
       CASE bcp.etat WHEN 0 THEN 'Annule' WHEN 1 THEN 'Cree'
                     WHEN 11 THEN 'Vise' WHEN 9 THEN 'Cloture'
                     ELSE 'Autre' END AS etatlib
FROM BONCOMMANDE_POUSSIN bcp
LEFT JOIN MAGASIN2 m ON m.id = bcp.idmagasin;

CREATE OR REPLACE VIEW BONCOMMANDE_POUSSIN_FILLE_LIB AS
SELECT bcpf.*,
       i.libelle AS produitlib,
       s.val     AS souchelibr,
       (bcpf.qte * bcpf.pu) AS montant
FROM BONCOMMANDE_POUSSIN_FILLE bcpf
LEFT JOIN AS_INGREDIENTS i ON i.id = bcpf.idproduit
LEFT JOIN SOUCHE         s ON s.id = bcpf.idsouche;

CREATE OR REPLACE VIEW BONRECEPTION_POUSSIN_LIB AS
SELECT brp.id, brp.daty, brp.reference,
       brp.idboncommande, brp.idbrprecedent,
       brp.typereception, brp.idmagasin, brp.etat, brp.remarque,
       bc.reference  AS idboncommandelib,
       brq.reference AS idbrprecedentlib,
       tr.val        AS typereceptionlib,
       m.val         AS idmagasinlib,
       CASE brp.etat WHEN 0 THEN 'Annule' WHEN 1 THEN 'Cree'
                     WHEN 11 THEN 'Vise' WHEN 9 THEN 'Cloture'
                     ELSE 'Autre' END AS etatlib
FROM BONRECEPTION_POUSSIN brp
LEFT JOIN BONCOMMANDE_POUSSIN  bc  ON bc.id  = brp.idboncommande
LEFT JOIN BONRECEPTION_POUSSIN brq ON brq.id = brp.idbrprecedent
LEFT JOIN TYPERECEPTION        tr  ON tr.id  = brp.typereception
LEFT JOIN MAGASIN2             m   ON m.id   = brp.idmagasin;

CREATE OR REPLACE VIEW BONRECEPTION_POUSSIN_FILLE_LIB AS
SELECT brpf.*,
       i.libelle AS produitlib,
       s.val     AS souchelibr,
       (brpf.qte * brpf.pu) AS montant
FROM BONRECEPTION_POUSSIN_FILLE brpf
LEFT JOIN AS_INGREDIENTS i ON i.id = brpf.idproduit
LEFT JOIN SOUCHE         s ON s.id = brpf.idsouche;

CREATE OR REPLACE VIEW COMPARTIMENT_LIB AS
SELECT c.id, c.nom, c.capacite
FROM COMPARTIMENT c;

CREATE OR REPLACE VIEW INSERTION_COMPARTIMENT_LIB AS
SELECT ic.id, ic.daty, ic.reference, ic.idbonreception, ic.etat, ic.remarque,
       brp.reference AS idbonreceptionlib,
       CASE ic.etat WHEN 0 THEN 'Annule' WHEN 1 THEN 'Cree'
                    WHEN 11 THEN 'Vise' WHEN 9 THEN 'Cloture'
                    ELSE 'Autre' END AS etatlib
FROM INSERTION_COMPARTIMENT ic
LEFT JOIN BONRECEPTION_POUSSIN brp ON brp.id = ic.idbonreception;

CREATE OR REPLACE VIEW INSERTION_COMPARTIMENT_FILLE_LIB AS
SELECT icf.*,
       c.nom    AS compartimentlib,
       s.val    AS sexelib,
       sp.val   AS santelib
FROM INSERTION_COMPARTIMENT_FILLE icf
LEFT JOIN COMPARTIMENT c  ON c.id  = icf.idcompartiment
LEFT JOIN SEXE         s  ON s.id  = icf.sexe
LEFT JOIN SANTEPOUSSIN sp ON sp.id = icf.idsante;


-- ============================================================================
-- 9. MENU
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 9.1. Créer les menus
-- ----------------------------------------------------------------------------

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON','Poussins','egg',
        'module.jsp?but=a_tahina/poussin/boncommande-poussin-liste.jsp', 50, 1, NULL);

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON_BC','Bons de commande','shopping_cart',
        'module.jsp?but=a_tahina/poussin/boncommande-poussin-liste.jsp', 1, 2, 'MENUPOUSSON');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON_BR','Bons de réception','inventory',
        'module.jsp?but=a_tahina/poussin/bonreception-poussin-liste.jsp', 2, 2, 'MENUPOUSSON');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON_CMP','Compartiments','meeting_room',
        'module.jsp?but=a_tahina/configuration/compartiment-liste.jsp', 3, 2, 'MENUPOUSSON');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON_IC','Insertions compartiment','grid_view',
        'module.jsp?but=a_tahina/poussin/insertion-compartiment-liste.jsp', 4, 2, 'MENUPOUSSON');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON_SP','Santé poussin','health_and_safety',
        'module.jsp?but=a_tahina/poussin/santepoussin-liste.jsp', 9, 2, 'MENUPOUSSON');

COMMIT;


-- ----------------------------------------------------------------------------
-- 9.2. Rattacher les menus au rôle "resp"
-- ----------------------------------------------------------------------------

DELETE FROM usermenu
WHERE IDMENU IN (
    'MENUPOUSSON','MENUPOUSSON_BC','MENUPOUSSON_BR',
    'MENUPOUSSON_CMP','MENUPOUSSON_IC','MENUPOUSSON_SP'
);

INSERT INTO usermenu (id, IDMENU, idrole, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUPOUSSON',     'resp', 0);

INSERT INTO usermenu (id, IDMENU, idrole, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUPOUSSON_BC',  'resp', 0);

INSERT INTO usermenu (id, IDMENU, idrole, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUPOUSSON_BR',  'resp', 0);

INSERT INTO usermenu (id, IDMENU, idrole, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUPOUSSON_CMP', 'resp', 0);

INSERT INTO usermenu (id, IDMENU, idrole, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUPOUSSON_IC',  'resp', 0);

INSERT INTO usermenu (id, IDMENU, idrole, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUPOUSSON_SP',  'resp', 0);

COMMIT;


-- ============================================================================
-- 10. VÉRIFICATION FINALE
-- ============================================================================

-- 10.1. Tables (doit retourner 9 lignes)
SELECT table_name FROM user_tables
WHERE table_name IN (
    'BONCOMMANDE_POUSSIN','BONCOMMANDE_POUSSIN_FILLE',
    'BONRECEPTION_POUSSIN','BONRECEPTION_POUSSIN_FILLE',
    'COMPARTIMENT','INSERTION_COMPARTIMENT','INSERTION_COMPARTIMENT_FILLE',
    'SANTEPOUSSIN','TYPERECEPTION'
);

-- 10.2. Séquences (doit retourner 9 lignes)
SELECT sequence_name FROM user_sequences
WHERE sequence_name IN (
    'SEQ_BCPOUSSIN','SEQ_BCPOUSSINFILLE',
    'SEQ_BRPOUSSIN','SEQ_BRPOUSSINFILLE',
    'SEQ_INSERTIONCOMP','SEQ_INSERTIONCOMPFILLE',
    'SEQ_COMPARTIMENT','SEQ_SANTEPOUSSIN','SEQ_TYPERECEPTION'
);

-- 10.3. Fonctions (doit retourner 9 lignes)
SELECT object_name FROM user_objects
WHERE object_type = 'FUNCTION'
  AND object_name IN (
    'GETSEQBCPOUSSIN','GETSEQBCPOUSSINFILLE',
    'GETSEQBRPOUSSIN','GETSEQBRPOUSSINFILLE',
    'GETSEQINSERTIONCOMP','GETSEQINSERTIONCOMPFILLE',
    'GETSEQCOMPARTIMENT','GETSEQSANTEPOUSSIN','GETSEQTYPERECEPTION'
  );

-- 10.4. Vues (doit retourner 7 lignes)
SELECT view_name FROM user_views
WHERE view_name IN (
    'BONCOMMANDE_POUSSIN_LIB','BONCOMMANDE_POUSSIN_FILLE_LIB',
    'BONRECEPTION_POUSSIN_LIB','BONRECEPTION_POUSSIN_FILLE_LIB',
    'COMPARTIMENT_LIB','INSERTION_COMPARTIMENT_LIB','INSERTION_COMPARTIMENT_FILLE_LIB'
);

-- 10.5. Dictionnaires
SELECT * FROM SANTEPOUSSIN;    -- 2 lignes
SELECT * FROM TYPERECEPTION;   -- 3 lignes

-- 10.6. Menus (doit retourner 6 lignes)
SELECT id, libelle, niveau FROM MENUDYNAMIQUE
WHERE id LIKE 'MENUPOUSSON%'
ORDER BY niveau, rang;

-- 10.7. usermenu (doit retourner 6 lignes)
SELECT id, IDMENU, idrole, INTERDIT FROM usermenu
WHERE IDMENU LIKE 'MENUPOUSSON%'
ORDER BY IDMENU;

-- 10.8. Test séquence (doit retourner 1)
SELECT GETSEQBCPOUSSIN FROM DUAL;


-- ============================================================================
-- FIN DU SCRIPT
-- ============================================================================