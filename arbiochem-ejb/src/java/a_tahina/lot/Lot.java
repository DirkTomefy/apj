package a_tahina.lot;

import java.sql.Connection;
import bean.ClassMAPTable;

public class Lot extends ClassMAPTable {

    private String id;
    private String idbonreception;
    private String idproduit;
    private String idsouche;
    private double qte;
    private double pu;
    private String sexe;
    private String idsante;
    private String idinsertcompfille;
    private String idboncommande;
    private double qteActuelle;

    public Lot() throws Exception {
        this.setNomTable("LOT");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("LOT", "GETSEQLOT");
        this.setId(makePK(c));
    }

    @Override public String getAttributIDName() { return "id"; }
    @Override public String getTuppleID()       { return this.getId(); }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdbonreception() { return idbonreception; }
    public void setIdbonreception(String v) { this.idbonreception = v; }

    public String getIdproduit() { return idproduit; }
    public void setIdproduit(String v) { this.idproduit = v; }

    public String getIdsouche() { return idsouche; }
    public void setIdsouche(String v) { this.idsouche = v; }

    public double getQte() { return qte; }
    public void setQte(double v) { this.qte = v; }

    public double getPu() { return pu; }
    public void setPu(double v) { this.pu = v; }

    public String getSexe() { return sexe; }
    public void setSexe(String v) { this.sexe = v; }

    public String getIdsante() { return idsante; }
    public void setIdsante(String v) { this.idsante = v; }

    public String getIdinsertcompfille() { return idinsertcompfille; }
    public void setIdinsertcompfille(String v) { this.idinsertcompfille = v; }

    public String getIdboncommande() { return idboncommande; }
    public void setIdboncommande(String v) { this.idboncommande = v; }

    public double getQteActuelle() { return qteActuelle; }
    public void setQteActuelle(double v) { this.qteActuelle = v; }
}