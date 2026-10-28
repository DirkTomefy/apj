package a_tahina.core.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import affichage.Champ;
import affichage.Liste;
import affichage.PageRecherche;
import bean.ClassMAPTable;

public class AtahinaPageRecherche extends PageRecherche {

    private static final String BOUTON_SAISIR =
            "<a class='btn btn-primary pull-right btn-small' href='%s?but=%s'>"
          + "<i class='material-symbols-rounded'>add</i> Saisir</a>";

    private final AtahinaChampHelper[] champs;
    private final String[] libEntete;

    public AtahinaPageRecherche(ClassMAPTable objet, HttpServletRequest request, HttpSession session,
                                String pageActuelle, String titre, AtahinaChampHelper... champs) throws Exception {
        super(objet, request, criteres(champs), intervalles(champs), 3, entete(champs), entete(champs).length);
        this.champs = champs;
        this.libEntete = entete(champs);
        setTitre(titre);
        setUtilisateur(AtahinaUtil.getUser(session));
        setLien(AtahinaUtil.getLien(session));
        setApres(pageActuelle);
        setNpp(50);
        configurerCriteres();
    }

    public static AtahinaPageRecherche onglet(ClassMAPTable objet, HttpServletRequest request, HttpSession session,
                                              String pageFiche, String idMere, String tab,
                                              AtahinaChampHelper... colonnes) throws Exception {
        AtahinaPageRecherche pr = new AtahinaPageRecherche(objet, request, session,
                pageFiche + "&id=" + AtahinaUtil.url(idMere) + "&tab=" + tab, "", colonnes);
        pr.setNpp(10);
        return pr;
    }

    // ------------------------------------------------------------------
    // Calcul des tableaux attendus par PageRecherche
    // ------------------------------------------------------------------
    private static String[] criteres(AtahinaChampHelper[] champs) {
        List<String> l = new ArrayList<String>();
        for (AtahinaChampHelper c : champs) if (c.recherche || c.intervalle) l.add(c.nom);
        return l.toArray(new String[0]);
    }

    private static String[] intervalles(AtahinaChampHelper[] champs) {
        List<String> l = new ArrayList<String>();
        for (AtahinaChampHelper c : champs) if (c.intervalle) l.add(c.nom);
        return l.toArray(new String[0]);
    }

    private static String[] entete(AtahinaChampHelper[] champs) {
        List<String> l = new ArrayList<String>();
        for (AtahinaChampHelper c : champs) if (c.colonne) l.add(c.nom);
        return l.toArray(new String[0]);
    }

    private void configurerCriteres() throws Exception {
        List<Liste> listes = new ArrayList<Liste>();
        for (AtahinaChampHelper c : champs) {
            if ((c.recherche || c.intervalle) && c.aUneListe()) listes.add(c.creerListe());
        }
        if (!listes.isEmpty()) getFormu().changerEnChamp(listes.toArray(new Liste[0]));

        for (AtahinaChampHelper c : champs) {
            if (c.intervalle) {
                critere(c.nom + "1", c.libelle + " min", c.defaut);
                critere(c.nom + "2", c.libelle + " max", c.defaut);
            } else if (c.recherche) {
                critere(c.nom, c.libelle, c.defaut);
            }
        }
    }

    private void critere(String nom, String libelle, String defaut) {
        Champ ch = getFormu().getChamp(nom);
        if (ch == null) return;
        ch.setLibelle(libelle);
        if (defaut != null) ch.setDefaut(defaut);
    }

    // ------------------------------------------------------------------
    // Execution
    // ------------------------------------------------------------------
    public AtahinaPageRecherche construire() throws Exception {
        creerObjetPage(libEntete, null);

        List<String> libelles = new ArrayList<String>();
        for (AtahinaChampHelper c : champs) if (c.colonne) libelles.add(c.libelle);
        getTableau().setLibelleAffiche(libelles.toArray(new String[0]));

        for (AtahinaChampHelper c : champs) {
            if (c.lienPage != null) {
                lienFiche(c.nom, c.lienPage, c.lienParametre);
                break;
            }
        }
        return this;
    }

    private void lienFiche(String colonne, String pageFiche, String parametre) {
        String lienFiche = getLien() + "?but=" + pageFiche;
        getTableau().setLien(new String[] { lienFiche });
        getTableau().setColonneLien(new String[] { colonne });
        if (!colonne.equals(parametre)) {
            getTableau().setAttLien(new String[] { parametre });
        }
        menuClicDroit("fiche", pageFiche);
    }

    // ------------------------------------------------------------------
    // Options
    // ------------------------------------------------------------------
    public AtahinaPageRecherche filtreEgal(String colonne, String valeur) {
        setAWhere(AtahinaUtil.egal(colonne, valeur));
        return this;
    }

    public AtahinaPageRecherche filtre(String clauseSure) {
        setAWhere(clauseSure);
        return this;
    }

    public AtahinaPageRecherche parPage(int npp) {
        setNpp(npp);
        return this;
    }

    public AtahinaPageRecherche boutonSaisir(String pageSaisie) {
        getFormu().setAnotherButton(String.format(BOUTON_SAISIR, getLien(), pageSaisie));
        return this;
    }

    public AtahinaPageRecherche menuClicDroit(String cle, String pageCible) {
        Map<String, String> menu = new HashMap<String, String>();
        Map<String, String> existant = getTableau().getLienClicDroite();
        if (existant != null) menu.putAll(existant);
        menu.put(cle, getLien() + "?but=" + pageCible);
        getTableau().setLienClicDroite(menu);
        return this;
    }

    // ------------------------------------------------------------------
    // Rendu
    // ------------------------------------------------------------------
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

    public String getHtmlOnglet(String messageVide) throws Exception {
        String html = getTableau().getHtml();
        if (html == null)
            return "<center><h4>" + messageVide + "</h4></center>";
        return html;
    }
}
