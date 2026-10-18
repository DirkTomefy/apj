package a_tahina.prevision;

import java.sql.Connection;

import bean.CGenUtil;
import bean.ClassMAPTable;

public class DeclPerteLot extends ClassMAPTable {

    private String id;
    private String idlot;
    private String sexe;
    private double qteMaty;
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
        if (this.idlot == null || this.idlot.isEmpty())
            throw new Exception("Lot obligatoire");
        if (this.qteMaty < 0 || this.qteVery < 0)
            throw new Exception("Quantites invalides");

        // Charger le lot pour deduire le sexe et verifier l'effectif
        Object[] arr = CGenUtil.rechercher(new Lot(), null, null, c,
                " AND id = '" + this.idlot + "' ");
        if (arr == null || arr.length == 0)
            throw new Exception("Lot introuvable : " + this.idlot);

        Lot lot = (Lot) arr[0];
        this.sexe = lot.getSexe();   // heriter du sexe

        // Verifier l'effectif restant
        double dejaMorts = 0, dejaPerdus = 0;
        Object[] arrD = CGenUtil.rechercher(new DeclPerteLot(), null, null, c,
                " AND idlot = '" + this.idlot + "' ");
        if (arrD != null) {
            for (Object o : arrD) {
                DeclPerteLot d = (DeclPerteLot) o;
                dejaMorts  += d.getQteMaty();
                dejaPerdus += d.getQteVery();
            }
        }
        double restant = lot.getQteinit() - dejaMorts - dejaPerdus;
        if (this.qteMaty + this.qteVery > restant)
            throw new Exception("Perte (" + (this.qteMaty + this.qteVery)
                    + ") superieure a l'effectif restant (" + (long) restant + ")");
    }

    // Getters / Setters
    public String getId()                    { return id; }
    public void   setId(String v)            { this.id = v; }
    public String getIdlot()                 { return idlot; }
    public void   setIdlot(String v)         { this.idlot = v; }
    public String getSexe()                  { return sexe; }
    public void   setSexe(String v)          { this.sexe = v; }
    public double getQteMaty()               { return qteMaty; }
    public void   setQteMaty(double v)       { this.qteMaty = v; }
    public double getQteVery()               { return qteVery; }
    public void   setQteVery(double v)       { this.qteVery = v; }
    public java.sql.Date getDaty()           { return daty; }
    public void   setDaty(java.sql.Date v)   { this.daty = v; }
    public String getReference()             { return reference; }
    public void   setReference(String v)     { this.reference = v; }
    public String getRemarque()              { return remarque; }
    public void   setRemarque(String v)      { this.remarque = v; }
}