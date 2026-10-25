SET DEFINE OFF
SET LINESIZE 200

-- ============================================================
-- Vue des cumuls : s'appuie sur CALENDRIER_POUSSIN_RAW (non modifiee)
-- ============================================================
CREATE OR REPLACE VIEW CALENDRIER_POUSSIN_CUMUL AS
SELECT daty,
       poussins,
       poussinsmales,
       poussinsfemelles,
       pertes,
       SUM(poussins)         OVER (ORDER BY daty) AS poussinscumules,
       SUM(poussinsmales)    OVER (ORDER BY daty) AS malescumules,
       SUM(poussinsfemelles) OVER (ORDER BY daty) AS femellescumulees
FROM   CALENDRIER_POUSSIN_RAW;
SHOW ERRORS VIEW CALENDRIER_POUSSIN_CUMUL

-- ============================================================
-- DIAGNOSTIC 1 : la ponte demarre-t-elle ?
-- Pour un lot femelle eclos le 13/01/2026 (souche 21 j d'eclosion, ponte des la
-- semaine 21) les premiers poussins issus de la ponte arrivent le 23/06/2026.
-- ============================================================
PROMPT --- Poussins autour du debut de ponte (23/06/2026) ---
SELECT TO_CHAR(daty,'DD/MM/YYYY') AS jour,
       ROUND(poussins) AS poussins,
       ROUND(poussinscumules) AS cumul
FROM   CALENDRIER_POUSSIN_CUMUL
WHERE  daty BETWEEN TO_DATE('2026-06-20','YYYY-MM-DD')
                AND TO_DATE('2026-07-05','YYYY-MM-DD')
ORDER  BY daty;

-- ============================================================
-- DIAGNOSTIC 2 : si tout reste a 0, verifier le lien lot -> souche -> config
-- ============================================================
PROMPT --- Lots et leur souche (IDSOUCHE doit valoir SOUCHE001 pour avoir de la ponte) ---
SELECT l.id, l.idsouche, l.sexe, l.qteinit, l.dateeclosion
FROM   LOT l
ORDER  BY l.dateeclosion;

PROMPT --- Parametres de la souche ---
SELECT id, nb_jours_eclosion, semaine_debut_ponte, taux_eclosion, ratio_femelle,
       duree_vie_semaines
FROM   SOUCHE;