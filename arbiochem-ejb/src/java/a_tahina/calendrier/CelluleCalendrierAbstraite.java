package a_tahina.calendrier;

import java.util.Collections;
import java.util.Map;

public abstract class CelluleCalendrierAbstraite {

    // ==================================================================
    // Constantes de classes CSS
    // ==================================================================
    public static final String ROUGE    = "rouge";
    public static final String VERT     = "vert";
    public static final String JAUNE    = "jaune";
    public static final String ORANGE   = "orange";
    public static final String BLEU     = "bleu";
    public static final String VIOLET   = "violet";
    public static final String GRIS     = "gris";
    public static final String NOIR     = "noir";
    public static final String BLANC    = "blanc";
    public static final String ROSE     = "rose";
    public static final String DIMANCHE = "dimanche";

    // ==================================================================
    // Contrat
    // ==================================================================
    public abstract Object getValeur();
    public abstract String getCssClass();
    public abstract String getLibelleCourt();

    public boolean isAffichee() { return true; }

    /** Style inline optionnel (ex: "background:#abc;color:#fff"). */
    public String getStyle() { return null; }

    /**
     * Valeurs chiffrees supplementaires de la cellule, par cle (ex: "ca", "benefice").
     * Le calendrier les additionne par mois pour les indicateurs declares dans
     * CalendrierAbstrait.getIndicateurs(). Vide par defaut.
     */
    public Map<String, Double> getIndicateurs() { return Collections.emptyMap(); }

    // ==================================================================
    // Helpers par defaut
    // ==================================================================
    public double getValeurNumerique() {
        Object v = getValeur();
        if (v instanceof Number) return ((Number) v).doubleValue();
        return 0;
    }

    public String getValeurAffichee() {
        Object v = getValeur();
        if (v == null) return "-";
        if (v instanceof Number) {
            double d = ((Number) v).doubleValue();
            if (d == 0) return "-";
            return String.format("%,.0f", d);
        }
        return v.toString();
    }

    /** Helper heatmap : renvoie une classe selon un ratio 0..1. */
    protected static String couleurSelonRatio(double ratio) {
        if (ratio >= 0.8) return ROUGE;
        if (ratio >= 0.6) return ORANGE;
        if (ratio >= 0.4) return JAUNE;
        if (ratio >= 0.2) return VERT;
        return BLANC;
    }
}