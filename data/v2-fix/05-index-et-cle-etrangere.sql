SET DEFINE OFF

-- ============================================================
-- Deux objets qui existaient en base sans etre dans aucun script :
--  * IDX_LOT_SEXE_DATY : index du calendrier (lots femelles par date d'eclosion)
--  * FK_DPL_COMP       : une perte declaree pointe un compartiment qui existe
-- Chaque bloc ignore l'objet deja present, donc le fichier est rejouable.
-- Si la cle etrangere echoue avec ORA-02298, des pertes pointent un compartiment
-- inexistant : les lister avec
--   SELECT * FROM DECL_PERTE_LOT WHERE idcompartiment IS NOT NULL
--     AND idcompartiment NOT IN (SELECT id FROM COMPARTIMENT);
-- ============================================================

BEGIN
    EXECUTE IMMEDIATE 'CREATE INDEX IDX_LOT_SEXE_DATY ON LOT (SEXE, DATEECLOSION)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-955, -1408) THEN   -- nom deja utilise / colonnes deja indexees
            RAISE;
        END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE DECL_PERTE_LOT ADD CONSTRAINT FK_DPL_COMP FOREIGN KEY (IDCOMPARTIMENT) REFERENCES COMPARTIMENT (ID)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE <> -2275 THEN   -- cette contrainte existe deja
            RAISE;
        END IF;
END;
/
