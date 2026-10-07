package a_tahina.poussin;

import bean.ClassFille;
import java.sql.Connection;

public class BonReceptionPoussinFille extends ClassFille {

    private String id;
    private String idmere;
    private String idproduit;
    private String idsouche;
    private double qte;
    private double pu;
    private double maty;
    private double very;

    // Champs joints (lecture seule)
    private String produitlib;
    private String souchelibr;
    private double montant;

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
    public void setQte(double qte) { this.qte = qte; }

    public double getPu() { return pu; }
    public void setPu(double pu) { this.pu = pu; }

    public double getMaty() { return maty; }
    public void setMaty(double maty) { this.maty = maty; }

    public double getVery() { return very; }
    public void setVery(double very) { this.very = very; }

    public String getProduitlib() { return produitlib; }
    public void setProduitlib(String v) { this.produitlib = v; }

    public String getSouchelibr() { return souchelibr; }
    public void setSouchelibr(String v) { this.souchelibr = v; }

    public double getMontant() { return montant; }
    public void setMontant(double v) { this.montant = v; }
}