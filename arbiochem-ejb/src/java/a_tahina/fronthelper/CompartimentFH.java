package a_tahina.fronthelper;

import a_tahina.configuration.CompartimentLib;
import a_tahina.configuration.CompartimentValeur;

import affichage.PageConsulte;
import affichage.PageInsert;
import affichage.PageRecherche;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class CompartimentFH {

    /* =========================================================
     *  LISTE  — avec valeurs calculées
     * ========================================================= */
    public static PageRecherche getPageRechercheForList(
            HttpServletRequest request,
            HttpSession session) throws Exception {

        CompartimentValeur f = new CompartimentValeur();
        f.setNomTable("COMPARTIMENT_VALEUR_LIB");

        String[] listeCrt  = {"id", "nom"};
        String[] listeInt  = {};
        String[] libEntete = {"id", "nom", "capacite",
                              "qteActuelle", "pu", "valeurActuelle",
                              "valeurTheorique", "valeurNonRemplie"};

        PageRecherche pr = new PageRecherche(f, request, listeCrt, listeInt, 3,
                                              libEntete, libEntete.length);
        pr.setTitre("Liste des compartiments");
        pr.setUtilisateur((user.UserEJB) session.getValue("u"));
        pr.setLien((String) session.getValue("lien"));
        pr.setApres("a_tahina/configuration/compartiment-liste.jsp");
        pr.setNpp(50);

        pr.getFormu().getChamp("id").setLibelle("Id");
        pr.getFormu().getChamp("nom").setLibelle("Nom");

        pr.creerObjetPage(libEntete, null);

        String[] lienTableau = { pr.getLien()
            + "?but=a_tahina/configuration/compartiment-fiche.jsp" };
        String[] colonneLien = { "id" };
        pr.getTableau().setLien(lienTableau);
        pr.getTableau().setColonneLien(colonneLien);
        pr.getTableau().setLibelleAffiche(new String[]{
            "Id", "Nom", "Capacité", "Qté actuelle",
            "P.U.", "Valeur actuelle",
            "Valeur théorique", "Valeur non remplie"
        });

        Map<String,String> lienTab = new HashMap<>();
        lienTab.put("modifier",
            pr.getLien() + "?but=a_tahina/configuration/compartiment-modif.jsp");
        pr.getTableau().setLienClicDroite(lienTab);

        pr.getFormu().setAnotherButton(
            "<a class='btn btn-primary pull-right btn-small' " +
            "href='" + pr.getLien()
            + "?but=a_tahina/configuration/compartiment-saisie.jsp'>" +
            "<i class='material-symbols-rounded'>add</i> Saisir</a>");

        return pr;
    }

   
    public static PageInsert getPageInsert(
            HttpServletRequest request,
            HttpSession session) throws Exception {

        CompartimentLib f = new CompartimentLib();

        PageInsert pi = new PageInsert(f, request);

        pi.setLien((String) session.getValue("lien"));
        pi.setUtilisateur((user.UserEJB) session.getValue("u"));

        pi.getFormu().getChamp("nom").setLibelle("Nom");
        pi.getFormu().getChamp("capacite").setLibelle("Capacité");

        pi.getFormu().makeHtmlInsertTabIndex();

        return pi;
    }

    /* =========================================================
     *  FICHE  — avec valeurs calculées
     * ========================================================= */
    public static PageConsulte getPageConsulte(
            HttpServletRequest request,
            HttpSession session) throws Exception {

        CompartimentValeur f = new CompartimentValeur();
        f.setNomTable("COMPARTIMENT_VALEUR_LIB");

        PageConsulte pc = new PageConsulte(f, request,
                            (user.UserEJB) session.getValue("u"));
        pc.setTitre("Fiche compartiment");
        pc.getBase();

        pc.getChampByName("id").setLibelle("Id");
        pc.getChampByName("nom").setLibelle("Nom");
        pc.getChampByName("capacite").setLibelle("Capacité");
        pc.getChampByName("qteActuelle").setLibelle("Qté actuelle");
        pc.getChampByName("pu").setLibelle("P.U.");
        pc.getChampByName("valeurActuelle").setLibelle("Valeur actuelle");
        pc.getChampByName("valeurTheorique").setLibelle("Valeur théorique");
        pc.getChampByName("valeurNonRemplie").setLibelle("Valeur non remplie");

        return pc;
    }
}