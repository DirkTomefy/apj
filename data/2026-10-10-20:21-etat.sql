CREATE TABLE ETAT (
    id     VARCHAR2(30) PRIMARY KEY,
    val    VARCHAR2(100),
    desce  VARCHAR2(255)
);

INSERT INTO ETAT VALUES ('1',  'Cree',    'Créé');
INSERT INTO ETAT VALUES ('11', 'Vise',    'Visé');
INSERT INTO ETAT VALUES ('0',  'Annule',  'Annulé');
INSERT INTO ETAT VALUES ('9',  'Cloture', 'Clôturé');

COMMIT;

-- Vérification
SELECT * FROM ETAT;