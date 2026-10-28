-- ============================================================================
--  CORRECTIFS POUR UNE BASE DE DEVELOPPEMENT DEJA INSTALLEE PAR LES ANCIENS SCRIPTS
--  (inutile apres une installation par final.sql : tout est deja correct)
--
--  1. ETAT : les accents ont ete remplaces par des "?" a l'import (client SQL*Plus en ASCII).
--  2. Menu : "Bons de r;ception" / "Saisir une r;ception" (&eacute; pris pour une variable
--     de substitution a l'import du menu v1).
--  3. ATH_CLIENT.pourcentageavoir : le commentaire ecrit apres DEFAULT 40 a ete garde par Oracle
--     dans la valeur par defaut de la colonne.
--  4. Index du calendrier et cle etrangere des pertes (voir data/v2-fix/05-index-et-cle-etrangere.sql).
--
--  A lancer :  ./import_sql.sh data/final/correctifs-base-existante.sql
-- ============================================================================
SET DEFINE OFF
SET SQLBLANKLINES ON

-- 1. ETAT
UPDATE ETAT SET desce = UNISTR('Cr\00e9\00e9')     WHERE id = '1';
UPDATE ETAT SET desce = UNISTR('Vis\00e9')         WHERE id = '11';
UPDATE ETAT SET desce = UNISTR('Annul\00e9')       WHERE id = '0';
UPDATE ETAT SET desce = UNISTR('Cl\00f4tur\00e9')  WHERE id = '9';

-- 2. Menu
UPDATE MENUDYNAMIQUE SET libelle = 'Bons de r&eacute;ception'    WHERE id = 'MENUPOUSSON_BR';
UPDATE MENUDYNAMIQUE SET libelle = 'Saisir une r&eacute;ception' WHERE id = 'MENUPOUSSON_BR_ADD';

-- 3. Valeur par defaut propre
ALTER TABLE ATH_CLIENT MODIFY (pourcentageavoir DEFAULT 40);

COMMIT;

-- 4. Index + cle etrangere (rejouable)
BEGIN
    EXECUTE IMMEDIATE 'CREATE INDEX IDX_LOT_SEXE_DATY ON LOT (SEXE, DATEECLOSION)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-955, -1408) THEN RAISE; END IF;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE DECL_PERTE_LOT ADD CONSTRAINT FK_DPL_COMP FOREIGN KEY (IDCOMPARTIMENT) REFERENCES COMPARTIMENT (ID)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE <> -2275 THEN RAISE; END IF;
END;
/

PROMPT Correctifs appliques. Redemarrer WildFly pour le menu.
