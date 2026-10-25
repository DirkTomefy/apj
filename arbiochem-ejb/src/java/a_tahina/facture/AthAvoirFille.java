package a_tahina.facture;

import java.sql.Connection;

import bean.ClassFille;

/** Ligne d'avoir : retour de `qte` sur une ligne de facture (pu et tva repris de cette ligne). */
public class AthAvoirFille extends ClassFille {

    private String id;
    private String idmere;
    private String idfacturefille;
    private double qte;
    private double pu;
    private double tva;

    public AthAvoirFille() throws Exception {
        this.setNomTable("ATH_AVOIR_FILLE");
        this.setNomClasseMere("a_tahina.facture.AthAvoir");
        this.setLiaisonMere("idmere");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("AAL", "GETSEQATHAVOIRFILLE");
        this.setId(makePK(c));
    }

    @Override public String getAttributIDName() { return "id"; }
    @Override public String getTuppleID()       { return this.getId(); }
    @Override public String getNomClasseMere()  { return "a_tahina.facture.AthAvoir"; }
    @Override public String getLiaisonMere()    { return "idmere"; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdmere() { return idmere; }
    public void setIdmere(String v) { this.idmere = v; }
    public String getIdfacturefille() { return idfacturefille; }
    public void setIdfacturefille(String v) { this.idfacturefille = v; }
    public double getQte() { return qte; }
    public void setQte(double v) { this.qte = v; }
    public double getPu() { return pu; }
    public void setPu(double v) { this.pu = v; }
    public double getTva() { return tva; }
    public void setTva(double v) { this.tva = v; }
}
