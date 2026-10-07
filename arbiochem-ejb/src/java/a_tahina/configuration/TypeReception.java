package a_tahina.configuration;

import bean.TypeObjet;
import java.sql.Connection;

public class TypeReception extends TypeObjet {

    public TypeReception() throws Exception {
        this.setNomTable("TYPERECEPTION");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("TR", "GETSEQTYPERECEPTION");
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