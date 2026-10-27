package a_tahina.fronthelper.prevision;

import static a_tahina.page.AtahinaChamp.champ;

import a_tahina.fronthelper.GeneraticFH;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.ic.Compartiment;
import a_tahina.page.AtahinaPageInsert;
import a_tahina.prevision.DeclPerteLot;

public class DeclPerteLotFH extends GeneraticFH {

    public static final String PAGE_ACTUEL = PrevisionPoussinFH.PAGE_ACTUEL;

    public static AtahinaPageInsert getPageInsert(HttpServletRequest request,
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
}
