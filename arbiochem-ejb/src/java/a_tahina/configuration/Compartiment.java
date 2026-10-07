package a_tahina.configuration;

import bean.ClassMAPTable;
import java.sql.Connection;

public class Compartiment extends ClassMAPTable {

    private String id;
    private String libelle;
    private int    capacite;
    private String idbatiment;

    // Champ joint (lecture seule)
    private String batimentlib;

    public Compartiment() {
        setNomTable("COMPARTIMENT");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("CTG", "GETSEQCOMPARTIMENT");
        this.setId(makePK(c));
    }

    @Override public String getTuppleID()       { return id; }
    @Override public String getAttributIDName() { return "id"; }

    @Override public String[] getMotCles()    { return new String[]{"id", "libelle"}; }
    @Override public String[] getValMotCles() { return new String[]{"id", "libelle"}; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }

    public String getIdbatiment() { return idbatiment; }
    public void setIdbatiment(String idbatiment) { this.idbatiment = idbatiment; }

    public String getBatimentlib() { return batimentlib; }
    public void setBatimentlib(String v) { this.batimentlib = v; }
}