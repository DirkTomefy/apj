CREATE OR REPLACE VIEW COMPARTIMENT_VALEUR_LIB AS
SELECT c.id,
       c.nom,
       c.capacite,
       NVL(SUM(icf.qte), 0)                       AS qteActuelle,
       NVL(SUM(icf.valeurCompartiment), 0)        AS valeurActuelle,
       NVL(MAX(icf.pu), 0)                        AS pu,
       (c.capacite * NVL(MAX(icf.pu), 0))         AS valeurTheorique,
       (c.capacite * NVL(MAX(icf.pu), 0))
           - NVL(SUM(icf.valeurCompartiment), 0)  AS valeurNonRemplie
FROM COMPARTIMENT c
LEFT JOIN INSERTION_COMPARTIMENT_FILLE icf
       ON icf.idcompartiment = c.id
GROUP BY c.id, c.nom, c.capacite;

COMMIT;.2

$










ù