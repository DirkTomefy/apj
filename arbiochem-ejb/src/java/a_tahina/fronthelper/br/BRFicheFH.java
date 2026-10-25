package a_tahina.fronthelper.br;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.br.BonReceptionPoussinLib;
import a_tahina.page.AtahinaPageConsulte;

public class BRFicheFH {

    public static final String PAGE_ACTUEL   = "a_tahina/v1/br/bonreception-fiche.jsp";
    public static final String PAGE_LISTE    = "a_tahina/v1/br/bonreception-liste.jsp";
    public static final String PAGE_MODIF    = "a_tahina/v1/br/bonreception-modif.jsp";
    public static final String PAGE_SAISIE   = "a_tahina/v1/br/bonreception-saisie.jsp";
    public static final String PAGE_BC_FICHE = "a_tahina/v1/bc/boncommande-fiche.jsp";
    public static final String PAGE_IC_SAISIE = "a_tahina/v1/ic/insertioncompartiment-saisie.jsp";
    public static final String CLASSE        = "a_tahina.br.BonReceptionPoussin";

    public static AtahinaPageConsulte getPageConsulte(HttpServletRequest request,
                                                      HttpSession session) throws Exception {
        return new AtahinaPageConsulte(new BonReceptionPoussinLib(), request, session,
                "Fiche bon de reception poussin")
                .libelle("id", "Id")
                .libelle("daty", "Date reception")
                .libelle("reference", "Reference")
                .libelle("idboncommandelib", "Bon de commande")
                .libelle("idbrprecedentlib", "BR precedent")
                .libelle("idlieureceptionlib", "Lieu de reception")
                .libelle("idmagasinlib", "Magasin")
                .libelle("etat", "Etat")
                .libelle("etatlib", "Etat")
                .libelle("qtAvantCumule", "Qte avant cumul")
                .libelle("qtApresCumule", "Qte apres cumul")
                .libelle("maty", "Morts")
                .libelle("very", "Perdus")
                .libelle("remarque", "Remarque")
                .libelle("produitlib", "Produit")
                .libelle("souchelibr", "Souche")
                .libelle("dateEclosion", "Date d'eclosion")
                .cacher("idboncommande", "idbrprecedent", "idlieureception", "idmagasin",
                        "idboncommandefille");
    }
}
