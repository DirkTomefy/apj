<%@page import="affichage.PageConsulte"%>
<%@page import="a_tahina.fronthelper.BonCommandePoussinFH"%>
<%@page import="a_tahina.poussin.BonCommandePoussinLib"%>
<% try {
    String lien = (String) session.getValue("lien");

    PageConsulte pc = BonCommandePoussinFH.getPageConsulte(request, session);

    BonCommandePoussinLib o = (BonCommandePoussinLib) pc.getBase();
    String id   = o.getTuppleID();
    int    etat = o.getEtat();

    String pageActuel     = "a_tahina/poussin/boncommande-poussin-fiche.jsp";
    String pageModif      = "a_tahina/poussin/boncommande-poussin-modif.jsp";
    String pageReception  = "a_tahina/poussin/bonreception-poussin-saisie.jsp";
    String classe         = "a_tahina.poussin.BonCommandePoussin";

    String urlViser       = lien + "?but=apresTarif.jsp&acte=valider&id=" + id
                          + "&bute=" + pageActuel + "&classe=" + classe;
    String urlAnnuler     = lien + "?but=apresTarif.jsp&acte=annuler&id=" + id
                          + "&bute=" + pageActuel + "&classe=" + classe;
    String urlAnnulerVisa = lien + "?but=apresTarif.jsp&acte=annulerVisa&id=" + id
                          + "&bute=" + pageActuel + "&classe=" + classe;
    String urlModifier    = lien + "?but=" + pageModif + "&id=" + id + "&acte=update";

    String urlReceptionner = lien + "?but=" + pageReception
                           + "&idboncommande=" + id;
%>
<div class="content-wrapper">
  <h1 class="box-title">
    <a href="<%= lien %>?but=a_tahina/poussin/boncommande-poussin-liste.jsp">
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
            <%-- BC non visé : on peut viser, modifier, annuler --%>
            <a class="btn btn-primary pull-right"   href="<%= urlViser %>">Viser</a>
            <a class="btn btn-secondary pull-right" href="<%= urlModifier %>">Modifier</a>
            <a class="btn btn-danger pull-left"     href="<%= urlAnnuler %>">Annuler</a>
          <% } %>

          <% if (etat == 11) { %>
            <%-- BC visé : on peut réceptionner ou annuler le visa --%>
            <a class="btn btn-success pull-right" href="<%= urlReceptionner %>">
              <i class="material-symbols-rounded">inventory</i> Réceptionner
            </a>
            <a class="btn btn-danger pull-left"   href="<%= urlAnnulerVisa %>">
              Annuler Visa
            </a>
          <% } %>

          <% if (etat == 0) { %>
            <%-- BC annulé : aucune action --%>
            <span class="text-muted">Bon de commande annulé</span>
          <% } %>
        </div>

      </div></div></div>
    </div>
  </div>

  <%-- Détails (toujours affichés) --%>
  <div class="row m-0">
    <div class="col-md-12 nopadding">
      <div class="nav-tabs-custom">
        <div class="tab-content">
          <jsp:include page="inc/boncommande-poussin-fille-liste.jsp">
            <jsp:param name="idMere" value="<%= id %>" />
          </jsp:include>
        </div>
      </div>
    </div>
  </div>
</div>
<% } catch(Exception e){ e.printStackTrace(); } %>