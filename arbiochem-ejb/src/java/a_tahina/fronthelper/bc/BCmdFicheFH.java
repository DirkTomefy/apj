package a_tahina.fronthelper.bc;

import static a_tahina.page.AtahinaChamp.champ;

import a_tahina.fronthelper.GeneraticFH;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.bc.BonCommandePoussinLib;
import a_tahina.page.AtahinaPageConsulte;
import a_tahina.bc.BonCommandePoussinFilleLib;
import a_tahina.page.AtahinaPageRecherche;

public class BCmdFicheFH extends GeneraticFH {

    public static final String PAGE_ACTUEL  = "a_tahina/v1/bc/boncommande-fiche.jsp";
    public static final String PAGE_LISTE   = "a_tahina/v1/bc/boncommande-liste.jsp";
    public static final String PAGE_MODIF   = "a_tahina/v1/bc/boncommande-modif.jsp";
    public static final String PAGE_SAISIE  = "a_tahina/v1/bc/boncommande-saisie.jsp";
    public static final String CLASSE       = "a_tahina.bc.BonCommandePoussin";
    public static final String CLASSE_FILLE = "a_tahina.bc.BonCommandePoussinFille";

    public static AtahinaPageConsulte getPageConsulte(HttpServletRequest request,
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
}
