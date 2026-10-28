SET DEFINE OFF
SET SQLBLANKLINES ON
SET SERVEROUTPUT ON

PROMPT ============================================================
PROMPT CREATE - Structures completes pour la prevision poussin
PROMPT ============================================================

-- ============================================================================
-- 1. Enrichir SOUCHE (sans TYPE - non utilise)
-- ============================================================================
PROMPT >> Enrichissement de SOUCHE

BEGIN EXECUTE IMMEDIATE 'ALTER TABLE SOUCHE ADD (NB_JOURS_ECLOSION NUMBER(5) DEFAULT 21)';
EXCEPTION WHEN OTHERS THEN IF SQLCODE = -1430 THEN NULL; ELSE RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'ALTER TABLE SOUCHE ADD (DUREE_VIE_SEMAINES NUMBER(5) DEFAULT 72)';
EXCEPTION WHEN OTHERS THEN IF SQLCODE = -1430 THEN NULL; ELSE RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'ALTER TABLE SOUCHE ADD (SEMAINE_DEBUT_PONTE NUMBER(5) DEFAULT 21)';
EXCEPTION WHEN OTHERS THEN IF SQLCODE = -1430 THEN NULL; ELSE RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'ALTER TABLE SOUCHE ADD (TAUX_ECLOSION NUMBER(5,2) DEFAULT 85)';
EXCEPTION WHEN OTHERS THEN IF SQLCODE = -1430 THEN NULL; ELSE RAISE; END IF; END;
/
PROMPT SOUCHE enrichie : OK

-- ============================================================================
-- 2. DATE_ECLOSION dans BONRECEPTION_POUSSIN (saisie utilisateur)
-- ============================================================================
PROMPT >> Enrichissement de BONRECEPTION_POUSSIN

BEGIN EXECUTE IMMEDIATE 'ALTER TABLE BONRECEPTION_POUSSIN ADD (DATE_ECLOSION DATE)';
EXCEPTION WHEN OTHERS THEN IF SQLCODE = -1430 THEN NULL; ELSE RAISE; END IF; END;
/
PROMPT BONRECEPTION_POUSSIN.DATE_ECLOSION : OK

-- ============================================================================
-- 3. DATE_ECLOSION dans LOT (copiee depuis BR par le code Java)
-- ============================================================================
PROMPT >> Enrichissement de LOT

BEGIN EXECUTE IMMEDIATE 'ALTER TABLE LOT ADD (DATE_ECLOSION DATE)';
EXCEPTION WHEN OTHERS THEN IF SQLCODE = -1430 THEN NULL; ELSE RAISE; END IF; END;
/
PROMPT LOT.DATE_ECLOSION : OK

-- ============================================================================
-- 4. CONFIG_PREVISION
-- ============================================================================
PROMPT >> Creation de CONFIG_PREVISION

BEGIN EXECUTE IMMEDIATE 'DROP TABLE CONFIG_PREVISION CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL; END;
/

CREATE TABLE CONFIG_PREVISION (
    ID        VARCHAR2(30)  PRIMARY KEY,
    IDSOUCHE  VARCHAR2(50)  NOT NULL,
    SEMAINE   NUMBER(10)    NOT NULL,
    NB_JOUR   NUMBER(15,2)  DEFAULT 0,
    CONSTRAINT fk_cfgprev_souche    FOREIGN KEY (IDSOUCHE) REFERENCES SOUCHE(ID),
    CONSTRAINT ck_cfgprev_nb        CHECK (NB_JOUR >= 0),
    CONSTRAINT uq_cfgprev_souche_sem UNIQUE (IDSOUCHE, SEMAINE)
);

BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_CONFIG_PREVISION'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
CREATE SEQUENCE SEQ_CONFIG_PREVISION START WITH 1;

CREATE OR REPLACE FUNCTION GETSEQCONFIG_PREVISION RETURN NUMBER IS
    v NUMBER;
BEGIN
    SELECT SEQ_CONFIG_PREVISION.NEXTVAL INTO v FROM DUAL;
    RETURN v;
END;
/
PROMPT CONFIG_PREVISION : OK

-- ============================================================================
-- 5. DECL_PERTE_LOT (avec SEXE)
-- ============================================================================
PROMPT >> Creation de DECL_PERTE_LOT

BEGIN EXECUTE IMMEDIATE 'DROP TABLE DECL_PERTE_LOT CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL; END;
/

CREATE TABLE DECL_PERTE_LOT (
    ID          VARCHAR2(30)  PRIMARY KEY,
    IDLOT       VARCHAR2(30)  NOT NULL,
    SEXE        VARCHAR2(10),
    QTE_MATY    NUMBER(15,2)  DEFAULT 0,
    QTE_VERY    NUMBER(15,2)  DEFAULT 0,
    DATY        DATE,
    REFERENCE   VARCHAR2(100),
    REMARQUE    VARCHAR2(500),
    CONSTRAINT fk_dpl_lot    FOREIGN KEY (IDLOT) REFERENCES LOT(ID),
    CONSTRAINT fk_dpl_sexe   FOREIGN KEY (SEXE)  REFERENCES SEXE(ID),
    CONSTRAINT ck_dpl_qte    CHECK (QTE_MATY >= 0 AND QTE_VERY >= 0)
);

CREATE INDEX idx_dpl_lot  ON DECL_PERTE_LOT (IDLOT);
CREATE INDEX idx_dpl_daty ON DECL_PERTE_LOT (DATY);
CREATE INDEX idx_dpl_sexe ON DECL_PERTE_LOT (SEXE);

BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_DECL_PERTE_LOT'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
CREATE SEQUENCE SEQ_DECL_PERTE_LOT START WITH 1;

CREATE OR REPLACE FUNCTION GETSEQDECL_PERTE_LOT RETURN NUMBER IS
    v NUMBER;
BEGIN
    SELECT SEQ_DECL_PERTE_LOT.NEXTVAL INTO v FROM DUAL;
    RETURN v;
END;
/
PROMPT DECL_PERTE_LOT : OK

-- ============================================================================
-- 6. Vue BONRECEPTION_POUSSIN_LIB (avec DATE_ECLOSION exposee)
-- ============================================================================
PROMPT >> Recreation de la vue BONRECEPTION_POUSSIN_LIB

CREATE OR REPLACE VIEW BONRECEPTION_POUSSIN_LIB AS
SELECT brp.ID,
       brp.DATY,
       brp.REFERENCE,
       brp.IDBONCOMMANDE,
       brp.IDBONCOMMANDEFILLE,
       brp.IDBRPRECEDENT,
       brp.IDLIEURECEPTION,
       brp.IDMAGASIN,
       brp.ETAT,
       brp.QTAVANTCUMULE,
       brp.QTAPRESCUMULE,
       brp.MATY,
       brp.VERY,
       brp.REMARQUE,
       brp.DATE_ECLOSION,                              -- <-- NOUVEAU
       bc.REFERENCE      AS IDBONCOMMANDELIB,
       bcf.IDPRODUIT     AS IDBONCOMMANDEFILLEPRODUIT,
       brq.REFERENCE     AS IDBRPRECEDENTLIB,
       lr.VAL            AS IDLIEURECEPTIONLIB,
       m.VAL             AS IDMAGASINLIB,
       i.LIBELLE         AS PRODUITLIB,
       s.VAL             AS SOUCHELIBR,
       CASE brp.ETAT
            WHEN 0  THEN 'Annule'
            WHEN 1  THEN 'Cree'
            WHEN 11 THEN 'Vise'
            WHEN 9  THEN 'Cloture'
            ELSE 'Autre'
       END AS ETATLIB
FROM BONRECEPTION_POUSSIN brp
LEFT JOIN BONCOMMANDE_POUSSIN        bc  ON bc.ID  = brp.IDBONCOMMANDE
LEFT JOIN BONCOMMANDE_POUSSIN_FILLE  bcf ON bcf.ID = brp.IDBONCOMMANDEFILLE
LEFT JOIN BONRECEPTION_POUSSIN       brq ON brq.ID = brp.IDBRPRECEDENT
LEFT JOIN LIEURECEPTION              lr  ON lr.ID  = brp.IDLIEURECEPTION
LEFT JOIN MAGASIN2                   m   ON m.ID   = brp.IDMAGASIN
LEFT JOIN AS_INGREDIENTS             i   ON i.ID   = bcf.IDPRODUIT
LEFT JOIN SOUCHE                     s   ON s.ID   = bcf.IDSOUCHE;
/
PROMPT Vue BONRECEPTION_POUSSIN_LIB : OK

-- ============================================================================
-- 7. Vue LOT_LIB (si elle existe, on l'enrichit aussi)
-- ============================================================================
PROMPT >> Recreation de la vue LOT_LIB (avec DATE_ECLOSION)

BEGIN
    EXECUTE IMMEDIATE 'DROP VIEW LOT_LIB';
EXCEPTION WHEN OTHERS THEN NULL; END;
/

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
       l.DATE_ECLOSION,                                -- <-- NOUVEAU
       brp.REFERENCE  AS IDBONRECEPTIONLIB,
       bc.REFERENCE   AS IDBONCOMMANDELIB,
       i.LIBELLE      AS PRODUITLIB,
       s.VAL          AS SOUCHELIBR,
       sp.VAL         AS SANTELIB,
       sx.VAL         AS SEXELIB,
       c.NOM          AS COMPARTIMENTLIB,
       (l.QTEINIT * l.PU) AS VALEURTOTALE,
       CASE
            WHEN l.QTEINIT > 0 AND l.QTEACTUELLE > 0
            THEN l.PU * (l.QTEINIT / l.QTEACTUELLE)
            ELSE 0
       END AS VALEURUNITAIRE
FROM LOT l
LEFT JOIN BONRECEPTION_POUSSIN       brp ON brp.ID = l.IDBONRECEPTION
LEFT JOIN BONCOMMANDE_POUSSIN        bc  ON bc.ID  = l.IDBONCOMMANDE
LEFT JOIN AS_INGREDIENTS             i   ON i.ID   = l.IDPRODUIT
LEFT JOIN SOUCHE                     s   ON s.ID   = l.IDSOUCHE
LEFT JOIN SANTEPOUSSIN               sp  ON sp.ID  = l.IDSANTE
LEFT JOIN SEXE                       sx  ON sx.ID  = l.SEXE
LEFT JOIN INSERTION_COMPARTIMENT_FILLE icf ON icf.ID = l.IDINSERTCOMPFILLE
LEFT JOIN COMPARTIMENT               c   ON c.ID   = icf.IDCOMPARTIMENT;
/
PROMPT Vue LOT_LIB : OK

-- ============================================================================
-- 8. Vue CALENDRIER_POUSSIN_RAW
-- ============================================================================
PROMPT >> Creation de la vue CALENDRIER_POUSSIN_RAW

CREATE OR REPLACE VIEW CALENDRIER_POUSSIN_RAW AS
WITH date_bounds AS (
    SELECT MIN(TRUNC(l.date_eclosion))               AS d_min,
           MAX(TRUNC(l.date_eclosion)) + 1000        AS d_max
    FROM   LOT l
    WHERE  l.sexe = '0' AND l.date_eclosion IS NOT NULL
),
days AS (
    SELECT d_min + LEVEL - 1 AS daty
    FROM   date_bounds
    CONNECT BY LEVEL <= d_max - d_min + 1
),
lot_info AS (
    SELECT l.id                                AS idlot,
           l.idsouche,
           l.qteinit,
           TRUNC(l.date_eclosion)              AS date_eclosion,
           NVL(s.nb_jours_eclosion, 21)        AS nb_jours_eclosion,
           NVL(s.semaine_debut_ponte, 21)      AS semaine_debut_ponte,
           NVL(s.taux_eclosion, 85)            AS taux_eclosion
    FROM   LOT l
           LEFT JOIN SOUCHE s ON s.id = l.idsouche
    WHERE  l.sexe = '0' AND l.date_eclosion IS NOT NULL
),
daily_poussins AS (
    SELECT d.daty,
           SUM(
             CASE
               WHEN d.daty - li.nb_jours_eclosion >= li.date_eclosion
                 AND FLOOR((d.daty - li.nb_jours_eclosion - li.date_eclosion) / 7) + 1
                     >= li.semaine_debut_ponte
               THEN GREATEST(0,
                        li.qteinit - NVL((
                            SELECT SUM(NVL(p.qte_maty,0) + NVL(p.qte_very,0))
                            FROM   DECL_PERTE_LOT p
                            WHERE  p.idlot = li.idlot
                              AND  p.sexe  = '0'
                              AND  TRUNC(p.daty) <= d.daty - li.nb_jours_eclosion
                        ), 0))
                  * NVL((
                        SELECT cp.nb_jour
                        FROM   CONFIG_PREVISION cp
                        WHERE  cp.idsouche = li.idsouche
                          AND  cp.semaine  = FLOOR((d.daty - li.nb_jours_eclosion
                                                    - li.date_eclosion) / 7) + 1
                     ), 0)
                  * li.taux_eclosion / 100
               ELSE 0
             END
           ) AS poussins
    FROM   days d CROSS JOIN lot_info li
    GROUP  BY d.daty
),
daily_pertes AS (
    SELECT TRUNC(daty) AS daty,
           SUM(NVL(qte_maty,0) + NVL(qte_very,0)) AS pertes
    FROM   DECL_PERTE_LOT
    WHERE  daty IS NOT NULL
    GROUP  BY TRUNC(daty)
)
SELECT d.daty,
       NVL(dp.poussins, 0) AS poussins,
       NVL(dpt.pertes, 0)  AS pertes
FROM   days d
       LEFT JOIN daily_poussins dp  ON dp.daty  = d.daty
       LEFT JOIN daily_pertes   dpt ON dpt.daty = d.daty;
/
PROMPT Vue CALENDRIER_POUSSIN_RAW : OK

-- ============================================================================
-- 9. Backfill : recopier DATE_ECLOSION depuis BR vers LOT existants
-- ============================================================================
PROMPT >> Backfill des donnees existantes

UPDATE LOT l
SET l.date_eclosion = (
    SELECT br.date_eclosion
    FROM   BONRECEPTION_POUSSIN br
    WHERE  br.id = l.idbonreception
)
WHERE l.idbonreception IS NOT NULL
  AND l.date_eclosion IS NULL;

COMMIT;
PROMPT Backfill termine.

-- ============================================================================
-- 10. VERIFICATIONS
-- ============================================================================
PROMPT ============================================================
PROMPT VERIFICATION
PROMPT ============================================================

PROMPT >> Structure SOUCHE (sans TYPE)
SELECT column_name, data_type
FROM   user_tab_columns
WHERE  table_name = 'SOUCHE'
ORDER BY column_id;

PROMPT >> Structure BONRECEPTION_POUSSIN (avec DATE_ECLOSION)
SELECT column_name, data_type
FROM   user_tab_columns
WHERE  table_name = 'BONRECEPTION_POUSSIN'
ORDER BY column_id;

PROMPT >> Structure LOT (avec DATE_ECLOSION)
SELECT column_name, data_type
FROM   user_tab_columns
WHERE  table_name = 'LOT'
ORDER BY column_id;

PROMPT >> Structure DECL_PERTE_LOT (avec SEXE)
SELECT column_name, data_type
FROM   user_tab_columns
WHERE  table_name = 'DECL_PERTE_LOT'
ORDER BY column_id;

PROMPT >> Objets crees (doivent tous etre VALID)
SELECT object_name, object_type, status
FROM   user_objects
WHERE  object_name IN (
    'CONFIG_PREVISION', 'SEQ_CONFIG_PREVISION', 'GETSEQCONFIG_PREVISION',
    'DECL_PERTE_LOT',   'SEQ_DECL_PERTE_LOT',   'GETSEQDECL_PERTE_LOT',
    'BONRECEPTION_POUSSIN_LIB', 'LOT_LIB', 'CALENDRIER_POUSSIN_RAW'
)
ORDER BY object_type, object_name;

PROMPT >> Pas de trigger (doit etre vide)
SELECT trigger_name FROM user_triggers WHERE table_name = 'LOT';

PROMPT >> Erreurs de compilation des vues ? (doit etre vide)
SELECT name, type, line, position, text
FROM   user_errors
WHERE  name IN ('BONRECEPTION_POUSSIN_LIB', 'LOT_LIB', 'CALENDRIER_POUSSIN_RAW');

PROMPT >> Exemple : BR avec date_eclosion
SELECT id, TO_CHAR(daty,'DD/MM/YYYY') daty,
       TO_CHAR(date_eclosion,'DD/MM/YYYY') date_eclosion
FROM   BONRECEPTION_POUSSIN
WHERE  ROWNUM <= 3;

PROMPT ============================================================
PROMPT CREATE termine
PROMPT ============================================================