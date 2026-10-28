SET DEFINE OFF

-- ============================================================
-- CALENDRIER POUSSIN : vues de calcul (a lancer apres 01-parametres.sql)
--
-- Memes noms, memes colonnes, memes resultats que
-- data/v2/2026-10-19-03:04-calendrier_poussin_vues_v2.sql.
-- Seule difference : les constantes viennent de V_CAL_PARAM et
-- les parametres de souche de V_CAL_SOUCHE (plus de 21/85/50/72/2000 en dur).
--
-- Chaine de calcul (de la source vers l'ecran) :
--   LOT, SOUCHE, CONFIG_PREVISION, DECL_PERTE_LOT, BC, IC
--     -> V_CAL_LOT_PU, V_CAL_LOT_INFO, V_CAL_INITIAUX, V_CAL_PERTES
--     -> V_CAL_GENERATIONS (ponte gen 0, 1, 2)
--     -> CALENDRIER_POUSSIN_RAW (un jour = une ligne)
--     -> CALENDRIER_POUSSIN_CUMUL (lu par a_tahina.calendrier.child.CalendrierPoussin)
--
-- Regles metier :
--  * Gen 0 : semaine de CONFIG_PREVISION = age en semaines revolues ;
--    pas de plafond duree_vie_semaines (c'est la config qui borne).
--  * Gen 1 et 2 : semaine de CONFIG_PREVISION = age + 1 ; ponte entre
--    semaine_debut_ponte et duree_vie_semaines.
--  * Pondeuses gen 0 = qteinit du lot femelle - pertes femelles du compartiment
--    (au prorata du lot dans le compartiment) declarees jusqu'au jour de ponte.
--  * CA = poussins de la ponte gen 0 x PU de la commande (gen 1 et 2 hors CA).
--  * Cout = pertes x PU moyen du compartiment ; benefice = CA - cout.
--  * Vivants (poussinscumules) = naissances cumulees - pertes cumulees.
--  * Sexe : '0' = femelle, '1' = male (table SEXE).
-- ============================================================

PROMPT === V_CAL_BORNES / V_CAL_JOURS : jours couverts par le calendrier ===
CREATE OR REPLACE VIEW V_CAL_BORNES AS
SELECT MIN(TRUNC(l.dateeclosion))                       AS d_min,
       MAX(TRUNC(l.dateeclosion)) + MAX(p.horizon_jours) AS d_max
FROM   LOT l
       CROSS JOIN V_CAL_PARAM p
WHERE  l.dateeclosion IS NOT NULL;
SHOW ERRORS VIEW V_CAL_BORNES

CREATE OR REPLACE VIEW V_CAL_JOURS AS
SELECT b.d_min + LEVEL - 1 AS daty
FROM   V_CAL_BORNES b
CONNECT BY LEVEL <= b.d_max - b.d_min + 1;
SHOW ERRORS VIEW V_CAL_JOURS

PROMPT === V_CAL_K7 : les 7 jours d'une semaine ===
CREATE OR REPLACE VIEW V_CAL_K7 AS
SELECT LEVEL - 1 AS k FROM dual CONNECT BY LEVEL <= 7;
SHOW ERRORS VIEW V_CAL_K7

PROMPT === V_CAL_LOT_PU : PU d'un lot (moyenne ponderee de la ligne BC, sinon PU du lot) ===
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

PROMPT === V_CAL_INITIAUX : poussins des lots, au jour d'eclosion ===
CREATE OR REPLACE VIEW V_CAL_INITIAUX AS
SELECT TRUNC(l.dateeclosion)                                   AS daty,
       SUM(l.qteinit)                                          AS total,
       SUM(CASE WHEN l.sexe = '0' THEN l.qteinit ELSE 0 END)   AS femelles,
       SUM(CASE WHEN l.sexe = '1' THEN l.qteinit ELSE 0 END)   AS males
FROM   LOT l
WHERE  l.dateeclosion IS NOT NULL
GROUP  BY TRUNC(l.dateeclosion);
SHOW ERRORS VIEW V_CAL_INITIAUX

PROMPT === V_CAL_LOT_INFO : lots femelles avec parametres de souche et part du compartiment ===
CREATE OR REPLACE VIEW V_CAL_LOT_INFO AS
SELECT x.idlot, x.idsouche, x.idcompartiment, x.qteinit,
       x.dateeclosion, x.nb_jours_eclosion, x.semaine_debut_ponte,
       x.taux_eclosion, x.ratio_femelle, x.pu,
       NVL(x.qteinit / NULLIF(SUM(x.qteinit) OVER (PARTITION BY x.idcompartiment), 0), 0) AS part_comp
FROM  (SELECT l.id                                                  AS idlot,
              l.idsouche,
              icf.idcompartiment,
              l.qteinit,
              TRUNC(l.dateeclosion)                                 AS dateeclosion,
              NVL(vs.nb_jours_eclosion,   p.def_nb_jours_eclosion)   AS nb_jours_eclosion,
              NVL(vs.semaine_debut_ponte, p.def_semaine_debut_ponte) AS semaine_debut_ponte,
              NVL(vs.taux_eclosion,       p.def_taux_eclosion)       AS taux_eclosion,
              NVL(vs.ratio_femelle,       p.def_ratio_femelle)       AS ratio_femelle,
              lp.pu
       FROM   LOT l
              CROSS JOIN V_CAL_PARAM p
              LEFT JOIN V_CAL_SOUCHE vs                  ON vs.idsouche = l.idsouche
              LEFT JOIN INSERTION_COMPARTIMENT_FILLE icf ON icf.id      = l.idinsertcompfille
              LEFT JOIN V_CAL_LOT_PU lp                  ON lp.idlot    = l.id
       WHERE  l.sexe = '0' AND l.dateeclosion IS NOT NULL) x;
SHOW ERRORS VIEW V_CAL_LOT_INFO

-- ============================================================
-- V_CAL_GENERATIONS : ponte des generations 0, 1 et 2, par jour
--   g0_* : poussins issus des lots achetes (+ CA)
--   g1_* : poussins issus des femelles nees de la ponte gen 0
--   g2_* : poussins issus des femelles nees de la ponte gen 1
-- ============================================================
PROMPT === V_CAL_GENERATIONS ===
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
           MAX(pu)                       AS pu,
           SUM(nb)                       AS total,
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
           JOIN cfg c           ON c.idsouche  = h.idsouche
                               AND c.semaine - 1 >= vs.semaine_debut_ponte
                               AND c.semaine - 1 <= vs.duree_vie_semaines
           CROSS JOIN V_CAL_K7 k
    WHERE  h.femelles > 0
),
gen1 AS (
    SELECT /*+ MATERIALIZE */
           daty, idsouche,
           SUM(nb)                       AS total,
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
           JOIN cfg c           ON c.idsouche  = h.idsouche
                               AND c.semaine - 1 >= vs.semaine_debut_ponte
                               AND c.semaine - 1 <= vs.duree_vie_semaines
           CROSS JOIN V_CAL_K7 k
    WHERE  h.femelles > 0
),
gen2 AS (
    SELECT daty,
           SUM(nb)                       AS total,
           SUM(nb * ratio_femelle / 100) AS femelles
    FROM   gen2_calc
    WHERE  daty <= (SELECT d_max FROM bornes)
    GROUP  BY daty
)
SELECT daty,
       SUM(g0_total)    AS g0_total,
       SUM(g0_femelles) AS g0_femelles,
       SUM(g0_ca)       AS g0_ca,
       SUM(g1_total)    AS g1_total,
       SUM(g1_femelles) AS g1_femelles,
       SUM(g2_total)    AS g2_total,
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
SHOW ERRORS VIEW V_CAL_GENERATIONS

PROMPT === Vues de detail par generation (memes noms et colonnes qu avant) ===
CREATE OR REPLACE VIEW V_CAL_PONTE_JOUR AS
SELECT daty, g0_total AS total, g0_femelles AS femelles, g0_ca AS ca
FROM   V_CAL_GENERATIONS
WHERE  g0_total > 0;
SHOW ERRORS VIEW V_CAL_PONTE_JOUR

CREATE OR REPLACE VIEW V_CAL_GEN1_PONTE AS
SELECT daty, g1_total AS total, g1_femelles AS femelles
FROM   V_CAL_GENERATIONS
WHERE  g1_total > 0;
SHOW ERRORS VIEW V_CAL_GEN1_PONTE

CREATE OR REPLACE VIEW V_CAL_GEN2_PONTE AS
SELECT daty, g2_total AS total, g2_femelles AS femelles
FROM   V_CAL_GENERATIONS
WHERE  g2_total > 0;
SHOW ERRORS VIEW V_CAL_GEN2_PONTE

PROMPT === V_CAL_PERTES : pertes, pertes par sexe et cout, par jour ===
CREATE OR REPLACE VIEW V_CAL_PERTES AS
WITH pu_comp AS (
    SELECT icf.idcompartiment,
           SUM(NVL(l.qteinit,0) * NVL(icf.pu,0)) / NULLIF(SUM(NVL(l.qteinit,0)), 0) AS pu_moyen
    FROM   INSERTION_COMPARTIMENT_FILLE icf
           LEFT JOIN LOT l ON l.idinsertcompfille = icf.id
    GROUP  BY icf.idcompartiment
)
SELECT TRUNC(dp.daty) AS daty,
       SUM(NVL(dp.qtemort,0) + NVL(dp.qtevery,0))                                         AS pertes,
       SUM(CASE WHEN dp.sexe = '0' THEN NVL(dp.qtemort,0) + NVL(dp.qtevery,0) ELSE 0 END) AS pertesfemelles,
       SUM(CASE WHEN dp.sexe = '1' THEN NVL(dp.qtemort,0) + NVL(dp.qtevery,0) ELSE 0 END) AS pertesmales,
       SUM((NVL(dp.qtemort,0) + NVL(dp.qtevery,0)) * NVL(pc.pu_moyen, 0))                 AS cout
FROM   DECL_PERTE_LOT dp
       LEFT JOIN pu_comp pc ON pc.idcompartiment = dp.idcompartiment
WHERE  dp.daty IS NOT NULL
GROUP  BY TRUNC(dp.daty);
SHOW ERRORS VIEW V_CAL_PERTES

CREATE OR REPLACE VIEW V_CAL_PERTES_COUT AS
SELECT daty, cout FROM V_CAL_PERTES;
SHOW ERRORS VIEW V_CAL_PERTES_COUT

PROMPT === CALENDRIER_POUSSIN_RAW : un jour = naissances (3 generations), pertes, CA, cout ===
CREATE OR REPLACE VIEW CALENDRIER_POUSSIN_RAW AS
SELECT d.daty,
       NVL(i.total, 0) + NVL(g.g0_total, 0) + NVL(g.g1_total, 0) + NVL(g.g2_total, 0) AS poussins,
       NVL(i.males, 0)
       + (NVL(g.g0_total, 0) - NVL(g.g0_femelles, 0))
       + (NVL(g.g1_total, 0) - NVL(g.g1_femelles, 0))
       + (NVL(g.g2_total, 0) - NVL(g.g2_femelles, 0))                                  AS poussinsmales,
       NVL(i.femelles, 0) + NVL(g.g0_femelles, 0) + NVL(g.g1_femelles, 0) + NVL(g.g2_femelles, 0) AS poussinsfemelles,
       NVL(t.pertes, 0)         AS pertes,
       NVL(t.pertesmales, 0)    AS pertesmales,
       NVL(t.pertesfemelles, 0) AS pertesfemelles,
       NVL(g.g0_ca, 0)          AS ca,
       NVL(t.cout, 0)           AS cout,
       NVL(g.g0_ca, 0) - NVL(t.cout, 0) AS benefice
FROM   V_CAL_JOURS d
       LEFT JOIN V_CAL_INITIAUX    i ON i.daty = d.daty
       LEFT JOIN V_CAL_GENERATIONS g ON g.daty = d.daty
       LEFT JOIN V_CAL_PERTES      t ON t.daty = d.daty;
SHOW ERRORS VIEW CALENDRIER_POUSSIN_RAW

PROMPT === CALENDRIER_POUSSIN_CUMUL : cumuls glissants (vivants = naissances - pertes) ===
CREATE OR REPLACE VIEW CALENDRIER_POUSSIN_CUMUL AS
SELECT daty,
       poussins, poussinsmales, poussinsfemelles, pertes, ca, cout, benefice,
       naissancescumulees, malesnescumules, femellesneescumulees,
       pertescumulees, pertesmalescumulees, pertesfemellescumulees,
       GREATEST(0, naissancescumulees   - pertescumulees)         AS poussinscumules,
       GREATEST(0, malesnescumules      - pertesmalescumulees)    AS malescumules,
       GREATEST(0, femellesneescumulees - pertesfemellescumulees) AS femellescumulees
FROM (
    SELECT r.daty, r.poussins, r.poussinsmales, r.poussinsfemelles,
           r.pertes, r.ca, r.cout, r.benefice,
           SUM(r.poussins)         OVER (ORDER BY r.daty ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS naissancescumulees,
           SUM(r.poussinsmales)    OVER (ORDER BY r.daty ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS malesnescumules,
           SUM(r.poussinsfemelles) OVER (ORDER BY r.daty ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS femellesneescumulees,
           SUM(r.pertes)           OVER (ORDER BY r.daty ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS pertescumulees,
           SUM(r.pertesmales)      OVER (ORDER BY r.daty ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS pertesmalescumulees,
           SUM(r.pertesfemelles)   OVER (ORDER BY r.daty ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS pertesfemellescumulees
    FROM   CALENDRIER_POUSSIN_RAW r
);
SHOW ERRORS VIEW CALENDRIER_POUSSIN_CUMUL
