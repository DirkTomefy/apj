-- ============================================================================
--  MODULE POUSSIN + FACTURE / AVOIR  --  SCRIPT FINAL D'INSTALLATION
--
--  Cree d'un seul coup tout ce dont l'application a besoin, avec des noms de
--  colonnes IDENTIQUES aux attributs des classes ORM Java de a_tahina
--  (insensible a la casse ; les underscores comptent : macolonne = MaColonne
--  mais different de ma_colonne).
--
--  UTILISATION
--    1. Schema de l'application deja installe (SEXE, MAGASIN2, AS_INGREDIENTS,
--       FOURNISSEUR, SOUCHE, MENUDYNAMIQUE, USERMENU, sequsermenu).
--    2. Depuis la racine du projet :
--           ./import_sql.sh data/final/final.sql
--       ou, dans SQL*Plus connecte au schema de l'application :
--           @data/final/final.sql
--    3. Redemarrer WildFly (menus et roles sont mis en cache).
--
--  Reinstaller de zero : data/final/drop.sql puis ce fichier.
--  Le script s'arrete a la premiere erreur (WHENEVER SQLERROR) et dit laquelle.
--
--  CONTENU
--    0. controle des prerequis      5. vues (libelles, calendrier, facture)
--    1. colonnes ajoutees a SOUCHE  6. donnees de reference
--    2. sequences + fonctions       7. role et utilisateur "resp"
--    3. tables et contraintes       8. menu + droits
--    4. index                       9. verification
--
--  CONNEXION APRES INSTALLATION : login  resp  /  mot de passe  Resp2026
--  (mot de passe initial a changer apres la premiere connexion).
-- ============================================================================
SET DEFINE OFF
SET SQLBLANKLINES ON
SET SERVEROUTPUT ON
SET FEEDBACK OFF
WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK

-- ============================================================================
-- 0. PREREQUIS : objets de l'application de base que ce script utilise
-- ============================================================================
DECLARE
    n NUMBER;

    PROCEDURE exiger(p_objet VARCHAR2, p_colonnes VARCHAR2) IS
    BEGIN
        EXECUTE IMMEDIATE 'SELECT COUNT(*) FROM (SELECT ' || p_colonnes || ' FROM ' || p_objet || ' WHERE 1 = 0)' INTO n;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20001, 'Prerequis manquant : ' || p_objet || ' (' || p_colonnes || ') -> ' || SQLERRM);
    END;
BEGIN
    exiger('SEXE',           'id, val');
    exiger('MAGASIN2',       'id, val');
    exiger('AS_INGREDIENTS', 'id, libelle');
    exiger('FOURNISSEUR',    'id, nom');
    exiger('SOUCHE',         'id, val');
    exiger('MENUDYNAMIQUE',  'id, libelle, icone, href, rang, niveau, id_pere');
    exiger('USERMENU',       'id, idmenu, idrole, interdit');
    exiger('ROLES',          'idrole, descrole, rang');
    exiger('UTILISATEUR',    'refuser, loginuser, pwduser, nomuser, adruser, teluser, idrole');
    exiger('UTILISATEURVALIDE', 'refuser, loginuser, pwduser, idrole');
    exiger('PARAMCRYPT',     'id, niveau, croissante, idutilisateur');

    SELECT COUNT(*) INTO n FROM user_sequences WHERE sequence_name = 'SEQUSERMENU';
    IF n = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Prerequis manquant : sequence SEQUSERMENU');
    END IF;
    SELECT COUNT(*) INTO n FROM user_sequences WHERE sequence_name = 'SEQ_UTILISATEUR';
    IF n = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Prerequis manquant : sequence SEQ_UTILISATEUR');
    END IF;
    SELECT COUNT(*) INTO n FROM user_objects WHERE object_name = 'GETSEQPARAMCRYPT' AND object_type = 'FUNCTION';
    IF n = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Prerequis manquant : fonction GETSEQPARAMCRYPT');
    END IF;

    -- l'installation ne doit pas etre lancee deux fois
    SELECT COUNT(*) INTO n FROM user_tables WHERE table_name = 'ATH_CLIENT';
    IF n > 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Deja installe (ATH_CLIENT existe). Lancer data/final/drop.sql avant de reinstaller.');
    END IF;
END;
/

-- ============================================================================
-- 1. TABLE SOUCHE : parametres de ponte lus par le calendrier
-- ============================================================================
-- Colonnes lues par V_CAL_SOUCHE / le calendrier. Ajoutees seulement si absentes.
DECLARE
    PROCEDURE ajouter(p_colonne VARCHAR2, p_definition VARCHAR2) IS
    BEGIN
        EXECUTE IMMEDIATE 'ALTER TABLE SOUCHE ADD (' || p_colonne || ' ' || p_definition || ')';
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE <> -1430 THEN   -- ORA-01430 : colonne deja presente
                RAISE;
            END IF;
    END;
BEGIN
    ajouter('NB_JOURS_ECLOSION',   'NUMBER(5) DEFAULT 21');
    ajouter('DUREE_VIE_SEMAINES',  'NUMBER(5) DEFAULT 72');
    ajouter('SEMAINE_DEBUT_PONTE', 'NUMBER(5) DEFAULT 21');
    ajouter('TAUX_ECLOSION',       'NUMBER(5,2) DEFAULT 85');
    ajouter('RATIO_FEMELLE',       'NUMBER(5,2) DEFAULT 100');
END;
/

-- ============================================================================
-- 2. SEQUENCES ET FONCTIONS GETSEQ (utilisees par makePK de l'ORM : prefixe + 6 chiffres)
-- ============================================================================
CREATE SEQUENCE SEQ_BCPOUSSIN START WITH 1;
CREATE SEQUENCE SEQ_BCPOUSSINFILLE START WITH 1;
CREATE SEQUENCE SEQ_BRPOUSSIN START WITH 1;
CREATE SEQUENCE SEQ_INSERTIONCOMP START WITH 1;
CREATE SEQUENCE SEQ_INSERTIONCOMPFILLE START WITH 1;
CREATE SEQUENCE SEQ_COMPARTIMENT START WITH 1;
CREATE SEQUENCE SEQ_SANTEPOUSSIN START WITH 1;
CREATE SEQUENCE SEQ_LIEURECEPTION START WITH 1;
CREATE SEQUENCE SEQ_LOT START WITH 1;
CREATE SEQUENCE SEQ_DECL_PERTE_LOT START WITH 1;
CREATE SEQUENCE SEQ_CONFIG_PREVISION START WITH 1;
CREATE SEQUENCE SEQATHCLIENT START WITH 1;
CREATE SEQUENCE SEQATHPRODUIT START WITH 1;
CREATE SEQUENCE SEQATHFACTURE START WITH 1;
CREATE SEQUENCE SEQATHFACTUREFILLE START WITH 1;
CREATE SEQUENCE SEQATHAVOIR START WITH 1;
CREATE SEQUENCE SEQATHAVOIRFILLE START WITH 1;
CREATE SEQUENCE SEQATHAVOIRUSAGE START WITH 1;

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

CREATE OR REPLACE FUNCTION GETSEQDECL_PERTE_LOT RETURN NUMBER IS
    v NUMBER;
BEGIN
    SELECT SEQ_DECL_PERTE_LOT.NEXTVAL INTO v FROM DUAL;
    RETURN v;
END;
/

CREATE OR REPLACE FUNCTION GETSEQCONFIG_PREVISION RETURN NUMBER IS
    v NUMBER;
BEGIN
    SELECT SEQ_CONFIG_PREVISION.NEXTVAL INTO v FROM DUAL;
    RETURN v;
END;
/

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


-- ============================================================================
-- 3. TABLES (noms de colonnes = attributs de l'ORM Java, sans underscore)
-- ============================================================================
-- Dictionnaires
CREATE TABLE SANTEPOUSSIN (
    id                 VARCHAR2(30) PRIMARY KEY,
    val                VARCHAR2(100),
    desce              VARCHAR2(255)
);

CREATE TABLE LIEURECEPTION (
    id                 VARCHAR2(30) PRIMARY KEY,
    val                VARCHAR2(100),
    desce              VARCHAR2(255)
);

CREATE TABLE COMPARTIMENT (
    id                 VARCHAR2(30) PRIMARY KEY,
    nom                VARCHAR2(100),
    capacite           NUMBER(10,0)
);

-- Etats (table commune : creee seulement si elle n'existe pas deja)
DECLARE
BEGIN
    EXECUTE IMMEDIATE 'CREATE TABLE ETAT (id VARCHAR2(30) PRIMARY KEY, val VARCHAR2(100), desce VARCHAR2(255))';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE <> -955 THEN   -- ORA-00955 : la table existe deja (application de base)
            RAISE;
        END IF;
END;
/

-- Parametrage du calendrier : nombre de pontes par semaine et par souche
CREATE TABLE CONFIG_PREVISION (
    id                 VARCHAR2(30) PRIMARY KEY,
    idsouche           VARCHAR2(50) NOT NULL,
    semaine            NUMBER(10,0) NOT NULL,
    nbjour             NUMBER(15,2) DEFAULT 0
);
ALTER TABLE CONFIG_PREVISION
    ADD CONSTRAINT CK_CFGPREV_NB CHECK (NBJOUR>=0);
ALTER TABLE CONFIG_PREVISION
    ADD CONSTRAINT UQ_CFGPREV_SOUCHE_SEM UNIQUE (IDSOUCHE, SEMAINE);
ALTER TABLE CONFIG_PREVISION
    ADD CONSTRAINT FK_CFGPREV_SOUCHE FOREIGN KEY (IDSOUCHE)
        REFERENCES SOUCHE (ID);

-- Bon de commande poussin (mere / fille)
CREATE TABLE BONCOMMANDE_POUSSIN (
    id                 VARCHAR2(30) PRIMARY KEY,
    daty               DATE,
    reference          VARCHAR2(100),
    descri             VARCHAR2(500),
    idmagasin          VARCHAR2(30),
    etat               NUMBER(2,0) DEFAULT 1,
    remarque           VARCHAR2(500)
);
ALTER TABLE BONCOMMANDE_POUSSIN
    ADD CONSTRAINT CK_BCP_ETAT CHECK (etat IN (0,1,11,9));
ALTER TABLE BONCOMMANDE_POUSSIN
    ADD CONSTRAINT FK_BCP_MAGASIN FOREIGN KEY (IDMAGASIN)
        REFERENCES MAGASIN2 (ID);

CREATE TABLE BONCOMMANDE_POUSSIN_FILLE (
    id                 VARCHAR2(30) PRIMARY KEY,
    idmere             VARCHAR2(30),
    idproduit          VARCHAR2(30),
    idsouche           VARCHAR2(50),
    qte                NUMBER(15,2),
    pu                 NUMBER(15,2),
    fournisseur        VARCHAR2(255)
);
ALTER TABLE BONCOMMANDE_POUSSIN_FILLE
    ADD CONSTRAINT CK_BCPF_QTE CHECK (qte >= 0 AND pu >= 0);
ALTER TABLE BONCOMMANDE_POUSSIN_FILLE
    ADD CONSTRAINT FK_BCPF_MERE FOREIGN KEY (IDMERE)
        REFERENCES BONCOMMANDE_POUSSIN (ID);
ALTER TABLE BONCOMMANDE_POUSSIN_FILLE
    ADD CONSTRAINT FK_BCPF_FOURNISSEUR FOREIGN KEY (FOURNISSEUR)
        REFERENCES FOURNISSEUR (ID);

-- Bon de reception poussin
CREATE TABLE BONRECEPTION_POUSSIN (
    id                 VARCHAR2(30) PRIMARY KEY,
    daty               DATE,
    reference          VARCHAR2(100),
    idboncommande      VARCHAR2(30),
    idboncommandefille VARCHAR2(30),
    idbrprecedent      VARCHAR2(30),
    idlieureception    VARCHAR2(30),
    idmagasin          VARCHAR2(30),
    etat               NUMBER(2,0) DEFAULT 1,
    qtavantcumule      NUMBER(15,2),
    qtaprescumule      NUMBER(15,2),
    maty               NUMBER(15,2) DEFAULT 0,
    very               NUMBER(15,2) DEFAULT 0,
    remarque           VARCHAR2(500),
    dateeclosion       DATE
);
ALTER TABLE BONRECEPTION_POUSSIN
    ADD CONSTRAINT CK_BRP_ETAT CHECK (etat IN (0,1,11,9));
ALTER TABLE BONRECEPTION_POUSSIN
    ADD CONSTRAINT CK_BRP_MATY CHECK (maty >= 0);
ALTER TABLE BONRECEPTION_POUSSIN
    ADD CONSTRAINT CK_BRP_VERY CHECK (very >= 0);
ALTER TABLE BONRECEPTION_POUSSIN
    ADD CONSTRAINT FK_BRP_BC FOREIGN KEY (IDBONCOMMANDE)
        REFERENCES BONCOMMANDE_POUSSIN (ID);
ALTER TABLE BONRECEPTION_POUSSIN
    ADD CONSTRAINT FK_BRP_BCF FOREIGN KEY (IDBONCOMMANDEFILLE)
        REFERENCES BONCOMMANDE_POUSSIN_FILLE (ID);
ALTER TABLE BONRECEPTION_POUSSIN
    ADD CONSTRAINT FK_BRP_PREC FOREIGN KEY (IDBRPRECEDENT)
        REFERENCES BONRECEPTION_POUSSIN (ID);
ALTER TABLE BONRECEPTION_POUSSIN
    ADD CONSTRAINT FK_BRP_LIEU FOREIGN KEY (IDLIEURECEPTION)
        REFERENCES LIEURECEPTION (ID);
ALTER TABLE BONRECEPTION_POUSSIN
    ADD CONSTRAINT FK_BRP_MAGASIN FOREIGN KEY (IDMAGASIN)
        REFERENCES MAGASIN2 (ID);

-- Insertion compartiment (mere / fille)
CREATE TABLE INSERTION_COMPARTIMENT (
    id                 VARCHAR2(30) PRIMARY KEY,
    daty               DATE,
    reference          VARCHAR2(100),
    idbonreception     VARCHAR2(30),
    etat               NUMBER(2,0) DEFAULT 1,
    remarque           VARCHAR2(500)
);
ALTER TABLE INSERTION_COMPARTIMENT
    ADD CONSTRAINT CK_IC_ETAT CHECK (etat IN (0,1,11,9));
ALTER TABLE INSERTION_COMPARTIMENT
    ADD CONSTRAINT FK_IC_BR FOREIGN KEY (IDBONRECEPTION)
        REFERENCES BONRECEPTION_POUSSIN (ID);

CREATE TABLE INSERTION_COMPARTIMENT_FILLE (
    id                 VARCHAR2(30) PRIMARY KEY,
    idmere             VARCHAR2(30),
    idcompartiment     VARCHAR2(30),
    qte                NUMBER(15,2),
    pu                 NUMBER(15,2),
    valeurcompartiment NUMBER(15,2),
    sexe               VARCHAR2(10),
    idsante            VARCHAR2(30)
);
ALTER TABLE INSERTION_COMPARTIMENT_FILLE
    ADD CONSTRAINT CK_ICF_QTE CHECK (qte >= 0 AND pu >= 0);
ALTER TABLE INSERTION_COMPARTIMENT_FILLE
    ADD CONSTRAINT CK_ICF_SEXE CHECK (sexe IN ('0','1'));
ALTER TABLE INSERTION_COMPARTIMENT_FILLE
    ADD CONSTRAINT FK_ICF_MERE FOREIGN KEY (IDMERE)
        REFERENCES INSERTION_COMPARTIMENT (ID);
ALTER TABLE INSERTION_COMPARTIMENT_FILLE
    ADD CONSTRAINT FK_ICF_COMP FOREIGN KEY (IDCOMPARTIMENT)
        REFERENCES COMPARTIMENT (ID);
ALTER TABLE INSERTION_COMPARTIMENT_FILLE
    ADD CONSTRAINT FK_ICF_SEXE FOREIGN KEY (SEXE)
        REFERENCES SEXE (ID);
ALTER TABLE INSERTION_COMPARTIMENT_FILLE
    ADD CONSTRAINT FK_ICF_SANT FOREIGN KEY (IDSANTE)
        REFERENCES SANTEPOUSSIN (ID);

-- Lot et declaration de pertes
CREATE TABLE LOT (
    id                 VARCHAR2(30) PRIMARY KEY,
    idbonreception     VARCHAR2(30),
    idproduit          VARCHAR2(30),
    idsouche           VARCHAR2(50),
    qteinit            NUMBER(15,2),
    pu                 NUMBER(15,2),
    sexe               VARCHAR2(10),
    idsante            VARCHAR2(30),
    idinsertcompfille  VARCHAR2(30),
    idboncommande      VARCHAR2(30),
    qteactuelle        NUMBER(15,2),
    dateeclosion       DATE
);
ALTER TABLE LOT
    ADD CONSTRAINT CK_LOT_QTEINIT CHECK (qteinit >= 0 AND pu >= 0);
ALTER TABLE LOT
    ADD CONSTRAINT CK_LOT_QTEACTUELLE CHECK (qteactuelle >= 0);
ALTER TABLE LOT
    ADD CONSTRAINT FK_LOT_BR FOREIGN KEY (IDBONRECEPTION)
        REFERENCES BONRECEPTION_POUSSIN (ID);
ALTER TABLE LOT
    ADD CONSTRAINT FK_LOT_ICF FOREIGN KEY (IDINSERTCOMPFILLE)
        REFERENCES INSERTION_COMPARTIMENT_FILLE (ID);
ALTER TABLE LOT
    ADD CONSTRAINT FK_LOT_BC FOREIGN KEY (IDBONCOMMANDE)
        REFERENCES BONCOMMANDE_POUSSIN (ID);
ALTER TABLE LOT
    ADD CONSTRAINT FK_LOT_SANTE FOREIGN KEY (IDSANTE)
        REFERENCES SANTEPOUSSIN (ID);

CREATE TABLE DECL_PERTE_LOT (
    id                 VARCHAR2(30) PRIMARY KEY,
    sexe               VARCHAR2(10),
    qtemort            NUMBER(15,2) DEFAULT 0,
    qtevery            NUMBER(15,2) DEFAULT 0,
    daty               DATE,
    reference          VARCHAR2(100),
    remarque           VARCHAR2(500),
    idcompartiment     VARCHAR2(30)
);
ALTER TABLE DECL_PERTE_LOT
    ADD CONSTRAINT CK_DPL_QTE CHECK (QTEMORT>=0 AND QTEVERY>=0);
ALTER TABLE DECL_PERTE_LOT
    ADD CONSTRAINT FK_DPL_SEXE FOREIGN KEY (SEXE)
        REFERENCES SEXE (ID);
ALTER TABLE DECL_PERTE_LOT
    ADD CONSTRAINT fk_dpl_comp FOREIGN KEY (idcompartiment)
        REFERENCES COMPARTIMENT (id);

-- Facture / avoir : clients et produits
CREATE TABLE ATH_CLIENT (
    id                 VARCHAR2(30) PRIMARY KEY,
    nom                VARCHAR2(200) NOT NULL,
    contact            VARCHAR2(100),
    adresse            VARCHAR2(300),
    mail               VARCHAR2(150),
    pourcentageavoir   NUMBER(5,2) DEFAULT 40
);
ALTER TABLE ATH_CLIENT
    ADD CONSTRAINT CK_ATHC_PCTAVOIR CHECK (pourcentageavoir >= 0 AND pourcentageavoir <= 100);

CREATE TABLE ATH_PRODUIT (
    id                 VARCHAR2(30) PRIMARY KEY,
    libelle            VARCHAR2(200) NOT NULL,
    unite              VARCHAR2(30),
    pu                 NUMBER(15,2) DEFAULT 0,
    actif              NUMBER(1,0) DEFAULT 1
);
ALTER TABLE ATH_PRODUIT
    ADD CONSTRAINT CK_ATHP_PU CHECK (pu >= 0);

-- Facture (mere / fille)
CREATE TABLE ATH_FACTURE (
    id                 VARCHAR2(30) PRIMARY KEY,
    daty               DATE,
    reference          VARCHAR2(100),
    idclient           VARCHAR2(30) NOT NULL,
    etat               NUMBER(2,0) DEFAULT 1,
    remarque           VARCHAR2(500),
    montantavoir       NUMBER(15,2) DEFAULT 0
);
ALTER TABLE ATH_FACTURE
    ADD CONSTRAINT CK_ATHF_ETAT CHECK (etat IN (0,1,11,9));
ALTER TABLE ATH_FACTURE
    ADD CONSTRAINT CK_ATHF_AVOIR CHECK (montantavoir >= 0);
ALTER TABLE ATH_FACTURE
    ADD CONSTRAINT FK_ATHF_CLIENT FOREIGN KEY (IDCLIENT)
        REFERENCES ATH_CLIENT (ID);

CREATE TABLE ATH_FACTURE_FILLE (
    id                 VARCHAR2(30) PRIMARY KEY,
    idmere             VARCHAR2(30) NOT NULL,
    idproduit          VARCHAR2(30) NOT NULL,
    qte                NUMBER(15,2),
    pu                 NUMBER(15,2),
    tva                NUMBER(5,2) DEFAULT 0
);
ALTER TABLE ATH_FACTURE_FILLE
    ADD CONSTRAINT CK_ATHFF_VAL CHECK (qte >= 0 AND pu >= 0);
ALTER TABLE ATH_FACTURE_FILLE
    ADD CONSTRAINT CK_ATHFF_TVA CHECK (tva >= 0 AND tva <= 100);
ALTER TABLE ATH_FACTURE_FILLE
    ADD CONSTRAINT FK_ATHFF_MERE FOREIGN KEY (IDMERE)
        REFERENCES ATH_FACTURE (ID);
ALTER TABLE ATH_FACTURE_FILLE
    ADD CONSTRAINT FK_ATHFF_PRODUIT FOREIGN KEY (IDPRODUIT)
        REFERENCES ATH_PRODUIT (ID);

-- Avoir (mere / fille) et consommation du credit
CREATE TABLE ATH_AVOIR (
    id                 VARCHAR2(30) PRIMARY KEY,
    daty               DATE,
    reference          VARCHAR2(100),
    idfacture          VARCHAR2(30) NOT NULL,
    idclient           VARCHAR2(30) NOT NULL,
    etat               NUMBER(2,0) DEFAULT 1,
    motif              VARCHAR2(300),
    remarque           VARCHAR2(500)
);
ALTER TABLE ATH_AVOIR
    ADD CONSTRAINT CK_ATHA_ETAT CHECK (etat IN (0,1,11,9));
ALTER TABLE ATH_AVOIR
    ADD CONSTRAINT FK_ATHA_FACTURE FOREIGN KEY (IDFACTURE)
        REFERENCES ATH_FACTURE (ID);
ALTER TABLE ATH_AVOIR
    ADD CONSTRAINT FK_ATHA_CLIENT FOREIGN KEY (IDCLIENT)
        REFERENCES ATH_CLIENT (ID);

CREATE TABLE ATH_AVOIR_FILLE (
    id                 VARCHAR2(30) PRIMARY KEY,
    idmere             VARCHAR2(30) NOT NULL,
    idfacturefille     VARCHAR2(30) NOT NULL,
    qte                NUMBER(15,2),
    pu                 NUMBER(15,2),
    tva                NUMBER(5,2) DEFAULT 0
);
ALTER TABLE ATH_AVOIR_FILLE
    ADD CONSTRAINT CK_ATHAF_VAL CHECK (qte >= 0 AND pu >= 0);
ALTER TABLE ATH_AVOIR_FILLE
    ADD CONSTRAINT CK_ATHAF_TVA CHECK (tva >= 0 AND tva <= 100);
ALTER TABLE ATH_AVOIR_FILLE
    ADD CONSTRAINT FK_ATHAF_MERE FOREIGN KEY (IDMERE)
        REFERENCES ATH_AVOIR (ID);
ALTER TABLE ATH_AVOIR_FILLE
    ADD CONSTRAINT FK_ATHAF_FF FOREIGN KEY (IDFACTUREFILLE)
        REFERENCES ATH_FACTURE_FILLE (ID);

CREATE TABLE ATH_AVOIR_USAGE (
    id                 VARCHAR2(30) PRIMARY KEY,
    idavoir            VARCHAR2(30) NOT NULL,
    idfacture          VARCHAR2(30) NOT NULL,
    montant            NUMBER(15,2),
    daty               DATE
);
ALTER TABLE ATH_AVOIR_USAGE
    ADD CONSTRAINT CK_ATHAU_MONTANT CHECK (montant > 0);
ALTER TABLE ATH_AVOIR_USAGE
    ADD CONSTRAINT FK_ATHAU_AVOIR FOREIGN KEY (IDAVOIR)
        REFERENCES ATH_AVOIR (ID);
ALTER TABLE ATH_AVOIR_USAGE
    ADD CONSTRAINT FK_ATHAU_FACTURE FOREIGN KEY (IDFACTURE)
        REFERENCES ATH_FACTURE (ID);


-- ============================================================================
-- 4. INDEX
-- ============================================================================
CREATE INDEX idx_bcp_daty             ON BONCOMMANDE_POUSSIN (daty);
CREATE INDEX idx_bcp_ref              ON BONCOMMANDE_POUSSIN (reference);
CREATE INDEX idx_bcpf_mr              ON BONCOMMANDE_POUSSIN_FILLE (idmere);
CREATE INDEX idx_bcpf_prd             ON BONCOMMANDE_POUSSIN_FILLE (idproduit);
CREATE INDEX idx_bcpf_frs             ON BONCOMMANDE_POUSSIN_FILLE (fournisseur);
CREATE INDEX idx_brp_daty             ON BONRECEPTION_POUSSIN (daty);
CREATE INDEX idx_brp_bc               ON BONRECEPTION_POUSSIN (idboncommande);
CREATE INDEX idx_brp_bcf              ON BONRECEPTION_POUSSIN (idboncommandefille);
CREATE INDEX idx_brp_prec             ON BONRECEPTION_POUSSIN (idbrprecedent);
CREATE INDEX idx_ic_br                ON INSERTION_COMPARTIMENT (idbonreception);
CREATE INDEX idx_icf_mr               ON INSERTION_COMPARTIMENT_FILLE (idmere);
CREATE INDEX idx_icf_cmp              ON INSERTION_COMPARTIMENT_FILLE (idcompartiment);
CREATE INDEX idx_lot_br               ON LOT (idbonreception);
CREATE INDEX idx_lot_bc               ON LOT (idboncommande);
CREATE INDEX idx_lot_icf              ON LOT (idinsertcompfille);
CREATE INDEX idx_lot_sexe_daty        ON LOT (sexe, dateeclosion);
CREATE INDEX idx_dpl_daty             ON DECL_PERTE_LOT (daty);
CREATE INDEX idx_dpl_sexe             ON DECL_PERTE_LOT (sexe);
CREATE INDEX idx_dpl_comp_sexe_daty   ON DECL_PERTE_LOT (idcompartiment, sexe, TRUNC(daty));
CREATE INDEX idx_athf_client          ON ATH_FACTURE (idclient);
CREATE INDEX idx_athf_daty            ON ATH_FACTURE (daty);
CREATE INDEX idx_athff_mere           ON ATH_FACTURE_FILLE (idmere);
CREATE INDEX idx_athff_produit        ON ATH_FACTURE_FILLE (idproduit);
CREATE INDEX idx_atha_facture         ON ATH_AVOIR (idfacture);
CREATE INDEX idx_atha_client          ON ATH_AVOIR (idclient);
CREATE INDEX idx_athaf_mere           ON ATH_AVOIR_FILLE (idmere);
CREATE INDEX idx_athaf_ff             ON ATH_AVOIR_FILLE (idfacturefille);
CREATE INDEX idx_athau_avoir          ON ATH_AVOIR_USAGE (idavoir);
CREATE INDEX idx_athau_facture        ON ATH_AVOIR_USAGE (idfacture);

-- ============================================================================
-- 5. VUES (libelles pour les listes et fiches ; calendrier ; facture / avoir)
-- ============================================================================
-- ---------------------------------------------------------------------------
-- REGLES DU CALENDRIER POUSSIN
-- Chaine de calcul :
--   LOT, SOUCHE, CONFIG_PREVISION, DECL_PERTE_LOT, BONCOMMANDE_POUSSIN_FILLE,
--   INSERTION_COMPARTIMENT_FILLE
--     -> V_CAL_PARAM, V_CAL_SOUCHE                       (constantes et parametres)
--     -> V_CAL_LOT_PU, V_CAL_LOT_INFO, V_CAL_INITIAUX, V_CAL_PERTES
--     -> V_CAL_GENERATIONS                               (ponte des generations 0, 1, 2)
--     -> CALENDRIER_POUSSIN_RAW                          (un jour = une ligne)
--     -> CALENDRIER_POUSSIN_CUMUL                        (lu par CalendrierPoussin.java)
--
--  * Constantes (horizon, valeurs par defaut d'une souche) : uniquement dans V_CAL_PARAM.
--  * Gen 0 : semaine de CONFIG_PREVISION = age en semaines revolues ; pas de plafond
--    duree_vie_semaines (c'est la config qui borne).
--  * Gen 1 et 2 : semaine de CONFIG_PREVISION = age + 1 ; ponte entre
--    semaine_debut_ponte et duree_vie_semaines.
--  * Pondeuses gen 0 = qteinit du lot femelle - pertes femelles du compartiment
--    (au prorata du lot) declarees jusqu'au jour de ponte.
--  * CA = poussins de la ponte gen 0 x PU de la commande (gen 1 et 2 hors CA).
--  * Cout = pertes x PU moyen du compartiment ; benefice = CA - cout.
--  * Vivants (poussinscumules) = naissances cumulees - pertes cumulees.
--  * Sexe : '0' = femelle, '1' = male (table SEXE).
-- ---------------------------------------------------------------------------

-- ---- Dictionnaires ----

CREATE OR REPLACE VIEW COMPARTIMENT_LIB AS
SELECT c.id, c.nom, c.capacite
FROM COMPARTIMENT c;

CREATE OR REPLACE VIEW SANTEPOUSSIN_LIB AS
SELECT s.id, s.val, s.desce
FROM SANTEPOUSSIN s;

CREATE OR REPLACE VIEW LIEURECEPTION_LIB AS
SELECT l.id, l.val, l.desce
FROM LIEURECEPTION l;

-- ---- Bon de commande, bon de reception, insertion compartiment, lot ----

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

CREATE OR REPLACE VIEW BONCOMMANDE_POUSSIN_FILLE_LIB AS
SELECT bcpf.id, bcpf.idmere, bcpf.idproduit, bcpf.idsouche,
       bcpf.qte, bcpf.pu, bcpf.fournisseur,
       i.libelle AS produitlib,
       s.val	 AS souchelibr,
       (bcpf.qte * bcpf.pu) AS montant,
       (bcpf.id || ' - ' || NVL(i.libelle, '?')) AS idproduitlib,
       f.nom	 AS fournisseurlib
FROM BONCOMMANDE_POUSSIN_FILLE bcpf
LEFT JOIN AS_INGREDIENTS i ON i.id = bcpf.idproduit
LEFT JOIN SOUCHE	 s ON s.id = bcpf.idsouche
LEFT JOIN FOURNISSEUR	 f ON f.id = bcpf.fournisseur;

CREATE OR REPLACE VIEW BONRECEPTION_POUSSIN_LIB AS
SELECT brp.id,
       brp.daty,
       brp.reference,
       brp.idboncommande,
       brp.idboncommandefille,
       brp.idbrprecedent,
       brp.idlieureception,
       brp.idmagasin,
       brp.etat,
       brp.qtavantcumule,		     --  sans underscore
       brp.qtaprescumule,		     --  sans underscore
       brp.maty,
       brp.very,
       brp.remarque,
       brp.dateeclosion,		     --  NOUVEAU
       bc.reference	AS idboncommandelib,
       bcf.idproduit	AS idboncommandefilleproduit,
       bcf.idsouche	AS idsouche,	      --  NOUVEAU
       bcf.qte		AS qtecommandee,      --  NOUVEAU (bonus)
       brq.reference	AS idbrprecedentlib,
       lr.val		AS idlieureceptionlib,
       m.val		AS idmagasinlib,
       i.libelle	AS produitlib,
       s.val		AS souchelibr,
       CASE brp.etat
	    WHEN 0  THEN 'Annule'
	    WHEN 1  THEN 'Cree'
	    WHEN 11 THEN 'Vise'
	    WHEN 9  THEN 'Cloture'
	    ELSE 'Autre'
       END AS etatlib
FROM BONRECEPTION_POUSSIN brp
LEFT JOIN BONCOMMANDE_POUSSIN	     bc  ON bc.id  = brp.idboncommande
LEFT JOIN BONCOMMANDE_POUSSIN_FILLE  bcf ON bcf.id = brp.idboncommandefille
LEFT JOIN BONRECEPTION_POUSSIN	     brq ON brq.id = brp.idbrprecedent
LEFT JOIN LIEURECEPTION 	     lr  ON lr.id  = brp.idlieureception
LEFT JOIN MAGASIN2		     m	 ON m.id   = brp.idmagasin
LEFT JOIN AS_INGREDIENTS	     i	 ON i.id   = bcf.idproduit
LEFT JOIN SOUCHE		     s	 ON s.id   = bcf.idsouche;

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

CREATE OR REPLACE VIEW INSERTION_COMP_FILLE_LIB AS
SELECT icf.id, icf.idmere, icf.idcompartiment, icf.qte, icf.pu,
       icf.valeurCompartiment, icf.sexe, icf.idsante,
       c.nom	AS compartimentlib,
       s.val	AS sexelib,
       sp.val	AS santelib
FROM INSERTION_COMPARTIMENT_FILLE icf
LEFT JOIN COMPARTIMENT c  ON c.id  = icf.idcompartiment
LEFT JOIN SEXE	       s  ON s.id  = icf.sexe
LEFT JOIN SANTEPOUSSIN sp ON sp.id = icf.idsante;

CREATE OR REPLACE VIEW LOT_LIB AS
SELECT l.ID,
       l.IDBONRECEPTION,
       l.IDPRODUIT,
       l.IDSOUCHE,
       l.QTEINIT,
       l.PU,
       l.SEXE,
       l.IDSANTE,
       l.IDINSERTCOMPFILLE,
       l.IDBONCOMMANDE,
       l.QTEACTUELLE,
       l.DATEECLOSION,
       brp.REFERENCE  AS IDBONRECEPTIONLIB,
       bc.REFERENCE   AS IDBONCOMMANDELIB,
       i.LIBELLE      AS PRODUITLIB,
       s.VAL	      AS SOUCHELIBR,
       sp.VAL	      AS SANTELIB,
       sx.VAL	      AS SEXELIB,
       c.NOM	      AS COMPARTIMENTLIB,
       (l.QTEINIT * l.PU) AS VALEURTOTALE,
       CASE
	    WHEN l.QTEINIT > 0 AND l.QTEACTUELLE > 0
	    THEN l.PU * (l.QTEINIT / l.QTEACTUELLE)
	    ELSE 0
       END AS VALEURUNITAIRE
FROM LOT l
LEFT JOIN BONRECEPTION_POUSSIN	     brp ON brp.ID = l.IDBONRECEPTION
LEFT JOIN BONCOMMANDE_POUSSIN	     bc  ON bc.ID  = l.IDBONCOMMANDE
LEFT JOIN AS_INGREDIENTS	     i	 ON i.ID   = l.IDPRODUIT
LEFT JOIN SOUCHE		     s	 ON s.ID   = l.IDSOUCHE
LEFT JOIN SANTEPOUSSIN		     sp  ON sp.ID  = l.IDSANTE
LEFT JOIN SEXE			     sx  ON sx.ID  = l.SEXE
LEFT JOIN INSERTION_COMPARTIMENT_FILLE icf ON icf.ID = l.IDINSERTCOMPFILLE
LEFT JOIN COMPARTIMENT		     c	 ON c.ID   = icf.IDCOMPARTIMENT;

CREATE OR REPLACE VIEW SITUATION_COMPARTIMENT AS
SELECT
       l.id,
       brp.idboncommandefille,
       c.id						 AS compid,
       NVL(l.qteinit, 0)				 AS qtetheorique,
       NVL(l.qteactuelle, 0)				 AS qteactuelle,
       NVL(l.pu, 0)					 AS pu,
       NVL(bcf.qte, 0)					 AS qtebc,
       NVL(SUM(l.qteactuelle) OVER (PARTITION BY bcf.id), 0) AS sommeqteactuelle,
       NVL(ROUND(l.pu * (bcf.qte
		     / NULLIF(SUM(l.qteactuelle) OVER (PARTITION BY bcf.id), 0)), 2), 0)
							 AS valeurunitaire
FROM LOT l
LEFT JOIN BONRECEPTION_POUSSIN	       brp ON brp.id = l.idbonreception
LEFT JOIN BONCOMMANDE_POUSSIN_FILLE    bcf ON bcf.id = brp.idboncommandefille
LEFT JOIN INSERTION_COMPARTIMENT_FILLE icf ON icf.id = l.idinsertcompfille
LEFT JOIN COMPARTIMENT		       c   ON c.id   = icf.idcompartiment;

-- ---- Calendrier poussin : parametres ----

CREATE OR REPLACE VIEW V_CAL_PARAM AS
SELECT 2000 AS horizon_jours,		    -- jours calcules apres la derniere eclosion
       21   AS def_nb_jours_eclosion,	    -- souche sans valeur : jours d'incubation
       21   AS def_semaine_debut_ponte,     -- souche sans valeur : 1re semaine de ponte
       85   AS def_taux_eclosion,	    -- souche sans valeur : % d'oeufs qui eclosent
       50   AS def_ratio_femelle,	    -- souche sans valeur : % de femelles
       72   AS def_duree_vie_semaines	    -- souche sans valeur : fin de ponte (semaines)
FROM   dual;

CREATE OR REPLACE VIEW V_CAL_SOUCHE AS
SELECT s.id						     AS idsouche,
       NVL(s.nb_jours_eclosion,   p.def_nb_jours_eclosion)   AS nb_jours_eclosion,
       NVL(s.semaine_debut_ponte, p.def_semaine_debut_ponte) AS semaine_debut_ponte,
       NVL(s.taux_eclosion,	  p.def_taux_eclosion)	     AS taux_eclosion,
       NVL(s.ratio_femelle,	  p.def_ratio_femelle)	     AS ratio_femelle,
       NVL(s.duree_vie_semaines,  p.def_duree_vie_semaines)  AS duree_vie_semaines
FROM   SOUCHE s
       CROSS JOIN V_CAL_PARAM p;

-- ---- Calendrier poussin : calcul ----

CREATE OR REPLACE VIEW V_CAL_BORNES AS
SELECT MIN(TRUNC(l.dateeclosion))			AS d_min,
       MAX(TRUNC(l.dateeclosion)) + MAX(p.horizon_jours) AS d_max
FROM   LOT l
       CROSS JOIN V_CAL_PARAM p
WHERE  l.dateeclosion IS NOT NULL;

CREATE OR REPLACE VIEW V_CAL_JOURS AS
SELECT b.d_min + LEVEL - 1 AS daty
FROM   V_CAL_BORNES b
CONNECT BY LEVEL <= b.d_max - b.d_min + 1;

CREATE OR REPLACE VIEW V_CAL_K7 AS
SELECT LEVEL - 1 AS k FROM dual CONNECT BY LEVEL <= 7;

CREATE OR REPLACE VIEW V_CAL_LOT_PU AS
SELECT l.id AS idlot,
       NVL(bf.pu, NVL(l.pu, 0)) AS pu
FROM   LOT l
       LEFT JOIN (SELECT idmere, idsouche,
			 SUM(NVL(qte,0) * NVL(pu,0)) / NULLIF(SUM(NVL(qte,0)), 0) AS pu
		  FROM	 BONCOMMANDE_POUSSIN_FILLE
		  GROUP  BY idmere, idsouche) bf
	      ON bf.idmere   = l.idboncommande
	     AND bf.idsouche = l.idsouche;

CREATE OR REPLACE VIEW V_CAL_INITIAUX AS
SELECT TRUNC(l.dateeclosion)				       AS daty,
       SUM(l.qteinit)					       AS total,
       SUM(CASE WHEN l.sexe = '0' THEN l.qteinit ELSE 0 END)   AS femelles,
       SUM(CASE WHEN l.sexe = '1' THEN l.qteinit ELSE 0 END)   AS males
FROM   LOT l
WHERE  l.dateeclosion IS NOT NULL
GROUP  BY TRUNC(l.dateeclosion);

CREATE OR REPLACE VIEW V_CAL_LOT_INFO AS
SELECT x.idlot, x.idsouche, x.idcompartiment, x.qteinit,
       x.dateeclosion, x.nb_jours_eclosion, x.semaine_debut_ponte,
       x.taux_eclosion, x.ratio_femelle, x.pu,
       NVL(x.qteinit / NULLIF(SUM(x.qteinit) OVER (PARTITION BY x.idcompartiment), 0), 0) AS part_comp
FROM  (SELECT l.id						    AS idlot,
	      l.idsouche,
	      icf.idcompartiment,
	      l.qteinit,
	      TRUNC(l.dateeclosion)				    AS dateeclosion,
	      NVL(vs.nb_jours_eclosion,   p.def_nb_jours_eclosion)   AS nb_jours_eclosion,
	      NVL(vs.semaine_debut_ponte, p.def_semaine_debut_ponte) AS semaine_debut_ponte,
	      NVL(vs.taux_eclosion,	  p.def_taux_eclosion)	     AS taux_eclosion,
	      NVL(vs.ratio_femelle,	  p.def_ratio_femelle)	     AS ratio_femelle,
	      lp.pu
       FROM   LOT l
	      CROSS JOIN V_CAL_PARAM p
	      LEFT JOIN V_CAL_SOUCHE vs 		 ON vs.idsouche = l.idsouche
	      LEFT JOIN INSERTION_COMPARTIMENT_FILLE icf ON icf.id	= l.idinsertcompfille
	      LEFT JOIN V_CAL_LOT_PU lp 		 ON lp.idlot	= l.id
       WHERE  l.sexe = '0' AND l.dateeclosion IS NOT NULL) x;

CREATE OR REPLACE VIEW V_CAL_GENERATIONS AS
WITH bornes AS (
    SELECT d_max FROM V_CAL_BORNES
),
cfg AS (
    -- MAX evite ORA-01427 sur les doublons ; les semaines a 0 ne produisent rien
    SELECT idsouche, semaine, MAX(nbjour) AS nbjour
    FROM   CONFIG_PREVISION
    GROUP  BY idsouche, semaine
    HAVING MAX(nbjour) > 0
),
-- ---------- Gen 0 : un lot x une semaine de ponte x 7 jours ----------
gen0_calc AS (
    SELECT li.idsouche,
	   li.dateeclosion + 7 * c.semaine + k.k + li.nb_jours_eclosion AS daty,
	   li.ratio_femelle,
	   li.pu,
	   GREATEST(0, li.qteinit - li.part_comp * NVL((
	       SELECT SUM(NVL(pl.qtemort,0) + NVL(pl.qtevery,0))
	       FROM   DECL_PERTE_LOT pl
	       WHERE  pl.idcompartiment = li.idcompartiment
		 AND  pl.sexe = '0'
		 -- = TRUNC(pl.daty) <= jour de ponte, en gardant l'index (IDCOMPARTIMENT, SEXE, DATY)
		 AND  pl.daty < li.dateeclosion + 7 * c.semaine + k.k + 1
	   ), 0))
	   * c.nbjour * li.taux_eclosion / 100 AS nb
    FROM   V_CAL_LOT_INFO li
	   JOIN cfg c ON c.idsouche = li.idsouche
		     AND c.semaine >= li.semaine_debut_ponte
	   CROSS JOIN V_CAL_K7 k
),
gen0 AS (
    SELECT /*+ MATERIALIZE */
	   daty, idsouche,
	   MAX(pu)			 AS pu,
	   SUM(nb)			 AS total,
	   SUM(nb * ratio_femelle / 100) AS femelles
    FROM   gen0_calc
    WHERE  daty <= (SELECT d_max FROM bornes)
    GROUP  BY daty, idsouche
),
-- ---------- Gen 1 : cohortes = femelles nees de la ponte gen 0 ----------
gen1_calc AS (
    SELECT h.idsouche,
	   h.daty + vs.nb_jours_eclosion + 7 * (c.semaine - 1) + k.k AS daty,
	   vs.ratio_femelle,
	   h.femelles * c.nbjour * vs.taux_eclosion / 100 AS nb
    FROM   gen0 h
	   JOIN V_CAL_SOUCHE vs ON vs.idsouche = h.idsouche
	   JOIN cfg c		ON c.idsouche  = h.idsouche
			       AND c.semaine - 1
>= vs.semaine_debut_ponte
			       AND c.semaine - 1 <= vs.duree_vie_semaines
	   CROSS JOIN V_CAL_K7 k
    WHERE  h.femelles > 0
),
gen1 AS (
    SELECT /*+ MATERIALIZE */
	   daty, idsouche,
	   SUM(nb)			 AS total,
	   SUM(nb * ratio_femelle / 100) AS femelles
    FROM   gen1_calc
    WHERE  daty <= (SELECT d_max FROM bornes)
    GROUP  BY daty, idsouche
),
-- ---------- Gen 2 : cohortes = femelles nees de la ponte gen 1 ----------
gen2_calc AS (
    SELECT h.daty + vs.nb_jours_eclosion + 7 * (c.semaine - 1) + k.k AS daty,
	   vs.ratio_femelle,
	   h.femelles * c.nbjour * vs.taux_eclosion / 100 AS nb
    FROM   gen1 h
	   JOIN V_CAL_SOUCHE vs ON vs.idsouche = h.idsouche
	   JOIN cfg c		ON c.idsouche  = h.idsouche
			       AND c.semaine - 1 >= vs.semaine_debut_ponte
			       AND c.semaine - 1 <= vs.duree_vie_semaines
	   CROSS JOIN V_CAL_K7 k
    WHERE  h.femelles > 0
),
gen2 AS (
    SELECT daty,
	   SUM(nb)			 AS total,
	   SUM(nb * ratio_femelle / 100) AS femelles
    FROM   gen2_calc
    WHERE  daty <= (SELECT d_max FROM bornes)
    GROUP  BY daty
)
SELECT daty,
       SUM(g0_total)	AS g0_total,
       SUM(g0_femelles) AS g0_femelles,
       SUM(g0_ca)	AS g0_ca,
       SUM(g1_total)	AS g1_total,
       SUM(g1_femelles) AS g1_femelles,
       SUM(g2_total)	AS g2_total,
       SUM(g2_femelles) AS g2_femelles
FROM (
    SELECT daty, total AS g0_total, femelles AS g0_femelles, total * pu AS g0_ca,
	   0 AS g1_total, 0 AS g1_femelles, 0 AS g2_total, 0 AS g2_femelles
    FROM   gen0
    UNION ALL
    SELECT daty, 0, 0, 0, total, femelles, 0, 0
    FROM   gen1
    UNION ALL
    SELECT daty, 0, 0, 0, 0, 0, total, femelles
    FROM   gen2
)
GROUP  BY daty;

CREATE OR REPLACE VIEW V_CAL_PONTE_JOUR AS
SELECT daty, g0_total AS total, g0_femelles AS femelles, g0_ca AS ca
FROM   V_CAL_GENERATIONS
WHERE  g0_total > 0;

CREATE OR REPLACE VIEW V_CAL_GEN1_PONTE AS
SELECT daty, g1_total AS total, g1_femelles AS femelles
FROM   V_CAL_GENERATIONS
WHERE  g1_total > 0;

CREATE OR REPLACE VIEW V_CAL_GEN2_PONTE AS
SELECT daty, g2_total AS total, g2_femelles AS femelles
FROM   V_CAL_GENERATIONS
WHERE  g2_total > 0;

CREATE OR REPLACE VIEW V_CAL_PERTES AS
WITH pu_comp AS (
    SELECT icf.idcompartiment,
	   SUM(NVL(l.qteinit,0) * NVL(icf.pu,0)) / NULLIF(SUM(NVL(l.qteinit,0)), 0) AS pu_moyen
    FROM   INSERTION_COMPARTIMENT_FILLE icf
	   LEFT JOIN LOT l ON l.idinsertcompfille = icf.id
    GROUP  BY icf.idcompartiment
)
SELECT TRUNC(dp.daty) AS daty,
       SUM(NVL(dp.qtemort,0) + NVL(dp.qtevery,0))					  AS pertes,
       SUM(CASE WHEN dp.sexe = '0' THEN NVL(dp.qtemort,0) + NVL(dp.qtevery,0) ELSE 0 END) AS pertesfemelles,
       SUM(CASE WHEN dp.sexe = '1' THEN NVL(dp.qtemort,0) + NVL(dp.qtevery,0) ELSE 0 END) AS pertesmales,
       SUM((NVL(dp.qtemort,0) + NVL(dp.qtevery,0)) * NVL(pc.pu_moyen, 0))		  AS cout
FROM   DECL_PERTE_LOT dp
       LEFT JOIN pu_comp pc ON pc.idcompartiment = dp.idcompartiment
WHERE  dp.daty IS NOT NULL
GROUP  BY TRUNC(dp.daty);

CREATE OR REPLACE VIEW V_CAL_PERTES_COUT AS
SELECT daty, cout FROM V_CAL_PERTES;

CREATE OR REPLACE VIEW CALENDRIER_POUSSIN_RAW AS
SELECT d.daty,
       NVL(i.total, 0) + NVL(g.g0_total, 0) + NVL(g.g1_total, 0) + NVL(g.g2_total, 0) AS poussins,
       NVL(i.males, 0)
       + (NVL(g.g0_total, 0) - NVL(g.g0_femelles, 0))
       + (NVL(g.g1_total, 0) - NVL(g.g1_femelles, 0))
       + (NVL(g.g2_total, 0) - NVL(g.g2_femelles, 0))				       AS poussinsmales,
       NVL(i.femelles, 0) + NVL(g.g0_femelles, 0) + NVL(g.g1_femelles, 0) + NVL(g.g2_femelles, 0) AS poussinsfemelles,
       NVL(t.pertes, 0) 	AS pertes,
       NVL(t.pertesmales, 0)	AS pertesmales,
       NVL(t.pertesfemelles, 0) AS pertesfemelles,
       NVL(g.g0_ca, 0)		AS ca,
       NVL(t.cout, 0)		AS cout,
       NVL(g.g0_ca, 0) - NVL(t.cout, 0) AS benefice
FROM   V_CAL_JOURS d
       LEFT JOIN V_CAL_INITIAUX    i ON i.daty = d.daty
       LEFT JOIN V_CAL_GENERATIONS g ON g.daty = d.daty
       LEFT JOIN V_CAL_PERTES	   t ON t.daty = d.daty;

CREATE OR REPLACE VIEW CALENDRIER_POUSSIN_CUMUL AS
SELECT daty,
       poussins, poussinsmales, poussinsfemelles, pertes, ca, cout, benefice,
       naissancescumulees, malesnescumules, femellesneescumulees,
       pertescumulees, pertesmalescumulees, pertesfemellescumulees,
       GREATEST(0, naissancescumulees	- pertescumulees)	  AS poussinscumules,
       GREATEST(0, malesnescumules	- pertesmalescumulees)	  AS malescumules,
       GREATEST(0, femellesneescumulees - pertesfemellescumulees) AS femellescumulees
FROM (
    SELECT r.daty, r.poussins, r.poussinsmales, r.poussinsfemelles,
	   r.pertes, r.ca, r.cout, r.benefice,
	   SUM(r.poussins)	   OVER (ORDER BY r.daty ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS naissancescumulees,
	   SUM(r.poussinsmales)    OVER (ORDER BY r.daty ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS malesnescumules,
	   SUM(r.poussinsfemelles) OVER (ORDER BY r.daty ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS femellesneescumulees,
	   SUM(r.pertes)	   OVER (ORDER BY r.daty ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS pertescumulees,
	   SUM(r.pertesmales)	   OVER (ORDER BY r.daty ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS pertesmalescumulees,
	   SUM(r.pertesfemelles)   OVER (ORDER BY r.daty ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS pertesfemellescumulees
    FROM   CALENDRIER_POUSSIN_RAW r
);

-- ---- Facture / avoir ----

CREATE OR REPLACE VIEW ATH_FACTURE_FILLE_LIB AS
SELECT ff.id, ff.idmere, ff.idproduit, ff.qte, ff.pu, ff.tva,
       p.libelle				   AS produitlib,
       ROUND(ff.qte * ff.pu, 2) 		   AS montantht,
       ROUND(ff.qte * ff.pu * ff.tva / 100, 2)	   AS montanttva,
       ROUND(ff.qte * ff.pu, 2)
	 + ROUND(ff.qte * ff.pu * ff.tva / 100, 2) AS montantttc,
       NVL(r.qteretournee, 0)			   AS qteretournee,
       ff.qte - NVL(r.qteretournee, 0)		   AS qtedisponible
FROM ATH_FACTURE_FILLE ff
LEFT JOIN ATH_PRODUIT p ON p.id = ff.idproduit
LEFT JOIN (SELECT af.idfacturefille, SUM(af.qte) AS qteretournee
	   FROM ATH_AVOIR_FILLE af
	   JOIN ATH_AVOIR a ON a.id = af.idmere
	   WHERE a.etat <> 0
	   GROUP BY af.idfacturefille) r ON r.idfacturefille = ff.id;

CREATE OR REPLACE VIEW ATH_FACTURE_LIB AS
SELECT f.id, f.daty, f.reference, f.idclient, f.etat, f.remarque,
       NVL(f.montantavoir, 0)			     AS montantavoir,
       c.nom					     AS idclientlib,
       NVL(t.montantht, 0)			     AS montantht,
       NVL(t.montanttva, 0)			     AS montanttva,
       NVL(t.montantttc, 0)			     AS montantttc,
       NVL(c.pourcentageavoir, 0)		     AS pourcentageavoir,
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

CREATE OR REPLACE VIEW ATH_AVOIR_FILLE_LIB AS
SELECT af.id, af.idmere, af.idfacturefille, af.qte, af.pu, af.tva,
       p.libelle				   AS produitlib,
       ff.qte					   AS qtefacturee,
       ROUND(af.qte * af.pu, 2) 		   AS montantht,
       ROUND(af.qte * af.pu * af.tva / 100, 2)	   AS montanttva,
       ROUND(af.qte * af.pu, 2)
	 + ROUND(af.qte * af.pu * af.tva / 100, 2) AS montantttc
FROM ATH_AVOIR_FILLE af
LEFT JOIN ATH_FACTURE_FILLE ff ON ff.id = af.idfacturefille
LEFT JOIN ATH_PRODUIT	    p  ON p.id	= ff.idproduit;

CREATE OR REPLACE VIEW ATH_AVOIR_LIB AS
SELECT a.id, a.daty, a.reference, a.idfacture, a.idclient, a.etat,
       a.motif, a.remarque,
       NVL(f.reference, f.id)			     AS idfacturelib,
       c.nom					     AS idclientlib,
       NVL(t.montantht, 0)			     AS montantht,
       NVL(t.montanttva, 0)			     AS montanttva,
       NVL(t.montantttc, 0)			     AS montantttc,
       NVL(u.montantutilise, 0) 		     AS montantutilise,
       CASE WHEN a.etat = 11
	    THEN NVL(t.montantttc, 0) - NVL(u.montantutilise, 0)
	    ELSE 0
       END					     AS reste,
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

CREATE OR REPLACE VIEW ATH_CLIENT_SOLDE AS
SELECT c.id, c.nom, c.pourcentageavoir,
       NVL(s.totalavoir, 0)			    AS totalavoir,
       NVL(s.totalutilise, 0)			    AS totalutilise,
       NVL(s.totalavoir, 0) - NVL(s.totalutilise, 0) AS solde
FROM ATH_CLIENT c
LEFT JOIN (SELECT idclient,
		  SUM(montantttc)     AS totalavoir,
		  SUM(montantutilise) AS totalutilise
	   FROM ATH_AVOIR_LIB
	   WHERE etat = 11
	   GROUP BY idclient) s ON s.idclient = c.id;

-- ============================================================================
-- 6. DONNEES DE REFERENCE
-- ============================================================================
-- Dictionnaires
INSERT INTO SANTEPOUSSIN (id, val, desce)
SELECT 'SP01', 'SAIN', 'Poussin en bonne sante' FROM dual WHERE NOT EXISTS (SELECT 1 FROM SANTEPOUSSIN WHERE id = 'SP01');
INSERT INTO SANTEPOUSSIN (id, val, desce)
SELECT 'SP02', 'MARARY', 'Poussin malade' FROM dual WHERE NOT EXISTS (SELECT 1 FROM SANTEPOUSSIN WHERE id = 'SP02');

INSERT INTO LIEURECEPTION (id, val, desce)
SELECT 'TR01', 'Aeroport', 'Reception a l aeroport' FROM dual WHERE NOT EXISTS (SELECT 1 FROM LIEURECEPTION WHERE id = 'TR01');
INSERT INTO LIEURECEPTION (id, val, desce)
SELECT 'TR02', 'Intermediaire', 'Reception lieu intermediaire' FROM dual WHERE NOT EXISTS (SELECT 1 FROM LIEURECEPTION WHERE id = 'TR02');
INSERT INTO LIEURECEPTION (id, val, desce)
SELECT 'TR03', 'Magasin', 'Reception en magasin' FROM dual WHERE NOT EXISTS (SELECT 1 FROM LIEURECEPTION WHERE id = 'TR03');

-- Etats (accents via UNISTR : independant du jeu de caracteres du client)
INSERT INTO ETAT (id, val, desce)
SELECT '1', 'Cree', UNISTR('Cr\00e9\00e9') FROM dual WHERE NOT EXISTS (SELECT 1 FROM ETAT WHERE id = '1');
INSERT INTO ETAT (id, val, desce)
SELECT '11', 'Vise', UNISTR('Vis\00e9') FROM dual WHERE NOT EXISTS (SELECT 1 FROM ETAT WHERE id = '11');
INSERT INTO ETAT (id, val, desce)
SELECT '0', 'Annule', UNISTR('Annul\00e9') FROM dual WHERE NOT EXISTS (SELECT 1 FROM ETAT WHERE id = '0');
INSERT INTO ETAT (id, val, desce)
SELECT '9', 'Cloture', UNISTR('Cl\00f4tur\00e9') FROM dual WHERE NOT EXISTS (SELECT 1 FROM ETAT WHERE id = '9');

-- Compartiments (a adapter a la ferme)
INSERT INTO COMPARTIMENT (id, nom, capacite)
SELECT 'CMP' || LPAD(GETSEQCOMPARTIMENT, 6, '0'), nom, capacite
FROM (SELECT 'PARQUET1' AS nom, 3000 AS capacite FROM dual UNION ALL
      SELECT 'PARQUET2', 500  FROM dual UNION ALL
      SELECT 'PARQUET3', 1000 FROM dual UNION ALL
      SELECT 'PARQUET4', 1000 FROM dual);

-- Planning de ponte de la souche SOUCHE001 (ISA BROWN), si elle existe :
-- semaines 1 a 20 : pas de ponte (0) ; semaines 21 a 72 : 1 ponte / jour (1).
-- Pour une autre souche, ajouter ses lignes dans CONFIG_PREVISION (idsouche, semaine, nbjour).
INSERT INTO CONFIG_PREVISION (id, idsouche, semaine, nbjour)
SELECT 'CFP' || LPAD(GETSEQCONFIG_PREVISION, 6, '0'), s.id, w.semaine, CASE WHEN w.semaine >= 21 THEN 1 ELSE 0 END
FROM   SOUCHE s
       CROSS JOIN (SELECT LEVEL AS semaine FROM dual CONNECT BY LEVEL <= 72) w
WHERE  s.id = 'SOUCHE001';

COMMIT;

-- ============================================================================
-- 7. ROLE ET UTILISATEUR "resp" (crees seulement s'ils n'existent pas)
-- ============================================================================
-- Le role "resp" donne acces aux menus Poussins, Prevision et Facturation (section 8).
--
-- Compte de connexion : login  resp  /  mot de passe a saisir  Resp2026
-- A CHANGER apres la premiere connexion (administration des utilisateurs de l'application).
--
-- L'application ne stocke pas le mot de passe saisi : elle le chiffre avec les parametres de
-- PARAMCRYPT (une ligne par utilisateur : niveau, sens) et compare avec UTILISATEUR.PWDUSER.
-- Ici niveau = 4, sens = 1 (comme les autres utilisateurs) : "Resp2026" est stocke "naol2022".
-- Pour un autre mot de passe : le changer depuis l'application (elle chiffre toute seule), ou
-- calculer la valeur avec Utilitaire.cryptWord(mot, 4, 1) puis remplacer 'naol2022' ci-dessous.

INSERT INTO ROLES (IDROLE, DESCROLE, RANG)
SELECT 'resp', 'MR TAHINA', 6
FROM dual WHERE NOT EXISTS (SELECT 1 FROM ROLES WHERE IDROLE = 'resp');

INSERT INTO UTILISATEUR (REFUSER, LOGINUSER, PWDUSER, NOMUSER, ADRUSER, TELUSER, IDROLE)
SELECT SEQ_UTILISATEUR.NEXTVAL, 'resp', 'naol2022', 'Responsable poussin et facturation', NULL, NULL, 'resp'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM UTILISATEUR WHERE LOGINUSER = 'resp');

-- sans cette ligne la connexion echoue ("Pas de cryptage associe")
INSERT INTO PARAMCRYPT (ID, NIVEAU, CROISSANTE, IDUTILISATEUR)
SELECT 'CRY' || LPAD(GETSEQPARAMCRYPT, 6, '0'), 4, 1, TO_CHAR(u.REFUSER)
FROM   UTILISATEUR u
WHERE  u.LOGINUSER = 'resp'
  AND  NOT EXISTS (SELECT 1 FROM PARAMCRYPT p WHERE p.IDUTILISATEUR = TO_CHAR(u.REFUSER));

COMMIT;

-- ============================================================================
-- 8. MENU (Poussins, Prevision, Facturation) + droits du role "resp"
-- ============================================================================
-- Les roles et menus sont mis en cache : redemarrer WildFly apres l'import.
-- Le role "resp" est cree a la section 7.

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENUATHFACT', 'Facturation', 'receipt_long',
       'module.jsp?but=a_tahina/v1/facture/facture/facture-liste.jsp',
       51, 1, NULL
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENUATHFACT');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENUPOUSSON', 'Poussins', 'egg',
       'module.jsp?but=a_tahina/v1/bc/boncommande-liste.jsp',
       50, 1, NULL
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENUPOUSSON');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENUPOUSSON_SITUCOMP', 'Situation des compartiments', 'egg',
       'module.jsp?but=a_tahina/v1/compartiment/situation.jsp',
       50, 1, NULL
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENUPOUSSON_SITUCOMP');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENUATHFACT_AVOIR', 'Avoirs', 'assignment_return',
       'module.jsp?but=a_tahina/v1/facture/avoir/avoir-liste.jsp',
       4, 2, 'MENUATHFACT'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENUATHFACT_AVOIR');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENUATHFACT_CLIENT', 'Clients', 'group',
       'module.jsp?but=a_tahina/v1/facture/client/client-liste.jsp',
       1, 2, 'MENUATHFACT'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENUATHFACT_CLIENT');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENUATHFACT_FACTURE', 'Factures', 'receipt',
       'module.jsp?but=a_tahina/v1/facture/facture/facture-liste.jsp',
       3, 2, 'MENUATHFACT'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENUATHFACT_FACTURE');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENUATHFACT_PRODUIT', 'Produits', 'inventory_2',
       'module.jsp?but=a_tahina/v1/facture/produit/produit-liste.jsp',
       2, 2, 'MENUATHFACT'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENUATHFACT_PRODUIT');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENUPOUSSON_BC', 'Bons de commande', 'shopping_cart',
       'module.jsp?but=a_tahina/v1/bc/boncommande-liste.jsp',
       1, 2, 'MENUPOUSSON'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENUPOUSSON_BC');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENUPOUSSON_BR', 'Bons de r&eacute;ception', 'inventory_2',
       'module.jsp?but=a_tahina/v1/br/bonreception-liste.jsp',
       2, 2, 'MENUPOUSSON'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENUPOUSSON_BR');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENUPOUSSON_IC', 'Insertions compartiment', 'grid_view',
       'module.jsp?but=a_tahina/v1/ic/insertioncompartiment-liste.jsp',
       3, 2, 'MENUPOUSSON'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENUPOUSSON_IC');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENUATHFACT_AVOIR_ADD', 'Saisir un avoir', 'add_box',
       'module.jsp?but=a_tahina/v1/facture/avoir/avoir-saisie.jsp',
       1, 3, 'MENUATHFACT_AVOIR'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENUATHFACT_AVOIR_ADD');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENUATHFACT_AVOIR_LIST', 'Liste des avoirs', 'list',
       'module.jsp?but=a_tahina/v1/facture/avoir/avoir-liste.jsp',
       2, 3, 'MENUATHFACT_AVOIR'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENUATHFACT_AVOIR_LIST');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENUATHFACT_CLIENT_ADD', 'Saisir un client', 'add_box',
       'module.jsp?but=a_tahina/v1/facture/client/client-saisie.jsp',
       1, 3, 'MENUATHFACT_CLIENT'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENUATHFACT_CLIENT_ADD');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENUATHFACT_CLIENT_LIST', 'Liste des clients', 'list',
       'module.jsp?but=a_tahina/v1/facture/client/client-liste.jsp',
       2, 3, 'MENUATHFACT_CLIENT'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENUATHFACT_CLIENT_LIST');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENUATHFACT_FACTURE_ADD', 'Saisir une facture', 'add_box',
       'module.jsp?but=a_tahina/v1/facture/facture/facture-saisie.jsp',
       1, 3, 'MENUATHFACT_FACTURE'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENUATHFACT_FACTURE_ADD');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENUATHFACT_FACTURE_LIST', 'Liste des factures', 'list',
       'module.jsp?but=a_tahina/v1/facture/facture/facture-liste.jsp',
       2, 3, 'MENUATHFACT_FACTURE'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENUATHFACT_FACTURE_LIST');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENUATHFACT_PRODUIT_ADD', 'Saisir un produit', 'add_box',
       'module.jsp?but=a_tahina/v1/facture/produit/produit-saisie.jsp',
       1, 3, 'MENUATHFACT_PRODUIT'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENUATHFACT_PRODUIT_ADD');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENUATHFACT_PRODUIT_LIST', 'Liste des produits', 'list',
       'module.jsp?but=a_tahina/v1/facture/produit/produit-liste.jsp',
       2, 3, 'MENUATHFACT_PRODUIT'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENUATHFACT_PRODUIT_LIST');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENUPOUSSON_BC_ADD', 'Saisir un bon de commande', 'add_box',
       'module.jsp?but=a_tahina/v1/bc/boncommande-saisie.jsp',
       1, 3, 'MENUPOUSSON_BC'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENUPOUSSON_BC_ADD');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENUPOUSSON_BC_LIST', 'Liste des BC', 'list',
       'module.jsp?but=a_tahina/v1/bc/boncommande-liste.jsp',
       2, 3, 'MENUPOUSSON_BC'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENUPOUSSON_BC_LIST');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENUPOUSSON_BR_ADD', 'Saisir une r&eacute;ception', 'add_box',
       'module.jsp?but=a_tahina/v1/br/bonreception-saisie.jsp',
       1, 3, 'MENUPOUSSON_BR'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENUPOUSSON_BR_ADD');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENUPOUSSON_BR_LIST', 'Liste des BR', 'list',
       'module.jsp?but=a_tahina/v1/br/bonreception-liste.jsp',
       2, 3, 'MENUPOUSSON_BR'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENUPOUSSON_BR_LIST');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENUPOUSSON_IC_ADD', 'Saisir une insertion', 'add_box',
       'module.jsp?but=a_tahina/v1/ic/insertioncompartiment-saisie.jsp',
       1, 3, 'MENUPOUSSON_IC'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENUPOUSSON_IC_ADD');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENUPOUSSON_IC_LIST', 'Liste des IC', 'list',
       'module.jsp?but=a_tahina/v1/ic/insertioncompartiment-liste.jsp',
       2, 3, 'MENUPOUSSON_IC'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENUPOUSSON_IC_LIST');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENU_PREVISION_POUSSIN', 'Prevision poussin', 'egg_alt',
       'module.jsp?but=a_tahina/v1/prevision/prevision-poussin.jsp',
       1, 1, NULL
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENU_PREVISION_POUSSIN');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENUATHFACT', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENUATHFACT' AND IDROLE = 'resp');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENUATHFACT_AVOIR', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENUATHFACT_AVOIR' AND IDROLE = 'resp');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENUATHFACT_AVOIR_ADD', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENUATHFACT_AVOIR_ADD' AND IDROLE = 'resp');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENUATHFACT_AVOIR_LIST', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENUATHFACT_AVOIR_LIST' AND IDROLE = 'resp');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENUATHFACT_CLIENT', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENUATHFACT_CLIENT' AND IDROLE = 'resp');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENUATHFACT_CLIENT_ADD', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENUATHFACT_CLIENT_ADD' AND IDROLE = 'resp');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENUATHFACT_CLIENT_LIST', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENUATHFACT_CLIENT_LIST' AND IDROLE = 'resp');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENUATHFACT_FACTURE', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENUATHFACT_FACTURE' AND IDROLE = 'resp');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENUATHFACT_FACTURE_ADD', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENUATHFACT_FACTURE_ADD' AND IDROLE = 'resp');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENUATHFACT_FACTURE_LIST', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENUATHFACT_FACTURE_LIST' AND IDROLE = 'resp');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENUATHFACT_PRODUIT', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENUATHFACT_PRODUIT' AND IDROLE = 'resp');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENUATHFACT_PRODUIT_ADD', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENUATHFACT_PRODUIT_ADD' AND IDROLE = 'resp');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENUATHFACT_PRODUIT_LIST', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENUATHFACT_PRODUIT_LIST' AND IDROLE = 'resp');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENUPOUSSON', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENUPOUSSON' AND IDROLE = 'resp');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENUPOUSSON_BC', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENUPOUSSON_BC' AND IDROLE = 'resp');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENUPOUSSON_BC_ADD', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENUPOUSSON_BC_ADD' AND IDROLE = 'resp');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENUPOUSSON_BC_LIST', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENUPOUSSON_BC_LIST' AND IDROLE = 'resp');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENUPOUSSON_BR', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENUPOUSSON_BR' AND IDROLE = 'resp');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENUPOUSSON_BR_ADD', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENUPOUSSON_BR_ADD' AND IDROLE = 'resp');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENUPOUSSON_BR_LIST', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENUPOUSSON_BR_LIST' AND IDROLE = 'resp');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENUPOUSSON_IC', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENUPOUSSON_IC' AND IDROLE = 'resp');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENUPOUSSON_IC_ADD', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENUPOUSSON_IC_ADD' AND IDROLE = 'resp');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENUPOUSSON_IC_LIST', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENUPOUSSON_IC_LIST' AND IDROLE = 'resp');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENUPOUSSON_SITUCOMP', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENUPOUSSON_SITUCOMP' AND IDROLE = 'resp');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENU_PREVISION_POUSSIN', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENU_PREVISION_POUSSIN' AND IDROLE = 'resp');

COMMIT;

-- ============================================================================
-- 9. VERIFICATION
-- ============================================================================
PROMPT >> Objets du module invalides (doit etre vide)
SELECT object_type, object_name
FROM   user_objects
WHERE  status <> 'VALID'
  AND  (object_name LIKE 'ATH\_%' ESCAPE '\' OR object_name LIKE 'V\_CAL\_%' ESCAPE '\'
        OR object_name IN ('CALENDRIER_POUSSIN_RAW', 'CALENDRIER_POUSSIN_CUMUL', 'SITUATION_COMPARTIMENT',
                           'LOT_LIB', 'BONCOMMANDE_POUSSIN_LIB', 'BONCOMMANDE_POUSSIN_FILLE_LIB',
                           'BONRECEPTION_POUSSIN_LIB', 'INSERTION_COMPARTIMENT_LIB', 'INSERTION_COMP_FILLE_LIB'));

PROMPT >> Nombre d'objets crees
SELECT object_type, COUNT(*) AS nb
FROM   user_objects
WHERE  object_name LIKE 'ATH\_%' ESCAPE '\' OR object_name LIKE 'V\_CAL\_%' ESCAPE '\'
       OR object_name LIKE 'SEQATH%' OR object_name LIKE 'GETSEQATH%'
       OR object_name IN ('CALENDRIER_POUSSIN_RAW', 'CALENDRIER_POUSSIN_CUMUL', 'SITUATION_COMPARTIMENT',
                          'LOT', 'LOT_LIB', 'DECL_PERTE_LOT', 'CONFIG_PREVISION', 'COMPARTIMENT', 'SANTEPOUSSIN', 'LIEURECEPTION',
                          'BONCOMMANDE_POUSSIN', 'BONCOMMANDE_POUSSIN_FILLE', 'BONRECEPTION_POUSSIN',
                          'INSERTION_COMPARTIMENT', 'INSERTION_COMPARTIMENT_FILLE',
                          'BONCOMMANDE_POUSSIN_LIB', 'BONCOMMANDE_POUSSIN_FILLE_LIB', 'BONRECEPTION_POUSSIN_LIB',
                          'INSERTION_COMPARTIMENT_LIB', 'INSERTION_COMP_FILLE_LIB', 'COMPARTIMENT_LIB',
                          'SANTEPOUSSIN_LIB', 'LIEURECEPTION_LIB')
GROUP  BY object_type
ORDER  BY object_type;

PROMPT >> Menus installes
SELECT COUNT(*) AS nb_menus FROM MENUDYNAMIQUE WHERE id LIKE 'MENUPOUSSON%' OR id LIKE 'MENUATHFACT%' OR id = 'MENU_PREVISION_POUSSIN';

PROMPT >> Compte de connexion
SELECT loginuser, idrole FROM UTILISATEUR WHERE loginuser = 'resp';

PROMPT Installation terminee. Redemarrer WildFly, puis se connecter avec resp / Resp2026.

