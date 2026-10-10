package a_tahina.fronthelper.bc;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.bc.BonCommandePoussinLib;
import affichage.Liste;
import affichage.PageRecherche;
import bean.TypeObjet;

public class BCmdListeFH {

    public static void prepareGlobaleInfForPageRecherche(PageRecherche pr, HttpSession session) {
        pr.setTitre("Liste des bons de commande poussin");
        pr.setUtilisateur((user.UserEJB) session.getValue("u"));
        pr.setLien((String) session.getValue("lien"));
        pr.setApres("a_tahina/v1/bc/boncommande-liste.jsp");
        pr.setNpp(50);

    }

    public static void prepareLibelleChamp(PageRecherche pr) {
        pr.getFormu().getChamp("id").setLibelle("Id");
        pr.getFormu().getChamp("daty1").setLibelle("Date min");
        pr.getFormu().getChamp("daty2").setLibelle("Date max");
        pr.getFormu().getChamp("reference").setLibelle("Référence");
        pr.getFormu().getChamp("idmagasinlib").setLibelle("Magasin");
        pr.getFormu().getChamp("daty1").setDefaut(utilitaire.Utilitaire.dateDuJour());
        pr.getFormu().getChamp("daty2").setDefaut(utilitaire.Utilitaire.dateDuJour());

    }

    public static void changeChampIntoListe(PageRecherche pr) throws Exception {
        Liste[] listeMere = new Liste[1];
        listeMere[0] = new Liste("etat", new TypeObjet("ETAT"), "desce", "id");
        pr.getFormu().changerEnChamp(listeMere);
        pr.getFormu().getChamp("etat").setLibelle("État");

    }

    public static void handleContextualClickableLink(PageRecherche pr){
         Map<String, String> lienTab = new HashMap<>();
        lienTab.put("modifier",
                pr.getLien() + "?but=a_tahina/v1/bc/boncommande-modif.jsp");
        lienTab.put("fiche",
                pr.getLien() + "?but=a_tahina/v1/bc/boncommande-fiche.jsp");
        pr.getTableau().setLienClicDroite(lienTab);
    }

    public static void handleFicheLink(PageRecherche pr) {
        String[] lienTableau = { pr.getLien()
                + "?but=a_tahina/v1/bc/boncommande-fiche.jsp" };
        String[] colonneLien = { "id" };
        pr.getTableau().setLien(lienTableau);
        pr.getTableau().setColonneLien(colonneLien);
        
    }

    public static PageRecherche getPageRechercheForList(HttpServletRequest request, HttpSession session)
            throws Exception {
        BonCommandePoussinLib f = new BonCommandePoussinLib();
        f.setNomTable("BONCOMMANDE_POUSSIN_LIB");

        String[] listeCrt = { "id", "daty", "reference", "idmagasinlib", "etat" };
        String[] listeInt = { "daty" };
        String[] libEntete = { "id", "daty", "reference", "idmagasinlib", "etatlib" };

        PageRecherche pr = new PageRecherche(f, request, listeCrt, listeInt, 3,
                libEntete, libEntete.length);

        prepareGlobaleInfForPageRecherche(pr, session);
        prepareLibelleChamp(pr);
        changeChampIntoListe(pr);

        pr.creerObjetPage(libEntete, null);

        
        handleFicheLink(pr);
        pr.getTableau().setLibelleAffiche(new String[] {
                "Id", "Date", "Référence", "Magasin", "État"
        });

        handleContextualClickableLink(pr);
        pr.getFormu().setAnotherButton(
                "<a class='btn btn-primary pull-right btn-small' " +
                        "href='" + pr.getLien()
                        + "?but=a_tahina/v1/bc/boncommande-saisie.jsp'>" +
                        "<i class='material-symbols-rounded'>add</i> Saisir</a>");
        return pr;

    }
}
