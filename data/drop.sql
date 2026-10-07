-- ============================================================================
--  MODULE POUSSIN — Script de DROP complet
-- ============================================================================
SET SERVEROUTPUT ON;

BEGIN
    -- ------------------------------------------------------------------------
    -- 1. Vues
    -- ------------------------------------------------------------------------
    FOR v IN (SELECT view_name FROM user_views
              WHERE view_name IN (
                  'BONCOMMANDE_POUSSIN_LIB',
                  'BONCOMMANDE_POUSSIN_FILLE_LIB',
                  'BONRECEPTION_POUSSIN_LIB',
                  'BONRECEPTION_POUSSIN_FILLE_LIB',
                  'COMPARTIMENT_LIB',
                  'INSERTION_COMPARTIMENT_LIB',
                  'INSERTION_COMP_FILLE_LIB',
                  'INSERTION_COMPARTIMENT_FILLE_LIB'
              ))
    LOOP
        BEGIN
            EXECUTE IMMEDIATE 'DROP VIEW ' || v.view_name;
            DBMS_OUTPUT.PUT_LINE('OK : DROP VIEW ' || v.view_name);
        EXCEPTION WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('SKIP: ' || v.view_name || ' -> ' || SQLERRM);
        END;
    END LOOP;

    -- ------------------------------------------------------------------------
    -- 2. Tables (filles d'abord, puis mères)
    -- ------------------------------------------------------------------------
    FOR t IN (SELECT table_name FROM user_tables
              WHERE table_name IN (
                  'INSERTION_COMPARTIMENT_FILLE',
                  'INSERTION_COMPARTIMENT',
                  'BONRECEPTION_POUSSIN_FILLE',
                  'BONRECEPTION_POUSSIN',
                  'BONCOMMANDE_POUSSIN_FILLE',
                  'BONCOMMANDE_POUSSIN',
                  'COMPARTIMENT',
                  'SANTEPOUSSIN',
                  'TYPERECEPTION'
              )
              ORDER BY CASE
                  WHEN table_name LIKE '%FILLE%' THEN 1
                  ELSE 2
              END)
    LOOP
        BEGIN
            EXECUTE IMMEDIATE 'DROP TABLE ' || t.table_name || ' CASCADE CONSTRAINTS';
            DBMS_OUTPUT.PUT_LINE('OK : DROP TABLE ' || t.table_name);
        EXCEPTION WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('SKIP: ' || t.table_name || ' -> ' || SQLERRM);
        END;
    END LOOP;

    -- ------------------------------------------------------------------------
    -- 3. Séquences internes
    -- ------------------------------------------------------------------------
    FOR s IN (SELECT sequence_name FROM user_sequences
              WHERE sequence_name IN (
                  'SEQ_BCPOUSSIN','SEQ_BCPOUSSINFILLE',
                  'SEQ_BRPOUSSIN','SEQ_BRPOUSSINFILLE',
                  'SEQ_INSERTIONCOMP','SEQ_INSERTIONCOMPFILLE',
                  'SEQ_COMPARTIMENT','SEQ_SANTEPOUSSIN','SEQ_TYPERECEPTION'
              ))
    LOOP
        BEGIN
            EXECUTE IMMEDIATE 'DROP SEQUENCE ' || s.sequence_name;
            DBMS_OUTPUT.PUT_LINE('OK : DROP SEQUENCE ' || s.sequence_name);
        EXCEPTION WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('SKIP: ' || s.sequence_name || ' -> ' || SQLERRM);
        END;
    END LOOP;

    -- ------------------------------------------------------------------------
    -- 4. Fonctions APJ
    -- ------------------------------------------------------------------------
    FOR f IN (SELECT object_name FROM user_objects
              WHERE object_type = 'FUNCTION'
                AND object_name IN (
                    'GETSEQBCPOUSSIN','GETSEQBCPOUSSINFILLE',
                    'GETSEQBRPOUSSIN','GETSEQBRPOUSSINFILLE',
                    'GETSEQINSERTIONCOMP','GETSEQINSERTIONCOMPFILLE',
                    'GETSEQCOMPARTIMENT','GETSEQSANTEPOUSSIN','GETSEQTYPERECEPTION'
                ))
    LOOP
        BEGIN
            EXECUTE IMMEDIATE 'DROP FUNCTION ' || f.object_name;
            DBMS_OUTPUT.PUT_LINE('OK : DROP FUNCTION ' || f.object_name);
        EXCEPTION WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('SKIP: ' || f.object_name || ' -> ' || SQLERRM);
        END;
    END LOOP;
END;
/


-- ============================================================================
-- 5. MENU
-- ============================================================================

-- 5.1. usermenu (rattachements d'abord)
DELETE FROM usermenu
WHERE IDMENU LIKE 'MENUPOUSSON%';

-- 5.2. Sous-menus (avant le parent)
DELETE FROM MENUDYNAMIQUE
WHERE id LIKE 'MENUPOUSSON%'
  AND id <> 'MENUPOUSSON';

-- 5.3. Menu parent
DELETE FROM MENUDYNAMIQUE
WHERE id = 'MENUPOUSSON';

COMMIT;


-- ============================================================================
-- 6. VÉRIFICATION — tout doit retourner 0 ligne
-- ============================================================================

SELECT table_name FROM user_tables
WHERE table_name IN (
    'BONCOMMANDE_POUSSIN','BONCOMMANDE_POUSSIN_FILLE',
    'BONRECEPTION_POUSSIN','BONRECEPTION_POUSSIN_FILLE',
    'COMPARTIMENT','INSERTION_COMPARTIMENT','INSERTION_COMPARTIMENT_FILLE',
    'SANTEPOUSSIN','TYPERECEPTION'
);

SELECT sequence_name FROM user_sequences
WHERE sequence_name IN (
    'SEQ_BCPOUSSIN','SEQ_BCPOUSSINFILLE',
    'SEQ_BRPOUSSIN','SEQ_BRPOUSSINFILLE',
    'SEQ_INSERTIONCOMP','SEQ_INSERTIONCOMPFILLE',
    'SEQ_COMPARTIMENT','SEQ_SANTEPOUSSIN','SEQ_TYPERECEPTION'
);

SELECT object_name FROM user_objects
WHERE object_type = 'FUNCTION'
  AND object_name IN (
    'GETSEQBCPOUSSIN','GETSEQBCPOUSSINFILLE',
    'GETSEQBRPOUSSIN','GETSEQBRPOUSSINFILLE',
    'GETSEQINSERTIONCOMP','GETSEQINSERTIONCOMPFILLE',
    'GETSEQCOMPARTIMENT','GETSEQSANTEPOUSSIN','GETSEQTYPERECEPTION'
  );

SELECT view_name FROM user_views
WHERE view_name LIKE '%POUSSIN%' OR view_name LIKE '%COMPARTIMENT%';

SELECT id FROM MENUDYNAMIQUE WHERE id LIKE 'MENUPOUSSON%';
SELECT id FROM usermenu      WHERE IDMENU LIKE 'MENUPOUSSON%';