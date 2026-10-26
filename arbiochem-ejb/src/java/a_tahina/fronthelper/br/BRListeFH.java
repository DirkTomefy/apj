package a_tahina.fronthelper.br;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.br.BonReceptionPoussinLib;
import a_tahina.page.AtahinaPageRecherche;

public class BRListeFH {

    public static final String PAGE_ACTUEL = "a_tahina/v1/br/bonreception-liste.jsp";
    public static final String PAGE_FICHE  = "a_tahina/v1/br/bonreception-fiche.jsp";
    public static final String PAGE_SAISIE = "a_tahina/v1/br/bonreception-saisie.jsp";

    public static AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                               HttpSession session) throws Exception {
        String[] listeCrt  = { "id", "daty", "reference", "idboncommandelib",
                               "idlieureceptionlib", "etat" };
        String[] listeInt  = { "daty" };
        String[] libEntete = { "id", "daty", "reference", "idboncommandelib",
                               "idlieureceptionlib", "etatlib" };

        AtahinaPageRecherche pr = new AtahinaPageRecherche(new BonReceptionPoussinLib(), request, session,
                PAGE_ACTUEL, "Liste des bons de r&eacute;ception poussin", listeCrt, listeInt, libEntete);

        pr.libelle("id", "Id")
          .libelle("reference", "R&eacute;f&eacute;rence")
          .libelle("idboncommandelib", "Bon de commande")
          .libelle("idlieureceptionlib", "Lieu")
          .intervalleDatyAujourdhui()
          .listeEtat("&Eacute;tat");

        return pr.construire("Id", "Date reception", "Reference", "Bon de commande", "Lieu", "Etat")
                 .lienFiche("id", PAGE_FICHE)
                 .menuClicDroit("modifier", PAGE_FICHE)
                 .boutonSaisir(PAGE_SAISIE);
    }
}
