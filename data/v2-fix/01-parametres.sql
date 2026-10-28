SET DEFINE OFF

-- ============================================================
-- PARAMETRES DU CALENDRIER POUSSIN
--
-- Toutes les constantes metier sont ICI et nulle part ailleurs.
-- Changer une valeur : modifier la ligne, relancer ce fichier,
-- le calendrier est recalcule (les vues ne stockent rien).
-- ============================================================

PROMPT === V_CAL_PARAM : constantes ===
CREATE OR REPLACE VIEW V_CAL_PARAM AS
SELECT 2000 AS horizon_jours,               -- jours calcules apres la derniere eclosion
       21   AS def_nb_jours_eclosion,       -- souche sans valeur : jours d'incubation
       21   AS def_semaine_debut_ponte,     -- souche sans valeur : 1re semaine de ponte
       85   AS def_taux_eclosion,           -- souche sans valeur : % d'oeufs qui eclosent
       50   AS def_ratio_femelle,           -- souche sans valeur : % de femelles
       72   AS def_duree_vie_semaines       -- souche sans valeur : fin de ponte (semaines)
FROM   dual;
SHOW ERRORS VIEW V_CAL_PARAM

-- ============================================================
-- V_CAL_SOUCHE : parametres effectifs de chaque souche
-- (valeur de SOUCHE, sinon la valeur par defaut de V_CAL_PARAM)
-- ============================================================
PROMPT === V_CAL_SOUCHE ===
CREATE OR REPLACE VIEW V_CAL_SOUCHE AS
SELECT s.id                                                  AS idsouche,
       NVL(s.nb_jours_eclosion,   p.def_nb_jours_eclosion)   AS nb_jours_eclosion,
       NVL(s.semaine_debut_ponte, p.def_semaine_debut_ponte) AS semaine_debut_ponte,
       NVL(s.taux_eclosion,       p.def_taux_eclosion)       AS taux_eclosion,
       NVL(s.ratio_femelle,       p.def_ratio_femelle)       AS ratio_femelle,
       NVL(s.duree_vie_semaines,  p.def_duree_vie_semaines)  AS duree_vie_semaines
FROM   SOUCHE s
       CROSS JOIN V_CAL_PARAM p;
SHOW ERRORS VIEW V_CAL_SOUCHE
