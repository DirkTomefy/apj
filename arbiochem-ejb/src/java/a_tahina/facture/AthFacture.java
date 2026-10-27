package a_tahina.facture;

import java.sql.Connection;
import java.sql.Date;

import bean.CGenUtil;
import bean.ClassFille;
import bean.ClassMAPTable;
import bean.ClassMere;
import constante.ConstanteEtat;

import a_tahina.page.AtahinaUtil;


public class AthFacture extends ClassMere {

    private String id;
    private Date daty;
    private String reference;
    private String idclient;
    private int etat = 1;
    private String remarque;
    private double montantavoir;

    public AthFacture() throws Exception {
        this.setNomTable("ATH_FACTURE");
        this.setLiaisonFille("idmere");
        this.setNomClasseFille("a_tahina.facture.AthFactureFille");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("AFC", "GETSEQATHFACTURE");
        this.setId(makePK(c));
    }

    @Override public String getAttributIDName() { return "id"; }
    @Override public String getTuppleID()       { return this.getId(); }

    // ------------------------------------------------------------------
    // Creation
    // ------------------------------------------------------------------
    @Override
    public ClassMAPTable createObject(String u, Connection c) throws Exception {
        ClassFille[] filles = this.getFille();
        if (filles == null || filles.length == 0) {
            throw new Exception("Une facture doit contenir au moins une ligne.");
        }
        return super.createObject(u, c);
    }

    @Override
    public void controler(Connection c) throws Exception {
        super.controler(c);

        if (this.idclient == null || this.idclient.trim().isEmpty()) {
            throw new Exception("Le client est obligatoire.");
        }
        if (new AthClient().getById(this.idclient, null, c) == null) {
            throw new Exception("Client introuvable : " + this.idclient);
        }
        this.montantavoir = 0;   // fixe uniquement au visa

        ClassFille[] filles = this.getFille();
        if (filles == null) return;
        for (ClassFille f : filles) {
            AthFactureFille l = (AthFactureFille) f;
            if (l.getIdproduit() == null || l.getIdproduit().trim().isEmpty()) {
                throw new Exception("Le produit est obligatoire sur chaque ligne.");
            }
            AthProduit p = (AthProduit) new AthProduit().getById(l.getIdproduit(), null, c);
            if (p == null) {
                throw new Exception("Produit introuvable : " + l.getIdproduit());
            }
            if (l.getQte() <= 0) {
                throw new Exception("La quantite doit etre superieure a 0 (" + p.getLibelle() + ").");
            }
            if (l.getPu() < 0) {
                throw new Exception("Le prix unitaire ne peut pas etre negatif (" + p.getLibelle() + ").");
            }
            if (l.getPu() == 0) {
                l.setPu(p.getPu());    // prix du produit par defaut
            }
            if (l.getTva() < 0 || l.getTva() > 100) {
                throw new Exception("La TVA doit etre comprise entre 0 et 100 (" + p.getLibelle() + ").");
            }
        }
    }

    // ------------------------------------------------------------------
    // Visa : deduction du credit d'avoir
    // ------------------------------------------------------------------
    @Override
    public Object validerObject(String u, Connection c) throws Exception {
        if (this.etat > ConstanteEtat.getEtatAnnuler() && this.etat < ConstanteEtat.getEtatValider()) {
            this.montantavoir = AthAvoirService.appliquer(this, u, c);
        }
        return super.validerObject(u, c);
    }

    // ------------------------------------------------------------------
    // Annulation du visa : restitution du credit
    // ------------------------------------------------------------------
    @Override
    public void controlerAnnulationVisa(Connection c) throws Exception {
        super.controlerAnnulationVisa(c);
        AthAvoir[] avoirs = (AthAvoir[]) CGenUtil.rechercher(new AthAvoir(), null, null, c,
                AtahinaUtil.egal("idfacture", this.id) + " and etat <> " + ConstanteEtat.getEtatAnnuler());
        if (avoirs != null && avoirs.length > 0) {
            throw new Exception("Impossible d'annuler le visa : un avoir existe sur cette facture ("
                    + avoirs[0].getId() + "). Annulez-le d'abord.");
        }
        AthAvoirService.restituer(this, c);
        this.montantavoir = 0;
    }

    // ---- Getters / Setters ----
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Date getDaty() { return daty; }
    public void setDaty(Date v) { this.daty = v; }
    public String getReference() { return reference; }
    public void setReference(String v) { this.reference = v; }
    public String getIdclient() { return idclient; }
    public void setIdclient(String v) { this.idclient = v; }
    public int getEtat() { return etat; }
    public void setEtat(int v) { this.etat = v; }
    public String getRemarque() { return remarque; }
    public void setRemarque(String v) { this.remarque = v; }
    public double getMontantavoir() { return montantavoir; }
    public void setMontantavoir(double v) { this.montantavoir = v; }
}
