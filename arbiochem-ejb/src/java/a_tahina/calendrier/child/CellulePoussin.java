package a_tahina.calendrier.child;

import a_tahina.calendrier.CelluleCalendrierAbstraite;

/**
 * Cellule poussin : jour de la semaine + nombre de poussins.
 *
 * Priorite de couleur :
 *   1. pertes > 0          -> ROUGE
 *   2. dimanche            -> DIMANCHE
 *   3. poussins > 0        -> normal (blanc)
 *   4. sinon               -> GRIS
 */
public class CellulePoussin extends CelluleCalendrierAbstraite {

    private String  jourSemaine;
    private double  poussins;
    private double  pertes;
    private boolean dimanche;

    public CellulePoussin(String jourSemaine, double poussins, double pertes, boolean dimanche) {
        this.jourSemaine = jourSemaine;
        this.poussins    = poussins;
        this.pertes      = pertes;
        this.dimanche    = dimanche;
    }

    @Override public String getLibelleCourt() { return jourSemaine == null ? "" : jourSemaine; }
    @Override public Object getValeur()       { return poussins; }

    @Override
    public String getCssClass() {
        if (pertes > 0)   return ROUGE;
        if (dimanche)     return DIMANCHE;
        if (poussins > 0) return "";
        return GRIS;
    }

    public double  getPoussins() { return poussins; }
    public double  getPertes()   { return pertes; }
    public boolean isDimanche()  { return dimanche; }
}