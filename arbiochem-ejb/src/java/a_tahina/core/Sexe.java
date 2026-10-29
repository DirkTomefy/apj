package a_tahina.core;

import a_tahina.core.erreur.SaisieException;

public enum Sexe {

    FEMELLE("0", "femelle", "femelles"),
    MALE("1", "male", "males");

    private final String code;
    private final String libelle;
    private final String pluriel;

    Sexe(String code, String libelle, String pluriel) {
        this.code = code;
        this.libelle = libelle;
        this.pluriel = pluriel;
    }

    public String code()    { return code; }
    public String libelle() { return libelle; }
    public String pluriel() { return pluriel; }

    public static Sexe depuis(String code) throws SaisieException {
        if (code == null || code.trim().isEmpty()) return null;
        for (Sexe s : values()) if (s.code.equals(code.trim())) return s;
        throw new SaisieException("sexe", "Le sexe doit etre '0' (femelle) ou '1' (male) : " + code);
    }

    public static Sexe obligatoire(String code) throws SaisieException {
        Sexe s = depuis(code);
        if (s == null) throw new SaisieException("sexe", "Le sexe est obligatoire.");
        return s;
    }
}
