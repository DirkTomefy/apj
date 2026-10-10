package a_tahina.fronthelper.ic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.ic.InsertionCompartimentLib;
import affichage.Champ;
import affichage.PageConsulte;
import user.UserEJB;

public class ICFicheFH {

    public static final String PAGE_ACTUEL   = "a_tahina/v1/ic/insertioncompartiment-fiche.jsp";
    public static final String PAGE_LISTE    = "a_tahina/v1/ic/insertioncompartiment-liste.jsp";
    public static final String PAGE_BR_FICHE = "a_tahina/v1/br/bonreception-fiche.jsp";
    public static final String CLASSE        = "a_tahina.ic.InsertionCompartiment";

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

        InsertionCompartimentLib f = new InsertionCompartimentLib();
        f.setNomTable("INSERTION_COMPARTIMENT_LIB");

        PageConsulte pc = new PageConsulte(f, request, u);
        pc.setTitre("Fiche insertion compartiment");
        pc.setLien((String) session.getValue("lien"));
        pc.setCanUploadFile(false);

        setLabel(pc, "id",                 "Id");
        setLabel(pc, "daty",               "Date");
        setLabel(pc, "reference",          "R&eacute;f&eacute;rence");
        setLabel(pc, "idbonreceptionlib",  "Bon de r&eacute;ception");
        setLabel(pc, "etat",               "&Eacute;tat");
        setLabel(pc, "etatlib",            "&Eacute;tat");
        setLabel(pc, "remarque",           "Remarque");

        hide(pc, "idbonreception");
        hide(pc, "etatlib");

        return pc;
    }
}