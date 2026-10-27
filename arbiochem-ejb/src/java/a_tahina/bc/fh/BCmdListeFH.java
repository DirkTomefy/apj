package a_tahina.bc.fh;

import static a_tahina.core.page.AtahinaChamp.champ;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.calendrier.CalendrierAbstrait;
import a_tahina.core.GeneraticFH;
import a_tahina.core.page.AtahinaPageConsulte;
import a_tahina.core.page.AtahinaPageInsertMultiple;
import a_tahina.core.page.AtahinaPageInsert;
import a_tahina.bc.orm.BonCommandePoussinLib;
import a_tahina.core.page.AtahinaPageRecherche;

public class BCmdListeFH extends GeneraticFH {

    public static final String PAGE_ACTUEL = BCmdFicheFH.PAGE_LISTE;

    @Override
    public AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                        HttpSession session) throws Exception {
        return liste(new BonCommandePoussinLib(), request, session,
                PAGE_ACTUEL, "Liste des bons de commande poussin",
                champ("id",           "Id").recherche().lien(BCmdFicheFH.PAGE_ACTUEL),
                champ("daty",         "Date").intervalle().defautAujourdhui(),
                champ("reference",    "Référence").recherche(),
                champ("idmagasinlib", "Magasin").recherche(),
                champ("etat",         "État").filtreSeul().listeEtat(),
                champ("etatlib",      "État"))
            .menuClicDroit("modifier", BCmdFicheFH.PAGE_MODIF)
            .boutonSaisir(BCmdFicheFH.PAGE_SAISIE);
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
