package a_tahina.calendrier;

/**
 * Contrat abstrait d'une cellule de calendrier.
 *
 * Deux facons de colorer une cellule :
 *   1. getCssClass() : retourne une classe CSS parmi les constantes ci-dessous
 *   2. getStyle()    : retourne un style inline (ex: "background:#abc")
 *
 * Les deux peuvent etre combines (CSS + style inline).
 */
public abstract class CelluleCalendrierAbstraite {

    // ==================================================================
    // Constantes de classes CSS (a utiliser dans getCssClass())
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
    public static final String DIMANCHE = "dimanche";

    // ==================================================================
    // Contrat
    // ==================================================================

    /** Valeur a afficher (Double, Integer, String, ...). */
    public abstract Object getValeur();

    /** Classe CSS (une ou plusieurs, separees par espace). */
    public abstract String getCssClass();

    /** Libelle court affiche dans la 1ere sous-colonne (ex: "Lun."). */
    public abstract String getLibelleCourt();

    /** true si la cellule doit etre affichee (sinon "-"). */
    public boolean isAffichee() {
        return true;
    }

    /**
     * Style inline optionnel (ex: "background:#abc;color:#fff").
     * Retourner null ou "" si aucun style supplementaire n'est necessaire.
     */
    public String getStyle() {
        return null;
    }

    // ==================================================================
    // Helpers par defaut (surchargeables)
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

    // ==================================================================
    // Helper de heatmap : renvoie une classe CSS selon un ratio 0..1
    // ==================================================================
    protected static String couleurSelonRatio(double ratio) {
        if (ratio >= 0.8) return ROUGE;
        if (ratio >= 0.6) return ORANGE;
        if (ratio >= 0.4) return JAUNE;
        if (ratio >= 0.2) return VERT;
        return BLANC;
    }
}