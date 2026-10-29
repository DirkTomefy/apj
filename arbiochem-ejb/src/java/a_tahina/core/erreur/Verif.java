package a_tahina.core.erreur;

public final class Verif {

    private Verif() {}

    public static void verifier(boolean condition, String message) throws SaisieException {
        if (!condition) throw new SaisieException(message);
    }

    public static void obligatoire(String valeur, String libelle) throws SaisieException {
        if (valeur == null || valeur.trim().isEmpty()) throw new SaisieException(libelle + " est obligatoire.");
    }

    public static void obligatoire(Object valeur, String libelle) throws SaisieException {
        if (valeur == null) throw new SaisieException(libelle + " est obligatoire.");
    }

    public static <T> T existe(T objet, String message) throws AtahinaException {
        if (objet == null) throw new AtahinaException(message);
        return objet;
    }
}
