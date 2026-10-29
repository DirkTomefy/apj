package a_tahina.prevision.simulation;

import java.util.Collections;
import java.util.List;

import a_tahina.core.erreur.AtahinaException;

public class StockException extends AtahinaException {

    private final List<ServiceVente> manques;

    public StockException(String message, List<ServiceVente> manques) {
        super(message);
        this.manques = Collections.unmodifiableList(manques);
    }

    public List<ServiceVente> getManques() { return manques; }
}
