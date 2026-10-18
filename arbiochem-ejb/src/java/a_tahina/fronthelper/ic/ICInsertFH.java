package a_tahina.fronthelper.ic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.ic.Compartiment;
import a_tahina.ic.InsertionCompartiment;
import a_tahina.ic.InsertionCompartimentFille;
import a_tahina.br.BonReceptionPoussin;
import affichage.Champ;
import affichage.Liste;
import affichage.PageInsertMultiple;
import bean.TypeObjet;

/**
 * Front-Helper de saisie d'une Insertion Compartiment.
 *
 * - Le BC parent et les cumuls sont déduits dans InsertionCompartiment.createObject().
 * - Chaque ligne de la fille génère un LOT à l'insertion.
 * - Les valeurs par défaut (0 pour les nombres, "" pour les listes) sont
 *   appliquées par PRÉFIXE pour être robustes aux noms Oracle en majuscules.
 */
public class ICInsertFH {

    public static final String PAGE_ACTUEL   = "a_tahina/v1/ic/insertioncompartiment-saisie.jsp";
    public static final String PAGE_LISTE    = "a_tahina/v1/ic/insertioncompartiment-liste.jsp";
    public static final String PAGE_IC_FICHE = "a_tahina/v1/ic/insertioncompartiment-fiche.jsp";
    public static final String PAGE_BR_FICHE = "a_tahina/v1/br/bonreception-fiche.jsp";

    public static final String CLASSE        = "a_tahina.ic.InsertionCompartiment";
    public static final String CLASSE_FILLE  = "a_tahina.ic.InsertionCompartimentFille";

    // =========================================================
    //  Lecture paramètres
    // =========================================================
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

    // =========================================================
    //  Helpers null-safe (mère)
    // =========================================================
    private static Champ champMere(PageInsertMultiple pi, String nom) {
        return pi.getFormu().getChamp(nom);
    }

    private static void setLabelMere(PageInsertMultiple pi, String nom, String libelle) {
        Champ c = champMere(pi, nom);
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

    // =========================================================
    //  Helpers par préfixe (fille) — insensibles à la casse
    // =========================================================
    private static void applyToFilleByPrefix(PageInsertMultiple pi, String prefixe, String valeur) {
        Champ[] champs = pi.getFormufle().getListeChamp();
        if (champs == null) return;
        String p = prefixe.toLowerCase();
        for (Champ c : champs) {
            if (c == null || c.getNom() == null) continue;
            if (c.getNom().toLowerCase().startsWith(p)) {
                c.setDefaut(valeur);
                c.setValeur(valeur);
            }
        }
    }

    private static void setFilleLabelByPrefix(PageInsertMultiple pi, String prefixe, String libelle) {
        Champ[] champs = pi.getFormufle().getListeChamp();
        if (champs == null) return;
        String p = prefixe.toLowerCase();
        for (Champ c : champs) {
            if (c == null || c.getNom() == null) continue;
            if (c.getNom().toLowerCase().startsWith(p)) {
                c.setLibelle(libelle);
            }
        }
    }

    private static void masquerFilleByPrefix(PageInsertMultiple pi, String prefixe) {
        Champ[] champs = pi.getFormufle().getListeChamp();
        if (champs == null) return;
        String p = prefixe.toLowerCase();
        for (Champ c : champs) {
            if (c == null || c.getNom() == null) continue;
            if (c.getNom().toLowerCase().startsWith(p)) {
                c.setVisible(false);
            }
        }
    }

    // =========================================================
    //  Libellés
    // =========================================================
    private static void prepareLibelleMere(PageInsertMultiple pi) {
        setLabelMere(pi, "daty",           "Date");
        setLabelMere(pi, "reference",      "R&eacute;f&eacute;rence");
        setLabelMere(pi, "idbonreception", "Bon de r&eacute;ception");
        setLabelMere(pi, "remarque",       "Remarque");

        Champ etat = champMere(pi, "etat");
        if (etat != null) etat.setVisible(false);
    }

    private static void prepareLibelleFille(PageInsertMultiple pi) {
        setFilleLabelByPrefix(pi, "idcompartiment",     "Compartiment");
        setFilleLabelByPrefix(pi, "qte",                "Qt&eacute;");
        setFilleLabelByPrefix(pi, "sexe",               "Sexe");
        setFilleLabelByPrefix(pi, "idsante",            "Sant&eacute;");

        // Masquer les colonnes techniques
        masquerFilleByPrefix(pi, "idmere_");
        masquerFilleByPrefix(pi, "id_");
        masquerFilleByPrefix(pi, "pu_");                    // <-- NOUVEAU
        masquerFilleByPrefix(pi, "valeurcompartiment_");   
    }

    // =========================================================
    //  Listes déroulantes
    // =========================================================
    private static void changeChampIntoListe(PageInsertMultiple pi, String idbr) throws Exception {

        // ---- Mère : BC en liste seulement si non imposé ----
        if (idbr == null) {
            Liste[] listeMere = new Liste[1];
            listeMere[0] = new Liste("idbonreception",
                    new BonReceptionPoussin(), "reference", "id");
            pi.getFormu().changerEnChamp(listeMere);
        }

        // ---- Fille : Compartiment (classe dédiée), Sexe & Santé (TypeObjet) ----
        Liste[] listeFille = new Liste[3];
        listeFille[0] = new Liste("idcompartiment",
                new Compartiment(), "nom", "id");
        listeFille[1] = new Liste("sexe",
                new TypeObjet("SEXE"), "val", "id");
        listeFille[2] = new Liste("idsante",
                new TypeObjet("SANTEPOUSSIN"), "val", "id");

        pi.getFormufle().changerEnChamp(listeFille);
    }

    // =========================================================
    //  Valeurs par défaut (anti-"null")
    // =========================================================
    private static void prepareDefaultValuesFille(PageInsertMultiple pi) {
        // Champs numériques → 0
        applyToFilleByPrefix(pi, "qte_",                "0");
        applyToFilleByPrefix(pi, "pu_",                 "0");
        applyToFilleByPrefix(pi, "valeurcompartiment_", "0");

        // Listes → sélection vide
        applyToFilleByPrefix(pi, "idcompartiment_", "");
        applyToFilleByPrefix(pi, "sexe_",           "");
        applyToFilleByPrefix(pi, "idsante_",        "");
    }

    // =========================================================
    //  Debug (décommenter si nécessaire)
    // =========================================================
    public static void debugPrintChampsFille(PageInsertMultiple pi) {
        System.out.println("=============== CHAMPS FILLE ===============");
        Champ[] champs = pi.getFormufle().getListeChamp();
        if (champs != null) {
            for (Champ c : champs) {
                if (c != null) {
                    System.out.println(">>> '" + c.getNom()
                            + "'  valeur='" + c.getValeur()
                            + "'  visible=" + c.getVisible());
                }
            }
        }
        System.out.println("=============================================");
    }

    // =========================================================
    //  Point d'entrée
    // =========================================================
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

        // 1. Libellés
        prepareLibelleMere(pi);
        prepareLibelleFille(pi);

        // 2. Valeurs par défaut (mère)
        setDefautMere(pi, "daty", utilitaire.Utilitaire.dateDuJour());

        // 3. Transformations en listes déroulantes
        changeChampIntoListe(pi, idbr);

        // 4. Contexte BR (figé si on vient d'une fiche BR)
        if (idbr != null) {
            setDefautMere(pi, "idbonreception", idbr);
            setReadonlyMere(pi, "idbonreception");
        }

        // 5. Ordre d'affichage des colonnes de la fille
        pi.getFormufle().setColOrdre(new String[]{
                "idcompartiment", "qte", "pu", "valeurCompartiment", "sexe", "idsante"
        });

        // 6. Charger les données (options des listes)
        pi.preparerDataFormu();

        // 7. Initialisation des valeurs par défaut de la fille (anti-"null")
        prepareDefaultValuesFille(pi);

        // Debug (décommenter pour inspecter les noms exacts des champs)
        // debugPrintChampsFille(pi);

        // 8. Génération du HTML
        pi.getFormu().makeHtmlInsertTabIndex();
        pi.getFormufle().makeHtmlInsertTableauIndex();

        return pi;
    }
}