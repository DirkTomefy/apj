package a_tahina.ic.fh;

import static a_tahina.core.page.AtahinaChamp.champ;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.calendrier.CalendrierAbstrait;
import a_tahina.core.GeneraticFH;
import a_tahina.core.page.AtahinaPageRecherche;
import a_tahina.core.page.AtahinaPageConsulte;
import a_tahina.core.page.AtahinaPageInsert;
import a_tahina.br.orm.BonReceptionPoussin;
import a_tahina.br.fh.BRFicheFH;
import a_tahina.compartiment.orm.Compartiment;
import a_tahina.ic.orm.InsertionCompartiment;
import a_tahina.ic.orm.InsertionCompartimentFille;
import a_tahina.core.page.AtahinaChamp;
import a_tahina.core.page.AtahinaPageInsertMultiple;

public class ICInsertFH extends GeneraticFH {

    public static final String PAGE_ACTUEL   = ICFicheFH.PAGE_SAISIE;
    public static final String PAGE_LISTE    = ICFicheFH.PAGE_LISTE;
    public static final String PAGE_IC_FICHE = ICFicheFH.PAGE_ACTUEL;
    public static final String PAGE_BR_FICHE = BRFicheFH.PAGE_ACTUEL;

    public static final String CLASSE        = "a_tahina.ic.orm.InsertionCompartiment";
    public static final String CLASSE_FILLE  = "a_tahina.ic.orm.InsertionCompartimentFille";

    public static String getIdbr(HttpServletRequest req) { return getParam(req, "idbr"); }

    public static String getButeRetour(HttpServletRequest req) {
        return retour(getIdbr(req), PAGE_BR_FICHE, PAGE_LISTE);
    }

    @Override
    public AtahinaPageInsertMultiple getPageInsertMultiple(HttpServletRequest request,
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

    @Override
    public AtahinaPageInsert getPageInsert(HttpServletRequest request,
                                           HttpSession session) throws Exception {
        throw nonSupporte("getPageInsert");
    }

    @Override
    public AtahinaPageConsulte getPageConsulte(HttpServletRequest request,
                                               HttpSession session) throws Exception {
        throw nonSupporte("getPageConsulte");
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
