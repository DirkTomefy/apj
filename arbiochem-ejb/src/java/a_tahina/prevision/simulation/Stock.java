package a_tahina.prevision.simulation;

abstract class Stock {

    final double pu;
    double qte;

    Stock(double pu, double qte) {
        this.pu = pu;
        this.qte = qte;
    }

    double prelever(double demande) {
        double q = Math.min(demande, qte);
        qte -= q;
        return q;
    }
}
