SET DEFINE OFF
SET SQLBLANKLINES ON
SET SERVEROUTPUT ON

PROMPT ============================================================
PROMPT FIX - Recreation des vues avec DATEECLOSION
PROMPT ============================================================

-- ============================================================================
-- 1. Drop des vues invalides
-- ============================================================================
PROMPT >> Drop des vues

BEGIN EXECUTE IMMEDIATE 'DROP VIEW BONRECEPTION_POUSSIN_LIB';
EXCEPTION WHEN OTHERS THEN NULL; END;
/
PROMPT BONRECEPTION_POUSSIN_LIB : drop OK

BEGIN EXECUTE IMMEDIATE 'DROP VIEW LOT_LIB';
EXCEPTION WHEN OTHERS THEN NULL; END;
/
PROMPT LOT_LIB : drop OK

BEGIN EXECUTE IMMEDIATE 'DROP VIEW CALENDRIER_POUSSIN_RAW';
EXCEPTION WHEN OTHERS THEN NULL; END;
/
PROMPT CALENDRIER_POUSSIN_RAW : drop OK

-- ============================================================================
-- 2. Recreation BONRECEPTION_POUSSIN_LIB
-- ============================================================================
PROMPT >> Recreation BONRECEPTION_POUSSIN_LIB

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
       brp.DATEECLOSION,
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
PROMPT BONRECEPTION_POUSSIN_LIB : OK

-- ============================================================================
-- 3. Recreation LOT_LIB
-- ============================================================================
PROMPT >> Recreation LOT_LIB

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
PROMPT LOT_LIB : OK

-- ============================================================================
-- 4. Recreation CALENDRIER_POUSSIN_RAW
-- ============================================================================
PROMPT >> Recreation CALENDRIER_POUSSIN_RAW

CREATE OR REPLACE VIEW CALENDRIER_POUSSIN_RAW AS
WITH date_bounds AS (
    SELECT MIN(TRUNC(l.dateeclosion))                AS d_min,
           MAX(TRUNC(l.dateeclosion)) + 1000         AS d_max
    FROM   LOT l
    WHERE  l.sexe = '0' AND l.dateeclosion IS NOT NULL
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
           TRUNC(l.dateeclosion)               AS dateeclosion,
           NVL(s.nb_jours_eclosion, 21)        AS nb_jours_eclosion,
           NVL(s.semaine_debut_ponte, 21)      AS semaine_debut_ponte,
           NVL(s.taux_eclosion, 85)            AS taux_eclosion
    FROM   LOT l
           LEFT JOIN SOUCHE s ON s.id = l.idsouche
    WHERE  l.sexe = '0' AND l.dateeclosion IS NOT NULL
),
daily_poussins AS (
    SELECT d.daty,
           SUM(
             CASE
               WHEN d.daty - li.nb_jours_eclosion >= li.dateeclosion
                 AND FLOOR((d.daty - li.nb_jours_eclosion - li.dateeclosion) / 7) + 1
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
                                                    - li.dateeclosion) / 7) + 1
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
PROMPT CALENDRIER_POUSSIN_RAW : OK

-- ============================================================================
-- 5. Verification
-- ============================================================================
PROMPT ============================================================
PROMPT VERIFICATION
PROMPT ============================================================

PROMPT >> Statut des vues
SELECT object_name, object_type, status
FROM   user_objects
WHERE  object_name IN ('BONRECEPTION_POUSSIN_LIB', 'LOT_LIB', 'CALENDRIER_POUSSIN_RAW')
ORDER BY object_name;

PROMPT >> Colonnes DATEECLOSION dans les vues
SELECT table_name, column_name
FROM   user_tab_columns
WHERE  table_name IN ('BONRECEPTION_POUSSIN', 'BONRECEPTION_POUSSIN_LIB',
                     'LOT', 'LOT_LIB')
  AND  column_name LIKE 'DATEECL%'
ORDER BY table_name, column_name;

PROMPT >> Erreurs de compilation (doit etre vide)
SELECT name, line, position, text
FROM   user_errors
WHERE  name IN ('BONRECEPTION_POUSSIN_LIB', 'LOT_LIB', 'CALENDRIER_POUSSIN_RAW');

PROMPT >> Test DESC
PROMPT LOT_LIB
DESC LOT_LIB

PROMPT BONRECEPTION_POUSSIN_LIB
DESC BONRECEPTION_POUSSIN_LIB

PROMPT ============================================================
PROMPT FIX termine
PROMPT ============================================================