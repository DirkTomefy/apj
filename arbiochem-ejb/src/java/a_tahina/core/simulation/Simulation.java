package a_tahina.core.simulation;

import java.time.LocalDate;

public abstract class Simulation<J, R extends ResultatSimulation<J>> {

    private boolean verification;

    public void verifierInvariants(boolean actif) {
        this.verification = actif;
    }

    public R simuler() {
        return simuler(null);
    }

    public R simuler(LocalDate jusqua) {
        R resultat = preparer();
        resultat.limiter(jusqua);
        LocalDate debut = debut();
        if (debut == null) return resultat;
        LocalDate fin = jusqua != null && jusqua.isBefore(fin()) ? jusqua : fin();
        J veille = null;
        for (LocalDate d = debut; !d.isAfter(fin); d = d.plusDays(1)) {
            J jour = simulerJour(d);
            if (verification) verifierJour(veille, jour);
            resultat.ajouterJour(d, jour);
            veille = jour;
        }
        return resultat;
    }

    protected abstract R preparer();

    protected abstract LocalDate debut();

    protected abstract LocalDate fin();

    protected abstract J simulerJour(LocalDate jour);

    protected void verifierJour(J veille, J jour) {
    }
}
