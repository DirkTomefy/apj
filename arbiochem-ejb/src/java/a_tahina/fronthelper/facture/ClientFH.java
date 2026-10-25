package a_tahina.fronthelper.facture;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.facture.AthClient;
import a_tahina.facture.AthClientSolde;
import a_tahina.page.AtahinaPageConsulte;
import a_tahina.page.AtahinaPageInsert;
import a_tahina.page.AtahinaPageRecherche;

public class ClientFH {

    public static final String DOSSIER     = "a_tahina/v1/facture/client/";
    public static final String PAGE_LISTE  = DOSSIER + "client-liste.jsp";
    public static final String PAGE_FICHE  = DOSSIER + "client-fiche.jsp";
    public static final String PAGE_SAISIE = DOSSIER + "client-saisie.jsp";
    public static final String CLASSE      = "a_tahina.facture.AthClient";

    // -------- LISTE : une ligne par client avec son solde d'avoir --------
    public static AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                               HttpSession session) throws Exception {
        String[] listeCrt  = { "id", "nom" };
        String[] listeInt  = { };
        String[] libEntete = { "id", "nom", "pourcentageavoir", "totalavoir", "totalutilise", "solde" };

        AtahinaPageRecherche pr = new AtahinaPageRecherche(new AthClientSolde(), request, session,
                PAGE_LISTE, "Liste des clients", listeCrt, listeInt, libEntete);

        pr.libelle("id", "Id").libelle("nom", "Nom");

        return pr.construire("Id", "Nom", "% avoir max", "Avoirs vis&eacute;s", "Avoirs utilis&eacute;s", "Solde")
                 .lienFiche("id", PAGE_FICHE)
                 .boutonSaisir(PAGE_SAISIE);
    }

    // -------- FICHE --------
    public static AtahinaPageConsulte getPageConsulte(HttpServletRequest request,
                                                      HttpSession session) throws Exception {
        return new AtahinaPageConsulte(new AthClientSolde(), request, session, "Fiche client")
                .libelle("id", "Id")
                .libelle("nom", "Nom")
                .libelle("pourcentageavoir", "% maximum d'une facture payable par avoir")
                .libelle("totalavoir", "Total des avoirs vis&eacute;s")
                .libelle("totalutilise", "Total d&eacute;j&agrave; utilis&eacute;")
                .libelle("solde", "Solde d'avoir disponible");
    }

    // -------- SAISIE --------
    public static AtahinaPageInsert getPageInsert(HttpServletRequest request,
                                                  HttpSession session) throws Exception {
        AtahinaPageInsert pi = new AtahinaPageInsert(new AthClient(), request, session, "Saisie d'un client");
        pi.libelle("nom", "Nom")
          .libelle("contact", "Contact")
          .libelle("adresse", "Adresse")
          .libelle("mail", "Mail")
          .libelle("pourcentageavoir", "% maximum d'une facture payable par avoir (0 &agrave; 100)")
          .cacher("id");
        return pi.preparer();
    }
}
