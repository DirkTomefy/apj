<%@page import="affichage.PageConsulte"%>
<%@page import="a_tahina.fronthelper.InsertionCompartimentFH"%>
<%@page import="a_tahina.poussin.InsertionCompartimentLib"%>
<% try {
    String lien = (String) session.getValue("lien");

    PageConsulte pc = InsertionCompartimentFH.getPageConsulte(request, session);

    InsertionCompartimentLib o = (InsertionCompartimentLib) pc.getBase();
    String id   = o.getTuppleID();
    int    etat = o.getEtat();

    String pageActuel = "a_tahina/poussin/insertion-compartiment-fiche.jsp";
    String pageModif  = "a_tahina/poussin/insertion-compartiment-modif.jsp";
    String classe     = "a_tahina.poussin.InsertionCompartiment";

    String urlViser       = lien + "?but=apresTarif.jsp&acte=valider&id=" + id
                          + "&bute=" + pageActuel + "&classe=" + classe;
    String urlAnnuler     = lien + "?but=apresTarif.jsp&acte=annuler&id=" + id
                          + "&bute=" + pageActuel + "&classe=" + classe;
    String urlAnnulerVisa = lien + "?but=apresTarif.jsp&acte=annulerVisa&id=" + id
                          + "&bute=" + pageActuel + "&classe=" + classe;
    String urlModifier    = lien + "?but=" + pageModif + "&id=" + id + "&acte=update";
%>
<div class="content-wrapper">
  <h1 class="box-title">
    <a href="<%= lien %>?but=a_tahina/poussin/insertion-compartiment-liste.jsp">
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
            <a class="btn btn-danger pull-left" href="<%= urlAnnulerVisa %>">
              Annuler Visa
            </a>
          <% } %>
          <% if (etat == 0) { %>
            <span class="text-muted">Insertion annulée</span>
          <% } %>
        </div>
      </div></div></div>
    </div>
  </div>

  <div class="row m-0">
    <div class="col-md-12 nopadding">
      <div class="nav-tabs-custom">
        <div class="tab-content">
          <jsp:include page="inc/insertion-compartiment-fille-liste.jsp">
            <jsp:param name="idMere" value="<%= id %>" />
          </jsp:include>
        </div>
      </div>
    </div>
  </div>
</div>
<% } catch(Exception e){ e.printStackTrace(); } %>