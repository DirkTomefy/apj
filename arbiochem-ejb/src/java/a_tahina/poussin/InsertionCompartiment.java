package a_tahina.poussin;

import bean.CGenUtil;
import bean.ClassMere;
import bean.ClassMAPTable;
import java.sql.Connection;
import java.sql.Date;

public class InsertionCompartiment extends ClassMere {

    private String id;
    private Date   daty;
    private String reference;
    private String idlieu;
    private String idbonreception;
    private int    etat = 1;
    private String remarque;

    // Champs joints (lecture)
    private String idlieulib;
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
        checkCompartimentsLibres();
        return super.createObject(u, c);
    }

    public void checkCompartimentsLibres() throws Exception {
        InsertionCompartimentFille[] filles =
            (InsertionCompartimentFille[]) this.getFille();
        if (filles == null || filles.length == 0) return;

        for (InsertionCompartimentFille f : filles) {
            String idCompartiment = f.getIdcompartiment();
            if (idCompartiment == null || idCompartiment.trim().isEmpty()) continue;

            InsertionCompartimentFille crit = new InsertionCompartimentFille();
            crit.setIdcompartiment(idCompartiment);

            String aWhere = " and est_final = 1";
            if (this.getId() != null && !this.getId().trim().isEmpty()) {
                aWhere += " and idmere <> '" + this.getId() + "'";
            }

            InsertionCompartimentFille[] existantes =
                (InsertionCompartimentFille[]) CGenUtil.rechercher(crit, null, null, aWhere);

            if (existantes != null && existantes.length > 0) {
                throw new Exception("Le compartiment " + idCompartiment
                                  + " est déjà occupé par un autre lot.");
            }
        }
    }

    public void chargerFillesLib(Connection c) throws Exception {
        this.getFille("INSERTION_COMPARTIMENT_FILLE_LIB", c,
                      " and idmere = '" + this.getId() + "'");
    }

    // ---- Getters / setters ----

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Date getDaty() { return daty; }
    public void setDaty(Date daty) { this.daty = daty; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getIdlieu() { return idlieu; }
    public void setIdlieu(String idlieu) { this.idlieu = idlieu; }
    public String getIdbonreception() { return idbonreception; }
    public void setIdbonreception(String idbonreception) { this.idbonreception = idbonreception; }
    public int getEtat() { return etat; }
    public void setEtat(int etat) { this.etat = etat; }
    public String getRemarque() { return remarque; }
    public void setRemarque(String remarque) { this.remarque = remarque; }

    public String getIdlieulib() { return idlieulib; }
    public void setIdlieulib(String v) { this.idlieulib = v; }
    public String getIdbonreceptionlib() { return idbonreceptionlib; }
    public void setIdbonreceptionlib(String v) { this.idbonreceptionlib = v; }
    public String getEtatlib() { return etatlib; }
    public void setEtatlib(String v) { this.etatlib = v; }
}