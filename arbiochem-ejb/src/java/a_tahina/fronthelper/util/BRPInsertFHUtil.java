package a_tahina.fronthelper.util;

import a_tahina.poussin.BonCommandePoussinFilleLib;
import a_tahina.poussin.BonReceptionPoussinLib;
import a_tahina.poussin.BonReceptionPoussinFilleLib;
import a_tahina.configuration.Souche;

import affichage.Liste;
import affichage.PageInsertMultiple;

import java.util.ArrayList;
import java.util.List;


public class BRPInsertFHUtil {


    public static void configurerListesFille(PageInsertMultiple pi) throws Exception {
        Liste[] listeFille = new Liste[2];
        listeFille[0] = new Liste("idproduit", new produits.Ingredients(),
                                   "libelle", "id");
        listeFille[1] = new Liste("idsouche", new Souche(), "val", "id");
        pi.getFormufle().changerEnChamp(listeFille);
    }

    /** Configure les libellés des colonnes de détail. */
    public static void configurerLibellesFille(PageInsertMultiple pi) {
        pi.getFormufle().getChamp("idproduit_0").setLibelle("Produit");
        pi.getFormufle().getChamp("idsouche_0").setLibelle("Souche");
        pi.getFormufle().getChamp("qte_0").setLibelle("qte");
        pi.getFormufle().getChamp("pu_0").setLibelle("pu");
        pi.getFormufle().getChamp("maty_0").setLibelle("maty");
        pi.getFormufle().getChamp("very_0").setLibelle("very");
        pi.getFormufle().getChamp("reste_0").setLibelle("reste");
    }

   
    public static void masquerColonnesTechniques(PageInsertMultiple pi) throws Exception {
        pi.getFormufle().getChampMulitple("id").setVisible(false);
        pi.getFormufle().getChampMulitple("idmere").setVisible(false);
    }

    /* =========================================================
     *  CHARGEMENT DES FILLES SOURCES
     * ========================================================= */
    public static List<Object[]> chargerFillesDuBC(String idBC) {
        List<Object[]> result = new ArrayList<>();
        if (idBC == null || idBC.trim().isEmpty()) return result;

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
                        String.valueOf(qte),
                        String.valueOf(f.getPu()),
                        "0",
                        "0",
                        String.valueOf(qte)
                    });
                }
            }
        } catch (Exception ex) { /* ignore */ }
        return result;
    }

   
    public static List<Object[]> chargerFillesDuBR(String idBRPrec) {
        List<Object[]> result = new ArrayList<>();
        if (idBRPrec == null || idBRPrec.trim().isEmpty()) return result;

        try {
            BonReceptionPoussinFilleLib crit = new BonReceptionPoussinFilleLib();
            crit.setNomTable("BONRECEPTION_POUSSIN_FILLE_LIB");

            BonReceptionPoussinFilleLib[] filles = (BonReceptionPoussinFilleLib[])
                bean.CGenUtil.rechercher(crit, null, null,
                    " and idmere = '" + idBRPrec + "'");

            if (filles != null) {
                for (BonReceptionPoussinFilleLib f : filles) {
                    double restePrec = f.getReste();
                    result.add(new Object[]{
                        f.getIdproduit(),
                        f.getIdsouche(),
                        String.valueOf(restePrec),
                        String.valueOf(f.getPu()),
                        "0",
                        "0",
                        String.valueOf(restePrec)
                    });
                }
            }
        } catch (Exception ex) { /* ignore */ }
        return result;
    }

   
    public static String getBCSourceDuBR(String idBRPrec) {
        if (idBRPrec == null || idBRPrec.trim().isEmpty()) return null;
        try {
            BonReceptionPoussinLib brPrec = (BonReceptionPoussinLib)
                new BonReceptionPoussinLib().getById(idBRPrec,
                    "BONRECEPTION_POUSSIN_LIB", null);
            if (brPrec != null) {
                return brPrec.getIdboncommande();
            }
        } catch (Exception ex) { /* ignore */ }
        return null;
    }

   
    public static void appliquerDefauts(PageInsertMultiple pi,
                                         List<Object[]> filles,
                                         int nombreLigne) {
        for (int i = 0; i < nombreLigne; i++) {
            // qte : readonly (non modifiable)
            pi.getFormufle().getChamp("qte_"+i).setAutre("readonly");

            // reste : readonly (calculé)
            pi.getFormufle().getChamp("reste_"+i).setAutre("readonly");

            // maty et very : éditable, recalcul du reste
            pi.getFormufle().getChamp("maty_"+i)
                .setAutre("onchange='recalcReste("+i+")'");
            pi.getFormufle().getChamp("very_"+i)
                .setAutre("onchange='recalcReste("+i+")'");

            // Défauts de base
            pi.getFormufle().getChamp("qte_"+i).setDefaut("0");
            pi.getFormufle().getChamp("pu_"+i).setDefaut("0");
            pi.getFormufle().getChamp("maty_"+i).setDefaut("0");
            pi.getFormufle().getChamp("very_"+i).setDefaut("0");
            pi.getFormufle().getChamp("reste_"+i).setDefaut("0");

            // Surcharge si pré-remplissage
            if (i < filles.size()) {
                Object[] f = filles.get(i);
                pi.getFormufle().getChamp("idproduit_"+i).setDefaut((String) f[0]);
                pi.getFormufle().getChamp("idsouche_"+i).setDefaut((String) f[1]);
                pi.getFormufle().getChamp("qte_"+i).setDefaut((String) f[2]);
                pi.getFormufle().getChamp("pu_"+i).setDefaut((String) f[3]);
                pi.getFormufle().getChamp("maty_"+i).setDefaut((String) f[4]);
                pi.getFormufle().getChamp("very_"+i).setDefaut((String) f[5]);
                pi.getFormufle().getChamp("reste_"+i).setDefaut((String) f[6]);
            }
        }
    }

    /** Même chose que appliquerDefauts mais pour PageUpdateMultiple. */
    public static void appliquerDefautsUpdate(affichage.PageUpdateMultiple pu,
                                               int nombreLigne) {
        for (int i = 0; i < nombreLigne; i++) {
            pu.getFormufle().getChamp("qte_"+i).setAutre("readonly");
            pu.getFormufle().getChamp("reste_"+i).setAutre("readonly");
            pu.getFormufle().getChamp("maty_"+i)
                .setAutre("onchange='recalcReste("+i+")'");
            pu.getFormufle().getChamp("very_"+i)
                .setAutre("onchange='recalcReste("+i+")'");
        }
    }

  

    public static boolean estNonVide(String s) {
        return s != null && !s.trim().isEmpty();
    }

    public static String[] getColOrdre() {
        return new String[]{
            "idproduit", "idsouche", "qte", "pu", "maty", "very", "reste"
        };
    }

    /** Libellés du tableau de l'onglet Détails (fiche). */
    public static String[] getLibelleAfficheFille() {
        return new String[]{
            "Id", "Produit", "Souche",
            "qte", "pu", "maty", "very", "reste", "montant"
        };
    }
}