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
    private final List<AtahinaChamp> champsMere = new ArrayList<AtahinaChamp>();
    private final List<AtahinaChamp> colonnesFille = new ArrayList<AtahinaChamp>();

    public AtahinaPageInsertMultiple(ClassMAPTable mere, ClassMAPTable fille, HttpServletRequest request,
                                     HttpSession session, int nbLignes) throws Exception {
        super(mere, fille, request, nbLignes, AtahinaUtil.getUser(session));
        this.nbLignes = nbLignes;
        setLien(AtahinaUtil.getLien(session));
    }

    public int getNbLignes() { return nbLignes; }

    public AtahinaPageInsertMultiple champs(AtahinaChamp... defs) {
        champsMere.addAll(AtahinaFormulaire.liste(defs));
        return this;
    }

    public AtahinaPageInsertMultiple colonnes(AtahinaChamp... defs) {
        colonnesFille.addAll(AtahinaFormulaire.liste(defs));
        return this;
    }

    public AtahinaPageInsertMultiple preparer() throws Exception {
        AtahinaFormulaire.creerListes(getFormu(), champsMere);
        List<Liste> listesFille = new ArrayList<Liste>();
        for (AtahinaChamp d : colonnesFille) {
            if (d.aUneListe()) listesFille.add(d.creerListe());
        }
        if (!listesFille.isEmpty()) getFormufle().changerEnChamp(listesFille.toArray(new Liste[0]));

        preparerDataFormu();

        AtahinaFormulaire.appliquer(getFormu(), champsMere, getBase().getClass().getSimpleName());

        String[] ordre = new String[colonnesFille.size()];
        int i = 0;
        for (AtahinaChamp d : colonnesFille) {
            List<Champ> lignes = champsFille(d.nom);
            if (lignes.isEmpty()) {
                throw new IllegalArgumentException("Colonne '" + d.nom + "' inconnue dans "
                        + getFille().getClass().getSimpleName());
            }
            for (Champ c : lignes) {
                c.setLibelle(d.libelle);
                if (d.defaut != null) { c.setDefaut(d.defaut); c.setValeur(d.defaut); }
                if (d.lectureSeule)   c.setAutre("readonly");
                c.setVisible(!d.cache);
            }
            ordre[i++] = d.nom;
        }
        getFormufle().setColOrdre(ordre);

        getFormu().makeHtmlInsertTabIndex();
        getFormufle().makeHtmlInsertTableauIndex();
        return this;
    }

    private List<Champ> champsFille(String nom) {
        List<Champ> res = new ArrayList<Champ>();
        Champ[] tous = getFormufle().getListeChamp();
        if (tous == null) return res;
        Pattern p = Pattern.compile(Pattern.quote(nom) + "_\\d+", Pattern.CASE_INSENSITIVE);
        for (Champ c : tous) {
            if (c != null && c.getNom() != null && p.matcher(c.getNom()).matches()) res.add(c);
        }
        return res;
    }
}
