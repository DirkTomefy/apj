-- ============================================================================
--  Ajout de la colonne "reste" = qte - maty - very
-- ============================================================================

-- 1) Ajout de la colonne
ALTER TABLE BONRECEPTION_POUSSIN_FILLE
    ADD (reste NUMBER(15,2) DEFAULT 0);

-- 2) Contrainte : reste ne peut pas être négatif
ALTER TABLE BONRECEPTION_POUSSIN_FILLE
    ADD CONSTRAINT ck_brpf_reste CHECK (reste >= 0);

-- ============================================================================
--  Correction du montant : montant = reste * pu (au lieu de qte * pu)
-- ============================================================================

CREATE OR REPLACE VIEW BONRECEPTION_POUSSIN_FILLE_LIB AS
SELECT brpf.*,
       i.libelle AS produitlib,
       s.val     AS souchelibr,
       (brpf.reste * brpf.pu) AS montant     -- ⚡ reste * pu
FROM BONRECEPTION_POUSSIN_FILLE brpf
LEFT JOIN AS_INGREDIENTS i ON i.id = brpf.idproduit
LEFT JOIN SOUCHE         s ON s.id = brpf.idsouche;

COMMIT;

-- ============================================================================
--  Vue COMPARTIMENT_VALEUR_LIB
--  Calcule pour chaque compartiment :
--    - qte_actuelle      : somme des qte déjà insérées
--    - valeur_actuelle   : somme des valeur_compartiment
--    - pu                : prix unitaire de référence (max des insertions)
--    - valeur_theorique  : capacite * pu
--    - valeur_non_remplie: valeur_theorique - valeur_actuelle
-- ============================================================================

CREATE OR REPLACE VIEW COMPARTIMENT_VALEUR_LIB AS
SELECT c.id,
       c.nom,
       c.capacite,
       NVL(SUM(icf.qte), 0)                       AS qte_actuelle,
       NVL(SUM(icf.valeur_compartiment), 0)       AS valeur_actuelle,
       NVL(MAX(icf.pu), 0)                        AS pu,
       (c.capacite * NVL(MAX(icf.pu), 0))         AS valeur_theorique,
       (c.capacite * NVL(MAX(icf.pu), 0))
           - NVL(SUM(icf.valeur_compartiment), 0) AS valeur_non_remplie
FROM COMPARTIMENT c
LEFT JOIN INSERTION_COMPARTIMENT_FILLE icf
       ON icf.idcompartiment = c.id
GROUP BY c.id, c.nom, c.capacite;

COMMIT;

-- Vérification
SELECT * FROM COMPARTIMENT_VALEUR_LIB;