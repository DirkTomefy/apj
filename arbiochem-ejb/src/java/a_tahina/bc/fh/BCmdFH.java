package a_tahina.bc.fh;

import static a_tahina.core.page.AtahinaChampHelper.champ;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.bc.orm.BonCommandePoussin;
import a_tahina.bc.orm.BonCommandePoussinFille;
import a_tahina.bc.orm.BonCommandePoussinFilleLib;
import a_tahina.bc.orm.BonCommandePoussinLib;
import a_tahina.core.GeneraticFH;
import a_tahina.core.page.AtahinaPageConsulte;
import a_tahina.core.page.AtahinaPageInsertMultiple;
import a_tahina.core.page.AtahinaPageRecherche;
import a_tahina.fournisseur.orm.Fournisseur;
import ferme.configuration.Souche;

public class BCmdFH extends GeneraticFH {

    public static final String DOSSIER      = "a_tahina/v1/bc/";
    public static final String PAGE_FICHE   = DOSSIER + "boncommande-fiche.jsp";
    public static final String PAGE_LISTE   = DOSSIER + "boncommande-liste.jsp";
    public static final String PAGE_MODIF   = DOSSIER + "boncommande-modif.jsp";
    public static final String PAGE_SAISIE  = DOSSIER + "boncommande-saisie.jsp";
    public static final String CLASSE       = "a_tahina.bc.orm.BonCommandePoussin";
    public static final String CLASSE_FILLE = "a_tahina.bc.orm.BonCommandePoussinFille";
    public static final int    NOMBRE_LIGNE = 10;

    @Override
    public AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,
                                                        HttpSession session) throws Exception {
        return liste(new BonCommandePoussinLib(), request, session,
                PAGE_LISTE, "Liste des bons de commande poussin",
                champ("id",           "Id").recherche().lien(PAGE_FICHE),
                champ("daty",         "Date").intervalle().defautAujourdhui(),
                champ("reference",    "Référence").recherche(),
                champ("idmagasinlib", "Magasin").recherche(),
                champ("etat",         "État").filtreSeul().listeEtat(),
                champ("etatlib",      "État"))
            .menuClicDroit("modifier", PAGE_MODIF)
            .boutonSaisir(PAGE_SAISIE);
    }

    @Override
    public AtahinaPageConsulte getPageConsulte(HttpServletRequest request,
                                               HttpSession session) throws Exception {
        return consulte(new BonCommandePoussinLib(), request, session, "Fiche bon de commande poussin",
                champ("id",           "Id"),
                champ("daty",         "Date"),
                champ("reference",    "R&eacute;f&eacute;rence"),
                champ("descri",       "Description"),
                champ("idmagasinlib", "Magasin"),
                champ("etatlib",      "&Eacute;tat"),
                champ("remarque",     "Remarque"));
    }

    @Override
    public AtahinaPageInsertMultiple getPageInsertMultiple(HttpServletRequest request,
                                                           HttpSession session,
                                                           int nombreLigne) throws Exception {
        AtahinaPageInsertMultiple pi = new AtahinaPageInsertMultiple(
                new BonCommandePoussin(), new BonCommandePoussinFille(), request, session, nombreLigne);

        pi.champs(
            champ("daty",      "Date").defautAujourdhui(),
            champ("reference", "Référence"),
            champ("descri",    "Description"),
            champ("idmagasin", "Magasin").liste(new magasin.Magasin(), "val", "id"),
            champ("remarque",  "Remarque"));

        pi.colonnes(
            champ("idproduit",   "Produit").liste(new produits.Ingredients(), "libelle", "id"),
            champ("idsouche",    "Souche").liste(new Souche(), "val", "id"),
            champ("fournisseur", "Fournisseur").liste(new Fournisseur(), "nom", "id"),
            champ("qte",         "Qté").defaut("0"),
            champ("pu",          "P.U.").defaut("0"));

        return pi.preparer();
    }

    public static String ongletDetail(HttpServletRequest request, HttpSession session, String idMere) throws Exception {
        return onglet(new BonCommandePoussinFilleLib(), request, session, PAGE_FICHE, idMere, "inc/bc-liste-detail", "idmere", "Aucune ligne trouv&eacute;e",
                champ("idproduitlib", "Produit"),
                champ("souchelibr", "Souche"),
                champ("qte", "Qt&eacute;"),
                champ("pu", "P.U."),
                champ("montant", "Montant"),
                champ("fournisseurlib", "Fournisseur"));
    }
}
