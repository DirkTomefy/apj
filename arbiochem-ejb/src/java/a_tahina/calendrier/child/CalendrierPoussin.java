package a_tahina.calendrier.child;

import javax.servlet.http.HttpServletRequest;

import a_tahina.calendrier.Calendrier;
import a_tahina.prevision.orm.CalendrierPoussinCumulJour;

public class CalendrierPoussin extends Calendrier<CalendrierPoussinCumulJour> {

    public CalendrierPoussin(HttpServletRequest request) throws Exception {
        super(request, new CalendrierPoussinCumulJour(), "daty");

        titre("Prevision globale des poussins (cumul)");

        valeur(CalendrierPoussinCumulJour::getPoussinscumules);
        total("NAISSANCES", CalendrierPoussinCumulJour::getPoussins);
        indicateur("CA", CalendrierPoussinCumulJour::getCa);
        indicateurSigne("BENEFICE", CalendrierPoussinCumulJour::getBenefice);

        style("cal-perte",     "Perte déclarée",              j -> j.getPertes() > 0);
        style("cal-naissance", "Naissances",                   j -> j.getPoussins() > 0);
        style("cal-vide",      "Aucune naissance",             j -> true);
    }
}
