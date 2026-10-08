package a_tahina.configuration;

public class CompartimentValeur extends Compartiment {

    private double qteActuelle;
    private double valeurActuelle;
    private double pu;
    private double valeurTheorique;
    private double valeurNonRemplie;
    private double valeurUnitaire;   

    public CompartimentValeur() throws Exception {
        this.setNomTable("COMPARTIMENT_VALEUR_LIB");
    }

    public double getQteActuelle() { return qteActuelle; }
    public void setQteActuelle(double v) { this.qteActuelle = v; }

    public double getValeurActuelle() { return valeurActuelle; }
    public void setValeurActuelle(double v) { this.valeurActuelle = v; }

    public double getPu() { return pu; }
    public void setPu(double v) { this.pu = v; }

    public double getValeurTheorique() { return valeurTheorique; }
    public void setValeurTheorique(double v) { this.valeurTheorique = v; }

    public double getValeurNonRemplie() { return valeurNonRemplie; }
    public void setValeurNonRemplie(double v) { this.valeurNonRemplie = v; }

    public double getValeurUnitaire() { return valeurUnitaire; }
    public void setValeurUnitaire(double v) { this.valeurUnitaire = v; }
}