SET DEFINE OFF
SET SERVEROUTPUT ON
SET LINESIZE 200

-- ============================================================
-- Compatible Oracle 11g (pas de LATERAL, pas de FETCH FIRST)
-- Ordre d'execution : 0 (index) -> 1 a 5 (vues) -> 6 (verification)
-- Apres chaque CREATE, SHOW ERRORS affiche l'erreur exacte s'il y en a une.
-- ============================================================

PROMPT === 0. Index (accelere les sous-requetes de pertes et la config) ===
-- Si un index existe deja, Oracle renvoie ORA-00955 / ORA-01408 : ignorer.
CREATE INDEX IDX_DPL_LOT_SEXE_DATY ON DECL_PERTE_LOT (IDLOT, SEXE, DATY);
CREATE INDEX IDX_CFGPREV_SOUCHE_SEM ON CONFIG_PREVISION (IDSOUCHE, SEMAINE);

PROMPT === 1. V_CAL_JOURS : calendrier des jours ===
CREATE OR REPLACE VIEW V_CAL_JOURS AS
SELECT b.d_min + LEVEL - 1 AS daty
FROM   (SELECT MIN(TRUNC(dateeclosion))        AS d_min,
               MAX(TRUNC(dateeclosion)) + 1000 AS d_max
        FROM   LOT
        WHERE  dateeclosion IS NOT NULL) b
CONNECT BY LEVEL <= b.d_max - b.d_min + 1;
SHOW ERRORS VIEW V_CAL_JOURS

PROMPT === 2. V_CAL_INITIAUX : poussins initiaux a leur date declosion ===
CREATE OR REPLACE VIEW V_CAL_INITIAUX AS
SELECT TRUNC(l.dateeclosion)                                   AS daty,
       SUM(l.qteinit)                                          AS total,
       SUM(CASE WHEN l.sexe = '0' THEN l.qteinit ELSE 0 END)   AS femelles,
       SUM(CASE WHEN l.sexe = '1' THEN l.qteinit ELSE 0 END)   AS males
FROM   LOT l
WHERE  l.dateeclosion IS NOT NULL
GROUP  BY TRUNC(l.dateeclosion);
SHOW ERRORS VIEW V_CAL_INITIAUX

PROMPT === 3. V_CAL_LOT_INFO : lots femelles + parametres de la souche ===
CREATE OR REPLACE VIEW V_CAL_LOT_INFO AS
SELECT l.id                                AS idlot,
       l.idsouche,
       l.qteinit,
       TRUNC(l.dateeclosion)               AS dateeclosion,
       NVL(s.nb_jours_eclosion, 21)        AS nb_jours_eclosion,
       NVL(s.semaine_debut_ponte, 21)      AS semaine_debut_ponte,
       NVL(s.taux_eclosion, 85)            AS taux_eclosion,
       NVL(s.ratio_femelle, 50)            AS ratio_femelle
FROM   LOT l
       LEFT JOIN SOUCHE s ON s.id = l.idsouche
WHERE  l.sexe = '0'
  AND  l.dateeclosion IS NOT NULL;
SHOW ERRORS VIEW V_CAL_LOT_INFO

PROMPT === 4. V_CAL_PONTE_JOUR : poussins issus des pontes, par jour ===
CREATE OR REPLACE VIEW V_CAL_PONTE_JOUR AS
WITH cfg AS (
    -- MAX evite ORA-01427 si doublons (idsouche, semaine)
    SELECT idsouche, semaine, MAX(nbjour) AS nbjour
    FROM   CONFIG_PREVISION
    GROUP  BY idsouche, semaine
),
ponte AS (
    SELECT d.daty,
           li.idlot, li.idsouche, li.qteinit,
           li.taux_eclosion, li.ratio_femelle,
           li.nb_jours_eclosion, li.semaine_debut_ponte,
           FLOOR((d.daty - li.nb_jours_eclosion - li.dateeclosion) / 7) + 1 AS sem
    FROM   V_CAL_JOURS d
           JOIN V_CAL_LOT_INFO li
             ON d.daty - li.nb_jours_eclosion >= li.dateeclosion
),
ponte_calc AS (
    SELECT p.daty,
           p.ratio_femelle,
           GREATEST(0, p.qteinit - NVL((
               SELECT SUM(NVL(pl.qtematy,0) + NVL(pl.qtevery,0))
               FROM   DECL_PERTE_LOT pl
               WHERE  pl.idlot = p.idlot
                 AND  pl.sexe  = '0'
                 AND  TRUNC(pl.daty) <= p.daty - p.nb_jours_eclosion
           ), 0))
           * NVL(c.nbjour, 0) * p.taux_eclosion / 100 AS nb
    FROM   ponte p
           JOIN cfg c ON c.idsouche = p.idsouche AND c.semaine = p.sem
    WHERE  p.sem >= p.semaine_debut_ponte
)
SELECT daty,
       SUM(nb)                       AS total,
       SUM(nb * ratio_femelle / 100) AS femelles
FROM   ponte_calc
GROUP  BY daty;
SHOW ERRORS VIEW V_CAL_PONTE_JOUR

PROMPT === 5. V_CAL_PERTES + vue finale CALENDRIER_POUSSIN_RAW ===
CREATE OR REPLACE VIEW V_CAL_PERTES AS
SELECT TRUNC(daty) AS daty,
       SUM(NVL(qtematy,0) + NVL(qtevery,0)) AS pertes
FROM   DECL_PERTE_LOT
WHERE  daty IS NOT NULL
GROUP  BY TRUNC(daty);
SHOW ERRORS VIEW V_CAL_PERTES

CREATE OR REPLACE VIEW CALENDRIER_POUSSIN_RAW AS
SELECT d.daty,
       NVL(i.total, 0)    + NVL(p.total, 0)                      AS poussins,
       NVL(i.males, 0)    + (NVL(p.total, 0) - NVL(p.femelles, 0)) AS poussinsmales,
       NVL(i.femelles, 0) + NVL(p.femelles, 0)                   AS poussinsfemelles,
       NVL(t.pertes, 0)                                          AS pertes
FROM   V_CAL_JOURS d
       LEFT JOIN V_CAL_INITIAUX   i ON i.daty = d.daty
       LEFT JOIN V_CAL_PONTE_JOUR p ON p.daty = d.daty
       LEFT JOIN V_CAL_PERTES     t ON t.daty = d.daty;
SHOW ERRORS VIEW CALENDRIER_POUSSIN_RAW

PROMPT === 6. VERIFICATION (une etape a la fois pour localiser un probleme) ===
PROMPT --- Nb de jours ---
SELECT COUNT(*) AS nb_jours, MIN(daty) AS premier, MAX(daty) AS dernier FROM V_CAL_JOURS;

PROMPT --- Nb de lots femelles ---
SELECT COUNT(*) AS nb_lots_femelles FROM V_CAL_LOT_INFO;

PROMPT --- Doublons dans CONFIG_PREVISION (doit etre vide) ---
SELECT idsouche, semaine, COUNT(*) FROM CONFIG_PREVISION
GROUP BY idsouche, semaine HAVING COUNT(*) > 1;

PROMPT --- Test 13/01/2026 : attendu 465 total (460 F + 5 M) ---
SELECT TO_CHAR(daty,'DD/MM/YYYY') AS jour, poussins, poussinsmales, poussinsfemelles
FROM   CALENDRIER_POUSSIN_RAW
WHERE  daty = TO_DATE('2026-01-13','YYYY-MM-DD');

PROMPT --- 10 premiers jours avec poussins > 0 ---
SELECT TO_CHAR(daty,'DD/MM/YYYY') AS jour, total, males, femelles, pertes
FROM (
    SELECT daty,
           ROUND(poussins)         AS total,
           ROUND(poussinsmales)    AS males,
           ROUND(poussinsfemelles) AS femelles,
           pertes
    FROM   CALENDRIER_POUSSIN_RAW
    WHERE  poussins > 0
    ORDER  BY daty
)
WHERE ROWNUM <= 10;

-- ============================================================
-- OPTIONNEL : si la vue reste trop lente, version "table + procedure".
-- A decommenter. Il faut appeler la procedure apres chaque changement
-- de LOT / DECL_PERTE_LOT / CONFIG_PREVISION, et lire CALENDRIER_POUSSIN_CACHE.
-- ============================================================
-- CREATE TABLE CALENDRIER_POUSSIN_CACHE AS
--     SELECT * FROM CALENDRIER_POUSSIN_RAW WHERE 1 = 0;
--
-- CREATE OR REPLACE PROCEDURE PR_REFRESH_CALENDRIER_POUSSIN AS
-- BEGIN
--     EXECUTE IMMEDIATE 'TRUNCATE TABLE CALENDRIER_POUSSIN_CACHE';
--     INSERT INTO CALENDRIER_POUSSIN_CACHE
--         SELECT * FROM CALENDRIER_POUSSIN_RAW;
--     COMMIT;
-- END;
-- /
-- SHOW ERRORS PROCEDURE PR_REFRESH_CALENDRIER_POUSSIN
-- EXEC PR_REFRESH_CALENDRIER_POUSSIN;