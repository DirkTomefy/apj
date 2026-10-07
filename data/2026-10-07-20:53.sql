CREATE TABLE ETAT (
    id     VARCHAR2(30) PRIMARY KEY,
    val    VARCHAR2(100),
    desce  VARCHAR2(255)
);

INSERT INTO ETAT VALUES ('1',  'Créé',    'Créé');
INSERT INTO ETAT VALUES ('11', 'Visé',    'Visé');
INSERT INTO ETAT VALUES ('0',  'Annulé',  'Annulé');
INSERT INTO ETAT VALUES ('9',  'Clôturé', 'Clôturé');
COMMIT;