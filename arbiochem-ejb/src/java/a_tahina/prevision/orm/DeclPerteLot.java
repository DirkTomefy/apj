package a_tahina.prevision.orm;

import java.sql.Connection;

import a_tahina.compartiment.orm.Compartiment;
import bean.ClassMAPTable;
import a_tahina.core.Sexe;
import a_tahina.core.erreur.AtahinaException;
import a_tahina.core.erreur.SaisieException;

public class DeclPerteLot extends ClassMAPTable {

    private String id;
    private String idcompartiment;
    private String sexe;
    private double qteMort;
    private double qteVery;
    private java.sql.Date daty;
    private String reference;
    private String remarque;

    public DeclPerteLot() throws Exception {
        super();
        setNomTable("decl_perte_lot");
    }

    @Override public String getTuppleID()       { return id; }
    @Override public String getAttributIDName() { return "id"; }

    @Override
    public void construirePK(Connection c) throws Exception {
        preparePk("DPL", "GETSEQDECL_PERTE_LOT");
        setId(makePK(c));
    }

    @Override
    public void controler(Connection c) throws Exception {
        if (this.idcompartiment == null || this.idcompartiment.isEmpty())
            throw new SaisieException("Compartiment obligatoire");

        Sexe.obligatoire(this.sexe);

        if (this.qteMort < 0 || this.qteVery < 0)
            throw new SaisieException("Quantites invalides");
        if (this.qteMort + this.qteVery <= 0)
            throw new SaisieException("Saisir au moins une perte (morts ou perdus)");

        // La vue de prevision ignore les pertes sans date
        if (this.daty == null)
            this.daty = new java.sql.Date(System.currentTimeMillis());

        // Le compartiment doit exister
        if (new Compartiment().getById(this.idcompartiment, null, c) == null)
            throw new AtahinaException("Compartiment introuvable : " + this.idcompartiment);
    }

    // Getters / Setters
    public String getId()                    { return id; }
    public void   setId(String v)            { this.id = v; }
    public String getIdcompartiment()        { return idcompartiment; }
    public void   setIdcompartiment(String v){ this.idcompartiment = v; }
    public String getSexe()                  { return sexe; }
    public void   setSexe(String v)          { this.sexe = v; }
    public double getQteMort()               { return qteMort; }
    public void   setQteMort(double v)       { this.qteMort = v; }
    public double getQteVery()               { return qteVery; }
    public void   setQteVery(double v)       { this.qteVery = v; }
    public java.sql.Date getDaty()           { return daty; }
    public void   setDaty(java.sql.Date v)   { this.daty = v; }
    public String getReference()             { return reference; }
    public void   setReference(String v)     { this.reference = v; }
    public String getRemarque()              { return remarque; }
    public void   setRemarque(String v)      { this.remarque = v; }
}