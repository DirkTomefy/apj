SET DEFINE OFF
SET LINESIZE 200

-- ============================================================
-- Migration de DECL_PERTE_LOT
--   IDLOT      -> IDCOMPARTIMENT (repris via LOT.IDINSERTCOMPFILLE)
--   QTEMATY    -> QTEMORT
--   structure finale : ID, IDCOMPARTIMENT, SEXE, QTEMORT, QTEVERY, DATY
-- A lancer AVANT calendrier_poussin_vues_v2.sql.
-- Les colonnes REFERENCE et REMARQUE ne sont pas touchees (voir la fin).
-- ============================================================

PROMPT === 1. Sauvegarde ===
CREATE TABLE DECL_PERTE_LOT_BAK AS SELECT * FROM DECL_PERTE_LOT;

PROMPT === 2. Nouvelle colonne ===
ALTER TABLE DECL_PERTE_LOT ADD (IDCOMPARTIMENT VARCHAR2(30));

PROMPT === 3. Reprise des anciennes pertes : compartiment du lot ===
UPDATE DECL_PERTE_LOT p
SET    p.IDCOMPARTIMENT = (
           SELECT icf.IDCOMPARTIMENT
           FROM   LOT l
                  JOIN INSERTION_COMPARTIMENT_FILLE icf ON icf.ID = l.IDINSERTCOMPFILLE
           WHERE  l.ID = p.IDLOT);
COMMIT;

PROMPT === 4. Anciennes pertes dont le lot n'a pas de compartiment ===
PROMPT (si la liste n'est pas vide : corriger IDCOMPARTIMENT a la main avant l'etape 5 ;
PROMPT  l'ancien IDLOT reste lisible dans DECL_PERTE_LOT_BAK)
SELECT ID, IDLOT, DATY FROM DECL_PERTE_LOT WHERE IDCOMPARTIMENT IS NULL;

PROMPT === 5. Renommage, suppression de IDLOT, index ===
ALTER TABLE DECL_PERTE_LOT RENAME COLUMN QTEMATY TO QTEMORT;

-- Index cree par l'ancien script (ORA-01418 si absent : sans importance)
DROP INDEX IDX_DPL_LOT_SEXE_DATY;

ALTER TABLE DECL_PERTE_LOT DROP COLUMN IDLOT;

CREATE INDEX IDX_DPL_COMP_SEXE_DATY ON DECL_PERTE_LOT (IDCOMPARTIMENT, SEXE, DATY);

PROMPT === 6. Structure obtenue ===
DESC DECL_PERTE_LOT

-- ------------------------------------------------------------
-- OPTIONNEL, une fois la liste de l'etape 4 vide :
-- ALTER TABLE DECL_PERTE_LOT MODIFY (IDCOMPARTIMENT NOT NULL);
--
-- OPTIONNEL, pour obtenir exactement ID, IDCOMPARTIMENT, SEXE, QTEMORT, QTEVERY, DATY :
-- ALTER TABLE DECL_PERTE_LOT DROP (REFERENCE, REMARQUE);
-- ------------------------------------------------------------