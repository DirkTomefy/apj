package a_tahina.fronthelper.ic;

import static a_tahina.page.AtahinaChamp.champ;

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
        return new AtahinaPageRecherche(new InsertionCompartimentLib(), request, session,
                PAGE_ACTUEL, "Liste des insertions compartiment",
                champ("id",                "Id").recherche().lien(PAGE_FICHE),
                champ("daty",              "Date").intervalle().defautAujourdhui(),
                champ("reference",         "R&eacute;f&eacute;rence").recherche(),
                champ("idbonreceptionlib", "Bon de r&eacute;ception").recherche(),
                champ("etat",              "&Eacute;tat").filtreSeul().listeEtat(),
                champ("etatlib",           "&Eacute;tat"))
            .construire()
            .menuClicDroit("modifier", PAGE_FICHE)
            .boutonSaisir(PAGE_SAISIE);
    }
}
