package a_tahina.fronthelper.ic;

import static a_tahina.page.AtahinaChamp.champ;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.calendrier.CalendrierAbstrait;
import a_tahina.fronthelper.GeneraticFH;
import a_tahina.page.AtahinaPageConsulte;
import a_tahina.page.AtahinaPageInsertMultiple;
import a_tahina.page.AtahinaPageInsert;
import a_tahina.ic.InsertionCompartimentLib;
import a_tahina.page.AtahinaPageRecherche;

public class ICListeFH extends GeneraticFH {

    public static final String PAGE_ACTUEL = ICFicheFH.PAGE_LISTE;
    public static final String PAGE_FICHE  = ICFicheFH.PAGE_ACTUEL;
    public static final String PAGE_SAISIE = ICFicheFH.PAGE_SAISIE;

    @Override
    public AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                        HttpSession session) throws Exception {
        return liste(new InsertionCompartimentLib(), request, session,
                PAGE_ACTUEL, "Liste des insertions compartiment",
                champ("id",                "Id").recherche().lien(PAGE_FICHE),
                champ("daty",              "Date").intervalle().defautAujourdhui(),
                champ("reference",         "R&eacute;f&eacute;rence").recherche(),
                champ("idbonreceptionlib", "Bon de r&eacute;ception").recherche(),
                champ("etat",              "&Eacute;tat").filtreSeul().listeEtat(),
                champ("etatlib",           "&Eacute;tat"))
            .menuClicDroit("modifier", PAGE_FICHE)
            .boutonSaisir(PAGE_SAISIE);
    }

    @Override
    public AtahinaPageInsert getPageInsert(HttpServletRequest request,
                                           HttpSession session) throws Exception {
        throw nonSupporte("getPageInsert");
    }

    @Override
    public AtahinaPageInsertMultiple getPageInsertMultiple(HttpServletRequest request,
                                                           HttpSession session,
                                                           int nombreLigne) throws Exception {
        throw nonSupporte("getPageInsertMultiple");
    }

    @Override
    public AtahinaPageConsulte getPageConsulte(HttpServletRequest request,
                                               HttpSession session) throws Exception {
        throw nonSupporte("getPageConsulte");
    }

    @Override
    public CalendrierAbstrait getCalendrier(HttpServletRequest request,
                                            HttpSession session) throws Exception {
        throw nonSupporte("getCalendrier");
    }
}
