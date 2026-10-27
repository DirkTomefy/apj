package a_tahina.fronthelper.bc;

import static a_tahina.page.AtahinaChamp.champ;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.bc.BonCommandePoussinLib;
import a_tahina.page.AtahinaPageRecherche;

public class BCmdListeFH {

    public static final String PAGE_ACTUEL = BCmdFicheFH.PAGE_LISTE;

    public static AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                               HttpSession session) throws Exception {
        return new AtahinaPageRecherche(new BonCommandePoussinLib(), request, session,
                PAGE_ACTUEL, "Liste des bons de commande poussin",
                champ("id",           "Id").recherche().lien(BCmdFicheFH.PAGE_ACTUEL),
                champ("daty",         "Date").intervalle().defautAujourdhui(),
                champ("reference",    "Référence").recherche(),
                champ("idmagasinlib", "Magasin").recherche(),
                champ("etat",         "État").filtreSeul().listeEtat(),
                champ("etatlib",      "État"))
            .construire()
            .menuClicDroit("modifier", BCmdFicheFH.PAGE_MODIF)
            .boutonSaisir(BCmdFicheFH.PAGE_SAISIE);
    }
}
