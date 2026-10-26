package a_tahina.page;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import affichage.Champ;
import affichage.Liste;
import affichage.PageInsertMultiple;
import bean.ClassMAPTable;


public class AtahinaPageInsertMultiple extends PageInsertMultiple {

    private final int nbLignes;

    public AtahinaPageInsertMultiple(ClassMAPTable mere, ClassMAPTable fille, HttpServletRequest request,
                                     HttpSession session, int nbLignes) throws Exception {
        super(mere, fille, request, nbLignes, AtahinaUtil.getUser(session));
        this.nbLignes = nbLignes;
        setLien(AtahinaUtil.getLien(session));
    }

    public int getNbLignes() { return nbLignes; }

    // ------------------------------------------------------------------
    // Mere
    // ------------------------------------------------------------------
    public AtahinaPageInsertMultiple libelle(String champ, String libelle) {
        AtahinaUtil.libelle(getFormu().getChamp(champ), libelle);
        return this;
    }

    public AtahinaPageInsertMultiple defaut(String champ, String valeur) {
        AtahinaUtil.defaut(getFormu().getChamp(champ), valeur);
        return this;
    }

    public AtahinaPageInsertMultiple cacher(String... champs) {
        for (String c : champs) AtahinaUtil.cacher(getFormu().getChamp(c));
        return this;
    }

    public AtahinaPageInsertMultiple lectureSeule(String champ) {
        AtahinaUtil.lectureSeule(getFormu().getChamp(champ));
        return this;
    }

    public AtahinaPageInsertMultiple listes(Liste... listes) throws Exception {
        getFormu().changerEnChamp(listes);
        return this;
    }

    
    public List<Champ> champsFille(String nom) {
        List<Champ> res = new ArrayList<Champ>();
        Champ[] tous = getFormufle().getListeChamp();
        if (tous == null) return res;
        Pattern p = Pattern.compile(Pattern.quote(nom) + "_\\d+", Pattern.CASE_INSENSITIVE);
        for (Champ c : tous) {
            if (c != null && c.getNom() != null && p.matcher(c.getNom()).matches()) res.add(c);
        }
        return res;
    }

    public AtahinaPageInsertMultiple libelleFille(String champ, String libelle) {
        for (Champ c : champsFille(champ)) c.setLibelle(libelle);
        return this;
    }

    public AtahinaPageInsertMultiple defautFille(String champ, String valeur) {
        for (Champ c : champsFille(champ)) {
            c.setDefaut(valeur);
            c.setValeur(valeur);   // evite l'affichage de "null" apres preparerDataFormu
        }
        return this;
    }

    public AtahinaPageInsertMultiple cacherFille(String... champs) {
        for (String nom : champs)
            for (Champ c : champsFille(nom)) c.setVisible(false);
        return this;
    }

    public AtahinaPageInsertMultiple listesFille(Liste... listes) throws Exception {
        getFormufle().changerEnChamp(listes);
        return this;
    }

    // ------------------------------------------------------------------
    // Finalisation
    // ------------------------------------------------------------------
    /** Charge les options des listes (a appeler apres listes()/listesFille(), avant les defauts). */
    public AtahinaPageInsertMultiple charger() throws Exception {
        preparerDataFormu();
        return this;
    }

    /** Fixe l'ordre des colonnes de la fille et genere le HTML (a appeler en dernier). */
    public AtahinaPageInsertMultiple finaliser(String... ordreColonnesFille) throws Exception {
        if (ordreColonnesFille != null && ordreColonnesFille.length > 0)
            getFormufle().setColOrdre(ordreColonnesFille);
        getFormu().makeHtmlInsertTabIndex();
        getFormufle().makeHtmlInsertTableauIndex();
        return this;
    }
}
