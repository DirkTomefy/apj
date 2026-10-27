package a_tahina.fronthelper.br;

import static a_tahina.page.AtahinaChamp.champ;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.calendrier.CalendrierAbstrait;
import a_tahina.fronthelper.GeneraticFH;
import a_tahina.page.AtahinaPageRecherche;
import a_tahina.page.AtahinaPageConsulte;
import a_tahina.page.AtahinaPageInsertMultiple;
import a_tahina.bc.BonCommandePoussinFilleLib;
import a_tahina.br.BonReceptionPoussin;
import a_tahina.fronthelper.bc.BCmdFicheFH;
import a_tahina.page.AtahinaChamp;
import a_tahina.page.AtahinaPageInsert;
import a_tahina.page.AtahinaUtil;
import magasin.Magasin;

public class BRInsertFH extends GeneraticFH {

    public static final String PAGE_ACTUEL   = BRFicheFH.PAGE_SAISIE;
    public static final String PAGE_LISTE    = BRFicheFH.PAGE_LISTE;
    public static final String PAGE_BR_FICHE = BRFicheFH.PAGE_ACTUEL;
    public static final String PAGE_BC_FICHE = BCmdFicheFH.PAGE_ACTUEL;

    public static String getIdbc(HttpServletRequest req)  { return getParam(req, "idbc"); }
    public static String getIdbcf(HttpServletRequest req) { return getParam(req, "idbcf"); }

    public static String getButeRetour(HttpServletRequest req) {
        return retour(getIdbc(req), PAGE_BC_FICHE, PAGE_LISTE);
    }

    @Override
    public AtahinaPageInsert getPageInsert(HttpServletRequest request,
                                           HttpSession session) throws Exception {
        String idbc  = getIdbc(request);
        String idbcf = getIdbcf(request);

        AtahinaChamp ligneBc = champ("idboncommandefille", "Ligne BC")
                .liste(new BonCommandePoussinFilleLib(), "idproduitlib", "id",
                       idbc != null ? AtahinaUtil.egal("idmere", idbc) : null);
        if (idbcf != null) ligneBc.defaut(idbcf).lectureSeule();

        AtahinaPageInsert pi = new AtahinaPageInsert(new BonReceptionPoussin(), request, session, null);
        pi.champs(
            champ("daty",            "Date de r&eacute;ception").defautAujourdhui(),
            champ("reference",       "R&eacute;f&eacute;rence"),
            ligneBc,
            champ("idlieureception", "Lieu de r&eacute;ception").typeObjet("LIEURECEPTION"),
            champ("idmagasin",       "Magasin").liste(new Magasin(), "val", "id"),
            champ("dateEclosion",    "Date d'&eacute;closion").defautAujourdhui(),
            champ("maty",            "Morts").defaut("0"),
            champ("very",            "Perdus").defaut("0"),
            champ("remarque",        "Remarque"));
        return pi.preparer();
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
