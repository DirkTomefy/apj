package a_tahina.compartiment.orm;

import bean.ClassMAPTable;

public class SituationCompartiment extends ClassMAPTable {

    private String id;
    private String idboncommandefille;
    private String compid;
    private double qtetheorique;
    private double qteactuelle;
    private double pu;
    private double qtebc;
    private double sommeqteactuelle;
    private double valeurunitaire;

    public SituationCompartiment() throws Exception {
        setNomTable("SITUATION_COMPARTIMENT");
    }


    @Override
    public String getAttributIDName() {
        return "id";
    }

    @Override
    public String getTuppleID() {
        return id;
    }

    // -------- id --------
    public String getId() {
        return id;
    }
    public void setId(String v) {
        this.id = v;
    }

    // -------- idboncommandefille --------
    public String getIdboncommandefille() {
        return idboncommandefille;
    }
    public void setIdboncommandefille(String v) {
        this.idboncommandefille = v;
    }

    // -------- compid --------
    public String getCompid() {
        return compid;
    }
    public void setCompid(String v) {
        this.compid = v;
    }

    // -------- qtetheorique --------
    public double getQtetheorique() {
        return qtetheorique;
    }
    public void setQtetheorique(double v) {
        this.qtetheorique = v;
    }

    // -------- qteactuelle --------
    public double getQteactuelle() {
        return qteactuelle;
    }
    public void setQteactuelle(double v) {
        this.qteactuelle = v;
    }

    
    // -------- pu --------
    public double getPu() {
        return pu;
    }
    public void setPu(double v) {
        this.pu = v;
    }

    // -------- qtebc (ex QTE_BC) --------
    public double getQtebc() {
        return qtebc;
    }
    public void setQtebc(double v) {
        this.qtebc = v;
    }

    // -------- sommeqteactuelle (ex SOMME_QTEACTUELLE) --------
    public double getSommeqteactuelle() {
        return sommeqteactuelle;
    }
    public void setSommeqteactuelle(double v) {
        this.sommeqteactuelle = v;
    }

    // -------- valeurunitaire --------
    public double getValeurunitaire() {
        return valeurunitaire;
    }
    public void setValeurunitaire(double v) {
        this.valeurunitaire = v;
    }
}