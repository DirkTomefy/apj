package a_tahina.configuration;

import bean.TypeObjet;
import java.sql.Connection;

public class Souche extends TypeObjet {

    public Souche() {
        this.setNomTable("SOUCHE");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("SOU", "GETSEQSOUCHE");
        this.setId(makePK(c));
    }

    @Override
    public String[] getMotCles() { 
        return new String[]{"id", "val"};
    }

    @Override
    public String[] getValMotCles() {
        return new String[]{"id", "val"};
    }
}