package a_tahina.fronthelper.ic;

import static a_tahina.page.AtahinaChamp.champ;

import a_tahina.fronthelper.GeneraticFH;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.ic.InsertionCompartimentLib;
import a_tahina.page.AtahinaPageConsulte;
import a_tahina.ic.InsertionCompFilleLib;
import a_tahina.prevision.LotLib;
import a_tahina.page.AtahinaPageRecherche;
import a_tahina.page.AtahinaUtil;

public class ICFicheFH extends GeneraticFH {

    public static final String PAGE_ACTUEL   = "a_tahina/v1/ic/insertioncompartiment-fiche.jsp";
    public static final String PAGE_LISTE    = "a_tahina/v1/ic/insertioncompartiment-liste.jsp";
    public static final String PAGE_BR_FICHE = "a_tahina/v1/br/bonreception-fiche.jsp";
    public static final String CLASSE        = "a_tahina.ic.InsertionCompartiment";

    public static AtahinaPageConsulte getPageConsulte(HttpServletRequest request,
                                                      HttpSession session) throws Exception {
        return consulte(new InsertionCompartimentLib(), request, session, "Fiche insertion compartiment",
                champ("id",                "Id"),
                champ("daty",              "Date"),
                champ("reference",         "R&eacute;f&eacute;rence"),
                champ("idbonreceptionlib", "Bon de r&eacute;ception"),
                champ("etatlib",           "&Eacute;tat"),
                champ("remarque",          "Remarque"));
    }

    public static String ongletDetail(HttpServletRequest request, HttpSession session, String idMere) throws Exception {
        return onglet(new InsertionCompFilleLib(), request, session, PAGE_ACTUEL, idMere, "inc/ic-liste-detail", "idmere", "Aucune ligne trouv&eacute;e",
                champ("compartimentlib", "Compartiment"),
                champ("qte", "Qt&eacute;"),
                champ("pu", "P.U."),
                champ("sexelib", "Sexe"),
                champ("santelib", "Sant&eacute;"));
    }

    public static String ongletLots(HttpServletRequest request, HttpSession session, String idMere) throws Exception {
        return AtahinaPageRecherche.onglet(new LotLib(), request, session, PAGE_ACTUEL, idMere, "inc/ic-liste-lots",
                champ("id", "Id"),
                champ("produitlib", "Produit"),
                champ("souchelibr", "Souche"),
                champ("qteinit", "Qt&eacute;"),
                champ("pu", "P.U."),
                champ("qteActuelle", "Qt&eacute; actuelle"),
                champ("sexelib", "Sexe"),
                champ("santelib", "Sant&eacute;"),
                champ("compartimentlib", "Compartiment"))
            .filtre(AtahinaUtil.dansSelect("idinsertcompfille", "INSERTION_COMPARTIMENT_FILLE", "id", "idmere", idMere)).parPage(20)
            .construire()
            .getHtmlOnglet("Aucun lot cr&eacute;&eacute;");
    }
}
