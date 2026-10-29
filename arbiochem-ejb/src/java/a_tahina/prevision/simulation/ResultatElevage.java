package a_tahina.prevision.simulation;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import a_tahina.core.simulation.ResultatSimulation;

public class ResultatElevage extends ResultatSimulation<JourPrevision> {

    private final Map<Vente, ServiceVente> services = new IdentityHashMap<Vente, ServiceVente>();
    private final List<Vente> ventes;

    ResultatElevage(List<Vente> ventes) {
        this.ventes = new ArrayList<Vente>(ventes);
        for (Vente v : ventes) services.put(v, new ServiceVente(v));
    }

    public ServiceVente service(Vente v) {
        return services.get(v);
    }

    public List<ServiceVente> nonServies() {
        List<ServiceVente> r = new ArrayList<ServiceVente>();
        for (Vente v : ventes) {
            ServiceVente s = services.get(v);
            if (couvre(v.date) && s.getManque() > Elevage.EPS) r.add(s);
        }
        return r;
    }
}
