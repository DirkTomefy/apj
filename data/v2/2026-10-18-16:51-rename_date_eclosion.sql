SET DEFINE OFF
SET SERVEROUTPUT ON

PROMPT ============================================================
PROMPT RENAME - DATE_ECLOSION -> DATEECLOSION (sans underscore)
PROMPT ============================================================

-- 1. Vérifier l'état actuel
PROMPT >> Avant
SELECT table_name, column_name
FROM   user_tab_columns
WHERE  column_name IN ('DATE_ECLOSION', 'DATEECLOSION')
ORDER  BY table_name, column_name;

-- 2. Renommer dans BONRECEPTION_POUSSIN
BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE BONRECEPTION_POUSSIN RENAME COLUMN DATE_ECLOSION TO DATEECLOSION';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE = -904 THEN DBMS_OUTPUT.PUT_LINE('BONRECEPTION_POUSSIN : colonne DATE_ECLOSION inexistante, skip');
        ELSIF SQLCODE = -1430 THEN DBMS_OUTPUT.PUT_LINE('BONRECEPTION_POUSSIN : DATEECLOSION existe deja, skip');
        ELSE RAISE;
        END IF;
END;
/

-- 3. Renommer dans LOT
BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE LOT RENAME COLUMN DATE_ECLOSION TO DATEECLOSION';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE = -904 THEN DBMS_OUTPUT.PUT_LINE('LOT : colonne DATE_ECLOSION inexistante, skip');
        ELSIF SQLCODE = -1430 THEN DBMS_OUTPUT.PUT_LINE('LOT : DATEECLOSION existe deja, skip');
        ELSE RAISE;
        END IF;
END;
/

COMMIT;

-- 4. Vérification
PROMPT >> Apres
SELECT table_name, column_name
FROM   user_tab_columns
WHERE  table_name IN ('BONRECEPTION_POUSSIN', 'LOT')
  AND  column_name LIKE 'DATEECL%'
ORDER  BY table_name;

PROMPT ============================================================
PROMPT RENAME termine
PROMPT ============================================================