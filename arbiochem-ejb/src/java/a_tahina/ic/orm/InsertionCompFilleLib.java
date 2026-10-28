package a_tahina.ic.orm;

public class InsertionCompFilleLib extends InsertionCompartimentFille {

    private String compartimentlib;
    private String sexelib;
    private String santelib;

    public InsertionCompFilleLib() throws Exception {
        super();
        this.setNomTable("INSERTION_COMP_FILLE_LIB");
    }

    public String getCompartimentlib() { return compartimentlib; }
    public void setCompartimentlib(String v) { this.compartimentlib = v; }

    public String getSexelib() { return sexelib; }
    public void setSexelib(String v) { this.sexelib = v; }

    public String getSantelib() { return santelib; }
    public void setSantelib(String v) { this.santelib = v; }
}
