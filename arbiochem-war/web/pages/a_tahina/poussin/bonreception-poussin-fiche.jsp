<%@page import="affichage.PageConsulte"%>
<%@page import="a_tahina.fronthelper.BonReceptionPoussinFH"%>
<%@page import="a_tahina.poussin.BonReceptionPoussinLib"%>
<% try {
    String lien = (String) session.getValue("lien");

    PageConsulte pc = BonReceptionPoussinFH.getPageConsulte(request, session);

    BonReceptionPoussinLib o = (BonReceptionPoussinLib) pc.getBase();
    String id   = o.getTuppleID();
    int    etat = o.getEtat();

    String pageActuel = "a_tahina/poussin/bonreception-poussin-fiche.jsp";
    String pageModif  = "a_tahina/poussin/bonreception-poussin-modif.jsp";
    String pageSaisie = "a_tahina/poussin/bonreception-poussin-saisie.jsp";
    String classe     = "a_tahina.poussin.BonReceptionPoussin";

    // URLs en une seule ligne (pas de retour à la ligne)
    String urlViser       = lien + "?but=apresTarif.jsp&acte=valider&id=" + id
                          + "&bute=" + pageActuel + "&classe=" + classe;
    String urlAnnuler     = lien + "?but=apresTarif.jsp&acte=annuler&id=" + id
                          + "&bute=" + pageActuel + "&classe=" + classe;
    String urlAnnulerVisa = lien + "?but=apresTarif.jsp&acte=annulerVisa&id=" + id
                          + "&bute=" + pageActuel + "&classe=" + classe;
    String urlModifier    = lien + "?but=" + pageModif + "&id=" + id + "&acte=update";

    String urlReReceptionner = lien + "?but=" + pageSaisie
                             + "&idbrprecedent=" + id;
%>
<div class="content-wrapper">
  <h1 class="box-title">
    <a href="<%= lien %>?but=a_tahina/poussin/bonreception-poussin-liste.jsp">
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
            <a class="btn btn-primary pull-right"   href="<%= urlViser %>">Viser</a>
            <a class="btn btn-secondary pull-right" href="<%= urlModifier %>">Modifier</a>
            <a class="btn btn-danger pull-left"     href="<%= urlAnnuler %>">Annuler</a>
          <% } %>

          <% if (etat == 11) { %>
            <%-- Re-réceptionner : crée un nouveau BR à partir de celui-ci --%>
            <a class="btn btn-success pull-right" href="<%= urlReReceptionner %>">
              <i class="material-symbols-rounded">inventory</i> Re-réceptionner
            </a>
            <a class="btn btn-danger pull-left" href="<%= urlAnnulerVisa %>">
              Annuler Visa
            </a>
          <% } %>

          <% if (etat == 0) { %>
            <span class="text-muted">Bon de réception annulé</span>
          <% } %>
        </div>
      </div></div></div>
    </div>
  </div>

  <%-- Détails --%>
  <div class="row m-0">
    <div class="col-md-12 nopadding">
      <div class="nav-tabs-custom">
        <div class="tab-content">
          <jsp:include page="inc/bonreception-poussin-fille-liste.jsp">
            <jsp:param name="idMere" value="<%= id %>" />
          </jsp:include>
        </div>
      </div>
    </div>
  </div>
</div>
<% } catch(Exception e){ e.printStackTrace(); } %>