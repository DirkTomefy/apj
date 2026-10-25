-- ============================================================================
--  Ajout de la colonne fournisseur dans BONCOMMANDE_POUSSIN_FILLE
-- ============================================================================

-- 1) Ajouter la colonne
ALTER TABLE BONCOMMANDE_POUSSIN_FILLE
    ADD (fournisseur VARCHAR2(255));

-- 2) Clé étrangère vers FOURNISSEUR
ALTER TABLE BONCOMMANDE_POUSSIN_FILLE
    ADD CONSTRAINT fk_bcpf_fournisseur FOREIGN KEY (fournisseur)
        REFERENCES FOURNISSEUR(id);

-- 3) Index
CREATE INDEX idx_bcpf_frs ON BONCOMMANDE_POUSSIN_FILLE (fournisseur);

-- 4) Recréer la vue fille avec le libellé fournisseur
CREATE OR REPLACE VIEW BONCOMMANDE_POUSSIN_FILLE_LIB AS
SELECT bcpf.id, bcpf.idmere, bcpf.idproduit, bcpf.idsouche,
       bcpf.qte, bcpf.pu, bcpf.fournisseur,
       i.libelle AS produitlib,
       s.val     AS souchelibr,
       (bcpf.qte * bcpf.pu) AS montant,
       (bcpf.id || ' - ' || NVL(i.libelle, '?')) AS idproduitlib,
       f.nom     AS fournisseurlib
FROM BONCOMMANDE_POUSSIN_FILLE bcpf
LEFT JOIN AS_INGREDIENTS i ON i.id = bcpf.idproduit
LEFT JOIN SOUCHE         s ON s.id = bcpf.idsouche
LEFT JOIN FOURNISSEUR    f ON f.id = bcpf.fournisseur;
/

COMMIT;

-- 5) Vérification
SELECT column_name, data_type
FROM user_tab_columns
WHERE table_name = 'BONCOMMANDE_POUSSIN_FILLE'
ORDER BY column_id;

SELECT column_name
FROM user_tab_columns
WHERE table_name = 'BONCOMMANDE_POUSSIN_FILLE_LIB'
ORDER BY column_id;