SET DEFINE OFF
SET SQLBLANKLINES ON
SET SERVEROUTPUT ON
WHENEVER SQLERROR CONTINUE

PROMPT ============================================================
PROMPT DELETE ALL - Truncate de toutes les donnees
PROMPT ============================================================

-- ============================================================================
-- 1. Desactiver toutes les contraintes FK
-- ============================================================================
PROMPT [1/3] Desactivation des FK...

BEGIN
    FOR r IN (SELECT table_name, constraint_name
              FROM   user_constraints
              WHERE  constraint_type = 'R'
              AND    status = 'ENABLED')
    LOOP
        BEGIN
            EXECUTE IMMEDIATE 'ALTER TABLE "' || r.table_name
                || '" DISABLE CONSTRAINT "' || r.constraint_name || '"';
        EXCEPTION WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('SKIP ' || r.constraint_name || ': ' || SQLERRM);
        END;
    END LOOP;
END;
/

-- ============================================================================
-- 2. Truncate de toutes les tables (transactionnel + config)
-- ============================================================================
PROMPT [2/3] Truncate des tables...

TRUNCATE TABLE DECL_PERTE_LOT;
PROMPT   DECL_PERTE_LOT : OK

TRUNCATE TABLE LOT;
PROMPT   LOT : OK

TRUNCATE TABLE INSERTION_COMPARTIMENT_FILLE;
PROMPT   INSERTION_COMPARTIMENT_FILLE : OK

TRUNCATE TABLE INSERTION_COMPARTIMENT;
PROMPT   INSERTION_COMPARTIMENT : OK

TRUNCATE TABLE BONRECEPTION_POUSSIN;
PROMPT   BONRECEPTION_POUSSIN : OK

TRUNCATE TABLE BONCOMMANDE_POUSSIN_FILLE;
PROMPT   BONCOMMANDE_POUSSIN_FILLE : OK

TRUNCATE TABLE BONCOMMANDE_POUSSIN;
PROMPT   BONCOMMANDE_POUSSIN : OK

TRUNCATE TABLE CONFIG_PREVISION;
PROMPT   CONFIG_PREVISION : OK

TRUNCATE TABLE COMPARTIMENT;
PROMPT   COMPARTIMENT : OK

TRUNCATE TABLE SOUCHE;
PROMPT   SOUCHE : OK

-- ============================================================================
-- 3. Reactiver les contraintes FK
-- ============================================================================
PROMPT [3/3] Reactivation des FK...

BEGIN
    FOR r IN (SELECT table_name, constraint_name
              FROM   user_constraints
              WHERE  constraint_type = 'R'
              AND    status = 'DISABLED')
    LOOP
        BEGIN
            EXECUTE IMMEDIATE 'ALTER TABLE "' || r.table_name
                || '" ENABLE CONSTRAINT "' || r.constraint_name || '"';
        EXCEPTION WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('SKIP ' || r.constraint_name || ': ' || SQLERRM);
        END;
    END LOOP;
END;
/

-- ============================================================================
-- 4. Verification : tout doit etre a 0
-- ============================================================================
PROMPT ============================================================
PROMPT VERIFICATION
PROMPT ============================================================

SELECT 'DECL_PERTE_LOT' tbl, COUNT(*) nb FROM DECL_PERTE_LOT
UNION ALL SELECT 'LOT', COUNT(*) FROM LOT
UNION ALL SELECT 'INSERTION_COMPARTIMENT_FILLE', COUNT(*) FROM INSERTION_COMPARTIMENT_FILLE
UNION ALL SELECT 'INSERTION_COMPARTIMENT', COUNT(*) FROM INSERTION_COMPARTIMENT
UNION ALL SELECT 'BONRECEPTION_POUSSIN', COUNT(*) FROM BONRECEPTION_POUSSIN
UNION ALL SELECT 'BONCOMMANDE_POUSSIN_FILLE', COUNT(*) FROM BONCOMMANDE_POUSSIN_FILLE
UNION ALL SELECT 'BONCOMMANDE_POUSSIN', COUNT(*) FROM BONCOMMANDE_POUSSIN
UNION ALL SELECT 'CONFIG_PREVISION', COUNT(*) FROM CONFIG_PREVISION
UNION ALL SELECT 'COMPARTIMENT', COUNT(*) FROM COMPARTIMENT
UNION ALL SELECT 'SOUCHE', COUNT(*) FROM SOUCHE;

PROMPT ============================================================
PROMPT DELETE ALL termine
PROMPT ============================================================