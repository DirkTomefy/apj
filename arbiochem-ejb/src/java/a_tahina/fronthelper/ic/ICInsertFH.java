package a_tahina.fronthelper.ic;

import static a_tahina.page.AtahinaChamp.champ;

import a_tahina.fronthelper.GeneraticFH;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.br.BonReceptionPoussin;
import a_tahina.ic.Compartiment;
import a_tahina.ic.InsertionCompartiment;
import a_tahina.ic.InsertionCompartimentFille;
import a_tahina.page.AtahinaChamp;
import a_tahina.page.AtahinaPageInsertMultiple;

public class ICInsertFH extends GeneraticFH {

    public static final String PAGE_ACTUEL   = "a_tahina/v1/ic/insertioncompartiment-saisie.jsp";
    public static final String PAGE_LISTE    = "a_tahina/v1/ic/insertioncompartiment-liste.jsp";
    public static final String PAGE_IC_FICHE = "a_tahina/v1/ic/insertioncompartiment-fiche.jsp";
    public static final String PAGE_BR_FICHE = "a_tahina/v1/br/bonreception-fiche.jsp";

    public static final String CLASSE        = "a_tahina.ic.InsertionCompartiment";
    public static final String CLASSE_FILLE  = "a_tahina.ic.InsertionCompartimentFille";

    public static String getIdbr(HttpServletRequest req) { return getParam(req, "idbr"); }

    public static String getButeRetour(HttpServletRequest req) {
        String idbr = getIdbr(req);
        if (idbr != null) return PAGE_BR_FICHE + "&id=" + idbr;
        return PAGE_LISTE;
    }

    public static AtahinaPageInsertMultiple getPageInsertMultiple(HttpServletRequest request,
                                                                  HttpSession session,
                                                                  int nbLignes) throws Exception {
        String idbr = getIdbr(request);

        AtahinaChamp br = champ("idbonreception", "Bon de r&eacute;ception");
        if (idbr == null) br.liste(new BonReceptionPoussin(), "reference", "id");
        else              br.defaut(idbr).lectureSeule();

        AtahinaPageInsertMultiple pi = new AtahinaPageInsertMultiple(
                new InsertionCompartiment(), new InsertionCompartimentFille(), request, session, nbLignes);

        pi.champs(
            champ("daty",      "Date").defautAujourdhui(),
            champ("reference", "R&eacute;f&eacute;rence"),
            br,
            champ("remarque",  "Remarque"));

        pi.colonnes(
            champ("idcompartiment", "Compartiment").liste(new Compartiment(), "nom", "id").defaut(""),
            champ("qte",            "Qt&eacute;").defaut("0"),
            champ("sexe",           "Sexe").typeObjet("SEXE").defaut(""),
            champ("idsante",        "Sant&eacute;").typeObjet("SANTEPOUSSIN").defaut(""));

        return pi.preparer();
    }
}
