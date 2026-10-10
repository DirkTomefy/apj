package a_tahina.fronthelper.br;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.br.BonReceptionPoussin;
import a_tahina.bc.BonCommandePoussin;
import a_tahina.bc.BonCommandePoussinFille;
import affichage.Champ;
import affichage.Liste;
import affichage.PageInsert;
import magasin.Magasin;

public class BRInsertFH {

    public static final String PAGE_ACTUEL   = "a_tahina/v1/br/bonreception-saisie.jsp";
    public static final String PAGE_LISTE    = "a_tahina/v1/br/bonreception-liste.jsp";
    public static final String PAGE_BC_FICHE = "a_tahina/v1/bc/boncommande-fiche.jsp";
    public static final String PAGE_BR_FICHE = "a_tahina/v1/br/bonreception-fiche.jsp";

    // =========================================================
    //  Helpers null-safe
    // =========================================================
    private static void setLabel(PageInsert pi, String champ, String libelle) {
        Champ c = pi.getFormu().getChamp(champ);
        if (c != null) {
            c.setLibelle(libelle);
        } else {
            System.out.println(">>> [BRInsertFH] Champ absent (libelle) : " + champ);
        }
    }

    private static void hide(PageInsert pi, String champ) {
        Champ c = pi.getFormu().getChamp(champ);
        if (c != null) {
            c.setVisible(false);
        } else {
            System.out.println(">>> [BRInsertFH] Champ absent (hide) : " + champ);
        }
    }

    private static void setDefaut(PageInsert pi, String champ, String valeur) {
        Champ c = pi.getFormu().getChamp(champ);
        if (c != null) {
            c.setDefaut(valeur);
        } else {
            System.out.println(">>> [BRInsertFH] Champ absent (defaut) : " + champ);
        }
    }

    private static void setReadonly(PageInsert pi, String champ) {
        Champ c = pi.getFormu().getChamp(champ);
        if (c != null) {
            c.setAutre("readonly");
        }
    }

    // =========================================================
    //  Récupération idbc (peut être null / vide / "null")
    // =========================================================
    public static String getIdbc(HttpServletRequest request) {
        String idbc = request.getParameter("idbc");
        if (idbc == null || idbc.trim().isEmpty() || "null".equalsIgnoreCase(idbc)) {
            return null;
        }
        return idbc.trim();
    }

    public static String getButeRetour(HttpServletRequest request) {
        String idbc = getIdbc(request);
        if (idbc != null) {
            return PAGE_BC_FICHE + "&id=" + idbc;
        }
        return PAGE_BR_FICHE;
    }

    // =========================================================
    //  Libellés
    // =========================================================
    private static void prepareLibelle(PageInsert pi) {
        setLabel(pi, "daty",               "Date");
        setLabel(pi, "reference",          "R&eacute;f&eacute;rence");
        setLabel(pi, "idboncommande",      "Bon de commande");
        setLabel(pi, "idboncommandefille", "Ligne BC");
        setLabel(pi, "idbrprecedent",      "BR pr&eacute;c&eacute;dent");
        setLabel(pi, "idlieureception",    "Lieu de r&eacute;ception");
        setLabel(pi, "idmagasin",          "Magasin");
        setLabel(pi, "qt_avant_cumule",    "Qt&eacute; avant cumul");
        setLabel(pi, "qt_apres_cumule",    "Qt&eacute; apr&egrave;s cumul");
        setLabel(pi, "qtAvantCumule",      "Qt&eacute; avant cumul");
        setLabel(pi, "qtApresCumule",      "Qt&eacute; apr&egrave;s cumul");
        setLabel(pi, "maty",               "Maty");
        setLabel(pi, "very",               "Very");
        setLabel(pi, "remarque",           "Remarque");

        hide(pi, "etat");
    }

    // =========================================================
    //  Valeurs par défaut
    // =========================================================
    private static void prepareDefaultValues(PageInsert pi) {
        setDefaut(pi, "daty",            utilitaire.Utilitaire.dateDuJour());
        setDefaut(pi, "maty",            "0");
        setDefaut(pi, "very",            "0");
        setDefaut(pi, "qt_avant_cumule", "0");
        setDefaut(pi, "qt_apres_cumule", "0");
        setDefaut(pi, "qtAvantCumule",   "0");
        setDefaut(pi, "qtApresCumule",   "0");
    }

    // =========================================================
    //  Listes déroulantes
    // =========================================================
    private static void changeChampIntoListe(PageInsert pi, String idbc) throws Exception {
        Liste[] listes = new Liste[4];
        listes[0] = new Liste("idlieureception",
                new bean.TypeObjet("LIEURECEPTION"), "val", "id");
        listes[1] = new Liste("idmagasin",
                new Magasin(), "val", "id");
        listes[2] = new Liste("idbrprecedent",
                new BonReceptionPoussin(), "reference", "id");
        listes[3] = new Liste("idboncommandefille",
                new BonCommandePoussinFille(), "idproduit", "id");

        if (idbc == null) {
            // Saisie directe → BC devient une liste
            Liste[] listesAvecBC = new Liste[5];
            System.arraycopy(listes, 0, listesAvecBC, 0, listes.length);
            listesAvecBC[4] = new Liste("idboncommande",
                    new BonCommandePoussin(), "reference", "id");
            pi.getFormu().changerEnChamp(listesAvecBC);
        } else {
            pi.getFormu().changerEnChamp(listes);
        }
    }

    // =========================================================
    //  Point d'entrée
    // =========================================================
    public static PageInsert getPageInsert(HttpServletRequest request,
                                           HttpSession session) throws Exception {

        String idbc = getIdbc(request);

        BonReceptionPoussin br = new BonReceptionPoussin();

        PageInsert pi = new PageInsert(br, request,
                (user.UserEJB) session.getValue("u"));

        pi.setLien((String) session.getValue("lien"));

        // ---- DIAGNOSTIC (décommenter si besoin) ----
        // for (Champ c : pi.getFormu().getListeChamp()) {
        //     System.out.println(">>> CHAMP DISPO = '" + c.getNom() + "'");
        // }

        prepareLibelle(pi);
        prepareDefaultValues(pi);
        changeChampIntoListe(pi, idbc);

        // Cas "depuis fiche BC" : champ BC verrouillé
        if (idbc != null) {
            setDefaut(pi, "idboncommande", idbc);
            setReadonly(pi, "idboncommande");
        }

        pi.preparerDataFormu();
        pi.getFormu().makeHtmlInsertTabIndex();

        return pi;
    }
}