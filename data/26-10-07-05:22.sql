DELETE FROM usermenu      WHERE IDMENU = 'MENUATAHINA_LR';
DELETE FROM MENUDYNAMIQUE WHERE ID     = 'MENUATAHINA_LR';
COMMIT;

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUATAHINA_LR', 'Lieux de réception', 'place',
        'module.jsp?but=a_tahina/configuration/lieureception-liste.jsp',
        11, 2, 'MENUATAHINA');

INSERT INTO usermenu (id, IDMENU, idrole, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUATAHINA_LR', 'resp', 0);
COMMIT;