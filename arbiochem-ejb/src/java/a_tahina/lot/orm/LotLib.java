package a_tahina.lot.orm;


public class LotLib extends Lot {

    private String idbonreceptionlib;
    private String idboncommandelib;
    private String produitlib;
    private String souchelibr;
    private String santelib;
    private String sexelib;
    private String compartimentlib;
    private double valeurtotale;
    private double valeurunitaire;

    public LotLib() throws Exception {
        super();
        this.setNomTable("LOT_LIB");
    }

    public String getIdbonreceptionlib() { return idbonreceptionlib; }
    public void setIdbonreceptionlib(String v) { this.idbonreceptionlib = v; }

    public String getIdboncommandelib() { return idboncommandelib; }
    public void setIdboncommandelib(String v) { this.idboncommandelib = v; }

    public String getProduitlib() { return produitlib; }
    public void setProduitlib(String v) { this.produitlib = v; }

    public String getSouchelibr() { return souchelibr; }
    public void setSouchelibr(String v) { this.souchelibr = v; }

    public String getSantelib() { return santelib; }
    public void setSantelib(String v) { this.santelib = v; }

    public String getSexelib() { return sexelib; }
    public void setSexelib(String v) { this.sexelib = v; }

    public String getCompartimentlib() { return compartimentlib; }
    public void setCompartimentlib(String v) { this.compartimentlib = v; }

    public double getValeurtotale() { return valeurtotale; }
    public void setValeurtotale(double v) { this.valeurtotale = v; }

    public double getValeurunitaire() { return valeurunitaire; }
    public void setValeurunitaire(double v) { this.valeurunitaire = v; }
}
