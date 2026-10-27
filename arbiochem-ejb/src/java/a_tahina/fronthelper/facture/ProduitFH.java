package a_tahina.fronthelper.facture;

import static a_tahina.page.AtahinaChamp.champ;

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
        return new AtahinaPageRecherche(new AthProduit(), request, session,
                PAGE_LISTE, "Liste des produits",
                champ("id",      "Id").recherche(),
                champ("libelle", "Libell&eacute;").recherche(),
                champ("unite",   "Unit&eacute;"),
                champ("pu",      "Prix unitaire"),
                champ("actif",   "Actif (1/0)"))
            .construire()
            .boutonSaisir(PAGE_SAISIE);
    }

    public static AtahinaPageInsert getPageInsert(HttpServletRequest request,
                                                  HttpSession session) throws Exception {
        return new AtahinaPageInsert(new AthProduit(), request, session, "Saisie d'un produit")
            .champs(
                champ("libelle", "Libell&eacute;"),
                champ("unite",   "Unit&eacute;"),
                champ("pu",      "Prix unitaire par d&eacute;faut"),
                champ("actif",   "Actif (1 = oui, 0 = non)"))
            .preparer();
    }
}
