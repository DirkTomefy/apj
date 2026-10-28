SET DEFINE OFF
SET SQLBLANKLINES ON

-- Situation par compartiment : une ligne par lot, avec la valeur unitaire du lot.
-- (NVL / NULLIF : pas d'ORA-01476 quand la somme des quantites actuelles vaut 0)
CREATE OR REPLACE VIEW SITUATION_COMPARTIMENT AS
SELECT
       l.id,
       brp.idboncommandefille,
       c.id                                                  AS compid,
       NVL(l.qteinit, 0)                                     AS qtetheorique,
       NVL(l.qteactuelle, 0)                                 AS qteactuelle,
       NVL(l.pu, 0)                                          AS pu,
       NVL(bcf.qte, 0)                                       AS qtebc,
       NVL(SUM(l.qteactuelle) OVER (PARTITION BY bcf.id), 0) AS sommeqteactuelle,
       NVL(ROUND(l.pu * (bcf.qte
                         / NULLIF(SUM(l.qteactuelle) OVER (PARTITION BY bcf.id), 0)), 2), 0)
                                                             AS valeurunitaire
FROM LOT l
LEFT JOIN BONRECEPTION_POUSSIN         brp ON brp.id = l.idbonreception
LEFT JOIN BONCOMMANDE_POUSSIN_FILLE    bcf ON bcf.id = brp.idboncommandefille
LEFT JOIN INSERTION_COMPARTIMENT_FILLE icf ON icf.id = l.idinsertcompfille
LEFT JOIN COMPARTIMENT                 c   ON c.id   = icf.idcompartiment;
