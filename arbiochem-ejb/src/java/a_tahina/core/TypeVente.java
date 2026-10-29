package a_tahina.core;

import a_tahina.core.erreur.SaisieException;

public enum TypeVente {

    OEUF,
    POULET;

    public String code() { return name(); }

    public static TypeVente depuis(String code) throws SaisieException {
        if (code == null || code.trim().isEmpty()) return null;
        for (TypeVente t : values()) if (t.name().equalsIgnoreCase(code.trim())) return t;
        throw new SaisieException("typevente", "Le type de vente doit etre OEUF ou POULET : " + code);
    }

    public static TypeVente obligatoire(String code) throws SaisieException {
        TypeVente t = depuis(code);
        if (t == null) throw new SaisieException("typevente", "Le type de vente (oeuf ou poulet) est obligatoire sur chaque ligne.");
        return t;
    }
}
