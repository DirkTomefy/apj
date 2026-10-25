package a_tahina.fronthelper.bc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.bc.BonCommandePoussinLib;
import a_tahina.page.AtahinaPageRecherche;

public class BCmdListeFH {

    public static final String PAGE_ACTUEL = BCmdFicheFH.PAGE_LISTE;

    public static AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                               HttpSession session) throws Exception {
        String[] listeCrt  = { "id", "daty", "reference", "idmagasinlib", "etat" };
        String[] listeInt  = { "daty" };
        String[] libEntete = { "id", "daty", "reference", "idmagasinlib", "etatlib" };

        AtahinaPageRecherche pr = new AtahinaPageRecherche(new BonCommandePoussinLib(), request, session,
                PAGE_ACTUEL, "Liste des bons de commande poussin", listeCrt, listeInt, libEntete);

        pr.libelle("id", "Id")
          .libelle("reference", "Référence")
          .libelle("idmagasinlib", "Magasin")
          .intervalleDatyAujourdhui()
          .listeEtat("État");

        return pr.construire("Id", "Date", "Référence", "Magasin", "État")
                 .lienFiche("id", BCmdFicheFH.PAGE_ACTUEL)
                 .menuClicDroit("modifier", BCmdFicheFH.PAGE_MODIF)
                 .boutonSaisir(BCmdFicheFH.PAGE_SAISIE);
    }
}
