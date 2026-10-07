package a_tahina.poussin;

import bean.ClassMere;
import bean.ClassMAPTable;
import java.sql.Connection;
import java.sql.Date;

public class BonReceptionPoussin extends ClassMere {

    private String id;
    private Date   daty;
    private String reference;
    private String idboncommande;
    private String idbrprecedent;
    private String typereception;
    private String idmagasin;
    private int    etat = 1;
    private String remarque;

    // Champs joints (lecture seule)
    private String idboncommandelib;
    private String idbrprecedentlib;
    private String typereceptionlib;
    private String idmagasinlib;
    private String etatlib;

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
        checkMagasin();
        return super.createObject(u, c);
    }

    /** Le magasin est obligatoire sauf si la réception est à l'aéroport. */
    public void checkMagasin() throws Exception {
        boolean estAeroport = "TR01".equalsIgnoreCase(this.typereception);
        if (!estAeroport && (this.idmagasin == null || this.idmagasin.trim().isEmpty())) {
            throw new Exception("Le magasin est obligatoire sauf pour une réception à l'aéroport");
        }
    }

    /** Recharge les filles depuis la BD avec libellés. */
    public void chargerFillesLib(Connection c) throws Exception {
        this.getFille("BONRECEPTION_POUSSIN_FILLE_LIB", c,
                      " and idmere = '" + this.getId() + "'");
    }

    // ---- Getters / Setters ----

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Date getDaty() { return daty; }
    public void setDaty(Date daty) { this.daty = daty; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getIdboncommande() { return idboncommande; }
    public void setIdboncommande(String v) { this.idboncommande = v; }

    public String getIdbrprecedent() { return idbrprecedent; }
    public void setIdbrprecedent(String v) { this.idbrprecedent = v; }

    public String getTypereception() { return typereception; }
    public void setTypereception(String v) { this.typereception = v; }

    public String getIdmagasin() { return idmagasin; }
    public void setIdmagasin(String idmagasin) { this.idmagasin = idmagasin; }

    public int getEtat() { return etat; }
    public void setEtat(int etat) { this.etat = etat; }

    public String getRemarque() { return remarque; }
    public void setRemarque(String remarque) { this.remarque = remarque; }

    public String getIdboncommandelib() { return idboncommandelib; }
    public void setIdboncommandelib(String v) { this.idboncommandelib = v; }

    public String getIdbrprecedentlib() { return idbrprecedentlib; }
    public void setIdbrprecedentlib(String v) { this.idbrprecedentlib = v; }

    public String getTypereceptionlib() { return typereceptionlib; }
    public void setTypereceptionlib(String v) { this.typereceptionlib = v; }

    public String getIdmagasinlib() { return idmagasinlib; }
    public void setIdmagasinlib(String v) { this.idmagasinlib = v; }

    public String getEtatlib() { return etatlib; }
    public void setEtatlib(String v) { this.etatlib = v; }
}