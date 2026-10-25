package a_tahina.calendrier.helper;

import java.util.LinkedHashMap;
import java.util.Map;

import a_tahina.calendrier.CelluleCalendrierAbstraite;

/**
 * Structure d'un mois dans un calendrier.
 */
public class Mois {

    private int    annee;
    private int    mois;
    private String libelle;
    private String libelleCourt;
    private Map<Integer, CelluleCalendrierAbstraite> cellules = new LinkedHashMap<>();

    public Mois() {}

    public Mois(int annee, int mois, String libelle, String libelleCourt) {
        this.annee        = annee;
        this.mois         = mois;
        this.libelle      = libelle;
        this.libelleCourt = libelleCourt;
    }

    public int    getAnnee()        { return annee; }
    public void   setAnnee(int v)   { this.annee = v; }
    public int    getMois()         { return mois; }
    public void   setMois(int v)    { this.mois = v; }
    public String getLibelle()      { return libelle; }
    public void   setLibelle(String v) { this.libelle = v; }
    public String getLibelleCourt() { return libelleCourt; }
    public void   setLibelleCourt(String v) { this.libelleCourt = v; }

    public Map<Integer, CelluleCalendrierAbstraite> getCellules() { return cellules; }

    public void ajouterCellule(int jour, CelluleCalendrierAbstraite cellule) {
        this.cellules.put(jour, cellule);
    }

    public CelluleCalendrierAbstraite getCellule(int jour) {
        return this.cellules.get(jour);
    }

    public boolean aCellule(int jour) {
        return this.cellules.containsKey(jour);
    }
}