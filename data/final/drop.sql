-- ============================================================================
--  DESINSTALLATION du module Poussin + Facture / Avoir
--
--  ATTENTION : SUPPRIME DEFINITIVEMENT les tables du module ET TOUTES LEURS DONNEES
--  (bons de commande / reception, lots, pertes, clients, factures, avoirs...).
--
--  Ne touche PAS aux objets de l'application de base, ni a la table ETAT (partagee),
--  ni aux colonnes ajoutees a SOUCHE.
--
--  Usage : ./import_sql.sh data/final/drop.sql      puis, pour reinstaller :
--          ./import_sql.sh data/final/final.sql
-- ============================================================================
SET DEFINE OFF
SET SERVEROUTPUT ON
SET FEEDBACK OFF

-- 1. Menu et droits
DELETE FROM USERMENU WHERE IDMENU LIKE 'MENUPOUSSON%' OR IDMENU LIKE 'MENUATHFACT%';
DELETE FROM MENUDYNAMIQUE WHERE (ID LIKE 'MENUPOUSSON%' OR ID LIKE 'MENUATHFACT%') AND NIVEAU = 3;
DELETE FROM MENUDYNAMIQUE WHERE (ID LIKE 'MENUPOUSSON%' OR ID LIKE 'MENUATHFACT%') AND NIVEAU = 2;
DELETE FROM MENUDYNAMIQUE WHERE (ID LIKE 'MENUPOUSSON%' OR ID LIKE 'MENUATHFACT%') AND NIVEAU = 1;
COMMIT;

-- 2. Objets : chaque suppression ignore l'objet absent
DECLARE
    PROCEDURE supprimer(p_type VARCHAR2, p_nom VARCHAR2) IS
    BEGIN
        EXECUTE IMMEDIATE 'DROP ' || p_type || ' ' || p_nom
                          || CASE p_type WHEN 'TABLE' THEN ' CASCADE CONSTRAINTS PURGE' ELSE '' END;
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE NOT IN (-942, -4043, -2289) THEN   -- table ou vue / objet / sequence inexistant
                RAISE;
            END IF;
    END;
BEGIN
    -- vues (de la plus dependante a la moins dependante)
    FOR r IN (SELECT column_value n FROM TABLE(sys.odcivarchar2list(
        'ATH_CLIENT_SOLDE', 'ATH_AVOIR_LIB', 'ATH_AVOIR_FILLE_LIB', 'ATH_FACTURE_LIB', 'ATH_FACTURE_FILLE_LIB',
        'CALENDRIER_POUSSIN_CUMUL', 'CALENDRIER_POUSSIN_RAW', 'V_CAL_PERTES_COUT', 'V_CAL_PERTES',
        'V_CAL_GEN2_PONTE', 'V_CAL_GEN1_PONTE', 'V_CAL_PONTE_JOUR', 'V_CAL_GENERATIONS',
        'V_CAL_LOT_INFO', 'V_CAL_INITIAUX', 'V_CAL_LOT_PU', 'V_CAL_K7', 'V_CAL_JOURS', 'V_CAL_BORNES',
        'V_CAL_SOUCHE', 'V_CAL_PARAM',
        'V_CAL_GEN2_HENS', 'V_CAL_GEN1_PONTE_DETAIL', 'V_CAL_GEN1_HENS', 'V_CAL_PONTE_JOUR_DETAIL',
        'SITUATION_COMPARTIMENT', 'LOT_LIB', 'INSERTION_COMP_FILLE_LIB', 'INSERTION_COMPARTIMENT_LIB',
        'BONRECEPTION_POUSSIN_LIB', 'BONCOMMANDE_POUSSIN_FILLE_LIB', 'BONCOMMANDE_POUSSIN_LIB',
        'CONFIG_PREVISION_LIB', 'DECL_PERTE_LOT_LIB', 'DECL_PERTE_LOT_CPL', 'BCPF_VIVANTS_LIB',
        'LIEURECEPTION_LIB', 'SANTEPOUSSIN_LIB', 'COMPARTIMENT_LIB'))) LOOP
        supprimer('VIEW', r.n);
    END LOOP;

    -- tables (filles avant meres)
    FOR r IN (SELECT column_value n FROM TABLE(sys.odcivarchar2list(
        'ATH_AVOIR_USAGE', 'ATH_AVOIR_FILLE', 'ATH_AVOIR', 'ATH_FACTURE_FILLE', 'ATH_FACTURE', 'ATH_PRODUIT', 'ATH_CLIENT',
        'DECL_PERTE_LOT', 'DECL_PERTE_LOT_BAK', 'LOT', 'INSERTION_COMPARTIMENT_FILLE', 'INSERTION_COMPARTIMENT',
        'BONRECEPTION_POUSSIN', 'BONCOMMANDE_POUSSIN_FILLE', 'BONCOMMANDE_POUSSIN',
        'CONFIG_PREVISION', 'COMPARTIMENT', 'LIEURECEPTION', 'SANTEPOUSSIN'))) LOOP
        supprimer('TABLE', r.n);
    END LOOP;

    -- fonctions GETSEQ et sequences
    FOR r IN (SELECT column_value n FROM TABLE(sys.odcivarchar2list(
        'GETSEQBCPOUSSIN', 'GETSEQBCPOUSSINFILLE', 'GETSEQBRPOUSSIN', 'GETSEQINSERTIONCOMP', 'GETSEQINSERTIONCOMPFILLE',
        'GETSEQCOMPARTIMENT', 'GETSEQSANTEPOUSSIN', 'GETSEQLIEURECEPTION', 'GETSEQLOT', 'GETSEQDECL_PERTE_LOT',
        'GETSEQCONFIG_PREVISION', 'GETSEQATHCLIENT', 'GETSEQATHPRODUIT', 'GETSEQATHFACTURE', 'GETSEQATHFACTUREFILLE',
        'GETSEQATHAVOIR', 'GETSEQATHAVOIRFILLE', 'GETSEQATHAVOIRUSAGE'))) LOOP
        supprimer('FUNCTION', r.n);
    END LOOP;
    FOR r IN (SELECT column_value n FROM TABLE(sys.odcivarchar2list(
        'SEQ_BCPOUSSIN', 'SEQ_BCPOUSSINFILLE', 'SEQ_BRPOUSSIN', 'SEQ_INSERTIONCOMP', 'SEQ_INSERTIONCOMPFILLE',
        'SEQ_COMPARTIMENT', 'SEQ_SANTEPOUSSIN', 'SEQ_LIEURECEPTION', 'SEQ_LOT', 'SEQ_DECL_PERTE_LOT',
        'SEQ_CONFIG_PREVISION', 'SEQATHCLIENT', 'SEQATHPRODUIT', 'SEQATHFACTURE', 'SEQATHFACTUREFILLE',
        'SEQATHAVOIR', 'SEQATHAVOIRFILLE', 'SEQATHAVOIRUSAGE'))) LOOP
        supprimer('SEQUENCE', r.n);
    END LOOP;
END;
/

PROMPT Module desinstalle (la table ETAT et les colonnes de SOUCHE sont conservees).
