ALTER TABLE INSERTION_COMPARTIMENT_FILLE
    ADD (idbcpf VARCHAR2(30));

ALTER TABLE INSERTION_COMPARTIMENT_FILLE
    ADD CONSTRAINT fk_icf_bcpf FOREIGN KEY (idbcpf)
    REFERENCES BONCOMMANDE_POUSSIN_FILLE(id);

CREATE INDEX idx_icf_bcpf ON INSERTION_COMPARTIMENT_FILLE (idbcpf);

CREATE OR REPLACE VIEW INSERTION_COMP_FILLE_LIB AS
SELECT icf.*,
       c.nom    AS compartimentlib,
       s.val    AS sexelib,
       sp.val   AS santelib,
       bcpf.idproduit     AS bcpfidproduit,
       ai.libelle         AS bcpfproduitlib,
       bcpf.idsouche      AS bcpfidsouche,
       so.val             AS bcpfsouchelibr,
       bcpf.qte           AS bcpfqte,
       bcpf.pu            AS bcpfpu
FROM INSERTION_COMPARTIMENT_FILLE icf
LEFT JOIN COMPARTIMENT c  ON c.id  = icf.idcompartiment
LEFT JOIN SEXE         s  ON s.id  = icf.sexe
LEFT JOIN SANTEPOUSSIN sp ON sp.id = icf.idsante
LEFT JOIN BONCOMMANDE_POUSSIN_FILLE bcpf ON bcpf.id = icf.idbcpf
LEFT JOIN AS_INGREDIENTS ai ON ai.id = bcpf.idproduit
LEFT JOIN SOUCHE         so ON so.id = bcpf.idsouche;

COMMIT;


ALTER TABLE INSERTION_COMPARTIMENT_FILLE 
    RENAME COLUMN valeur_compartiment TO valeurCompartiment;


-- ============================================================================
--  Correction : colonne valeur_compartiment → valeurCompartiment
--  APJ mappe les colonnes SQL directement aux attributs Java (pas de
--  conversion snake_case → camelCase)
-- ============================================================================

-- 1) Renommer la colonne (Oracle la stockera en VALEURCOMPARTIMENT, ce qui
--    matche l'attribut Java `valeurCompartiment` en insensible à la casse)
ALTER TABLE INSERTION_COMPARTIMENT_FILLE
    RENAME COLUMN valeur_compartiment TO valeurCompartiment;

-- 2) Recréer la vue avec le nouveau nom
CREATE OR REPLACE VIEW INSERTION_COMP_FILLE_LIB AS
SELECT icf.*,
       c.nom    AS compartimentlib,
       s.val    AS sexelib,
       sp.val   AS santelib,
       bcpf.idproduit     AS bcpfidproduit,
       ai.libelle         AS bcpfproduitlib,
       bcpf.idsouche      AS bcpfidsouche,
       so.val             AS bcpfsouchelibr,
       bcpf.qte           AS bcpfqte,
       bcpf.pu            AS bcpfpu
FROM INSERTION_COMPARTIMENT_FILLE icf
LEFT JOIN COMPARTIMENT c  ON c.id  = icf.idcompartiment
LEFT JOIN SEXE         s  ON s.id  = icf.sexe
LEFT JOIN SANTEPOUSSIN sp ON sp.id = icf.idsante
LEFT JOIN BONCOMMANDE_POUSSIN_FILLE bcpf ON bcpf.id = icf.idbcpf
LEFT JOIN AS_INGREDIENTS ai ON ai.id = bcpf.idproduit
LEFT JOIN SOUCHE         so ON so.id = bcpf.idsouche;

COMMIT;

-- 3) Vérification
SELECT column_name FROM user_tab_columns
WHERE table_name = 'INSERTION_COMPARTIMENT_FILLE'
ORDER BY column_id;




-- ============================================================================
--  Ajout d'une colonne "idproduitlib" dans BONCOMMANDE_POUSSIN_FILLE_LIB
--  Affiche : "BCPF001 - Poussin" pour un select plus lisible
-- ============================================================================

CREATE OR REPLACE VIEW BONCOMMANDE_POUSSIN_FILLE_LIB AS
SELECT bcpf.*,
       i.libelle AS produitlib,
       s.val     AS souchelibr,
       (bcpf.qte * bcpf.pu) AS montant,
       (bcpf.id || ' - ' || NVL(i.libelle, '?')) AS idproduitlib   -- ⚡ NOUVEAU
FROM BONCOMMANDE_POUSSIN_FILLE bcpf
LEFT JOIN AS_INGREDIENTS i ON i.id = bcpf.idproduit
LEFT JOIN SOUCHE         s ON s.id = bcpf.idsouche;

COMMIT;

