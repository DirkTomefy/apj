package a_tahina.poussin;

import a_tahina.configuration.Compartiment;
import bean.CGenUtil;
import bean.ClassMere;
import bean.ClassMAPTable;
import java.sql.Connection;
import java.sql.Date;

public class InsertionCompartiment extends ClassMere {

    private String id;
    private Date   daty;
    private String reference;
    private String idbonreception;
    private int    etat = 1;
    private String remarque;

    // Champs joints (lecture seule)
    private String idbonreceptionlib;
    private String etatlib;

    public InsertionCompartiment() throws Exception {
        this.setNomTable("INSERTION_COMPARTIMENT");
        this.setLiaisonFille("idmere");
        this.setNomClasseFille("a_tahina.poussin.InsertionCompartimentFille");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("IC", "GETSEQINSERTIONCOMP");
        this.setId(makePK(c));
    }

    @Override public String getTuppleID()       { return id; }
    @Override public String getAttributIDName() { return "id"; }
    @Override public String getNomClasseFille() { return "a_tahina.poussin.InsertionCompartimentFille"; }
    @Override public String getLiaisonFille()   { return "idmere"; }

    @Override
    public ClassMAPTable createObject(String u, Connection c) throws Exception {
        checkCompartimentsLibres(c);
        return super.createObject(u, c);
    }

    /**
     * Anti-doublon : un compartiment déjà utilisé par une autre insertion
     * ne peut pas être réutilisé.
     */
    public void checkCompartimentsLibres(Connection c) throws Exception {
        InsertionCompartimentFille[] filles = (InsertionCompartimentFille[]) this.getFille();
        if (filles == null || filles.length == 0) return;

        for (InsertionCompartimentFille f : filles) {
            String idCompartiment = f.getIdcompartiment();
            if (idCompartiment == null || idCompartiment.trim().isEmpty()) continue;

            InsertionCompartimentFille crit = new InsertionCompartimentFille();
            crit.setIdcompartiment(idCompartiment);

            String aWhere = "";
            if (this.getId() != null && !this.getId().trim().isEmpty()) {
                aWhere = " and idmere <> '" + this.getId() + "'";
            }

            InsertionCompartimentFille[] existantes =
                (InsertionCompartimentFille[]) CGenUtil.rechercher(
                    crit, null, null, c, aWhere);

            if (existantes != null && existantes.length > 0) {
                throw new Exception("Le compartiment " + idCompartiment
                                  + " est déjà occupé par un autre lot.");
            }
        }
    }

    /** Recharge les filles depuis la BD avec libellés. */
    public void chargerFillesLib(Connection c) throws Exception {
        this.getFille("INSERTION_COMP_FILLE_LIB", c,
                      " and idmere = '" + this.getId() + "'");
    }

    // ---- Getters / Setters ----

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
    public void setRemarque(String remarque) { this.remarque = remarque; }

    public String getIdbonreceptionlib() { return idbonreceptionlib; }
    public void setIdbonreceptionlib(String v) { this.idbonreceptionlib = v; }

    public String getEtatlib() { return etatlib; }
    public void setEtatlib(String v) { this.etatlib = v; }
}