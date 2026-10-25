package a_tahina.fronthelper.facture;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.facture.AthAvoir;
import a_tahina.facture.AthAvoirFille;
import a_tahina.facture.AthAvoirLib;
import a_tahina.facture.AthFactureLib;
import a_tahina.facture.AthFactureFilleLib;
import a_tahina.page.AtahinaPageConsulte;
import a_tahina.page.AtahinaPageInsertMultiple;
import a_tahina.page.AtahinaPageRecherche;
import a_tahina.page.AtahinaUtil;
import affichage.Liste;
import bean.CGenUtil;

public class AvoirFH {

    public static final String DOSSIER      = "a_tahina/v1/facture/avoir/";
    public static final String PAGE_LISTE   = DOSSIER + "avoir-liste.jsp";
    public static final String PAGE_FICHE   = DOSSIER + "avoir-fiche.jsp";
    public static final String PAGE_SAISIE  = DOSSIER + "avoir-saisie.jsp";
    public static final String CLASSE       = "a_tahina.facture.AthAvoir";
    public static final String CLASSE_FILLE = "a_tahina.facture.AthAvoirFille";

    /** Facture d'origine de l'avoir a saisir (parametre idfacture). */
    public static String getIdfacture(HttpServletRequest req) {
        String v = req.getParameter("idfacture");
        if (v == null || v.trim().isEmpty() || "null".equalsIgnoreCase(v)) return null;
        return v.trim();
    }

    // -------- LISTE --------
    public static AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                               HttpSession session) throws Exception {
        String[] listeCrt  = { "id", "daty", "reference", "idfacturelib", "idclientlib", "etat" };
        String[] listeInt  = { "daty" };
        String[] libEntete = { "id", "daty", "reference", "idfacturelib", "idclientlib",
                               "montantttc", "montantutilise", "reste", "etatlib" };

        AtahinaPageRecherche pr = new AtahinaPageRecherche(new AthAvoirLib(), request, session,
                PAGE_LISTE, "Liste des avoirs", listeCrt, listeInt, libEntete);

        pr.libelle("id", "Id")
          .libelle("reference", "R&eacute;f&eacute;rence")
          .libelle("idfacturelib", "Facture")
          .libelle("idclientlib", "Client")
          .intervalleDatyAujourdhui()
          .listeEtat("&Eacute;tat");

        // pas de bouton « Saisir » : un avoir se cree depuis la fiche d'une facture visee
        return pr.construire("Id", "Date", "R&eacute;f&eacute;rence", "Facture", "Client",
                             "Montant TTC", "D&eacute;j&agrave; utilis&eacute;", "Reste", "&Eacute;tat")
                 .lienFiche("id", PAGE_FICHE);
    }

    // -------- FICHE --------
    public static AtahinaPageConsulte getPageConsulte(HttpServletRequest request,
                                                      HttpSession session) throws Exception {
        return new AtahinaPageConsulte(new AthAvoirLib(), request, session, "Fiche avoir")
                .libelle("id", "Id")
                .libelle("daty", "Date")
                .libelle("reference", "R&eacute;f&eacute;rence")
                .libelle("idfacturelib", "Facture")
                .libelle("idclientlib", "Client")
                .libelle("etatlib", "&Eacute;tat")
                .libelle("motif", "Motif")
                .libelle("remarque", "Remarque")
                .libelle("montantht", "Total HT")
                .libelle("montanttva", "TVA")
                .libelle("montantttc", "Total TTC")
                .libelle("montantutilise", "D&eacute;j&agrave; d&eacute;duit sur des factures")
                .libelle("reste", "Reste &agrave; appliquer")
                .cacher("idfacture", "idclient", "etat");
    }

    // -------- SAISIE, etape 1 : choix de la facture visee --------
    /**
     * Formulaire (GET) listant les factures visees ; il rouvre la page de saisie
     * avec le parametre idfacture.
     */
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

    // -------- SAISIE, etape 2 : lignes a retourner --------
    public static AtahinaPageInsertMultiple getPageInsertMultiple(HttpServletRequest request,
                                                                  HttpSession session) throws Exception {
        String idfacture = getIdfacture(request);
        if (idfacture == null) {
            throw new Exception("Ouvrez la création d'un avoir depuis la fiche d'une facture visée.");
        }
        Object[] lignes = CGenUtil.rechercher(new AthFactureFilleLib(), null, null,
                AtahinaUtil.egal("idmere", idfacture));
        int nombreLigne = Math.max(1, lignes == null ? 0 : lignes.length);

        AtahinaPageInsertMultiple pi = new AtahinaPageInsertMultiple(
                new AthAvoir(), new AthAvoirFille(), request, session, nombreLigne);

        // Lignes de la facture d'origine uniquement
        pi.listesFille(new Liste("idfacturefille", new AthFactureFilleLib(), "produitlib", "id",
                AtahinaUtil.egal("idmere", idfacture)));

        pi.libelle("daty", "Date")
          .libelle("reference", "R&eacute;f&eacute;rence")
          .libelle("idfacture", "Facture")
          .libelle("motif", "Motif")
          .libelle("remarque", "Remarque")
          .defaut("daty", utilitaire.Utilitaire.dateDuJour())
          .defaut("idfacture", idfacture)
          .lectureSeule("idfacture")
          .cacher("idclient", "etat");

        pi.libelleFille("idfacturefille", "Produit de la facture")
          .libelleFille("qte", "Qt&eacute; retourn&eacute;e")
          .cacherFille("id", "idmere", "pu", "tva");

        pi.charger();

        pi.defautFille("qte", "0").defautFille("pu", "0").defautFille("tva", "0");

        return pi.finaliser("idfacturefille", "qte", "pu", "tva");
    }
}
