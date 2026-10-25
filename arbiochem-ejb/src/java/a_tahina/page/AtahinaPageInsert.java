package a_tahina.page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import affichage.Liste;
import affichage.PageInsert;
import bean.ClassMAPTable;

/**
 * PageInsert (SAISIE simple) avec helpers null-safe sur les champs du formulaire.
 * Ordre conseille : listes(...) puis libelle/defaut/cacher (changerEnChamp remplace le champ),
 * puis {@link #preparer()}.
 */
public class AtahinaPageInsert extends PageInsert {

    public AtahinaPageInsert(ClassMAPTable objet, HttpServletRequest request, HttpSession session,
                             String titre) throws Exception {
        super(objet, request, AtahinaUtil.getUser(session));
        setLien(AtahinaUtil.getLien(session));
        if (titre != null) setTitre(titre);
    }

    public AtahinaPageInsert libelle(String champ, String libelle) {
        AtahinaUtil.libelle(getFormu().getChamp(champ), libelle);
        return this;
    }

    public AtahinaPageInsert defaut(String champ, String valeur) {
        AtahinaUtil.defaut(getFormu().getChamp(champ), valeur);
        return this;
    }

    public AtahinaPageInsert cacher(String... champs) {
        for (String c : champs) AtahinaUtil.cacher(getFormu().getChamp(c));
        return this;
    }

    public AtahinaPageInsert lectureSeule(String champ) {
        AtahinaUtil.lectureSeule(getFormu().getChamp(champ));
        return this;
    }

    public AtahinaPageInsert listes(Liste... listes) throws Exception {
        getFormu().changerEnChamp(listes);
        return this;
    }

    /** Charge les options des listes puis calcule les tabindex. */
    public AtahinaPageInsert preparer() throws Exception {
        preparerDataFormu();
        getFormu().makeHtmlInsertTabIndex();
        return this;
    }
}
