package a_tahina.fronthelper.bc;

import static a_tahina.page.AtahinaChamp.champ;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.bc.BonCommandePoussin;
import a_tahina.bc.BonCommandePoussinFille;
import a_tahina.page.AtahinaPageInsertMultiple;
import a_tahina.prevision.Fournisseur;
import ferme.configuration.Souche;

public class BCmdInsertFH {

    public static final int NOMBRE_LIGNE = 10;

    public static AtahinaPageInsertMultiple getPageInsertMultiple(HttpServletRequest request,
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
}
