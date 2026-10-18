package a_tahina.br;

import java.sql.Connection;
import java.sql.Date;

import a_tahina.bc.BonCommandePoussinFille;
import bean.CGenUtil;
import bean.ClassEtat;

public class BonReceptionPoussin extends ClassEtat {

    private String id;
    private Date daty;
    private String reference;
    private String idboncommande;
    private String idboncommandefille;
    private String idbrprecedent;
    private String idlieureception;
    private String idmagasin;
    private int etat = 1;
    private double qtAvantCumule;
    private double qtApresCumule;
    private double maty;
    private double very;
    private String remarque;
    private Date dateEclosion;

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

    // ==================================================================
    // CONTROLER
    // ==================================================================
    @Override
    public void controler(Connection c) throws Exception {
        super.controler(c);

        // 1. Verifier la ligne BC
        if (this.idboncommandefille == null || this.idboncommandefille.trim().isEmpty()) {
            throw new Exception("La ligne BC est obligatoire.");
        }

        BonCommandePoussinFille bcf = new BonCommandePoussinFille();
        bcf = (BonCommandePoussinFille) bcf.getById(this.idboncommandefille, null, c);
        if (bcf == null) {
            throw new Exception("Ligne BC introuvable : " + this.idboncommandefille);
        }
        this.idboncommande = bcf.getIdmere();

        // 2. Chercher le dernier BR sur cette ligne BC
        BonReceptionPoussin crit = new BonReceptionPoussin();
        BonReceptionPoussin[] precedents = (BonReceptionPoussin[]) CGenUtil.rechercher(
                crit, null, null, c,
                " AND idboncommandefille = '" + this.idboncommandefille + "'"
                        + " ORDER BY daty DESC, id DESC");

        // 3. Calcul des cumuls (gere le premier BR)
        double quantiteDisponible;
        if (precedents != null && precedents.length > 0) {
            // --- BR SUIVANT ---
            BonReceptionPoussin dernier = precedents[0];
            this.idbrprecedent = dernier.getId();
            this.qtAvantCumule = dernier.getQtApresCumule();
            quantiteDisponible = this.qtAvantCumule;
        } else {
            // --- PREMIER BR : pas de precedent ---
            this.idbrprecedent = null;
            this.qtAvantCumule = (int) bcf.getQte();
            quantiteDisponible = bcf.getQte();   // on part de la qte commandee
        }

        // 4. Calculer automatiquement qtApresCumule
        this.qtApresCumule = quantiteDisponible - this.maty - this.very;

        // 5. Validations
        if (this.maty < 0 || this.very < 0) {
            throw new Exception("Les pertes ne peuvent pas etre negatives.");
        }
        if (this.qtApresCumule < 0) {
            throw new Exception("Les pertes (" + (this.maty + this.very)
                    + ") depassent la quantite disponible (" + quantiteDisponible + ").");
        }

        // 6. Validation DATEECLOSION
        if (this.dateEclosion == null) {
            throw new Exception("La date d'eclosion est obligatoire.");
        }
        if (this.daty != null && this.dateEclosion.after(this.daty)) {
            throw new Exception("La date d'eclosion (" + this.dateEclosion
                    + ") ne peut pas etre posterieure a la date de reception ("
                    + this.daty + ").");
        }
    }

    // Getters / Setters (inchanges)
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
    public Date getDateEclosion() { return dateEclosion; }
    public void setDateEclosion(Date v) { this.dateEclosion = v; }
}