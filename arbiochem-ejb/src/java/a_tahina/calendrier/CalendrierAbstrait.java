package a_tahina.calendrier;

import java.time.LocalDate;
import java.util.*;

/**
 * Generateur de calendrier HTML.
 *
 * Une sous-classe doit fournir :
 *   - la liste des mois affiches (12 en general)
 *   - les lignes (1..31)
 *   - le contenu d'une cellule (jour, mois)
 *   - le total d'un mois
 *   - le libelle de la ligne de total
 *
 * Le HTML et le CSS sont entierement factorises ici.
 * Aucune logique metier : la sous-classe decide quoi afficher,
 * cette classe decide comment l'afficher.
 */
public abstract class CalendrierAbstrait {

    // ==================================================================
    // Contrat de la sous-classe
    // ==================================================================

    /** Titre du calendrier (ex: "Prevision globale des poussins"). */
    public abstract String getTitre();

    /** Liste des mois a afficher en colonnes. */
    public abstract List<Mois> getMoisList();

    /** Numero des lignes (ex: 1..31). */
    public abstract int[] getListeLignes();

    /** Retourne la cellule pour un jour et un mois donnes, ou null. */
    public abstract CelluleCalendrierAbstraite getCellule(int jour, Mois mois);

    /** Retourne la valeur totale d'un mois (pour la ligne de pied). */
    public abstract double getTotalMois(Mois mois);

    /** Libelle de la ligne de pied (ex: "TOTAL POUSSINS"). */
    public abstract String getLibelleTotal();

    // ==================================================================
    // Generation du HTML
    // ==================================================================
    public String generateHtml() {
        StringBuilder sb = new StringBuilder();

        // ---- CSS ----
        sb.append("<style>\n")
          .append(".table-container { max-width:100%; max-height:80vh; overflow:auto; ")
          .append("border:1px solid #ccc; margin-top:10px; }\n")

          .append("table.cal { border-collapse:collapse; width:100%; ")
          .append("table-layout:auto; min-width:fit-content; }\n")

          .append("table.cal th, table.cal td { border:1px solid #999; ")
          .append("padding:5px; font-size:12px; text-align:right; }\n")

          .append("table.cal th { background:#f2f2f2; text-align:center; ")
          .append("position:sticky; top:0; z-index:2; }\n")

          .append("table.cal td.jour-cell { text-align:center; }\n")

          .append("table.cal td.jour { text-align:left; position:sticky; ")
          .append("left:0; background:#fff; z-index:3; min-width:80px; ")
          .append("max-width:80px; white-space:nowrap; }\n")

          .append(".dimanche { background:#fdd; }\n")
          .append(".rouge { background:#fbb; color:#800; font-weight:bold; }\n")
          .append(".vert { background:#cfc; }\n")
          .append(".total { font-weight:bold; background:#eef; }\n")
          .append("</style>\n");

        // ---- Conteneur + table ----
        sb.append("<div class=\"table-container\">\n");
        sb.append("  <table class=\"cal\">\n");

        List<Mois> moisList = getMoisList();
        int[] lignes = getListeLignes();

        // ---- En-tete ----
        sb.append("    <thead>\n");
        sb.append("      <tr>\n");
        sb.append("        <th class=\"jour\">Jours</th>\n");
        for (Mois m : moisList) {
            sb.append("        <th class=\"jour-cell\">Jour</th>\n");
            sb.append("        <th class=\"jour-cell\">")
              .append(esc(m.libelleCourt))
              .append("</th>\n");
        }
        sb.append("      </tr>\n");
        sb.append("    </thead>\n");

        // ---- Corps ----
        sb.append("    <tbody>\n");
        for (int jour : lignes) {
            sb.append("      <tr>\n");
            sb.append("        <td class=\"jour\">").append(jour).append("</td>\n");

            for (Mois m : moisList) {
                // Le jour existe-t-il dans ce mois ? (ex: 31 fevrier n'existe pas)
                boolean jourExiste = false;
                try {
                    LocalDate.of(m.annee, m.mois, jour);
                    jourExiste = true;
                } catch (Exception ignore) {
                    jourExiste = false;
                }

                if (!jourExiste) {
                    sb.append("        <td class=\"jour-cell\">-</td>\n");
                    sb.append("        <td class=\"jour-cell\">-</td>\n");
                    continue;
                }

                CelluleCalendrierAbstraite c = getCellule(jour, m);
                if (c == null || !c.isAffichee()) {
                    sb.append("        <td class=\"jour-cell\">-</td>\n");
                    sb.append("        <td class=\"jour-cell\">-</td>\n");
                    continue;
                }

                String css = c.getCssClass() == null ? "" : c.getCssClass();

                sb.append("        <td class=\"jour-cell ").append(css).append("\">")
                  .append(esc(c.getLibelleCourt())).append("</td>\n");

                sb.append("        <td class=\"jour-cell ").append(css).append("\">")
                  .append(fmt(c.getValeur())).append("</td>\n");
            }
            sb.append("      </tr>\n");
        }
        sb.append("    </tbody>\n");

        // ---- Pied ----
        sb.append("    <tfoot>\n");
        sb.append("      <tr class=\"total\">\n");
        sb.append("        <td class=\"jour\">")
          .append(esc(getLibelleTotal()))
          .append("</td>\n");
        for (Mois m : moisList) {
            sb.append("        <td class=\"jour-cell\" colspan=\"2\">")
              .append(fmt(getTotalMois(m)))
              .append("</td>\n");
        }
        sb.append("      </tr>\n");
        sb.append("    </tfoot>\n");

        // ---- Fermeture ----
        sb.append("  </table>\n");
        sb.append("</div>\n");

        return sb.toString();
    }

    // ==================================================================
    // Helpers de formatage
    // ==================================================================

    /** Formate une valeur numerique : "-" si <= 0, sinon "1 234". */
    protected String fmt(double v) {
        if (v <= 0) return "-";
        return String.format("%,.0f", v);
    }

    /** Echappe les caracteres HTML sensibles. */
    protected String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    // ==================================================================
    // Structure Mois (utilisee par toutes les sous-classes)
    // ==================================================================
    public static class Mois {
        public int    annee;
        public int    mois;          // 1..12
        public String libelle;       // ex: "Octobre 2026"
        public String libelleCourt;  // ex: "OCT 2026"
        public Map<Integer, CelluleCalendrierAbstraite> cellules = new LinkedHashMap<>();
    }
}