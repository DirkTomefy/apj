package a_tahina.fronthelper.facture;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.facture.AthProduit;
import a_tahina.page.AtahinaPageInsert;
import a_tahina.page.AtahinaPageRecherche;

public class ProduitFH {

    public static final String DOSSIER     = "a_tahina/v1/facture/produit/";
    public static final String PAGE_LISTE  = DOSSIER + "produit-liste.jsp";
    public static final String PAGE_SAISIE = DOSSIER + "produit-saisie.jsp";
    public static final String CLASSE      = "a_tahina.facture.AthProduit";

    public static AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                               HttpSession session) throws Exception {
        String[] listeCrt  = { "id", "libelle" };
        String[] listeInt  = { };
        String[] libEntete = { "id", "libelle", "unite", "pu", "actif" };

        AtahinaPageRecherche pr = new AtahinaPageRecherche(new AthProduit(), request, session,
                PAGE_LISTE, "Liste des produits", listeCrt, listeInt, libEntete);

        pr.libelle("id", "Id").libelle("libelle", "Libell&eacute;");

        return pr.construire("Id", "Libell&eacute;", "Unit&eacute;", "Prix unitaire", "Actif (1/0)")
                 .boutonSaisir(PAGE_SAISIE);
    }

    public static AtahinaPageInsert getPageInsert(HttpServletRequest request,
                                                  HttpSession session) throws Exception {
        AtahinaPageInsert pi = new AtahinaPageInsert(new AthProduit(), request, session, "Saisie d'un produit");
        pi.libelle("libelle", "Libell&eacute;")
          .libelle("unite", "Unit&eacute;")
          .libelle("pu", "Prix unitaire par d&eacute;faut")
          .libelle("actif", "Actif (1 = oui, 0 = non)")
          .cacher("id");
        return pi.preparer();
    }
}
