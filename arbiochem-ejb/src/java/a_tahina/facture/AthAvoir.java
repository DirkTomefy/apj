package a_tahina.facture;

import java.sql.Connection;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import bean.ClassFille;
import bean.ClassMAPTable;
import bean.ClassMere;
import constante.ConstanteEtat;


public class AthAvoir extends ClassMere {

    private static final double EPS = 0.000001;

    private String id;
    private Date daty;
    private String reference;
    private String idfacture;
    private String idclient;
    private int etat = 1;
    private String motif;
    private String remarque;

    public AthAvoir() throws Exception {
        this.setNomTable("ATH_AVOIR");
        this.setLiaisonFille("idmere");
        this.setNomClasseFille("a_tahina.facture.AthAvoirFille");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("AAV", "GETSEQATHAVOIR");
        this.setId(makePK(c));
    }

    @Override public String getAttributIDName() { return "id"; }
    @Override public String getTuppleID()       { return this.getId(); }

    @Override
    public ClassMAPTable createObject(String u, Connection c) throws Exception {
        ClassFille[] filles = this.getFille();
        if (filles == null || filles.length == 0) {
            throw new Exception("Un avoir doit contenir au moins une ligne.");
        }
        return super.createObject(u, c);
    }

    /**
     * Controles a la creation :
     *  - la facture doit etre visee ;
     *  - chaque ligne doit appartenir a cette facture ;
     *  - quantite retournee (cumulee avec les autres avoirs non annules) <= quantite facturee.
     * pu et tva des lignes sont repris de la facture.
     */
    @Override
    public void controler(Connection c) throws Exception {
        super.controler(c);

        if (this.idfacture == null || this.idfacture.trim().isEmpty()) {
            throw new Exception("La facture est obligatoire.");
        }
        AthFactureLib facture = (AthFactureLib) new AthFactureLib().getById(this.idfacture, null, c);
        if (facture == null) {
            throw new Exception("Facture introuvable : " + this.idfacture);
        }
        if (facture.getEtat() != ConstanteEtat.getEtatValider()) {
            throw new Exception("Un avoir ne peut etre cree que sur une facture visee.");
        }
        this.idclient = facture.getIdclient();

        ClassFille[] filles = this.getFille();
        if (filles == null) return;

        Map<String, Double> cumul = new HashMap<String, Double>();
        for (ClassFille f : filles) {
            AthAvoirFille l = (AthAvoirFille) f;
            if (l.getIdfacturefille() == null || l.getIdfacturefille().trim().isEmpty()) {
                throw new Exception("La ligne de facture est obligatoire sur chaque ligne d'avoir.");
            }
            if (l.getQte() <= 0) {
                throw new Exception("La quantite retournee doit etre superieure a 0.");
            }
            AthFactureFilleLib ligne = (AthFactureFilleLib)
                    new AthFactureFilleLib().getById(l.getIdfacturefille(), null, c);
            if (ligne == null || !this.idfacture.equals(ligne.getIdmere())) {
                throw new Exception("La ligne " + l.getIdfacturefille()
                        + " n'appartient pas a la facture " + this.idfacture + ".");
            }
            Double deja = cumul.get(ligne.getId());
            double total = (deja == null ? 0 : deja) + l.getQte();
            cumul.put(ligne.getId(), total);
            if (total > ligne.getQtedisponible() + EPS) {
                throw new Exception("Quantite retournee (" + total + ") superieure a la quantite disponible ("
                        + ligne.getQtedisponible() + ") pour " + ligne.getProduitlib() + ".");
            }
            l.setPu(ligne.getPu());
            l.setTva(ligne.getTva());
        }
    }

    /** Un avoir dont le credit est deja (en partie) consomme ne peut plus etre devise. */
    @Override
    public void controlerAnnulationVisa(Connection c) throws Exception {
        super.controlerAnnulationVisa(c);
        AthAvoirLib a = (AthAvoirLib) new AthAvoirLib().getById(this.id, null, c);
        if (a != null && a.getMontantutilise() > EPS) {
            throw new Exception("Impossible d'annuler le visa : " + a.getMontantutilise()
                    + " de cet avoir ont deja ete deduits sur des factures.");
        }
    }

    // ---- Getters / Setters ----
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Date getDaty() { return daty; }
    public void setDaty(Date v) { this.daty = v; }
    public String getReference() { return reference; }
    public void setReference(String v) { this.reference = v; }
    public String getIdfacture() { return idfacture; }
    public void setIdfacture(String v) { this.idfacture = v; }
    public String getIdclient() { return idclient; }
    public void setIdclient(String v) { this.idclient = v; }
    public int getEtat() { return etat; }
    public void setEtat(int v) { this.etat = v; }
    public String getMotif() { return motif; }
    public void setMotif(String v) { this.motif = v; }
    public String getRemarque() { return remarque; }
    public void setRemarque(String v) { this.remarque = v; }
}
