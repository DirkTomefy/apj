package a_tahina.facture.orm;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import bean.CGenUtil;
import bean.ClassFille;
import bean.ClassMAPTable;
import bean.ClassMere;
import constante.ConstanteEtat;

import a_tahina.core.page.AtahinaUtil;
import a_tahina.core.Sexe;
import a_tahina.core.TypeVente;
import a_tahina.core.erreur.AtahinaException;
import a_tahina.core.erreur.Verif;


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
        this.setNomClasseFille("a_tahina.facture.orm.AthFactureFille");
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
        Verif.verifier(filles != null && filles.length > 0, "Une facture doit contenir au moins une ligne.");
        return super.createObject(u, c);
    }

    @Override
    public void controler(Connection c) throws Exception {
        super.controler(c);

        Verif.obligatoire(this.idclient, "Le client");
        Verif.existe(new AthClient().getById(this.idclient, null, c), "Client introuvable : " + this.idclient);
        Verif.obligatoire(this.daty, "La date");
        this.montantavoir = 0;   // fixe uniquement au visa

        ClassFille[] filles = this.getFille();
        if (filles == null) return;
        List<AthFactureFille> lignes = new ArrayList<AthFactureFille>();
        boolean estimer = false;
        for (ClassFille f : filles) {
            AthFactureFille l = (AthFactureFille) f;
            TypeVente type = TypeVente.obligatoire(l.getTypevente());
            l.setTypevente(type.code());
            l.setIdproduit(null);
            if (type == TypeVente.OEUF) {
                l.setSexe(null);
                Verif.verifier(l.getPu() > 0, "Le prix unitaire des oeufs est obligatoire.");
            } else {
                l.setSexe(Sexe.obligatoire(l.getSexe()).code());
            }
            Verif.verifier(l.getQte() > 0, "La quantite doit etre superieure a 0.");
            Verif.verifier(l.getPu() >= 0, "Le prix unitaire ne peut pas etre negatif.");
            Verif.verifier(l.getTva() >= 0 && l.getTva() <= 100, "La TVA doit etre comprise entre 0 et 100.");
            if (type == TypeVente.POULET && l.getPu() == 0) estimer = true;
            lignes.add(l);
        }
        if (estimer) AthVenteService.estimerPuPoulets(this, lignes, c);
    }

    // ------------------------------------------------------------------
    // Visa : deduction du credit d'avoir
    // ------------------------------------------------------------------
    @Override
    public Object validerObject(String u, Connection c) throws Exception {
        if (this.etat > ConstanteEtat.getEtatAnnuler() && this.etat < ConstanteEtat.getEtatValider()) {
            AthVenteService.controlerStock(this.id, c);
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
            throw new AtahinaException("Impossible d'annuler le visa : un avoir existe sur cette facture ("
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
