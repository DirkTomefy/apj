package a_tahina.page;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import affichage.Liste;
import affichage.PageRecherche;
import bean.ClassMAPTable;

public class AtahinaPageRecherche extends PageRecherche {

    private static final String BOUTON_SAISIR =
            "<a class='btn btn-primary pull-right btn-small' href='%s?but=%s'>"
          + "<i class='material-symbols-rounded'>add</i> Saisir</a>";

    private final String[] libEntete;

    public AtahinaPageRecherche(ClassMAPTable objet, HttpServletRequest request, HttpSession session,
                                String pageActuelle, String titre,
                                String[] criteres, String[] intervalles, String[] libEntete) throws Exception {
        super(objet, request, criteres, intervalles, 3, libEntete, libEntete.length);
        this.libEntete = libEntete;
        setTitre(titre);
        setUtilisateur(AtahinaUtil.getUser(session));
        setLien(AtahinaUtil.getLien(session));
        setApres(pageActuelle);
        setNpp(50);
    }

  
    public AtahinaPageRecherche libelle(String champ, String libelle) {
        AtahinaUtil.libelle(getFormu().getChamp(champ), libelle);
        return this;
    }

    public AtahinaPageRecherche defaut(String champ, String valeur) {
        AtahinaUtil.defaut(getFormu().getChamp(champ), valeur);
        return this;
    }

    /** Intervalle de dates daty1 / daty2 (libelles + defaut = aujourd'hui). */
    public AtahinaPageRecherche intervalleDatyAujourdhui() {
        String aujourdhui = utilitaire.Utilitaire.dateDuJour();
        libelle("daty1", "Date min").defaut("daty1", aujourdhui);
        libelle("daty2", "Date max").defaut("daty2", aujourdhui);
        return this;
    }

    public AtahinaPageRecherche listes(Liste... listes) throws Exception {
        getFormu().changerEnChamp(listes);
        return this;
    }

    /** Remplace le champ « etat » par la liste des etats. */
    public AtahinaPageRecherche listeEtat(String libelle) throws Exception {
        listes(AtahinaUtil.listeEtat("etat"));
        return libelle("etat", libelle);
    }

    public AtahinaPageRecherche boutonSaisir(String pageSaisie) {
        getFormu().setAnotherButton(String.format(BOUTON_SAISIR, getLien(), pageSaisie));
        return this;
    }

    // ------------------------------------------------------------------
    // Filtres supplementaires (valeurs echappees)
    // ------------------------------------------------------------------
    public AtahinaPageRecherche filtreEgal(String colonne, String valeur) {
        setAWhere(AtahinaUtil.egal(colonne, valeur));
        return this;
    }

    public AtahinaPageRecherche filtreDans(String colonne, String... valeurs) {
        setAWhere(AtahinaUtil.dans(colonne, valeurs));
        return this;
    }

    /** Clause deja construite avec AtahinaUtil (egal, dans, dansSelect...). */
    public AtahinaPageRecherche filtre(String clauseSure) {
        setAWhere(clauseSure);
        return this;
    }

    // ------------------------------------------------------------------
    // Execution + tableau
    // ------------------------------------------------------------------
    /** Lance la recherche et pose les libelles des colonnes (dans l'ordre de libEntete). */
    public AtahinaPageRecherche construire(String... libellesColonnes) throws Exception {
        creerObjetPage(libEntete, null);
        if (libellesColonnes != null && libellesColonnes.length > 0)
            getTableau().setLibelleAffiche(libellesColonnes);
        return this;
    }

    /** Colonne `colonne` cliquable vers `pageFiche` (+ menu clic droit « fiche »), parametre `id`. */
    public AtahinaPageRecherche lienFiche(String colonne, String pageFiche) {
        return lienFiche(colonne, pageFiche, colonne);
    }

    /**
     * Idem, mais le lien transmet la valeur de `colonne` sous le nom `parametre`
     * (ex: colonne idavoir -> fiche?id=...).
     */
    public AtahinaPageRecherche lienFiche(String colonne, String pageFiche, String parametre) {
        String lienFiche = getLien() + "?but=" + pageFiche;
        getTableau().setLien(new String[] { lienFiche });
        getTableau().setColonneLien(new String[] { colonne });
        if (!colonne.equals(parametre)) {
            getTableau().setAttLien(new String[] { parametre });
        }
        Map<String, String> menu = new HashMap<String, String>();
        menu.put("fiche", lienFiche);
        getTableau().setLienClicDroite(menu);
        return this;
    }

    /** Ajoute une entree au menu clic droit (ex: « modifier »). */
    public AtahinaPageRecherche menuClicDroit(String cle, String pageCible) {
        Map<String, String> menu = new HashMap<String, String>();
        menu.put(cle, getLien() + "?but=" + pageCible);
        // setLienClicDroite remplace : on fusionne avec l'existant
        Map<String, String> existant = getTableau().getLienClicDroite();
        if (existant != null) menu.putAll(existant);
        getTableau().setLienClicDroite(menu);
        return this;
    }

    // ------------------------------------------------------------------
    // Rendu
    // ------------------------------------------------------------------
    /** Page LISTE complete : titre, filtre (name="filtre"), recap, tableau, pagination. */
    public String getHtmlListe() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"content-wrapper\">\n")
          .append("  <h1>").append(getTitre()).append("</h1>\n")
          .append("  <form action=\"").append(getLien()).append("?but=").append(getApres())
          .append("\" method=\"post\" name=\"filtre\">\n")
          .append(getFormu().getHtmlEnsemble())
          .append("\n  </form>\n")
          .append(getTableauRecap().getHtml()).append("\n")
          .append(getTableau().getHtml()).append("\n")
          .append(getBasPage()).append("\n")
          .append("</div>");
        return sb.toString();
    }

    /** Tableau seul pour un onglet, avec message si vide. */
    public String getHtmlOnglet(String messageVide) throws Exception {
        String html = getTableau().getHtml();
        if (html == null)
            return "<center><h4>" + messageVide + "</h4></center>";
        return html;
    }
}
