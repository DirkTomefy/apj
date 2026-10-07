package a_tahina.poussin;

import bean.ClassMere;
import bean.ClassMAPTable;
import java.sql.Connection;
import java.sql.Date;

public class BonCommandePoussin extends ClassMere {

    private String id;
    private Date   daty;
    private String reference;
    private String iddmdachat;
    private String idfournisseur;
    private String modepaiement;
    private String iddevise;
    private String idmagasin;
    private Date   datelimite;
    private int    etat = 1;
    private String remarque;

    // Champs joints (lecture)
    private String fournisseurlib;
    private String modepaiementlib;
    private String iddeviselib;
    private String idmagasinlib;
    private String etatlib;

    public BonCommandePoussin() throws Exception {
        this.setNomTable("BONCOMMANDE_POUSSIN");
        this.setLiaisonFille("idmere");
        this.setNomClasseFille("a_tahina.poussin.BonCommandePoussinFille");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("BCP", "GETSEQBCPOUSSIN");
        this.setId(makePK(c));
    }

    @Override public String getTuppleID()       { return id; }
    @Override public String getAttributIDName() { return "id"; }
    @Override public String getNomClasseFille() { return "a_tahina.poussin.BonCommandePoussinFille"; }
    @Override public String getLiaisonFille()   { return "idmere"; }

    @Override
    public ClassMAPTable createObject(String u, Connection c) throws Exception {
        checkReference();
        return super.createObject(u, c);
    }

    public void checkReference() throws Exception {
        if (getMode().equals("modif")
                && (reference == null || reference.trim().isEmpty())) {
            throw new Exception("La référence du bon de commande est obligatoire");
        }
    }

    public void chargerFillesLib(Connection c) throws Exception {
        this.getFille("BONCOMMANDE_POUSSIN_FILLE_LIB", c,
                      " and idmere = '" + this.getId() + "'");
    }

    // ---- Getters / setters ----

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Date getDaty() { return daty; }
    public void setDaty(Date daty) { this.daty = daty; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getIddmdachat() { return iddmdachat; }
    public void setIddmdachat(String iddmdachat) { this.iddmdachat = iddmdachat; }
    public String getIdfournisseur() { return idfournisseur; }
    public void setIdfournisseur(String idfournisseur) throws Exception {
        if (getMode().equals("modif") && (idfournisseur == null || idfournisseur.isEmpty()))
            throw new Exception("Fournisseur obligatoire");
        this.idfournisseur = idfournisseur;
    }
    public String getModepaiement() { return modepaiement; }
    public void setModepaiement(String modepaiement) { this.modepaiement = modepaiement; }
    public String getIddevise() { return iddevise; }
    public void setIddevise(String iddevise) { this.iddevise = iddevise; }
    public String getIdmagasin() { return idmagasin; }
    public void setIdmagasin(String idmagasin) { this.idmagasin = idmagasin; }
    public Date getDatelimite() { return datelimite; }
    public void setDatelimite(Date datelimite) { this.datelimite = datelimite; }
    public int getEtat() { return etat; }
    public void setEtat(int etat) { this.etat = etat; }
    public String getRemarque() { return remarque; }
    public void setRemarque(String remarque) { this.remarque = remarque; }

    public String getFournisseurlib() { return fournisseurlib; }
    public void setFournisseurlib(String v) { this.fournisseurlib = v; }
    public String getModepaiementlib() { return modepaiementlib; }
    public void setModepaiementlib(String v) { this.modepaiementlib = v; }
    public String getIddeviselib() { return iddeviselib; }
    public void setIddeviselib(String v) { this.iddeviselib = v; }
    public String getIdmagasinlib() { return idmagasinlib; }
    public void setIdmagasinlib(String v) { this.idmagasinlib = v; }
    public String getEtatlib() { return etatlib; }
    public void setEtatlib(String v) { this.etatlib = v; }
}