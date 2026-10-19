SET DEFINE OFF
SET LINESIZE 200

-- ============================================================
-- Vues du calendrier poussin, version compartiments + CA / benefice.
-- A lancer APRES migration_decl_perte_lot.sql.
--
-- Regles :
--  * Ponte : une poule pond a partir de SEMAINE_DEBUT_PONTE semaines revolues
--    apres l'eclosion ; les poussins arrivent NB_JOURS_ECLOSION jours apres.
--  * Pertes : declarees par compartiment et par sexe. Une perte de sexe '0'
--    reduit les poules des lots femelles du compartiment (reparties au
--    prorata de QTEINIT), sans plafond d'effectif.
--  * PU commande : PU moyen pondere des lignes BCF du bon du lot et de sa souche.
--  * CA du jour   = poussins issus de la ponte x PU commande.
--  * Cout du jour = poussins achetes (lots) x PU commande, au jour d'eclosion.
--  * Benefice     = CA - cout.
--  * Cumuls (poussinscumules/malescumules/femellescumulees) = VIVANTS
--    = naissances cumulees - pertes cumulees (plancher 0).
-- ============================================================

PROMPT === V_CAL_JOURS ===
CREATE OR REPLACE VIEW V_CAL_JOURS AS
SELECT b.d_min + LEVEL - 1 AS daty
FROM   (SELECT MIN(TRUNC(dateeclosion))        AS d_min,
               MAX(TRUNC(dateeclosion)) + 1000 AS d_max
        FROM   LOT
        WHERE  dateeclosion IS NOT NULL) b
CONNECT BY LEVEL <= b.d_max - b.d_min + 1;
SHOW ERRORS VIEW V_CAL_JOURS

PROMPT === V_CAL_LOT_PU : PU commande par lot ===
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

PROMPT === V_CAL_INITIAUX : poussins achetes, a leclosion, et leur cout ===
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

PROMPT === V_CAL_LOT_INFO : lots femelles, compartiment, souche, PU ===
CREATE OR REPLACE VIEW V_CAL_LOT_INFO AS
SELECT x.idlot,
       x.idsouche,
       x.idcompartiment,
       x.qteinit,
       x.dateeclosion,
       x.nb_jours_eclosion,
       x.semaine_debut_ponte,
       x.taux_eclosion,
       x.ratio_femelle,
       x.pu,
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
       WHERE  l.sexe = '0'
         AND  l.dateeclosion IS NOT NULL) x;
SHOW ERRORS VIEW V_CAL_LOT_INFO

PROMPT === V_CAL_PONTE_JOUR : poussins issus de la ponte et CA, par jour ===
CREATE OR REPLACE VIEW V_CAL_PONTE_JOUR AS
WITH cfg AS (
    SELECT idsouche, semaine, MAX(nbjour) AS nbjour
    FROM   CONFIG_PREVISION
    GROUP  BY idsouche, semaine
),
ponte AS (
    SELECT d.daty,
           li.idlot, li.idsouche, li.idcompartiment, li.qteinit, li.part_comp, li.pu,
           li.taux_eclosion, li.ratio_femelle,
           li.nb_jours_eclosion, li.semaine_debut_ponte,
           FLOOR((d.daty - li.nb_jours_eclosion - li.dateeclosion) / 7) AS sem
    FROM   V_CAL_JOURS d
           JOIN V_CAL_LOT_INFO li
             ON d.daty - li.nb_jours_eclosion >= li.dateeclosion
),
ponte_calc AS (
    SELECT p.daty,
           p.ratio_femelle,
           p.pu,
           GREATEST(0, p.qteinit - p.part_comp * NVL((
               SELECT SUM(NVL(pl.qtemort,0) + NVL(pl.qtevery,0))
               FROM   DECL_PERTE_LOT pl
               WHERE  pl.idcompartiment = p.idcompartiment
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
       SUM(nb * ratio_femelle / 100) AS femelles,
       SUM(nb * pu)                  AS ca
FROM   ponte_calc
GROUP  BY daty;
SHOW ERRORS VIEW V_CAL_PONTE_JOUR

PROMPT === V_CAL_PERTES : pertes declarees par jour (avec ventilation sexe) ===
CREATE OR REPLACE VIEW V_CAL_PERTES AS
SELECT TRUNC(daty) AS daty,
       SUM(NVL(qtemort,0) + NVL(qtevery,0))                                      AS pertes,
       SUM(CASE WHEN sexe = '0' THEN NVL(qtemort,0) + NVL(qtevery,0) ELSE 0 END) AS pertesfemelles,
       SUM(CASE WHEN sexe = '1' THEN NVL(qtemort,0) + NVL(qtevery,0) ELSE 0 END) AS pertesmales
FROM   DECL_PERTE_LOT
WHERE  daty IS NOT NULL
GROUP  BY TRUNC(daty);
SHOW ERRORS VIEW V_CAL_PERTES

PROMPT === CALENDRIER_POUSSIN_RAW ===
CREATE OR REPLACE VIEW CALENDRIER_POUSSIN_RAW AS
SELECT d.daty,
       NVL(i.total, 0)    + NVL(p.total, 0)                        AS poussins,
       NVL(i.males, 0)    + (NVL(p.total, 0) - NVL(p.femelles, 0)) AS poussinsmales,
       NVL(i.femelles, 0) + NVL(p.femelles, 0)                     AS poussinsfemelles,
       NVL(t.pertes, 0)                                            AS pertes,
       NVL(t.pertesmales, 0)                                       AS pertesmales,
       NVL(t.pertesfemelles, 0)                                    AS pertesfemelles,
       NVL(p.ca, 0)                                                AS ca,
       NVL(i.cout, 0)                                              AS cout,
       NVL(p.ca, 0) - NVL(i.cout, 0)                               AS benefice
FROM   V_CAL_JOURS d
       LEFT JOIN V_CAL_INITIAUX   i ON i.daty = d.daty
       LEFT JOIN V_CAL_PONTE_JOUR p ON p.daty = d.daty
       LEFT JOIN V_CAL_PERTES     t ON t.daty = d.daty;
SHOW ERRORS VIEW CALENDRIER_POUSSIN_RAW

PROMPT === CALENDRIER_POUSSIN_CUMUL ===
-- CORRIGE : poussinscumules = naissances cumulees - pertes cumulees
-- (avant : cumul des naissances brutes, sans deduction des morts)
CREATE OR REPLACE VIEW CALENDRIER_POUSSIN_CUMUL AS
SELECT daty,
       poussins,
       poussinsmales,
       poussinsfemelles,
       pertes,
       ca,
       cout,
       benefice,
       -- Cumuls bruts (info)
       SUM(poussins)         OVER (ORDER BY daty) AS naissancescumulees,
       SUM(poussinsmales)    OVER (ORDER BY daty) AS malesnescumules,
       SUM(poussinsfemelles) OVER (ORDER BY daty) AS femellesneescumulees,
       SUM(pertes)           OVER (ORDER BY daty) AS pertescumulees,
       SUM(pertesmales)      OVER (ORDER BY daty) AS pertesmalescumulees,
       SUM(pertesfemelles)   OVER (ORDER BY daty) AS pertesfemellescumulees,
       -- ✅ VIVANTS = naissances - pertes (plancher a 0)
       GREATEST(0, SUM(poussins)         OVER (ORDER BY daty)
                 - SUM(pertes)           OVER (ORDER BY daty)) AS poussinscumules,
       GREATEST(0, SUM(poussinsmales)    OVER (ORDER BY daty)
                 - SUM(pertesmales)      OVER (ORDER BY daty)) AS malescumules,
       GREATEST(0, SUM(poussinsfemelles) OVER (ORDER BY daty)
                 - SUM(pertesfemelles)   OVER (ORDER BY daty)) AS femellescumulees
FROM   CALENDRIER_POUSSIN_RAW;
SHOW ERRORS VIEW CALENDRIER_POUSSIN_CUMUL

PROMPT ============================================================
PROMPT VERIFICATION
PROMPT ============================================================

PROMPT --- Lots dont les pertes ne s'appliqueront pas (pas de compartiment) ---
SELECT l.id, l.idinsertcompfille
FROM   LOT l
WHERE  NOT EXISTS (SELECT 1 FROM INSERTION_COMPARTIMENT_FILLE i WHERE i.id = l.idinsertcompfille);

PROMPT --- PU commande retenu par lot ---
SELECT l.id, l.idboncommande, l.idsouche, l.qteinit, lp.pu
FROM   LOT l JOIN V_CAL_LOT_PU lp ON lp.idlot = l.id
ORDER  BY l.dateeclosion;

PROMPT --- Premier jour avec des poussins issus de la ponte ---
SELECT TO_CHAR(MIN(daty),'DD/MM/YYYY') AS premier_jour
FROM   V_CAL_PONTE_JOUR
WHERE  total > 0;

PROMPT --- Verification cumuls : le 20/01 doit deduire les pertes ---
SELECT TO_CHAR(daty,'DD/MM/YYYY') daty,
       poussins,
       pertes,
       poussinscumules,
       malescumules,
       femellescumulees
FROM   CALENDRIER_POUSSIN_CUMUL
WHERE  daty IN (TO_DATE('2026-01-13','YYYY-MM-DD'),
                TO_DATE('2026-01-19','YYYY-MM-DD'),
                TO_DATE('2026-01-20','YYYY-MM-DD'),
                TO_DATE('2026-01-21','YYYY-MM-DD'),
                TO_DATE('2026-06-30','YYYY-MM-DD'))
ORDER  BY daty;

PROMPT --- Resume par mois (ce que le calendrier affichera en pied) ---
SELECT TO_CHAR(daty,'YYYY-MM')   AS mois,
       ROUND(SUM(poussins))      AS naissances,
       ROUND(SUM(ca))            AS ca,
       ROUND(SUM(cout))          AS cout,
       ROUND(SUM(benefice))      AS benefice,
       ROUND(SUM(pertes))        AS pertes
FROM   CALENDRIER_POUSSIN_RAW
GROUP  BY TO_CHAR(daty,'YYYY-MM')
HAVING SUM(poussins) > 0 OR SUM(pertes) > 0
ORDER  BY 1;