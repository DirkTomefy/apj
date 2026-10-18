package a_tahina.prevision;

import bean.ClassMAPTable;

/**
 * Bean mappe sur la vue CALENDRIER_POUSSIN_CUMUL.
 * Une ligne = un jour : poussins nes ce jour, pertes declarees ce jour
 * et cumul des poussins depuis le premier jour.
 */
public class CalendrierPoussinCumulJour extends ClassMAPTable {

    private java.sql.Date daty;
    private double poussins;
    private double pertes;
    private double poussinscumules;

    public CalendrierPoussinCumulJour() throws Exception {
        super();
        setNomTable("CALENDRIER_POUSSIN_CUMUL");
    }

    @Override public String getTuppleID()       { return String.valueOf(daty); }
    @Override public String getAttributIDName() { return "daty"; }

    public java.sql.Date getDaty()               { return daty; }
    public void          setDaty(java.sql.Date v) { this.daty = v; }

    public double getPoussins()                  { return poussins; }
    public void   setPoussins(double v)          { this.poussins = v; }

    public double getPertes()                    { return pertes; }
    public void   setPertes(double v)            { this.pertes = v; }

    public double getPoussinscumules()           { return poussinscumules; }
    public void   setPoussinscumules(double v)   { this.poussinscumules = v; }
}