package a_tahina.fronthelper.br;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.bc.BonCommandePoussinFilleLib;
import a_tahina.br.BonReceptionPoussin;
import a_tahina.page.AtahinaPageInsert;
import a_tahina.page.AtahinaUtil;
import affichage.Liste;
import bean.TypeObjet;
import magasin.Magasin;

public class BRInsertFH {

    public static final String PAGE_ACTUEL   = "a_tahina/v1/br/bonreception-saisie.jsp";
    public static final String PAGE_LISTE    = "a_tahina/v1/br/bonreception-liste.jsp";
    public static final String PAGE_BR_FICHE = "a_tahina/v1/br/bonreception-fiche.jsp";
    public static final String PAGE_BC_FICHE = "a_tahina/v1/bc/boncommande-fiche.jsp";

    // -------- Lecture parametres --------
    public static String getParam(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        if (v == null || v.trim().isEmpty() || "null".equalsIgnoreCase(v)) return null;
        return v.trim();
    }
    public static String getIdbc(HttpServletRequest req)  { return getParam(req, "idbc"); }
    public static String getIdbcf(HttpServletRequest req) { return getParam(req, "idbcf"); }

    public static String getButeRetour(HttpServletRequest req) {
        String idbc = getIdbc(req);
        if (idbc != null) return PAGE_BC_FICHE + "&id=" + idbc;
        return PAGE_LISTE;
    }

    // -------- Point d'entree --------
    public static AtahinaPageInsert getPageInsert(HttpServletRequest request,
                                                  HttpSession session) throws Exception {
        String idbc  = getIdbc(request);
        String idbcf = getIdbcf(request);

        AtahinaPageInsert pi = new AtahinaPageInsert(new BonReceptionPoussin(), request, session, null);

        // Ligne BC : libelle « id - produit », limitee au BC d'origine s'il est connu
        Liste ligneBc = (idbc != null)
                ? new Liste("idboncommandefille", new BonCommandePoussinFilleLib(), "idproduitlib", "id",
                            AtahinaUtil.egal("idmere", idbc))
                : new Liste("idboncommandefille", new BonCommandePoussinFilleLib(), "idproduitlib", "id");

        pi.listes(
                new Liste("idlieureception", new TypeObjet("LIEURECEPTION"), "val", "id"),
                new Liste("idmagasin",       new Magasin(), "val", "id"),
                ligneBc);

        pi.libelle("daty", "Date de r&eacute;ception")
          .libelle("reference", "R&eacute;f&eacute;rence")
          .libelle("idboncommandefille", "Ligne BC")
          .libelle("idlieureception", "Lieu de r&eacute;ception")
          .libelle("idmagasin", "Magasin")
          .libelle("maty", "Morts")
          .libelle("very", "Perdus")
          .libelle("remarque", "Remarque")
          .libelle("dateEclosion", "Date d'&eacute;closion");

        // Champs deduits automatiquement par BonReceptionPoussin.controler() -> caches
        pi.cacher("etat", "idboncommande", "idbrprecedent", "qtAvantCumule", "qtApresCumule");

        String aujourdHui = utilitaire.Utilitaire.dateDuJour();
        pi.defaut("daty", aujourdHui)
          .defaut("dateEclosion", aujourdHui)
          .defaut("maty", "0")
          .defaut("very", "0")
          .defaut("qtAvantCumule", "0")
          .defaut("qtApresCumule", "0");

        if (idbcf != null) {
            pi.defaut("idboncommandefille", idbcf).lectureSeule("idboncommandefille");
        }

        return pi.preparer();
    }
}
