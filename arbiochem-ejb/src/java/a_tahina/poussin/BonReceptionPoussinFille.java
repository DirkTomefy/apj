package a_tahina.poussin;

import bean.ClassFille;
import java.sql.Connection;

public class BonReceptionPoussinFille extends ClassFille {

    private String id;
    private String idmere;
    private String idproduit;
    private String sexe;
    private double quantite;
    private double nbmort;
    private double nbperdue;
    private double pu;
    private String iddevise;
    private double tauxdechange;
    private String idbcfilleorigine;

    // Champs joints (lecture)
    private String produitlib;
    private String sexelib;
    private double quantitetotale;

    public BonReceptionPoussinFille() throws Exception {
        this.setNomTable("BONRECEPTION_POUSSIN_FILLE");
        this.setNomClasseMere("a_tahina.poussin.BonReceptionPoussin");
        this.setLiaisonMere("idmere");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("BRPF", "GETSEQBRPOUSSINFILLE");
        this.setId(makePK(c));
    }

    @Override public String getTuppleID()       { return id; }
    @Override public String getAttributIDName() { return "id"; }
    @Override public String getNomClasseMere()  { return "a_tahina.poussin.BonReceptionPoussin"; }
    @Override public String getLiaisonMere()    { return "idmere"; }

    // ---- Getters / setters ----

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdmere() { return idmere; }
    public void setIdmere(String idmere) { this.idmere = idmere; }
    public String getIdproduit() { return idproduit; }
    public void setIdproduit(String idproduit) { this.idproduit = idproduit; }
    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }
    public double getQuantite() { return quantite; }
    public void setQuantite(double quantite) { this.quantite = quantite; }
    public double getNbmort() { return nbmort; }
    public void setNbmort(double nbmort) { this.nbmort = nbmort; }
    public double getNbperdue() { return nbperdue; }
    public void setNbperdue(double nbperdue) { this.nbperdue = nbperdue; }
    public double getPu() { return pu; }
    public void setPu(double pu) { this.pu = pu; }
    public String getIddevise() { return iddevise; }
    public void setIddevise(String iddevise) { this.iddevise = iddevise; }
    public double getTauxdechange() { return tauxdechange; }
    public void setTauxdechange(double tauxdechange) { this.tauxdechange = tauxdechange; }
    public String getIdbcfilleorigine() { return idbcfilleorigine; }
    public void setIdbcfilleorigine(String v) { this.idbcfilleorigine = v; }

    public String getProduitlib() { return produitlib; }
    public void setProduitlib(String v) { this.produitlib = v; }
    public String getSexelib() { return sexelib; }
    public void setSexelib(String v) { this.sexelib = v; }
    public double getQuantitetotale() { return quantitetotale; }
    public void setQuantitetotale(double v) { this.quantitetotale = v; }
}