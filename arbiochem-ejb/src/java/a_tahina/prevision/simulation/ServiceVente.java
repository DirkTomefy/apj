package a_tahina.prevision.simulation;

public class ServiceVente {

    private final Vente vente;
    double servi;
    double valeurStock;

    ServiceVente(Vente vente) {
        this.vente = vente;
    }

    public Vente getVente()     { return vente; }
    public double getServi()    { return servi; }
    public double getManque()   { return vente.qte - servi; }
    public double getPuStock()  { return servi > Elevage.EPS ? valeurStock / servi : 0; }
}
