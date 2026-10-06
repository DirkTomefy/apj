# 04 — Créer un écran FICHE

**Fichier** : `pages/monpackage/monobjet-fiche.jsp`

```jsp
<%@page import="affichage.PageConsulte"%>
<%@page import="monpackage.MonObjetCpl"%>
<%@page import="java.util.*"%>
<% try {
    user.UserEJB u = (user.UserEJB) session.getValue("u");
    String lien = (String) session.getValue("lien");

    // 1) Objet Cpl sur la vue SQL
    MonObjetCpl f = new MonObjetCpl();
    f.setNomTable("MONOBJET_CPL");

    // 2) PageConsulte(objet, request, user)
    PageConsulte pc = new PageConsulte(f, request, u);
    pc.setTitre("Fiche d'un objet");
    pc.getBase();                       // charge l'objet depuis request.getParameter("id")
    String id = pc.getBase().getTuppleID();

    // 3) Libellés
    pc.getChampByName("id").setLibelle("Id");
    pc.getChampByName("daty").setLibelle("Date");
    pc.getChampByName("libelle").setLibelle("Libellé");
    pc.getChampByName("etat").setLibelle("État");

    // 4) Masquer des colonnes techniques
    pc.getChampByName("idDevise").setVisible(false);
    pc.getChampByName("idClient").setVisible(false);
    // (souvent : on affiche idClientLib au lieu de idClient)

    // 5) Lien hypertexte sur un champ
    pc.getChampByName("iddmdachat").setLien(
        lien + "?but=facturefournisseur/dmdachat/dmdachat-fiche.jsp", "id=");

    // 6) Onglets
    Map<String,String> map = new HashMap<>();
    map.put("inc/monobjet-detail", "");          // onglet par défaut
    map.put("inc/monobjet-historique", "");
    String tab = request.getParameter("tab");
    if (tab == null) tab = "inc/monobjet-detail";
    map.put(tab, "active");
    tab = tab + ".jsp";

    // 7) Variables utiles
    MonObjetCpl o = (MonObjetCpl) pc.getBase();
    int etat = o.getEtat();
    String pageActuel = "monpackage/monobjet-fiche.jsp";
    String pageModif  = "monpackage/monobjet-modif.jsp";
    String classe     = "monpackage.MonObjet";
%>
<div class="content-wrapper">
  <h1 class="box-title">
    <a href="<%= lien %>?but=monpackage/monobjet-liste.jsp">
      <i class="fa fa-angle-left"></i></a>
    <%= pc.getTitre() %>
  </h1>

  <div class="row m-0">
    <div class="col-md-6">
      <div class="box-fiche"><div class="box"><div class="box-body">
        <% out.println(pc.getHtml()); %>
        <br>
        <div class="box-footer">
          <% if (etat < 11) { %>
            <a class="btn btn-primary pull-right"
               href="<%= lien %>?but=apresTarif.jsp&acte=valider&id=<%= id %>
                     &bute=<%= pageActuel %>&classe=<%= classe %>">Viser</a>
            <a class="btn btn-secondary pull-right"
               href="<%= lien %>?but=<%= pageModif %>&id=<%= id %>&acte=update">Modifier</a>
            <a class="btn btn-danger pull-left"
               href="<%= lien %>?but=apresTarif.jsp&acte=annuler&id=<%= id %>
                     &bute=<%= pageActuel %>&classe=<%= classe %>">Annuler</a>
          <% } %>
          <% if (etat >= 11) { %>
            <a class="btn btn-danger pull-left"
               href="<%= lien %>?but=apresTarif.jsp&acte=annulerVisa&id=<%= id %>
                     &bute=<%= pageActuel %>&classe=<%= classe %>">Annuler Visa</a>
          <% } %>
        </div>
      </div></div></div>
    </div>
  </div>

  <div class="row m-0">
    <div class="col-md-12 nopadding">
      <div class="nav-tabs-custom">
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
      </div>
    </div>
  </div>
</div>
<% } catch(Exception e){ e.printStackTrace(); } %>
```

## Points clés

- Le paramètre d'entrée est **toujours `id`** (`request.getParameter("id")`).
- `pc.getHtml()` génère le tableau **libellé / valeur**.
- Les boutons appellent `apresTarif.jsp` avec `acte=valider|annuler|…` et `classe=`.
- Le retour après action se fait via `bute=<page actuelle>&id=…`.
- Les onglets sont des `inc/*.jsp` inclus par `jsp:include`.
- On passe l'id à l'onglet via `<jsp:param name="idMere" value="…" />`.
