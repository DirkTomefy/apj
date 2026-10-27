package a_tahina.br.fh;

import static a_tahina.core.page.AtahinaChamp.champ;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.calendrier.CalendrierAbstrait;
import a_tahina.core.GeneraticFH;
import a_tahina.core.page.AtahinaPageRecherche;
import a_tahina.core.page.AtahinaPageInsertMultiple;
import a_tahina.core.page.AtahinaPageInsert;
import a_tahina.br.orm.BonReceptionPoussinLib;
import a_tahina.bc.fh.BCmdFicheFH;
import a_tahina.ic.fh.ICInsertFH;
import a_tahina.core.page.AtahinaPageConsulte;

public class BRFicheFH extends GeneraticFH {

    public static final String DOSSIER        = "a_tahina/v1/br/";
    public static final String PAGE_ACTUEL    = DOSSIER + "bonreception-fiche.jsp";
    public static final String PAGE_LISTE     = DOSSIER + "bonreception-liste.jsp";
    public static final String PAGE_MODIF     = DOSSIER + "bonreception-modif.jsp";
    public static final String PAGE_SAISIE    = DOSSIER + "bonreception-saisie.jsp";
    public static final String PAGE_BC_FICHE  = BCmdFicheFH.PAGE_ACTUEL;
    public static final String PAGE_IC_SAISIE = ICInsertFH.PAGE_ACTUEL;
    public static final String CLASSE         = "a_tahina.br.orm.BonReceptionPoussin";

    @Override
    public AtahinaPageConsulte getPageConsulte(HttpServletRequest request,
                                               HttpSession session) throws Exception {
        return consulte(new BonReceptionPoussinLib(), request, session, "Fiche bon de reception poussin",
                champ("id",                 "Id"),
                champ("daty",               "Date reception"),
                champ("reference",          "Reference"),
                champ("idboncommandelib",   "Bon de commande"),
                champ("produitlib",         "Produit"),
                champ("souchelibr",         "Souche"),
                champ("idbrprecedentlib",   "BR precedent"),
                champ("idlieureceptionlib", "Lieu de reception"),
                champ("idmagasinlib",       "Magasin"),
                champ("dateEclosion",       "Date d'eclosion"),
                champ("qtAvantCumule",      "Qte avant cumul"),
                champ("maty",               "Morts"),
                champ("very",               "Perdus"),
                champ("qtApresCumule",      "Qte apres cumul"),
                champ("etatlib",            "Etat"),
                champ("remarque",           "Remarque"));
    }

    public static String ongletDetail(HttpServletRequest request, HttpSession session, String idMere) throws Exception {
        return onglet(new BonReceptionPoussinLib(), request, session, PAGE_ACTUEL, idMere, "inc/br-liste-detail", "id", "Aucune donn&eacute;e trouv&eacute;e",
                champ("id", "Id BR"),
                champ("idboncommandelib", "Bon de commande"),
                champ("idlieureceptionlib", "Lieu"),
                champ("qtApresCumule", "Qt&eacute; re&ccedil;ue"),
                champ("maty", "Morts"),
                champ("very", "Perdus"));
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
