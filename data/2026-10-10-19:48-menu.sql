-- ============================================================================
--  MENU POUSSIN — Niveau 1/2/3
--  BC  : Saisir + Liste
--  BR  : Saisir + Liste
--  IC  : Saisir + Liste
-- ============================================================================

-- ---------------------------------------------------------------------------
-- 1) Menu racine "Poussins" (niveau 1)
-- ---------------------------------------------------------------------------
INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON', 'Poussins', 'egg',
        'module.jsp?but=a_tahina/v1/bc/boncommande-liste.jsp',
        50, 1, NULL);

-- ---------------------------------------------------------------------------
-- 2) Sous-menus niveau 2
-- ---------------------------------------------------------------------------
INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON_BC', 'Bons de commande', 'shopping_cart',
        'module.jsp?but=a_tahina/v1/bc/boncommande-liste.jsp',
        1, 2, 'MENUPOUSSON');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON_BR', 'Bons de r&eacute;ception', 'inventory_2',
        'module.jsp?but=a_tahina/v1/br/bonreception-liste.jsp',
        2, 2, 'MENUPOUSSON');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON_IC', 'Insertions compartiment', 'grid_view',
        'module.jsp?but=a_tahina/v1/ic/insertioncompartiment-liste.jsp',
        3, 2, 'MENUPOUSSON');

-- ---------------------------------------------------------------------------
-- 3) Sous-menus niveau 3 — BC
-- ---------------------------------------------------------------------------
INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON_BC_ADD', 'Saisir un bon de commande', 'add_box',
        'module.jsp?but=a_tahina/v1/bc/boncommande-saisie.jsp',
        1, 3, 'MENUPOUSSON_BC');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON_BC_LIST', 'Liste des BC', 'list',
        'module.jsp?but=a_tahina/v1/bc/boncommande-liste.jsp',
        2, 3, 'MENUPOUSSON_BC');

-- ---------------------------------------------------------------------------
-- 4) Sous-menus niveau 3 — BR
-- ---------------------------------------------------------------------------
INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON_BR_ADD', 'Saisir une r&eacute;ception', 'add_box',
        'module.jsp?but=a_tahina/v1/br/bonreception-saisie.jsp',
        1, 3, 'MENUPOUSSON_BR');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON_BR_LIST', 'Liste des BR', 'list',
        'module.jsp?but=a_tahina/v1/br/bonreception-liste.jsp',
        2, 3, 'MENUPOUSSON_BR');

-- ---------------------------------------------------------------------------
-- 5) Sous-menus niveau 3 — IC
-- ---------------------------------------------------------------------------
INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON_IC_ADD', 'Saisir une insertion', 'add_box',
        'module.jsp?but=a_tahina/v1/ic/insertioncompartiment-saisie.jsp',
        1, 3, 'MENUPOUSSON_IC');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON_IC_LIST', 'Liste des IC', 'list',
        'module.jsp?but=a_tahina/v1/ic/insertioncompartiment-liste.jsp',
        2, 3, 'MENUPOUSSON_IC');

-- ---------------------------------------------------------------------------
-- 6) Droits d'accès (rôle "resp")
-- ---------------------------------------------------------------------------
INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUPOUSSON',          'resp', 0);

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUPOUSSON_BC',       'resp', 0);

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUPOUSSON_BR',       'resp', 0);

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUPOUSSON_IC',       'resp', 0);

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUPOUSSON_BC_ADD',   'resp', 0);

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUPOUSSON_BC_LIST',  'resp', 0);

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUPOUSSON_BR_ADD',   'resp', 0);

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUPOUSSON_BR_LIST',  'resp', 0);

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUPOUSSON_IC_ADD',   'resp', 0);

INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUPOUSSON_IC_LIST',  'resp', 0);

COMMIT;

-- ============================================================================
--  Vérification
-- ============================================================================
SELECT m.ID, m.LIBELLE, m.NIVEAU, m.ID_PERE, m.RANG, m.HREF
FROM MENUDYNAMIQUE m
WHERE m.ID LIKE 'MENUPOUSSON%'
ORDER BY m.NIVEAU, m.RANG;

SELECT u.ID, u.IDMENU, u.IDROLE, u.INTERDIT
FROM USERMENU u
WHERE u.IDMENU LIKE 'MENUPOUSSON%'
ORDER BY u.IDMENU;