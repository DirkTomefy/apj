package a_tahina.poussin;

import bean.ClassFille;
import java.sql.Connection;

public class BonCommandePoussinFille extends ClassFille {

    private String id;
    private String idmere;
    private String idproduit;
    private String idsouche;
    private double qte;
    private double pu;

    // Champs joints (lecture seule)
    private String produitlib;
    private String souchelibr;
    private double montant;
    private String idproduitlib;  

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

    // ---- Getters / Setters ----

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdmere() { return idmere; }
    public void setIdmere(String idmere) { this.idmere = idmere; }

    public String getIdproduit() { return idproduit; }
    public void setIdproduit(String idproduit) { this.idproduit = idproduit; }

    public String getIdsouche() { return idsouche; }
    public void setIdsouche(String idsouche) { this.idsouche = idsouche; }

    public double getQte() { return qte; }
    public void setQte(double qte) throws Exception {
        if (getMode().equals("modif") && qte < 0)
            throw new Exception("Quantité négative");
        this.qte = qte;
    }

    public double getPu() { return pu; }
    public void setPu(double pu) throws Exception {
        if (getMode().equals("modif") && pu < 0)
            throw new Exception("PU négatif");
        this.pu = pu;
    }

    public String getProduitlib() { return produitlib; }
    public void setProduitlib(String v) { this.produitlib = v; }

    public String getSouchelibr() { return souchelibr; }
    public void setSouchelibr(String v) { this.souchelibr = v; }

    public double getMontant() { return montant; }
    public void setMontant(double v) { this.montant = v; }

    public String getIdproduitlib() { return idproduitlib; }
    public void setIdproduitlib(String v) { this.idproduitlib = v; }
}