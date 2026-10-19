SET DEFINE OFF
SET LINESIZE 200

-- ============================================================
-- Calendrier poussin multi-generations
-- Gen 0 : lots achetes
-- Gen 1 : poussins femelles issus de la ponte Gen 0
-- Gen 2 : poussins femelles issus de la ponte Gen 1 (optionnel)
-- ============================================================

PROMPT === V_CAL_JOURS ===
CREATE OR REPLACE VIEW V_CAL_JOURS AS
SELECT b.d_min + LEVEL - 1 AS daty
FROM   (SELECT MIN(TRUNC(dateeclosion))        AS d_min,
               MAX(TRUNC(dateeclosion)) + 2000 AS d_max
        FROM   LOT
        WHERE  dateeclosion IS NOT NULL) b
CONNECT BY LEVEL <= b.d_max - b.d_min + 1;
SHOW ERRORS VIEW V_CAL_JOURS

PROMPT === V_CAL_LOT_PU ===
CREATE OR REPLACE VIEW V_CAL_LOT_PU AS
SELECT l.id AS idlot,
       NVL(bf.pu, NVL(l.pu, 0)) AS pu
FROM   LOT l
       LEFT JOIN (SELECT idmere, idsouche,
                         SUM(NVL(qte,0) * NVL(pu,0)) / NULLIF(SUM(NVL(qte,0)), 0) AS pu
                  FROM   BONCOMMANDE_POUSSIN_FILLE
                  GROUP  BY idmere, idsouche) bf
              ON bf.idmere   = l.idboncommande
             AND bf.idsouche = l.idsouche;
SHOW ERRORS VIEW V_CAL_LOT_PU

PROMPT === V_CAL_INITIAUX ===
CREATE OR REPLACE VIEW V_CAL_INITIAUX AS
SELECT TRUNC(l.dateeclosion)                                   AS daty,
       SUM(l.qteinit)                                          AS total,
       SUM(CASE WHEN l.sexe = '0' THEN l.qteinit ELSE 0 END)   AS femelles,
       SUM(CASE WHEN l.sexe = '1' THEN l.qteinit ELSE 0 END)   AS males,
       SUM(l.qteinit * pu.pu)                                  AS cout
FROM   LOT l
       JOIN V_CAL_LOT_PU pu ON pu.idlot = l.id
WHERE  l.dateeclosion IS NOT NULL
GROUP  BY TRUNC(l.dateeclosion);
SHOW ERRORS VIEW V_CAL_INITIAUX

PROMPT === V_CAL_LOT_INFO ===
CREATE OR REPLACE VIEW V_CAL_LOT_INFO AS
SELECT x.idlot, x.idsouche, x.idcompartiment, x.qteinit,
       x.dateeclosion, x.nb_jours_eclosion, x.semaine_debut_ponte,
       x.taux_eclosion, x.ratio_femelle, x.pu,
       NVL(x.qteinit / NULLIF(SUM(x.qteinit) OVER (PARTITION BY x.idcompartiment), 0), 0) AS part_comp
FROM  (SELECT l.id                            AS idlot,
              l.idsouche,
              icf.idcompartiment,
              l.qteinit,
              TRUNC(l.dateeclosion)           AS dateeclosion,
              NVL(s.nb_jours_eclosion, 21)    AS nb_jours_eclosion,
              NVL(s.semaine_debut_ponte, 21)  AS semaine_debut_ponte,
              NVL(s.taux_eclosion, 85)        AS taux_eclosion,
              NVL(s.ratio_femelle, 50)        AS ratio_femelle,
              lp.pu
       FROM   LOT l
              LEFT JOIN SOUCHE s                       ON s.id   = l.idsouche
              LEFT JOIN INSERTION_COMPARTIMENT_FILLE icf ON icf.id = l.idinsertcompfille
              LEFT JOIN V_CAL_LOT_PU lp                ON lp.idlot = l.id
       WHERE  l.sexe = '0' AND l.dateeclosion IS NOT NULL) x;
SHOW ERRORS VIEW V_CAL_LOT_INFO

-- ============================================================
-- GEN 0 : ponte des lots initiaux
-- ============================================================
PROMPT === V_CAL_PONTE_JOUR_DETAIL (Gen 0 par SOUCHE) ===
CREATE OR REPLACE VIEW V_CAL_PONTE_JOUR_DETAIL AS
WITH cfg AS (
    SELECT idsouche, semaine, MAX(nbjour) AS nbjour
    FROM   CONFIG_PREVISION
    GROUP  BY idsouche, semaine
),
ponte AS (
    SELECT d.daty,
           li.idsouche, li.idcompartiment, li.qteinit, li.part_comp, li.pu,
           li.taux_eclosion, li.ratio_femelle,
           li.nb_jours_eclosion, li.semaine_debut_ponte,
           FLOOR((d.daty - li.nb_jours_eclosion - li.dateeclosion) / 7) AS sem
    FROM   V_CAL_JOURS d
           JOIN V_CAL_LOT_INFO li
             ON d.daty - li.nb_jours_eclosion >= li.dateeclosion
),
ponte_calc AS (
    SELECT p.daty, p.idsouche, p.ratio_femelle, p.taux_eclosion, p.pu,
           GREATEST(0, p.qteinit - p.part_comp * NVL((
               SELECT SUM(NVL(pl.qtemort,0) + NVL(pl.qtevery,0))
               FROM   DECL_PERTE_LOT pl
               WHERE  pl.idcompartiment = p.idcompartiment
                 AND  pl.sexe = '0'
                 AND  TRUNC(pl.daty) <= p.daty - p.nb_jours_eclosion
           ), 0))
           * NVL(c.nbjour, 0) * p.taux_eclosion / 100 AS nb
    FROM   ponte p
           JOIN cfg c ON c.idsouche = p.idsouche AND c.semaine = p.sem
    WHERE  p.sem >= p.semaine_debut_ponte
)
SELECT daty, idsouche,
       MAX(ratio_femelle) AS ratio_femelle,
       MAX(taux_eclosion) AS taux_eclosion,
       MAX(pu)            AS pu,
       SUM(nb)                                    AS total,
       SUM(nb * ratio_femelle / 100)              AS femelles
FROM   ponte_calc
GROUP  BY daty, idsouche;
SHOW ERRORS VIEW V_CAL_PONTE_JOUR_DETAIL

PROMPT === V_CAL_PONTE_JOUR (Gen 0 agrege) ===
CREATE OR REPLACE VIEW V_CAL_PONTE_JOUR AS
SELECT daty,
       SUM(total)                AS total,
       SUM(femelles)             AS femelles,
       SUM(total * pu)           AS ca
FROM   V_CAL_PONTE_JOUR_DETAIL
GROUP  BY daty;
SHOW ERRORS VIEW V_CAL_PONTE_JOUR

-- ============================================================
-- GEN 1 : ponte des poussins femelles issus de Gen 0
-- ============================================================
PROMPT === V_CAL_GEN1_HENS ===
CREATE OR REPLACE VIEW V_CAL_GEN1_HENS AS
SELECT daty AS birth_day, idsouche, femelles AS nb_females
FROM   V_CAL_PONTE_JOUR_DETAIL
WHERE  femelles > 0;
SHOW ERRORS VIEW V_CAL_GEN1_HENS

PROMPT === V_CAL_GEN1_PONTE_DETAIL ===
CREATE OR REPLACE VIEW V_CAL_GEN1_PONTE_DETAIL AS
WITH souche_p AS (
    SELECT id AS idsouche,
           NVL(nb_jours_eclosion, 21)   AS nb_jours_eclosion,
           NVL(semaine_debut_ponte, 21) AS semaine_debut_ponte,
           NVL(taux_eclosion, 85)       AS taux_eclosion,
           NVL(ratio_femelle, 50)       AS ratio_femelle,
           NVL(duree_vie_semaines, 72)  AS duree_vie_semaines
    FROM   SOUCHE
),
cfg AS (
    SELECT idsouche, semaine, MAX(nbjour) AS nbjour
    FROM   CONFIG_PREVISION
    GROUP  BY idsouche, semaine
),
gen1_calc AS (
    SELECT d.daty,
           gh.idsouche,
           gh.nb_females,
           sp.ratio_femelle,
           sp.taux_eclosion,
           FLOOR((d.daty - sp.nb_jours_eclosion - gh.birth_day) / 7) AS sem,
           sp.semaine_debut_ponte,
           sp.duree_vie_semaines,
           NVL(c.nbjour, 0) AS nbjour,
           NVL(gh.nb_females * NVL(c.nbjour, 0) * sp.taux_eclosion / 100, 0) AS nb
    FROM   V_CAL_JOURS d
           CROSS JOIN V_CAL_GEN1_HENS gh
           JOIN souche_p sp ON sp.idsouche = gh.idsouche
           LEFT JOIN cfg c ON c.idsouche = gh.idsouche
                          AND c.semaine = FLOOR((d.daty - sp.nb_jours_eclosion - gh.birth_day) / 7) + 1
    WHERE  d.daty - sp.nb_jours_eclosion >= gh.birth_day
)
SELECT daty, idsouche,
       SUM(CASE WHEN sem >= semaine_debut_ponte AND sem <= duree_vie_semaines THEN nb ELSE 0 END) AS total,
       SUM(CASE WHEN sem >= semaine_debut_ponte AND sem <= duree_vie_semaines THEN nb * ratio_femelle / 100 ELSE 0 END) AS femelles
FROM   gen1_calc
GROUP  BY daty, idsouche;
SHOW ERRORS VIEW V_CAL_GEN1_PONTE_DETAIL

PROMPT === V_CAL_GEN1_PONTE (agrege) ===
CREATE OR REPLACE VIEW V_CAL_GEN1_PONTE AS
SELECT daty, SUM(total) AS total, SUM(femelles) AS femelles
FROM   V_CAL_GEN1_PONTE_DETAIL
GROUP  BY daty;
SHOW ERRORS VIEW V_CAL_GEN1_PONTE

-- ============================================================
-- GEN 2 : ponte des poussins femelles issus de Gen 1
-- ============================================================
PROMPT === V_CAL_GEN2_HENS ===
CREATE OR REPLACE VIEW V_CAL_GEN2_HENS AS
SELECT daty AS birth_day, idsouche, femelles AS nb_females
FROM   V_CAL_GEN1_PONTE_DETAIL
WHERE  femelles > 0;
SHOW ERRORS VIEW V_CAL_GEN2_HENS

PROMPT === V_CAL_GEN2_PONTE ===
CREATE OR REPLACE VIEW V_CAL_GEN2_PONTE AS
WITH souche_p AS (
    SELECT id AS idsouche,
           NVL(nb_jours_eclosion, 21)   AS nb_jours_eclosion,
           NVL(semaine_debut_ponte, 21) AS semaine_debut_ponte,
           NVL(taux_eclosion, 85)       AS taux_eclosion,
           NVL(ratio_femelle, 50)       AS ratio_femelle,
           NVL(duree_vie_semaines, 72)  AS duree_vie_semaines
    FROM   SOUCHE
),
cfg AS (
    SELECT idsouche, semaine, MAX(nbjour) AS nbjour
    FROM   CONFIG_PREVISION
    GROUP  BY idsouche, semaine
),
gen2_calc AS (
    SELECT d.daty,
           gh.nb_females,
           sp.ratio_femelle,
           sp.taux_eclosion,
           FLOOR((d.daty - sp.nb_jours_eclosion - gh.birth_day) / 7) AS sem,
           sp.semaine_debut_ponte,
           sp.duree_vie_semaines,
           NVL(c.nbjour, 0) AS nbjour
    FROM   V_CAL_JOURS d
           CROSS JOIN V_CAL_GEN2_HENS gh
           JOIN souche_p sp ON sp.idsouche = gh.idsouche
           LEFT JOIN cfg c ON c.idsouche = gh.idsouche
                          AND c.semaine = FLOOR((d.daty - sp.nb_jours_eclosion - gh.birth_day) / 7) + 1
    WHERE  d.daty - sp.nb_jours_eclosion >= gh.birth_day
)
SELECT daty,
       SUM(CASE WHEN sem >= semaine_debut_ponte AND sem <= duree_vie_semaines
                THEN nb_females * nbjour * taux_eclosion / 100 ELSE 0 END) AS total,
       SUM(CASE WHEN sem >= semaine_debut_ponte AND sem <= duree_vie_semaines
                THEN nb_females * nbjour * taux_eclosion / 100 * ratio_femelle / 100 ELSE 0 END) AS femelles
FROM   gen2_calc
GROUP  BY daty;
SHOW ERRORS VIEW V_CAL_GEN2_PONTE

-- ============================================================
-- PERTES par jour (avec sexe)
-- ============================================================
PROMPT === V_CAL_PERTES ===
CREATE OR REPLACE VIEW V_CAL_PERTES AS
SELECT TRUNC(daty) AS daty,
       SUM(NVL(qtemort,0) + NVL(qtevery,0))                                      AS pertes,
       SUM(CASE WHEN sexe = '0' THEN NVL(qtemort,0) + NVL(qtevery,0) ELSE 0 END) AS pertesfemelles,
       SUM(CASE WHEN sexe = '1' THEN NVL(qtemort,0) + NVL(qtevery,0) ELSE 0 END) AS pertesmales
FROM   DECL_PERTE_LOT
WHERE  daty IS NOT NULL
GROUP  BY TRUNC(daty);
SHOW ERRORS VIEW V_CAL_PERTES

-- ============================================================
-- COUT DES PERTES par jour (agrege pour eviter cartesian)
-- ============================================================
PROMPT === V_CAL_PERTES_COUT ===
CREATE OR REPLACE VIEW V_CAL_PERTES_COUT AS
WITH pu_comp AS (
    SELECT icf.idcompartiment,
           SUM(NVL(l.qteinit,0) * NVL(icf.pu,0)) / NULLIF(SUM(NVL(l.qteinit,0)), 0) AS pu_moyen
    FROM   INSERTION_COMPARTIMENT_FILLE icf
           LEFT JOIN LOT l ON l.idinsertcompfille = icf.id
    GROUP  BY icf.idcompartiment
)
SELECT TRUNC(dp.daty) AS daty,
       SUM((NVL(dp.qtemort,0) + NVL(dp.qtevery,0)) * NVL(pc.pu_moyen, 0)) AS cout
FROM   DECL_PERTE_LOT dp
       LEFT JOIN pu_comp pc ON pc.idcompartiment = dp.idcompartiment
WHERE  dp.daty IS NOT NULL
GROUP  BY TRUNC(dp.daty);
SHOW ERRORS VIEW V_CAL_PERTES_COUT

-- ============================================================
-- CALENDRIER_POUSSIN_RAW : total des 3 generations
-- ============================================================
PROMPT === CALENDRIER_POUSSIN_RAW ===
CREATE OR REPLACE VIEW CALENDRIER_POUSSIN_RAW AS
SELECT d.daty,
       NVL(i.total, 0) + NVL(p.total, 0) + NVL(p1.total, 0) + NVL(p2.total, 0) AS poussins,
       NVL(i.males, 0)
       + (NVL(p.total, 0)  - NVL(p.femelles, 0))
       + (NVL(p1.total, 0) - NVL(p1.femelles, 0))
       + (NVL(p2.total, 0) - NVL(p2.femelles, 0))                              AS poussinsmales,
       NVL(i.femelles, 0) + NVL(p.femelles, 0) + NVL(p1.femelles, 0) + NVL(p2.femelles, 0) AS poussinsfemelles,
       NVL(t.pertes, 0)          AS pertes,
       NVL(t.pertesmales, 0)     AS pertesmales,
       NVL(t.pertesfemelles, 0)  AS pertesfemelles,
       NVL(p.ca, 0)              AS ca,
       NVL(cp.cout, 0)           AS cout,
       NVL(p.ca, 0) - NVL(cp.cout, 0) AS benefice
FROM   V_CAL_JOURS       d
       LEFT JOIN V_CAL_INITIAUX    i  ON i.daty  = d.daty
       LEFT JOIN V_CAL_PONTE_JOUR  p  ON p.daty  = d.daty
       LEFT JOIN V_CAL_GEN1_PONTE  p1 ON p1.daty = d.daty
       LEFT JOIN V_CAL_GEN2_PONTE  p2 ON p2.daty = d.daty
       LEFT JOIN V_CAL_PERTES      t  ON t.daty  = d.daty
       LEFT JOIN V_CAL_PERTES_COUT cp ON cp.daty = d.daty;
SHOW ERRORS VIEW CALENDRIER_POUSSIN_RAW

-- ============================================================
-- CALENDRIER_POUSSIN_CUMUL (VIVANTS = naissances - pertes)
-- ============================================================
PROMPT === CALENDRIER_POUSSIN_CUMUL ===
CREATE OR REPLACE VIEW CALENDRIER_POUSSIN_CUMUL AS
SELECT daty,
       poussins, poussinsmales, poussinsfemelles, pertes, ca, cout, benefice,
       SUM(poussins)         OVER (ORDER BY daty) AS naissancescumulees,
       SUM(poussinsmales)    OVER (ORDER BY daty) AS malesnescumules,
       SUM(poussinsfemelles) OVER (ORDER BY daty) AS femellesneescumulees,
       SUM(pertes)           OVER (ORDER BY daty) AS pertescumulees,
       SUM(pertesmales)      OVER (ORDER BY daty) AS pertesmalescumulees,
       SUM(pertesfemelles)   OVER (ORDER BY daty) AS pertesfemellescumulees,
       GREATEST(0, SUM(poussins)         OVER (ORDER BY daty) - SUM(pertes)         OVER (ORDER BY daty)) AS poussinscumules,
       GREATEST(0, SUM(poussinsmales)    OVER (ORDER BY daty) - SUM(pertesmales)    OVER (ORDER BY daty)) AS malescumules,
       GREATEST(0, SUM(poussinsfemelles) OVER (ORDER BY daty) - SUM(pertesfemelles) OVER (ORDER BY daty)) AS femellescumulees
FROM   CALENDRIER_POUSSIN_RAW;
SHOW ERRORS VIEW CALENDRIER_POUSSIN_CUMUL

-- ============================================================
-- VERIFICATIONS
-- ============================================================
PROMPT === VERIFICATION ===

PROMPT --- Premier jour ponte Gen 0 (03/06-30/06) ---
SELECT TO_CHAR(daty,'DD/MM/YYYY') daty, ROUND(total) total, ROUND(femelles) femelles
FROM   V_CAL_PONTE_JOUR
WHERE  total > 0 AND ROWNUM <= 3
ORDER  BY daty;

PROMPT --- Premier jour ponte Gen 1 ---
SELECT TO_CHAR(MIN(daty),'DD/MM/YYYY') premier_jour_gen1
FROM   V_CAL_GEN1_PONTE WHERE total > 0;

PROMPT --- Premier jour ponte Gen 2 ---
SELECT TO_CHAR(MIN(daty),'DD/MM/YYYY') premier_jour_gen2
FROM   V_CAL_GEN2_PONTE WHERE total > 0;

PROMPT --- Totaux par generation ---
SELECT 'Gen 0' gen, ROUND(SUM(total)) total, ROUND(SUM(femelles)) femelles FROM V_CAL_PONTE_JOUR
UNION ALL
SELECT 'Gen 1', ROUND(SUM(total)), ROUND(SUM(femelles)) FROM V_CAL_GEN1_PONTE
UNION ALL
SELECT 'Gen 2', ROUND(SUM(total)), ROUND(SUM(femelles)) FROM V_CAL_GEN2_PONTE;

PROMPT --- Verification cumuls le 20/01 ---
SELECT TO_CHAR(daty,'DD/MM/YYYY') daty, poussins, pertes, poussinscumules, malescumules, femellescumulees
FROM   CALENDRIER_POUSSIN_CUMUL
WHERE  daty = TO_DATE('2026-01-20','YYYY-MM-DD');

PROMPT --- Resume par mois ---
SELECT TO_CHAR(daty,'YYYY-MM') mois,
       ROUND(SUM(poussins)) naissances,
       ROUND(SUM(ca)) ca, ROUND(SUM(cout)) cout, ROUND(SUM(benefice)) benefice,
       ROUND(SUM(pertes)) pertes
FROM   CALENDRIER_POUSSIN_RAW
GROUP  BY TO_CHAR(daty,'YYYY-MM')
HAVING SUM(poussins) > 0 OR SUM(pertes) > 0
ORDER  BY 1;