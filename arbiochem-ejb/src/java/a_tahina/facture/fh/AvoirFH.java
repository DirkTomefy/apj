package a_tahina.facture.fh;

import static a_tahina.core.page.AtahinaChamp.champ;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.calendrier.Calendrier;
import a_tahina.core.GeneraticFH;
import a_tahina.core.page.AtahinaPageInsert;
import a_tahina.facture.orm.AthAvoir;
import a_tahina.facture.orm.AthAvoirFille;
import a_tahina.facture.orm.AthAvoirLib;
import a_tahina.facture.orm.AthFactureLib;
import a_tahina.facture.orm.AthFactureFilleLib;
import a_tahina.core.page.AtahinaPageConsulte;
import a_tahina.core.page.AtahinaPageInsertMultiple;
import a_tahina.core.page.AtahinaPageRecherche;
import a_tahina.core.page.AtahinaUtil;
import bean.CGenUtil;
import a_tahina.facture.orm.AthAvoirFilleLib;
import a_tahina.facture.orm.AthAvoirUsage;

public class AvoirFH extends GeneraticFH {

    public static final String DOSSIER      = "a_tahina/v1/facture/avoir/";
    public static final String PAGE_LISTE   = DOSSIER + "avoir-liste.jsp";
    public static final String PAGE_FICHE   = DOSSIER + "avoir-fiche.jsp";
    public static final String PAGE_SAISIE  = DOSSIER + "avoir-saisie.jsp";
    public static final String CLASSE       = "a_tahina.facture.orm.AthAvoir";
    public static final String CLASSE_FILLE = "a_tahina.facture.orm.AthAvoirFille";

    public static String getIdfacture(HttpServletRequest req) { return getParam(req, "idfacture"); }

    @Override
    public AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                        HttpSession session) throws Exception {
        return liste(new AthAvoirLib(), request, session,
                PAGE_LISTE, "Liste des avoirs",
                champ("id",             "Id").recherche().lien(PAGE_FICHE),
                champ("daty",           "Date").intervalle().defautAujourdhui(),
                champ("reference",      "R&eacute;f&eacute;rence").recherche(),
                champ("idfacturelib",   "Facture").recherche(),
                champ("idclientlib",    "Client").recherche(),
                champ("montantttc",     "Montant TTC"),
                champ("montantutilise", "D&eacute;j&agrave; utilis&eacute;"),
                champ("reste",          "Reste"),
                champ("etat",           "&Eacute;tat").filtreSeul().listeEtat(),
                champ("etatlib",        "&Eacute;tat"));
    }

    @Override
    public AtahinaPageConsulte getPageConsulte(HttpServletRequest request,
                                               HttpSession session) throws Exception {
        return consulte(new AthAvoirLib(), request, session, "Fiche avoir",
                champ("id",             "Id"),
                champ("daty",           "Date"),
                champ("reference",      "R&eacute;f&eacute;rence"),
                champ("idfacturelib",   "Facture"),
                champ("idclientlib",    "Client"),
                champ("etatlib",        "&Eacute;tat"),
                champ("motif",          "Motif"),
                champ("montantht",      "Total HT"),
                champ("montanttva",     "TVA"),
                champ("montantttc",     "Total TTC"),
                champ("montantutilise", "D&eacute;j&agrave; d&eacute;duit sur des factures"),
                champ("reste",          "Reste &agrave; appliquer"),
                champ("remarque",       "Remarque"));
    }

    public static String getHtmlChoixFacture(HttpSession session) throws Exception {
        AthFactureLib[] factures = (AthFactureLib[]) CGenUtil.rechercher(new AthFactureLib(), null, null,
                " and etat = " + constante.ConstanteEtat.getEtatValider() + " order by daty desc, id desc");

        StringBuilder sb = new StringBuilder();
        sb.append("<form action=\"").append(AtahinaUtil.esc(AtahinaUtil.getLien(session))).append("\" method=\"get\">")
          .append("<input type=\"hidden\" name=\"but\" value=\"").append(PAGE_SAISIE).append("\" />")
          .append("<div class=\"form-group\"><label>Facture &agrave; rembourser par avoir</label>")
          .append("<select class=\"form-control\" name=\"idfacture\" required>");
        if (factures == null || factures.length == 0) {
            sb.append("<option value=\"\">Aucune facture vis&eacute;e</option>");
        } else {
            for (AthFactureLib f : factures) {
                sb.append("<option value=\"").append(AtahinaUtil.esc(f.getId())).append("\">")
                  .append(AtahinaUtil.esc(f.getId())).append(" - ")
                  .append(AtahinaUtil.esc(f.getIdclientlib())).append(" - ")
                  .append(AtahinaUtil.esc(String.valueOf(f.getDaty()))).append(" - ")
                  .append(AtahinaUtil.esc(String.format("%,.0f", f.getMontantttc()))).append(" TTC")
                  .append("</option>");
            }
        }
        sb.append("</select></div>")
          .append("<button class=\"btn btn-primary\" type=\"submit\">Continuer</button></form>");
        return sb.toString();
    }

    public AtahinaPageInsertMultiple getPageInsertMultiple(HttpServletRequest request,
                                                           HttpSession session) throws Exception {
        String idfacture = getIdfacture(request);
        if (idfacture == null) {
            throw new Exception("Choisissez d'abord une facture vis\u00e9e.");
        }
        Object[] lignes = CGenUtil.rechercher(new AthFactureFilleLib(), null, null,
                AtahinaUtil.egal("idmere", idfacture));
        int nombreLigne = Math.max(1, lignes == null ? 0 : lignes.length);

        return new AtahinaPageInsertMultiple(new AthAvoir(), new AthAvoirFille(), request, session, nombreLigne)
            .champs(
                champ("daty",      "Date").defautAujourdhui(),
                champ("reference", "R&eacute;f&eacute;rence"),
                champ("idfacture", "Facture").defaut(idfacture).lectureSeule(),
                champ("motif",     "Motif"),
                champ("remarque",  "Remarque"))
            .colonnes(
                champ("idfacturefille", "Produit de la facture")
                        .liste(new AthFactureFilleLib(), "produitlib", "id", AtahinaUtil.egal("idmere", idfacture)),
                champ("qte",            "Qt&eacute; retourn&eacute;e").defaut("0"))
            .preparer();
    }

    public static String ongletDetail(HttpServletRequest request, HttpSession session, String idMere) throws Exception {
        return onglet(new AthAvoirFilleLib(), request, session, PAGE_FICHE, idMere, "inc/avoir-detail", "idmere", "Aucune ligne",
                champ("produitlib", "Produit"),
                champ("qte", "Qt&eacute; retourn&eacute;e"),
                champ("pu", "P.U."),
                champ("tva", "TVA %"),
                champ("montantht", "Total HT"),
                champ("montantttc", "Total TTC"));
    }

    public static String ongletUsages(HttpServletRequest request, HttpSession session, String idMere) throws Exception {
        return onglet(new AthAvoirUsage(), request, session, PAGE_FICHE, idMere, "inc/avoir-usages", "idavoir", "Cet avoir n'a pas encore &eacute;t&eacute; utilis&eacute;",
                champ("idfacture", "Facture").lien(FactureFH.PAGE_FICHE, "id"),
                champ("montant", "Montant d&eacute;duit"),
                champ("daty", "Date"));
    }

    @Override
    public AtahinaPageInsert getPageInsert(HttpServletRequest request,
                                           HttpSession session) throws Exception {
        throw nonSupporte("getPageInsert");
    }

    @Override
    public AtahinaPageInsertMultiple getPageInsertMultiple(HttpServletRequest request,
                                                           HttpSession session,
                                                           int nombreLigne) throws Exception {
        return getPageInsertMultiple(request, session);
    }

    @Override
    public Calendrier<?> getCalendrier(HttpServletRequest request,
                                            HttpSession session) throws Exception {
        throw nonSupporte("getCalendrier");
    }
}
