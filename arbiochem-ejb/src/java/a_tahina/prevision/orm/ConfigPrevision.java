package a_tahina.prevision.orm;

import java.sql.Connection;

import bean.ClassMAPTable;

public class ConfigPrevision extends ClassMAPTable {

    private String id;
    private String idsouche;
    private int semaine;
    private double nbjour;

    public ConfigPrevision() throws Exception {
        this.setNomTable("CONFIG_PREVISION");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("CFP", "GETSEQCONFIG_PREVISION");
        this.setId(makePK(c));
    }

    @Override public String getAttributIDName() { return "id"; }
    @Override public String getTuppleID()       { return this.getId(); }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdsouche() { return idsouche; }
    public void setIdsouche(String v) { this.idsouche = v; }
    public int getSemaine() { return semaine; }
    public void setSemaine(int v) { this.semaine = v; }
    public double getNbjour() { return nbjour; }
    public void setNbjour(double v) { this.nbjour = v; }
}
