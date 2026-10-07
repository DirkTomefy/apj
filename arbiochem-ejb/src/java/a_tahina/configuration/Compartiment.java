package a_tahina.configuration;

import bean.ClassMAPTable;
import java.sql.Connection;

public class Compartiment extends ClassMAPTable {

    private String id;
    private String nom;
    private double capacite;

    public Compartiment() throws Exception {
        this.setNomTable("COMPARTIMENT");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("CMP", "GETSEQCOMPARTIMENT");
        this.setId(makePK(c));
    }

    @Override
    public String getTuppleID() {
        return id;
    }

    @Override
    public String getAttributIDName() {
        return "id";
    }

    @Override
    public String[] getMotCles() {
        return new String[]{"id", "nom"};
    }

    @Override
    public String[] getValMotCles() {
        return new String[]{"id", "nom"};
    }

    // ---- Getters / Setters ----

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getCapacite() {
        return capacite;
    }

    public void setCapacite(double capacite) {
        this.capacite = capacite;
    }
}