package a_tahina.prevision.orm;

import bean.ClassMAPTable;

public class SoucheParametre extends ClassMAPTable {

    private String id;
    private double nb_jours_eclosion;
    private double duree_vie_semaines;
    private double semaine_debut_ponte;
    private double taux_eclosion;
    private double ratio_femelle;

    public SoucheParametre() throws Exception {
        this.setNomTable("SOUCHE");
    }

    @Override public String getAttributIDName() { return "id"; }
    @Override public String getTuppleID()       { return this.getId(); }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public double getNb_jours_eclosion() { return nb_jours_eclosion; }
    public void setNb_jours_eclosion(double v) { this.nb_jours_eclosion = v; }
    public double getDuree_vie_semaines() { return duree_vie_semaines; }
    public void setDuree_vie_semaines(double v) { this.duree_vie_semaines = v; }
    public double getSemaine_debut_ponte() { return semaine_debut_ponte; }
    public void setSemaine_debut_ponte(double v) { this.semaine_debut_ponte = v; }
    public double getTaux_eclosion() { return taux_eclosion; }
    public void setTaux_eclosion(double v) { this.taux_eclosion = v; }
    public double getRatio_femelle() { return ratio_femelle; }
    public void setRatio_femelle(double v) { this.ratio_femelle = v; }
}
