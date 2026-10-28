package a_tahina.bc.orm;

public class BonCommandePoussinFilleLib extends BonCommandePoussinFille {

    private String produitlib;
    private String souchelibr;
    private double montant;
    private String idproduitlib;
    private String fournisseurlib;

    public BonCommandePoussinFilleLib() throws Exception {
        this.setNomTable("BONCOMMANDE_POUSSIN_FILLE_LIB");
    }

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
