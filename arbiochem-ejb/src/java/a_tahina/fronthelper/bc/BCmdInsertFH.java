package a_tahina.fronthelper.bc;

import static a_tahina.page.AtahinaChamp.champ;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.calendrier.CalendrierAbstrait;
import a_tahina.fronthelper.GeneraticFH;
import a_tahina.page.AtahinaPageRecherche;
import a_tahina.page.AtahinaPageConsulte;
import a_tahina.page.AtahinaPageInsert;
import a_tahina.bc.BonCommandePoussin;
import a_tahina.bc.BonCommandePoussinFille;
import a_tahina.page.AtahinaPageInsertMultiple;
import a_tahina.prevision.Fournisseur;
import ferme.configuration.Souche;

public class BCmdInsertFH extends GeneraticFH {

    public static final int NOMBRE_LIGNE = 10;

    @Override
    public AtahinaPageInsertMultiple getPageInsertMultiple(HttpServletRequest request,
                                                           HttpSession session,
                                                           int nombreLigne) throws Exception {
        AtahinaPageInsertMultiple pi = new AtahinaPageInsertMultiple(
                new BonCommandePoussin(), new BonCommandePoussinFille(), request, session, nombreLigne);

        pi.champs(
            champ("daty",      "Date").defautAujourdhui(),
            champ("reference", "Référence"),
            champ("descri",    "Description"),
            champ("idmagasin", "Magasin").liste(new magasin.Magasin(), "val", "id"),
            champ("remarque",  "Remarque"));

        pi.colonnes(
            champ("idproduit",   "Produit").liste(new produits.Ingredients(), "libelle", "id"),
            champ("idsouche",    "Souche").liste(new Souche(), "val", "id"),
            champ("fournisseur", "Fournisseur").liste(new Fournisseur(), "nom", "id"),
            champ("qte",         "Qté").defaut("0"),
            champ("pu",          "P.U.").defaut("0"));

        return pi.preparer();
    }

    @Override
    public AtahinaPageInsert getPageInsert(HttpServletRequest request,
                                           HttpSession session) throws Exception {
        throw nonSupporte("getPageInsert");
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

    @Override
    public AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                        HttpSession session) throws Exception {
        throw nonSupporte("getPageRechercheForList");
    }
}
