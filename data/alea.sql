-- ============================================================================
--  Vue 1 : BCPF_VIVANTS_LIB
--  Pour chaque ligne de BC, calcule le total des very du (des) dernier(s) BR
--  - "dernier BR" = BR non référencé par un autre BR via idbrprecedent
--  - On matche par produit + souche
-- ============================================================================

CREATE OR REPLACE VIEW BCPF_VIVANTS_LIB AS
SELECT bcpf.id           AS idbcpf,
       bcpf.idmere       AS idbc,
       bcpf.idproduit,
       bcpf.idsouche,
       bcpf.qte          AS qteCommande,
       bcpf.pu,
       NVL(SUM(brpf.very), 0) AS totalVivants,
       NVL(SUM(brpf.maty), 0) AS totalMorts
FROM BONCOMMANDE_POUSSIN_FILLE bcpf
LEFT JOIN BONCOMMANDE_POUSSIN bcp
       ON bcp.id = bcpf.idmere
LEFT JOIN BONRECEPTION_POUSSIN brp
       ON brp.idboncommande = bcp.id
LEFT JOIN BONRECEPTION_POUSSIN_FILLE brpf
       ON brpf.idmere    = brp.id
      AND brpf.idproduit = bcpf.idproduit
      AND brpf.idsouche  = bcpf.idsouche
WHERE brp.id IS NULL
   OR NOT EXISTS (
        SELECT 1 FROM BONRECEPTION_POUSSIN brp2
        WHERE brp2.idbrprecedent = brp.id
   )
GROUP BY bcpf.id, bcpf.idmere, bcpf.idproduit, bcpf.idsouche,
         bcpf.qte, bcpf.pu;

COMMIT;

-- ============================================================================
--  Vue 2 : COMPARTIMENT_VALEUR_LIB
--  Agrège par compartiment :
--    - somme des ICF (qte, valeur, pu)
--    - valeur unitaire = valeurTotale / totalVivants (sur les BCPF distinctes)
-- ============================================================================

CREATE OR REPLACE VIEW COMPARTIMENT_VALEUR_LIB AS
SELECT c.id,
       c.nom,
       c.capacite,
       NVL((SELECT SUM(icf.qte) FROM INSERTION_COMPARTIMENT_FILLE icf
            WHERE icf.idcompartiment = c.id), 0)              AS qteActuelle,
       NVL((SELECT SUM(icf.valeurCompartiment) FROM INSERTION_COMPARTIMENT_FILLE icf
            WHERE icf.idcompartiment = c.id), 0)              AS valeurActuelle,
       NVL((SELECT MAX(icf.pu) FROM INSERTION_COMPARTIMENT_FILLE icf
            WHERE icf.idcompartiment = c.id), 0)              AS pu,
       (c.capacite * NVL((SELECT MAX(icf.pu) FROM INSERTION_COMPARTIMENT_FILLE icf
                          WHERE icf.idcompartiment = c.id), 0))
                                                              AS valeurTheorique,
       (c.capacite * NVL((SELECT MAX(icf.pu) FROM INSERTION_COMPARTIMENT_FILLE icf
                          WHERE icf.idcompartiment = c.id), 0))
           - NVL((SELECT SUM(icf.valeurCompartiment) FROM INSERTION_COMPARTIMENT_FILLE icf
                  WHERE icf.idcompartiment = c.id), 0)        AS valeurNonRemplie,
       -- ⚡ Valeur unitaire = valeurTotale / totalVivants cumulés
       CASE
         WHEN NVL((SELECT SUM(v.totalVivants)
                   FROM BCPF_VIVANTS_LIB v
                   WHERE v.idbcpf IN (
                       SELECT DISTINCT icf.idbcpf
                       FROM INSERTION_COMPARTIMENT_FILLE icf
                       WHERE icf.idcompartiment = c.id
                   )), 0) > 0
         THEN NVL((SELECT SUM(icf.valeurCompartiment)
                   FROM INSERTION_COMPARTIMENT_FILLE icf
                   WHERE icf.idcompartiment = c.id), 0)
              / (SELECT SUM(v.totalVivants)
                 FROM BCPF_VIVANTS_LIB v
                 WHERE v.idbcpf IN (
                     SELECT DISTINCT icf.idbcpf
                     FROM INSERTION_COMPARTIMENT_FILLE icf
                     WHERE icf.idcompartiment = c.id
                 ))
         ELSE 0
       END                                                    AS valeurUnitaire
FROM COMPARTIMENT c;

COMMIT;

-- Vérification
SELECT id, nom, capacite, qteActuelle, pu,
       valeurActuelle, valeurUnitaire
FROM COMPARTIMENT_VALEUR_LIB
ORDER BY id;