package a_tahina.fronthelper.br;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.br.BonReceptionPoussinLib;
import affichage.Champ;
import affichage.PageConsulte;
import user.UserEJB;

public class BRFicheFH {

    public static final String PAGE_ACTUEL   = "a_tahina/v1/br/bonreception-fiche.jsp";
    public static final String PAGE_LISTE    = "a_tahina/v1/br/bonreception-liste.jsp";
    public static final String PAGE_MODIF    = "a_tahina/v1/br/bonreception-modif.jsp";
    public static final String PAGE_SAISIE   = "a_tahina/v1/br/bonreception-saisie.jsp";
    public static final String PAGE_BC_FICHE = "a_tahina/v1/bc/boncommande-fiche.jsp";
    public static final String CLASSE        = "a_tahina.br.BonReceptionPoussin";

    private static void setLabel(PageConsulte pc, String nom, String libelle) {
        Champ c = pc.getChampByName(nom);
        if (c != null) c.setLibelle(libelle);
    }
    private static void hide(PageConsulte pc, String nom) {
        Champ c = pc.getChampByName(nom);
        if (c != null) c.setVisible(false);
    }

    public static PageConsulte getPageConsulte(HttpServletRequest request,
                                               HttpSession session) throws Exception {
        UserEJB u = (UserEJB) session.getValue("u");
        if (u == null) throw new Exception("Session expir\u00e9e. Reconnectez-vous.");

        BonReceptionPoussinLib f = new BonReceptionPoussinLib();
        f.setNomTable("BONRECEPTION_POUSSIN_LIB");

        PageConsulte pc = new PageConsulte(f, request, u);
        pc.setTitre("Fiche bon de r\u00e9ception poussin");
        pc.setLien((String) session.getValue("lien"));
        pc.setCanUploadFile(false);

        setLabel(pc, "id",                 "Id");
        setLabel(pc, "daty",               "Date");
        setLabel(pc, "reference",          "R&eacute;f&eacute;rence");
        setLabel(pc, "idboncommandelib",   "Bon de commande");
        setLabel(pc, "idbrprecedentlib",   "BR pr&eacute;c&eacute;dent");
        setLabel(pc, "idlieureceptionlib", "Lieu de r&eacute;ception");
        setLabel(pc, "idmagasinlib",       "Magasin");
        setLabel(pc, "etat",               "&Eacute;tat");
        setLabel(pc, "etatlib",            "&Eacute;tat");
        setLabel(pc, "qtAvantCumule",      "Qt&eacute; avant cumul");
        setLabel(pc, "qtApresCumule",      "Qt&eacute; apr&egrave;s cumul");
        setLabel(pc, "maty",               "Maty");
        setLabel(pc, "very",               "Very");
        setLabel(pc, "remarque",           "Remarque");
        setLabel(pc, "produitlib",         "Produit");
        setLabel(pc, "souchelibr",         "Souche");

        hide(pc, "idboncommande");
        hide(pc, "idbrprecedent");
        hide(pc, "idlieureception");
        hide(pc, "idmagasin");
        hide(pc, "idboncommandefille");

        return pc;
    }
}