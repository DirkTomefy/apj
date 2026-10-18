package a_tahina.fronthelper.bc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.bc.BonCommandePoussin;
import a_tahina.bc.BonCommandePoussinFille;
import affichage.Liste;
import affichage.PageInsertMultiple;
import bean.TypeObjet;
import a_tahina.prevision.Fournisseur;        
import ferme.configuration.Souche;

public class BCmdInsertFH {

    public static void prepareLibelleChampMere(PageInsertMultiple pi) {
        pi.getFormu().getChamp("daty").setLibelle("Date");
        pi.getFormu().getChamp("reference").setLibelle("Référence");
        pi.getFormu().getChamp("descri").setLibelle("Description");
        pi.getFormu().getChamp("idmagasin").setLibelle("Magasin");
        pi.getFormu().getChamp("remarque").setLibelle("Remarque");
        pi.getFormu().getChamp("etat").setVisible(false);
    }

    public static void prepareLibelleChampFille(PageInsertMultiple pi) throws Exception {
        pi.getFormufle().getChamp("idproduit_0").setLibelle("Produit");
        pi.getFormufle().getChamp("idsouche_0").setLibelle("Souche");
        pi.getFormufle().getChamp("fournisseur_0").setLibelle("Fournisseur");   // <-- NOUVEAU
        pi.getFormufle().getChamp("qte_0").setLibelle("Qté");
        pi.getFormufle().getChamp("pu_0").setLibelle("P.U.");

        pi.getFormufle().getChampMulitple("id").setVisible(false);
        pi.getFormufle().getChampMulitple("idmere").setVisible(false);
    }

    public static void prepareDefaultValueForFille(PageInsertMultiple pi, int nombreLigne) throws Exception {
        for (int i = 0; i < nombreLigne; i++) {
            pi.getFormufle().getChamp("qte_" + i).setDefaut("0");
            pi.getFormufle().getChamp("pu_" + i).setDefaut("0");
        }
    }

    public static void changeChampIntoListe(PageInsertMultiple pi) throws Exception {
        // !mère
        Liste[] listeMere = new Liste[1];
        listeMere[0] = new Liste("idmagasin", new magasin.Magasin(), "val", "id");
        pi.getFormu().changerEnChamp(listeMere);

        // !fille
        Liste[] listeFille = new Liste[3];                                      
        listeFille[0] = new Liste("idproduit",   new produits.Ingredients(), "libelle", "id");
        listeFille[1] = new Liste("idsouche",    new Souche(),               "val",     "id");
        listeFille[2] = new Liste("fournisseur", new Fournisseur(), "nom",   "id"); 
        pi.getFormufle().changerEnChamp(listeFille);
    }

    public static PageInsertMultiple getPageInsertMultiple(
            HttpServletRequest request,
            HttpSession session,
            int nombreLigne) throws Exception {
        BonCommandePoussin mere = new BonCommandePoussin();
        BonCommandePoussinFille fille = new BonCommandePoussinFille();
        PageInsertMultiple pi = new PageInsertMultiple(mere, fille, request, nombreLigne,
                (user.UserEJB) session.getValue("u"));

        pi.setLien((String) session.getValue("lien"));
        prepareLibelleChampMere(pi);
        pi.getFormu().getChamp("daty").setDefaut(
                utilitaire.Utilitaire.dateDuJour());
        changeChampIntoListe(pi);
        pi.preparerDataFormu();
        prepareLibelleChampFille(pi);
        prepareDefaultValueForFille(pi, nombreLigne);

        pi.getFormufle().setColOrdre(new String[] {
                "idproduit", "idsouche", "fournisseur", "qte", "pu"           // <-- fournisseur ajouté
        });

        pi.getFormu().makeHtmlInsertTabIndex();
        pi.getFormufle().makeHtmlInsertTableauIndex();
        return pi;
    }
}