package a_tahina.fronthelper.facture;

import static a_tahina.page.AtahinaChamp.champ;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.calendrier.CalendrierAbstrait;
import a_tahina.fronthelper.GeneraticFH;
import a_tahina.page.AtahinaPageConsulte;
import a_tahina.page.AtahinaPageInsertMultiple;
import a_tahina.facture.AthProduit;
import a_tahina.page.AtahinaPageInsert;
import a_tahina.page.AtahinaPageRecherche;

public class ProduitFH extends GeneraticFH {

    public static final String DOSSIER     = "a_tahina/v1/facture/produit/";
    public static final String PAGE_LISTE  = DOSSIER + "produit-liste.jsp";
    public static final String PAGE_SAISIE = DOSSIER + "produit-saisie.jsp";
    public static final String CLASSE      = "a_tahina.facture.AthProduit";

    @Override
    public AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                        HttpSession session) throws Exception {
        return liste(new AthProduit(), request, session,
                PAGE_LISTE, "Liste des produits",
                champ("id",      "Id").recherche(),
                champ("libelle", "Libell&eacute;").recherche(),
                champ("unite",   "Unit&eacute;"),
                champ("pu",      "Prix unitaire"),
                champ("actif",   "Actif (1/0)"))
            .boutonSaisir(PAGE_SAISIE);
    }

    @Override
    public AtahinaPageInsert getPageInsert(HttpServletRequest request,
                                           HttpSession session) throws Exception {
        return new AtahinaPageInsert(new AthProduit(), request, session, "Saisie d'un produit")
            .champs(
                champ("libelle", "Libell&eacute;"),
                champ("unite",   "Unit&eacute;"),
                champ("pu",      "Prix unitaire par d&eacute;faut"),
                champ("actif",   "Actif (1 = oui, 0 = non)"))
            .preparer();
    }

    @Override
    public AtahinaPageInsertMultiple getPageInsertMultiple(HttpServletRequest request,
                                                           HttpSession session,
                                                           int nombreLigne) throws Exception {
        throw nonSupporte("getPageInsertMultiple");
    }

    @Override
    public AtahinaPageConsulte getPageConsulte(HttpServletRequest request,
                                               HttpSession session) throws Exception {
        throw nonSupporte("getPageConsulte");
    }

    @Override
    public CalendrierAbstrait getCalendrier(HttpServletRequest request,
                                            HttpSession session) throws Exception {
        throw nonSupporte("getCalendrier");
    }
}
