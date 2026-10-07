package a_tahina.configuration;

import bean.TypeObjet;
import java.sql.Connection;

public class SantePoussin extends TypeObjet {

    public SantePoussin() throws Exception {
        this.setNomTable("SANTEPOUSSIN");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("SP", "GETSEQSANTEPOUSSIN");
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