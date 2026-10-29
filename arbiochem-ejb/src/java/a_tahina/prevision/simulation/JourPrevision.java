package a_tahina.prevision.simulation;

import java.time.LocalDate;

import a_tahina.core.Sexe;

public class JourPrevision {

    final LocalDate daty;
    double naissancesFemelles;
    double naissancesMales;
    double pertesFemelles;
    double pertesMales;
    double oeufsPondus;
    double oeufsVendus;
    double stockOeufs;
    double femellesVendues;
    double malesVendus;
    double femellesVivantes;
    double malesVivants;
    double ca;
    double cout;

    JourPrevision(LocalDate daty) {
        this.daty = daty;
    }

    void naissance(Sexe sexe, double q) {
        if (sexe == Sexe.FEMELLE) naissancesFemelles += q; else naissancesMales += q;
    }

    void perte(Sexe sexe, double q) {
        if (sexe == Sexe.FEMELLE) pertesFemelles += q; else pertesMales += q;
    }

    void vente(Sexe sexe, double q) {
        if (sexe == Sexe.FEMELLE) femellesVendues += q; else malesVendus += q;
    }

    public LocalDate getDaty()            { return daty; }
    public double getNaissances()         { return naissancesFemelles + naissancesMales; }
    public double getNaissancesFemelles() { return naissancesFemelles; }
    public double getNaissancesMales()    { return naissancesMales; }
    public double getPertes()             { return pertesFemelles + pertesMales; }
    public double getPertesFemelles()     { return pertesFemelles; }
    public double getPertesMales()        { return pertesMales; }
    public double getOeufsPondus()        { return oeufsPondus; }
    public double getOeufsVendus()        { return oeufsVendus; }
    public double getStockOeufs()         { return stockOeufs; }
    public double getPouletsVendus()      { return femellesVendues + malesVendus; }
    public double getFemellesVendues()    { return femellesVendues; }
    public double getMalesVendus()        { return malesVendus; }
    public double getVivants()            { return femellesVivantes + malesVivants; }
    public double getFemellesVivantes()   { return femellesVivantes; }
    public double getMalesVivants()       { return malesVivants; }
    public double getCa()                 { return ca; }
    public double getCout()               { return cout; }
    public double getBenefice()           { return ca - cout; }
}
