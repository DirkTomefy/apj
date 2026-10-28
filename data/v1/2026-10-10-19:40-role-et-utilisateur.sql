SET DEFINE OFF

-- ============================================================================
--  Role "resp" + utilisateur de connexion (a lancer avant le menu).
--  Crees seulement s'ils n'existent pas. Les roles sont mis en cache : redemarrer WildFly.
--
--  Compte de connexion : login  resp  /  mot de passe a saisir  Resp2026
--  A CHANGER apres la premiere connexion (administration des utilisateurs de l'application).
--
--  L'application ne stocke pas le mot de passe saisi : elle le chiffre avec les parametres de
--  PARAMCRYPT (une ligne par utilisateur : niveau, sens) et compare avec UTILISATEUR.PWDUSER.
--  Ici niveau = 4, sens = 1 (comme les autres utilisateurs) : "Resp2026" est stocke "naol2022".
--  Pour un autre mot de passe : le changer depuis l'application (elle chiffre toute seule), ou
--  calculer la valeur avec Utilitaire.cryptWord(mot, 4, 1) puis remplacer 'naol2022' ci-dessous.
-- ============================================================================

INSERT INTO ROLES (IDROLE, DESCROLE, RANG)
SELECT 'resp', 'MR TAHINA', 6
FROM dual WHERE NOT EXISTS (SELECT 1 FROM ROLES WHERE IDROLE = 'resp');

INSERT INTO UTILISATEUR (REFUSER, LOGINUSER, PWDUSER, NOMUSER, ADRUSER, TELUSER, IDROLE)
SELECT SEQ_UTILISATEUR.NEXTVAL, 'resp', 'naol2022', 'Responsable poussin et facturation', NULL, NULL, 'resp'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM UTILISATEUR WHERE LOGINUSER = 'resp');

-- sans cette ligne la connexion echoue ("Pas de cryptage associe")
INSERT INTO PARAMCRYPT (ID, NIVEAU, CROISSANTE, IDUTILISATEUR)
SELECT 'CRY' || LPAD(GETSEQPARAMCRYPT, 6, '0'), 4, 1, TO_CHAR(u.REFUSER)
FROM   UTILISATEUR u
WHERE  u.LOGINUSER = 'resp'
  AND  NOT EXISTS (SELECT 1 FROM PARAMCRYPT p WHERE p.IDUTILISATEUR = TO_CHAR(u.REFUSER));

COMMIT;

SELECT u.loginuser, u.idrole, p.niveau, p.croissante
FROM   UTILISATEUR u LEFT JOIN PARAMCRYPT p ON p.IDUTILISATEUR = TO_CHAR(u.REFUSER)
WHERE  u.loginuser = 'resp';
