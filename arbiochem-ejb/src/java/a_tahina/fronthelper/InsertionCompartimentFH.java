package a_tahina.fronthelper;

import a_tahina.poussin.BonCommandePoussinFilleLib;
import a_tahina.poussin.BonReceptionPoussinLib;
import a_tahina.poussin.InsertionCompartiment;
import a_tahina.poussin.InsertionCompartimentFille;
import a_tahina.poussin.InsertionCompartimentLib;
import a_tahina.poussin.InsertionCompartimentFilleLib;
import a_tahina.configuration.Compartiment;
import a_tahina.configuration.CompartimentValeur;
import a_tahina.configuration.SantePoussin;

import affichage.Champ;
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

  
    public static PageRecherche getPageRechercheForFille(
            HttpServletRequest request,
            HttpSession session,
            String idMere) throws Exception {

        InsertionCompartimentFilleLib f = new InsertionCompartimentFilleLib();
        f.setNomTable("INSERTION_COMP_FILLE_LIB");

        String[] listeCrt  = {"compartimentlib", "bcpfproduitlib"};
        String[] listeInt  = {};
        String[] libEntete = {"id", "bcpfproduitlib", "compartimentlib",
                              "qte", "pu", "valeurCompartiment",
                              "sexelib", "santelib"};

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

        safeHide(pr.getFormu().getChamp("compartimentlib"));
        safeHide(pr.getFormu().getChamp("bcpfproduitlib"));

        pr.creerObjetPage(libEntete, null);

        pr.getTableau().setLibelleAffiche(new String[]{
            "Id", "Produit (BC)", "Compartiment",
            "qte", "pu", "valeur", "sexe", "santé"
        });

        return pr;
    }

    public static Map<String, Double> getPuParBC(HttpServletRequest request) {
        Map<String, Double> map = new HashMap<>();

        String idBR = request.getParameter("idbonreception");
        String bcSourceFiltre = null;
        if (idBR != null && !idBR.trim().isEmpty()) {
            try {
                BonReceptionPoussinLib br = (BonReceptionPoussinLib)
                    new BonReceptionPoussinLib().getById(idBR,
                        "BONRECEPTION_POUSSIN_LIB", null);
                if (br != null) bcSourceFiltre = br.getIdboncommande();
            } catch (Exception ex) { /* ignore */ }
        }

        try {
            BonCommandePoussinFilleLib crit = new BonCommandePoussinFilleLib();
            crit.setNomTable("BONCOMMANDE_POUSSIN_FILLE_LIB");

            String where = (bcSourceFiltre != null
                            && !bcSourceFiltre.trim().isEmpty())
                           ? " and idmere = '" + bcSourceFiltre + "'" : "";

            BonCommandePoussinFilleLib[] filles = (BonCommandePoussinFilleLib[])
                bean.CGenUtil.rechercher(crit, null, null, where);

            if (filles != null) {
                for (BonCommandePoussinFilleLib f : filles) {
                    map.put(f.getId(), f.getPu());
                }
            }
        } catch (Exception ex) { /* ignore */ }

        return map;
    }

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
        safeLabel(pi.getFormu().getChamp("daty"),           "Date");
        safeLabel(pi.getFormu().getChamp("reference"),      "Référence");
        safeLabel(pi.getFormu().getChamp("idbonreception"), "BR source");
        safeLabel(pi.getFormu().getChamp("remarque"),       "Remarque");
        safeHide (pi.getFormu().getChamp("etat"));

        if (pi.getFormu().getChamp("daty") != null) {
            pi.getFormu().getChamp("daty").setDefaut(utilitaire.Utilitaire.dateDuJour());
        }

        String idBR = request.getParameter("idbonreception");
        boolean masquerIdBR = false;
        if (idBR != null && !idBR.trim().isEmpty()) {
            safeDefault(pi.getFormu().getChamp("idbonreception"), idBR);
            masquerIdBR = true;
        }

        String bcSourceFiltre = null;
        if (idBR != null && !idBR.trim().isEmpty()) {
            try {
                BonReceptionPoussinLib br = (BonReceptionPoussinLib)
                    new BonReceptionPoussinLib().getById(idBR,
                        "BONRECEPTION_POUSSIN_LIB", null);
                if (br != null) bcSourceFiltre = br.getIdboncommande();
            } catch (Exception ex) { /* ignore */ }
        }

        Liste[] listeMere = new Liste[1];
        listeMere[0] = new Liste("idbonreception",
                                  new BonReceptionPoussinLib(), "reference", "id");
        pi.getFormu().changerEnChamp(listeMere);

        if (masquerIdBR) safeHide(pi.getFormu().getChamp("idbonreception"));

        Liste[] listeFille = new Liste[4];

        listeFille[0] = new Liste("idbcpf",
                                   new BonCommandePoussinFilleLib(),
                                   "idproduitlib", "id");
        if (bcSourceFiltre != null && !bcSourceFiltre.trim().isEmpty()) {
            listeFille[0].setApresW(" and idmere = '" + bcSourceFiltre + "'");
        }

        listeFille[1] = new Liste("idcompartiment",
                                   new Compartiment(), "nom", "id");
        listeFille[2] = new Liste("sexe",
                                   new TypeObjet("SEXE"), "val", "id");
        listeFille[3] = new Liste("idsante",
                                   new SantePoussin(), "val", "id");

        pi.getFormufle().changerEnChamp(listeFille);

        pi.preparerDataFormu();

        // --- Libellés des filles
        safeLabel(pi.getFormufle().getChamp("idbcpf_0"),            "Ligne BC");
        safeLabel(pi.getFormufle().getChamp("idcompartiment_0"),     "Compartiment");
        safeLabel(pi.getFormufle().getChamp("qte_0"),                "qte");
        safeLabel(pi.getFormufle().getChamp("pu_0"),                 "pu");
        safeLabel(pi.getFormufle().getChamp("valeurCompartiment_0"), "valeur");
        safeLabel(pi.getFormufle().getChamp("sexe_0"),               "sexe");
        safeLabel(pi.getFormufle().getChamp("idsante_0"),            "santé");


        pi.getFormufle().getChampMulitple("id").setVisible(false);
        pi.getFormufle().getChampMulitple("idmere").setVisible(false);

        
        for (int i = 0; i < nombreLigne; i++) {
            Champ cVal  = pi.getFormufle().getChamp("valeurCompartiment_"+i);
            Champ cQte  = pi.getFormufle().getChamp("qte_"+i);
            Champ cPu   = pi.getFormufle().getChamp("pu_"+i);
            Champ cBcpf = pi.getFormufle().getChamp("idbcpf_"+i);

            if (cVal != null) cVal.setAutre("readonly");
            if (cQte != null) {
                cQte.setAutre("onchange='recalcValeur("+i+")'");
                cQte.setDefaut("0");
            }
            if (cPu != null) {
                cPu.setAutre("onchange='recalcValeur("+i+")'");
                cPu.setDefaut("0");
            }
            if (cVal != null) cVal.setDefaut("0");

            
            if (cBcpf != null) {
                cBcpf.setAutre("onchange='majPu("+i+")'");
            }
        }

        pi.getFormufle().setColOrdre(new String[]{
            "idbcpf", "idcompartiment", "qte", "pu",
            "valeurCompartiment", "sexe", "idsante"
        });

        pi.getFormu().makeHtmlInsertTabIndex();
        pi.getFormufle().makeHtmlInsertTableauIndex();

        return pi;
    }

    
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

        safeLabel(pu.getFormu().getChamp("daty"),          "Date");
        safeLabel(pu.getFormu().getChamp("reference"),     "Référence");
        safeLabel(pu.getFormu().getChamp("idbonreception"),"BR source");
        safeLabel(pu.getFormu().getChamp("remarque"),      "Remarque");
        safeHide (pu.getFormu().getChamp("etat"));

        String bcSourceFiltre = null;
        if (mere.getIdbonreception() != null) {
            try {
                BonReceptionPoussinLib br = (BonReceptionPoussinLib)
                    new BonReceptionPoussinLib().getById(mere.getIdbonreception(),
                        "BONRECEPTION_POUSSIN_LIB", null);
                if (br != null) bcSourceFiltre = br.getIdboncommande();
            } catch (Exception ex) { /* ignore */ }
        }

        Liste[] listeMere = new Liste[1];
        listeMere[0] = new Liste("idbonreception", new BonReceptionPoussinLib(),
                                  "reference", "id");
        pu.getFormu().changerEnChamp(listeMere);

        // ⚡ SELECT : affiche "BCPF001 - Poussin"
        Liste[] listeFille = new Liste[4];
        listeFille[0] = new Liste("idbcpf",
                                   new BonCommandePoussinFilleLib(),
                                   "idproduitlib", "id");
        if (bcSourceFiltre != null && !bcSourceFiltre.trim().isEmpty()) {
            listeFille[0].setApresW(" and idmere = '" + bcSourceFiltre + "'");
        }
        listeFille[1] = new Liste("idcompartiment", new Compartiment(), "nom", "id");
        listeFille[2] = new Liste("sexe", new TypeObjet("SEXE"), "val", "id");
        listeFille[3] = new Liste("idsante", new SantePoussin(), "val", "id");
        pu.getFormufle().changerEnChamp(listeFille);

        pu.preparerDataFormu();

        safeLabel(pu.getFormufle().getChamp("idbcpf_0"),            "Ligne BC");
        safeLabel(pu.getFormufle().getChamp("idcompartiment_0"),     "Compartiment");
        safeLabel(pu.getFormufle().getChamp("qte_0"),                "qte");
        safeLabel(pu.getFormufle().getChamp("pu_0"),                 "pu");
        safeLabel(pu.getFormufle().getChamp("valeurCompartiment_0"), "valeur");
        safeLabel(pu.getFormufle().getChamp("sexe_0"),               "sexe");
        safeLabel(pu.getFormufle().getChamp("idsante_0"),            "santé");

        pu.getFormufle().getChampMulitple("idmere").setVisible(false);

        int nbLignesTotal = pu.getNombreLigne();
        for (int i = 0; i < nbLignesTotal; i++) {
            Champ cVal  = pu.getFormufle().getChamp("valeurCompartiment_"+i);
            Champ cQte  = pu.getFormufle().getChamp("qte_"+i);
            Champ cPu   = pu.getFormufle().getChamp("pu_"+i);
            Champ cBcpf = pu.getFormufle().getChamp("idbcpf_"+i);

            if (cVal != null) cVal.setAutre("readonly");
            if (cQte != null) cQte.setAutre("onchange='recalcValeur("+i+")'");
            if (cPu  != null) cPu.setAutre("onchange='recalcValeur("+i+")'");
            if (cBcpf != null) cBcpf.setAutre("onchange='majPu("+i+")'");
        }

        pu.getFormufle().setColOrdre(new String[]{
            "idbcpf", "idcompartiment", "qte", "pu",
            "valeurCompartiment", "sexe", "idsante"
        });

        pu.getFormu().makeHtmlInsertTabIndex();
        pu.getFormufle().makeHtmlInsertTableauIndex();

        return pu;
    }

    /* =========================================================
     *  HELPERS SAFE
     * ========================================================= */
    private static void safeLabel(Champ c, String libelle) {
        if (c != null) c.setLibelle(libelle);
    }

    private static void safeHide(Champ c) {
        if (c != null) c.setVisible(false);
    }

    private static void safeDefault(Champ c, String defaut) {
        if (c != null) c.setDefaut(defaut);
    }


        public static PageRecherche getPageRechercheForList(
            HttpServletRequest request,
            HttpSession session) throws Exception {

        CompartimentValeur f = new CompartimentValeur();
        f.setNomTable("COMPARTIMENT_VALEUR_LIB");

        String[] listeCrt  = {"id", "nom"};
        String[] listeInt  = {};
        String[] libEntete = {"id", "nom", "capacite",
                              "qteActuelle", "valeurUnitaire",
                              "valeurActuelle", "valeurTheorique",
                              "valeurNonRemplie"};

        PageRecherche pr = new PageRecherche(f, request, listeCrt, listeInt, 3,
                                              libEntete, libEntete.length);
        pr.setTitre("Liste des compartiments");
        pr.setUtilisateur((user.UserEJB) session.getValue("u"));
        pr.setLien((String) session.getValue("lien"));
        pr.setApres("a_tahina/configuration/compartiment-liste.jsp");
        pr.setNpp(50);

        pr.getFormu().getChamp("id").setLibelle("Id");
        pr.getFormu().getChamp("nom").setLibelle("Nom");

        pr.creerObjetPage(libEntete, null);

        String[] lienTableau = { pr.getLien()
            + "?but=a_tahina/configuration/compartiment-fiche.jsp" };
        String[] colonneLien = { "id" };
        pr.getTableau().setLien(lienTableau);
        pr.getTableau().setColonneLien(colonneLien);

        pr.getTableau().setLibelleAffiche(new String[]{
            "Id", "Nom", "Capacité",
            "Nb théorique",      
            "Valeur unitaire",    
            "Valeur actuelle",
            "Valeur théorique",
            "Valeur non remplie"
        });

        Map<String,String> lienTab = new HashMap<>();
        lienTab.put("modifier",
            pr.getLien() + "?but=a_tahina/configuration/compartiment-modif.jsp");
        pr.getTableau().setLienClicDroite(lienTab);

        pr.getFormu().setAnotherButton(
            "<a class='btn btn-primary pull-right btn-small' " +
            "href='" + pr.getLien()
            + "?but=a_tahina/configuration/compartiment-saisie.jsp'>" +
            "<i class='material-symbols-rounded'>add</i> Saisir</a>");

        return pr;
    }
}