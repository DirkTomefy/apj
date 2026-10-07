-- ============================================================================
--  MODULE POUSSIN — Correction des menus
--  À exécuter pour synchroniser MENUDYNAMIQUE + usermenu
-- ============================================================================

-- 1) Nettoyage des anciens menus poussin
DELETE FROM usermenu     WHERE IDMENU LIKE 'MENUPOUSSON%';
DELETE FROM MENUDYNAMIQUE WHERE ID    LIKE 'MENUPOUSSON%';
COMMIT;


-- 2) Insertion des menus corrects
-- ----------------------------------------------------------------------------
-- 2.1. Menu parent
-- ----------------------------------------------------------------------------

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON', 'Poussins', 'egg',
        'module.jsp?but=a_tahina/poussin/boncommande-poussin-liste.jsp',
        50, 1, NULL);

-- ----------------------------------------------------------------------------
-- 2.2. Sous-menus
-- ----------------------------------------------------------------------------

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON_BC', 'Bons de commande', 'shopping_cart',
        'module.jsp?but=a_tahina/poussin/boncommande-poussin-liste.jsp',
        1, 2, 'MENUPOUSSON');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON_BR', 'Bons de réception', 'inventory',
        'module.jsp?but=a_tahina/poussin/bonreception-poussin-liste.jsp',
        2, 2, 'MENUPOUSSON');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON_IC', 'Insertions compartiment', 'grid_view',
        'module.jsp?but=a_tahina/poussin/insertion-compartiment-liste.jsp',
        3, 2, 'MENUPOUSSON');

INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUPOUSSON_CMP', 'Compartiments', 'meeting_room',
        'module.jsp?but=a_tahina/configuration/compartiment-liste.jsp',
        4, 2, 'MENUPOUSSON');

COMMIT;


-- 3) Rattachement au rôle "resp"
-- ----------------------------------------------------------------------------

INSERT INTO usermenu (id, IDMENU, idrole, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUPOUSSON',     'resp', 0);

INSERT INTO usermenu (id, IDMENU, idrole, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUPOUSSON_BC',  'resp', 0);

INSERT INTO usermenu (id, IDMENU, idrole, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUPOUSSON_BR',  'resp', 0);

INSERT INTO usermenu (id, IDMENU, idrole, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUPOUSSON_IC',  'resp', 0);

INSERT INTO usermenu (id, IDMENU, idrole, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUPOUSSON_CMP', 'resp', 0);

COMMIT;


-- ============================================================================
-- 4) VÉRIFICATION
-- ============================================================================

-- 4.1. Menus (5 lignes)
SELECT id, libelle, niveau, rang, href FROM MENUDYNAMIQUE
WHERE id LIKE 'MENUPOUSSON%' ORDER BY niveau, rang;

-- 4.2. usermenu (5 lignes)
SELECT id, IDMENU, idrole, INTERDIT FROM usermenu
WHERE IDMENU LIKE 'MENUPOUSSON%' ORDER BY IDMENU;

-- ============================================================================
-- FIN
-- ============================================================================