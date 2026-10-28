SET DEFINE OFF

CREATE TABLE ETAT (
    id     VARCHAR2(30) PRIMARY KEY,
    val    VARCHAR2(100),
    desce  VARCHAR2(255)
);

-- accents via UNISTR : independant du jeu de caracteres du client SQL*Plus
INSERT INTO ETAT VALUES ('1',  'Cree',    UNISTR('Cr\00e9\00e9'));
INSERT INTO ETAT VALUES ('11', 'Vise',    UNISTR('Vis\00e9'));
INSERT INTO ETAT VALUES ('0',  'Annule',  UNISTR('Annul\00e9'));
INSERT INTO ETAT VALUES ('9',  'Cloture', UNISTR('Cl\00f4tur\00e9'));

COMMIT;

-- Verification
SELECT * FROM ETAT;
