# 08 — Inclure des onglets dans une fiche

Un onglet est un fichier `inc/xxx-detail.jsp` **inclus** par
`<jsp:include>` dans la fiche.

## 8.1. Côté fiche (rappel)

```jsp
<%
    Map<String,String> map = new HashMap<>();
    map.put("inc/monobjet-detail", "");
    map.put("inc/monobjet-historique", "");

    String tab = request.getParameter("tab");
    if (tab == null) tab = "inc/monobjet-detail";
    map.put(tab, "active");
    tab = tab + ".jsp";
%>

<ul class="nav nav-tabs">
  <li class="<%= map.get("inc/monobjet-detail") %>">
    <a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>
             &tab=inc/monobjet-detail">Détails</a></li>
  <li class="<%= map.get("inc/monobjet-historique") %>">
    <a href="<%= lien %>?but=<%= pageActuel %>&id=<%= id %>
             &tab=inc/monobjet-historique">Historique</a></li>
</ul>

<div class="tab-content">
  <jsp:include page="<%= tab %>">
    <jsp:param name="idMere" value="<%= id %>" />
  </jsp:include>
</div>
```

## 8.2. Onglet contenant un sous-tableau de type LISTE

**Fichier** : `pages/monpackage/inc/monobjet-detail.jsp`

```jsp
<%@page import="affichage.PageRecherche"%>
<%@page import="monpackage.MonObjetFilleCpl"%>
<% try {
    String idMere = request.getParameter("idMere");
    if (idMere == null) idMere = request.getParameter("id");

    // 1) Objet Cpl pour la fille
    MonObjetFilleCpl f = new MonObjetFilleCpl();
    f.setNomTable("MONOBJET_FILLE_CPL");
    f.setIdmere(idMere);

    // 2) Colonnes
    String[] listeCrt  = {"produitlib"};
    String[] libEntete = {"id","produitlib","quantite","pu","montant"};

    // 3) PageRecherche
    PageRecherche pr = new PageRecherche(f, request, listeCrt, null,
                                          10, libEntete, libEntete.length);
    pr.setLien((String) session.getValue("lien"));
    pr.setApres("monpackage/monobjet-fiche.jsp&id=" + idMere
                + "&tab=inc/monobjet-detail");

    // ⚠️ ESSENTIEL : filtrer par la mère
    pr.setAWhere(" and idmere = '" + idMere + "'");

    pr.creerObjetPage(libEntete, null);

    pr.getTableau().setLibelleAffiche(new String[]{
        "Id","Produit","Qté","P.U.","Montant"
    });
%>
<div class="tab-pane active">
  <form action="<%= pr.getLien() %>?but=<%= pr.getApres() %>" method="post">
    <% out.println(pr.getFormu().getHtmlEnsemble()); %>
  </form>

  <% out.println(pr.getTableau().getHtml());
     out.println(pr.getBasPage()); %>
</div>
<% } catch(Exception e){ e.printStackTrace(); } %>
```

## 8.3. Onglet de saisie de filles (rattachées à la mère)

```jsp
<%@page import="affichage.PageInsertMultiple"%>
<%@page import="monpackage.MonObjet"%>
<%@page import="monpackage.MonObjetFille"%>
<% try {
    user.UserEJB u = (user.UserEJB) session.getValue("u");
    String idMere = request.getParameter("idMere");

    MonObjet      mere  = new MonObjet();
    MonObjetFille fille = new MonObjetFille();
    int nombreLigne = 5;
    PageInsertMultiple pi = new PageInsertMultiple(mere, fille, request,
                                                    nombreLigne, u);
    pi.setLien((String) session.getValue("lien"));
    pi.getFormufle().setColOrdre(new String[]{"produit","quantite","pu"});
    pi.preparerDataFormu();
    pi.getFormufle().makeHtmlInsertTableauIndex();
%>
<div class="tab-pane active">
  <form action="<%= pi.getLien() %>?but=apresMultiple.jsp" method="post">
    <% out.println(pi.getFormufle().getHtmlTableauInsert()); %>

    <input type="hidden" name="acte"        value="insertFille" />
    <input type="hidden" name="bute"
           value="monpackage/monobjet-fiche.jsp&tab=inc/monobjet-detail" />
    <input type="hidden" name="idMere"      value="<%= idMere %>" />
    <input type="hidden" name="classe"      value="monpackage.MonObjet" />
    <input type="hidden" name="classefille" value="monpackage.MonObjetFille" />
    <input type="hidden" name="colonneMere" value="idmere" />
    <input type="hidden" name="nombreLigne" value="<%= nombreLigne %>" />
    <input type="hidden" name="nomtable"    value="MONOBJET_FILLE" />
  </form>
</div>
<% } catch(Exception e){ e.printStackTrace(); } %>
```

## Pièges

- **Toujours** filtrer par `idMere` (`setAWhere`), sinon toutes les
  données de la table apparaissent.
- L'id de la mère arrive soit par `idMere` (jsp:param), soit par `id`
  (query string). Gérer les deux cas.
- Ne jamais appeler `pageContext.getRequest().getParameter("id")` deux
  fois pour la même chose — utiliser une variable.
