package a_tahina.prevision;

import java.sql.Connection;

import bean.CGenUtil;
import bean.ClassMAPTable;

/**
 * Declaration de perte sur un lot.
 *
 * Integration avec la prevision (vue CALENDRIER_POUSSIN_RAW) :
 *  - la vue ignore les lignes dont DATY est NULL -> la date est donc
 *    obligatoire (par defaut : aujourd'hui) pour que la perte compte ;
 *  - une perte sur un lot femelle (sexe '0') reduit le nombre de poules,
 *    donc les poussins issus de la ponte pondue a partir de cette date
 *    (ils apparaissent nb_jours_eclosion jours plus tard) ;
 *  - une perte sur un lot male s'affiche dans le calendrier (case rouge)
 *    mais ne change pas la prevision de ponte.
 */
public class DeclPerteLot extends ClassMAPTable {

    private String id;
    private String idlot;
    private String sexe;
    private double qteMaty;
    private double qteVery;
    private java.sql.Date daty;
    private String reference;
    private String remarque;

    public DeclPerteLot() throws Exception {
        super();
        setNomTable("decl_perte_lot");
    }

    @Override public String getTuppleID()       { return id; }
    @Override public String getAttributIDName() { return "id"; }

    @Override
    public void construirePK(Connection c) throws Exception {
        preparePk("DPL", "GETSEQDECL_PERTE_LOT");
        setId(makePK(c));
    }

    @Override
    public void controler(Connection c) throws Exception {
        if (this.idlot == null || this.idlot.isEmpty())
            throw new Exception("Lot obligatoire");
        if (this.qteMaty < 0 || this.qteVery < 0)
            throw new Exception("Quantites invalides");
        if (this.qteMaty + this.qteVery <= 0)
            throw new Exception("Saisir au moins une perte (morts ou perdus)");

        // La vue de prevision ignore les pertes sans date
        if (this.daty == null)
            this.daty = new java.sql.Date(System.currentTimeMillis());

        String idLotSafe = this.idlot.replace("'", "''");

        // Charger le lot pour deduire le sexe et verifier l'effectif
        Object[] arr = CGenUtil.rechercher(new Lot(), null, null, c,
                " AND id = '" + idLotSafe + "' ");
        if (arr == null || arr.length == 0)
            throw new Exception("Lot introuvable : " + this.idlot);

        Lot lot = (Lot) arr[0];
        this.sexe = lot.getSexe();   // heriter du sexe

        // Une perte ne peut pas preceder l'eclosion du lot
        if (lot.getDateEclosion() != null && this.daty.before(lot.getDateEclosion()))
            throw new Exception("La date de perte (" + this.daty
                    + ") est anterieure a l'eclosion du lot (" + lot.getDateEclosion() + ")");

        // Verifier l'effectif restant (hors la declaration en cours de modification)
        String filtre = " AND idlot = '" + idLotSafe + "' ";
        if (this.id != null && !this.id.isEmpty())
            filtre += " AND id <> '" + this.id.replace("'", "''") + "' ";

        double dejaMorts = 0, dejaPerdus = 0;
        Object[] arrD = CGenUtil.rechercher(new DeclPerteLot(), null, null, c, filtre);
        if (arrD != null) {
            for (Object o : arrD) {
                DeclPerteLot d = (DeclPerteLot) o;
                dejaMorts  += d.getQteMaty();
                dejaPerdus += d.getQteVery();
            }
        }
        double restant = lot.getQteinit() - dejaMorts - dejaPerdus;
        if (this.qteMaty + this.qteVery > restant)
            throw new Exception("Perte (" + (long) (this.qteMaty + this.qteVery)
                    + ") superieure a l'effectif restant (" + (long) restant + ")");
    }

    // Getters / Setters
    public String getId()                    { return id; }
    public void   setId(String v)            { this.id = v; }
    public String getIdlot()                 { return idlot; }
    public void   setIdlot(String v)         { this.idlot = v; }
    public String getSexe()                  { return sexe; }
    public void   setSexe(String v)          { this.sexe = v; }
    public double getQteMaty()               { return qteMaty; }
    public void   setQteMaty(double v)       { this.qteMaty = v; }
    public double getQteVery()               { return qteVery; }
    public void   setQteVery(double v)       { this.qteVery = v; }
    public java.sql.Date getDaty()           { return daty; }
    public void   setDaty(java.sql.Date v)   { this.daty = v; }
    public String getReference()             { return reference; }
    public void   setReference(String v)     { this.reference = v; }
    public String getRemarque()              { return remarque; }
    public void   setRemarque(String v)      { this.remarque = v; }
}