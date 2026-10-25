SET DEFINE OFF

-- Entree de menu « Saisir un avoir » (menu Facturation > Avoirs).
-- A lancer apres 2026-10-25-12:30-menu_facture_avoir.sql ; redemarrer WildFly ensuite.
INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUATHFACT_AVOIR_ADD', 'Saisir un avoir', 'add_box',
        'module.jsp?but=a_tahina/v1/facture/avoir/avoir-saisie.jsp', 1, 3, 'MENUATHFACT_AVOIR');

-- « Liste des avoirs » passe en 2e position
UPDATE MENUDYNAMIQUE SET RANG = 2 WHERE ID = 'MENUATHFACT_AVOIR_LIST';

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUATHFACT_AVOIR_ADD', 'resp', 0);

COMMIT;

-- ANNULATION
-- DELETE FROM USERMENU WHERE IDMENU = 'MENUATHFACT_AVOIR_ADD';
-- DELETE FROM MENUDYNAMIQUE WHERE ID = 'MENUATHFACT_AVOIR_ADD';
-- UPDATE MENUDYNAMIQUE SET RANG = 1 WHERE ID = 'MENUATHFACT_AVOIR_LIST';
-- COMMIT;
