package a_tahina.fronthelper.br;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.br.BonReceptionPoussin;
import a_tahina.bc.BonCommandePoussinFille;
import affichage.Champ;
import affichage.Liste;
import affichage.PageInsert;
import bean.TypeObjet;
import magasin.Magasin;

public class BRInsertFH {

    public static final String PAGE_ACTUEL   = "a_tahina/v1/br/bonreception-saisie.jsp";
    public static final String PAGE_LISTE    = "a_tahina/v1/br/bonreception-liste.jsp";
    public static final String PAGE_BR_FICHE = "a_tahina/v1/br/bonreception-fiche.jsp";
    public static final String PAGE_BC_FICHE = "a_tahina/v1/bc/boncommande-fiche.jsp";

    // -------- Lecture parametres --------
    public static String getParam(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        if (v == null || v.trim().isEmpty() || "null".equalsIgnoreCase(v)) return null;
        return v.trim();
    }
    public static String getIdbc(HttpServletRequest req)  { return getParam(req, "idbc"); }
    public static String getIdbcf(HttpServletRequest req) { return getParam(req, "idbcf"); }

    public static String getButeRetour(HttpServletRequest req) {
        String idbc = getIdbc(req);
        if (idbc != null) return PAGE_BC_FICHE + "&id=" + idbc;
        return PAGE_LISTE;
    }

    // -------- Helpers null-safe --------
    private static void setLabel(PageInsert pi, String nom, String libelle) {
        Champ c = pi.getFormu().getChamp(nom);
        if (c != null) c.setLibelle(libelle);
    }
    private static void setDefaut(PageInsert pi, String nom, String valeur) {
        Champ c = pi.getFormu().getChamp(nom);
        if (c != null) c.setDefaut(valeur);
    }
    private static void hide(PageInsert pi, String nom) {
        Champ c = pi.getFormu().getChamp(nom);
        if (c != null) c.setVisible(false);
    }
    private static void setReadonly(PageInsert pi, String nom) {
        Champ c = pi.getFormu().getChamp(nom);
        if (c != null) c.setAutre("readonly");
    }

    // -------- Config --------
    private static void prepareLibelle(PageInsert pi) {
        setLabel(pi, "daty",               "Date de r&eacute;ception");
        setLabel(pi, "reference",          "R&eacute;f&eacute;rence");
        setLabel(pi, "idboncommandefille", "Ligne BC");
        setLabel(pi, "idlieureception",    "Lieu de r&eacute;ception");
        setLabel(pi, "idmagasin",          "Magasin");
        setLabel(pi, "qtAvantCumule",      "Qt&eacute; avant cumul");
        setLabel(pi, "qtApresCumule",      "Qt&eacute; apr&egrave;s cumul");
        setLabel(pi, "maty",               "Morts");
        setLabel(pi, "very",               "Perdus");
        setLabel(pi, "remarque",           "Remarque");
        setLabel(pi, "dateEclosion",       "Date d'&eacute;closion");   // <-- NOUVEAU

        // Champs deduits automatiquement -> caches
        hide(pi, "etat");
        hide(pi, "idboncommande");
        hide(pi, "idbrprecedent");
    }

    private static void prepareDefaultValues(PageInsert pi) {
        setDefaut(pi, "daty",          utilitaire.Utilitaire.dateDuJour());
        setDefaut(pi, "maty",          "0");
        setDefaut(pi, "very",          "0");
        setDefaut(pi, "qtAvantCumule", "0");
        setDefaut(pi, "qtApresCumule", "0");
    }

    private static void changeChampIntoListe(PageInsert pi, String idbcf) throws Exception {
        Liste[] listes = new Liste[3];
        listes[0] = new Liste("idlieureception", new TypeObjet("LIEURECEPTION"), "val", "id");
        listes[1] = new Liste("idmagasin",       new Magasin(), "val", "id");
        listes[2] = new Liste("idboncommandefille", new BonCommandePoussinFille(), "idproduit", "id");

        pi.getFormu().changerEnChamp(listes);

        if (idbcf != null) {
            setDefaut(pi, "idboncommandefille", idbcf);
            setReadonly(pi, "idboncommandefille");
        }
    }

    // -------- Point d'entree --------
    public static PageInsert getPageInsert(HttpServletRequest request,
                                           HttpSession session) throws Exception {
        user.UserEJB u = (user.UserEJB) session.getValue("u");
        if (u == null) throw new Exception("Session expiree. Reconnectez-vous.");

        BonReceptionPoussin br = new BonReceptionPoussin();
        PageInsert pi = new PageInsert(br, request, u);
        pi.setLien((String) session.getValue("lien"));

        String idbcf = getIdbcf(request);

        prepareLibelle(pi);
        prepareDefaultValues(pi);
        changeChampIntoListe(pi, idbcf);

        pi.preparerDataFormu();
        pi.getFormu().makeHtmlInsertTabIndex();
        return pi;
    }
}