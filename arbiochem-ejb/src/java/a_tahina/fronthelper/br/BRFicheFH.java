package a_tahina.fronthelper.br;

import static a_tahina.page.AtahinaChamp.champ;

import a_tahina.fronthelper.GeneraticFH;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.br.BonReceptionPoussinLib;
import a_tahina.page.AtahinaPageConsulte;
import a_tahina.page.AtahinaPageRecherche;

public class BRFicheFH extends GeneraticFH {

    public static final String PAGE_ACTUEL    = "a_tahina/v1/br/bonreception-fiche.jsp";
    public static final String PAGE_LISTE     = "a_tahina/v1/br/bonreception-liste.jsp";
    public static final String PAGE_MODIF     = "a_tahina/v1/br/bonreception-modif.jsp";
    public static final String PAGE_SAISIE    = "a_tahina/v1/br/bonreception-saisie.jsp";
    public static final String PAGE_BC_FICHE  = "a_tahina/v1/bc/boncommande-fiche.jsp";
    public static final String PAGE_IC_SAISIE = "a_tahina/v1/ic/insertioncompartiment-saisie.jsp";
    public static final String CLASSE         = "a_tahina.br.BonReceptionPoussin";

    public static AtahinaPageConsulte getPageConsulte(HttpServletRequest request,
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
}
