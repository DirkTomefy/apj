-- ============================================================================
--  DONNEES D'EXEMPLE (facultatif) : un client a 40 % d'avoir et deux produits.
--  A lancer apres final.sql :  ./import_sql.sh data/final/exemple-facture.sql
-- ============================================================================
SET DEFINE OFF

INSERT INTO ATH_CLIENT (id, nom, contact, adresse, pourcentageavoir)
VALUES ('ACL' || LPAD(GETSEQATHCLIENT, 6, '0'), 'Client Exemple', '034 00 000 00', 'Antananarivo', 40);

INSERT INTO ATH_PRODUIT (id, libelle, unite, pu)
VALUES ('APR' || LPAD(GETSEQATHPRODUIT, 6, '0'), 'Coca-Cola', 'bouteille', 2500);

INSERT INTO ATH_PRODUIT (id, libelle, unite, pu)
VALUES ('APR' || LPAD(GETSEQATHPRODUIT, 6, '0'), 'Poussin', 'piece', 1200);

COMMIT;
