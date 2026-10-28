package a_tahina.prevision.orm;

import bean.ClassMAPTable;


public class CalendrierPoussinJour extends ClassMAPTable {

    private java.sql.Date daty;
    private double poussins;
    private double pertes;
    private double poussinsmales;
    private double poussinsfemelles;
    private double pertesmales;
    private double pertesfemelles;
    private double ca;
    private double cout;
    private double benefice;

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

    public double getPoussinsmales() { return poussinsmales; }
    public void   setPoussinsmales(double v) { this.poussinsmales = v; }

    public double getPoussinsfemelles() { return poussinsfemelles; }
    public void   setPoussinsfemelles(double v) { this.poussinsfemelles = v; }

    public double getPertesmales() { return pertesmales; }
    public void   setPertesmales(double v) { this.pertesmales = v; }

    public double getPertesfemelles() { return pertesfemelles; }
    public void   setPertesfemelles(double v) { this.pertesfemelles = v; }

    public double getCa() { return ca; }
    public void   setCa(double v) { this.ca = v; }

    public double getCout() { return cout; }
    public void   setCout(double v) { this.cout = v; }

    public double getBenefice() { return benefice; }
    public void   setBenefice(double v) { this.benefice = v; }
}
