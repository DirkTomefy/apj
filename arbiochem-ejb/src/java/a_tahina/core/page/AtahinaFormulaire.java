package a_tahina.core.page;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import affichage.Champ;
import affichage.Formulaire;
import affichage.Liste;

final class AtahinaFormulaire {

    private AtahinaFormulaire() {}

    static void creerListes(Formulaire f, List<AtahinaChampHelper> defs) throws Exception {
        List<Liste> listes = new ArrayList<Liste>();
        for (AtahinaChampHelper d : defs) {
            if (d.aUneListe()) listes.add(d.creerListe());
        }
        if (!listes.isEmpty()) f.changerEnChamp(listes.toArray(new Liste[0]));
    }

    static void appliquer(Formulaire f, List<AtahinaChampHelper> defs, String ecran) {
        Set<String> declares = new HashSet<String>();
        String[] ordre = new String[defs.size()];
        int i = 0;
        for (AtahinaChampHelper d : defs) {
            Champ c = f.getChamp(d.nom);
            if (c == null) {
                throw new IllegalArgumentException("Champ '" + d.nom + "' inconnu dans " + ecran);
            }
            c.setLibelle(d.libelle);
            if (d.defaut != null) c.setDefaut(d.defaut);
            if (d.lectureSeule)   c.setAutre("readonly");
            c.setVisible(!d.cache);
            declares.add(d.nom.toLowerCase());
            ordre[i++] = d.nom;
        }
        Champ[] tous = f.getListeChamp();
        if (tous != null) {
            for (Champ c : tous) {
                if (c != null && c.getNom() != null && !declares.contains(c.getNom().toLowerCase())) {
                    c.setVisible(false);
                }
            }
        }
        f.setOrdre(ordre);
    }

    static List<AtahinaChampHelper> liste(AtahinaChampHelper... defs) {
        List<AtahinaChampHelper> l = new ArrayList<AtahinaChampHelper>();
        for (AtahinaChampHelper d : defs) l.add(d);
        return l;
    }
}
