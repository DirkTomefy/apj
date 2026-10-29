package a_tahina.facture.fh;

import static a_tahina.core.page.AtahinaChampHelper.champ;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.core.GeneraticFH;
import a_tahina.facture.orm.AthClient;
import a_tahina.facture.orm.AthFacture;
import a_tahina.facture.orm.AthFactureFille;
import a_tahina.facture.orm.AthFactureLib;
import a_tahina.core.page.AtahinaPageConsulte;
import a_tahina.core.page.AtahinaPageInsertMultiple;
import a_tahina.core.page.AtahinaPageRecherche;
import a_tahina.facture.orm.AthFactureFilleLib;
import a_tahina.facture.orm.AthAvoirLib;
import a_tahina.facture.orm.AthAvoirUsage;

public class FactureFH extends GeneraticFH {

    public static final String DOSSIER      = "a_tahina/v1/facture/facture/";
    public static final String PAGE_LISTE   = DOSSIER + "facture-liste.jsp";
    public static final String PAGE_FICHE   = DOSSIER + "facture-fiche.jsp";
    public static final String PAGE_SAISIE  = DOSSIER + "facture-saisie.jsp";
    public static final String CLASSE       = "a_tahina.facture.orm.AthFacture";
    public static final String CLASSE_FILLE = "a_tahina.facture.orm.AthFactureFille";
    public static final int    NOMBRE_LIGNE = 10;

    @Override
    public AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                        HttpSession session) throws Exception {
        return liste(new AthFactureLib(), request, session,
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
            .boutonSaisir(PAGE_SAISIE);
    }

    @Override
    public AtahinaPageConsulte getPageConsulte(HttpServletRequest request,
                                               HttpSession session) throws Exception {
        return consulte(new AthFactureLib(), request, session, "Fiche facture",
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

    @Override
    public AtahinaPageInsertMultiple getPageInsertMultiple(HttpServletRequest request,
                                                           HttpSession session,
                                                           int nombreLigne) throws Exception {
        return new AtahinaPageInsertMultiple(new AthFacture(), new AthFactureFille(), request, session, nombreLigne)
            .champs(
                champ("daty",      "Date").defautAujourdhui(),
                champ("reference", "R&eacute;f&eacute;rence"),
                champ("idclient",  "Client").liste(new AthClient(), "nom", "id"),
                champ("remarque",  "Remarque"))
            .colonnes(
                champ("typevente", "Vente").typeObjet("ATH_TYPEVENTE"),
                champ("sexe",      "Sexe (poulet)").typeObjet("SEXE"),
                champ("qte",       "Qt&eacute;").defaut("1"),
                champ("pu",        "P.U. (poulet : 0 = prix du poussin)").defaut("0"),
                champ("tva",       "TVA %").defaut("0"))
            .preparer();
    }

    public static String ongletDetail(HttpServletRequest request, HttpSession session, String idMere) throws Exception {
        return onglet(new AthFactureFilleLib(), request, session, PAGE_FICHE, idMere, "inc/facture-detail", "idmere", "Aucune ligne",
                champ("produitlib", "Produit"),
                champ("qte", "Qt&eacute;"),
                champ("pu", "P.U."),
                champ("tva", "TVA %"),
                champ("montantht", "Total HT"),
                champ("montantttc", "Total TTC"),
                champ("qteretournee", "Qt&eacute; retourn&eacute;e"),
                champ("qtedisponible", "Encore retournable"));
    }

    public static String ongletAvoirs(HttpServletRequest request, HttpSession session, String idMere) throws Exception {
        return onglet(new AthAvoirLib(), request, session, PAGE_FICHE, idMere, "inc/facture-avoirs", "idfacture", "Aucun avoir sur cette facture",
                champ("id", "Id").lien(AvoirFH.PAGE_FICHE),
                champ("daty", "Date"),
                champ("montantttc", "Montant TTC"),
                champ("montantutilise", "D&eacute;j&agrave; utilis&eacute;"),
                champ("reste", "Reste"),
                champ("etatlib", "&Eacute;tat"));
    }

    public static String ongletUsages(HttpServletRequest request, HttpSession session, String idMere) throws Exception {
        return onglet(new AthAvoirUsage(), request, session, PAGE_FICHE, idMere, "inc/facture-usages", "idfacture", "Aucun avoir d&eacute;duit sur cette facture",
                champ("idavoir", "Avoir").lien(AvoirFH.PAGE_FICHE, "id"),
                champ("montant", "Montant d&eacute;duit"),
                champ("daty", "Date"));
    }
}
