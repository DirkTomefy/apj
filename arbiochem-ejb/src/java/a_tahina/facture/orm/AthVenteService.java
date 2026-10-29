package a_tahina.facture.orm;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import a_tahina.core.Sexe;
import a_tahina.core.TypeVente;
import a_tahina.core.erreur.AtahinaException;
import a_tahina.core.page.AtahinaUtil;
import a_tahina.prevision.simulation.ChargeurElevage;
import a_tahina.prevision.simulation.Elevage;
import a_tahina.prevision.simulation.StockException;
import a_tahina.prevision.simulation.Vente;
import bean.CGenUtil;

public final class AthVenteService {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private AthVenteService() {}

    public static void estimerPuPoulets(AthFacture f, List<AthFactureFille> lignes, Connection c) throws Exception {
        Elevage e = new ChargeurElevage().charger(c);
        List<Vente> ventes = new ArrayList<Vente>();
        LocalDate date = f.getDaty().toLocalDate();
        for (int i = 0; i < lignes.size(); i++) {
            AthFactureFille l = lignes.get(i);
            ventes.add(e.vente("~", String.valueOf(i), date, TypeVente.depuis(l.getTypevente()), Sexe.depuis(l.getSexe()), l.getQte(), 0));
        }
        e.simuler();
        for (int i = 0; i < lignes.size(); i++) {
            AthFactureFille l = lignes.get(i);
            if (TypeVente.depuis(l.getTypevente()) != TypeVente.POULET || l.getPu() > 0) continue;
            double pu = ventes.get(i).getPuStock();
            if (pu <= 0) {
                throw new AtahinaException("Aucun poulet " + Sexe.obligatoire(l.getSexe()).pluriel() + " disponible le " + date.format(FORMAT)
                        + " pour calculer le prix : saisissez le P.U.");
            }
            l.setPu(Math.round(pu * 100) / 100.0);
        }
    }

    public static void controlerStock(String idfacture, Connection c) throws Exception {
        AthFactureFilleLib[] lignes = (AthFactureFilleLib[]) CGenUtil.rechercher(new AthFactureFilleLib(), null, null, c,
                AtahinaUtil.egal("idmere", idfacture) + " and typevente is not null");
        if (lignes == null || lignes.length == 0) return;

        Elevage e = new ChargeurElevage().charger(c);
        e.simuler();
        Set<String> dejaManquantes = new HashSet<String>();
        for (Vente v : e.nonServies()) dejaManquantes.add(v.getIdfacture() + "|" + v.getId());

        for (AthFactureFilleLib l : lignes) ChargeurElevage.vente(e, l);
        e.simuler();

        StringBuilder erreurs = new StringBuilder();
        List<Vente> manques = new ArrayList<Vente>();
        for (Vente v : e.nonServies()) {
            if (dejaManquantes.contains(v.getIdfacture() + "|" + v.getId())) continue;
            manques.add(v);
            erreurs.append(idfacture.equals(v.getIdfacture()) ? "\n - " : "\n - vente deja visee " + v.getIdfacture() + " : ")
                   .append(v.getType() == TypeVente.OEUF ? "oeufs" : "poulets " + v.getSexe().pluriel())
                   .append(" le ").append(v.getDate().format(FORMAT))
                   .append(" : demande ").append(arrondi(v.getQte()))
                   .append(", disponible ").append(arrondi(v.getServi()));
        }
        if (!manques.isEmpty()) {
            throw new StockException("Stock insuffisant pour viser la facture " + idfacture + " :" + erreurs, manques);
        }
    }

    private static String arrondi(double v) {
        return String.format("%,.2f", v);
    }
}
