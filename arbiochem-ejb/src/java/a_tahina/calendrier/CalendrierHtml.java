package a_tahina.calendrier;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

import bean.ClassMAPTable;

/** Rendu HTML d'un Calendrier : uniquement des classes CSS, aucune couleur ici. */
final class CalendrierHtml<T extends ClassMAPTable> {

    static final String FICHIER_CSS = "/assets/css/a_tahina/calendrier.css";

    static final String CSS_CONTENEUR = "cal-conteneur";
    static final String CSS_TABLE     = "cal";
    static final String CSS_LEGENDE   = "cal-legende";
    static final String CSS_NUMERO    = "cal-numero";
    static final String CSS_CASE      = "cal-case";
    static final String CSS_HORS_MOIS = "cal-hors-mois";
    static final String CSS_DIMANCHE  = "cal-dimanche";
    static final String CSS_TOTAL     = "cal-total";
    static final String CSS_POSITIF   = "cal-positif";
    static final String CSS_NEGATIF   = "cal-negatif";

    private static final Locale FR = Locale.FRENCH;

    private final Calendrier<T> cal;
    private final StringBuilder sb = new StringBuilder();

    CalendrierHtml(Calendrier<T> cal) {
        this.cal = cal;
    }

    String rendu() throws Exception {
        sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"")
          .append(cal.getContextPath()).append(FICHIER_CSS).append("\" />\n");
        legende();
        sb.append("<div class=\"").append(CSS_CONTENEUR).append("\">\n")
          .append("<table class=\"").append(CSS_TABLE).append("\">\n");
        entete();
        corps();
        pied();
        sb.append("</table>\n</div>\n");
        return sb.toString();
    }

    private void legende() {
        sb.append("<div class=\"").append(CSS_LEGENDE).append("\">");
        for (Calendrier.Style<T> s : cal.getStyles()) {
            if (s.legende != null) span(s.classe, s.legende);
        }
        span(CSS_DIMANCHE, "Dimanche");
        sb.append("</div>\n");
    }

    private void span(String classe, String texte) {
        sb.append("<span class=\"").append(classe).append("\">").append(esc(texte)).append("</span>");
    }

    private void entete() {
        sb.append("<thead><tr><th class=\"").append(CSS_NUMERO).append("\">Jours</th>");
        for (int m = 1; m <= 12; m++) {
            sb.append("<th>Jour</th><th>").append(esc(nomMois(m))).append("</th>");
        }
        sb.append("</tr></thead>\n");
    }

    private void corps() throws Exception {
        sb.append("<tbody>\n");
        int annee = cal.getAnneeAffichee();
        for (int jour = 1; jour <= 31; jour++) {
            sb.append("<tr><td class=\"").append(CSS_NUMERO).append("\">").append(jour).append("</td>");
            for (int m = 1; m <= 12; m++) {
                if (jour > YearMonth.of(annee, m).lengthOfMonth()) {
                    deuxCases(CSS_HORS_MOIS, "-", CSS_HORS_MOIS, "-");
                    continue;
                }
                LocalDate date = LocalDate.of(annee, m, jour);
                T donnee = cal.jour(date);
                if (donnee == null) {
                    deuxCases(CSS_CASE, "-", CSS_CASE, "-");
                    continue;
                }
                String etat = cal.classeCss(donnee);
                String semaine = date.getDayOfWeek().getValue() == 7 ? CSS_DIMANCHE : etat;
                deuxCases(CSS_CASE + " " + semaine, jourSemaine(date),
                          CSS_CASE + " " + etat, nombre(cal.valeurCase(donnee)));
            }
            sb.append("</tr>\n");
        }
        sb.append("</tbody>\n");
    }

    private void pied() throws Exception {
        sb.append("<tfoot>\n");
        if (cal.getLibelleTotal() != null) {
            ligneTotal(cal.getLibelleTotal(), null, false);
        }
        for (Calendrier.Indicateur<T> ind : cal.getIndicateurs()) {
            ligneTotal(ind.libelle, ind, ind.signe);
        }
        sb.append("</tfoot>\n");
    }

    private void ligneTotal(String libelle, Calendrier.Indicateur<T> ind, boolean signe) throws Exception {
        sb.append("<tr class=\"").append(CSS_TOTAL).append("\"><td class=\"").append(CSS_NUMERO).append("\">")
          .append(esc(libelle)).append("</td>");
        for (int m = 1; m <= 12; m++) {
            double v = ind == null ? cal.totalMois(m) : cal.somme(m, ind.valeur);
            String classe = "";
            if (signe && v < -0.5) classe = CSS_NEGATIF;
            if (signe && v > 0.5)  classe = CSS_POSITIF;
            sb.append("<td colspan=\"2\" class=\"").append(classe).append("\">")
              .append(signe ? nombreSigne(v) : nombre(v)).append("</td>");
        }
        sb.append("</tr>\n");
    }

    private void deuxCases(String classe1, String texte1, String classe2, String texte2) {
        sb.append("<td class=\"").append(classe1).append("\">").append(esc(texte1)).append("</td>")
          .append("<td class=\"").append(classe2).append("\">").append(esc(texte2)).append("</td>");
    }

    private String nomMois(int m) {
        String court = YearMonth.of(cal.getAnneeAffichee(), m).getMonth().getDisplayName(TextStyle.SHORT, FR);
        return court.replace(".", "").toUpperCase(FR) + " " + cal.getAnneeAffichee();
    }

    private static String jourSemaine(LocalDate d) {
        return d.getDayOfWeek().getDisplayName(TextStyle.SHORT, FR).replace(".", "") + ".";
    }

    private static String nombre(double v) {
        return v <= 0 ? "-" : String.format("%,.0f", v);
    }

    private static String nombreSigne(double v) {
        return Math.abs(v) < 0.5 ? "-" : String.format("%,.0f", v);
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
