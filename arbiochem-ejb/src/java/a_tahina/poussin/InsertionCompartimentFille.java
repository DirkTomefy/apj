package a_tahina.poussin;

import bean.ClassFille;
import java.sql.Connection;

public class InsertionCompartimentFille extends ClassFille {

    private String id;
    private String idmere;
    private String idbcfilleorigine;
    private String idbrcfilleorigine;
    private String idcompartiment;
    private double quantitefinale;
    private String sexe;
    private int    sante;
    private String idlot;
    private int    est_final = 0;

    private String compartimentlib;
    private String produitlib;
    private String sexelib;
    private String est_finallib;
    private String santelib;

    public InsertionCompartimentFille() throws Exception {
        this.setNomTable("INSERTION_COMPARTIMENT_FILLE");
        this.setNomClasseMere("a_tahina.poussin.InsertionCompartiment");
        this.setLiaisonMere("idmere");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("ICF", "GETSEQINSERTIONCOMPFILLE");
        this.setId(makePK(c));
    }

    @Override public String getTuppleID()       { return id; }
    @Override public String getAttributIDName() { return "id"; }
    @Override public String getNomClasseMere()  { return "a_tahina.poussin.InsertionCompartiment"; }
    @Override public String getLiaisonMere()    { return "idmere"; }

    public void setSante(int sante) throws Exception {
        if (getMode().equals("modif") && (sante < 1 || sante > 10))
            throw new Exception("Santé doit être entre 1 et 10");
        this.sante = sante;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdmere() { return idmere; }
    public void setIdmere(String idmere) { this.idmere = idmere; }
    public String getIdbcfilleorigine() { return idbcfilleorigine; }
    public void setIdbcfilleorigine(String v) { this.idbcfilleorigine = v; }
    public String getIdbrcfilleorigine() { return idbrcfilleorigine; }
    public void setIdbrcfilleorigine(String v) { this.idbrcfilleorigine = v; }
    public String getIdcompartiment() { return idcompartiment; }
    public void setIdcompartiment(String idcompartiment) { this.idcompartiment = idcompartiment; }
    public double getQuantitefinale() { return quantitefinale; }
    public void setQuantitefinale(double quantitefinale) { this.quantitefinale = quantitefinale; }
    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }
    public int getSante() { return sante; }
    public String getIdlot() { return idlot; }
    public void setIdlot(String idlot) { this.idlot = idlot; }
    public int getEst_final() { return est_final; }
    public void setEst_final(int est_final) { this.est_final = est_final; }
    public String getCompartimentlib() { return compartimentlib; }
    public void setCompartimentlib(String v) { this.compartimentlib = v; }
    public String getProduitlib() { return produitlib; }
    public void setProduitlib(String v) { this.produitlib = v; }
    public String getSexelib() { return sexelib; }
    public void setSexelib(String v) { this.sexelib = v; }
    public String getEst_finallib() { return est_finallib; }
    public void setEst_finallib(String v) { this.est_finallib = v; }
    public String getSantelib() { return santelib; }
    public void setSantelib(String v) { this.santelib = v; }
}