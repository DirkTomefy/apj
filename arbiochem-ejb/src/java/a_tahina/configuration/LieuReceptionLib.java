package a_tahina.configuration;

public class LieuReceptionLib extends LieuReception {

    private String libelleComplet;

    public LieuReceptionLib() throws Exception {
        this.setNomTable("LIEURECEPTION");
    }

    public String getLibelleComplet() { return libelleComplet; }
    public void setLibelleComplet(String v) { this.libelleComplet = v; }
}