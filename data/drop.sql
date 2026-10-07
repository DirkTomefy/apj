-- ============================================================================
--  DROP COMPLET — Module Poussin
--  Version explicite (constraint par constraint)
-- ============================================================================

-- ============================================================================
-- 1. VUES
-- ============================================================================
DROP VIEW BONCOMMANDE_POUSSIN_LIB;
DROP VIEW BONCOMMANDE_POUSSIN_FILLE_LIB;
DROP VIEW BONRECEPTION_POUSSIN_LIB;
DROP VIEW BONRECEPTION_POUSSIN_FILLE_LIB;
DROP VIEW INSERTION_COMPARTIMENT_LIB;
DROP VIEW INSERTION_COMPARTIMENT_FILLE_LIB;


-- ============================================================================
-- 2. CONTRAINTES CHECK
-- ============================================================================
ALTER TABLE BONCOMMANDE_POUSSIN          DROP CONSTRAINT ck_bcp_etat;
ALTER TABLE BONRECEPTION_POUSSIN         DROP CONSTRAINT ck_brp_etat;
ALTER TABLE INSERTION_COMPARTIMENT       DROP CONSTRAINT ck_ic_etat;

ALTER TABLE BONRECEPTION_POUSSIN         DROP CONSTRAINT ck_brp_fin;
ALTER TABLE INSERTION_COMPARTIMENT_FILLE DROP CONSTRAINT ck_icf_fin;
ALTER TABLE INSERTION_COMPARTIMENT_FILLE DROP CONSTRAINT ck_icf_sante;

ALTER TABLE BONCOMMANDE_POUSSIN_FILLE    DROP CONSTRAINT ck_bcpf_sx;
ALTER TABLE BONRECEPTION_POUSSIN_FILLE   DROP CONSTRAINT ck_brpf_sx;
ALTER TABLE INSERTION_COMPARTIMENT_FILLE DROP CONSTRAINT ck_icf_sx;

ALTER TABLE BONCOMMANDE_POUSSIN_FILLE    DROP CONSTRAINT ck_bcpf_qte;
ALTER TABLE BONRECEPTION_POUSSIN_FILLE   DROP CONSTRAINT ck_brpf_qte;
ALTER TABLE INSERTION_COMPARTIMENT_FILLE DROP CONSTRAINT ck_icf_qte;

ALTER TABLE BONCOMMANDE_POUSSIN_FILLE    DROP CONSTRAINT ck_bcpf_rem;
ALTER TABLE BONCOMMANDE_POUSSIN_FILLE    DROP CONSTRAINT ck_bcpf_tva;
ALTER TABLE BONCOMMANDE_POUSSIN_FILLE    DROP CONSTRAINT ck_bcpf_tx;
ALTER TABLE BONRECEPTION_POUSSIN_FILLE   DROP CONSTRAINT ck_brpf_tx;


-- ============================================================================
-- 3. CONTRAINTES UNIQUE
-- ============================================================================
ALTER TABLE BONCOMMANDE_POUSSIN    DROP CONSTRAINT uk_bcp_ref;
ALTER TABLE BONRECEPTION_POUSSIN   DROP CONSTRAINT uk_brp_ref;
ALTER TABLE INSERTION_COMPARTIMENT DROP CONSTRAINT uk_ic_ref;


-- ============================================================================
-- 4. CLÉS ÉTRANGÈRES (filles d'abord, mères ensuite)
-- ============================================================================

-- Insertion compartiment fille
ALTER TABLE INSERTION_COMPARTIMENT_FILLE DROP CONSTRAINT fk_icf_mr;
ALTER TABLE INSERTION_COMPARTIMENT_FILLE DROP CONSTRAINT fk_icf_bcf;
ALTER TABLE INSERTION_COMPARTIMENT_FILLE DROP CONSTRAINT fk_icf_brf;
ALTER TABLE INSERTION_COMPARTIMENT_FILLE DROP CONSTRAINT fk_icf_cmp;
ALTER TABLE INSERTION_COMPARTIMENT_FILLE DROP CONSTRAINT fk_icf_lot;
ALTER TABLE INSERTION_COMPARTIMENT_FILLE DROP CONSTRAINT fk_icf_sx;

-- Insertion compartiment mère
ALTER TABLE INSERTION_COMPARTIMENT DROP CONSTRAINT fk_ic_lr;
ALTER TABLE INSERTION_COMPARTIMENT DROP CONSTRAINT fk_ic_br;

-- BR poussin fille
ALTER TABLE BONRECEPTION_POUSSIN_FILLE DROP CONSTRAINT fk_brpf_mr;
ALTER TABLE BONRECEPTION_POUSSIN_FILLE DROP CONSTRAINT fk_brpf_prd;
ALTER TABLE BONRECEPTION_POUSSIN_FILLE DROP CONSTRAINT fk_brpf_sx;
ALTER TABLE BONRECEPTION_POUSSIN_FILLE DROP CONSTRAINT fk_brpf_bcf;

-- BR poussin mère
ALTER TABLE BONRECEPTION_POUSSIN DROP CONSTRAINT fk_brp_lr;
ALTER TABLE BONRECEPTION_POUSSIN DROP CONSTRAINT fk_brp_bc;
ALTER TABLE BONRECEPTION_POUSSIN DROP CONSTRAINT fk_brp_bp;
ALTER TABLE BONRECEPTION_POUSSIN DROP CONSTRAINT fk_brp_frn;

-- BC poussin fille
ALTER TABLE BONCOMMANDE_POUSSIN_FILLE DROP CONSTRAINT fk_bcpf_mr;
ALTER TABLE BONCOMMANDE_POUSSIN_FILLE DROP CONSTRAINT fk_bcpf_prd;
ALTER TABLE BONCOMMANDE_POUSSIN_FILLE DROP CONSTRAINT fk_bcpf_sx;

-- BC poussin mère
ALTER TABLE BONCOMMANDE_POUSSIN DROP CONSTRAINT fk_bcp_frn;
ALTER TABLE BONCOMMANDE_POUSSIN DROP CONSTRAINT fk_bcp_dev;


-- ============================================================================
-- 5. INDEX (hors PK)
-- ============================================================================
DROP INDEX idx_bcp_daty;
DROP INDEX idx_bcp_frn;
DROP INDEX idx_bcp_etat;
DROP INDEX idx_bcpf_mr;
DROP INDEX idx_bcpf_prd;

DROP INDEX idx_brp_daty;
DROP INDEX idx_brp_bc;
DROP INDEX idx_brp_bp;
DROP INDEX idx_brp_etat;
DROP INDEX idx_brp_lr;
DROP INDEX idx_brpf_mr;
DROP INDEX idx_brpf_prd;
DROP INDEX idx_brpf_bcf;

DROP INDEX idx_ic_daty;
DROP INDEX idx_ic_br;
DROP INDEX idx_ic_etat;
DROP INDEX idx_icf_mr;
DROP INDEX idx_icf_cmp;
DROP INDEX idx_icf_brf;


-- ============================================================================
-- 6. TABLES (filles d'abord, puis mères)
-- ============================================================================
DROP TABLE INSERTION_COMPARTIMENT_FILLE;
DROP TABLE INSERTION_COMPARTIMENT;
DROP TABLE BONRECEPTION_POUSSIN_FILLE;
DROP TABLE BONRECEPTION_POUSSIN;
DROP TABLE BONCOMMANDE_POUSSIN_FILLE;
DROP TABLE BONCOMMANDE_POUSSIN;
DROP TABLE LIEURECEPTION;


-- ============================================================================
-- 7. SÉQUENCES
-- ============================================================================
DROP SEQUENCE GETSEQLIEURECEPTION;
DROP SEQUENCE GETSEQBCPOUSSIN;
DROP SEQUENCE GETSEQBCPOUSSINFILLE;
DROP SEQUENCE GETSEQBRPOUSSIN;
DROP SEQUENCE GETSEQBRPOUSSINFILLE;
DROP SEQUENCE GETSEQINSERTIONCOMP;
DROP SEQUENCE GETSEQINSERTIONCOMPFILLE;


-- ============================================================================
-- 8. MENU
-- ============================================================================
DELETE FROM usermenu
WHERE IDMENU IN ('MENUPOUSSON','MENUPOUSSON_BC','MENUPOUSSON_BR','MENUPOUSSON_IC');

DELETE FROM MENUDYNAMIQUE
WHERE ID IN ('MENUPOUSSON_BC','MENUPOUSSON_BR','MENUPOUSSON_IC','MENUPOUSSON');

COMMIT;