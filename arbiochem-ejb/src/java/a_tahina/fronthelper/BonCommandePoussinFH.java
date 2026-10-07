package a_tahina.fronthelper;

import a_tahina.poussin.BonCommandePoussin;
import a_tahina.poussin.BonCommandePoussinFille;
import a_tahina.poussin.BonCommandePoussinFilleLib;
import a_tahina.poussin.BonCommandePoussinLib;
import affichage.Liste;
import affichage.PageConsulte;
import affichage.PageRecherche;
import affichage.PageUpdateMultiple;
import bean.TypeObjet;
import affichage.Champ;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class BonCommandePoussinFH {

  
    public static PageRecherche getPageRechercheForList(
            HttpServletRequest request,
            HttpSession session) throws Exception {

        BonCommandePoussinLib f = new BonCommandePoussinLib();
        f.setNomTable("BONCOMMANDE_POUSSIN_LIB");

        String[] listeCrt  = {"id", "daty", "reference", "idmagasinlib", "etat"};
        String[] listeInt  = {"daty"};
        String[] libEntete = {"id", "daty", "reference", "idmagasinlib", "etatlib"};

        PageRecherche pr = new PageRecherche(f, request, listeCrt, listeInt, 3,
                                              libEntete, libEntete.length);
        pr.setTitre("Liste des bons de commande poussin");
        pr.setUtilisateur((user.UserEJB) session.getValue("u"));
        pr.setLien((String) session.getValue("lien"));
        pr.setApres("a_tahina/poussin/boncommande-poussin-liste.jsp");
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
        pr.getFormu().getChamp("idmagasinlib").setLibelle("Magasin");

        pr.getFormu().getChamp("daty1").setDefaut(utilitaire.Utilitaire.dateDuJour());
        pr.getFormu().getChamp("daty2").setDefaut(utilitaire.Utilitaire.dateDuJour());

        // Filtre état <select> alimenté par ETAT
        Liste[] liste = new Liste[1];
        liste[0] = new Liste("etat", new TypeObjet("ETAT"), "desce", "id");
        pr.getFormu().changerEnChamp(liste);
        pr.getFormu().getChamp("etat").setLibelle("État");

        pr.creerObjetPage(libEntete, null);

        String[] lienTableau = { pr.getLien()
            + "?but=a_tahina/poussin/boncommande-poussin-fiche.jsp" };
        String[] colonneLien = { "id" };
        pr.getTableau().setLien(lienTableau);
        pr.getTableau().setColonneLien(colonneLien);
        pr.getTableau().setLibelleAffiche(new String[]{
            "Id", "Date", "Référence", "Magasin", "État"
        });

        pr.getTableau().setLienFille(
            "a_tahina/poussin/inc/boncommande-poussin-fille-liste.jsp&id=");

        Map<String,String> lienTab = new HashMap<>();
        lienTab.put("modifier",
            pr.getLien() + "?but=a_tahina/poussin/boncommande-poussin-modif.jsp");
        lienTab.put("fiche",
            pr.getLien() + "?but=a_tahina/poussin/boncommande-poussin-fiche.jsp");
        pr.getTableau().setLienClicDroite(lienTab);

        pr.getFormu().setAnotherButton(
            "<a class='btn btn-primary pull-right btn-small' " +
            "href='" + pr.getLien()
            + "?but=a_tahina/poussin/boncommande-poussin-saisie.jsp'>" +
            "<i class='material-symbols-rounded'>add</i> Saisir</a>");

        return pr;
    }

    
    public static PageConsulte getPageConsulte(
            HttpServletRequest request,
            HttpSession session) throws Exception {

        BonCommandePoussinLib f = new BonCommandePoussinLib();
        f.setNomTable("BONCOMMANDE_POUSSIN_LIB");

        // PageConsulte(objet, request, user)
        PageConsulte pc = new PageConsulte(f, request,
                            (user.UserEJB) session.getValue("u"));
        pc.setTitre("Fiche d'un bon de commande poussin");

        // Charger l'objet depuis request.getParameter("id")
        pc.getBase();

        pc.getChampByName("id").setLibelle("Id");
        pc.getChampByName("daty").setLibelle("Date");
        pc.getChampByName("reference").setLibelle("Référence");
        pc.getChampByName("descri").setLibelle("Description");
        pc.getChampByName("idmagasinlib").setLibelle("Magasin");
        pc.getChampByName("etatlib").setLibelle("État");
        pc.getChampByName("remarque").setLibelle("Remarque");

        pc.getChampByName("idmagasin").setVisible(false);
        pc.getChampByName("etat").setVisible(false);

        // 6) Lien hypertexte vers le magasin (optionnel — à adapter)
        // pc.getChampByName("idmagasinlib").setLien(
        //     (String) session.getValue("lien")
        //     + "?but=magasin/magasin-fiche.jsp", "id=");

        return pc;
    }

   
    public static PageRecherche getPageRechercheForFille(
            HttpServletRequest request,
            HttpSession session,
            String idMere) throws Exception {

        BonCommandePoussinFilleLib f = new BonCommandePoussinFilleLib();
        f.setNomTable("BONCOMMANDE_POUSSON_FILLE_LIB");

        String[] listeCrt  = {"produitlib", "souchelibr"};
        String[] listeInt  = {};
        String[] libEntete = {"id", "produitlib", "souchelibr", "qte", "pu", "montant"};

        PageRecherche pr = new PageRecherche(f, request, listeCrt, listeInt, 3,
                                              libEntete, libEntete.length);
        pr.setTitre("");
        pr.setUtilisateur((user.UserEJB) session.getValue("u"));
        pr.setLien((String) session.getValue("lien"));
        pr.setApres("a_tahina/poussin/boncommande-poussin-fiche.jsp"
                    + "&id=" + idMere
                    + "&tab=inc/boncommande-poussin-fille-liste");
        pr.setNpp(200);

        pr.setAWhere(" and idmere = '" + idMere + "'");

        pr.getFormu().getChamp("produitlib").setVisible(false);
        pr.getFormu().getChamp("souchelibr").setVisible(false);

        pr.creerObjetPage(libEntete, null);

        pr.getTableau().setLibelleAffiche(new String[]{
            "Id", "Produit", "Souche", "Qté", "P.U.", "Montant"
        });

        return pr;
    }

        /* =========================================================
     *  MODIF (maître + filles)
     * ========================================================= */
    public static PageUpdateMultiple getPageUpdateMultiple(
            HttpServletRequest request,
            HttpSession session,
            String id,
            int nbNouvellesLignes) throws Exception {

        // 1) Recharger la mère
        BonCommandePoussin mere = new BonCommandePoussin();
        mere = (BonCommandePoussin) mere.getById(id, "BONCOMMANDE_POUSSIN", null);

        // 2) Fille + ses lignes existantes
        BonCommandePoussinFille fille = new BonCommandePoussinFille();
        fille.setNomTable("BONCOMMANDE_POUSSIN_FILLE");
        fille.setIdmere(id);

        BonCommandePoussinFille[] listef = (BonCommandePoussinFille[])
            bean.CGenUtil.rechercher(fille, null, null,
                " and idmere = '" + id + "'");

        // 3) PageUpdateMultiple(mere, fille, listef, request, user, nbNouvelles)
        PageUpdateMultiple pu = new PageUpdateMultiple(
                                    mere, fille, listef, request,
                                    (user.UserEJB) session.getValue("u"),
                                    nbNouvellesLignes);
        pu.setLien((String) session.getValue("lien"));

        // 4) En-tête
        pu.getFormu().getChamp("daty").setLibelle("Date");
        pu.getFormu().getChamp("reference").setLibelle("Référence");
        pu.getFormu().getChamp("descri").setLibelle("Description");
        pu.getFormu().getChamp("idmagasin").setLibelle("Magasin");
        pu.getFormu().getChamp("remarque").setLibelle("Remarque");
        pu.getFormu().getChamp("etat").setVisible(false);

        pu.getFormu().getChamp("idmagasin").setPageAppelComplete(
            "magasin.Magasin", "id", "MAGASIN2", "idmagasin", "id");

        pu.preparerDataFormu();

        // 5) Autocomplete filles
        Champ.setPageAppelComplete(
            pu.getFormufle().getChampFille("idproduit"),
            "produits.IngredientsLib", "id", "AS_INGREDIENTS",
            "idproduit", "id");

        Champ.setPageAppelComplete(
            pu.getFormufle().getChampFille("idsouche"),
            "a_tahina.configuration.Souche", "id", "SOUCHE",
            "idsouche", "id");

        // 6) Libellés détail
        pu.getFormufle().getChamp("idproduit_0").setLibelle("Produit");
        pu.getFormufle().getChamp("idsouche_0").setLibelle("Souche");
        pu.getFormufle().getChamp("qte_0").setLibelle("Qté");
        pu.getFormufle().getChamp("pu_0").setLibelle("P.U.");

        pu.getFormufle().getChampMulitple("idmere").setVisible(false);

        pu.getFormufle().setColOrdre(new String[]{
            "idproduit", "idsouche", "qte", "pu"
        });

        pu.getFormu().makeHtmlInsertTabIndex();
        pu.getFormufle().makeHtmlInsertTableauIndex();

        return pu;
    }
}