package a_tahina.facture;

public class AthFactureFilleLib extends AthFactureFille {

    private String produitlib;
    private double montantht;
    private double montanttva;
    private double montantttc;
    private double qteretournee;
    private double qtedisponible;

    public AthFactureFilleLib() throws Exception {
        super();
        this.setNomTable("ATH_FACTURE_FILLE_LIB");
    }

    public String getProduitlib() { return produitlib; }
    public void setProduitlib(String v) { this.produitlib = v; }
    public double getMontantht() { return montantht; }
    public void setMontantht(double v) { this.montantht = v; }
    public double getMontanttva() { return montanttva; }
    public void setMontanttva(double v) { this.montanttva = v; }
    public double getMontantttc() { return montantttc; }
    public void setMontantttc(double v) { this.montantttc = v; }
    public double getQteretournee() { return qteretournee; }
    public void setQteretournee(double v) { this.qteretournee = v; }
    public double getQtedisponible() { return qtedisponible; }
    public void setQtedisponible(double v) { this.qtedisponible = v; }
}
