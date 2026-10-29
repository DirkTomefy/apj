package a_tahina.prevision.simulation;

import java.time.LocalDate;

public class Vente {

    final String idfacture;
    final String id;
    final LocalDate date;
    final String type;
    final String sexe;
    final double qte;
    final double montant;
    double servi;
    double valeurStock;

    Vente(String idfacture, String id, LocalDate date, String type, String sexe, double qte, double montant) {
        this.idfacture = idfacture;
        this.id = id;
        this.date = date;
        this.type = type;
        this.sexe = sexe;
        this.qte = qte;
        this.montant = montant;
    }

    void reinitialiser() { servi = 0; valeurStock = 0; }

    public String getIdfacture()  { return idfacture; }
    public String getId()         { return id; }
    public LocalDate getDate()    { return date; }
    public String getType()       { return type; }
    public String getSexe()       { return sexe; }
    public double getQte()        { return qte; }
    public double getServi()      { return servi; }
    public double getManque()     { return qte - servi; }
    public double getPuStock()    { return servi > Elevage.EPS ? valeurStock / servi : 0; }
}
