package a_tahina.facture.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import a_tahina.core.page.AtahinaUtil;
import bean.CGenUtil;
import constante.ConstanteEtat;


public final class AthAvoirService {

    private static final double EPS = 0.005;

    private AthAvoirService() {}

    public static double plafond(double montantTtc, double pourcentageAvoir) {
        return arrondi(montantTtc * pourcentageAvoir / 100);
    }

    public static double montantDeduit(double plafond, double solde) {
        return arrondi(Math.max(0, Math.min(plafond, solde)));
    }

    
    public static double appliquer(AthFacture facture, String user, Connection c) throws Exception {
        AthFactureLib f = (AthFactureLib) new AthFactureLib().getById(facture.getId(), null, c);
        if (f == null) {
            throw new Exception("Facture introuvable : " + facture.getId());
        }
        if (f.getMontantttc() <= 0) {
            throw new Exception("Impossible de viser une facture sans montant.");
        }

        verrouillerClient(f.getIdclient(), c);  

        AthAvoirLib[] avoirs = (AthAvoirLib[]) CGenUtil.rechercher(new AthAvoirLib(), null, null, c,
                AtahinaUtil.egal("idclient", f.getIdclient())
                + " and etat = " + ConstanteEtat.getEtatValider()
                + " and reste > 0 order by daty, id");

        double solde = 0;
        if (avoirs != null) {
            for (AthAvoirLib a : avoirs) solde += a.getReste();
        }

        double restant = montantDeduit(f.getPlafondavoir(), solde);
        double total = 0;
        if (avoirs != null) {
            for (AthAvoirLib a : avoirs) {
                if (restant < EPS) break;
                double part = arrondi(Math.min(restant, a.getReste()));
                if (part < EPS) continue;

                AthAvoirUsage usage = new AthAvoirUsage();
                usage.setIdavoir(a.getId());
                usage.setIdfacture(f.getId());
                usage.setMontant(part);
                usage.setDaty(new java.sql.Date(System.currentTimeMillis()));
                usage.createObject(user, c);

                restant = arrondi(restant - part);
                total = arrondi(total + part);
            }
        }
        return total;
    }

    /** Annule les consommations d'une facture : le credit retourne aux avoirs. */
    public static void restituer(AthFacture facture, Connection c) throws Exception {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM ATH_AVOIR_USAGE WHERE idfacture = ?")) {
            ps.setString(1, facture.getId());
            ps.executeUpdate();
        }
    }

    private static void verrouillerClient(String idclient, Connection c) throws Exception {
        try (PreparedStatement ps = c.prepareStatement("SELECT id FROM ATH_CLIENT WHERE id = ? FOR UPDATE")) {
            ps.setString(1, idclient);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new Exception("Client introuvable : " + idclient);
            }
        }
    }

    private static double arrondi(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
