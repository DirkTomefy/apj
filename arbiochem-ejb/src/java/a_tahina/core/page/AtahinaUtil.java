package a_tahina.core.page;

import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import a_tahina.core.erreur.AtahinaException;
import user.UserEJB;

public final class AtahinaUtil {

    private static final Pattern IDENTIFIANT = Pattern.compile("[A-Za-z][A-Za-z0-9_]*");

    private AtahinaUtil() {}

    
    public static UserEJB getUser(HttpSession session) throws Exception {
        UserEJB u = (UserEJB) session.getValue("u");
        if (u == null) throw new AtahinaException("Session expirée. Reconnectez-vous.");
        return u;
    }

    public static String getLien(HttpSession session) {
        return (String) session.getValue("lien");
    }

    public static String quote(String valeur) {
        return "'" + (valeur == null ? "" : valeur.replace("'", "''")) + "'";
    }

    private static String colonne(String nom) {
        if (nom == null || !IDENTIFIANT.matcher(nom).matches())
            throw new IllegalArgumentException("Nom de colonne invalide : " + nom);
        return nom;
    }

    public static String egal(String col, String valeur) {
        return " and " + colonne(col) + " = " + quote(valeur);
    }

    public static String dans(String col, String... valeurs) {
        if (valeurs == null || valeurs.length == 0) return " and 1<0";
        StringBuilder sb = new StringBuilder(" and ").append(colonne(col)).append(" in (");
        for (int i = 0; i < valeurs.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(quote(valeurs[i]));
        }
        return sb.append(")").toString();
    }

    public static String dansSelect(String col, String table, String colSelect, String colFiltre, String valeur) {
        return " and " + colonne(col) + " in (select " + colonne(colSelect) + " from " + colonne(table)
                + " where " + colonne(colFiltre) + " = " + quote(valeur) + ")";
    }

   
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

    private static final String STYLE_ERREUR =
            "color:#c00;font-weight:bold;padding:8px;background:#fee;border-left:4px solid #c00;margin:8px 0;";

    public static String erreurHtml(Exception e) {
        AtahinaException metier = metier(e);
        if (metier != null) {
            return "<div style='" + STYLE_ERREUR + "'>" + esc(metier.getMessage()).replace("\n", "<br>") + "</div>";
        }
        e.printStackTrace();
        return "<div style='" + STYLE_ERREUR + "'>Erreur technique : " + esc(e.getMessage())
                + "<br><small>Le d&eacute;tail est dans le journal du serveur.</small></div>";
    }

    public static AtahinaException metier(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof AtahinaException) return (AtahinaException) t;
            if (t.getCause() == t) break;
        }
        return null;
    }
}
