package a_tahina.facture.fh;

import static a_tahina.core.page.AtahinaChampHelper.champ;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.calendrier.Calendrier;
import a_tahina.core.GeneraticFH;
import a_tahina.core.page.AtahinaPageInsertMultiple;
import a_tahina.facture.orm.AthClient;
import a_tahina.facture.orm.AthClientSolde;
import a_tahina.core.page.AtahinaPageConsulte;
import a_tahina.core.page.AtahinaPageInsert;
import a_tahina.core.page.AtahinaPageRecherche;
import a_tahina.facture.orm.AthFactureLib;
import a_tahina.facture.orm.AthAvoirLib;

public class ClientFH extends GeneraticFH {

    public static final String DOSSIER     = "a_tahina/v1/facture/client/";
    public static final String PAGE_LISTE  = DOSSIER + "client-liste.jsp";
    public static final String PAGE_FICHE  = DOSSIER + "client-fiche.jsp";
    public static final String PAGE_SAISIE = DOSSIER + "client-saisie.jsp";
    public static final String CLASSE      = "a_tahina.facture.orm.AthClient";

    @Override
    public AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                        HttpSession session) throws Exception {
        return liste(new AthClientSolde(), request, session,
                PAGE_LISTE, "Liste des clients",
                champ("id",               "Id").recherche().lien(PAGE_FICHE),
                champ("nom",              "Nom").recherche(),
                champ("pourcentageavoir", "% avoir max"),
                champ("totalavoir",       "Avoirs vis&eacute;s"),
                champ("totalutilise",     "Avoirs utilis&eacute;s"),
                champ("solde",            "Solde"))
            .boutonSaisir(PAGE_SAISIE);
    }

    @Override
    public AtahinaPageConsulte getPageConsulte(HttpServletRequest request,
                                               HttpSession session) throws Exception {
        return consulte(new AthClientSolde(), request, session, "Fiche client",
                champ("id",               "Id"),
                champ("nom",              "Nom"),
                champ("pourcentageavoir", "% maximum d'une facture payable par avoir"),
                champ("totalavoir",       "Total des avoirs vis&eacute;s"),
                champ("totalutilise",     "Total d&eacute;j&agrave; utilis&eacute;"),
                champ("solde",            "Solde d'avoir disponible"));
    }

    @Override
    public AtahinaPageInsert getPageInsert(HttpServletRequest request,
                                           HttpSession session) throws Exception {
        return new AtahinaPageInsert(new AthClient(), request, session, "Saisie d'un client")
            .champs(
                champ("nom",              "Nom"),
                champ("contact",          "Contact"),
                champ("adresse",          "Adresse"),
                champ("mail",             "Mail"),
                champ("pourcentageavoir", "% maximum d'une facture payable par avoir (0 &agrave; 100)"))
            .preparer();
    }

    public static String ongletFactures(HttpServletRequest request, HttpSession session, String idMere) throws Exception {
        return onglet(new AthFactureLib(), request, session, PAGE_FICHE, idMere, "inc/client-factures", "idclient", "Aucune facture",
                champ("id", "Id").lien(FactureFH.PAGE_FICHE),
                champ("daty", "Date"),
                champ("reference", "R&eacute;f&eacute;rence"),
                champ("montantttc", "Total TTC"),
                champ("montantavoir", "Avoir d&eacute;duit"),
                champ("netapayer", "Net &agrave; payer"),
                champ("etatlib", "&Eacute;tat"));
    }

    public static String ongletAvoirs(HttpServletRequest request, HttpSession session, String idMere) throws Exception {
        return onglet(new AthAvoirLib(), request, session, PAGE_FICHE, idMere, "inc/client-avoirs", "idclient", "Aucun avoir",
                champ("id", "Id").lien(AvoirFH.PAGE_FICHE),
                champ("daty", "Date"),
                champ("idfacturelib", "Facture"),
                champ("montantttc", "Montant TTC"),
                champ("montantutilise", "D&eacute;j&agrave; utilis&eacute;"),
                champ("reste", "Reste"),
                champ("etatlib", "&Eacute;tat"));
    }

    @Override
    public AtahinaPageInsertMultiple getPageInsertMultiple(HttpServletRequest request,
                                                           HttpSession session,
                                                           int nombreLigne) throws Exception {
        throw nonSupporte("getPageInsertMultiple");
    }

    @Override
    public Calendrier<?> getCalendrier(HttpServletRequest request,
                                            HttpSession session) throws Exception {
        throw nonSupporte("getCalendrier");
    }
}
