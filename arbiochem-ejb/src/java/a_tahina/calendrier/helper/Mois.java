package a_tahina.calendrier.helper;

import java.util.LinkedHashMap;
import java.util.Map;

import a_tahina.calendrier.CelluleCalendrierAbstraite;

/**
 * Structure d'un mois dans un calendrier.
 *
 * Contient :
 *   - le numero du mois (1..12)
 *   - l'annee
 *   - les libelles long et court
 *   - les cellules indexees par numero de jour (1..31)
 *
 * Utilisee par toutes les sous-classes de CalendrierAbstrait.
 * Les cellules sont stockees dans une LinkedHashMap pour preserver
 * l'ordre d'insertion (utile pour un debug eventuel).
 */
public class Mois {

    // ==================================================================
    // Champs
    // ==================================================================

    /** Annee (ex: 2026). */
    private int annee;

    /** Numero du mois (1..12). */
    private int mois;

    /** Libelle long (ex: "Octobre 2026"). */
    private String libelle;

    /** Libelle court (ex: "OCT 2026"). */
    private String libelleCourt;

    /** Cellules par numero de jour (cle = 1..31). */
    private Map<Integer, CelluleCalendrierAbstraite> cellules = new LinkedHashMap<>();

    // ==================================================================
    // Constructeurs
    // ==================================================================

    public Mois() {}

    public Mois(int annee, int mois, String libelle, String libelleCourt) {
        this.annee        = annee;
        this.mois         = mois;
        this.libelle      = libelle;
        this.libelleCourt = libelleCourt;
    }

    // ==================================================================
    // Accesseurs
    // ==================================================================

    public int getAnnee()        { return annee; }
    public void setAnnee(int v)  { this.annee = v; }

    public int getMois()         { return mois; }
    public void setMois(int v)   { this.mois = v; }

    public String getLibelle()      { return libelle; }
    public void setLibelle(String v) { this.libelle = v; }

    public String getLibelleCourt()      { return libelleCourt; }
    public void setLibelleCourt(String v) { this.libelleCourt = v; }

    public Map<Integer, CelluleCalendrierAbstraite> getCellules() {
        return cellules;
    }

    public void setCellules(Map<Integer, CelluleCalendrierAbstraite> v) {
        this.cellules = v;
    }

    // ==================================================================
    // Helpers
    // ==================================================================

    /** Ajoute une cellule pour un jour donne. */
    public void ajouterCellule(int jour, CelluleCalendrierAbstraite cellule) {
        this.cellules.put(jour, cellule);
    }

    /** Recupere la cellule d'un jour donne (ou null). */
    public CelluleCalendrierAbstraite getCellule(int jour) {
        return this.cellules.get(jour);
    }

    /** Retourne true si une cellule existe pour ce jour. */
    public boolean aCellule(int jour) {
        return this.cellules.containsKey(jour);
    }
}