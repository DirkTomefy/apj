package a_tahina.poussin;

import bean.ClassMere;
import bean.ClassMAPTable;
import java.sql.Connection;
import java.sql.Date;

public class BonCommandePoussin extends ClassMere {

    private String id;
    private Date   daty;
    private String reference;
    private String descri;
    private String idmagasin;
    private int    etat = 1;
    private String remarque;

    // Champs joints (lecture seule)
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

    /** La référence est obligatoire. */
    public void checkReference() throws Exception {
        if (this.reference == null || this.reference.trim().isEmpty()) {
            throw new Exception("La référence du bon de commande est obligatoire");
        }
    }

    /** Recharge les filles depuis la BD avec libellés. */
    public void chargerFillesLib(Connection c) throws Exception {
        this.getFille("BONCOMMANDE_POUSSIN_FILLE_LIB", c,
                      " and idmere = '" + this.getId() + "'");
    }

    // ---- Getters / Setters ----

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Date getDaty() { return daty; }
    public void setDaty(Date daty) { this.daty = daty; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getDescri() { return descri; }
    public void setDescri(String descri) { this.descri = descri; }

    public String getIdmagasin() { return idmagasin; }
    public void setIdmagasin(String idmagasin) { this.idmagasin = idmagasin; }

    public int getEtat() { return etat; }
    public void setEtat(int etat) { this.etat = etat; }

    public String getRemarque() { return remarque; }
    public void setRemarque(String remarque) { this.remarque = remarque; }

    public String getIdmagasinlib() { return idmagasinlib; }
    public void setIdmagasinlib(String v) { this.idmagasinlib = v; }

    public String getEtatlib() { return etatlib; }
    public void setEtatlib(String v) { this.etatlib = v; }
}