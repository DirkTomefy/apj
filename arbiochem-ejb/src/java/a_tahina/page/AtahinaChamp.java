package a_tahina.page;

import affichage.Liste;
import bean.ClassMAPTable;
import bean.TypeObjet;

public final class AtahinaChamp {

    final String nom;
    final String libelle;

    String  defaut;
    boolean lectureSeule;
    boolean cache;

    private ClassMAPTable listeObjet;
    private String listeAffiche;
    private String listeValeur;
    private String listeWhere;

    boolean recherche;
    boolean intervalle;
    boolean colonne = true;
    String  lienPage;
    String  lienParametre;

    private AtahinaChamp(String nom, String libelle) {
        this.nom = nom;
        this.libelle = libelle;
    }

    public static AtahinaChamp champ(String nom, String libelle) {
        return new AtahinaChamp(nom, libelle);
    }

    // ------------------------------------------------------------------
    // Saisie / fiche
    // ------------------------------------------------------------------
    public AtahinaChamp defaut(String valeur)  { this.defaut = valeur; return this; }
    public AtahinaChamp defautAujourdhui()     { return defaut(utilitaire.Utilitaire.dateDuJour()); }
    public AtahinaChamp lectureSeule()         { this.lectureSeule = true; return this; }
    public AtahinaChamp cache()                { this.cache = true; return this; }

    public AtahinaChamp liste(ClassMAPTable objet, String colAffiche, String colValeur) {
        return liste(objet, colAffiche, colValeur, null);
    }

    public AtahinaChamp liste(ClassMAPTable objet, String colAffiche, String colValeur, String aWhere) {
        this.listeObjet = objet;
        this.listeAffiche = colAffiche;
        this.listeValeur = colValeur;
        this.listeWhere = aWhere;
        return this;
    }

    public AtahinaChamp typeObjet(String table) throws Exception {
        return liste(new TypeObjet(table), "val", "id");
    }

    public AtahinaChamp listeEtat() throws Exception {
        return liste(new TypeObjet("ETAT"), "desce", "id");
    }

    // ------------------------------------------------------------------
    // Ecran LISTE
    // ------------------------------------------------------------------
    public AtahinaChamp recherche()            { this.recherche = true; return this; }
    public AtahinaChamp intervalle()           { this.intervalle = true; return this; }
    public AtahinaChamp filtreSeul()           { this.recherche = true; this.colonne = false; return this; }
    public AtahinaChamp lien(String pageFiche) { return lien(pageFiche, nom); }
    public AtahinaChamp lien(String pageFiche, String parametre) {
        this.lienPage = pageFiche;
        this.lienParametre = parametre;
        return this;
    }

    // ------------------------------------------------------------------
    // Interne
    // ------------------------------------------------------------------
    boolean aUneListe() { return listeObjet != null; }

    Liste creerListe() throws Exception {
        return (listeWhere == null || listeWhere.isEmpty())
                ? new Liste(nom, listeObjet, listeAffiche, listeValeur)
                : new Liste(nom, listeObjet, listeAffiche, listeValeur, listeWhere);
    }
}
