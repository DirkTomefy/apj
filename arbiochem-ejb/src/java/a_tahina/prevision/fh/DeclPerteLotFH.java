package a_tahina.prevision.fh;

import static a_tahina.core.page.AtahinaChampHelper.champ;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.core.GeneraticFH;
import a_tahina.compartiment.orm.Compartiment;
import a_tahina.core.page.AtahinaPageInsert;
import a_tahina.prevision.orm.DeclPerteLot;

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
}
