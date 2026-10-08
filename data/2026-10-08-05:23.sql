-- ============================================================================
--  Ajout de la colonne "reste" = qte - maty - very
-- ============================================================================

-- 1) Ajout de la colonne
ALTER TABLE BONRECEPTION_POUSSIN_FILLE
    ADD (reste NUMBER(15,2) DEFAULT 0);

-- 2) Contrainte : reste ne peut pas être négatif
ALTER TABLE BONRECEPTION_POUSSIN_FILLE
    ADD CONSTRAINT ck_brpf_reste CHECK (reste >= 0);

-- 3) Recréer la vue pour inclure la nouvelle colonne
CREATE OR REPLACE VIEW BONRECEPTION_POUSSIN_FILLE_LIB AS
SELECT brpf.*,
       i.libelle AS produitlib,
       s.val     AS souchelibr,
       (brpf.qte * brpf.pu) AS montant
FROM BONRECEPTION_POUSSIN_FILLE brpf
LEFT JOIN AS_INGREDIENTS i ON i.id = brpf.idproduit
LEFT JOIN SOUCHE         s ON s.id = brpf.idsouche;

COMMIT;

-- 4) Vérification
SELECT column_name, data_type
FROM user_tab_columns
WHERE table_name = 'BONRECEPTION_POUSSIN_FILLE'
ORDER BY column_id;