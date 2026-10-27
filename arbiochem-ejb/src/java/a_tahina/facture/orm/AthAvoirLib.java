package a_tahina.facture.orm;

public class AthAvoirLib extends AthAvoir {

    private String idfacturelib;
    private String idclientlib;
    private double montantht;
    private double montanttva;
    private double montantttc;
    private double montantutilise;
    private double reste;
    private String etatlib;

    public AthAvoirLib() throws Exception {
        super();
        this.setNomTable("ATH_AVOIR_LIB");
        this.setLiaisonFille(null);
        this.setNomClasseFille(null);
    }

    public String getIdfacturelib() { return idfacturelib; }
    public void setIdfacturelib(String v) { this.idfacturelib = v; }
    public String getIdclientlib() { return idclientlib; }
    public void setIdclientlib(String v) { this.idclientlib = v; }
    public double getMontantht() { return montantht; }
    public void setMontantht(double v) { this.montantht = v; }
    public double getMontanttva() { return montanttva; }
    public void setMontanttva(double v) { this.montanttva = v; }
    public double getMontantttc() { return montantttc; }
    public void setMontantttc(double v) { this.montantttc = v; }
    public double getMontantutilise() { return montantutilise; }
    public void setMontantutilise(double v) { this.montantutilise = v; }
    public double getReste() { return reste; }
    public void setReste(double v) { this.reste = v; }
    public String getEtatlib() { return etatlib; }
    public void setEtatlib(String v) { this.etatlib = v; }
}
