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

-- Vérification
SELECT id, qte, maty, very, reste, pu, (reste * pu) AS montant
FROM BONRECEPTION_POUSSIN_FILLE_LIB
ORDER BY id DESC
FETCH FIRST 5 ROWS ONLY;