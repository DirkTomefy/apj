package a_tahina.fronthelper.bc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.bc.BonCommandePoussinLib;
import a_tahina.page.AtahinaPageConsulte;

public class BCmdFicheFH {

    public static final String PAGE_ACTUEL = "a_tahina/v1/bc/boncommande-fiche.jsp";
    public static final String PAGE_LISTE  = "a_tahina/v1/bc/boncommande-liste.jsp";
    public static final String PAGE_MODIF  = "a_tahina/v1/bc/boncommande-modif.jsp";
    public static final String PAGE_SAISIE = "a_tahina/v1/bc/boncommande-saisie.jsp";
    public static final String CLASSE      = "a_tahina.bc.BonCommandePoussin";
    public static final String CLASSE_FILLE = "a_tahina.bc.BonCommandePoussinFille";

    public static AtahinaPageConsulte getPageConsulte(HttpServletRequest request,
                                                      HttpSession session) throws Exception {
        return new AtahinaPageConsulte(new BonCommandePoussinLib(), request, session,
                "Fiche bon de commande poussin")
                .libelle("id", "Id")
                .libelle("daty", "Date")
                .libelle("reference", "R&eacute;f&eacute;rence")
                .libelle("descri", "Description")
                .libelle("idmagasinlib", "Magasin")
                .libelle("etat", "&Eacute;tat")
                .libelle("remarque", "Remarque")
                .cacher("idmagasin", "etatlib");
    }
}
