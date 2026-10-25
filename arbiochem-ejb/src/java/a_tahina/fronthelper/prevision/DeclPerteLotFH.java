package a_tahina.fronthelper.prevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.ic.Compartiment;
import a_tahina.page.AtahinaPageInsert;
import a_tahina.prevision.DeclPerteLot;
import affichage.Liste;
import bean.TypeObjet;

public class DeclPerteLotFH {

    public static final String PAGE_ACTUEL = PrevisionPoussinFH.PAGE_ACTUEL;

    public static AtahinaPageInsert getPageInsert(HttpServletRequest request,
                                                  HttpSession session) throws Exception {
        AtahinaPageInsert pi = new AtahinaPageInsert(new DeclPerteLot(), request, session,
                "Declarer une perte");

        pi.listes(
                new Liste("idcompartiment", new Compartiment(), "nom", "id"),
                new Liste("sexe",           new TypeObjet("SEXE"), "val", "id"));

        pi.libelle("idcompartiment", "Compartiment")
          .libelle("sexe", "Sexe")
          .libelle("qteMort", "Morts")
          .libelle("qteVery", "Perdus")
          .libelle("daty", "Date")
          .defaut("qteMort", "0")
          .defaut("qteVery", "0")
          .defaut("daty", utilitaire.Utilitaire.dateDuJour())
          .cacher("id");

        return pi.preparer();
    }
}
