package a_tahina.page;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import affichage.Champ;
import affichage.PageConsulte;
import bean.ClassMAPTable;

public class AtahinaPageConsulte extends PageConsulte {

    public AtahinaPageConsulte(ClassMAPTable objet, HttpServletRequest request, HttpSession session,
                               String titre) throws Exception {
        super(objet, request, AtahinaUtil.getUser(session));
        setTitre(titre);
        setLien(AtahinaUtil.getLien(session));
        setCanUploadFile(false);
    }

    public AtahinaPageConsulte champs(AtahinaChamp... defs) {
        Set<String> declares = new HashSet<String>();
        String[] ordre = new String[defs.length];
        for (int i = 0; i < defs.length; i++) {
            Champ c = getChampByName(defs[i].nom);
            if (c == null) {
                throw new IllegalArgumentException("Champ '" + defs[i].nom + "' inconnu dans la fiche " + getTitre());
            }
            c.setLibelle(defs[i].libelle);
            c.setVisible(true);
            declares.add(defs[i].nom.toLowerCase());
            ordre[i] = defs[i].nom;
        }
        if (getListeChamp() != null) {
            for (Champ c : getListeChamp()) {
                if (c != null && c.getNom() != null && !declares.contains(c.getNom().toLowerCase())) {
                    c.setVisible(false);
                }
            }
        }
        setOrdre(ordre);
        return this;
    }

    // ------------------------------------------------------------------
    // Boutons
    // ------------------------------------------------------------------
    public String lienActe(String acte, String id, String pageRetour, String classe) {
        return getLien() + "?but=apresTarif.jsp&acte=" + acte + "&id=" + id
                + "&bute=" + pageRetour + "&classe=" + classe;
    }

    public String getBoutonsWorkflow(String id, int etat, String pageRetour, String classe, String pageModif) {
        if (etat >= 11) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("<a class=\"btn btn-primary pull-right\" style=\"margin-right:10px\" href=\"")
          .append(lienActe("valider", id, pageRetour, classe)).append("\">Viser</a>\n");
        if (pageModif != null) {
            sb.append("<a class=\"btn btn-secondary pull-right\" style=\"margin-right:10px\" href=\"")
              .append(getLien()).append("?but=").append(pageModif).append("&id=").append(id)
              .append("&acte=update\">Modifier</a>\n");
        }
        sb.append("<a class=\"btn btn-danger pull-left\" href=\"")
          .append(lienActe("annuler", id, pageRetour, classe)).append("\">Annuler</a>\n");
        return sb.toString();
    }
}
