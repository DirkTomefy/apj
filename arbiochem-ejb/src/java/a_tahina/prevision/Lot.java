package a_tahina.prevision;

import java.sql.Connection;
import java.sql.Date;

import bean.CGenUtil;
import bean.ClassMAPTable;

import a_tahina.br.BonReceptionPoussin;
import a_tahina.page.AtahinaUtil;

public class Lot extends ClassMAPTable {

    private String id;
    private String idbonreception;
    private String idproduit;
    private String idsouche;
    private double qteinit;
    private double pu;
    private String sexe;
    private String idsante;
    private String idinsertcompfille;
    private String idboncommande;
    private double qteActuelle;
    private Date dateEclosion;    

    public Lot() throws Exception {
        this.setNomTable("LOT");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("LOT", "GETSEQLOT");
        this.setId(makePK(c));
    }

    @Override public String getAttributIDName() { return "id"; }
    @Override public String getTuppleID()       { return this.getId(); }

    @Override
    public void controler(Connection c) throws Exception {

        // 1. Si QTEACTUELLE n'est pas definie, l'initialiser a QTEINIT
        if (this.qteActuelle == 0 && this.qteinit > 0) {
            this.qteActuelle = this.qteinit;
        }

        // 2. Si DATEECLOSION absente ET qu'on a un BR -> copier
        if (this.dateEclosion == null && this.idbonreception != null) {
            BonReceptionPoussin[] arr = (BonReceptionPoussin[]) CGenUtil.rechercher(
                    new BonReceptionPoussin(), null, null, c,
                    AtahinaUtil.egal("id", this.idbonreception));
            if (arr != null && arr.length > 0) {
                BonReceptionPoussin br = arr[0];
                if (br.getDateEclosion() != null) {
                    this.dateEclosion = br.getDateEclosion();
                }
            }
        }

        // 3. Verifications
        if (this.qteinit < 0) {
            throw new Exception("QTEINIT ne peut pas etre negatif");
        }
        if (this.sexe == null || (!this.sexe.equals("0") && !this.sexe.equals("1"))) {
            throw new Exception("SEXE doit etre '0' (Femelle) ou '1' (Male)");
        }
        if (this.dateEclosion == null) {
            throw new Exception("DATEECLOSION introuvable (BR " + this.idbonreception
                    + " sans date d'eclosion)");
        }
    }

    // =========================================================
    // Getters / Setters
    // =========================================================
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdbonreception() { return idbonreception; }
    public void setIdbonreception(String v) { this.idbonreception = v; }

    public String getIdproduit() { return idproduit; }
    public void setIdproduit(String v) { this.idproduit = v; }

    public String getIdsouche() { return idsouche; }
    public void setIdsouche(String v) { this.idsouche = v; }

    public double getQteinit() { return qteinit; }
    public void setQteinit(double qteinit) { this.qteinit = qteinit; }

    public double getPu() { return pu; }
    public void setPu(double v) { this.pu = v; }

    public String getSexe() { return sexe; }
    public void setSexe(String v) { this.sexe = v; }

    public String getIdsante() { return idsante; }
    public void setIdsante(String v) { this.idsante = v; }

    public String getIdinsertcompfille() { return idinsertcompfille; }
    public void setIdinsertcompfille(String v) { this.idinsertcompfille = v; }

    public String getIdboncommande() { return idboncommande; }
    public void setIdboncommande(String v) { this.idboncommande = v; }

    public double getQteActuelle() { return qteActuelle; }
    public void setQteActuelle(double v) { this.qteActuelle = v; }

    public Date getDateEclosion() { return dateEclosion; }
    public void setDateEclosion(Date v) { this.dateEclosion = v; }
}