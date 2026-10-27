package a_tahina.fronthelper.br;

import static a_tahina.page.AtahinaChamp.champ;

import a_tahina.fronthelper.GeneraticFH;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.br.BonReceptionPoussinLib;
import a_tahina.page.AtahinaPageRecherche;

public class BRListeFH extends GeneraticFH {

    public static final String PAGE_ACTUEL = "a_tahina/v1/br/bonreception-liste.jsp";
    public static final String PAGE_FICHE  = "a_tahina/v1/br/bonreception-fiche.jsp";
    public static final String PAGE_SAISIE = "a_tahina/v1/br/bonreception-saisie.jsp";

    public static AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                               HttpSession session) throws Exception {
        return liste(new BonReceptionPoussinLib(), request, session,
                PAGE_ACTUEL, "Liste des bons de r&eacute;ception poussin",
                champ("id",                 "Id").recherche().lien(PAGE_FICHE),
                champ("daty",               "Date r&eacute;ception").intervalle().defautAujourdhui(),
                champ("reference",          "R&eacute;f&eacute;rence").recherche(),
                champ("idboncommandelib",   "Bon de commande").recherche(),
                champ("idlieureceptionlib", "Lieu").recherche(),
                champ("etat",               "&Eacute;tat").filtreSeul().listeEtat(),
                champ("etatlib",            "&Eacute;tat"))
            .menuClicDroit("modifier", PAGE_FICHE)
            .boutonSaisir(PAGE_SAISIE);
    }
}
