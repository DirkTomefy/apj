package a_tahina.facture;

public class AthFactureLib extends AthFacture {

    private String idclientlib;
    private double montantht;
    private double montanttva;
    private double montantttc;
    private double pourcentageavoir;
    private double plafondavoir;
    private double netapayer;
    private String etatlib;

    public AthFactureLib() throws Exception {
        super();
        this.setNomTable("ATH_FACTURE_LIB");
        this.setLiaisonFille(null);
        this.setNomClasseFille(null);
    }

    public String getIdclientlib() { return idclientlib; }
    public void setIdclientlib(String v) { this.idclientlib = v; }
    public double getMontantht() { return montantht; }
    public void setMontantht(double v) { this.montantht = v; }
    public double getMontanttva() { return montanttva; }
    public void setMontanttva(double v) { this.montanttva = v; }
    public double getMontantttc() { return montantttc; }
    public void setMontantttc(double v) { this.montantttc = v; }
    public double getPourcentageavoir() { return pourcentageavoir; }
    public void setPourcentageavoir(double v) { this.pourcentageavoir = v; }
    public double getPlafondavoir() { return plafondavoir; }
    public void setPlafondavoir(double v) { this.plafondavoir = v; }
    public double getNetapayer() { return netapayer; }
    public void setNetapayer(double v) { this.netapayer = v; }
    public String getEtatlib() { return etatlib; }
    public void setEtatlib(String v) { this.etatlib = v; }
}
