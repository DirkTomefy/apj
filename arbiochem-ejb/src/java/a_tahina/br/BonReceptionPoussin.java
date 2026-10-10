package a_tahina.br;

import bean.ClassEtat;
import java.sql.Connection;
import java.sql.Date;

public class BonReceptionPoussin extends ClassEtat {

    private String id;
    private Date   daty;
    private String reference;
    private String idboncommande;
    private String idboncommandefille;
    private String idbrprecedent;
    private String idlieureception;
    private String idmagasin;
    private int    etat = 1;
    private double qtAvantCumule;
    private double qtApresCumule;
    private double maty;
    private double very;
    private String remarque;

    public BonReceptionPoussin() throws Exception {
        this.setNomTable("BONRECEPTION_POUSSIN");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("BRP", "GETSEQBRPOUSSIN");
        this.setId(makePK(c));
    }

    @Override public String getAttributIDName() { return "id"; }
    @Override public String getTuppleID()       { return this.getId(); }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Date getDaty() { return daty; }
    public void setDaty(Date daty) { this.daty = daty; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getIdboncommande() { return idboncommande; }
    public void setIdboncommande(String v) { this.idboncommande = v; }

    public String getIdboncommandefille() { return idboncommandefille; }
    public void setIdboncommandefille(String v) { this.idboncommandefille = v; }

    public String getIdbrprecedent() { return idbrprecedent; }
    public void setIdbrprecedent(String v) { this.idbrprecedent = v; }

    public String getIdlieureception() { return idlieureception; }
    public void setIdlieureception(String v) { this.idlieureception = v; }

    public String getIdmagasin() { return idmagasin; }
    public void setIdmagasin(String v) { this.idmagasin = v; }

    public int getEtat() { return etat; }
    public void setEtat(int etat) { this.etat = etat; }

    public double getQtAvantCumule() { return qtAvantCumule; }
    public void setQtAvantCumule(double v) { this.qtAvantCumule = v; }

    public double getQtApresCumule() { return qtApresCumule; }
    public void setQtApresCumule(double v) { this.qtApresCumule = v; }

    public double getMaty() { return maty; }
    public void setMaty(double v) { this.maty = v; }

    public double getVery() { return very; }
    public void setVery(double v) { this.very = v; }

    public String getRemarque() { return remarque; }
    public void setRemarque(String v) { this.remarque = v; }
}