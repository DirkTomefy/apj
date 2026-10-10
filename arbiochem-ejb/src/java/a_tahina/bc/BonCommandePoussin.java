package a_tahina.bc;
import bean.ClassMere;
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

    private String idmagasinlib;
    private String etatlib;

    public BonCommandePoussin() throws Exception {
        this.setNomTable("BONCOMMANDE_POUSSIN");
        this.setLiaisonFille("idmere");
        this.setNomClasseFille("a_tahina.poussin.BonCommandePoussinFille");
    }
    @Override
    public String getAttributIDName() {
       return "id";
    }
    @Override
    public String getTuppleID() {
        return this.getId();
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Date getDaty() {
        return daty;
    }
    public void setDaty(Date daty) {
        this.daty = daty;
    }
    public String getReference() {
        return reference;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }
    public String getDescri() {
        return descri;
    }
    public void setDescri(String descri) {
        this.descri = descri;
    }
    public String getIdmagasin() {
        return idmagasin;
    }
    public void setIdmagasin(String idmagasin) {
        this.idmagasin = idmagasin;
    }
    public int getEtat() {
        return etat;
    }
    public void setEtat(int etat) {
        this.etat = etat;
    }
    public String getRemarque() {
        return remarque;
    }
    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }
    public String getIdmagasinlib() {
        return idmagasinlib;
    }
    public void setIdmagasinlib(String idmagasinlib) {
        this.idmagasinlib = idmagasinlib;
    }
    public String getEtatlib() {
        return etatlib;
    }
    public void setEtatlib(String etatlib) {
        this.etatlib = etatlib;
    }
}
