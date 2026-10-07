-- ============================================================================
--  COMPARTIMENT — Création propre
--  Un compartiment est physiquement dans un BÂTIMENT.
--  Hiérarchie : Ferme → Bâtiment → Compartiment → Poussins
-- ============================================================================

-- 1. Séquence
CREATE SEQUENCE GETSEQCOMPARTIMENT START WITH 1;

-- 2. Table
CREATE TABLE COMPARTIMENT (
    id           VARCHAR2(30) PRIMARY KEY,
    libelle      VARCHAR2(100),
    capacite     NUMBER(10),
    idbatiment   VARCHAR2(30)
);

-- 3. FK vers BATIMENT
ALTER TABLE COMPARTIMENT
    ADD CONSTRAINT fk_cmp_bat FOREIGN KEY (idbatiment) REFERENCES BATIMENT(id);

-- 4. Index
CREATE INDEX idx_cmp_bat ON COMPARTIMENT (idbatiment);

-- 5. Vue avec libellé
CREATE OR REPLACE VIEW COMPARTIMENT_LIB AS
SELECT c.id,
       c.libelle,
       c.capacite,
       c.idbatiment,
       b.nombatiment AS batimentlib
FROM   COMPARTIMENT c
LEFT JOIN BATIMENT   b ON b.id = c.idbatiment;


-- Si la FK n'existe pas, on la crée :
ALTER TABLE INSERTION_COMPARTIMENT_FILLE
    ADD CONSTRAINT fk_icf_cmp FOREIGN KEY (idcompartiment)
        REFERENCES COMPARTIMENT(id);
        