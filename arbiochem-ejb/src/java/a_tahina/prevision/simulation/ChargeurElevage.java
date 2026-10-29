package a_tahina.prevision.simulation;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import constante.ConstanteEtat;

import a_tahina.bc.orm.BonCommandePoussinFille;
import a_tahina.core.Sexe;
import a_tahina.core.TypeVente;
import a_tahina.core.erreur.SaisieException;
import a_tahina.core.simulation.Chargeur;
import a_tahina.facture.orm.AthFactureFilleLib;
import a_tahina.ic.orm.InsertionCompartimentFille;
import a_tahina.lot.orm.Lot;
import a_tahina.prevision.orm.ConfigPrevision;
import a_tahina.prevision.orm.DeclPerteLot;
import a_tahina.prevision.orm.SoucheParametre;

public class ChargeurElevage extends Chargeur<Elevage> {

    @Override
    protected Elevage nouvelleSimulation() {
        return new Elevage();
    }

    @Override
    protected void remplir(Elevage e, Connection c) throws Exception {
        chargerSouches(e, c);
        chargerPontes(e, c);
        chargerLots(e, c);
        chargerPertes(e, c);
        chargerVentes(e, c);
    }

    private void chargerSouches(Elevage e, Connection c) throws Exception {
        for (SoucheParametre s : lire(new SoucheParametre(), c)) {
            e.souche(s.getId(), (int) s.getNb_jours_eclosion(), (int) s.getSemaine_debut_ponte(),
                     (int) s.getDuree_vie_semaines(), s.getTaux_eclosion(), s.getRatio_femelle());
        }
    }

    private void chargerPontes(Elevage e, Connection c) throws Exception {
        for (ConfigPrevision p : lire(new ConfigPrevision(), c)) {
            e.ponte(p.getIdsouche(), p.getSemaine(), p.getNbjour());
        }
    }

    private void chargerLots(Elevage e, Connection c) throws Exception {
        Map<String, Double> puCommande = puParCommande(c);
        Map<String, String> compartiments = compartimentParInsertion(c);
        for (Lot l : lire(new Lot(), c, " and dateeclosion is not null")) {
            Double pu = puCommande.get(cleCommande(l.getIdboncommande(), l.getIdsouche()));
            e.lot(l.getIdsouche(), compartiments.get(l.getIdinsertcompfille()), Sexe.depuis(l.getSexe()),
                  date(l.getDateEclosion()), l.getQteinit(), pu != null ? pu : l.getPu());
        }
    }

    private Map<String, Double> puParCommande(Connection c) throws Exception {
        Map<String, double[]> cumuls = new HashMap<String, double[]>();
        for (BonCommandePoussinFille f : lire(new BonCommandePoussinFille(), c)) {
            double[] t = cumuls.computeIfAbsent(cleCommande(f.getIdmere(), f.getIdsouche()), k -> new double[2]);
            t[0] += f.getQte() * f.getPu();
            t[1] += f.getQte();
        }
        Map<String, Double> pu = new HashMap<String, Double>();
        for (Map.Entry<String, double[]> t : cumuls.entrySet()) {
            if (t.getValue()[1] > 0) pu.put(t.getKey(), t.getValue()[0] / t.getValue()[1]);
        }
        return pu;
    }

    private Map<String, String> compartimentParInsertion(Connection c) throws Exception {
        Map<String, String> compartiments = new HashMap<String, String>();
        for (InsertionCompartimentFille f : lire(new InsertionCompartimentFille(), c)) {
            compartiments.put(f.getId(), f.getIdcompartiment());
        }
        return compartiments;
    }

    private static String cleCommande(String idboncommande, String idsouche) {
        return idboncommande + "|" + idsouche;
    }

    private void chargerPertes(Elevage e, Connection c) throws Exception {
        for (DeclPerteLot p : lire(new DeclPerteLot(), c, " and daty is not null")) {
            e.perte(date(p.getDaty()), p.getIdcompartiment(), Sexe.depuis(p.getSexe()), p.getQteMort() + p.getQteVery());
        }
    }

    private void chargerVentes(Elevage e, Connection c) throws Exception {
        String visees = " and typevente is not null and etatfacture in ("
                + ConstanteEtat.getEtatValider() + ", " + ConstanteEtat.getEtatCloture() + ")";
        for (AthFactureFilleLib l : lire(new AthFactureFilleLib(), c, visees)) vente(e, l);
    }

    public static Vente vente(Elevage e, AthFactureFilleLib l) throws SaisieException {
        return e.vente(l.getIdmere(), l.getId(), date(l.getDaty()), TypeVente.depuis(l.getTypevente()), Sexe.depuis(l.getSexe()),
                       l.getQte(), l.getQte() * l.getPu());
    }
}
