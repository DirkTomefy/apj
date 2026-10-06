# 11 — Conventions & pièges

## 11.1. Conventions de nommage

| Convention | Signification |
|---|---|
| `id` | Paramètre d'URL standard pour la PK |
| `acte` | Action (`insert`, `update`, `delete`, `valider`, …) |
| `bute` | Page de redirection après action |
| `classe` | Nom Java complet du modèle (avec package) |
| `classefille` | Idem pour la fille |
| `nomtable` | Surcharge la table |
| `nombreLigne` | Nombre de lignes du tableau m/d |
| `colonneMere` | Nom de la FK dans la fille |
| `…libelle` | Champ d'affichage lecture seule associé à un champ id |
| Suffixe `_0`, `_1`, … | Ligne du tableau m/d |
| Suffixe `1`, `2` (`daty1`, `daty2`) | Intervalle min / max |
| `XxxCpl` | Vue SQL jointe en lecture seule |
| `ClassEtat` | Ajoute `etat` (1 créé, 11 validé, 0 annulé, 9 clôturé) |
| `ClassMere` | Objet maître |
| `ClassFille` | Objet détail |
| `TypeObjet` | Dictionnaire (id / val / desce) |

## 11.2. États standards

| Valeur | Constante | Signification |
|---|---|---|
| 1 | `getEtatCreer()` | Créé(e) |
| 11 | `getEtatValider()` | Traité(e) / Visé(e) / Validé(e) |
| 0 | `getEtatAnnuler()` | Annulé(e) |
| 9 | `getEtatCloture()` | Clôturé(e) |
| Autres | … | Planifié(e), Interviewé(e), Payé(e) |

## 11.3. Pièges fréquents

### 🔴 Le formulaire de liste doit s'appeler `name="filtre"`

```html
<form name="filtre" action="...">
```
Sinon le bouton « Rechercher » du framework ne le soumet pas
(`document.filtre.submit()` échoue silencieusement).

### 🔴 Les onglets ne sont pas filtrés automatiquement

Dans un `inc/*.jsp`, **toujours** filtrer par la mère :
```java
pr.setAWhere(" and idmere = '" + idMere + "'");
```
Sinon l'onglet affiche toute la table.

### 🔴 XSS : `getValeur()` n'échappe pas

`Champ.getValeur()` et `getValeurLibelle()` **ne font aucun échappement
HTML**. Si la donnée vient de l'utilisateur, échapper manuellement :
```java
String safe = valeur.replace("&","&amp;")
                    .replace("<","&lt;")
                    .replace(">","&gt;")
                    .replace("\"","&quot;");
```

### 🔴 `PageConsulte.getBase()` relit l'objet

Toute modification faite sur `f` **avant** `pc.getBase()` est perdue.
Modifier **après** `getBase()` :
```java
pc.getBase();
pc.getChampByName("libelle").setLibelle("Libellé");
```

### 🔴 `preparerDataFormu()` obligatoire avant `getChamp("xxx_0")`

Dans `PageInsertMultiple` / `PageUpdateMultiple`, accéder à un champ de
ligne (`produit_0`, `quantite_0`, …) sans avoir appelé
`preparerDataFormu()` lève une NPE.

### 🔴 `setAWhere()` concatène du SQL brut

```java
pr.setAWhere(" and etat = " + request.getParameter("etat"));
```
Si `etat` vient de l'utilisateur, **risque d'injection SQL**. Échapper
ou valider :
```java
String etat = request.getParameter("etat");
if (etat != null && etat.matches("\\d+"))
    pr.setAWhere(" and etat = " + etat);
```

### 🔴 `nomtable` sur `apresTarif.jsp`

Quand la même classe Java sert à plusieurs tables/vues (`Fiche` vs
`FicheCPL`), le paramètre `nomtable` est **indispensable** pour que le
framework sache où écrire :
```html
<input type="hidden" name="nomtable" value="MONOBJET" />
```

### 🔴 Redirection après action

`apresTarif.jsp` fait :
```js
document.location.replace(lien + "?but=" + bute + "&valeur=" + val + "&id=" + id);
```
Le `id` renvoyé peut être vide pour certaines actions. Toujours inclure
`&id=` explicitement dans `bute` si nécessaire.

### 🔴 Rôles en cache

Après ajout/modification d'un rôle dans `roles` ou `usermenu`, il faut
**redémarrer WildFly 10**.

### 🟡 Duplication de code

Les JSP sont souvent dupliqués pour changer 2-3 lignes (ex:
`bondecommande-fiche.jsp` vs `bondecommande-arbiochem-fiche.jsp`).
Préférer des paramètres d'URL ou une même JSP avec un flag.

### 🟡 `try / catch` autour de tout le JSP

Beaucoup de JSP englobent **tout** le code dans `try { ... } catch(Exception e) { e.printStackTrace(); }`.
Conséquence : en cas d'erreur, la page reste **blanche**.
Ajouter au minimum :
```jsp
<% } catch (Exception e) { %>
    <script>alert('<%= e.getMessage() %>'); history.back();</script>
<% } %>
```

### 🟡 Ne pas oublier les `@page` d'import

Les JSP n'utilisent pas de `<%@ taglib %>` mais des imports directs :
```jsp
<%@page import="affichage.*" %>
<%@page import="bean.*" %>
<%@page import="user.*" %>
<%@page import="utilitaire.*" %>
```

## 11.4. Checklist « créer un nouvel écran »

```
[ ] 1. Table SQL créée (et éventuellement vue CPL avec libellés)
[ ] 2. Classe modèle Java (extends ClassMAPTable | ClassEtat | ClassMere | ClassFille)
       - constructeur qui appelle setNomTable(...)
       - construirePK(c) avec preparePk(prefixe, sequence)
       - getTuppleID() et getAttributIDName()
       - getters / setters pour toutes les colonnes
       - (si maître) getNomClasseFille() + getLiaisonFille()
       - (si fille) getNomClasseMere() + getLiaisonMere()
[ ] 3. Classe Cpl correspondante sur la vue SQL
[ ] 4. JSP LISTE  → PageRecherche
[ ] 5. JSP FICHE  → PageConsulte
[ ] 6. JSP SAISIE → PageInsert ou PageInsertMultiple
[ ] 7. JSP MODIF  → PageUpdateMultiple
[ ] 8. Onglets (inc/*.jsp) si fiche à onglets
[ ] 9. Entrée MENUDYNAMIQUE + usermenu
[ ] 10. Redémarrer WildFly
```

## 11.5. Table récapitulative

| Je veux… | J'utilise… | Fichier de sortie |
|---|---|---|
| Afficher une liste | `PageRecherche` | `xxx-liste.jsp` |
| Afficher une fiche | `PageConsulte` | `xxx-fiche.jsp` |
| Saisir un objet simple | `PageInsert` | `xxx-saisie.jsp` |
| Saisir maître + détails | `PageInsertMultiple` | `xxx-saisie.jsp` |
| Modifier maître + détails | `PageUpdateMultiple` | `xxx-modif.jsp` |
| Afficher un sous-tableau | `PageRecherche` filtré | `inc/xxx-detail.jsp` |
| Bouton d'action standard | `apresTarif.jsp?acte=…` | — |
| Bouton d'action m/d | `apresMultiple.jsp?acte=…` | — |
| Action métier perso | JSP `apresXxx.jsp` | — |
| Déclarer dans le menu | SQL `MENUDYNAMIQUE` + `usermenu` | — |
