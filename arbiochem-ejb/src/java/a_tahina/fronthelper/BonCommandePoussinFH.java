package a_tahina.fronthelper;

import a_tahina.poussin.BonCommandePoussin;
import a_tahina.poussin.BonCommandePoussinFille;
import a_tahina.poussin.BonCommandePoussinLib;
import a_tahina.poussin.BonCommandePoussinFilleLib;
import a_tahina.configuration.Souche;

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

        PageConsulte pc = new PageConsulte(f, request,
                            (user.UserEJB) session.getValue("u"));
        pc.setTitre("Fiche d'un bon de commande poussin");
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

        return pc;
    }

    
    public static PageRecherche getPageRechercheForFille(
            HttpServletRequest request,
            HttpSession session,
            String idMere) throws Exception {

        BonCommandePoussinFilleLib f = new BonCommandePoussinFilleLib();
        f.setNomTable("BONCOMMANDE_POUSSIN_FILLE_LIB");

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

    
    public static PageInsertMultiple getPageInsertMultiple(
            HttpServletRequest request,
            HttpSession session,
            int nombreLigne) throws Exception {

        // 1) Mère + fille
        BonCommandePoussin      mere  = new BonCommandePoussin();
        BonCommandePoussinFille fille = new BonCommandePoussinFille();
        fille.setNomTable("BONCOMMANDE_POUSSIN_FILLE");

        // 2) PageInsertMultiple
        PageInsertMultiple pi = new PageInsertMultiple(mere, fille, request,
                                                        nombreLigne,
                                                        (user.UserEJB) session.getValue("u"));
        pi.setLien((String) session.getValue("lien"));

        // 3) En-tête : libellés
        pi.getFormu().getChamp("daty").setLibelle("Date");
        pi.getFormu().getChamp("reference").setLibelle("Référence");
        pi.getFormu().getChamp("descri").setLibelle("Description");
        pi.getFormu().getChamp("idmagasin").setLibelle("Magasin");
        pi.getFormu().getChamp("remarque").setLibelle("Remarque");
        pi.getFormu().getChamp("etat").setVisible(false);

        pi.getFormu().getChamp("daty").setDefaut(
            utilitaire.Utilitaire.dateDuJour());

        // 4) En-tête : Magasin → LISTE DÉROULANTE
        Liste[] listeMere = new Liste[1];
        listeMere[0] = new Liste("idmagasin", new magasin.Magasin(), "val", "id");
        pi.getFormu().changerEnChamp(listeMere);

        // 5) Fille : Produit + Souche → LISTES DÉROULANTES
        Liste[] listeFille = new Liste[2];
        listeFille[0] = new Liste("idproduit", new produits.Ingredients(), "libelle", "id");
        listeFille[1] = new Liste("idsouche",  new a_tahina.configuration.Souche(), "val", "id");
        pi.getFormufle().changerEnChamp(listeFille);

        // 6) Préparer les données (OBLIGATOIRE avant getChamp("xxx_0"))
        pi.preparerDataFormu();

        // 7) Libellés colonnes de détail
        pi.getFormufle().getChamp("idproduit_0").setLibelle("Produit");
        pi.getFormufle().getChamp("idsouche_0").setLibelle("Souche");
        pi.getFormufle().getChamp("qte_0").setLibelle("Qté");
        pi.getFormufle().getChamp("pu_0").setLibelle("P.U.");

        // 8) Masquer les colonnes techniques
        pi.getFormufle().getChampMulitple("id").setVisible(false);
        pi.getFormufle().getChampMulitple("idmere").setVisible(false);

        // 9) Défauts ligne par ligne
        for (int i = 0; i < nombreLigne; i++) {
            pi.getFormufle().getChamp("qte_"+i).setDefaut("0");
            pi.getFormufle().getChamp("pu_"+i).setDefaut("0");
        }

        // 10) Colonnes conservées dans le tableau
        pi.getFormufle().setColOrdre(new String[]{
            "idproduit", "idsouche", "qte", "pu"
        });

        // 11) Tabindex
        pi.getFormu().makeHtmlInsertTabIndex();
        pi.getFormufle().makeHtmlInsertTableauIndex();

        return pi;
    }

    /* =========================================================
     *  MODIF (maître + filles) — inchangé
    * ========================================================= */
   
    public static PageUpdateMultiple getPageUpdateMultiple(
            HttpServletRequest request,
            HttpSession session,
            String id,
            int nbNouvellesLignes) throws Exception {

        BonCommandePoussin mere = new BonCommandePoussin();
        mere = (BonCommandePoussin) mere.getById(id, "BONCOMMANDE_POUSSIN", null);

        BonCommandePoussinFille fille = new BonCommandePoussinFille();
        fille.setNomTable("BONCOMMANDE_POUSSIN_FILLE");
        fille.setIdmere(id);

        BonCommandePoussinFille[] listef = (BonCommandePoussinFille[])
            bean.CGenUtil.rechercher(fille, null, null,
                " and idmere = '" + id + "'");

        PageUpdateMultiple pu = new PageUpdateMultiple(
                                    mere, fille, listef, request,
                                    (user.UserEJB) session.getValue("u"),
                                    nbNouvellesLignes);
        pu.setLien((String) session.getValue("lien"));

        pu.getFormu().getChamp("daty").setLibelle("Date");
        pu.getFormu().getChamp("reference").setLibelle("Référence");
        pu.getFormu().getChamp("descri").setLibelle("Description");
        pu.getFormu().getChamp("idmagasin").setLibelle("Magasin");
        pu.getFormu().getChamp("remarque").setLibelle("Remarque");
        pu.getFormu().getChamp("etat").setVisible(false);

        // Ici aussi : Magasin en Liste déroulante
        Liste[] listeMere = new Liste[1];
        listeMere[0] = new Liste("idmagasin", new magasin.Magasin(), "val", "id");
        pu.getFormu().changerEnChamp(listeMere);

        // Fille : Produit + Souche en Liste
        Liste[] listeFille = new Liste[2];
        listeFille[0] = new Liste("idproduit", new produits.Ingredients(), "libelle", "id");
        listeFille[1] = new Liste("idsouche",  new a_tahina.configuration.Souche(), "val", "id");
        pu.getFormufle().changerEnChamp(listeFille);

        pu.preparerDataFormu();

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