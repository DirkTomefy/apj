package a_tahina.bc.fh;

import static a_tahina.core.page.AtahinaChamp.champ;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.calendrier.CalendrierAbstrait;
import a_tahina.core.GeneraticFH;
import a_tahina.core.page.AtahinaPageRecherche;
import a_tahina.core.page.AtahinaPageInsertMultiple;
import a_tahina.core.page.AtahinaPageInsert;
import a_tahina.bc.orm.BonCommandePoussinLib;
import a_tahina.core.page.AtahinaPageConsulte;
import a_tahina.bc.orm.BonCommandePoussinFilleLib;

public class BCmdFicheFH extends GeneraticFH {

    public static final String DOSSIER      = "a_tahina/v1/bc/";
    public static final String PAGE_ACTUEL  = DOSSIER + "boncommande-fiche.jsp";
    public static final String PAGE_LISTE   = DOSSIER + "boncommande-liste.jsp";
    public static final String PAGE_MODIF   = DOSSIER + "boncommande-modif.jsp";
    public static final String PAGE_SAISIE  = DOSSIER + "boncommande-saisie.jsp";
    public static final String CLASSE       = "a_tahina.bc.orm.BonCommandePoussin";
    public static final String CLASSE_FILLE = "a_tahina.bc.orm.BonCommandePoussinFille";

    @Override
    public AtahinaPageConsulte getPageConsulte(HttpServletRequest request,
                                               HttpSession session) throws Exception {
        return consulte(new BonCommandePoussinLib(), request, session, "Fiche bon de commande poussin",
                champ("id",           "Id"),
                champ("daty",         "Date"),
                champ("reference",    "R&eacute;f&eacute;rence"),
                champ("descri",       "Description"),
                champ("idmagasinlib", "Magasin"),
                champ("etatlib",      "&Eacute;tat"),
                champ("remarque",     "Remarque"));
    }

    public static String ongletDetail(HttpServletRequest request, HttpSession session, String idMere) throws Exception {
        return onglet(new BonCommandePoussinFilleLib(), request, session, PAGE_ACTUEL, idMere, "inc/bc-liste-detail", "idmere", "Aucune ligne trouv&eacute;e",
                champ("idproduitlib", "Produit"),
                champ("souchelibr", "Souche"),
                champ("qte", "Qt&eacute;"),
                champ("pu", "P.U."),
                champ("montant", "Montant"),
                champ("fournisseurlib", "Fournisseur"));
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
    public CalendrierAbstrait getCalendrier(HttpServletRequest request,
                                            HttpSession session) throws Exception {
        throw nonSupporte("getCalendrier");
    }

    @Override
    public AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                        HttpSession session) throws Exception {
        throw nonSupporte("getPageRechercheForList");
    }
}
