CREATE OR REPLACE VIEW SITUATION_COMPARTIMENT AS 
SELECT 
       l.id,
       brp.idboncommandefille,
       c.id                                              AS compid,
       l.qteinit                                         AS qtetheorique,
       l.qteactuelle                                     AS qteactuelle,
       l.pu                                              AS pu,
       bcf.qte                                           AS qtebc,
       SUM(l.qteactuelle) OVER (PARTITION BY bcf.id)     AS sommeqteactuelle,
       ROUND(l.pu * (bcf.qte 
                     / SUM(l.qteactuelle) OVER (PARTITION BY bcf.id)), 2) 
                                                         AS valeurunitaire
FROM LOT l
LEFT JOIN BONRECEPTION_POUSSIN         brp ON brp.id = l.idbonreception
LEFT JOIN BONCOMMANDE_POUSSIN_FILLE    bcf ON bcf.id = brp.idboncommandefille
LEFT JOIN INSERTION_COMPARTIMENT_FILLE icf ON icf.id = l.idinsertcompfille
LEFT JOIN COMPARTIMENT                 c   ON c.id   = icf.idcompartiment;
/