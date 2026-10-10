package a_tahina.fronthelper.br;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.br.BonReceptionPoussinLib;
import affichage.Champ;
import affichage.Liste;
import affichage.PageRecherche;
import bean.TypeObjet;

public class BRListeFH {

    public static final String PAGE_ACTUEL = "a_tahina/v1/br/bonreception-liste.jsp";
    public static final String PAGE_FICHE  = "a_tahina/v1/br/bonreception-fiche.jsp";
    public static final String PAGE_SAISIE = "a_tahina/v1/br/bonreception-saisie.jsp";

    // =========================================================
    //  Helpers null-safe
    // =========================================================
    private static void setLabel(PageRecherche pr, String champ, String libelle) {
        Champ c = pr.getFormu().getChamp(champ);
        if (c != null) {
            c.setLibelle(libelle);
        } else {
            System.out.println(">>> [BRListeFH] Champ absent (libelle) : " + champ);
        }
    }

    private static void setDefaut(PageRecherche pr, String champ, String valeur) {
        Champ c = pr.getFormu().getChamp(champ);
        if (c != null) {
            c.setDefaut(valeur);
        } else {
            System.out.println(">>> [BRListeFH] Champ absent (defaut) : " + champ);
        }
    }

    // =========================================================
    //  Config globale
    // =========================================================
    public static void prepareGlobaleInfForPageRecherche(PageRecherche pr, HttpSession session) {
        pr.setTitre("Liste des bons de r&eacute;ception poussin");
        pr.setUtilisateur((user.UserEJB) session.getValue("u"));
        pr.setLien((String) session.getValue("lien"));
        pr.setApres(PAGE_ACTUEL);
        pr.setNpp(50);
    }

    // =========================================================
    //  Libellés des champs de recherche
    // =========================================================
    public static void prepareLibelleChamp(PageRecherche pr) {
        // Champs "simples"
        setLabel(pr, "id",                    "Id");
        setLabel(pr, "reference",             "R&eacute;f&eacute;rence");
        setLabel(pr, "etat",                  "&Eacute;tat");
        setLabel(pr, "etatlib",               "&Eacute;tat");

        // Champs "intervalle" (daty → daty1/daty2)
        setLabel(pr, "daty1",                 "Date min");
        setLabel(pr, "daty2",                 "Date max");

        // Champs joints de la vue
        setLabel(pr, "idboncommandelib",      "Bon de commande");
        setLabel(pr, "idlieureceptionlib",    "Lieu");
        setLabel(pr, "idbrprecedentlib",      "BR pr&eacute;c&eacute;dent");
        setLabel(pr, "idmagasinlib",          "Magasin");
        setLabel(pr, "produitlib",            "Produit");
        setLabel(pr, "souchelibr",            "Souche");

        // Valeurs par défaut sur la date
        setDefaut(pr, "daty1", utilitaire.Utilitaire.dateDuJour());
        setDefaut(pr, "daty2", utilitaire.Utilitaire.dateDuJour());
    }

    // =========================================================
    //  Transformation d'un champ en liste déroulante
    // =========================================================
    public static void changeChampIntoListe(PageRecherche pr) throws Exception {
        Liste[] listeMere = new Liste[1];
        listeMere[0] = new Liste("etat", new TypeObjet("ETAT"), "desce", "id");
        pr.getFormu().changerEnChamp(listeMere);
        pr.getFormu().getChamp("etat").setLibelle("&Eacute;tat");
    }

    // =========================================================
    //  Liens (clic sur id, clic droit contextuel)
    // =========================================================
    public static void handleFicheLink(PageRecherche pr) {
        String[] lienTableau  = { pr.getLien() + "?but=" + PAGE_FICHE };
        String[] colonneLien  = { "id" };
        pr.getTableau().setLien(lienTableau);
        pr.getTableau().setColonneLien(colonneLien);
    }

    public static void handleContextualClickableLink(PageRecherche pr) {
        Map<String, String> lienTab = new HashMap<>();
        lienTab.put("modifier", pr.getLien() + "?but=" + PAGE_FICHE);
        lienTab.put("fiche",    pr.getLien() + "?but=" + PAGE_FICHE);
        pr.getTableau().setLienClicDroite(lienTab);
    }

    // =========================================================
    //  Point d'entrée
    // =========================================================
    public static PageRecherche getPageRechercheForList(HttpServletRequest request,
                                                        HttpSession session) throws Exception {

        BonReceptionPoussinLib f = new BonReceptionPoussinLib();
        f.setNomTable("BONRECEPTION_POUSSIN_LIB");

        String[] listeCrt  = { "id", "daty", "reference", "idboncommandelib", "etat" };
        String[] listeInt  = { "daty" };
        String[] libEntete = { "id", "daty", "reference", "idboncommandelib",
                               "idlieureceptionlib", "etatlib" };

        PageRecherche pr = new PageRecherche(f, request, listeCrt, listeInt, 3,
                libEntete, libEntete.length);

        prepareGlobaleInfForPageRecherche(pr, session);

        // ---- DIAGNOSTIC (décommenter si besoin) ----
        // for (Champ c : pr.getFormu().getListeChamp()) {
        //     System.out.println(">>> CHAMP DISPO LISTE = '" + c.getNom() + "'");
        // }

        prepareLibelleChamp(pr);
        changeChampIntoListe(pr);

        pr.creerObjetPage(libEntete, null);

        handleFicheLink(pr);
        pr.getTableau().setLibelleAffiche(new String[] {
                "Id", "Date", "R&eacute;f&eacute;rence", "Bon de commande",
                "Lieu", "&Eacute;tat"
        });

        handleContextualClickableLink(pr);

        pr.getFormu().setAnotherButton(
                "<a class='btn btn-primary pull-right btn-small' " +
                "href='" + pr.getLien() + "?but=" + PAGE_SAISIE + "'>" +
                "<i class='material-symbols-rounded'>add</i> Saisir</a>");

        return pr;
    }
}