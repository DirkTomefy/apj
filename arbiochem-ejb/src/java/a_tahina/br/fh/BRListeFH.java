package a_tahina.br.fh;

import static a_tahina.core.page.AtahinaChamp.champ;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.calendrier.CalendrierAbstrait;
import a_tahina.core.GeneraticFH;
import a_tahina.core.page.AtahinaPageConsulte;
import a_tahina.core.page.AtahinaPageInsertMultiple;
import a_tahina.core.page.AtahinaPageInsert;
import a_tahina.br.orm.BonReceptionPoussinLib;
import a_tahina.core.page.AtahinaPageRecherche;

public class BRListeFH extends GeneraticFH {

    public static final String PAGE_ACTUEL = BRFicheFH.PAGE_LISTE;
    public static final String PAGE_FICHE  = BRFicheFH.PAGE_ACTUEL;
    public static final String PAGE_SAISIE = BRFicheFH.PAGE_SAISIE;

    @Override
    public AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
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
