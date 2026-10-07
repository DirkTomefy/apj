-- ============================================================================
--  MENU A_TAHINA — Module Poussin (isolé du module ferme)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. Nettoyage des anciennes entrées (si déjà créées)
-- ----------------------------------------------------------------------------
DELETE FROM usermenu      WHERE IDMENU LIKE 'MENUATAHINA%' OR IDMENU LIKE 'MENUPOUSS%';
DELETE FROM MENUDYNAMIQUE WHERE ID     LIKE 'MENUATAHINA%' OR ID     LIKE 'MENUPOUSS%';
COMMIT;

-- ----------------------------------------------------------------------------
-- 2. Menu principal A_TAHINA
-- ----------------------------------------------------------------------------
INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUATAHINA', 'A Tahina', 'science',
        'module.jsp?but=a_tahina/poussin/boncommande-poussin-liste.jsp',
        99, 1, NULL);

-- ----------------------------------------------------------------------------
-- 3. Sous-menu : Bon de commande poussin
-- ----------------------------------------------------------------------------
INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUATAHINA_BCP', 'Bons de commande poussin', 'shopping_cart',
        'module.jsp?but=a_tahina/poussin/boncommande-poussin-liste.jsp',
        1, 2, 'MENUATAHINA');

-- ----------------------------------------------------------------------------
-- 4. Sous-menu : Bon de réception poussin
-- ----------------------------------------------------------------------------
INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUATAHINA_BRP', 'Bons de réception poussin', 'inventory',
        'module.jsp?but=a_tahina/poussin/bonreception-poussin-liste.jsp',
        2, 2, 'MENUATAHINA');

-- ----------------------------------------------------------------------------
-- 5. Sous-menu : Insertion compartiment
-- ----------------------------------------------------------------------------
INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUATAHINA_ICP', 'Insertions compartiment', 'grid_view',
        'module.jsp?but=a_tahina/poussin/insertion-compartiment-liste.jsp',
        3, 2, 'MENUATAHINA');

-- ----------------------------------------------------------------------------
-- 6. Sous-menu : Lieux de réception (dictionnaire)
-- ----------------------------------------------------------------------------
INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES ('MENUATAHINA_LR', 'Lieux de réception', 'place',
        'module.jsp?but=a_tahina/poussin/lieureception-liste.jsp',
        9, 2, 'MENUATAHINA');

COMMIT;

-- ----------------------------------------------------------------------------
-- 7. Rattachement au rôle (à adapter selon ton rôle)
-- ----------------------------------------------------------------------------
-- Remplace 'resp' par ton rôle réel
INSERT INTO usermenu (id, IDMENU, idrole, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUATAHINA',     'resp', 0);
INSERT INTO usermenu (id, IDMENU, idrole, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUATAHINA_BCP', 'resp', 0);
INSERT INTO usermenu (id, IDMENU, idrole, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUATAHINA_BRP', 'resp', 0);
INSERT INTO usermenu (id, IDMENU, idrole, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUATAHINA_ICP', 'resp', 0);
INSERT INTO usermenu (id, IDMENU, idrole, INTERDIT)
VALUES (sequsermenu.nextval, 'MENUATAHINA_LR',  'resp', 0);
COMMIT;