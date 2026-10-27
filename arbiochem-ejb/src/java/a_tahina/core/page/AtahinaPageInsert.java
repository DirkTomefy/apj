package a_tahina.core.page;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import affichage.PageInsert;
import bean.ClassMAPTable;

public class AtahinaPageInsert extends PageInsert {

    private final List<AtahinaChamp> champs = new ArrayList<AtahinaChamp>();

    public AtahinaPageInsert(ClassMAPTable objet, HttpServletRequest request, HttpSession session,
                             String titre) throws Exception {
        super(objet, request, AtahinaUtil.getUser(session));
        setLien(AtahinaUtil.getLien(session));
        if (titre != null) setTitre(titre);
    }

    public AtahinaPageInsert champs(AtahinaChamp... defs) {
        champs.addAll(AtahinaFormulaire.liste(defs));
        return this;
    }

    public AtahinaPageInsert preparer() throws Exception {
        AtahinaFormulaire.creerListes(getFormu(), champs);
        preparerDataFormu();
        AtahinaFormulaire.appliquer(getFormu(), champs, getBase().getClass().getSimpleName());
        getFormu().makeHtmlInsertTabIndex();
        return this;
    }
}
