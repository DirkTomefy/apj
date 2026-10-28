package a_tahina.core.page;

import affichage.Liste;
import bean.ClassMAPTable;
import bean.TypeObjet;

public final class AtahinaChampHelper   {

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

    private AtahinaChampHelper(String nom, String libelle) {
        this.nom = nom;
        this.libelle = libelle;
    }

    public static AtahinaChampHelper champ(String nom, String libelle) {
        return new AtahinaChampHelper(nom, libelle);
    }

    public AtahinaChampHelper defaut(String valeur)  { this.defaut = valeur; return this; }
    public AtahinaChampHelper defautAujourdhui()     { return defaut(utilitaire.Utilitaire.dateDuJour()); }
    public AtahinaChampHelper lectureSeule()         { this.lectureSeule = true; return this; }
    public AtahinaChampHelper cache()                { this.cache = true; return this; }

    public AtahinaChampHelper liste(ClassMAPTable objet, String colAffiche, String colValeur) {
        return liste(objet, colAffiche, colValeur, null);
    }

    public AtahinaChampHelper liste(ClassMAPTable objet, String colAffiche, String colValeur, String aWhere) {
        this.listeObjet = objet;
        this.listeAffiche = colAffiche;
        this.listeValeur = colValeur;
        this.listeWhere = aWhere;
        return this;
    }

    public AtahinaChampHelper typeObjet(String table) throws Exception {
        return liste(new TypeObjet(table), "val", "id");
    }

    public AtahinaChampHelper listeEtat() throws Exception {
        return liste(new TypeObjet("ETAT"), "desce", "id");
    }

    public AtahinaChampHelper recherche()            { this.recherche = true; return this; }
    public AtahinaChampHelper intervalle()           { this.intervalle = true; return this; }
    public AtahinaChampHelper filtreSeul()           { this.recherche = true; this.colonne = false; return this; }
    public AtahinaChampHelper lien(String pageFiche) { return lien(pageFiche, nom); }
    public AtahinaChampHelper lien(String pageFiche, String parametre) {
        this.lienPage = pageFiche;
        this.lienParametre = parametre;
        return this;
    }

    boolean aUneListe() { return listeObjet != null; }

    Liste creerListe() throws Exception {
        return (listeWhere == null || listeWhere.isEmpty())
                ? new Liste(nom, listeObjet, listeAffiche, listeValeur)
                : new Liste(nom, listeObjet, listeAffiche, listeValeur, listeWhere);
    }
}
