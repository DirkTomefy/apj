package a_tahina.compartiment.orm;

import java.sql.Connection;
import bean.ClassMAPTable;

public class Compartiment extends ClassMAPTable {

    private String id;
    private String nom;
    private int    capacite;

    public Compartiment() throws Exception {
        this.setNomTable("COMPARTIMENT");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("CMP", "GETSEQCOMPARTIMENT");
        this.setId(makePK(c));
    }

    @Override public String getAttributIDName() { return "id"; }
    @Override public String getTuppleID()       { return this.getId(); }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }
}