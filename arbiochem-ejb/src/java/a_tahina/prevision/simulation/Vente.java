package a_tahina.prevision.simulation;

import java.time.LocalDate;
import java.util.Comparator;

import a_tahina.core.Sexe;

import a_tahina.core.TypeVente;

public class Vente {

    static final Comparator<Vente> ORDRE_DU_JOUR =
            Comparator.comparing((Vente v) -> v.idfacture == null ? "" : v.idfacture)
                      .thenComparing(v -> v.id == null ? "" : v.id);

    final String idfacture;
    final String id;
    final LocalDate date;
    final TypeVente type;
    final Sexe sexe;
    final double qte;
    final double montant;
    double servi;
    double valeurStock;

    Vente(String idfacture, String id, LocalDate date, TypeVente type, Sexe sexe, double qte, double montant) {
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
    public TypeVente getType()    { return type; }
    public Sexe getSexe()         { return sexe; }
    public double getQte()        { return qte; }
    public double getServi()      { return servi; }
    public double getManque()     { return qte - servi; }
    public double getPuStock()    { return servi > Elevage.EPS ? valeurStock / servi : 0; }
}
