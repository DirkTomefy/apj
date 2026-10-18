<%@ page import="java.time.LocalDate, java.time.format.DateTimeFormatter" %>
<%@ page import="a_tahina.fronthelper.prevision.PrevisionPoussinFH" %>
<%@ page import="a_tahina.calendrier.child.CalendrierPoussin" %>
<%@ page import="a_tahina.prevision.Lot" %>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<style>
.box { border:1px solid #ddd; border-radius:8px; padding:12px; margin-bottom:14px; }
.box h4 { margin-top:0; }
.row-f { display:flex; gap:10px; align-items:end; flex-wrap:wrap; }
.msg-ok { color:#080; } .msg-err { color:#c00; }
.legend { display:flex; gap:14px; margin:8px 0; font-size:12px; }
.legend span { display:inline-block; padding:2px 8px; border:1px solid #999; }
.legend .dimanche { background:#fdd; }
.legend .rouge { background:#fbb; color:#800; font-weight:bold; }
.legend .gris { background:#eee; color:#333; }
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
    <form action="<%= lien %>" method="get" class="row-f">
      <input type="hidden" name="but" value="<%= bute %>">
      <div><label>Date debut</label><br>
        <input class="form-control" type="date" name="dateDebut"
               value="<%= cal.getDebut().format(iso) %>"></div>
      <div><label>Date fin</label><br>
        <input class="form-control" type="date" name="dateFin"
               value="<%= cal.getFin().format(iso) %>"></div>
      <div><label>Annee</label><br>
        <input class="form-control" type="number" name="annee"
               value="<%= cal.getAnneeAffichee() %>" style="width:100px"></div>
      <div><button class="btn btn-primary" type="submit">Afficher</button></div>
    </form>
  </div>

  <!-- ==================== Legende ==================== -->
  <div class="legend">
    <span class="dimanche">Dimanche</span>
    <span class="rouge">Perte declaree</span>
    <span class="gris">Aucune production</span>
    <span>Normal</span>
  </div>

  <!-- ==================== Formulaire : declarer une perte ==================== -->
  <div class="box">
    <h4>Declarer une perte (morts / perdus)</h4>
    <form action="<%= lien %>?but=apresTarif.jsp" method="post" class="row-f">
      <div>
        <label>Lot</label><br>
        <select class="form-control" name="idlot" required>
          <% for (Lot l : cal.getLotsFemelles()) { %>
            <option value="<%= l.getId() %>">
              <%= l.getId() %> - <%= l.getIdsouche() %> (init: <%= (long) l.getQteinit() %>, actuel: <%= (long) l.getQteActuelle() %>)
            </option>
          <% } %>
        </select>
      </div>
      <div>
        <label>Morts</label><br>
        <input class="form-control" type="number" min="0" name="qteMaty" value="0" required style="width:90px">
      </div>
      <div>
        <label>Perdus</label><br>
        <input class="form-control" type="number" min="0" name="qteVery" value="0" required style="width:90px">
      </div>
      <div>
        <label>Date</label><br>
        <input class="form-control" type="date" name="daty"
               value="<%= LocalDate.now().format(iso) %>" required>
      </div>
      <div>
        <label>Reference</label><br>
        <input class="form-control" type="text" name="reference" style="width:150px">
      </div>
      <div>
        <label>Remarque</label><br>
        <input class="form-control" type="text" name="remarque" style="width:200px">
      </div>
      <div>
        <button class="btn btn-danger" type="submit">Declarer</button>
      </div>
      <input type="hidden" name="acte"   value="insert" />
      <input type="hidden" name="classe" value="a_tahina.prevision.DeclPerteLot" />
      <input type="hidden" name="bute"   value="<%= bute %>" />
    </form>
  </div>

  <!-- ==================== Calendrier ==================== -->
  <%= cal.generateHtml() %>

</div>