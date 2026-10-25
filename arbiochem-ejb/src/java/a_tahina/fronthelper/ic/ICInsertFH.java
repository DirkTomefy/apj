package a_tahina.fronthelper.ic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.br.BonReceptionPoussin;
import a_tahina.ic.Compartiment;
import a_tahina.ic.InsertionCompartiment;
import a_tahina.ic.InsertionCompartimentFille;
import a_tahina.page.AtahinaPageInsertMultiple;
import affichage.Liste;
import bean.TypeObjet;

/**
 * Saisie d'une Insertion Compartiment.
 *
 * - Le BC parent et les cumuls sont deduits dans InsertionCompartiment.createObject().
 * - Chaque ligne de la fille genere un LOT a l'insertion.
 */
public class ICInsertFH {

    public static final String PAGE_ACTUEL   = "a_tahina/v1/ic/insertioncompartiment-saisie.jsp";
    public static final String PAGE_LISTE    = "a_tahina/v1/ic/insertioncompartiment-liste.jsp";
    public static final String PAGE_IC_FICHE = "a_tahina/v1/ic/insertioncompartiment-fiche.jsp";
    public static final String PAGE_BR_FICHE = "a_tahina/v1/br/bonreception-fiche.jsp";

    public static final String CLASSE        = "a_tahina.ic.InsertionCompartiment";
    public static final String CLASSE_FILLE  = "a_tahina.ic.InsertionCompartimentFille";

    public static String getParam(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        if (v == null || v.trim().isEmpty() || "null".equalsIgnoreCase(v)) return null;
        return v.trim();
    }

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

        AtahinaPageInsertMultiple pi = new AtahinaPageInsertMultiple(
                new InsertionCompartiment(), new InsertionCompartimentFille(), request, session, nbLignes);

        // 1. Listes (avant libelles/defauts : changerEnChamp remplace le champ)
        if (idbr == null) {
            pi.listes(new Liste("idbonreception", new BonReceptionPoussin(), "reference", "id"));
        }
        pi.listesFille(
                new Liste("idcompartiment", new Compartiment(), "nom", "id"),
                new Liste("sexe",           new TypeObjet("SEXE"), "val", "id"),
                new Liste("idsante",        new TypeObjet("SANTEPOUSSIN"), "val", "id"));

        // 2. Libelles
        pi.libelle("daty", "Date")
          .libelle("reference", "R&eacute;f&eacute;rence")
          .libelle("idbonreception", "Bon de r&eacute;ception")
          .libelle("remarque", "Remarque")
          .cacher("etat")
          .defaut("daty", utilitaire.Utilitaire.dateDuJour());

        pi.libelleFille("idcompartiment", "Compartiment")
          .libelleFille("qte", "Qt&eacute;")
          .libelleFille("sexe", "Sexe")
          .libelleFille("idsante", "Sant&eacute;")
          // colonnes techniques : pu et valeurCompartiment sont calcules a l'insertion
          .cacherFille("idmere", "id", "pu", "valeurcompartiment");

        // 3. Contexte BR (fige si on vient d'une fiche BR)
        if (idbr != null) {
            pi.defaut("idbonreception", idbr).lectureSeule("idbonreception");
        }

        // 4. Options des listes puis valeurs par defaut (anti-"null")
        pi.charger();

        pi.defautFille("qte", "0")
          .defautFille("pu", "0")
          .defautFille("valeurcompartiment", "0")
          .defautFille("idcompartiment", "")
          .defautFille("sexe", "")
          .defautFille("idsante", "");

        return pi.finaliser("idcompartiment", "qte", "pu", "valeurCompartiment", "sexe", "idsante");
    }
}
