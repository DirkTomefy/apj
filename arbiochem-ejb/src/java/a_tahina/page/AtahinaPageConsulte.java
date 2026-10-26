package a_tahina.page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

    public AtahinaPageConsulte libelle(String champ, String libelle) {
        AtahinaUtil.libelle(getChampByName(champ), libelle);
        return this;
    }

    public AtahinaPageConsulte cacher(String... champs) {
        for (String c : champs) AtahinaUtil.cacher(getChampByName(c));
        return this;
    }

   
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
