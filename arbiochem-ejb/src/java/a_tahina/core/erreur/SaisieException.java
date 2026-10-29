package a_tahina.core.erreur;

public class SaisieException extends AtahinaException {

    private final String champ;

    public SaisieException(String message) {
        this(null, message);
    }

    public SaisieException(String champ, String message) {
        super(message);
        this.champ = champ;
    }

    public String getChamp() { return champ; }
}
