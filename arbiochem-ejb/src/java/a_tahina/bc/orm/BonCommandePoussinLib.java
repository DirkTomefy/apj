package a_tahina.bc.orm;

public class BonCommandePoussinLib extends BonCommandePoussin {

    private String idmagasinlib;
    private String etatlib;

    public BonCommandePoussinLib() throws Exception {
        super();
        this.setNomTable("BONCOMMANDE_POUSSIN_LIB");
        this.setLiaisonFille(null);
        this.setNomClasseFille(null);
    }

    public String getIdmagasinlib() { return idmagasinlib; }
    public void setIdmagasinlib(String v) { this.idmagasinlib = v; }

    public String getEtatlib() { return etatlib; }
    public void setEtatlib(String v) { this.etatlib = v; }
}