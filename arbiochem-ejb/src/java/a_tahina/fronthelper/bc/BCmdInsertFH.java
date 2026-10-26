package a_tahina.fronthelper.bc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.bc.BonCommandePoussin;
import a_tahina.bc.BonCommandePoussinFille;
import a_tahina.page.AtahinaPageInsertMultiple;
import a_tahina.prevision.Fournisseur;
import affichage.Liste;
import ferme.configuration.Souche;

public class BCmdInsertFH {

    public static final int NOMBRE_LIGNE = 10;

    public static AtahinaPageInsertMultiple getPageInsertMultiple(HttpServletRequest request,
                                                                  HttpSession session,
                                                                  int nombreLigne) throws Exception {
        AtahinaPageInsertMultiple pi = new AtahinaPageInsertMultiple(
                new BonCommandePoussin(), new BonCommandePoussinFille(), request, session, nombreLigne);

        pi.listes(new Liste("idmagasin", new magasin.Magasin(), "val", "id"))
          .listesFille(
                new Liste("idproduit",   new produits.Ingredients(), "libelle", "id"),
                new Liste("idsouche",    new Souche(),               "val",     "id"),
                new Liste("fournisseur", new Fournisseur(),          "nom",     "id"));

        pi.libelle("daty", "Date")
          .libelle("reference", "Référence")
          .libelle("descri", "Description")
          .libelle("idmagasin", "Magasin")
          .libelle("remarque", "Remarque")
          .defaut("daty", utilitaire.Utilitaire.dateDuJour())
          .cacher("etat");

        pi.libelleFille("idproduit", "Produit")
          .libelleFille("idsouche", "Souche")
          .libelleFille("fournisseur", "Fournisseur")
          .libelleFille("qte", "Qté")
          .libelleFille("pu", "P.U.")
          .cacherFille("id", "idmere");

        pi.charger();

        pi.defautFille("qte", "0").defautFille("pu", "0");

        return pi.finaliser("idproduit", "idsouche", "fournisseur", "qte", "pu");
    }
}
