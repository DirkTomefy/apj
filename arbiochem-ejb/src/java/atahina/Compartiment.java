package atahina;

import bean.TypeObjet;
import java.sql.Connection;


public class Compartiment extends ClassEtat{
    
    private String id;
    private String libelle;
    private int capacite;

    public Categorie() {
        this.setNomTable("COMPARTIMENT");
    }
    
    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("CTG", "getSeqCategorie");
        this.setId(makePK(c));
    }

    public String getIdTypeProduit() {
        return idTypeProduit;
    }

    public void setIdTypeProduit(String idTypeProduit) {
        this.idTypeProduit = idTypeProduit;
    }


    @Override
    public String[] getMotCles() {
        String[] motCles={"id","val"};
        return motCles;
    }

    @Override
    public String[] getValMotCles() {
	 String[] valMotCles={"val"};
        return valMotCles;
    }
}