package a_tahina.fronthelper.ic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.ic.InsertionCompartiment;
import a_tahina.ic.InsertionCompartimentFille;
import a_tahina.br.BonReceptionPoussin;
import affichage.Champ;
import affichage.Liste;
import affichage.PageInsertMultiple;
import bean.TypeObjet;

public class ICInsertFH {

    public static final String PAGE_ACTUEL   = "a_tahina/v1/ic/insertioncompartiment-saisie.jsp";
    public static final String PAGE_LISTE    = "a_tahina/v1/ic/insertioncompartiment-liste.jsp";
    public static final String PAGE_IC_FICHE = "a_tahina/v1/ic/insertioncompartiment-fiche.jsp";
    public static final String PAGE_BR_FICHE = "a_tahina/v1/br/bonreception-fiche.jsp";

    public static final String CLASSE        = "a_tahina.ic.InsertionCompartiment";
    public static final String CLASSE_FILLE  = "a_tahina.ic.InsertionCompartimentFille";

    public static String getParam(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        if (v == null || v.trim().isEmpty() || "null".equalsIgnoreCase(v)) return null;
        return v.trim();
    }
    public static String getIdbr(HttpServletRequest req) { return getParam(req, "idbr"); }

    public static String getButeRetour(HttpServletRequest req) {
        String idbr = getIdbr(req);
        if (idbr != null) return PAGE_BR_FICHE + "&id=" + idbr;
        return PAGE_LISTE;
    }

    private static Champ champMere(PageInsertMultiple pi, String nom)  { return pi.getFormu().getChamp(nom); }
    private static Champ champFille(PageInsertMultiple pi, String nom) { return pi.getFormufle().getChamp(nom); }

    private static void setLabelMere(PageInsertMultiple pi, String nom, String libelle) {
        Champ c = champMere(pi, nom);
        if (c != null) c.setLibelle(libelle);
    }
    private static void setLabelFille(PageInsertMultiple pi, String nom, String libelle) {
        Champ c = champFille(pi, nom);
        if (c != null) c.setLibelle(libelle);
    }
    private static void setDefautMere(PageInsertMultiple pi, String nom, String valeur) {
        Champ c = champMere(pi, nom);
        if (c != null) c.setDefaut(valeur);
    }
    private static void setReadonlyMere(PageInsertMultiple pi, String nom) {
        Champ c = champMere(pi, nom);
        if (c != null) c.setAutre("readonly");
    }

    private static void prepareLibelleMere(PageInsertMultiple pi) {
        setLabelMere(pi, "daty",           "Date");
        setLabelMere(pi, "reference",      "R&eacute;f&eacute;rence");
        setLabelMere(pi, "idbonreception", "Bon de r&eacute;ception");
        setLabelMere(pi, "remarque",       "Remarque");
        Champ etat = champMere(pi, "etat");
        if (etat != null) etat.setVisible(false);
    }

    private static void prepareLibelleFille(PageInsertMultiple pi) {
        setLabelFille(pi, "idcompartiment_0", "Compartiment");
        setLabelFille(pi, "qte_0",            "Qt&eacute;");
        setLabelFille(pi, "pu_0",             "P.U.");
        setLabelFille(pi, "sexe_0",           "Sexe");
        setLabelFille(pi, "idsante_0",        "Sant&eacute;");

        Champ id = champFille(pi, "id");
        if (id != null) id.setVisible(false);
        Champ idmere = champFille(pi, "idmere");
        if (idmere != null) idmere.setVisible(false);
    }

    private static void changeChampIntoListe(PageInsertMultiple pi, String idbr) throws Exception {
        if (idbr == null) {
            Liste[] listeMere = new Liste[1];
            listeMere[0] = new Liste("idbonreception",
                    new BonReceptionPoussin(), "reference", "id");
            pi.getFormu().changerEnChamp(listeMere);
        }

        Liste[] listeFille = new Liste[3];
        listeFille[0] = new Liste("idcompartiment", new TypeObjet("COMPARTIMENT"), "nom", "id");
        listeFille[1] = new Liste("sexe",           new TypeObjet("SEXE"), "val", "id");
        listeFille[2] = new Liste("idsante",        new TypeObjet("SANTEPOUSSIN"), "val", "id");
        pi.getFormufle().changerEnChamp(listeFille);
    }

    private static void prepareDefaultValuesFille(PageInsertMultiple pi, int nbLignes) {
        for (int i = 0; i < nbLignes; i++) {
            Champ qte = champFille(pi, "qte_" + i);
            if (qte != null) qte.setDefaut("0");
            Champ pu = champFille(pi, "pu_" + i);
            if (pu != null) pu.setDefaut("0");
        }
    }

    public static PageInsertMultiple getPageInsertMultiple(HttpServletRequest request,
                                                          HttpSession session,
                                                          int nbLignes) throws Exception {
        user.UserEJB u = (user.UserEJB) session.getValue("u");
        if (u == null) throw new Exception("Session expir\u00e9e. Reconnectez-vous.");

        String idbr = getIdbr(request);

        InsertionCompartiment mere       = new InsertionCompartiment();
        InsertionCompartimentFille fille = new InsertionCompartimentFille();

        PageInsertMultiple pi = new PageInsertMultiple(mere, fille, request, nbLignes, u);
        pi.setLien((String) session.getValue("lien"));

        prepareLibelleMere(pi);
        prepareLibelleFille(pi);

        setDefautMere(pi, "daty", utilitaire.Utilitaire.dateDuJour());

        changeChampIntoListe(pi, idbr);

        if (idbr != null) {
            setDefautMere(pi, "idbonreception", idbr);
            setReadonlyMere(pi, "idbonreception");
        }

        pi.getFormufle().setColOrdre(new String[]{
                "idcompartiment", "qte", "pu", "sexe", "idsante"
        });

        pi.preparerDataFormu();
        prepareDefaultValuesFille(pi, nbLignes);

        pi.getFormu().makeHtmlInsertTabIndex();
        pi.getFormufle().makeHtmlInsertTableauIndex();
        return pi;
    }
}