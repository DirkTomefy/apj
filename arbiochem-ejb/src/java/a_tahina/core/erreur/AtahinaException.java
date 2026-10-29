package a_tahina.core.erreur;

public class AtahinaException extends Exception {

    public AtahinaException(String message) {
        super(message);
    }

    public AtahinaException(String message, Throwable cause) {
        super(message, cause);
    }
}
