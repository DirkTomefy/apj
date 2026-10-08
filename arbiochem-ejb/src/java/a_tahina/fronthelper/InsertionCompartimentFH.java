package a_tahina.fronthelper;

import a_tahina.poussin.BonReceptionPoussinLib;
import a_tahina.poussin.InsertionCompartiment;
import a_tahina.poussin.InsertionCompartimentFille;
import a_tahina.poussin.InsertionCompartimentLib;
import a_tahina.poussin.InsertionCompartimentFilleLib;
import a_tahina.configuration.Compartiment;
import a_tahina.configuration.SantePoussin;

import affichage.Liste;
import affichage.PageConsulte;
import affichage.PageInsertMultiple;
import affichage.PageRecherche;
import affichage.PageUpdateMultiple;

import bean.TypeObjet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

public class InsertionCompartimentFH {

    /* =========================================================
     *  LISTE
     * ========================================================= */
    public static PageRecherche getPageRechercheForList(
            HttpServletRequest request,
            HttpSession session) throws Exception {

        InsertionCompartimentLib f = new InsertionCompartimentLib();
        f.setNomTable("INSERTION_COMPARTIMENT_LIB");

        String[] listeCrt  = {"id", "daty", "reference",
                              "idbonreceptionlib", "etat"};
        String[] listeInt  = {"daty"};
        String[] libEntete = {"id", "daty", "reference",
                              "idbonreceptionlib", "etatlib"};

        PageRecherche pr = new PageRecherche(f, request, listeCrt, listeInt, 3,
                                              libEntete, libEntete.length);
        pr.setTitre("Liste des insertions en compartiment");
        pr.setUtilisateur((user.UserEJB) session.getValue("u"));
        pr.setLien((String) session.getValue("lien"));
        pr.setApres("a_tahina/poussin/insertion-compartiment-liste.jsp");
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
        pr.getFormu().getChamp("idbonreceptionlib").setLibelle("BR source");

        pr.getFormu().getChamp("daty1").setDefaut(utilitaire.Utilitaire.dateDuJour());
        pr.getFormu().getChamp("daty2").setDefaut(utilitaire.Utilitaire.dateDuJour());

        Liste[] listeMere = new Liste[1];
        listeMere[0] = new Liste("etat", new TypeObjet("ETAT"), "desce", "id");
        pr.getFormu().changerEnChamp(listeMere);
        pr.getFormu().getChamp("etat").setLibelle("État");

        pr.creerObjetPage(libEntete, null);

        String[] lienTableau = { pr.getLien()
            + "?but=a_tahina/poussin/insertion-compartiment-fiche.jsp" };
        String[] colonneLien = { "id" };
        pr.getTableau().setLien(lienTableau);
        pr.getTableau().setColonneLien(colonneLien);
        pr.getTableau().setLibelleAffiche(new String[]{
            "Id", "Date", "Référence", "BR source", "État"
        });

        pr.getTableau().setLienFille(
            "a_tahina/poussin/inc/insertion-compartiment-fille-liste.jsp&id=");

        Map<String,String> lienTab = new HashMap<>();
        lienTab.put("modifier",
            pr.getLien() + "?but=a_tahina/poussin/insertion-compartiment-modif.jsp");
        lienTab.put("fiche",
            pr.getLien() + "?but=a_tahina/poussin/insertion-compartiment-fiche.jsp");
        pr.getTableau().setLienClicDroite(lienTab);

        pr.getFormu().setAnotherButton(
            "<a class='btn btn-primary pull-right btn-small' " +
            "href='" + pr.getLien()
            + "?but=a_tahina/poussin/insertion-compartiment-saisie.jsp'>" +
            "<i class='material-symbols-rounded'>add</i> Saisir</a>");

        return pr;
    }

    /* =========================================================
     *  FICHE
     * ========================================================= */
    public static PageConsulte getPageConsulte(
            HttpServletRequest request,
            HttpSession session) throws Exception {

        InsertionCompartimentLib f = new InsertionCompartimentLib();
        f.setNomTable("INSERTION_COMPARTIMENT_LIB");

        PageConsulte pc = new PageConsulte(f, request,
                            (user.UserEJB) session.getValue("u"));
        pc.setTitre("Fiche d'une insertion en compartiment");
        pc.getBase();

        pc.getChampByName("id").setLibelle("Id");
        pc.getChampByName("daty").setLibelle("Date");
        pc.getChampByName("reference").setLibelle("Référence");
        pc.getChampByName("idbonreceptionlib").setLibelle("BR source");
        pc.getChampByName("etatlib").setLibelle("État");
        pc.getChampByName("remarque").setLibelle("Remarque");

        pc.getChampByName("idbonreception").setVisible(false);
        pc.getChampByName("etat").setVisible(false);

        return pc;
    }

    /* =========================================================
     *  ONGLET DÉTAILS
     * ========================================================= */
    public static PageRecherche getPageRechercheForFille(
            HttpServletRequest request,
            HttpSession session,
            String idMere) throws Exception {

        InsertionCompartimentFilleLib f = new InsertionCompartimentFilleLib();
        f.setNomTable("INSERTION_COMP_FILLE_LIB");

        String[] listeCrt  = {"compartimentlib"};
        String[] listeInt  = {};
        String[] libEntete = {"id", "compartimentlib", "qte", "pu",
                              "valeurCompartiment", "sexelib", "santelib"};

        PageRecherche pr = new PageRecherche(f, request, listeCrt, listeInt, 3,
                                              libEntete, libEntete.length);
        pr.setTitre("");
        pr.setUtilisateur((user.UserEJB) session.getValue("u"));
        pr.setLien((String) session.getValue("lien"));
        pr.setApres("a_tahina/poussin/insertion-compartiment-fiche.jsp"
                    + "&id=" + idMere
                    + "&tab=inc/insertion-compartiment-fille-liste");
        pr.setNpp(200);

        pr.setAWhere(" and idmere = '" + idMere + "'");

        pr.getFormu().getChamp("compartimentlib").setVisible(false);

        pr.creerObjetPage(libEntete, null);

        pr.getTableau().setLibelleAffiche(new String[]{
            "Id", "Compartiment", "qte", "pu",
            "valeur", "sexe", "santé"
        });

        return pr;
    }

    /* =========================================================
     *  SAISIE
     * ========================================================= */
    public static PageInsertMultiple getPageInsertMultiple(
            HttpServletRequest request,
            HttpSession session,
            int nombreLigne) throws Exception {

        InsertionCompartiment      mere  = new InsertionCompartiment();
        InsertionCompartimentFille fille = new InsertionCompartimentFille();
        fille.setNomTable("INSERTION_COMPARTIMENT_FILLE");

        PageInsertMultiple pi = new PageInsertMultiple(mere, fille, request,
                                                        nombreLigne,
                                                        (user.UserEJB) session.getValue("u"));
        pi.setLien((String) session.getValue("lien"));

        // --- En-tête
        pi.getFormu().getChamp("daty").setLibelle("Date");
        pi.getFormu().getChamp("reference").setLibelle("Référence");
        pi.getFormu().getChamp("idbonreception").setLibelle("BR source");
        pi.getFormu().getChamp("remarque").setLibelle("Remarque");
        pi.getFormu().getChamp("etat").setVisible(false);
        pi.getFormu().getChamp("daty").setDefaut(utilitaire.Utilitaire.dateDuJour());

        Liste[] listeMere = new Liste[1];
        listeMere[0] = new Liste("idbonreception",
                                  new BonReceptionPoussinLib(), "reference", "id");
        pi.getFormu().changerEnChamp(listeMere);

        // --- Cas : on vient d'une fiche BR (bouton « Insérer »)
        String idBR = request.getParameter("idbonreception");
        if (idBR != null && !idBR.trim().isEmpty()) {
            pi.getFormu().getChamp("idbonreception").setDefaut(idBR);
            pi.getFormu().getChamp("idbonreception").setVisible(false);
        }

        // --- Listes déroulantes des filles
        Liste[] listeFille = new Liste[3];
        listeFille[0] = new Liste("idcompartiment", new Compartiment(), "nom", "id");
        listeFille[1] = new Liste("sexe", new TypeObjet("SEXE"), "val", "id");
        listeFille[2] = new Liste("idsante", new SantePoussin(), "val", "id");
        pi.getFormufle().changerEnChamp(listeFille);

        pi.preparerDataFormu();

        // --- Libellés
        pi.getFormufle().getChamp("idcompartiment_0").setLibelle("Compartiment");
        pi.getFormufle().getChamp("qte_0").setLibelle("qte");
        pi.getFormufle().getChamp("pu_0").setLibelle("pu");
        pi.getFormufle().getChamp("valeurCompartiment_0").setLibelle("valeur");
        pi.getFormufle().getChamp("sexe_0").setLibelle("sexe");
        pi.getFormufle().getChamp("idsante_0").setLibelle("santé");

        pi.getFormufle().getChampMulitple("id").setVisible(false);
        pi.getFormufle().getChampMulitple("idmere").setVisible(false);

        // valeurCompartiment : readonly (calculé = qte * pu)
        for (int i = 0; i < nombreLigne; i++) {
            pi.getFormufle().getChamp("valeurCompartiment_"+i).setAutre("readonly");
            pi.getFormufle().getChamp("qte_"+i)
                .setAutre("onchange='recalcValeur("+i+")'");
            pi.getFormufle().getChamp("pu_"+i)
                .setAutre("onchange='recalcValeur("+i+")'");

            pi.getFormufle().getChamp("qte_"+i).setDefaut("0");
            pi.getFormufle().getChamp("pu_"+i).setDefaut("0");
            pi.getFormufle().getChamp("valeurCompartiment_"+i).setDefaut("0");
        }

        pi.getFormufle().setColOrdre(new String[]{
            "idcompartiment", "qte", "pu", "valeurCompartiment", "sexe", "idsante"
        });

        pi.getFormu().makeHtmlInsertTabIndex();
        pi.getFormufle().makeHtmlInsertTableauIndex();

        return pi;
    }

    /* =========================================================
     *  MODIF
     * ========================================================= */
    public static PageUpdateMultiple getPageUpdateMultiple(
            HttpServletRequest request,
            HttpSession session,
            String id,
            int nbNouvellesLignes) throws Exception {

        InsertionCompartiment mere = new InsertionCompartiment();
        mere = (InsertionCompartiment) mere.getById(id, "INSERTION_COMPARTIMENT", null);

        InsertionCompartimentFille fille = new InsertionCompartimentFille();
        fille.setNomTable("INSERTION_COMPARTIMENT_FILLE");
        fille.setIdmere(id);

        InsertionCompartimentFille[] listef = (InsertionCompartimentFille[])
            bean.CGenUtil.rechercher(fille, null, null,
                " and idmere = '" + id + "'");

        PageUpdateMultiple pu = new PageUpdateMultiple(
                                    mere, fille, listef, request,
                                    (user.UserEJB) session.getValue("u"),
                                    nbNouvellesLignes);
        pu.setLien((String) session.getValue("lien"));

        pu.getFormu().getChamp("daty").setLibelle("Date");
        pu.getFormu().getChamp("reference").setLibelle("Référence");
        pu.getFormu().getChamp("idbonreception").setLibelle("BR source");
        pu.getFormu().getChamp("remarque").setLibelle("Remarque");
        pu.getFormu().getChamp("etat").setVisible(false);

        Liste[] listeMere = new Liste[1];
        listeMere[0] = new Liste("idbonreception", new BonReceptionPoussinLib(),
                                  "reference", "id");
        pu.getFormu().changerEnChamp(listeMere);

        Liste[] listeFille = new Liste[3];
        listeFille[0] = new Liste("idcompartiment", new Compartiment(), "nom", "id");
        listeFille[1] = new Liste("sexe", new TypeObjet("SEXE"), "val", "id");
        listeFille[2] = new Liste("idsante", new SantePoussin(), "val", "id");
        pu.getFormufle().changerEnChamp(listeFille);

        pu.preparerDataFormu();

        pu.getFormufle().getChamp("idcompartiment_0").setLibelle("Compartiment");
        pu.getFormufle().getChamp("qte_0").setLibelle("qte");
        pu.getFormufle().getChamp("pu_0").setLibelle("pu");
        pu.getFormufle().getChamp("valeurCompartiment_0").setLibelle("valeur");
        pu.getFormufle().getChamp("sexe_0").setLibelle("sexe");
        pu.getFormufle().getChamp("idsante_0").setLibelle("santé");

        pu.getFormufle().getChampMulitple("idmere").setVisible(false);

        int nbLignesTotal = pu.getNombreLigne();
        for (int i = 0; i < nbLignesTotal; i++) {
            pu.getFormufle().getChamp("valeurCompartiment_"+i).setAutre("readonly");
            pu.getFormufle().getChamp("qte_"+i)
                .setAutre("onchange='recalcValeur("+i+")'");
            pu.getFormufle().getChamp("pu_"+i)
                .setAutre("onchange='recalcValeur("+i+")'");
        }

        pu.getFormufle().setColOrdre(new String[]{
            "idcompartiment", "qte", "pu", "valeurCompartiment", "sexe", "idsante"
        });

        pu.getFormu().makeHtmlInsertTabIndex();
        pu.getFormufle().makeHtmlInsertTableauIndex();

        return pu;
    }
}