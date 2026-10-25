package a_tahina.facture;

/** Vue ATH_AVOIR_FILLE_LIB. */
public class AthAvoirFilleLib extends AthAvoirFille {

    private String produitlib;
    private double qtefacturee;
    private double montantht;
    private double montanttva;
    private double montantttc;

    public AthAvoirFilleLib() throws Exception {
        super();
        this.setNomTable("ATH_AVOIR_FILLE_LIB");
    }

    public String getProduitlib() { return produitlib; }
    public void setProduitlib(String v) { this.produitlib = v; }
    public double getQtefacturee() { return qtefacturee; }
    public void setQtefacturee(double v) { this.qtefacturee = v; }
    public double getMontantht() { return montantht; }
    public void setMontantht(double v) { this.montantht = v; }
    public double getMontanttva() { return montanttva; }
    public void setMontanttva(double v) { this.montanttva = v; }
    public double getMontantttc() { return montantttc; }
    public void setMontantttc(double v) { this.montantttc = v; }
}
