package a_tahina.configuration;

import bean.TypeObjet;
import java.sql.Connection;

public class LieuReception extends TypeObjet {

    public LieuReception() throws Exception {
        this.setNomTable("LIEURECEPTION");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("LR", "GETSEQLIEURECEPTION");
        this.setId(makePK(c));
    }

    @Override public String[] getMotCles()    { return new String[]{"id", "val"}; }
    @Override public String[] getValMotCles() { return new String[]{"id", "val"}; }
}