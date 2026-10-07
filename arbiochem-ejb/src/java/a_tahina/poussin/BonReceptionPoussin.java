package a_tahina.poussin;

import bean.ClassMere;
import bean.ClassMAPTable;
import java.sql.Connection;
import java.sql.Date;

public class BonReceptionPoussin extends ClassMere {

    private String id;
    private Date   daty;
    private String reference;
    private String idlieureception;
    private String idboncommandepoussin;
    private String idbonreceptionprecedent;
    private int    est_final = 0;
    private String idfournisseur;
    private int    etat = 1;
    private String remarque;

    // Champs joints (lecture)
    private String idlieureceptionlib;
    private String fournisseurlib;
    private String etatlib;
    private String est_finallib;

    public BonReceptionPoussin() throws Exception {
        this.setNomTable("BONRECEPTION_POUSSIN");
        this.setLiaisonFille("idmere");
        this.setNomClasseFille("a_tahina.poussin.BonReceptionPoussinFille");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("BRP", "GETSEQBRPOUSSIN");
        this.setId(makePK(c));
    }

    @Override public String getTuppleID()       { return id; }
    @Override public String getAttributIDName() { return "id"; }
    @Override public String getNomClasseFille() { return "a_tahina.poussin.BonReceptionPoussinFille"; }
    @Override public String getLiaisonFille()   { return "idmere"; }

    @Override
    public ClassMAPTable createObject(String u, Connection c) throws Exception {
        checkOrigine();
        return super.createObject(u, c);
    }

    public void checkOrigine() throws Exception {
        boolean hasBc = idboncommandepoussin != null && !idboncommandepoussin.trim().isEmpty();
        boolean hasBr = idbonreceptionprecedent != null && !idbonreceptionprecedent.trim().isEmpty();

        if (!hasBc && !hasBr)
            throw new Exception("Il faut préciser soit un bon de commande, "
                              + "soit un bon de réception antécédent");
        if (hasBc && hasBr)
            throw new Exception("Impossible de préciser un BC ET un BR antécédent à la fois");
    }

    public void chargerFillesLib(Connection c) throws Exception {
        this.getFille("BONRECEPTION_POUSSIN_FILLE_LIB", c,
                      " and idmere = '" + this.getId() + "'");
    }

    // ---- Getters / setters ----

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Date getDaty() { return daty; }
    public void setDaty(Date daty) { this.daty = daty; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getIdlieureception() { return idlieureception; }
    public void setIdlieureception(String v) { this.idlieureception = v; }
    public String getIdboncommandepoussin() { return idboncommandepoussin; }
    public void setIdboncommandepoussin(String v) { this.idboncommandepoussin = v; }
    public String getIdbonreceptionprecedent() { return idbonreceptionprecedent; }
    public void setIdbonreceptionprecedent(String v) { this.idbonreceptionprecedent = v; }
    public int getEst_final() { return est_final; }
    public void setEst_final(int est_final) { this.est_final = est_final; }
    public String getIdfournisseur() { return idfournisseur; }
    public void setIdfournisseur(String idfournisseur) { this.idfournisseur = idfournisseur; }
    public int getEtat() { return etat; }
    public void setEtat(int etat) { this.etat = etat; }
    public String getRemarque() { return remarque; }
    public void setRemarque(String remarque) { this.remarque = remarque; }

    public String getIdlieureceptionlib() { return idlieureceptionlib; }
    public void setIdlieureceptionlib(String v) { this.idlieureceptionlib = v; }
    public String getFournisseurlib() { return fournisseurlib; }
    public void setFournisseurlib(String v) { this.fournisseurlib = v; }
    public String getEtatlib() { return etatlib; }
    public void setEtatlib(String v) { this.etatlib = v; }
    public String getEst_finallib() { return est_finallib; }
    public void setEst_finallib(String v) { this.est_finallib = v; }
}