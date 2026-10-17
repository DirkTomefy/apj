package a_tahina.calendrier;

import java.time.LocalDate;

import javax.servlet.http.HttpServletRequest;

import bean.CGenUtil;
import bean.ClassMAPTable;

public abstract class CalendrierFromBean extends CalendrierAbstrait {

    private static final String DEFAULT_DATE_COL = "daty";

    private String dateSQLNameOverride = null;

    protected CalendrierFromBean(HttpServletRequest request) {
        super(request);
    }

    protected abstract Class<? extends ClassMAPTable> getBeanClass();

    protected abstract CelluleDate mapBean(Object bean) throws Exception;

    protected void chargerOptions() throws Exception {}

    
    protected String getDateSQLName() {
        return DEFAULT_DATE_COL;
    }

    /** Alternative : definit le nom de la colonne via setter. */
    public void setDateSQLName(String name) {
        this.dateSQLNameOverride = name;
    }

    protected String dateSQLName() {
        if (dateSQLNameOverride != null && !dateSQLNameOverride.isEmpty())
            return dateSQLNameOverride;
        String override = getDateSQLName();
        if (override != null && !override.isEmpty())
            return override;
        return DEFAULT_DATE_COL;
    }

  
    protected String getWhere() {
        String col = dateSQLName();
        return " AND " + col + " BETWEEN TO_DATE('" + debut + "','YYYY-MM-DD') "
             + " AND TO_DATE('" + fin + "','YYYY-MM-DD') ";
    }

    // ==================================================================
    // Chargement : fait tout
    // ==================================================================
    @Override
    protected final void charger() throws Exception {
        chargerOptions();
        chargerCellules();
    }

    private void chargerCellules() throws Exception {
        ClassMAPTable bean = getBeanClass().getDeclaredConstructor().newInstance();
        Object[] arr = CGenUtil.rechercher(bean, null, null, getWhere());
        if (arr == null) return;

        for (Object o : arr) {
            CelluleDate cd = mapBean(o);
            if (cd != null && cd.date != null) {
                ajouterCellule(cd.date, cd.cellule);
            }
        }
    }

    /** Les cellules sont deja remplies dans charger() -> rien a simuler. */
    @Override
    protected final void simuler() {}


    public static class CelluleDate {
        public LocalDate date;
        public CelluleCalendrierAbstraite cellule;
        public CelluleDate(LocalDate date, CelluleCalendrierAbstraite cellule) {
            this.date    = date;
            this.cellule = cellule;
        }
    }
}