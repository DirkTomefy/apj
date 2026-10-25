package a_tahina.prevision;

import bean.ClassMAPTable;


public class Fournisseur extends ClassMAPTable {

    private String id;
    private String nom;
    private String nif;
    private String stat;
    private String adresse;
    private String contact;
    private String codepostal;
    private String compte;
    private String idtypefournisseur;
    private String compteauxiliaire;
    private int   estactif;
    private double echeance;
    private String devise;
    private String mail;
    private String banque;

    public Fournisseur() throws Exception {
        super();
        setNomTable("fournisseur");
    }

    @Override public String getTuppleID()       { return id; }
    @Override public String getAttributIDName() { return "id"; }

    public String getId()                          { return id; }
    public void   setId(String v)                  { this.id = v; }
    public String getNom()                         { return nom; }
    public void   setNom(String v)                 { this.nom = v; }
    public String getNif()                         { return nif; }
    public void   setNif(String v)                 { this.nif = v; }
    public String getStat()                        { return stat; }
    public void   setStat(String v)                { this.stat = v; }
    public String getAdresse()                     { return adresse; }
    public void   setAdresse(String v)             { this.adresse = v; }
    public String getContact()                     { return contact; }
    public void   setContact(String v)             { this.contact = v; }
    public String getCodepostal()                  { return codepostal; }
    public void   setCodepostal(String v)          { this.codepostal = v; }
    public String getCompte()                      { return compte; }
    public void   setCompte(String v)              { this.compte = v; }
    public String getIdtypefournisseur()           { return idtypefournisseur; }
    public void   setIdtypefournisseur(String v)   { this.idtypefournisseur = v; }
    public String getCompteauxiliaire()            { return compteauxiliaire; }
    public void   setCompteauxiliaire(String v)    { this.compteauxiliaire = v; }
    public int   getEstactif()                    { return estactif; }
    public void   setEstactif(int v)              { this.estactif = v; }
    public double getEcheance()                    { return echeance; }
    public void   setEcheance(double v)            { this.echeance = v; }
    public String getDevise()                      { return devise; }
    public void   setDevise(String v)              { this.devise = v; }
    public String getMail()                        { return mail; }
    public void   setMail(String v)                { this.mail = v; }
    public String getBanque()                      { return banque; }
    public void   setBanque(String v)              { this.banque = v; }
}