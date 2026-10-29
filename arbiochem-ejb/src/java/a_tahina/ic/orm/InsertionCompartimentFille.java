package a_tahina.ic.orm;

import a_tahina.compartiment.orm.Compartiment;

import java.sql.Connection;
import java.sql.Statement;
import bean.ClassFille;
import bean.ClassMAPTable;

import a_tahina.br.orm.BonReceptionPoussin;
import a_tahina.bc.orm.BonCommandePoussinFille;
import a_tahina.core.Sexe;
import a_tahina.core.erreur.AtahinaException;
import a_tahina.core.erreur.SaisieException;

public class InsertionCompartimentFille extends ClassFille {

    private String id;
    private String idmere;
    private String idcompartiment;
    private double qte;
    private double pu;
    private double valeurCompartiment;
    private String sexe;
    private String idsante;

    public InsertionCompartimentFille() throws Exception {
        this.setNomTable("INSERTION_COMPARTIMENT_FILLE");
        this.setNomClasseMere("a_tahina.ic.orm.InsertionCompartiment");
        this.setLiaisonMere("idmere");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("ICF", "GETSEQINSERTIONCOMPFILLE");
        this.setId(makePK(c));
    }

    @Override public String getAttributIDName() { return "id"; }
    @Override public String getTuppleID()       { return this.getId(); }
    @Override public String getNomClasseMere()  { return "a_tahina.ic.orm.InsertionCompartiment"; }
    @Override public String getLiaisonMere()    { return "idmere"; }

    
    @Override
    public ClassMAPTable createObjectBatch(String u, Statement st) throws Exception {
        calculerPuEtValeur(st.getConnection());
        return super.createObjectBatch(u, st);
    }


    @Override
    public ClassMAPTable createObject(String u, Connection c) throws Exception {
        calculerPuEtValeur(c);
        return super.createObject(u, c);
    }

  
    private void calculerPuEtValeur(Connection c) throws Exception {

        if (this.idcompartiment == null || this.idcompartiment.trim().isEmpty()) {
            throw new SaisieException("Compartiment obligatoire");
        }
        if (this.qte <= 0) {
            throw new SaisieException("Qte doit etre > 0");
        }
        Sexe.obligatoire(this.sexe);
        if (this.idsante == null || this.idsante.trim().isEmpty()) {
            throw new SaisieException("Sante obligatoire");
        }

        if (this.pu == 0 && this.idmere != null) {

            InsertionCompartiment ic = (InsertionCompartiment)
                    new InsertionCompartiment().getById(this.idmere, null, c);
            if (ic == null) {
                throw new AtahinaException("InsertionCompartiment introuvable : " + this.idmere);
            }
            if (ic.getIdbonreception() == null || ic.getIdbonreception().trim().isEmpty()) {
                throw new AtahinaException("La mere n'a pas de Bon de Reception associe");
            }

            BonReceptionPoussin br = (BonReceptionPoussin)
                    new BonReceptionPoussin().getById(ic.getIdbonreception(), null, c);
            if (br == null) {
                throw new AtahinaException("Bon de Reception introuvable : " + ic.getIdbonreception());
            }
            if (br.getIdboncommandefille() == null || br.getIdboncommandefille().trim().isEmpty()) {
                throw new AtahinaException("Le BR n'a pas de ligne BC associee");
            }

            BonCommandePoussinFille bcf = (BonCommandePoussinFille)
                    new BonCommandePoussinFille().getById(br.getIdboncommandefille(), null, c);
            if (bcf == null) {
                throw new AtahinaException("Ligne BC introuvable : " + br.getIdboncommandefille());
            }

            this.pu = bcf.getPu();
        }

        this.valeurCompartiment = this.qte * this.pu;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdmere() { return idmere; }
    public void setIdmere(String idmere) { this.idmere = idmere; }
    public String getIdcompartiment() { return idcompartiment; }
    public void setIdcompartiment(String v) { this.idcompartiment = v; }
    public double getQte() { return qte; }
    public void setQte(double qte) { this.qte = qte; }
    public double getPu() { return pu; }
    public void setPu(double pu) { this.pu = pu; }
    public double getValeurCompartiment() { return valeurCompartiment; }
    public void setValeurCompartiment(double v) { this.valeurCompartiment = v; }
    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }
    public String getIdsante() { return idsante; }
    public void setIdsante(String v) { this.idsante = v; }
}