package a_tahina.bc.orm;

import bean.ClassFille;
import java.sql.Connection;

public class BonCommandePoussinFille extends ClassFille {

    private String id;
    private String idmere;
    private String idproduit;
    private String idsouche;
    private double qte;
    private double pu;
    private String fournisseur;

    // Champs joints (lecture seule)
    private String produitlib;
    private String souchelibr;
    private double montant;
    private String idproduitlib;
    private String fournisseurlib;

    public BonCommandePoussinFille() throws Exception {
        this.setNomTable("BONCOMMANDE_POUSSIN_FILLE");
        this.setNomClasseMere("a_tahina.bc.orm.BonCommandePoussin");
        this.setLiaisonMere("idmere");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("BCPF", "GETSEQBCPOUSSINFILLE");
        this.setId(makePK(c));
    }

    @Override public String getAttributIDName() { return "id"; }
    @Override public String getTuppleID()       { return this.getId(); }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdmere() { return idmere; }
    public void setIdmere(String idmere) { this.idmere = idmere; }

    public String getIdproduit() { return idproduit; }
    public void setIdproduit(String idproduit) { this.idproduit = idproduit; }

    public String getIdsouche() { return idsouche; }
    public void setIdsouche(String idsouche) { this.idsouche = idsouche; }

    public double getQte() { return qte; }
    public void setQte(double qte) { this.qte = qte; }

    public double getPu() { return pu; }
    public void setPu(double pu) { this.pu = pu; }

    public String getFournisseur() { return fournisseur; }
    public void setFournisseur(String fournisseur) { this.fournisseur = fournisseur; }

    public String getProduitlib() { return produitlib; }
    public void setProduitlib(String produitlib) { this.produitlib = produitlib; }

    public String getSouchelibr() { return souchelibr; }
    public void setSouchelibr(String souchelibr) { this.souchelibr = souchelibr; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public String getIdproduitlib() { return idproduitlib; }
    public void setIdproduitlib(String idproduitlib) { this.idproduitlib = idproduitlib; }

    public String getFournisseurlib() { return fournisseurlib; }
    public void setFournisseurlib(String fournisseurlib) { this.fournisseurlib = fournisseurlib; }
}