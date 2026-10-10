-- ============================================================================
--  Correction : COMPARTIMENT_VALEUR_LIB (suite au rename valeurCompartiment)
-- ============================================================================

CREATE OR REPLACE VIEW COMPARTIMENT_VALEUR_LIB AS
SELECT c.id,
       c.nom,
       c.capacite,
       NVL(SUM(icf.qte), 0)                       AS qte_actuelle,
       NVL(SUM(icf.valeurCompartiment), 0)        AS valeur_actuelle,
       NVL(MAX(icf.pu), 0)                        AS pu,
       (c.capacite * NVL(MAX(icf.pu), 0))         AS valeur_theorique,
       (c.capacite * NVL(MAX(icf.pu), 0))
           - NVL(SUM(icf.valeurCompartiment), 0)  AS valeur_non_remplie
FROM COMPARTIMENT c
LEFT JOIN INSERTION_COMPARTIMENT_FILLE icf
       ON icf.idcompartiment = c.id
GROUP BY c.id, c.nom, c.capacite;

COMMIT;

-- Vérification
SELECT * FROM COMPARTIMENT_VALEUR_LIB;