package a_tahina.poussin;

import bean.ClassFille;
import java.sql.Connection;

public class BonCommandePoussinFille extends ClassFille {

    private String id;
    private String idmere;
    private String idproduit;
    private String sexe;
    private double quantite;
    private double pu;
    private String iddevise;
    private double tauxdechange;
    private double remise;
    private double tva;
    private double montant;

    // Champs joints (lecture)
    private String produitlib;
    private String sexelib;
    private double montantht;
    private double montanthtnet;
    private double montantttc;

    public BonCommandePoussinFille() throws Exception {
        this.setNomTable("BONCOMMANDE_POUSSIN_FILLE");
        this.setNomClasseMere("a_tahina.poussin.BonCommandePoussin");
        this.setLiaisonMere("idmere");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("BCPF", "GETSEQBCPOUSSINFILLE");
        this.setId(makePK(c));
    }

    @Override public String getTuppleID()       { return id; }
    @Override public String getAttributIDName() { return "id"; }
    @Override public String getNomClasseMere()  { return "a_tahina.poussin.BonCommandePoussin"; }
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
    public void setQuantite(double quantite) throws Exception {
        if (getMode().equals("modif") && quantite < 0)
            throw new Exception("Quantité négative");
        this.quantite = quantite;
    }
    public double getPu() { return pu; }
    public void setPu(double pu) { this.pu = pu; }
    public String getIddevise() { return iddevise; }
    public void setIddevise(String iddevise) { this.iddevise = iddevise; }
    public double getTauxdechange() { return tauxdechange; }
    public void setTauxdechange(double tauxdechange) throws Exception {
        if (getMode().equals("modif") && tauxdechange <= 0)
            throw new Exception("Taux de change invalide");
        this.tauxdechange = tauxdechange;
    }
    public double getRemise() { return remise; }
    public void setRemise(double remise) { this.remise = remise; }
    public double getTva() { return tva; }
    public void setTva(double tva) { this.tva = tva; }
    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public String getProduitlib() { return produitlib; }
    public void setProduitlib(String v) { this.produitlib = v; }
    public String getSexelib() { return sexelib; }
    public void setSexelib(String v) { this.sexelib = v; }
    public double getMontantht() { return montantht; }
    public void setMontantht(double v) { this.montantht = v; }
    public double getMontanthtnet() { return montanthtnet; }
    public void setMontanthtnet(double v) { this.montanthtnet = v; }
    public double getMontantttc() { return montantttc; }
    public void setMontantttc(double v) { this.montantttc = v; }
}