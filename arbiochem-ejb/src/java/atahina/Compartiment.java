package atahina;

import bean.ClassMAPTable;
import java.sql.Connection;


public class Compartiment extends ClassMAPTable{
    
    private String id;
    private String libelle;
    private int capacite;

    
    
    public Compartiment() {
        setNomTable("COMPARTIMENT");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("CTG", "GETSEQCOMPARTIMENT");
        this.setId(makePK(c));
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    @Override public String getAttributIDName() { return "id"; } // "comment s'appelle le champ PK ?"

    @Override public String getTuppleID()       { return id; }  // "quelle est la valeur de la PK ?"
}