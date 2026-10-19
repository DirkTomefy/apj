package a_tahina.fronthelper.prevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.prevision.DeclPerteLot;
import a_tahina.ic.Compartiment;
import affichage.Champ;
import affichage.Liste;
import affichage.PageInsert;
import bean.TypeObjet;

public class DeclPerteLotFH {

    public static final String PAGE_ACTUEL = "a_tahina/v1/prevision/prevision-poussin.jsp";

    private static void setLabel(PageInsert pi, String nom, String libelle) {
        Champ c = pi.getFormu().getChamp(nom);
        if (c != null) c.setLibelle(libelle);
    }

    private static void setDefaut(PageInsert pi, String nom, String valeur) {
        Champ c = pi.getFormu().getChamp(nom);
        if (c != null) c.setDefaut(valeur);
    }

    private static void hide(PageInsert pi, String nom) {
        Champ c = pi.getFormu().getChamp(nom);
        if (c != null) c.setVisible(false);
    }

    private static void prepareLibelle(PageInsert pi) {
        setLabel(pi, "idcompartiment", "Compartiment");
        setLabel(pi, "sexe",           "Sexe");
        setLabel(pi, "qteMort",        "Morts");
        setLabel(pi, "qteVery",        "Perdus");
        setLabel(pi, "daty",           "Date");

        // Champ technique masque
        hide(pi, "id");
    }

    private static void prepareDefaultValues(PageInsert pi) {
        setDefaut(pi, "qteMort", "0");
        setDefaut(pi, "qteVery", "0");
        setDefaut(pi, "daty",    utilitaire.Utilitaire.dateDuJour());
    }


    private static void changeChampIntoListe(PageInsert pi) throws Exception {

        Liste[] listes = new Liste[2];

        listes[0] = new Liste("idcompartiment",
                new Compartiment(), "nom", "id");

        listes[1] = new Liste("sexe",
                new TypeObjet("SEXE"), "val", "id");

        pi.getFormu().changerEnChamp(listes);
    }

    public static PageInsert getPageInsert(HttpServletRequest request,
                                           HttpSession session) throws Exception {

        user.UserEJB u = (user.UserEJB) session.getValue("u");
        if (u == null) throw new Exception("Session expiree. Reconnectez-vous.");

        DeclPerteLot dpl = new DeclPerteLot();
        PageInsert pi = new PageInsert(dpl, request, u);
        pi.setLien((String) session.getValue("lien"));
        pi.setTitre("Declarer une perte");

        prepareLibelle(pi);
        prepareDefaultValues(pi);
        changeChampIntoListe(pi);

        pi.preparerDataFormu();
        pi.getFormu().makeHtmlInsertTabIndex();

        return pi;
    }
}