package a_tahina.prevision.simulation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import a_tahina.core.Sexe;
import a_tahina.core.TypeVente;
import a_tahina.core.simulation.FileEvenements;
import a_tahina.core.simulation.Simulation;

public class Elevage extends Simulation<JourPrevision, ResultatElevage> {

    public static final int    HORIZON_JOURS          = 2000;
    public static final int    DERNIERE_GEN_PONDEUSE  = 2;
    public static final int    DEF_NB_JOURS_ECLOSION  = 21;
    public static final int    DEF_SEMAINE_DEBUT_PONTE = 21;
    public static final int    DEF_DUREE_VIE_SEMAINES = 72;
    public static final double DEF_TAUX_ECLOSION      = 85;
    public static final double DEF_RATIO_FEMELLE      = 50;

    public static final Gestion GESTION_OEUF    = Gestion.LIFO;
    public static final Gestion GESTION_POUSSIN = Gestion.FIFO;

    static final double EPS = 1e-6;

    private final Map<String, ParametreSouche> souches = new HashMap<String, ParametreSouche>();
    private final Map<String, Map<Integer, Double>> pontes = new HashMap<String, Map<Integer, Double>>();
    private final List<Cohorte> lots = new ArrayList<Cohorte>();
    private final List<Perte> pertes = new ArrayList<Perte>();
    private final List<Vente> ventes = new ArrayList<Vente>();

    private FileEvenements<Cohorte> arrivees;
    private FileEvenements<Perte> pertesAVenir;
    private FileEvenements<Vente> ventesAVenir;
    private List<Cohorte> cohortes;
    private List<Couvee> couvees;
    private ResultatElevage resultat;
    private double femellesVivantes;
    private double malesVivants;
    private double stockOeufs;

    public void souche(String id, int nbJoursEclosion, int semaineDebutPonte, int dureeVieSemaines,
                       double tauxEclosion, double ratioFemelle) {
        souches.put(id, new ParametreSouche(
                ouDefaut(nbJoursEclosion,   DEF_NB_JOURS_ECLOSION),
                ouDefaut(semaineDebutPonte, DEF_SEMAINE_DEBUT_PONTE),
                ouDefaut(dureeVieSemaines,  DEF_DUREE_VIE_SEMAINES),
                ouDefaut(tauxEclosion,      DEF_TAUX_ECLOSION),
                ouDefaut(ratioFemelle,      DEF_RATIO_FEMELLE)));
    }

    public void ponte(String idsouche, int semaine, double nbjour) {
        pontes.computeIfAbsent(idsouche, k -> new HashMap<Integer, Double>()).merge(semaine, nbjour, Math::max);
    }

    public void lot(String idsouche, String idcompartiment, Sexe sexe, LocalDate eclosion, double qte, double pu) {
        if (eclosion == null || sexe == null || qte <= 0) return;
        lots.add(new Cohorte(eclosion, idsouche, sexe, 0, pu, idcompartiment, qte));
    }

    public void perte(LocalDate date, String idcompartiment, Sexe sexe, double qte) {
        if (date == null || idcompartiment == null || sexe == null || qte <= 0) return;
        pertes.add(new Perte(date, idcompartiment, sexe, qte));
    }

    public Vente vente(String idfacture, String id, LocalDate date, TypeVente type, Sexe sexe, double qte, double montant) {
        Vente v = new Vente(idfacture, id, date, type, sexe, qte, montant);
        ventes.add(v);
        return v;
    }

    public LocalDate derniereVente() {
        LocalDate derniere = null;
        for (Vente v : ventes) if (derniere == null || v.date.isAfter(derniere)) derniere = v.date;
        return derniere;
    }

    @Override
    protected ResultatElevage preparer() {
        arrivees = new FileEvenements<Cohorte>(lots, c -> c.naissance);
        pertesAVenir = new FileEvenements<Perte>(pertes, p -> p.date);
        ventesAVenir = new FileEvenements<Vente>(ventes, v -> v.date, Vente.ORDRE_DU_JOUR);
        cohortes = new ArrayList<Cohorte>();
        couvees = new ArrayList<Couvee>();
        femellesVivantes = 0;
        malesVivants = 0;
        stockOeufs = 0;
        resultat = new ResultatElevage(ventes);
        return resultat;
    }

    @Override
    protected LocalDate debut() {
        return arrivees.premiereDate();
    }

    @Override
    protected LocalDate fin() {
        LocalDate fin = arrivees.derniereDate().plusDays(HORIZON_JOURS);
        LocalDate derniereVente = ventesAVenir.derniereDate();
        return derniereVente != null && derniereVente.isAfter(fin) ? derniereVente : fin;
    }

    @Override
    protected JourPrevision simulerJour(LocalDate d) {
        JourPrevision j = new JourPrevision(d);
        for (Cohorte lot : arrivees.jusquA(d)) naitre(lot.copie(), j);
        eclore(d, j);
        reformer(d, j);
        for (Perte p : pertesAVenir.jusquA(d)) repartirPerte(p, j);
        pondre(d, j);
        for (Vente v : ventesAVenir.jusquA(d)) vendre(v, j);
        retirerStocksVides();
        dresserBilan(j);
        return j;
    }

    private void eclore(LocalDate d, JourPrevision j) {
        for (Iterator<Couvee> it = couvees.iterator(); it.hasNext(); ) {
            Couvee c = it.next();
            if (c.eclosion.isAfter(d)) continue;
            it.remove();
            stockOeufs -= c.qte;
            j.oeufsEclos += c.qte;
            faireEclore(c, d, j);
        }
    }

    private void faireEclore(Couvee c, LocalDate d, JourPrevision j) {
        ParametreSouche s = parametres(c.souche);
        double poussins = c.qte * s.tauxEclosion / 100;
        double femelles = poussins * s.ratioFemelle / 100;
        naitre(new Cohorte(d, c.souche, Sexe.FEMELLE, c.generation + 1, c.pu, c.compartiment, femelles), j);
        naitre(new Cohorte(d, c.souche, Sexe.MALE,    c.generation + 1, c.pu, c.compartiment, poussins - femelles), j);
    }

    private void naitre(Cohorte c, JourPrevision j) {
        if (c.qte <= EPS) return;
        c.reforme = c.naissance.plusWeeks(parametres(c.souche).dureeVieSemaines);
        cohortes.add(c);
        ajusterVivants(c.sexe, c.qte);
        j.naissance(c.sexe, c.qte);
    }

    private void reformer(LocalDate d, JourPrevision j) {
        for (Iterator<Cohorte> it = cohortes.iterator(); it.hasNext(); ) {
            Cohorte c = it.next();
            if (d.isBefore(c.reforme)) continue;
            it.remove();
            ajusterVivants(c.sexe, -c.qte);
            j.reforme(c.sexe, c.qte);
        }
    }

    private void repartirPerte(Perte p, JourPrevision j) {
        List<Cohorte> touchees = cohortesConcernees(p);
        double total = quantite(touchees);
        if (total <= EPS) return;
        double retire = Math.min(p.qte, total);
        for (Cohorte c : touchees) {
            j.cout += c.prelever(retire * c.qte / total) * c.pu;
        }
        ajusterVivants(p.sexe, -retire);
        j.perte(p.sexe, retire);
    }

    private List<Cohorte> cohortesConcernees(Perte p) {
        List<Cohorte> r = new ArrayList<Cohorte>();
        for (Cohorte c : cohortes) if (c.concerne(p)) r.add(c);
        return r;
    }

    private void pondre(LocalDate d, JourPrevision j) {
        Map<String, Couvee> couveesDuJour = new HashMap<String, Couvee>();
        for (Cohorte poule : cohortes) {
            double oeufs = poule.qte * oeufsParPoule(poule, d);
            if (oeufs <= 0) continue;
            couveeDuJour(couveesDuJour, poule, d).qte += oeufs;
            stockOeufs += oeufs;
            j.oeufsPondus += oeufs;
        }
    }

    private double oeufsParPoule(Cohorte poule, LocalDate d) {
        if (!peutPondre(poule)) return 0;
        int semaine = semaineDeVie(poule.naissance, d);
        if (semaine < parametres(poule.souche).semaineDebutPonte) return 0;
        return ponteConfiguree(poule.souche, semaine);
    }

    private static boolean peutPondre(Cohorte c) {
        return c.sexe == Sexe.FEMELLE && c.qte > EPS && c.generation <= DERNIERE_GEN_PONDEUSE;
    }

    private static int semaineDeVie(LocalDate naissance, LocalDate d) {
        return (int) (ChronoUnit.DAYS.between(naissance, d) / 7) + 1;
    }

    private double ponteConfiguree(String idsouche, int semaine) {
        Map<Integer, Double> config = pontes.get(idsouche);
        Double nbjour = config == null ? null : config.get(semaine);
        return nbjour == null ? 0 : Math.max(0, nbjour);
    }

    private Couvee couveeDuJour(Map<String, Couvee> couveesDuJour, Cohorte mere, LocalDate d) {
        String cle = mere.souche + "|" + mere.compartiment + "|" + mere.generation + "|" + mere.pu;
        return couveesDuJour.computeIfAbsent(cle, k -> {
            Couvee c = new Couvee(d.plusDays(parametres(mere.souche).nbJoursEclosion), mere.souche, mere.compartiment,
                                  mere.generation, mere.pu);
            couvees.add(c);
            return c;
        });
    }

    private void vendre(Vente v, JourPrevision j) {
        ServiceVente s = resultat.service(v);
        if (v.type == TypeVente.OEUF) {
            servir(s, couvees, GESTION_OEUF);
            stockOeufs -= s.servi;
            j.oeufsVendus += s.servi;
        } else {
            servir(s, cohortesDuSexe(v.sexe), GESTION_POUSSIN);
            ajusterVivants(v.sexe, -s.servi);
            j.vente(v.sexe, s.servi);
        }
        j.ca += v.montant;
    }

    private static void servir(ServiceVente s, List<? extends Stock> stocks, Gestion gestion) {
        double reste = s.getVente().qte;
        for (int i = 0; i < stocks.size() && reste > EPS; i++) {
            Stock stock = stocks.get(gestion.indice(i, stocks.size()));
            double q = stock.prelever(reste);
            reste -= q;
            s.valeurStock += q * stock.pu;
        }
        s.servi = s.getVente().qte - reste;
    }

    private void retirerStocksVides() {
        for (Iterator<Cohorte> it = cohortes.iterator(); it.hasNext(); ) {
            Cohorte c = it.next();
            if (c.qte > EPS) continue;
            it.remove();
            ajusterVivants(c.sexe, -c.qte);
        }
        for (Iterator<Couvee> it = couvees.iterator(); it.hasNext(); ) {
            Couvee c = it.next();
            if (c.qte > EPS) continue;
            it.remove();
            stockOeufs -= c.qte;
        }
    }

    private void ajusterVivants(Sexe sexe, double q) {
        if (sexe == Sexe.FEMELLE) femellesVivantes += q; else malesVivants += q;
    }

    private void dresserBilan(JourPrevision j) {
        j.stockOeufs = stockOeufs;
        j.femellesVivantes = femellesVivantes;
        j.malesVivants = malesVivants;
    }

    @Override
    protected void verifierJour(JourPrevision veille, JourPrevision j) {
        double vivantsVeille = veille == null ? 0 : veille.getVivants();
        double oeufsVeille = veille == null ? 0 : veille.stockOeufs;
        egal(j, "vivants", vivantsVeille + j.getNaissances() - j.getPertes() - j.getPouletsVendus() - j.getReformes(), j.getVivants());
        egal(j, "stock d'oeufs", oeufsVeille + j.oeufsPondus - j.oeufsEclos - j.oeufsVendus, j.stockOeufs);
        egal(j, "femelles comptees", quantite(cohortesDuSexe(Sexe.FEMELLE)), j.femellesVivantes);
        egal(j, "males comptes", quantite(cohortesDuSexe(Sexe.MALE)), j.malesVivants);
        egal(j, "oeufs comptes", quantite(couvees), j.stockOeufs);
    }

    private static void egal(JourPrevision j, String grandeur, double attendu, double obtenu) {
        if (Math.abs(attendu - obtenu) > 1e-4 + 1e-9 * Math.abs(attendu)) {
            throw new IllegalStateException("Invariant faux le " + j.daty + " (" + grandeur + ") : attendu "
                    + attendu + ", obtenu " + obtenu);
        }
    }

    private List<Cohorte> cohortesDuSexe(Sexe sexe) {
        List<Cohorte> r = new ArrayList<Cohorte>();
        for (Cohorte c : cohortes) if (c.sexe == sexe) r.add(c);
        return r;
    }

    private static double quantite(List<? extends Stock> stocks) {
        double total = 0;
        for (Stock s : stocks) total += s.qte;
        return total;
    }

    private ParametreSouche parametres(String idsouche) {
        return souches.computeIfAbsent(idsouche, k -> new ParametreSouche(DEF_NB_JOURS_ECLOSION, DEF_SEMAINE_DEBUT_PONTE,
                DEF_DUREE_VIE_SEMAINES, DEF_TAUX_ECLOSION, DEF_RATIO_FEMELLE));
    }

    private static int ouDefaut(int valeur, int defaut)          { return valeur > 0 ? valeur : defaut; }
    private static double ouDefaut(double valeur, double defaut) { return valeur > 0 ? valeur : defaut; }
}
