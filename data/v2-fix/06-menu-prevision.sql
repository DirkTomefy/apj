SET DEFINE OFF

-- ============================================================================
--  Menu "Prevision poussin" (page du calendrier) + droit du role resp.
--  Existait en base sans etre dans un script. Rejouable (pas de doublon).
--  Redemarrer WildFly ensuite (menus mis en cache).
-- ============================================================================

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
SELECT 'MENU_PREVISION_POUSSIN', 'Prevision poussin', 'egg_alt',
       'module.jsp?but=a_tahina/v1/prevision/prevision-poussin.jsp',
       1, 1, NULL
FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = 'MENU_PREVISION_POUSSIN');

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
SELECT sequsermenu.NEXTVAL, 'MENU_PREVISION_POUSSIN', 'resp', 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = 'MENU_PREVISION_POUSSIN' AND IDROLE = 'resp');

COMMIT;
