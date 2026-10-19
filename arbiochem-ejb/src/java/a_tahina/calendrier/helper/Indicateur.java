package a_tahina.calendrier.helper;

public class Indicateur {

    private final String  cle;
    private final String  libelle;
    private final boolean colorSigne;

    public Indicateur(String cle, String libelle, boolean colorSigne) {
        this.cle        = cle;
        this.libelle    = libelle;
        this.colorSigne = colorSigne;
    }

    public String  getCle()        { return cle; }
    public String  getLibelle()    { return libelle; }
    public boolean isColorSigne()  { return colorSigne; }
}