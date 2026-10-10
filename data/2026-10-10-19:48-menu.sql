INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON', 'Poussins', 'egg',
        'module.jsp?but=a_tahina/v1/bc/boncommande-liste.jsp',
        50, 1, NULL);

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON_BC', 'Bons de commande', 'shopping_cart',
        'module.jsp?but=a_tahina/v1/bc/boncommande-liste.jsp',
        1, 2, 'MENUPOUSSON');

INSERT INTO USERMENU (ID, IDMENU , IDROLE , INTERDIT)
VALUES  (sequsermenu.nextval ,'MENUPOUSSON' ,'resp',0 );

INSERT INTO USERMENU (ID, IDMENU , IDROLE , INTERDIT)
VALUES  (sequsermenu.nextval ,'MENUPOUSSON_BC' ,'resp',0 );