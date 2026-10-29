package a_tahina.prevision.simulation;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import bean.CGenUtil;
import bean.ClassMAPTable;
import constante.ConstanteEtat;

import a_tahina.bc.orm.BonCommandePoussinFille;
import a_tahina.facture.orm.AthFactureFilleLib;
import a_tahina.ic.orm.InsertionCompartimentFille;
import a_tahina.lot.orm.Lot;
import a_tahina.prevision.orm.ConfigPrevision;
import a_tahina.prevision.orm.DeclPerteLot;
import a_tahina.prevision.orm.SoucheParametre;

public final class ChargeurElevage {

    private ChargeurElevage() {}

    public static Elevage charger(Connection c) throws Exception {
        Elevage e = new Elevage();

        for (Object o : lire(new SoucheParametre(), c, "")) {
            SoucheParametre s = (SoucheParametre) o;
            e.souche(s.getId(), (int) s.getNb_jours_eclosion(), (int) s.getSemaine_debut_ponte(),
                     (int) s.getDuree_vie_semaines(), s.getTaux_eclosion(), s.getRatio_femelle());
        }
        for (Object o : lire(new ConfigPrevision(), c, "")) {
            ConfigPrevision p = (ConfigPrevision) o;
            e.ponte(p.getIdsouche(), p.getSemaine(), p.getNbjour());
        }

        Map<String, double[]> puCommande = new HashMap<String, double[]>();
        for (Object o : lire(new BonCommandePoussinFille(), c, "")) {
            BonCommandePoussinFille f = (BonCommandePoussinFille) o;
            double[] t = puCommande.computeIfAbsent(f.getIdmere() + "|" + f.getIdsouche(), k -> new double[2]);
            t[0] += f.getQte() * f.getPu();
            t[1] += f.getQte();
        }
        Map<String, String> compartiments = new HashMap<String, String>();
        for (Object o : lire(new InsertionCompartimentFille(), c, "")) {
            InsertionCompartimentFille f = (InsertionCompartimentFille) o;
            compartiments.put(f.getId(), f.getIdcompartiment());
        }
        for (Object o : lire(new Lot(), c, " and dateeclosion is not null")) {
            Lot l = (Lot) o;
            double[] t = puCommande.get(l.getIdboncommande() + "|" + l.getIdsouche());
            double pu = t != null && t[1] > 0 ? t[0] / t[1] : l.getPu();
            e.lot(l.getIdsouche(), compartiments.get(l.getIdinsertcompfille()), l.getSexe(),
                  date(l.getDateEclosion()), l.getQteinit(), pu);
        }

        for (Object o : lire(new DeclPerteLot(), c, " and daty is not null")) {
            DeclPerteLot p = (DeclPerteLot) o;
            e.perte(date(p.getDaty()), p.getIdcompartiment(), p.getSexe(), p.getQteMort() + p.getQteVery());
        }

        for (Object o : lire(new AthFactureFilleLib(), c, " and typevente is not null and etatfacture in ("
                + ConstanteEtat.getEtatValider() + ", " + ConstanteEtat.getEtatCloture() + ")")) {
            vente(e, (AthFactureFilleLib) o);
        }
        return e;
    }

    public static Vente vente(Elevage e, AthFactureFilleLib l) {
        return e.vente(l.getIdmere(), l.getId(), date(l.getDaty()), l.getTypevente(), l.getSexe(),
                       l.getQte(), l.getQte() * l.getPu());
    }

    private static Object[] lire(ClassMAPTable critere, Connection c, String apresWhere) throws Exception {
        Object[] r = CGenUtil.rechercher(critere, null, null, c, apresWhere);
        return r == null ? new Object[0] : r;
    }

    static LocalDate date(java.util.Date d) {
        if (d == null) return null;
        return d instanceof java.sql.Date ? ((java.sql.Date) d).toLocalDate()
                                          : new java.sql.Date(d.getTime()).toLocalDate();
    }
}
