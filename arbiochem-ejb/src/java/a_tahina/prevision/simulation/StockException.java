package a_tahina.prevision.simulation;

import java.util.Collections;
import java.util.List;

import a_tahina.core.erreur.AtahinaException;

public class StockException extends AtahinaException {

    private final List<Vente> manques;

    public StockException(String message, List<Vente> manques) {
        super(message);
        this.manques = Collections.unmodifiableList(manques);
    }

    public List<Vente> getManques() { return manques; }
}
