package a_tahina.facture.orm;

import java.sql.Connection;

import bean.ClassMAPTable;

public class AthProduit extends ClassMAPTable {

    private String id;
    private String libelle;
    private String unite;
    private double pu;
    private int actif = 1;

    public AthProduit() throws Exception {
        this.setNomTable("ATH_PRODUIT");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("APR", "GETSEQATHPRODUIT");
        this.setId(makePK(c));
    }

    @Override public String getAttributIDName() { return "id"; }
    @Override public String getTuppleID()       { return this.getId(); }

    @Override
    public void controler(Connection c) throws Exception {
        super.controler(c);
        if (this.libelle == null || this.libelle.trim().isEmpty()) {
            throw new Exception("Le libelle du produit est obligatoire.");
        }
        if (this.pu < 0) {
            throw new Exception("Le prix unitaire ne peut pas etre negatif.");
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String v) { this.libelle = v; }
    public String getUnite() { return unite; }
    public void setUnite(String v) { this.unite = v; }
    public double getPu() { return pu; }
    public void setPu(double v) { this.pu = v; }
    public int getActif() { return actif; }
    public void setActif(int v) { this.actif = v; }
}
