package a_tahina.fronthelper;

import a_tahina.poussin.BonCommandePoussinFilleLib;
import a_tahina.poussin.BonReceptionPoussin;
import a_tahina.poussin.BonReceptionPoussinFille;
import a_tahina.poussin.BonReceptionPoussinLib;
import a_tahina.poussin.BonReceptionPoussinFilleLib;
import a_tahina.configuration.TypeReception;
import a_tahina.configuration.Souche;

import affichage.Liste;
import affichage.PageConsulte;
import affichage.PageInsertMultiple;
import affichage.PageRecherche;
import affichage.PageUpdateMultiple;

import bean.TypeObjet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BonReceptionPoussinFH {

    /* =========================================================
     *  LISTE
     * ========================================================= */
    public static PageRecherche getPageRechercheForList(
            HttpServletRequest request,
            HttpSession session) throws Exception {

        BonReceptionPoussinLib f = new BonReceptionPoussinLib();
        f.setNomTable("BONRECEPTION_POUSSIN_LIB");

        String[] listeCrt  = {"id", "daty", "reference",
                              "idboncommandelib", "typereception", "etat"};
        String[] listeInt  = {"daty"};
        String[] libEntete = {"id", "daty", "reference",
                              "idboncommandelib", "typereceptionlib", "etatlib"};

        PageRecherche pr = new PageRecherche(f, request, listeCrt, listeInt, 3,
                                              libEntete, libEntete.length);
        pr.setTitre("Liste des bons de réception poussin");
        pr.setUtilisateur((user.UserEJB) session.getValue("u"));
        pr.setLien((String) session.getValue("lien"));
        pr.setApres("a_tahina/poussin/bonreception-poussin-liste.jsp");
        pr.setNpp(50);

        String awhere = "";
        if (request.getParameter("etat") != null
            && !request.getParameter("etat").isEmpty())
            awhere += " and etat = " + request.getParameter("etat");
        pr.setAWhere(awhere);

        pr.getFormu().getChamp("id").setLibelle("Id");
        pr.getFormu().getChamp("daty1").setLibelle("Date min");
        pr.getFormu().getChamp("daty2").setLibelle("Date max");
        pr.getFormu().getChamp("reference").setLibelle("Référence");
        pr.getFormu().getChamp("idboncommandelib").setLibelle("BC source");

        pr.getFormu().getChamp("daty1").setDefaut(utilitaire.Utilitaire.dateDuJour());
        pr.getFormu().getChamp("daty2").setDefaut(utilitaire.Utilitaire.dateDuJour());

        Liste[] listeMere = new Liste[2];
        listeMere[0] = new Liste("typereception", new TypeReception(), "val", "id");
        listeMere[1] = new Liste("etat", new TypeObjet("ETAT"), "desce", "id");
        pr.getFormu().changerEnChamp(listeMere);
        pr.getFormu().getChamp("typereception").setLibelle("Type de réception");
        pr.getFormu().getChamp("etat").setLibelle("État");

        pr.creerObjetPage(libEntete, null);

        String[] lienTableau = { pr.getLien()
            + "?but=a_tahina/poussin/bonreception-poussin-fiche.jsp" };
        String[] colonneLien = { "id" };
        pr.getTableau().setLien(lienTableau);
        pr.getTableau().setColonneLien(colonneLien);
        pr.getTableau().setLibelleAffiche(new String[]{
            "Id", "Date", "Référence", "BC source", "Type réception", "État"
        });

        pr.getTableau().setLienFille(
            "a_tahina/poussin/inc/bonreception-poussin-fille-liste.jsp&id=");

        Map<String,String> lienTab = new HashMap<>();
        lienTab.put("modifier",
            pr.getLien() + "?but=a_tahina/poussin/bonreception-poussin-modif.jsp");
        lienTab.put("fiche",
            pr.getLien() + "?but=a_tahina/poussin/bonreception-poussin-fiche.jsp");
        pr.getTableau().setLienClicDroite(lienTab);

        pr.getFormu().setAnotherButton(
            "<a class='btn btn-primary pull-right btn-small' " +
            "href='" + pr.getLien()
            + "?but=a_tahina/poussin/bonreception-poussin-saisie.jsp'>" +
            "<i class='material-symbols-rounded'>add</i> Saisir</a>");

        return pr;
    }

    /* =========================================================
     *  FICHE
     * ========================================================= */
    public static PageConsulte getPageConsulte(
            HttpServletRequest request,
            HttpSession session) throws Exception {

        BonReceptionPoussinLib f = new BonReceptionPoussinLib();
        f.setNomTable("BONRECEPTION_POUSSIN_LIB");

        PageConsulte pc = new PageConsulte(f, request,
                            (user.UserEJB) session.getValue("u"));
        pc.setTitre("Fiche d'un bon de réception poussin");
        pc.getBase();

        pc.getChampByName("id").setLibelle("Id");
        pc.getChampByName("daty").setLibelle("Date");
        pc.getChampByName("reference").setLibelle("Référence");
        pc.getChampByName("idboncommandelib").setLibelle("BC source");
        pc.getChampByName("idbrprecedentlib").setLibelle("BR précédent");
        pc.getChampByName("typereceptionlib").setLibelle("Type de réception");
        pc.getChampByName("idmagasinlib").setLibelle("Magasin");
        pc.getChampByName("etatlib").setLibelle("État");
        pc.getChampByName("remarque").setLibelle("Remarque");

        pc.getChampByName("idboncommande").setVisible(false);
        pc.getChampByName("idbrprecedent").setVisible(false);
        pc.getChampByName("typereception").setVisible(false);
        pc.getChampByName("idmagasin").setVisible(false);
        pc.getChampByName("etat").setVisible(false);

        return pc;
    }

    /* =========================================================
     *  ONGLET DÉTAILS (filles) DANS LA FICHE
     * ========================================================= */
    public static PageRecherche getPageRechercheForFille(
            HttpServletRequest request,
            HttpSession session,
            String idMere) throws Exception {

        BonReceptionPoussinFilleLib f = new BonReceptionPoussinFilleLib();
        f.setNomTable("BONRECEPTION_POUSSIN_FILLE_LIB");

        String[] listeCrt  = {"produitlib", "souchelibr"};
        String[] listeInt  = {};
        String[] libEntete = {"id", "produitlib", "souchelibr",
                              "qte", "pu", "maty", "very", "montant"};

        PageRecherche pr = new PageRecherche(f, request, listeCrt, listeInt, 3,
                                              libEntete, libEntete.length);
        pr.setTitre("");
        pr.setUtilisateur((user.UserEJB) session.getValue("u"));
        pr.setLien((String) session.getValue("lien"));
        pr.setApres("a_tahina/poussin/bonreception-poussin-fiche.jsp"
                    + "&id=" + idMere
                    + "&tab=inc/bonreception-poussin-fille-liste");
        pr.setNpp(200);

        pr.setAWhere(" and idmere = '" + idMere + "'");

        pr.getFormu().getChamp("produitlib").setVisible(false);
        pr.getFormu().getChamp("souchelibr").setVisible(false);

        pr.creerObjetPage(libEntete, null);

        pr.getTableau().setLibelleAffiche(new String[]{
            "Id", "Produit", "Souche", "Qté", "P.U.", "Maty", "Very", "Montant"
        });

        return pr;
    }

    /* =========================================================
     *  SAISIE (maître + filles)
     * ========================================================= */
    public static PageInsertMultiple getPageInsertMultiple(
            HttpServletRequest request,
            HttpSession session,
            int nombreLigne) throws Exception {

        BonReceptionPoussin      mere  = new BonReceptionPoussin();
        BonReceptionPoussinFille fille = new BonReceptionPoussinFille();
        fille.setNomTable("BONRECEPTION_POUSSIN_FILLE");

        PageInsertMultiple pi = new PageInsertMultiple(mere, fille, request,
                                                        nombreLigne,
                                                        (user.UserEJB) session.getValue("u"));
        pi.setLien((String) session.getValue("lien"));

        configurerEntete(pi);
        configurerListesMere(pi);

        // --- Déterminer la source de pré-remplissage des filles
        List<Object[]> fillesAPreRemplir = new ArrayList<>();

        String idBC = request.getParameter("idboncommande");
        if (estNonVide(idBC)) {
            pi.getFormu().getChamp("idboncommande").setDefaut(idBC);
            pi.getFormu().getChamp("idboncommande").setVisible(false);
            fillesAPreRemplir.addAll(chargerFillesDuBC(idBC));
        }

        String idBRPrec = request.getParameter("idbrprecedent");
        if (estNonVide(idBRPrec)) {
            pi.getFormu().getChamp("idbrprecedent").setDefaut(idBRPrec);
            pi.getFormu().getChamp("idbrprecedent").setVisible(false);
            masquerBCSourceDepuisBR(pi, idBRPrec);
            fillesAPreRemplir.addAll(chargerFillesDuBR(idBRPrec));
        }

        configurerListesFille(pi);
        pi.preparerDataFormu();
        configurerLibellesFille(pi);

        // Masquer les colonnes techniques
        pi.getFormufle().getChampMulitple("id").setVisible(false);
        pi.getFormufle().getChampMulitple("idmere").setVisible(false);

        // ⚡ Cacher qte (calculé côté serveur)
        pi.getFormufle().getChampMulitple("qte").setVisible(false);

        // Défauts + pré-remplissage
        appliquerDefauts(pi, fillesAPreRemplir, nombreLigne);

        pi.getFormufle().setColOrdre(new String[]{
            "idproduit", "idsouche", "qte", "pu", "maty", "very"
        });

        pi.getFormu().makeHtmlInsertTabIndex();
        pi.getFormufle().makeHtmlInsertTableauIndex();

        return pi;
    }

    /* =========================================================
     *  HELPERS PRIVÉS
     * ========================================================= */

    private static void configurerEntete(PageInsertMultiple pi) {
        pi.getFormu().getChamp("daty").setLibelle("Date");
        pi.getFormu().getChamp("reference").setLibelle("Référence");
        pi.getFormu().getChamp("idboncommande").setLibelle("BC source");
        pi.getFormu().getChamp("idbrprecedent").setLibelle("BR précédent");
        pi.getFormu().getChamp("typereception").setLibelle("Type de réception");
        pi.getFormu().getChamp("idmagasin").setLibelle("Magasin");
        pi.getFormu().getChamp("remarque").setLibelle("Remarque");
        pi.getFormu().getChamp("etat").setVisible(false);
        pi.getFormu().getChamp("daty").setDefaut(utilitaire.Utilitaire.dateDuJour());
    }

    private static void configurerListesMere(PageInsertMultiple pi) throws Exception {
        Liste[] listeMere = new Liste[4];
        listeMere[0] = new Liste("idboncommande",
                                  new BonReceptionPoussinLib(), "reference", "id");
        listeMere[1] = new Liste("idbrprecedent",
                                  new BonReceptionPoussinLib(), "reference", "id");
        listeMere[2] = new Liste("typereception", new TypeReception(), "val", "id");
        listeMere[3] = new Liste("idmagasin", new magasin.Magasin(), "val", "id");
        pi.getFormu().changerEnChamp(listeMere);
    }

    private static void configurerListesFille(PageInsertMultiple pi) throws Exception {
        Liste[] listeFille = new Liste[2];
        listeFille[0] = new Liste("idproduit", new produits.Ingredients(),
                                   "libelle", "id");
        listeFille[1] = new Liste("idsouche", new Souche(), "val", "id");
        pi.getFormufle().changerEnChamp(listeFille);
    }

    private static void configurerLibellesFille(PageInsertMultiple pi) {
        pi.getFormufle().getChamp("idproduit_0").setLibelle("Produit");
        pi.getFormufle().getChamp("idsouche_0").setLibelle("Souche");
        pi.getFormufle().getChamp("qte_0").setLibelle("Qté");
        pi.getFormufle().getChamp("pu_0").setLibelle("P.U.");
        pi.getFormufle().getChamp("maty_0").setLibelle("Morts");
        pi.getFormufle().getChamp("very_0").setLibelle("Restants");
    }

    private static boolean estNonVide(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static void masquerBCSourceDepuisBR(PageInsertMultiple pi, String idBRPrec) {
        try {
            BonReceptionPoussinLib brPrec = (BonReceptionPoussinLib)
                new BonReceptionPoussinLib().getById(idBRPrec,
                    "BONRECEPTION_POUSSIN_LIB", null);
            if (brPrec != null && brPrec.getIdboncommande() != null) {
                pi.getFormu().getChamp("idboncommande")
                    .setDefaut(brPrec.getIdboncommande());
                pi.getFormu().getChamp("idboncommande").setVisible(false);
            }
        } catch (Exception ex) { /* ignore */ }
    }

    /**
     * Charge les filles d'un BC :
     * qte = qte du BC, maty = 0, very = qte (tous vivants au départ).
     * Format : [idproduit, idsouche, qte, pu, maty, very]
     */
    private static List<Object[]> chargerFillesDuBC(String idBC) {
        List<Object[]> result = new ArrayList<>();
        try {
            BonCommandePoussinFilleLib crit = new BonCommandePoussinFilleLib();
            crit.setNomTable("BONCOMMANDE_POUSSIN_FILLE_LIB");

            BonCommandePoussinFilleLib[] filles = (BonCommandePoussinFilleLib[])
                bean.CGenUtil.rechercher(crit, null, null,
                    " and idmere = '" + idBC + "'");

            if (filles != null) {
                for (BonCommandePoussinFilleLib f : filles) {
                    double qte = f.getQte();
                    result.add(new Object[]{
                        f.getIdproduit(),
                        f.getIdsouche(),
                        String.valueOf(qte),         // qte = celle commandée
                        String.valueOf(f.getPu()),
                        "0",                          // maty : aucune mort
                        String.valueOf(qte)           // very = qte
                    });
                }
            }
        } catch (Exception ex) { /* ignore */ }
        return result;
    }

    /**
     * Charge les filles d'un BR précédent :
     * qte = very (survivants) du BR précédent, maty = 0, very = qte.
     * Format : [idproduit, idsouche, qte, pu, maty, very]
     */
    private static List<Object[]> chargerFillesDuBR(String idBRPrec) {
        List<Object[]> result = new ArrayList<>();
        try {
            BonReceptionPoussinFilleLib crit = new BonReceptionPoussinFilleLib();
            crit.setNomTable("BONRECEPTION_POUSSIN_FILLE_LIB");

            BonReceptionPoussinFilleLib[] filles = (BonReceptionPoussinFilleLib[])
                bean.CGenUtil.rechercher(crit, null, null,
                    " and idmere = '" + idBRPrec + "'");

            if (filles != null) {
                for (BonReceptionPoussinFilleLib f : filles) {
                    double survivants = f.getVery();
                    result.add(new Object[]{
                        f.getIdproduit(),
                        f.getIdsouche(),
                        String.valueOf(survivants),    // qte = very préc
                        String.valueOf(f.getPu()),
                        "0",                            // maty : nouveau cycle
                        String.valueOf(survivants)      // very = qte
                    });
                }
            }
        } catch (Exception ex) { /* ignore */ }
        return result;
    }

    /**
     * Applique les valeurs par défaut, rend very readonly,
     * et attache le JS de recalcul (very = qte - maty).
     */
    private static void appliquerDefauts(PageInsertMultiple pi,
                                          List<Object[]> filles,
                                          int nombreLigne) {
        for (int i = 0; i < nombreLigne; i++) {
            // very : lecture seule (calculé)
            pi.getFormufle().getChamp("very_"+i).setAutre("readonly");
            // maty : recalcul au changement
            pi.getFormufle().getChamp("maty_"+i)
                .setAutre("onchange='recalcVery("+i+")'");

            // Défauts de base
            pi.getFormufle().getChamp("qte_"+i).setDefaut("0");
            pi.getFormufle().getChamp("pu_"+i).setDefaut("0");
            pi.getFormufle().getChamp("maty_"+i).setDefaut("0");
            pi.getFormufle().getChamp("very_"+i).setDefaut("0");

            // Surcharge si pré-remplissage
            if (i < filles.size()) {
                Object[] f = filles.get(i);
                pi.getFormufle().getChamp("idproduit_"+i).setDefaut((String) f[0]);
                pi.getFormufle().getChamp("idsouche_"+i).setDefaut((String) f[1]);
                pi.getFormufle().getChamp("qte_"+i).setDefaut((String) f[2]);
                pi.getFormufle().getChamp("pu_"+i).setDefaut((String) f[3]);
                pi.getFormufle().getChamp("maty_"+i).setDefaut((String) f[4]);
                pi.getFormufle().getChamp("very_"+i).setDefaut((String) f[5]);
            }
        }
    }

    /* =========================================================
     *  MODIF (maître + filles)
     * ========================================================= */
    public static PageUpdateMultiple getPageUpdateMultiple(
            HttpServletRequest request,
            HttpSession session,
            String id,
            int nbNouvellesLignes) throws Exception {

        BonReceptionPoussin mere = new BonReceptionPoussin();
        mere = (BonReceptionPoussin) mere.getById(id, "BONRECEPTION_POUSSIN", null);

        BonReceptionPoussinFille fille = new BonReceptionPoussinFille();
        fille.setNomTable("BONRECEPTION_POUSSIN_FILLE");
        fille.setIdmere(id);

        BonReceptionPoussinFille[] listef = (BonReceptionPoussinFille[])
            bean.CGenUtil.rechercher(fille, null, null,
                " and idmere = '" + id + "'");

        PageUpdateMultiple pu = new PageUpdateMultiple(
                                    mere, fille, listef, request,
                                    (user.UserEJB) session.getValue("u"),
                                    nbNouvellesLignes);
        pu.setLien((String) session.getValue("lien"));

        // --- En-tête
        pu.getFormu().getChamp("daty").setLibelle("Date");
        pu.getFormu().getChamp("reference").setLibelle("Référence");
        pu.getFormu().getChamp("idboncommande").setLibelle("BC source");
        pu.getFormu().getChamp("idbrprecedent").setLibelle("BR précédent");
        pu.getFormu().getChamp("typereception").setLibelle("Type de réception");
        pu.getFormu().getChamp("idmagasin").setLibelle("Magasin");
        pu.getFormu().getChamp("remarque").setLibelle("Remarque");
        pu.getFormu().getChamp("etat").setVisible(false);

        Liste[] listeMere = new Liste[4];
        listeMere[0] = new Liste("idboncommande", new BonReceptionPoussinLib(),
                                  "reference", "id");
        listeMere[1] = new Liste("idbrprecedent", new BonReceptionPoussinLib(),
                                  "reference", "id");
        listeMere[2] = new Liste("typereception", new TypeReception(), "val", "id");
        listeMere[3] = new Liste("idmagasin", new magasin.Magasin(), "val", "id");
        pu.getFormu().changerEnChamp(listeMere);

        Liste[] listeFille = new Liste[2];
        listeFille[0] = new Liste("idproduit", new produits.Ingredients(),
                                   "libelle", "id");
        listeFille[1] = new Liste("idsouche", new Souche(), "val", "id");
        pu.getFormufle().changerEnChamp(listeFille);

        pu.preparerDataFormu();

        pu.getFormufle().getChamp("idproduit_0").setLibelle("Produit");
        pu.getFormufle().getChamp("idsouche_0").setLibelle("Souche");
        pu.getFormufle().getChamp("qte_0").setLibelle("Qté");
        pu.getFormufle().getChamp("pu_0").setLibelle("P.U.");
        pu.getFormufle().getChamp("maty_0").setLibelle("Morts");
        pu.getFormufle().getChamp("very_0").setLibelle("Restants");

        // very readonly + JS sur maty
        int nbLignesTotal = pu.getNombreLigne();
        for (int i = 0; i < nbLignesTotal; i++) {
            pu.getFormufle().getChamp("very_"+i).setAutre("readonly");
            pu.getFormufle().getChamp("maty_"+i)
                .setAutre("onchange='recalcVery("+i+")'");
        }

        pu.getFormufle().getChampMulitple("idmere").setVisible(false);

        pu.getFormufle().setColOrdre(new String[]{
            "idproduit", "idsouche", "qte", "pu", "maty", "very"
        });

        pu.getFormu().makeHtmlInsertTabIndex();
        pu.getFormufle().makeHtmlInsertTableauIndex();

        return pu;
    }
}