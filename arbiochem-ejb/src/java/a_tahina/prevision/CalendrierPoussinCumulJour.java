package a_tahina.prevision;

import bean.ClassMAPTable;

public class CalendrierPoussinCumulJour extends ClassMAPTable {

    private java.sql.Date daty;
    private double poussins;
    private double pertes;
    private double ca;
    private double cout;
    private double benefice;
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

    public double getCa()                        { return ca; }
    public void   setCa(double v)                { this.ca = v; }

    public double getCout()                      { return cout; }
    public void   setCout(double v)              { this.cout = v; }

    public double getBenefice()                  { return benefice; }
    public void   setBenefice(double v)          { this.benefice = v; }

    public double getPoussinscumules()           { return poussinscumules; }
    public void   setPoussinscumules(double v)   { this.poussinscumules = v; }
}