package a_tahina.calendrier.child;

import java.util.HashMap;
import java.util.Map;

import a_tahina.calendrier.CelluleCalendrierAbstraite;

/**
 * Cellule poussin : jour de la semaine + cumul de poussins.
 *
 * - La valeur AFFICHEE est le cumul de poussins a cette date.
 * - La valeur NUMERIQUE (getValeur) reste le nombre de poussins nes ce jour-la,
 *   pour que le total du mois en pied de tableau soit la somme des naissances
 *   et non une somme de cumuls.
 * - Les indicateurs "ca" et "benefice" du jour sont additionnes par mois dans
 *   les lignes de pied du calendrier.
 *
 * Priorite de couleur :
 *   1. pertes > 0          -> ROUGE (une declaration de perte existe ce jour-la)
 *   2. poussins > 0        -> normal (blanc) : des poussins sont nes ce jour-la
 *   3. sinon               -> GRIS : aucune naissance ce jour-la
 */
public class CellulePoussin extends CelluleCalendrierAbstraite {

    public static final String IND_CA       = "ca";
    public static final String IND_BENEFICE = "benefice";

    private String  jourSemaine;
    private double  poussins;
    private double  cumul;
    private double  pertes;
    private double  ca;
    private double  benefice;
    private boolean dimanche;
    private final Map<String, Double> indicateurs = new HashMap<>();

    public CellulePoussin(String jourSemaine, double poussins, double cumul,
                          double pertes, double ca, double benefice, boolean dimanche) {
        this.jourSemaine = jourSemaine;
        this.poussins    = poussins;
        this.cumul       = cumul;
        this.pertes      = pertes;
        this.ca          = ca;
        this.benefice    = benefice;
        this.dimanche    = dimanche;
        this.indicateurs.put(IND_CA, ca);
        this.indicateurs.put(IND_BENEFICE, benefice);
    }

    /** Sans CA ni benefice. */
    public CellulePoussin(String jourSemaine, double poussins, double cumul,
                          double pertes, boolean dimanche) {
        this(jourSemaine, poussins, cumul, pertes, 0, 0, dimanche);
    }

    /** Ancien constructeur : sans cumul, la case affiche les poussins du jour. */
    public CellulePoussin(String jourSemaine, double poussins, double pertes, boolean dimanche) {
        this(jourSemaine, poussins, poussins, pertes, 0, 0, dimanche);
    }

    @Override public String getLibelleCourt() { return jourSemaine == null ? "" : jourSemaine; }

    /** Naissances du jour (sert au total du mois). */
    @Override public Object getValeur()       { return poussins; }

    /** Ce qui est affiche dans la case : le cumul. */
    @Override
    public String getValeurAffichee() {
        if (cumul <= 0) return "-";
        return String.format("%,.0f", cumul);
    }

    @Override
    public String getCssClass() {
        if (pertes > 0)   return ROUGE;
        if (poussins > 0) return "";
        return GRIS;
    }

    @Override public Map<String, Double> getIndicateurs() { return indicateurs; }

    public double  getPoussins() { return poussins; }
    public double  getCumul()    { return cumul; }
    public double  getPertes()   { return pertes; }
    public double  getCa()       { return ca; }
    public double  getBenefice() { return benefice; }
    public boolean isDimanche()  { return dimanche; }
}