package a_tahina.facture;

import bean.ClassMAPTable;

public class AthClientSolde extends ClassMAPTable {

    private String id;
    private String nom;
    private double pourcentageavoir;
    private double totalavoir;
    private double totalutilise;
    private double solde;

    public AthClientSolde() throws Exception {
        this.setNomTable("ATH_CLIENT_SOLDE");
    }

    @Override public String getAttributIDName() { return "id"; }
    @Override public String getTuppleID()       { return this.getId(); }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String v) { this.nom = v; }
    public double getPourcentageavoir() { return pourcentageavoir; }
    public void setPourcentageavoir(double v) { this.pourcentageavoir = v; }
    public double getTotalavoir() { return totalavoir; }
    public void setTotalavoir(double v) { this.totalavoir = v; }
    public double getTotalutilise() { return totalutilise; }
    public void setTotalutilise(double v) { this.totalutilise = v; }
    public double getSolde() { return solde; }
    public void setSolde(double v) { this.solde = v; }
}
