# 10 — Déclarer un menu

Pour exposer un nouvel écran dans la barre latérale, il faut insérer dans
`MENUDYNAMIQUE` **et** rattacher le menu au(x) rôle(s) concerné(s).

## 10.1. Créer le rôle

```sql
INSERT INTO roles (IDROLE, DESCROLE, RANG)
VALUES ('resp', 'MR TAHINA', 6);
```

> ⚠️ Les rôles sont **mis en cache** : redémarrer **WildFly 10** après
> toute modification.

## 10.2. Créer l'utilisateur

Se connecter en administrateur et créer l'utilisateur depuis l'interface
d'administration.

## 10.3. Créer le menu

```sql
INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES (
    'MENUMONOBJET',
    'Mon objet',
    'add',
    'module.jsp?but=monpackage/monobjet-liste.jsp',
    6,
    1,
    NULL
);
```

| Colonne | Description |
|---|---|
| `ID` | Identifiant unique du menu |
| `LIBELLE` | Nom affiché |
| `ICONE` | Nom d'une **Material Icon** (ex: `add`, `list`, `edit`) |
| `HREF` | URL à ouvrir — **le paramètre `but` est obligatoire** |
| `RANG` | Ordre d'affichage |
| `NIVEAU` | `1` = menu principal, `2` = sous-menu, `3` = sous-sous-menu |
| `ID_PERE` | ID du menu parent (`NULL` si menu principal) |

### Sous-menu

```sql
INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)
VALUES (
    'MENUMONOBJET_SAISIE',
    'Saisir',
    'add',
    'module.jsp?but=monpackage/monobjet-saisie.jsp',
    1,
    2,
    'MENUMONOBJET'
);
```

## 10.4. Rattacher le menu au rôle

```sql
INSERT INTO usermenu (id, IDMENU, idrole, INTERDIT)
VALUES (
    sequsermenu.nextval,
    'MENUMONOBJET',
    'resp',
    0   -- 0 = autorisé, 1 = interdit
);
```

## 10.5. Emplacement des JSP

Les JSP doivent être placés dans :
```
archiochem-war/web/pages/<monpackage>/<page>.jsp
```

Le paramètre `but` dans `HREF` est relatif à `pages/` :
```
module.jsp?but=monpackage/monobjet-liste.jsp
```

## 10.6. Résumé

```
1. Créer le rôle (SQL)
       ↓
2. Créer l'utilisateur (interface admin)
       ↓
3. Redémarrer WildFly 10
       ↓
4. Créer le menu (SQL MENUDYNAMIQUE)
       ↓
5. Associer le menu au rôle (SQL usermenu)
```

## 10.7. URL alternatives (accueil.jsp)

Certains écrans sont accessibles depuis `accueil.jsp` via des cartes :

```html
<a href="module.jsp?but=stock/etatstock/etatstock-liste.jsp"
   class="card col-md-4 vert">
    <img src="${pageContext.request.contextPath}/assets/img/stock.png"
         class="icon"/>
    <div class="card-content">
        <h3>État de Stock</h3>
        <p>Consultez l'état réel de votre stock.</p>
    </div>
</a>
```
