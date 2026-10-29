package a_tahina.ic.orm;

import java.sql.Connection;
import java.sql.Date;

import a_tahina.br.orm.BonReceptionPoussinLib;
import a_tahina.lot.orm.Lot;
import bean.ClassFille;
import bean.ClassMAPTable;
import bean.ClassMere;
import a_tahina.core.erreur.AtahinaException;

public class InsertionCompartiment extends ClassMere {

    private String id;
    private Date   daty;
    private String reference;
    private String idbonreception;
    private int    etat = 1;
    private String remarque;

    public InsertionCompartiment() throws Exception {
        this.setNomTable("INSERTION_COMPARTIMENT");
        this.setLiaisonFille("idmere");
        this.setNomClasseFille("a_tahina.ic.orm.InsertionCompartimentFille");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("ICP", "GETSEQINSERTIONCOMP");
        this.setId(makePK(c));
    }

    @Override public String getAttributIDName() { return "id"; }
    @Override public String getTuppleID()       { return this.getId(); }

  
    @Override
    public ClassMAPTable createObject(String u, Connection c) throws Exception {

        super.createObject(u, c);

        ClassFille[] filles = this.getFille();
        if (filles != null && filles.length > 0) {

            BonReceptionPoussinLib br = new BonReceptionPoussinLib();
            br = (BonReceptionPoussinLib) br.getById(this.idbonreception, null, c);
            if (br == null) {
                throw new AtahinaException("BR introuvable : " + this.idbonreception);
            }

            for (ClassFille f : filles) {
                InsertionCompartimentFille icf = (InsertionCompartimentFille) f;

                Lot lot = new Lot();
                lot.setIdbonreception(this.idbonreception);
                lot.setIdboncommande(br.getIdboncommande());
                lot.setIdproduit(br.getIdboncommandefilleproduit());
                lot.setIdsouche(br.getIdsouche());
                lot.setQteinit(icf.getQte());
                lot.setPu(icf.getPu());
                lot.setSexe(icf.getSexe());
                lot.setIdsante(icf.getIdsante());
                lot.setIdinsertcompfille(icf.getId());
                lot.setQteActuelle(icf.getQte());

                lot.createObject(u, c);
            }
        }

        return this;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Date getDaty() { return daty; }
    public void setDaty(Date daty) { this.daty = daty; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getIdbonreception() { return idbonreception; }
    public void setIdbonreception(String v) { this.idbonreception = v; }

    public int getEtat() { return etat; }
    public void setEtat(int etat) { this.etat = etat; }

    public String getRemarque() { return remarque; }
    public void setRemarque(String v) { this.remarque = v; }
}