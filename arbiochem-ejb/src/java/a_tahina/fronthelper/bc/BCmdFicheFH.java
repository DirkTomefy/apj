package a_tahina.fronthelper.bc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.bc.BonCommandePoussinLib;
import affichage.PageConsulte;
import user.UserEJB;

public class BCmdFicheFH {

    public static final String PAGE_ACTUEL = "a_tahina/v1/bc/boncommande-fiche.jsp";
    public static final String PAGE_LISTE  = "a_tahina/v1/bc/boncommande-liste.jsp";
    public static final String PAGE_MODIF  = "a_tahina/v1/bc/boncommande-modif.jsp";
    public static final String PAGE_SAISIE = "a_tahina/v1/bc/boncommande-saisie.jsp";
    public static final String CLASSE      = "a_tahina.bc.BonCommandePoussin";

    public static PageConsulte getPageConsulte(HttpServletRequest request,
                                               HttpSession session) throws Exception {

        UserEJB u = (UserEJB) session.getValue("u");

        BonCommandePoussinLib f = new BonCommandePoussinLib();
        f.setNomTable("BONCOMMANDE_POUSSIN_LIB");

        PageConsulte pc = new PageConsulte(f, request, u);
        pc.setTitre("Fiche bon de commande poussin");
        pc.setLien((String) session.getValue("lien"));
        pc.setCanUploadFile(false);

        pc.getChampByName("id").setLibelle("Id");
        pc.getChampByName("daty").setLibelle("Date");
        pc.getChampByName("reference").setLibelle("R&eacute;f&eacute;rence");
        pc.getChampByName("descri").setLibelle("Description");
        pc.getChampByName("idmagasin").setLibelle("Magasin");
        pc.getChampByName("idmagasinlib").setLibelle("Magasin");
        pc.getChampByName("etat").setLibelle("&Eacute;tat");
        pc.getChampByName("etatlib").setLibelle("&Eacute;tat");
        pc.getChampByName("remarque").setLibelle("Remarque");

        pc.getChampByName("idmagasin").setVisible(false);
        pc.getChampByName("etatlib").setVisible(false);

        return pc;
    }
}