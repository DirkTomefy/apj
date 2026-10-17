package a_tahina.calendrier;

public abstract class CelluleCalendrierAbstraite {
    public abstract String getLibelleCourt();
    public abstract double getValeur();
    public abstract String getCssClass();
    public boolean isAffichee() { return true; }
}