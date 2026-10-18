package a_tahina.prevision;

import bean.ClassMAPTable;

/**
 * Bean mappe sur la vue CALENDRIER_POUSSIN_RAW.
 * Une ligne = un jour, avec le nombre de poussins nes et de pertes.
 */
public class CalendrierPoussinJour extends ClassMAPTable {

    private java.sql.Date daty;
    private double poussins;
    private double pertes;

    public CalendrierPoussinJour() throws Exception {
        super();
        setNomTable("CALENDRIER_POUSSIN_RAW");
    }

    @Override public String getTuppleID()       { return String.valueOf(daty); }
    @Override public String getAttributIDName() { return "daty"; }

    public java.sql.Date getDaty()         { return daty; }
    public void          setDaty(java.sql.Date v) { this.daty = v; }

    public double getPoussins()            { return poussins; }
    public void   setPoussins(double v)    { this.poussins = v; }

    public double getPertes()              { return pertes; }
    public void   setPertes(double v)      { this.pertes = v; }
}