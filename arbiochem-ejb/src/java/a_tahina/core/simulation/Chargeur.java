package a_tahina.core.simulation;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import bean.CGenUtil;
import bean.ClassMAPTable;

public abstract class Chargeur<S extends Simulation<?>> {

    public S charger(Connection c) throws Exception {
        S simulation = nouvelleSimulation();
        remplir(simulation, c);
        return simulation;
    }

    protected abstract S nouvelleSimulation();

    protected abstract void remplir(S simulation, Connection c) throws Exception;

    protected static <T extends ClassMAPTable> List<T> lire(T critere, Connection c) throws Exception {
        return lire(critere, c, "");
    }

    @SuppressWarnings("unchecked")
    protected static <T extends ClassMAPTable> List<T> lire(T critere, Connection c, String apresWhere) throws Exception {
        List<T> lignes = new ArrayList<T>();
        Object[] r = CGenUtil.rechercher(critere, null, null, c, apresWhere);
        if (r != null) for (Object o : r) lignes.add((T) o);
        return lignes;
    }

    protected static LocalDate date(java.util.Date d) {
        if (d == null) return null;
        return d instanceof java.sql.Date ? ((java.sql.Date) d).toLocalDate()
                                          : new java.sql.Date(d.getTime()).toLocalDate();
    }
}
