SET DEFINE OFF

-- ============================================================================
--  MENU FACTURE / AVOIR (a_tahina/v1/facture)
--  Niveau 1 : Facturation ; niveau 2 : Clients, Produits, Factures, Avoirs ;
--  niveau 3 : Saisir / Liste. Pas de « Saisir » pour les avoirs : on les cree
--  depuis la fiche d'une facture visee.
--  Droits donnes au role 'resp' (comme le menu Poussins).
--  Redemarrer WildFly apres l'import (menus et roles sont mis en cache).
-- ============================================================================

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUATHFACT', 'Facturation', 'receipt_long',
        'module.jsp?but=a_tahina/v1/facture/facture/facture-liste.jsp', 51, 1, NULL);

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUATHFACT_CLIENT', 'Clients', 'group',
        'module.jsp?but=a_tahina/v1/facture/client/client-liste.jsp', 1, 2, 'MENUATHFACT');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUATHFACT_PRODUIT', 'Produits', 'inventory_2',
        'module.jsp?but=a_tahina/v1/facture/produit/produit-liste.jsp', 2, 2, 'MENUATHFACT');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUATHFACT_FACTURE', 'Factures', 'receipt',
        'module.jsp?but=a_tahina/v1/facture/facture/facture-liste.jsp', 3, 2, 'MENUATHFACT');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUATHFACT_AVOIR', 'Avoirs', 'assignment_return',
        'module.jsp?but=a_tahina/v1/facture/avoir/avoir-liste.jsp', 4, 2, 'MENUATHFACT');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUATHFACT_CLIENT_ADD', 'Saisir un client', 'add_box',
        'module.jsp?but=a_tahina/v1/facture/client/client-saisie.jsp', 1, 3, 'MENUATHFACT_CLIENT');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUATHFACT_CLIENT_LIST', 'Liste des clients', 'list',
        'module.jsp?but=a_tahina/v1/facture/client/client-liste.jsp', 2, 3, 'MENUATHFACT_CLIENT');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUATHFACT_PRODUIT_ADD', 'Saisir un produit', 'add_box',
        'module.jsp?but=a_tahina/v1/facture/produit/produit-saisie.jsp', 1, 3, 'MENUATHFACT_PRODUIT');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUATHFACT_PRODUIT_LIST', 'Liste des produits', 'list',
        'module.jsp?but=a_tahina/v1/facture/produit/produit-liste.jsp', 2, 3, 'MENUATHFACT_PRODUIT');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUATHFACT_FACTURE_ADD', 'Saisir une facture', 'add_box',
        'module.jsp?but=a_tahina/v1/facture/facture/facture-saisie.jsp', 1, 3, 'MENUATHFACT_FACTURE');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUATHFACT_FACTURE_LIST', 'Liste des factures', 'list',
        'module.jsp?but=a_tahina/v1/facture/facture/facture-liste.jsp', 2, 3, 'MENUATHFACT_FACTURE');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUATHFACT_AVOIR_LIST', 'Liste des avoirs', 'list',
        'module.jsp?but=a_tahina/v1/facture/avoir/avoir-liste.jsp', 1, 3, 'MENUATHFACT_AVOIR');


INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT) VALUES (sequsermenu.nextval, 'MENUATHFACT', 'resp', 0);
INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT) VALUES (sequsermenu.nextval, 'MENUATHFACT_CLIENT', 'resp', 0);
INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT) VALUES (sequsermenu.nextval, 'MENUATHFACT_PRODUIT', 'resp', 0);
INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT) VALUES (sequsermenu.nextval, 'MENUATHFACT_FACTURE', 'resp', 0);
INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT) VALUES (sequsermenu.nextval, 'MENUATHFACT_AVOIR', 'resp', 0);
INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT) VALUES (sequsermenu.nextval, 'MENUATHFACT_CLIENT_ADD', 'resp', 0);
INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT) VALUES (sequsermenu.nextval, 'MENUATHFACT_CLIENT_LIST', 'resp', 0);
INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT) VALUES (sequsermenu.nextval, 'MENUATHFACT_PRODUIT_ADD', 'resp', 0);
INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT) VALUES (sequsermenu.nextval, 'MENUATHFACT_PRODUIT_LIST', 'resp', 0);
INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT) VALUES (sequsermenu.nextval, 'MENUATHFACT_FACTURE_ADD', 'resp', 0);
INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT) VALUES (sequsermenu.nextval, 'MENUATHFACT_FACTURE_LIST', 'resp', 0);
INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT) VALUES (sequsermenu.nextval, 'MENUATHFACT_AVOIR_LIST', 'resp', 0);

COMMIT;

-- ============================================================================
--  ANNULATION (a decommenter)
-- ============================================================================
-- DELETE FROM USERMENU WHERE IDMENU LIKE 'MENUATHFACT%';
-- DELETE FROM MENUDYNAMIQUE WHERE ID LIKE 'MENUATHFACT_%_%';
-- DELETE FROM MENUDYNAMIQUE WHERE ID LIKE 'MENUATHFACT_%';
-- DELETE FROM MENUDYNAMIQUE WHERE ID = 'MENUATHFACT';
-- COMMIT;
