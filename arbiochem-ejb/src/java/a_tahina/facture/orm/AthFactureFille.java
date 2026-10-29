package a_tahina.facture.orm;

import java.sql.Connection;

import bean.ClassFille;

public class AthFactureFille extends ClassFille {

    private String id;
    private String idmere;
    private String idproduit;
    private String typevente;
    private String sexe;
    private double qte;
    private double pu;
    private double tva;     // pourcentage, 0 par defaut

    public AthFactureFille() throws Exception {
        this.setNomTable("ATH_FACTURE_FILLE");
        this.setNomClasseMere("a_tahina.facture.orm.AthFacture");
        this.setLiaisonMere("idmere");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("AFL", "GETSEQATHFACTUREFILLE");
        this.setId(makePK(c));
    }

    @Override public String getAttributIDName() { return "id"; }
    @Override public String getTuppleID()       { return this.getId(); }
    @Override public String getNomClasseMere()  { return "a_tahina.facture.orm.AthFacture"; }
    @Override public String getLiaisonMere()    { return "idmere"; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdmere() { return idmere; }
    public void setIdmere(String v) { this.idmere = v; }
    public String getIdproduit() { return idproduit; }
    public void setIdproduit(String v) { this.idproduit = v; }
    public String getTypevente() { return typevente; }
    public void setTypevente(String v) { this.typevente = v; }
    public String getSexe() { return sexe; }
    public void setSexe(String v) { this.sexe = v; }
    public double getQte() { return qte; }
    public void setQte(double v) { this.qte = v; }
    public double getPu() { return pu; }
    public void setPu(double v) { this.pu = v; }
    public double getTva() { return tva; }
    public void setTva(double v) { this.tva = v; }
}
