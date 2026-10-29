SET DEFINE OFF
SET SQLBLANKLINES ON

-- ============================================================================
--  VENTE D'OEUFS ET DE POULETS (facture)
--  Une ligne de facture vend des oeufs (LIFO : les plus frais d'abord) ou des
--  poulets d'un sexe donne (FIFO : les plus vieux d'abord). Le stock et la
--  prevision sont calcules en Java (a_tahina.prevision.simulation.Elevage).
--  idproduit devient facultatif (anciennes lignes conservees).
-- ============================================================================

CREATE TABLE ATH_TYPEVENTE (
    id                 VARCHAR2(30) PRIMARY KEY,
    val                VARCHAR2(100),
    desce              VARCHAR2(255)
);
INSERT INTO ATH_TYPEVENTE (id, val, desce) VALUES ('OEUF', 'Oeuf', 'Vente d oeufs (les plus frais d abord)');
INSERT INTO ATH_TYPEVENTE (id, val, desce) VALUES ('POULET', 'Poulet', 'Vente de poulets (les plus vieux d abord)');
COMMIT;

ALTER TABLE ATH_FACTURE_FILLE ADD (typevente VARCHAR2(30), sexe VARCHAR2(1));
ALTER TABLE ATH_FACTURE_FILLE MODIFY (idproduit NULL);
ALTER TABLE ATH_FACTURE_FILLE
    ADD CONSTRAINT FK_ATHFF_TYPEVENTE FOREIGN KEY (TYPEVENTE)
        REFERENCES ATH_TYPEVENTE (ID);
ALTER TABLE ATH_FACTURE_FILLE
    ADD CONSTRAINT CK_ATHFF_ARTICLE CHECK (typevente IS NOT NULL OR idproduit IS NOT NULL);
ALTER TABLE ATH_FACTURE_FILLE
    ADD CONSTRAINT CK_ATHFF_SEXE CHECK (typevente IS NULL OR typevente <> 'POULET' OR sexe IN ('0', '1'));
CREATE INDEX idx_athff_typevente ON ATH_FACTURE_FILLE (typevente);

CREATE OR REPLACE VIEW ATH_FACTURE_FILLE_LIB AS
SELECT ff.id, ff.idmere, ff.idproduit, ff.typevente, ff.sexe, ff.qte, ff.pu, ff.tva,
       f.daty,
       f.etat                                      AS etatfacture,
       CASE ff.typevente
            WHEN 'OEUF'   THEN 'Oeuf'
            WHEN 'POULET' THEN CASE ff.sexe WHEN '0' THEN 'Poulet femelle' ELSE 'Poulet male' END
            ELSE p.libelle
       END                                         AS produitlib,
       ROUND(ff.qte * ff.pu, 2)                    AS montantht,
       ROUND(ff.qte * ff.pu * ff.tva / 100, 2)     AS montanttva,
       ROUND(ff.qte * ff.pu, 2)
         + ROUND(ff.qte * ff.pu * ff.tva / 100, 2) AS montantttc,
       NVL(r.qteretournee, 0)                      AS qteretournee,
       ff.qte - NVL(r.qteretournee, 0)             AS qtedisponible
FROM ATH_FACTURE_FILLE ff
JOIN ATH_FACTURE f ON f.id = ff.idmere
LEFT JOIN ATH_PRODUIT p ON p.id = ff.idproduit
LEFT JOIN (SELECT af.idfacturefille, SUM(af.qte) AS qteretournee
           FROM ATH_AVOIR_FILLE af
           JOIN ATH_AVOIR a ON a.id = af.idmere
           WHERE a.etat <> 0
           GROUP BY af.idfacturefille) r ON r.idfacturefille = ff.id;

CREATE OR REPLACE VIEW ATH_AVOIR_FILLE_LIB AS
SELECT af.id, af.idmere, af.idfacturefille, af.qte, af.pu, af.tva,
       ff.produitlib,
       ff.qte                                      AS qtefacturee,
       ROUND(af.qte * af.pu, 2)                    AS montantht,
       ROUND(af.qte * af.pu * af.tva / 100, 2)     AS montanttva,
       ROUND(af.qte * af.pu, 2)
         + ROUND(af.qte * af.pu * af.tva / 100, 2) AS montantttc
FROM ATH_AVOIR_FILLE af
LEFT JOIN ATH_FACTURE_FILLE_LIB ff ON ff.id = af.idfacturefille;

ALTER VIEW ATH_FACTURE_LIB COMPILE;
ALTER VIEW ATH_AVOIR_LIB COMPILE;
ALTER VIEW ATH_CLIENT_SOLDE COMPILE;
