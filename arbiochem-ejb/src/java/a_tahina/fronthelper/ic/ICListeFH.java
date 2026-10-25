package a_tahina.fronthelper.ic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.ic.InsertionCompartimentLib;
import a_tahina.page.AtahinaPageRecherche;

public class ICListeFH {

    public static final String PAGE_ACTUEL = "a_tahina/v1/ic/insertioncompartiment-liste.jsp";
    public static final String PAGE_FICHE  = "a_tahina/v1/ic/insertioncompartiment-fiche.jsp";
    public static final String PAGE_SAISIE = "a_tahina/v1/ic/insertioncompartiment-saisie.jsp";

    public static AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                               HttpSession session) throws Exception {
        String[] listeCrt  = { "id", "daty", "reference", "idbonreceptionlib", "etat" };
        String[] listeInt  = { "daty" };
        String[] libEntete = { "id", "daty", "reference", "idbonreceptionlib", "etatlib" };

        AtahinaPageRecherche pr = new AtahinaPageRecherche(new InsertionCompartimentLib(), request, session,
                PAGE_ACTUEL, "Liste des insertions compartiment", listeCrt, listeInt, libEntete);

        pr.libelle("id", "Id")
          .libelle("reference", "R&eacute;f&eacute;rence")
          .libelle("idbonreceptionlib", "Bon de r&eacute;ception")
          .intervalleDatyAujourdhui()
          .listeEtat("&Eacute;tat");

        // pas de page de modification : « modifier » ouvre la fiche
        return pr.construire("Id", "Date", "R&eacute;f&eacute;rence", "BR", "&Eacute;tat")
                 .lienFiche("id", PAGE_FICHE)
                 .menuClicDroit("modifier", PAGE_FICHE)
                 .boutonSaisir(PAGE_SAISIE);
    }
}
