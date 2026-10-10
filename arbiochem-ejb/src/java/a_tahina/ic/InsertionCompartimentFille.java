package a_tahina.ic;

import java.sql.Connection;
import bean.ClassFille;

public class InsertionCompartimentFille extends ClassFille {

    private String id;
    private String idmere;
    private String idcompartiment;
    private double qte;
    private double pu;
    private double valeurCompartiment;
    private String sexe;
    private String idsante;

    // Champs joints
    private String compartimentlib;
    private String sexelib;
    private String santelib;

    public InsertionCompartimentFille() throws Exception {
        this.setNomTable("INSERTION_COMPARTIMENT_FILLE");
        this.setNomClasseMere("a_tahina.ic.InsertionCompartiment");
        this.setLiaisonMere("idmere");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("ICF", "GETSEQINSERTIONCOMPFILLE");
        this.setId(makePK(c));
    }

    @Override public String getAttributIDName() { return "id"; }
    @Override public String getTuppleID()       { return this.getId(); }
    @Override public String getNomClasseMere()  { return "a_tahina.ic.InsertionCompartiment"; }
    @Override public String getLiaisonMere()    { return "idmere"; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdmere() { return idmere; }
    public void setIdmere(String idmere) { this.idmere = idmere; }

    public String getIdcompartiment() { return idcompartiment; }
    public void setIdcompartiment(String v) { this.idcompartiment = v; }

    public double getQte() { return qte; }
    public void setQte(double qte) { this.qte = qte; }

    public double getPu() { return pu; }
    public void setPu(double pu) { this.pu = pu; }

    public double getValeurCompartiment() { return valeurCompartiment; }
    public void setValeurCompartiment(double v) { this.valeurCompartiment = v; }

    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }

    public String getIdsante() { return idsante; }
    public void setIdsante(String v) { this.idsante = v; }

    public String getCompartimentlib() { return compartimentlib; }
    public void setCompartimentlib(String v) { this.compartimentlib = v; }

    public String getSexelib() { return sexelib; }
    public void setSexelib(String v) { this.sexelib = v; }

    public String getSantelib() { return santelib; }
    public void setSantelib(String v) { this.santelib = v; }
}