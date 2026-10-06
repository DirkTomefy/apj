CREATE USER arbiochemstandard IDENTIFIED BY arbiochemstandard;

ALTER USER arbiochemstandard QUOTA UNLIMITED ON USERS;

GRANT ALL PRIVILEGES TO arbiochemstandard;


GRANT DBA TO arbiochemstandard;


CREATE TABLE compartiment (id varchar2(50) primary key  , libelle varchar2(50) , capacite number(10,10));
CREATE sequence seq_compartiment;

create function getseqcompartiment return number is retour number ; begin select seq_compartiement.nextval into retour from dual ; return retour ; end;