package a_tahina.br.fh;

import static a_tahina.core.page.AtahinaChampHelper.champ;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.bc.fh.BCmdFH;
import a_tahina.bc.orm.BonCommandePoussinFilleLib;
import a_tahina.br.orm.BonReceptionPoussin;
import a_tahina.br.orm.BonReceptionPoussinLib;
import a_tahina.calendrier.Calendrier;
import a_tahina.core.GeneraticFH;
import a_tahina.core.page.AtahinaChampHelper;
import a_tahina.core.page.AtahinaPageConsulte;
import a_tahina.core.page.AtahinaPageInsert;
import a_tahina.core.page.AtahinaPageInsertMultiple;
import a_tahina.core.page.AtahinaPageRecherche;
import a_tahina.core.page.AtahinaUtil;
import a_tahina.ic.fh.ICFH;
import magasin.Magasin;

public class BRFH extends GeneraticFH {

    public static final String DOSSIER        = "a_tahina/v1/br/";
    public static final String PAGE_FICHE     = DOSSIER + "bonreception-fiche.jsp";
    public static final String PAGE_LISTE     = DOSSIER + "bonreception-liste.jsp";
    public static final String PAGE_MODIF     = DOSSIER + "bonreception-modif.jsp";
    public static final String PAGE_SAISIE    = DOSSIER + "bonreception-saisie.jsp";
    public static final String PAGE_BC_FICHE  = BCmdFH.PAGE_FICHE;
    public static final String PAGE_IC_SAISIE = ICFH.PAGE_SAISIE;
    public static final String CLASSE         = "a_tahina.br.orm.BonReceptionPoussin";

    public static String getIdbc(HttpServletRequest req)  { return getParam(req, "idbc"); }
    public static String getIdbcf(HttpServletRequest req) { return getParam(req, "idbcf"); }

    public static String getButeRetour(HttpServletRequest req) {
        return retour(getIdbc(req), PAGE_BC_FICHE, PAGE_LISTE);
    }

    @Override
    public AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                        HttpSession session) throws Exception {
        return liste(new BonReceptionPoussinLib(), request, session,
                PAGE_LISTE, "Liste des bons de r&eacute;ception poussin",
                champ("id",                 "Id").recherche().lien(PAGE_FICHE),
                champ("daty",               "Date r&eacute;ception").intervalle().defautAujourdhui(),
                champ("reference",          "R&eacute;f&eacute;rence").recherche(),
                champ("idboncommandelib",   "Bon de commande").recherche(),
                champ("idlieureceptionlib", "Lieu").recherche(),
                champ("etat",               "&Eacute;tat").filtreSeul().listeEtat(),
                champ("etatlib",            "&Eacute;tat"))
            .menuClicDroit("modifier", PAGE_FICHE)
            .boutonSaisir(PAGE_SAISIE);
    }

    @Override
    public AtahinaPageConsulte getPageConsulte(HttpServletRequest request,
                                               HttpSession session) throws Exception {
        return consulte(new BonReceptionPoussinLib(), request, session, "Fiche bon de reception poussin",
                champ("id",                 "Id"),
                champ("daty",               "Date reception"),
                champ("reference",          "Reference"),
                champ("idboncommandelib",   "Bon de commande"),
                champ("produitlib",         "Produit"),
                champ("souchelibr",         "Souche"),
                champ("idbrprecedentlib",   "BR precedent"),
                champ("idlieureceptionlib", "Lieu de reception"),
                champ("idmagasinlib",       "Magasin"),
                champ("dateEclosion",       "Date d'eclosion"),
                champ("qtAvantCumule",      "Qte avant cumul"),
                champ("maty",               "Morts"),
                champ("very",               "Perdus"),
                champ("qtApresCumule",      "Qte apres cumul"),
                champ("etatlib",            "Etat"),
                champ("remarque",           "Remarque"));
    }

    @Override
    public AtahinaPageInsert getPageInsert(HttpServletRequest request,
                                           HttpSession session) throws Exception {
        String idbc  = getIdbc(request);
        String idbcf = getIdbcf(request);

        AtahinaChampHelper ligneBc = champ("idboncommandefille", "Ligne BC")
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

    public static String ongletDetail(HttpServletRequest request, HttpSession session, String idMere) throws Exception {
        return onglet(new BonReceptionPoussinLib(), request, session, PAGE_FICHE, idMere, "inc/br-liste-detail", "id", "Aucune donn&eacute;e trouv&eacute;e",
                champ("id", "Id BR"),
                champ("idboncommandelib", "Bon de commande"),
                champ("idlieureceptionlib", "Lieu"),
                champ("qtApresCumule", "Qt&eacute; re&ccedil;ue"),
                champ("maty", "Morts"),
                champ("very", "Perdus"));
    }

    @Override
    public AtahinaPageInsertMultiple getPageInsertMultiple(HttpServletRequest request,
                                                           HttpSession session,
                                                           int nombreLigne) throws Exception {
        throw nonSupporte("getPageInsertMultiple");
    }

    @Override
    public Calendrier<?> getCalendrier(HttpServletRequest request,
                                            HttpSession session) throws Exception {
        throw nonSupporte("getCalendrier");
    }
}
