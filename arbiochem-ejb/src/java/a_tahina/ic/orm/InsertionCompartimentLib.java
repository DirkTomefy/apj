package a_tahina.ic.orm;

public class InsertionCompartimentLib extends InsertionCompartiment {

    private String idbonreceptionlib;
    private String etatlib;

    public InsertionCompartimentLib() throws Exception {
        super();
        this.setNomTable("INSERTION_COMPARTIMENT_LIB");
    }

    public String getIdbonreceptionlib() { return idbonreceptionlib; }
    public void setIdbonreceptionlib(String v) { this.idbonreceptionlib = v; }

    public String getEtatlib() { return etatlib; }
    public void setEtatlib(String v) { this.etatlib = v; }
}