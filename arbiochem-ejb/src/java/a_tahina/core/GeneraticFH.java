package a_tahina.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.calendrier.Calendrier;
import a_tahina.core.page.AtahinaChampHelper;
import a_tahina.core.page.AtahinaPageConsulte;
import a_tahina.core.page.AtahinaPageInsert;
import a_tahina.core.page.AtahinaPageInsertMultiple;
import a_tahina.core.page.AtahinaPageRecherche;
import a_tahina.core.page.AtahinaUtil;
import bean.ClassMAPTable;


public abstract class GeneraticFH {

    public abstract AtahinaPageInsert getPageInsert(HttpServletRequest request,
                                                    HttpSession session) throws Exception;

    public abstract AtahinaPageInsertMultiple getPageInsertMultiple(HttpServletRequest request,
                                                                    HttpSession session,
                                                                    int nombreLigne) throws Exception;

    public abstract AtahinaPageConsulte getPageConsulte(HttpServletRequest request,
                                                        HttpSession session) throws Exception;

    public abstract Calendrier<?> getCalendrier(HttpServletRequest request,
                                                     HttpSession session) throws Exception;

    public abstract AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                                 HttpSession session) throws Exception;

    protected UnsupportedOperationException nonSupporte(String operation) {
        return new UnsupportedOperationException(getClass().getSimpleName() + " ne supporte pas " + operation);
    }

    public static String getParam(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        if (v == null || v.trim().isEmpty() || "null".equalsIgnoreCase(v)) return null;
        return v.trim();
    }

    public static String fiche(String pageFiche, String id) {
        return pageFiche + "&id=" + AtahinaUtil.url(id);
    }

    protected static String retour(String idOrigine, String pageFiche, String pageListe) {
        return idOrigine != null ? fiche(pageFiche, idOrigine) : pageListe;
    }

    protected static AtahinaPageRecherche liste(ClassMAPTable objet, HttpServletRequest request, HttpSession session,
                                                String pageActuelle, String titre,
                                                AtahinaChampHelper... champs) throws Exception {
        return new AtahinaPageRecherche(objet, request, session, pageActuelle, titre, champs).construire();
    }

    protected static AtahinaPageConsulte consulte(ClassMAPTable objet, HttpServletRequest request, HttpSession session,
                                                  String titre, AtahinaChampHelper... champs) throws Exception {
        return new AtahinaPageConsulte(objet, request, session, titre).champs(champs);
    }

    protected static String onglet(ClassMAPTable objet, HttpServletRequest request, HttpSession session,
                                   String pageFiche, String idMere, String tab,
                                   String colonneMere, String messageVide,
                                   AtahinaChampHelper... colonnes) throws Exception {
        return AtahinaPageRecherche.onglet(objet, request, session, pageFiche, idMere, tab, colonnes)
            .filtreEgal(colonneMere, idMere)
            .construire()
            .getHtmlOnglet(messageVide);
    }
}
