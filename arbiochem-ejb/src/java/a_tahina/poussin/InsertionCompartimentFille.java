package a_tahina.poussin;

import bean.ClassFille;
import java.sql.Connection;

public class InsertionCompartimentFille extends ClassFille {

    private String id;
    private String idmere;
    private String idcompartiment;
    private double qte;
    private double pu;
    private double valeurCompartiment;   // = pu * qte
    private String sexe;
    private String idsante;

    // Champs joints (lecture seule)
    private String compartimentlib;
    private String sexelib;
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

    /** Le setter de qte recalcule automatiquement la valeur. */
    public void setQte(double qte) {
        this.qte = qte;
        recalculerValeur();
    }

    /** Le setter de pu recalcule automatiquement la valeur. */
    public void setPu(double pu) {
        this.pu = pu;
        recalculerValeur();
    }

    private void recalculerValeur() {
        this.valeurCompartiment = this.qte * this.pu;
    }

    // ---- Getters / Setters ----

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdmere() { return idmere; }
    public void setIdmere(String idmere) { this.idmere = idmere; }

    public String getIdcompartiment() { return idcompartiment; }
    public void setIdcompartiment(String idcompartiment) { this.idcompartiment = idcompartiment; }

    public double getQte() { return qte; }

    public double getPu() { return pu; }

    public double getValeurCompartiment() { return valeurCompartiment; }
    public void setValeurCompartiment(double v) { this.valeurCompartiment = v; }

    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }

    public String getIdsante() { return idsante; }
    public void setIdsante(String idsante) { this.idsante = idsante; }

    public String getCompartimentlib() { return compartimentlib; }
    public void setCompartimentlib(String v) { this.compartimentlib = v; }

    public String getSexelib() { return sexelib; }
    public void setSexelib(String v) { this.sexelib = v; }

    public String getSantelib() { return santelib; }
    public void setSantelib(String v) { this.santelib = v; }
}