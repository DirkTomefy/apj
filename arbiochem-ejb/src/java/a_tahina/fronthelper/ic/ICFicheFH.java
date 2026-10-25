package a_tahina.fronthelper.ic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import a_tahina.ic.InsertionCompartimentLib;
import a_tahina.page.AtahinaPageConsulte;

public class ICFicheFH {

    public static final String PAGE_ACTUEL   = "a_tahina/v1/ic/insertioncompartiment-fiche.jsp";
    public static final String PAGE_LISTE    = "a_tahina/v1/ic/insertioncompartiment-liste.jsp";
    public static final String PAGE_BR_FICHE = "a_tahina/v1/br/bonreception-fiche.jsp";
    public static final String CLASSE        = "a_tahina.ic.InsertionCompartiment";

    public static AtahinaPageConsulte getPageConsulte(HttpServletRequest request,
                                                      HttpSession session) throws Exception {
        return new AtahinaPageConsulte(new InsertionCompartimentLib(), request, session,
                "Fiche insertion compartiment")
                .libelle("id", "Id")
                .libelle("daty", "Date")
                .libelle("reference", "R&eacute;f&eacute;rence")
                .libelle("idbonreceptionlib", "Bon de r&eacute;ception")
                .libelle("etat", "&Eacute;tat")
                .libelle("remarque", "Remarque")
                .cacher("idbonreception", "etatlib");
    }
}
