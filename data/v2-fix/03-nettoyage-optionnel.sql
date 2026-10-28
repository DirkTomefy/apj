SET DEFINE OFF

-- ============================================================
-- OPTIONNEL : vues de l'ancienne version du calendrier.
-- Aucune autre vue ni aucun code Java/JSP ne les utilise
-- (verifie dans USER_DEPENDENCIES et dans le code le 28/10/2026).
-- Ordre : de la plus dependante a la moins dependante.
-- Pour les recreer : decommenter le DDL en bas de ce fichier (copie de la base).
-- ============================================================

-- chaque DROP ignore la vue absente (ORA-00942) : le script passe sur n'importe quelle base
BEGIN EXECUTE IMMEDIATE 'DROP VIEW V_CAL_GEN2_HENS';          EXCEPTION WHEN OTHERS THEN IF SQLCODE <> -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP VIEW V_CAL_GEN1_PONTE_DETAIL';  EXCEPTION WHEN OTHERS THEN IF SQLCODE <> -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP VIEW V_CAL_GEN1_HENS';          EXCEPTION WHEN OTHERS THEN IF SQLCODE <> -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP VIEW V_CAL_PONTE_JOUR_DETAIL';  EXCEPTION WHEN OTHERS THEN IF SQLCODE <> -942 THEN RAISE; END IF; END;
/

-- ============================================================
-- RECREATION (DDL copie de la base le 28/10/2026, dans l'ordre de creation)
-- ============================================================
-- CREATE OR REPLACE FORCE VIEW "V_CAL_PONTE_JOUR_DETAIL" ("DATY", "IDSOUCHE", "RATIO_FEMELLE", "TAUX_ECLOSION", "PU", "TOTAL", "FEMELLES") AS
--   WITH cfg AS (
--     SELECT idsouche, semaine, MAX(nbjour) AS nbjour
--     FROM   CONFIG_PREVISION
--     GROUP  BY idsouche, semaine
-- ),
-- ponte AS (
--     SELECT d.daty,
-- 	   li.idsouche, li.idcompartiment, li.qteinit, li.part_comp, li.pu,
-- 	   li.taux_eclosion, li.ratio_femelle,
-- 	   li.nb_jours_eclosion, li.semaine_debut_ponte,
-- 	   FLOOR((d.daty - li.nb_jours_eclosion - li.dateeclosion) / 7) AS sem
--     FROM   V_CAL_JOURS d
-- 	   JOIN V_CAL_LOT_INFO li
-- 	     ON d.daty - li.nb_jours_eclosion >= li.dateeclosion
-- ),
-- ponte_calc AS (
--     SELECT p.daty, p.idsouche, p.ratio_femelle, p.taux_eclosion, p.pu,
-- 	   GREATEST(0, p.qteinit - p.part_comp * NVL((
-- 	       SELECT SUM(NVL(pl.qtemort,0) + NVL(pl.qtevery,0))
-- 	       FROM   DECL_PERTE_LOT pl
-- 	       WHERE  pl.idcompartiment = p.idcompartiment
-- 		 AND  pl.sexe = '0'
-- 		 AND  TRUNC(pl.daty) <= p.daty - p.nb_jours_eclosion
-- 	   ), 0))
-- 	   * NVL(c.nbjour, 0) * p.taux_eclosion / 100 AS nb
--     FROM   ponte p
-- 	   JOIN cfg c ON c.idsouche = p.idsouche AND c.semaine = p.sem
--     WHERE  p.sem >= p.semaine_debut_ponte
-- )
-- SELECT daty, idsouche,
--        MAX(ratio_femelle) AS ratio_femelle,
--        MAX(taux_eclosion) AS taux_eclosion,
--        MAX(pu)		  AS pu,
--        SUM(nb)					  AS total,
--        SUM(nb * ratio_femelle / 100)		  AS femelles
-- FROM   ponte_calc
-- GROUP  BY daty, idsouche
-- ;

-- CREATE OR REPLACE FORCE VIEW "V_CAL_GEN1_HENS" ("BIRTH_DAY", "IDSOUCHE", "NB_FEMALES") AS
--   SELECT daty AS birth_day, idsouche, femelles AS nb_females
-- FROM   V_CAL_PONTE_JOUR_DETAIL
-- WHERE  femelles > 0
-- ;

-- CREATE OR REPLACE FORCE VIEW "V_CAL_GEN1_PONTE_DETAIL" ("DATY", "IDSOUCHE", "TOTAL", "FEMELLES") AS
--   WITH souche_p AS (
--     SELECT id AS idsouche,
-- 	   NVL(nb_jours_eclosion, 21)	AS nb_jours_eclosion,
-- 	   NVL(semaine_debut_ponte, 21) AS semaine_debut_ponte,
-- 	   NVL(taux_eclosion, 85)	AS taux_eclosion,
-- 	   NVL(ratio_femelle, 50)	AS ratio_femelle,
-- 	   NVL(duree_vie_semaines, 72)	AS duree_vie_semaines
--     FROM   SOUCHE
-- ),
-- cfg AS (
--     SELECT idsouche, semaine, MAX(nbjour) AS nbjour
--     FROM   CONFIG_PREVISION
--     GROUP  BY idsouche, semaine
-- ),
-- gen1_calc AS (
--     SELECT d.daty,
-- 	   gh.idsouche,
-- 	   gh.nb_females,
-- 	   sp.ratio_femelle,
-- 	   sp.taux_eclosion,
-- 	   FLOOR((d.daty - sp.nb_jours_eclosion - gh.birth_day) / 7) AS sem,
-- 	   sp.semaine_debut_ponte,
-- 	   sp.duree_vie_semaines,
-- 	   NVL(c.nbjour, 0) AS nbjour,
-- 	   NVL(gh.nb_females * NVL(c.nbjour, 0) * sp.taux_eclosion / 100, 0) AS nb
--     FROM   V_CAL_JOURS d
-- 	   CROSS JOIN V_CAL_GEN1_HENS gh
-- 	   JOIN souche_p sp ON sp.idsouche = gh.idsouche
-- 	   LEFT JOIN cfg c ON c.idsouche = gh.idsouche
-- 			  AND c.semaine = FLOOR((d.daty - sp.nb_jours_eclosion - gh.birth_day) / 7) + 1
--     WHERE  d.daty - sp.nb_jours_eclosion >= gh.birth_day
-- )
-- SELECT daty, idsouche,
--        SUM(CASE WHEN sem >= semaine_debut_ponte AND sem <= duree_vie_semaines THEN nb ELSE 0 END) AS total,
--        SUM(CASE WHEN sem >= semaine_debut_ponte AND sem <= duree_vie_semaines THEN nb * ratio_femelle / 100 ELSE 0 END) AS femelles
-- FROM   gen1_calc
-- GROUP  BY daty, idsouche
-- ;

-- CREATE OR REPLACE FORCE VIEW "V_CAL_GEN2_HENS" ("BIRTH_DAY", "IDSOUCHE", "NB_FEMALES") AS
--   SELECT daty AS birth_day, idsouche, femelles AS nb_females
-- FROM   V_CAL_GEN1_PONTE_DETAIL
-- WHERE  femelles > 0
-- ;
