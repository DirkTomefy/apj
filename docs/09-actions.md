# 09 — Écrire une action personnalisée

Quand `apresTarif.jsp` (simple) ou `apresMultiple.jsp` (maître/détail) ne
suffisent pas, on écrit un **JSP d'action dédié**.

## 9.1. Structure type

**Fichier** : `pages/monpackage/apresMonAction.jsp`

```jsp
<%@page import="user.UserEJB"%>
<%@page import="monpackage.MonObjet"%>
<%
try {
    UserEJB u = (UserEJB) session.getValue("u");
    String lien = (String) session.getValue("lien");
    String id   = request.getParameter("id");
    String bute = request.getParameter("bute");

    // 1) Récupérer l'objet
    MonObjet o = (MonObjet) new MonObjet().getById(id, "MONOBJET", null);

    // 2) Appeler une méthode métier
    o.maMethodeMetier(u.getUser().getTuppleID());

    // 3) Rediriger
%>
<script>
    document.location.replace("<%= lien %>?but=<%= bute %>&id=<%= id %>");
</script>
<%
} catch (Exception e) {
    e.printStackTrace(); %>
    <script>
        alert('<%= new String(e.getMessage().getBytes(), "UTF-8") %>');
        history.back();
    </script>
<% } %>
```

## 9.2. Règles

1. **Toujours** `try / catch` — pas d'exception silencieuse.
2. **Toujours** afficher l'erreur : `alert(...)` + `history.back()`.
3. **Toujours** rediriger en cas de succès avec `document.location.replace`.
4. Pour l'UTF-8 : `new String(e.getMessage().getBytes(), "UTF-8")`.
5. Utiliser `Utilitaire.champNull(x)` pour éviter les `null` dans les URLs.

## 9.3. Action avec plusieurs ids (checkbox)

```jsp
<%@page import="user.UserEJB"%>
<%@page import="stock.MvtStock"%>
<%
try {
    UserEJB u = (UserEJB) session.getAttribute("u");
    String lien = request.getParameter("lien");
    String[] ids = request.getParameterValues("ids"); // tableau

    MvtStock mvt = new MvtStock();
    mvt.jeter(null, u.getUser().getTuppleID(), ids);
%>
<script>
    document.location.replace("<%= lien %>?but=stock/dechets-jeter-multiple.jsp");
</script>
<%
} catch (Exception e) {
    e.printStackTrace(); %>
    <script>
        alert('<%= e.getMessage() %>');
        history.back();
    </script>
<% } %>
```

## 9.4. Action avec plusieurs objets et transaction

```jsp
<%
Connection c = null;
try {
    UserEJB u = (UserEJB) session.getAttribute("u");
    c = new utilitaire.UtilDB().GetConn();
    c.setAutoCommit(false);

    for (String idFor : request.getParameterValues("ids")) {
        ComptaEcriture compta = (ComptaEcriture)
            new ComptaEcriture().getById(idFor, null, c);
        if (compta.getEtat() != ConstanteEtat.getEtatValider()) {
            compta.validerObject(String.valueOf(u.getUser().getRefuser()), c);
        }
    }
    c.commit();
%>
<script>
    document.location.replace("<%= session.getAttribute("lien") %>?but=<%= request.getParameter("bute") %>");
</script>
<%
} catch (Exception e) {
    if (c != null) try { c.rollback(); } catch (Exception ex) {}
    e.printStackTrace();
%>
<script>alert('<%= e.getMessage() %>'); history.back();</script>
<%
} finally {
    if (c != null) try { c.close(); } catch (Exception ex) {}
}
%>
```

## 9.5. Actions « post-formulaire » déjà fournies

Le framework fournit plusieurs JSP d'action génériques :

| Fichier | Sert à |
|---|---|
| `apresTarif.jsp` | Objet unique (insert, update, delete, valider, annuler, dupliquer, …) |
| `apresMultiple.jsp` | Maître/détail (insert, updateInsert, insertFille, deleteFille, interview, …) |
| `apresPopup.jsp` | Retour de popup avec `champReturn` |
| `apresSpecifique.jsp` | Actions métier ponctuelles (paie, etc.) |

## 9.6. Envoyer un formulaire vers `apresMonAction.jsp`

```html
<form action="<%= lien %>?but=monpackage/apresMonAction.jsp" method="post">
    <input type="hidden" name="bute" value="monpackage/monobjet-fiche.jsp" />
    ...
</form>
```

Le JSP d'action récupère `bute` et redirige.
