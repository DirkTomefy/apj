package a_tahina.page;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import affichage.Champ;
import affichage.Liste;
import bean.TypeObjet;
import user.UserEJB;

/**
 * Helpers communs aux pages Atahina (session, SQL sur, HTML sur, listes courantes).
 */
public final class AtahinaUtil {

    private static final Pattern IDENTIFIANT = Pattern.compile("[A-Za-z][A-Za-z0-9_]*");

    private AtahinaUtil() {}

    // ==================================================================
    // Session
    // ==================================================================
    public static UserEJB getUser(HttpSession session) throws Exception {
        UserEJB u = (UserEJB) session.getValue("u");
        if (u == null) throw new Exception("Session expirée. Reconnectez-vous.");
        return u;
    }

    public static String getLien(HttpSession session) {
        return (String) session.getValue("lien");
    }

    // ==================================================================
    // SQL : le framework concatene les clauses (setAWhere, rechercher...),
    // donc toute valeur venant de l'utilisateur passe par ici.
    // ==================================================================
    public static String quote(String valeur) {
        return "'" + (valeur == null ? "" : valeur.replace("'", "''")) + "'";
    }

    private static String colonne(String nom) {
        if (nom == null || !IDENTIFIANT.matcher(nom).matches())
            throw new IllegalArgumentException("Nom de colonne invalide : " + nom);
        return nom;
    }

    /** " and col = 'valeur'" */
    public static String egal(String col, String valeur) {
        return " and " + colonne(col) + " = " + quote(valeur);
    }

    /** " and col in ('a','b')" ; " and 1<0" si la liste est vide. */
    public static String dans(String col, String... valeurs) {
        if (valeurs == null || valeurs.length == 0) return " and 1<0";
        StringBuilder sb = new StringBuilder(" and ").append(colonne(col)).append(" in (");
        for (int i = 0; i < valeurs.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(quote(valeurs[i]));
        }
        return sb.append(")").toString();
    }

    /** " and col in (select colSelect from table where colFiltre = 'valeur')" */
    public static String dansSelect(String col, String table, String colSelect, String colFiltre, String valeur) {
        return " and " + colonne(col) + " in (select " + colonne(colSelect) + " from " + colonne(table)
                + " where " + colonne(colFiltre) + " = " + quote(valeur) + ")";
    }

    // ==================================================================
    // HTML
    // ==================================================================
    /** Encode une valeur de parametre d'URL. */
    public static String url(String s) {
        try {
            return java.net.URLEncoder.encode(s == null ? "" : s, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    /** Trace serveur + bloc d'erreur affichable (echappe) a la place de la page. */
    public static String erreurHtml(Exception e) {
        e.printStackTrace();
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return "<pre style='color:red'>" + esc(sw.toString()) + "</pre>";
    }

    // ==================================================================
    // Champs / listes
    // ==================================================================
    public static void libelle(Champ c, String libelle) { if (c != null) c.setLibelle(libelle); }
    public static void defaut(Champ c, String valeur)   { if (c != null) c.setDefaut(valeur); }
    public static void cacher(Champ c)                  { if (c != null) c.setVisible(false); }
    public static void lectureSeule(Champ c)            { if (c != null) c.setAutre("readonly"); }

    /** Liste deroulante des etats (1 cree, 11 vise, 0 annule, 9 cloture). */
    public static Liste listeEtat(String nomChamp) throws Exception {
        return new Liste(nomChamp, new TypeObjet("ETAT"), "desce", "id");
    }
}
