# Créer un utilisateur et un menu

## 1. Créer le rôle

```sql
INSERT INTO roles (IDROLE, DESCROLE, RANG)
VALUES ('resp', 'MR TAHINA', 6);
```

* `IDROLE` : identifiant du rôle
* `DESCROLE` : nom du rôle
* `RANG` : ordre du rôle

---

## 2. Créer l'utilisateur

Se connecter en **administrateur** et créer l'utilisateur depuis l'interface d'administration.

> ⚠️ Les rôles sont mis en cache. **Redémarrer WildFly 10** après la création/modification du rôle.

---

## 3. Créer le menu

```sql
INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES (
    'MENUTAHIANA',
    'TAHINA',
    'add',
    'module.jsp?but=a_tahina/page.jsp',
    6,
    1,
    NULL
);
```

### À retenir

* `ID` : identifiant du menu
* `LIBELLE` : nom affiché
* `ICONE` : Material Icon
* `HREF` : page à ouvrir
* `RANG` : ordre d'affichage
* `NIVEAU` : `1` = menu principal, `2` = sous-menu, `3` = sous-menu
* `ID_PERE` : menu parent (`NULL` pour un menu principal)

> 📁 La page doit être dans `archiochem-war/web/pages` et le paramètre `but` est obligatoire.

---

## 4. Donner le menu au rôle

```sql
INSERT INTO usermenu (id, IDMENU, idrole, INTERDIT)
VALUES (
    sequsermenu.nextval,
    'MENUTAHIANA',
    'resp',
    0
);
```

* `IDMENU` : ID du menu
* `idrole` : ID du rôle
* `INTERDIT = 0` : accès autorisé

---

## Résumé

```text
1. Créer le rôle
       ↓
2. Créer l'utilisateur
       ↓
3. Redémarrer WildFly
       ↓
4. Créer le menu
       ↓
5. Associer le menu au rôle
```


# Les différents éléments de apj 


