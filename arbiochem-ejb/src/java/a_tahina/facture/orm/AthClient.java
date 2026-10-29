package a_tahina.facture.orm;

import java.sql.Connection;

import bean.ClassMAPTable;
import a_tahina.core.erreur.SaisieException;


public class AthClient extends ClassMAPTable {

    private String id;
    private String nom;
    private String contact;
    private String adresse;
    private String mail;
    private double pourcentageavoir = 40;

    public AthClient() throws Exception {
        this.setNomTable("ATH_CLIENT");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("ACL", "GETSEQATHCLIENT");
        this.setId(makePK(c));
    }

    @Override public String getAttributIDName() { return "id"; }
    @Override public String getTuppleID()       { return this.getId(); }

    @Override
    public void controler(Connection c) throws Exception {
        super.controler(c);
        if (this.nom == null || this.nom.trim().isEmpty()) {
            throw new SaisieException("Le nom du client est obligatoire.");
        }
        if (this.pourcentageavoir < 0 || this.pourcentageavoir > 100) {
            throw new SaisieException("Le pourcentage d'avoir doit etre compris entre 0 et 100.");
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String v) { this.nom = v; }
    public String getContact() { return contact; }
    public void setContact(String v) { this.contact = v; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String v) { this.adresse = v; }
    public String getMail() { return mail; }
    public void setMail(String v) { this.mail = v; }
    public double getPourcentageavoir() { return pourcentageavoir; }
    public void setPourcentageavoir(double v) { this.pourcentageavoir = v; }
}
