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

    public String getId() {
        return id;
    }
    public void setId(String v) {
        this.id = v;
    }

    public String getIdboncommandefille() {
        return idboncommandefille;
    }
    public void setIdboncommandefille(String v) {
        this.idboncommandefille = v;
    }

    public String getCompid() {
        return compid;
    }
    public void setCompid(String v) {
        this.compid = v;
    }

    public double getQtetheorique() {
        return qtetheorique;
    }
    public void setQtetheorique(double v) {
        this.qtetheorique = v;
    }

    public double getQteactuelle() {
        return qteactuelle;
    }
    public void setQteactuelle(double v) {
        this.qteactuelle = v;
    }

    
    public double getPu() {
        return pu;
    }
    public void setPu(double v) {
        this.pu = v;
    }

    public double getQtebc() {
        return qtebc;
    }
    public void setQtebc(double v) {
        this.qtebc = v;
    }

    public double getSommeqteactuelle() {
        return sommeqteactuelle;
    }
    public void setSommeqteactuelle(double v) {
        this.sommeqteactuelle = v;
    }

    public double getValeurunitaire() {
        return valeurunitaire;
    }
    public void setValeurunitaire(double v) {
        this.valeurunitaire = v;
    }
}