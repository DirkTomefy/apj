package a_tahina.fronthelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.page.AtahinaChamp;
import a_tahina.page.AtahinaPageConsulte;
import a_tahina.page.AtahinaPageRecherche;
import bean.ClassMAPTable;

/**
 * Base commune des FrontHelper : lecture de paramètres et construction
 * des pages liste / fiche / onglet. Les FH concrets l'étendent et
 * n'ont plus qu'à déclarer leurs pages, classes et champs.
 */
public abstract class GeneraticFH {

    public static String getParam(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        if (v == null || v.trim().isEmpty() || "null".equalsIgnoreCase(v)) return null;
        return v.trim();
    }

    protected static AtahinaPageRecherche liste(ClassMAPTable objet, HttpServletRequest request, HttpSession session,
                                                String pageActuelle, String titre,
                                                AtahinaChamp... champs) throws Exception {
        return new AtahinaPageRecherche(objet, request, session, pageActuelle, titre, champs).construire();
    }

    protected static AtahinaPageConsulte consulte(ClassMAPTable objet, HttpServletRequest request, HttpSession session,
                                                  String titre, AtahinaChamp... champs) throws Exception {
        return new AtahinaPageConsulte(objet, request, session, titre).champs(champs);
    }

    /** Onglet d'une fiche : lignes de {@code objet} dont {@code colonneMere} vaut {@code idMere}. */
    protected static String onglet(ClassMAPTable objet, HttpServletRequest request, HttpSession session,
                                   String pageFiche, String idMere, String tab,
                                   String colonneMere, String messageVide,
                                   AtahinaChamp... colonnes) throws Exception {
        return AtahinaPageRecherche.onglet(objet, request, session, pageFiche, idMere, tab, colonnes)
            .filtreEgal(colonneMere, idMere)
            .construire()
            .getHtmlOnglet(messageVide);
    }
}
