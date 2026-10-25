package a_tahina.fronthelper.facture;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.facture.AthClient;
import a_tahina.facture.AthFacture;
import a_tahina.facture.AthFactureFille;
import a_tahina.facture.AthFactureLib;
import a_tahina.facture.AthProduit;
import a_tahina.page.AtahinaPageConsulte;
import a_tahina.page.AtahinaPageInsertMultiple;
import a_tahina.page.AtahinaPageRecherche;
import affichage.Liste;

public class FactureFH {

    public static final String DOSSIER      = "a_tahina/v1/facture/facture/";
    public static final String PAGE_LISTE   = DOSSIER + "facture-liste.jsp";
    public static final String PAGE_FICHE   = DOSSIER + "facture-fiche.jsp";
    public static final String PAGE_SAISIE  = DOSSIER + "facture-saisie.jsp";
    public static final String CLASSE       = "a_tahina.facture.AthFacture";
    public static final String CLASSE_FILLE = "a_tahina.facture.AthFactureFille";
    public static final int    NOMBRE_LIGNE = 10;

    // -------- LISTE --------
    public static AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                               HttpSession session) throws Exception {
        String[] listeCrt  = { "id", "daty", "reference", "idclientlib", "etat" };
        String[] listeInt  = { "daty" };
        String[] libEntete = { "id", "daty", "reference", "idclientlib", "montantttc",
                               "montantavoir", "netapayer", "etatlib" };

        AtahinaPageRecherche pr = new AtahinaPageRecherche(new AthFactureLib(), request, session,
                PAGE_LISTE, "Liste des factures", listeCrt, listeInt, libEntete);

        pr.libelle("id", "Id")
          .libelle("reference", "R&eacute;f&eacute;rence")
          .libelle("idclientlib", "Client")
          .intervalleDatyAujourdhui()
          .listeEtat("&Eacute;tat");

        return pr.construire("Id", "Date", "R&eacute;f&eacute;rence", "Client", "Total TTC",
                             "Avoir d&eacute;duit", "Net &agrave; payer", "&Eacute;tat")
                 .lienFiche("id", PAGE_FICHE)
                 .boutonSaisir(PAGE_SAISIE);
    }

    // -------- FICHE --------
    public static AtahinaPageConsulte getPageConsulte(HttpServletRequest request,
                                                      HttpSession session) throws Exception {
        return new AtahinaPageConsulte(new AthFactureLib(), request, session, "Fiche facture")
                .libelle("id", "Id")
                .libelle("daty", "Date")
                .libelle("reference", "R&eacute;f&eacute;rence")
                .libelle("idclientlib", "Client")
                .libelle("etatlib", "&Eacute;tat")
                .libelle("remarque", "Remarque")
                .libelle("montantht", "Total HT")
                .libelle("montanttva", "TVA")
                .libelle("montantttc", "Total TTC")
                .libelle("pourcentageavoir", "% maximum payable par avoir")
                .libelle("plafondavoir", "Plafond d'avoir de cette facture")
                .libelle("montantavoir", "Avoir d&eacute;duit")
                .libelle("netapayer", "Net &agrave; payer")
                .cacher("idclient", "etat");
    }

    // -------- SAISIE --------
    public static AtahinaPageInsertMultiple getPageInsertMultiple(HttpServletRequest request,
                                                                  HttpSession session,
                                                                  int nombreLigne) throws Exception {
        AtahinaPageInsertMultiple pi = new AtahinaPageInsertMultiple(
                new AthFacture(), new AthFactureFille(), request, session, nombreLigne);

        pi.listes(new Liste("idclient", new AthClient(), "nom", "id"))
          .listesFille(new Liste("idproduit", new AthProduit(), "libelle", "id", " and actif = 1"));

        pi.libelle("daty", "Date")
          .libelle("reference", "R&eacute;f&eacute;rence")
          .libelle("idclient", "Client")
          .libelle("remarque", "Remarque")
          .defaut("daty", utilitaire.Utilitaire.dateDuJour())
          .cacher("etat", "montantavoir");

        pi.libelleFille("idproduit", "Produit")
          .libelleFille("qte", "Qt&eacute;")
          .libelleFille("pu", "P.U. (0 = prix du produit)")
          .libelleFille("tva", "TVA %")
          .cacherFille("id", "idmere");

        pi.charger();

        pi.defautFille("qte", "1").defautFille("pu", "0").defautFille("tva", "0");

        return pi.finaliser("idproduit", "qte", "pu", "tva");
    }
}
