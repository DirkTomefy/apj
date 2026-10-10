

CREATE OR REPLACE VIEW BCPF_VIVANTS_LIB AS
SELECT bcpf.id           AS idbcpf,
       bcpf.idmere       AS idbc,
       bcpf.idproduit,
       bcpf.idsouche,
       bcpf.qte          AS qteCommande,
       bcpf.pu,
       NVL(SUM(brpf.qte),  0) AS totalQteBR,
       NVL(SUM(brpf.very), 0) AS totalVivants,
       NVL(SUM(brpf.maty), 0) AS totalMorts,
       CASE
         WHEN NVL(SUM(brpf.very), 0) > 0
         THEN (NVL(SUM(brpf.qte), 0) * bcpf.pu) / NVL(SUM(brpf.very), 0)
         ELSE bcpf.pu
       END               AS valeurUnitaire
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
/


CREATE OR REPLACE VIEW INSERTION_COMP_FILLE_LIB AS
SELECT icf.id,
       icf.idmere,
       icf.idcompartiment,
       icf.idbcpf,
       icf.qte,
       icf.pu,
       icf.valeurCompartiment,
       icf.sexe,
       icf.idsante,
       c.nom    AS compartimentlib,
       s.val    AS sexelib,
       sp.val   AS santelib,
       bcpf.idproduit     AS bcpfidproduit,
       ai.libelle         AS bcpfproduitlib,
       bcpf.idsouche      AS bcpfidsouche,
       so.val             AS bcpfsouchelibr,
       bcpf.qte           AS bcpfqte,
       bcpf.pu            AS bcpfpu,
       v.valeurUnitaire   AS valeurUnitaire
FROM INSERTION_COMPARTIMENT_FILLE icf
LEFT JOIN COMPARTIMENT c  ON c.id  = icf.idcompartiment
LEFT JOIN SEXE         s  ON s.id  = icf.sexe
LEFT JOIN SANTEPOUSSIN sp ON sp.id = icf.idsante
LEFT JOIN BONCOMMANDE_POUSSIN_FILLE bcpf ON bcpf.id = icf.idbcpf
LEFT JOIN AS_INGREDIENTS ai ON ai.id = bcpf.idproduit
LEFT JOIN SOUCHE         so ON so.id = bcpf.idsouche
LEFT JOIN BCPF_VIVANTS_LIB v ON v.idbcpf = icf.idbcpf;
/

CREATE OR REPLACE VIEW COMPARTIMENT_VALEUR_LIB AS
SELECT c.id,
       c.nom,
       c.capacite,
       NVL(SUM(icf.qte), 0)                                    AS qteActuelle,
       NVL(SUM(icf.valeurCompartiment), 0)                     AS valeurActuelle,
       NVL(MAX(icf.pu), 0)                                     AS pu,
       (c.capacite * NVL(MAX(icf.pu), 0))                      AS valeurTheorique,
       (c.capacite * NVL(MAX(icf.pu), 0))
           - NVL(SUM(icf.valeurCompartiment), 0)               AS valeurNonRemplie,
       CASE
         WHEN NVL(SUM(icf.qte), 0) > 0
         THEN NVL(SUM(icf.qte * v.valeurUnitaire), 0) / SUM(icf.qte)
         ELSE 0
       END                                                     AS valeurUnitaire
FROM COMPARTIMENT c
LEFT JOIN INSERTION_COMPARTIMENT_FILLE icf
       ON icf.idcompartiment = c.id
LEFT JOIN BCPF_VIVANTS_LIB v
       ON v.idbcpf = icf.idbcpf
GROUP BY c.id, c.nom, c.capacite;

/

COMMIT;