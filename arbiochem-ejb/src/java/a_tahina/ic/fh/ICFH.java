package a_tahina.ic.fh;

import static a_tahina.core.page.AtahinaChamp.champ;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.br.fh.BRFH;
import a_tahina.br.orm.BonReceptionPoussin;
import a_tahina.calendrier.Calendrier;
import a_tahina.compartiment.orm.Compartiment;
import a_tahina.core.GeneraticFH;
import a_tahina.core.page.AtahinaChamp;
import a_tahina.core.page.AtahinaPageConsulte;
import a_tahina.core.page.AtahinaPageInsert;
import a_tahina.core.page.AtahinaPageInsertMultiple;
import a_tahina.core.page.AtahinaPageRecherche;
import a_tahina.core.page.AtahinaUtil;
import a_tahina.ic.orm.InsertionCompFilleLib;
import a_tahina.ic.orm.InsertionCompartiment;
import a_tahina.ic.orm.InsertionCompartimentFille;
import a_tahina.ic.orm.InsertionCompartimentLib;
import a_tahina.lot.orm.LotLib;

public class ICFH extends GeneraticFH {

    public static final String DOSSIER       = "a_tahina/v1/ic/";
    public static final String PAGE_FICHE    = DOSSIER + "insertioncompartiment-fiche.jsp";
    public static final String PAGE_LISTE    = DOSSIER + "insertioncompartiment-liste.jsp";
    public static final String PAGE_SAISIE   = DOSSIER + "insertioncompartiment-saisie.jsp";
    public static final String PAGE_BR_FICHE = BRFH.PAGE_FICHE;
    public static final String CLASSE        = "a_tahina.ic.orm.InsertionCompartiment";
    public static final String CLASSE_FILLE  = "a_tahina.ic.orm.InsertionCompartimentFille";

    public static String getIdbr(HttpServletRequest req) { return getParam(req, "idbr"); }

    public static String getButeRetour(HttpServletRequest req) {
        return retour(getIdbr(req), PAGE_BR_FICHE, PAGE_LISTE);
    }

    @Override
    public AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                        HttpSession session) throws Exception {
        return liste(new InsertionCompartimentLib(), request, session,
                PAGE_LISTE, "Liste des insertions compartiment",
                champ("id",                "Id").recherche().lien(PAGE_FICHE),
                champ("daty",              "Date").intervalle().defautAujourdhui(),
                champ("reference",         "R&eacute;f&eacute;rence").recherche(),
                champ("idbonreceptionlib", "Bon de r&eacute;ception").recherche(),
                champ("etat",              "&Eacute;tat").filtreSeul().listeEtat(),
                champ("etatlib",           "&Eacute;tat"))
            .menuClicDroit("modifier", PAGE_FICHE)
            .boutonSaisir(PAGE_SAISIE);
    }

    @Override
    public AtahinaPageConsulte getPageConsulte(HttpServletRequest request,
                                               HttpSession session) throws Exception {
        return consulte(new InsertionCompartimentLib(), request, session, "Fiche insertion compartiment",
                champ("id",                "Id"),
                champ("daty",              "Date"),
                champ("reference",         "R&eacute;f&eacute;rence"),
                champ("idbonreceptionlib", "Bon de r&eacute;ception"),
                champ("etatlib",           "&Eacute;tat"),
                champ("remarque",          "Remarque"));
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

    public static String ongletDetail(HttpServletRequest request, HttpSession session, String idMere) throws Exception {
        return onglet(new InsertionCompFilleLib(), request, session, PAGE_FICHE, idMere, "inc/ic-liste-detail", "idmere", "Aucune ligne trouv&eacute;e",
                champ("compartimentlib", "Compartiment"),
                champ("qte", "Qt&eacute;"),
                champ("pu", "P.U."),
                champ("sexelib", "Sexe"),
                champ("santelib", "Sant&eacute;"));
    }

    public static String ongletLots(HttpServletRequest request, HttpSession session, String idMere) throws Exception {
        return AtahinaPageRecherche.onglet(new LotLib(), request, session, PAGE_FICHE, idMere, "inc/ic-liste-lots",
                champ("id", "Id"),
                champ("produitlib", "Produit"),
                champ("souchelibr", "Souche"),
                champ("qteinit", "Qt&eacute;"),
                champ("pu", "P.U."),
                champ("qteActuelle", "Qt&eacute; actuelle"),
                champ("sexelib", "Sexe"),
                champ("santelib", "Sant&eacute;"),
                champ("compartimentlib", "Compartiment"))
            .filtre(AtahinaUtil.dansSelect("idinsertcompfille", "INSERTION_COMPARTIMENT_FILLE", "id", "idmere", idMere)).parPage(20)
            .construire()
            .getHtmlOnglet("Aucun lot cr&eacute;&eacute;");
    }

    @Override
    public AtahinaPageInsert getPageInsert(HttpServletRequest request,
                                           HttpSession session) throws Exception {
        throw nonSupporte("getPageInsert");
    }

    @Override
    public Calendrier<?> getCalendrier(HttpServletRequest request,
                                            HttpSession session) throws Exception {
        throw nonSupporte("getCalendrier");
    }
}
