package a_tahina.fronthelper.prevision;

import static a_tahina.page.AtahinaChamp.champ;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.calendrier.CalendrierAbstrait;
import a_tahina.fronthelper.GeneraticFH;
import a_tahina.page.AtahinaPageRecherche;
import a_tahina.page.AtahinaPageConsulte;
import a_tahina.page.AtahinaPageInsertMultiple;
import a_tahina.ic.Compartiment;
import a_tahina.page.AtahinaPageInsert;
import a_tahina.prevision.DeclPerteLot;

public class DeclPerteLotFH extends GeneraticFH {

    public static final String PAGE_ACTUEL = PrevisionPoussinFH.PAGE_ACTUEL;

    @Override
    public AtahinaPageInsert getPageInsert(HttpServletRequest request,
                                           HttpSession session) throws Exception {
        return new AtahinaPageInsert(new DeclPerteLot(), request, session, "Declarer une perte")
            .champs(
                champ("idcompartiment", "Compartiment").liste(new Compartiment(), "nom", "id"),
                champ("sexe",           "Sexe").typeObjet("SEXE"),
                champ("qteMort",        "Morts").defaut("0"),
                champ("qteVery",        "Perdus").defaut("0"),
                champ("daty",           "Date").defautAujourdhui())
            .preparer();
    }

    @Override
    public AtahinaPageInsertMultiple getPageInsertMultiple(HttpServletRequest request,
                                                           HttpSession session,
                                                           int nombreLigne) throws Exception {
        throw nonSupporte("getPageInsertMultiple");
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
