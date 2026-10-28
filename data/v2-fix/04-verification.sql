SET DEFINE OFF
SET PAGESIZE 100
SET LINESIZE 200

-- ============================================================
-- VERIFICATION apres 01 et 02
-- ============================================================

PROMPT --- Vues invalides (doit etre vide) ---
SELECT object_name, status
FROM   user_objects
WHERE  object_type = 'VIEW'
  AND  (object_name LIKE 'V\_CAL\_%' ESCAPE '\' OR object_name LIKE 'CALENDRIER\_POUSSIN%' ESCAPE '\')
  AND  status <> 'VALID';

PROMPT --- Parametres en vigueur ---
SELECT * FROM V_CAL_PARAM;

PROMPT --- Parametres effectifs par souche ---
SELECT * FROM V_CAL_SOUCHE ORDER BY idsouche;

PROMPT --- Resume par mois (a comparer avant / apres) ---
SELECT TO_CHAR(daty,'YYYY-MM') mois,
       ROUND(SUM(poussins)) naissances,
       ROUND(SUM(ca)) ca, ROUND(SUM(cout)) cout, ROUND(SUM(benefice)) benefice,
       ROUND(SUM(pertes)) pertes
FROM   CALENDRIER_POUSSIN_RAW
GROUP  BY TO_CHAR(daty,'YYYY-MM')
HAVING SUM(poussins) > 0 OR SUM(pertes) > 0
ORDER  BY 1;
