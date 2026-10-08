-- ============================================================================
--  Insertion de 4 compartiments
-- ============================================================================

INSERT INTO COMPARTIMENT (id, nom, capacite) VALUES ('CMP01', 'C1', 2000);
INSERT INTO COMPARTIMENT (id, nom, capacite) VALUES ('CMP02', 'C2', 2000);
INSERT INTO COMPARTIMENT (id, nom, capacite) VALUES ('CMP03', 'C3', 3000);
INSERT INTO COMPARTIMENT (id, nom, capacite) VALUES ('CMP04', 'C4', 4000);

COMMIT;

-- Vérification
SELECT id, nom, capacite FROM COMPARTIMENT ORDER BY id;