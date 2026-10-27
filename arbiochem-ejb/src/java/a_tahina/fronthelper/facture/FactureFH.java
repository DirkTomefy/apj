package a_tahina.fronthelper.facture;

import static a_tahina.page.AtahinaChamp.champ;

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
import a_tahina.facture.AthFactureFilleLib;
import a_tahina.facture.AthAvoirLib;
import a_tahina.facture.AthAvoirUsage;

public class FactureFH {

    public static final String DOSSIER      = "a_tahina/v1/facture/facture/";
    public static final String PAGE_LISTE   = DOSSIER + "facture-liste.jsp";
    public static final String PAGE_FICHE   = DOSSIER + "facture-fiche.jsp";
    public static final String PAGE_SAISIE  = DOSSIER + "facture-saisie.jsp";
    public static final String CLASSE       = "a_tahina.facture.AthFacture";
    public static final String CLASSE_FILLE = "a_tahina.facture.AthFactureFille";
    public static final int    NOMBRE_LIGNE = 10;

    public static AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                               HttpSession session) throws Exception {
        return new AtahinaPageRecherche(new AthFactureLib(), request, session,
                PAGE_LISTE, "Liste des factures",
                champ("id",           "Id").recherche().lien(PAGE_FICHE),
                champ("daty",         "Date").intervalle().defautAujourdhui(),
                champ("reference",    "R&eacute;f&eacute;rence").recherche(),
                champ("idclientlib",  "Client").recherche(),
                champ("montantttc",   "Total TTC"),
                champ("montantavoir", "Avoir d&eacute;duit"),
                champ("netapayer",    "Net &agrave; payer"),
                champ("etat",         "&Eacute;tat").filtreSeul().listeEtat(),
                champ("etatlib",      "&Eacute;tat"))
            .construire()
            .boutonSaisir(PAGE_SAISIE);
    }

    public static AtahinaPageConsulte getPageConsulte(HttpServletRequest request,
                                                      HttpSession session) throws Exception {
        return new AtahinaPageConsulte(new AthFactureLib(), request, session, "Fiche facture")
            .champs(
                champ("id",               "Id"),
                champ("daty",             "Date"),
                champ("reference",        "R&eacute;f&eacute;rence"),
                champ("idclientlib",      "Client"),
                champ("etatlib",          "&Eacute;tat"),
                champ("montantht",        "Total HT"),
                champ("montanttva",       "TVA"),
                champ("montantttc",       "Total TTC"),
                champ("pourcentageavoir", "% maximum payable par avoir"),
                champ("plafondavoir",     "Plafond d'avoir de cette facture"),
                champ("montantavoir",     "Avoir d&eacute;duit"),
                champ("netapayer",        "Net &agrave; payer"),
                champ("remarque",         "Remarque"));
    }

    public static AtahinaPageInsertMultiple getPageInsertMultiple(HttpServletRequest request,
                                                                  HttpSession session,
                                                                  int nombreLigne) throws Exception {
        return new AtahinaPageInsertMultiple(new AthFacture(), new AthFactureFille(), request, session, nombreLigne)
            .champs(
                champ("daty",      "Date").defautAujourdhui(),
                champ("reference", "R&eacute;f&eacute;rence"),
                champ("idclient",  "Client").liste(new AthClient(), "nom", "id"),
                champ("remarque",  "Remarque"))
            .colonnes(
                champ("idproduit", "Produit").liste(new AthProduit(), "libelle", "id", " and actif = 1"),
                champ("qte",       "Qt&eacute;").defaut("1"),
                champ("pu",        "P.U. (0 = prix du produit)").defaut("0"),
                champ("tva",       "TVA %").defaut("0"))
            .preparer();
    }

    public static String ongletDetail(HttpServletRequest request, HttpSession session, String idMere) throws Exception {
        return AtahinaPageRecherche.onglet(new AthFactureFilleLib(), request, session, PAGE_FICHE, idMere, "inc/facture-detail",
                champ("produitlib", "Produit"),
                champ("qte", "Qt&eacute;"),
                champ("pu", "P.U."),
                champ("tva", "TVA %"),
                champ("montantht", "Total HT"),
                champ("montantttc", "Total TTC"),
                champ("qteretournee", "Qt&eacute; retourn&eacute;e"),
                champ("qtedisponible", "Encore retournable"))
            .filtreEgal("idmere", idMere)
            .construire()
            .getHtmlOnglet("Aucune ligne");
    }

    public static String ongletAvoirs(HttpServletRequest request, HttpSession session, String idMere) throws Exception {
        return AtahinaPageRecherche.onglet(new AthAvoirLib(), request, session, PAGE_FICHE, idMere, "inc/facture-avoirs",
                champ("id", "Id").lien(AvoirFH.PAGE_FICHE),
                champ("daty", "Date"),
                champ("montantttc", "Montant TTC"),
                champ("montantutilise", "D&eacute;j&agrave; utilis&eacute;"),
                champ("reste", "Reste"),
                champ("etatlib", "&Eacute;tat"))
            .filtreEgal("idfacture", idMere)
            .construire()
            .getHtmlOnglet("Aucun avoir sur cette facture");
    }

    public static String ongletUsages(HttpServletRequest request, HttpSession session, String idMere) throws Exception {
        return AtahinaPageRecherche.onglet(new AthAvoirUsage(), request, session, PAGE_FICHE, idMere, "inc/facture-usages",
                champ("idavoir", "Avoir").lien(AvoirFH.PAGE_FICHE, "id"),
                champ("montant", "Montant d&eacute;duit"),
                champ("daty", "Date"))
            .filtreEgal("idfacture", idMere)
            .construire()
            .getHtmlOnglet("Aucun avoir d&eacute;duit sur cette facture");
    }
}
