package a_tahina.facture;

import java.sql.Connection;
import java.sql.Date;

import bean.ClassMAPTable;

/** Part d'un avoir consommee par une facture. */
public class AthAvoirUsage extends ClassMAPTable {

    private String id;
    private String idavoir;
    private String idfacture;
    private double montant;
    private Date daty;

    public AthAvoirUsage() throws Exception {
        this.setNomTable("ATH_AVOIR_USAGE");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("AUS", "GETSEQATHAVOIRUSAGE");
        this.setId(makePK(c));
    }

    @Override public String getAttributIDName() { return "id"; }
    @Override public String getTuppleID()       { return this.getId(); }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdavoir() { return idavoir; }
    public void setIdavoir(String v) { this.idavoir = v; }
    public String getIdfacture() { return idfacture; }
    public void setIdfacture(String v) { this.idfacture = v; }
    public double getMontant() { return montant; }
    public void setMontant(double v) { this.montant = v; }
    public Date getDaty() { return daty; }
    public void setDaty(Date v) { this.daty = v; }
}
