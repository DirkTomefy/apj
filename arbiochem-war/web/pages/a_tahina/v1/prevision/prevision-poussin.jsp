<%@ page import="java.time.LocalDate, java.time.format.DateTimeFormatter" %>
<%@ page import="affichage.PageInsert" %>
<%@ page import="a_tahina.fronthelper.prevision.PrevisionPoussinFH" %>
<%@ page import="a_tahina.fronthelper.prevision.DeclPerteLotFH" %>
<%@ page import="a_tahina.calendrier.child.CalendrierPoussin" %>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<style>
.box { border:1px solid #ddd; border-radius:8px; padding:12px; margin-bottom:14px; }
.box h4 { margin-top:0; }
.row-f { display:flex; gap:10px; align-items:end; flex-wrap:wrap; }
.msg-ok { color:#080; font-weight:bold; padding:8px; background:#efe; border-left:4px solid #080; }
.msg-err { color:#c00; font-weight:bold; padding:8px; background:#fee; border-left:4px solid #c00; }
.legend { display:flex; gap:14px; margin:8px 0; font-size:12px; }
.legend span { display:inline-block; padding:2px 8px; border:1px solid #999; }
.legend .dimanche { background:#fdd; }
.legend .rouge { background:#fbb; color:#800; font-weight:bold; }
.legend .gris { background:#eee; color:#333; }
.legend .normal { background:#fff; }
</style>

<%
String lien = (String) session.getValue("lien");
String bute = "a_tahina/v1/prevision/prevision-poussin.jsp";
DateTimeFormatter iso = DateTimeFormatter.ofPattern("yyyy-MM-dd");

String flash = request.getParameter("msg");
boolean flashErr = "1".equals(request.getParameter("err"));

CalendrierPoussin cal = PrevisionPoussinFH.getCalendrier(request, session);
%>

<div class="content-wrapper">
  <section class="content-header">
    <h1><%= cal.getTitre() %></h1>
  </section>

  <% if (flash != null && !flash.isEmpty()) { %>
    <p class="<%= flashErr ? "msg-err" : "msg-ok" %>"><%= flash %></p>
  <% } %>

  <!-- ==================== Filtre periode ==================== -->
  <div class="box">
    <h4>Filtrer la p&eacute;riode</h4>
    <form action="<%= lien %>" method="get" class="row-f">
      <input type="hidden" name="but" value="<%= bute %>">
      <div><label>Date d&eacute;but</label><br>
        <input class="form-control" type="date" name="dateDebut"
               value="<%= cal.getDebut().format(iso) %>"></div>
      <div><label>Date fin</label><br>
        <input class="form-control" type="date" name="dateFin"
               value="<%= cal.getFin().format(iso) %>"></div>
      <div><label>Ann&eacute;e</label><br>
        <input class="form-control" type="number" name="annee"
               value="<%= cal.getAnneeAffichee() %>" style="width:100px"></div>
      <div><button class="btn btn-primary" type="submit">Afficher</button></div>
    </form>
  </div>



  <!-- ==================== Formulaire : declarer une perte ==================== -->
  <div class="box">
    <h4>D&eacute;clarer une perte (morts / perdus)</h4>
    <%
        PageInsert dplPi = DeclPerteLotFH.getPageInsert(request, session);
    %>
    <form action="<%= lien %>?but=apresTarif.jsp" method="post" class="row-f">
      <% out.println(dplPi.getFormu().getHtmlInsert()); %>

      <div>
        <label>&nbsp;</label>
        <button class="btn btn-danger" type="submit">D&eacute;clarer</button>
      </div>

      <input type="hidden" name="acte"   value="insert" />
      <input type="hidden" name="classe" value="a_tahina.prevision.DeclPerteLot" />
      <input type="hidden" name="bute"   value="<%= bute %>" />
    </form>
  </div>

  <!-- ==================== Calendrier ==================== -->
  <%= cal.generateHtml() %>

</div>