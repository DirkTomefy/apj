package a_tahina.calendrier.child;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import a_tahina.calendrier.Calendrier;
import a_tahina.prevision.simulation.ChargeurElevage;
import a_tahina.prevision.simulation.JourPrevision;

public class CalendrierPoussin extends Calendrier<JourPrevision> {

    public CalendrierPoussin(HttpServletRequest request) throws Exception {
        super(request);

        titre("Prevision de l'elevage (poulets vivants)");

        valeur(JourPrevision::getVivants);
        total("NAISSANCES", JourPrevision::getNaissances);
        // indicateur("OEUFS PONDUS", JourPrevision::getOeufsPondus);
        // indicateur("OEUFS VENDUS", JourPrevision::getOeufsVendus);
        // indicateur("POULETS VENDUS", JourPrevision::getPouletsVendus);
        // indicateur("PERTES", JourPrevision::getPertes);
        // indicateur("CA", JourPrevision::getCa);
        // indicateurSigne("BENEFICE", JourPrevision::getBenefice);

        style("cal-perte",     "Perte déclarée",   j -> j.getPertes() > 0);
        style("cal-vente",     "Vente",            j -> j.getOeufsVendus() > 0 || j.getPouletsVendus() > 0);
        style("cal-naissance", "Naissances",       j -> j.getNaissances() > 0);
        style("cal-vide",      "Aucune naissance", j -> true);
    }

    @Override
    protected Map<LocalDate, JourPrevision> charger(LocalDate d1, LocalDate d2) throws Exception {
        Map<LocalDate, JourPrevision> res = new HashMap<LocalDate, JourPrevision>();
        for (Map.Entry<LocalDate, JourPrevision> e : ChargeurElevage.charger(null).simuler().entrySet()) {
            if (!e.getKey().isBefore(d1) && !e.getKey().isAfter(d2)) res.put(e.getKey(), e.getValue());
        }
        return res;
    }
}
