package a_tahina.prevision.simulation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Elevage {

    public static final int    HORIZON_JOURS          = 2000;
    public static final int    DERNIERE_GEN_PONDEUSE  = 2;
    public static final int    DEF_NB_JOURS_ECLOSION  = 21;
    public static final int    DEF_SEMAINE_DEBUT_PONTE = 21;
    public static final int    DEF_DUREE_VIE_SEMAINES = 72;
    public static final double DEF_TAUX_ECLOSION      = 85;
    public static final double DEF_RATIO_FEMELLE      = 50;

    public static final String FEMELLE = "0";
    public static final String MALE    = "1";
    public static final String OEUF    = "OEUF";
    public static final String POULET  = "POULET";

    static final double EPS = 1e-6;

    private final Map<String, ParametreSouche> souches = new HashMap<String, ParametreSouche>();
    private final Map<String, Map<Integer, Double>> pontes = new HashMap<String, Map<Integer, Double>>();
    private final List<Cohorte> lots = new ArrayList<Cohorte>();
    private final List<Perte> pertes = new ArrayList<Perte>();
    private final List<Vente> ventes = new ArrayList<Vente>();

    // ------------------------------------------------------------------
    // Donnees
    // ------------------------------------------------------------------
    public void souche(String id, int nbJoursEclosion, int semaineDebutPonte, int dureeVieSemaines,
                       double tauxEclosion, double ratioFemelle) {
        souches.put(id, new ParametreSouche(
                nbJoursEclosion   > 0 ? nbJoursEclosion   : DEF_NB_JOURS_ECLOSION,
                semaineDebutPonte > 0 ? semaineDebutPonte : DEF_SEMAINE_DEBUT_PONTE,
                dureeVieSemaines  > 0 ? dureeVieSemaines  : DEF_DUREE_VIE_SEMAINES,
                tauxEclosion      > 0 ? tauxEclosion      : DEF_TAUX_ECLOSION,
                ratioFemelle      > 0 ? ratioFemelle      : DEF_RATIO_FEMELLE));
    }

    public void ponte(String idsouche, int semaine, double nbjour) {
        Map<Integer, Double> s = pontes.get(idsouche);
        if (s == null) pontes.put(idsouche, s = new HashMap<Integer, Double>());
        Double avant = s.get(semaine);
        s.put(semaine, avant == null ? nbjour : Math.max(avant, nbjour));
    }

    public void lot(String idsouche, String idcompartiment, String sexe, LocalDate eclosion, double qte, double pu) {
        if (eclosion == null || qte <= 0) return;
        lots.add(new Cohorte(eclosion, idsouche, sexe, 0, pu, idcompartiment, qte));
    }

    public void perte(LocalDate date, String idcompartiment, String sexe, double qte) {
        if (date == null || idcompartiment == null || qte <= 0) return;
        pertes.add(new Perte(date, idcompartiment, sexe, qte));
    }

    public Vente vente(String idfacture, String id, LocalDate date, String type, String sexe, double qte, double montant) {
        Vente v = new Vente(idfacture, id, date, type, sexe, qte, montant);
        ventes.add(v);
        return v;
    }

    // ------------------------------------------------------------------
    // Simulation jour par jour : eclosions, pertes, ponte, ventes
    // ------------------------------------------------------------------
    public Map<LocalDate, JourPrevision> simuler() {
        Map<LocalDate, JourPrevision> jours = new TreeMap<LocalDate, JourPrevision>();
        for (Vente v : ventes) v.reinitialiser();
        if (lots.isEmpty()) return jours;

        List<Cohorte> arrivees = new ArrayList<Cohorte>(lots);
        Collections.sort(arrivees, Comparator.comparing((Cohorte c) -> c.naissance));
        List<Perte> pertesTriees = new ArrayList<Perte>(pertes);
        Collections.sort(pertesTriees, Comparator.comparing((Perte p) -> p.date));
        List<Vente> ventesTriees = new ArrayList<Vente>(ventes);
        Collections.sort(ventesTriees, Comparator.comparing((Vente v) -> v.date)
                .thenComparing(v -> v.idfacture == null ? "" : v.idfacture)
                .thenComparing(v -> v.id == null ? "" : v.id));

        LocalDate debut = arrivees.get(0).naissance;
        LocalDate fin = arrivees.get(arrivees.size() - 1).naissance.plusDays(HORIZON_JOURS);
        for (Vente v : ventesTriees) if (v.date.isAfter(fin)) fin = v.date;

        List<Cohorte> cohortes = new ArrayList<Cohorte>();
        List<Couvee> couvees = new ArrayList<Couvee>();
        int iLot = 0, iPerte = 0, iVente = 0;

        for (LocalDate d = debut; !d.isAfter(fin); d = d.plusDays(1)) {
            JourPrevision j = new JourPrevision(d);

            while (iLot < arrivees.size() && !arrivees.get(iLot).naissance.isAfter(d)) {
                Cohorte c = arrivees.get(iLot++).copie();
                cohortes.add(c);
                j.naissance(c.sexe, c.qte);
            }
            eclore(d, couvees, cohortes, j);

            while (iPerte < pertesTriees.size() && !pertesTriees.get(iPerte).date.isAfter(d)) {
                retirerPerte(pertesTriees.get(iPerte++), cohortes, j);
            }

            pondre(d, cohortes, couvees, j);

            while (iVente < ventesTriees.size() && !ventesTriees.get(iVente).date.isAfter(d)) {
                vendre(ventesTriees.get(iVente++), cohortes, couvees, j);
            }

            for (Couvee c : couvees) j.stockOeufs += c.qte;
            for (Cohorte c : cohortes) {
                if (FEMELLE.equals(c.sexe)) j.femellesVivantes += c.qte;
                else                        j.malesVivants     += c.qte;
            }
            jours.put(d, j);
        }
        return jours;
    }

    public List<Vente> getVentes() { return Collections.unmodifiableList(ventes); }

    public List<Vente> nonServies() {
        List<Vente> r = new ArrayList<Vente>();
        for (Vente v : ventes) if (v.getManque() > EPS) r.add(v);
        return r;
    }

    private void eclore(LocalDate d, List<Couvee> couvees, List<Cohorte> cohortes, JourPrevision j) {
        for (int i = 0; i < couvees.size(); ) {
            Couvee c = couvees.get(i);
            if (c.eclosion.isAfter(d)) { i++; continue; }
            couvees.remove(i);
            ParametreSouche s = souche(c.souche);
            double poussins = c.qte * s.tauxEclosion / 100;
            double femelles = poussins * s.ratioFemelle / 100;
            ajouter(cohortes, new Cohorte(d, c.souche, FEMELLE, c.generation + 1, c.pu, null, femelles), j);
            ajouter(cohortes, new Cohorte(d, c.souche, MALE,    c.generation + 1, c.pu, null, poussins - femelles), j);
        }
    }

    private static void ajouter(List<Cohorte> cohortes, Cohorte c, JourPrevision j) {
        if (c.qte <= EPS) return;
        cohortes.add(c);
        j.naissance(c.sexe, c.qte);
    }

    private static void retirerPerte(Perte p, List<Cohorte> cohortes, JourPrevision j) {
        double total = 0;
        for (Cohorte c : cohortes) if (c.concerne(p)) total += c.qte;
        if (total <= EPS) return;
        double retire = Math.min(p.qte, total);
        for (Cohorte c : cohortes) {
            if (!c.concerne(p)) continue;
            double q = retire * c.qte / total;
            c.qte -= q;
            j.cout += q * c.pu;
        }
        j.perte(p.sexe, retire);
    }

    private void pondre(LocalDate d, List<Cohorte> cohortes, List<Couvee> couvees, JourPrevision j) {
        Map<String, Couvee> duJour = new HashMap<String, Couvee>();
        for (Cohorte c : cohortes) {
            if (!FEMELLE.equals(c.sexe) || c.qte <= EPS || c.generation > DERNIERE_GEN_PONDEUSE) continue;
            ParametreSouche s = souche(c.souche);
            int semaine = (int) (ChronoUnit.DAYS.between(c.naissance, d) / 7) + 1;
            if (semaine < s.semaineDebutPonte) continue;
            if (c.generation > 0 && semaine > s.dureeVieSemaines) continue;
            Map<Integer, Double> cfg = pontes.get(c.souche);
            Double nbjour = cfg == null ? null : cfg.get(semaine);
            if (nbjour == null || nbjour <= 0) continue;

            String cle = c.souche + "|" + c.generation + "|" + c.pu;
            Couvee cv = duJour.get(cle);
            if (cv == null) {
                cv = new Couvee(d.plusDays(s.nbJoursEclosion), c.souche, c.generation, c.pu);
                duJour.put(cle, cv);
                couvees.add(cv);
            }
            double oeufs = c.qte * nbjour;
            cv.qte += oeufs;
            j.oeufsPondus += oeufs;
        }
    }

    private static void vendre(Vente v, List<Cohorte> cohortes, List<Couvee> couvees, JourPrevision j) {
        double reste = v.qte;
        if (OEUF.equals(v.type)) {
            for (int i = couvees.size() - 1; i >= 0 && reste > EPS; i--) {
                Couvee c = couvees.get(i);
                double q = Math.min(reste, c.qte);
                c.qte -= q;
                reste -= q;
            }
            j.oeufsVendus += v.qte - reste;
        } else {
            for (int i = 0; i < cohortes.size() && reste > EPS; i++) {
                Cohorte c = cohortes.get(i);
                if (!c.sexe.equals(v.sexe)) continue;
                double q = Math.min(reste, c.qte);
                c.qte -= q;
                reste -= q;
                v.valeurStock += q * c.pu;
            }
            j.vente(v.sexe, v.qte - reste);
        }
        v.servi = v.qte - reste;
        j.ca += v.montant;
    }

    private ParametreSouche souche(String id) {
        ParametreSouche s = souches.get(id);
        if (s == null) {
            s = new ParametreSouche(DEF_NB_JOURS_ECLOSION, DEF_SEMAINE_DEBUT_PONTE, DEF_DUREE_VIE_SEMAINES,
                           DEF_TAUX_ECLOSION, DEF_RATIO_FEMELLE);
            souches.put(id, s);
        }
        return s;
    }
}
