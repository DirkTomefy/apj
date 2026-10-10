package a_tahina.br;

public class BonReceptionPoussinLib extends BonReceptionPoussin {

    private String idboncommandelib;
    private String idboncommandefilleproduit;
    private String idbrprecedentlib;
    private String idlieureceptionlib;
    private String idmagasinlib;
    private String produitlib;
    private String souchelibr;
    private String etatlib;

    public BonReceptionPoussinLib() throws Exception {
        super();
        this.setNomTable("BONRECEPTION_POUSSIN_LIB");
    }

    public String getIdboncommandelib() { return idboncommandelib; }
    public void setIdboncommandelib(String v) { this.idboncommandelib = v; }

    public String getIdboncommandefilleproduit() { return idboncommandefilleproduit; }
    public void setIdboncommandefilleproduit(String v) { this.idboncommandefilleproduit = v; }

    public String getIdbrprecedentlib() { return idbrprecedentlib; }
    public void setIdbrprecedentlib(String v) { this.idbrprecedentlib = v; }

    public String getIdlieureceptionlib() { return idlieureceptionlib; }
    public void setIdlieureceptionlib(String v) { this.idlieureceptionlib = v; }

    public String getIdmagasinlib() { return idmagasinlib; }
    public void setIdmagasinlib(String v) { this.idmagasinlib = v; }

    public String getProduitlib() { return produitlib; }
    public void setProduitlib(String v) { this.produitlib = v; }

    public String getSouchelibr() { return souchelibr; }
    public void setSouchelibr(String v) { this.souchelibr = v; }

    public String getEtatlib() { return etatlib; }
    public void setEtatlib(String v) { this.etatlib = v; }
}