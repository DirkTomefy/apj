#!/usr/bin/env python3
"""
apj_gen.py - Generateur de code pour le framework APJ.

A partir d'un fichier JSON de specification, genere :
  * SQL      : drop + tables, sequences, fonctions GETSEQXxx, vues XXX_LIB, menus
  * Java     : beans  (ClassMAPTable / ClassEtat / ClassMere / ClassFille) + beans "Lib"
  * Java     : front helpers (liste / saisie / fiche) - calques sur BCmdInsertFH et BRInsertFH
  * JSP      : <entite>-liste.jsp, <entite>-saisie.jsp, <entite>-fiche.jsp

Usage :  python3 apj_gen.py spec.json
"""
import argparse, json, os, re, sys


# ============================================================================
# CONFIGURATION - CHEMINS DE SORTIE
# ============================================================================
SRC_JAVA_DIR  = "arbiochem-ejb/src/java"
WEB_PAGES_DIR = "arbiochem-war/web/pages"
SQL_DIR       = "sql"
# ============================================================================


# ---------------------------------------------------------------- types
JAVA = {"string": "String", "int": "int", "number": "double", "date": "java.sql.Date"}
SQL  = {"string": "VARCHAR2({size})", "int": "NUMBER(10)",
        "number": "NUMBER(15,2)", "date": "DATE"}

KINDS = ("map", "etat", "mere", "fille")

BASE_IMPORT = {
    "map":   "bean.ClassMAPTable",
    "etat":  "bean.ClassEtat",
    "mere":  "bean.ClassMere",
    "fille": "bean.ClassFille",
}
BASE_CLASS = {
    "map":   "ClassMAPTable",
    "etat":  "ClassEtat",
    "mere":  "ClassMere",
    "fille": "ClassFille",
}


def cap(s): return s[:1].upper() + s[1:]
def low(s): return s.lower()
def die(msg): sys.exit("ERREUR spec : " + msg)


class Field:
    def __init__(self, d, ent):
        self.name = low(d["name"])
        if not re.fullmatch(r"[a-z][a-z0-9_]*", self.name):
            die(f"{ent}: nom de champ invalide '{d['name']}'")
        self.type = d.get("type", "string")
        if self.type not in JAVA:
            die(f"{ent}.{self.name}: type '{self.type}' inconnu {list(JAVA)}")
        self.size     = d.get("size", 255)
        self.label    = d.get("label", cap(self.name))
        self.required = d.get("required", False)
        self.fk       = d.get("fk")
        self.search   = d.get("search", True)
        self.list     = d.get("list", True)
        if self.fk and self.type != "string":
            die(f"{ent}.{self.name}: une cle etrangere doit etre de type string")

    @property
    def jtype(self):    return JAVA[self.type]
    @property
    def sqltype(self):  return SQL[self.type].format(size=self.size)
    @property
    def libname(self):  return self.name + "lib"


class Entity:
    def __init__(self, d, spec):
        self.name   = d["name"]
        self.kind   = d.get("kind", "map")
        if self.kind not in KINDS:
            die(f"{self.name}: kind '{self.kind}' invalide {KINDS}")
        self.table  = d.get("table", self.name).upper()
        self.prefix = d["prefix"].upper()
        self.title  = d.get("title", self.name)
        self.mere   = d.get("mere")
        self.fille  = d.get("fille")
        self.nbligne = d.get("lignes", 5)
        self.fields = [Field(f, self.name) for f in d.get("fields", [])]

        if self.kind == "fille":
            if not self.mere:
                die(f"{self.name}: 'mere' obligatoire pour une fille")
            if not any(f.name == "idmere" for f in self.fields):
                self.fields.insert(0, Field(
                    {"name": "idmere", "label": "Mere", "list": False, "size": 30},
                    self.name))

        if self.kind == "mere" and not self.fille:
            die(f"{self.name}: 'fille' obligatoire pour une mere")
        self.spec = spec

    @property
    def has_etat(self): return self.kind in ("etat", "mere")
    @property
    def seq_func(self): return "GETSEQ" + self.name.upper()
    @property
    def seq_name(self): return "SEQ_" + self.name.upper()
    @property
    def view(self):     return self.table + "_LIB"
    @property
    def base(self):     return BASE_CLASS[self.kind]
    @property
    def base_import(self): return BASE_IMPORT[self.kind]
    @property
    def slug(self):
        return re.sub(r"(?<!^)(?=[A-Z])", "-", self.name).lower()
    @property
    def fks(self):      return [f for f in self.fields if f.fk]


class Spec:
    def __init__(self, d):
        self.pkg         = d["package"]
        self.fh          = d.get("fhPackage", self.pkg + ".fronthelper")
        self.jsp         = d.get("jspDir", "gen").strip("/")
        self.menu_parent = d.get("menuParent")
        self.role        = d.get("role", "resp")
        self.entities    = [Entity(e, self) for e in d["entities"]]
        self.by          = {e.name: e for e in self.entities}

        for e in self.entities:
            if e.kind == "mere" and e.fille not in self.by:
                die(f"{e.name}: fille '{e.fille}' introuvable")
            if e.kind == "fille" and e.mere not in self.by:
                die(f"{e.name}: mere '{e.mere}' introuvable")


# ---------------------------------------------------------------- SQL : DROP
def gen_drop_sql(sp):
    o = ["-- Drop genere par apj_gen.py", "-- Base : Oracle", ""]

    o.append("-- Vues")
    for e in sp.entities:
        o.append(f"DROP VIEW {e.view};")
    o.append("")

    o.append("-- Contraintes et index")
    for e in sp.entities:
        if e.kind == "fille":
            o.append(f"ALTER TABLE {e.table} DROP CONSTRAINT fk_{e.prefix.lower()}_mere;")
            o.append(f"DROP INDEX idx_{e.prefix.lower()}_mere;")
        for f in e.fks:
            o.append(f"ALTER TABLE {e.table} DROP CONSTRAINT fk_{e.prefix.lower()}_{f.name};")
            o.append(f"DROP INDEX idx_{e.prefix.lower()}_{f.name};")
        if e.has_etat:
            o.append(f"ALTER TABLE {e.table} DROP CONSTRAINT ck_{e.prefix.lower()}_etat;")
    o.append("")

    o.append("-- Tables")
    filles = [e for e in sp.entities if e.kind == "fille"]
    autres = [e for e in sp.entities if e.kind != "fille"]
    for e in filles + autres:
        o.append(f"DROP TABLE {e.table} CASCADE CONSTRAINTS;")
    o.append("")

    o.append("-- Sequences")
    for e in sp.entities:
        o.append(f"DROP SEQUENCE {e.seq_name};")
    o.append("")

    o.append("-- Fonctions")
    for e in sp.entities:
        o.append(f"DROP FUNCTION {e.seq_func};")
    o.append("")

    o.append("-- Menus")
    for e in sp.entities:
        if e.kind == "fille":
            continue
        mid     = "MENU_" + e.name.upper()
        mid_add = mid + "_ADD"
        o.append(f"DELETE FROM USERMENU WHERE IDMENU IN ('{mid}', '{mid_add}');")
        o.append(f"DELETE FROM MENUDYNAMIQUE WHERE ID IN ('{mid}', '{mid_add}');")
    o.append("")
    o.append("COMMIT;")
    return "\n".join(o) + "\n"


# ---------------------------------------------------------------- SQL : CREATE
def gen_sql(sp):
    o = ["-- Genere par apj_gen.py", "-- Base : Oracle", ""]

    for e in sp.entities:
        cols = ["    id VARCHAR2(30) PRIMARY KEY"]
        for f in e.fields:
            nn = " NOT NULL" if f.required else ""
            cols.append(f"    {f.name} {f.sqltype}{nn}")
        if e.has_etat:
            cols.append("    etat NUMBER(2) DEFAULT 1")
        o.append(f"CREATE TABLE {e.table} (\n" + ",\n".join(cols) + "\n);\n")

        o.append(f"CREATE SEQUENCE {e.seq_name} START WITH 1;\n")
        o.append(
            f"CREATE OR REPLACE FUNCTION {e.seq_func} RETURN NUMBER IS\n"
            f"    v NUMBER;\n"
            f"BEGIN\n"
            f"    SELECT {e.seq_name}.NEXTVAL INTO v FROM DUAL;\n"
            f"    RETURN v;\n"
            f"END;\n/\n"
        )
        if e.has_etat:
            o.append(f"ALTER TABLE {e.table} ADD CONSTRAINT ck_{e.prefix.lower()}_etat "
                     f"CHECK (etat IN (0,1,11,9));\n")

    o.append("-- Cles etrangeres")
    for e in sp.entities:
        if e.kind == "fille":
            m = sp.by[e.mere]
            o.append(f"ALTER TABLE {e.table} ADD CONSTRAINT fk_{e.prefix.lower()}_mere "
                     f"FOREIGN KEY (idmere) REFERENCES {m.table}(id);")
            o.append(f"CREATE INDEX idx_{e.prefix.lower()}_mere ON {e.table} (idmere);")
        for f in e.fks:
            o.append(f"ALTER TABLE {e.table} ADD CONSTRAINT fk_{e.prefix.lower()}_{f.name} "
                     f"FOREIGN KEY ({f.name}) "
                     f"REFERENCES {f.fk['table']}({f.fk.get('id','id')});")
            o.append(f"CREATE INDEX idx_{e.prefix.lower()}_{f.name} "
                     f"ON {e.table} ({f.name});")
    o.append("")

    o.append("-- Vues d'affichage")
    for e in sp.entities:
        sel   = ["t.id"] + [f"t.{f.name}" for f in e.fields]
        joins = []
        for i, f in enumerate(e.fks):
            a = f"j{i}"
            sel.append(f"{a}.{f.fk.get('label','nom')} AS {f.libname}")
            joins.append(f"LEFT JOIN {f.fk['table']} {a} "
                         f"ON {a}.{f.fk.get('id','id')} = t.{f.name}")
        if e.has_etat:
            sel.append("t.etat")
            sel.append("CASE t.etat "
                       "WHEN 0 THEN 'Annule' WHEN 1 THEN 'Cree' "
                       "WHEN 11 THEN 'Vise' WHEN 9 THEN 'Cloture' "
                       "ELSE 'Autre' END AS etatlib")

        sql = (f"CREATE OR REPLACE VIEW {e.view} AS\n"
               f"SELECT " + ",\n       ".join(sel) + "\n"
               f"FROM {e.table} t")
        if joins:
            sql += "\n" + "\n".join(joins)
        o.append(sql + ";\n/\n")

    o.append("-- Menus")
    root = sp.menu_parent
    for e in sp.entities:
        if e.kind == "fille":
            continue
        mid     = "MENU_" + e.name.upper()
        mid_add = mid + "_ADD"
        href_l  = f"module.jsp?but={sp.jsp}/{e.slug}-liste.jsp"
        href_s  = f"module.jsp?but={sp.jsp}/{e.slug}-saisie.jsp"

        if root:
            pere_l, niv_l = f"'{root}'", 2
            pere_s, niv_s = f"'{mid}'",  3
        else:
            pere_l, niv_l = "NULL", 1
            pere_s, niv_s = f"'{mid}'", 2

        o.append(f"INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE) VALUES "
                 f"('{mid}', '{e.title}', 'list', '{href_l}', 1, {niv_l}, {pere_l});")
        o.append(f"INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE) VALUES "
                 f"('{mid_add}', 'Saisir : {e.title}', 'add_box', '{href_s}', 1, {niv_s}, {pere_s});")

        o.append(f"INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT) VALUES "
                 f"(sequsermenu.nextval, '{mid}', '{sp.role}', 0);")
        o.append(f"INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT) VALUES "
                 f"(sequsermenu.nextval, '{mid_add}', '{sp.role}', 0);")

    o.append("\nCOMMIT;")
    return "\n".join(o) + "\n"


# ---------------------------------------------------------------- Java beans
def getset(f):
    n, c = f.name, cap(f.name)
    return (f"    public {f.jtype} get{c}() {{\n"
            f"        return {n};\n"
            f"    }}\n"
            f"    public void set{c}({f.jtype} v) {{\n"
            f"        this.{n} = v;\n"
            f"    }}\n")


def gen_bean(sp, e):
    cls = e.name
    o = [f"package {sp.pkg};", "",
         "import java.sql.Connection;",
         f"import {e.base_import};",
         "",
         f"public class {cls} extends {e.base} {{", ""]
    o.append("    private String id;")
    for f in e.fields:
        o.append(f"    private {f.jtype} {f.name};")
    o.append(f"\n    public {cls}() throws Exception {{\n"
             f"        super();\n"
             f"        setNomTable(\"{e.table.lower()}\");\n"
             f"    }}\n")

    o.append("    @Override\n    public String getTuppleID() {\n        return id;\n    }\n")
    o.append("    @Override\n    public String getAttributIDName() {\n        return \"id\";\n    }\n")

    if e.kind == "mere":
        o.append("    @Override\n"
                 "    public String getNomClasseFille() {\n"
                 f"        return \"{sp.pkg}.{e.fille}\";\n"
                 "    }\n")
        o.append("    @Override\n"
                 "    public String getLiaisonFille() {\n"
                 "        return \"idmere\";\n"
                 "    }\n")
    if e.kind == "fille":
        o.append("    @Override\n"
                 "    public String getNomClasseMere() {\n"
                 f"        return \"{sp.pkg}.{e.mere}\";\n"
                 "    }\n")
        o.append("    @Override\n"
                 "    public String getLiaisonMere() {\n"
                 "        return \"idmere\";\n"
                 "    }\n")

    o.append("    @Override\n"
             "    public void construirePK(Connection c) throws Exception {\n"
             f"        preparePk(\"{e.prefix}\", \"{e.seq_func}\");\n"
             "        setId(makePK(c));\n"
             "    }\n")

    o.append("    public String getId() {\n        return id;\n    }\n"
             "    public void setId(String v) {\n        this.id = v;\n    }\n")
    for f in e.fields:
        o.append(getset(f))
    o.append("}")
    return "\n".join(o) + "\n"


def gen_lib(sp, e):
    cls   = e.name + "Lib"
    extra = [(f.libname, "String") for f in e.fks]
    if e.has_etat:
        extra.append(("etatlib", "String"))

    o = [f"package {sp.pkg};", "",
         f"public class {cls} extends {e.name} {{", ""]
    for n, t in extra:
        o.append(f"    private {t} {n};")
    o.append(f"\n    public {cls}() throws Exception {{\n"
             f"        super();\n"
             f"        setNomTable(\"{e.view}\");\n"
             f"    }}\n")
    for n, t in extra:
        o.append(f"    public {t} get{cap(n)}() {{\n"
                 f"        return {n};\n"
                 f"    }}\n"
                 f"    public void set{cap(n)}({t} v) {{\n"
                 f"        this.{n} = v;\n"
                 f"    }}\n")
    o.append("}")
    return "\n".join(o) + "\n"


# ---------------------------------------------------------------- Front helpers
def display_cols(e):
    cols = ["id"]
    for f in e.fields:
        if not f.list:
            continue
        cols.append(f.libname if f.fk else f.name)
    if e.has_etat:
        cols.append("etatlib")
    return cols


def labels_for(e, cols):
    m = {"id": "Id", "etatlib": "Etat"}
    for f in e.fields:
        m[f.libname if f.fk else f.name] = f.label
    return [m.get(c, cap(c)) for c in cols]


def jarr(l):
    return "{ " + ", ".join(f'"{x}"' for x in l) + " }" if l else "{ }"


def gen_fh_list(sp, e):
    cls  = e.name + "ListFH"
    cols = display_cols(e)
    crt  = ["id"] + [(f.libname if f.fk else f.name)
                     for f in e.fields
                     if f.search and f.type == "string" and f.name != "idmere"]
    inter = [f.name for f in e.fields
             if f.search and f.type in ("date", "number") and f.list]
    page  = f"{sp.jsp}/{e.slug}-liste.jsp"

    return f'''package {sp.fh};

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import {sp.pkg}.{e.name}Lib;
import affichage.PageRecherche;

public class {cls} {{

    public static final String PAGE_ACTUEL = "{page}";

    public static void prepareGlobaleInf(PageRecherche pr, HttpSession session) {{
        pr.setTitre("Liste : {e.title}");
        pr.setUtilisateur((user.UserEJB) session.getValue("u"));
        pr.setLien((String) session.getValue("lien"));
        pr.setApres(PAGE_ACTUEL);
        pr.setNpp(20);
    }}

    public static PageRecherche getPageRechercheForList(HttpServletRequest request,
                                                        HttpSession session) throws Exception {{
        {e.name}Lib f = new {e.name}Lib();

        String[] listeCrt  = {jarr(crt)};
        String[] listeInt  = {jarr(inter)};
        String[] libEntete = {jarr(cols)};

        PageRecherche pr = new PageRecherche(f, request, listeCrt, listeInt, 3,
                libEntete, libEntete.length);
        prepareGlobaleInf(pr, session);
        pr.creerObjetPage(libEntete, null);

        pr.getTableau().setLibelleAffiche(new String[] {jarr(labels_for(e, cols))});

        pr.getTableau().setLien(new String[] {{ "module.jsp?but={sp.jsp}/{e.slug}-fiche.jsp" }});
        pr.getTableau().setColonneLien(new String[] {{ "id" }});

        return pr;
    }}
}}
'''


def _fk_liste_setup(field, idx_var="i", liste_var="listes"):
    """
    Genere la ligne Java pour ajouter une FK dans un Liste[].
    Si field.fk['class'] est defini -> new Classe() + import.
    Sinon -> new TypeObjet("TABLE").
    """
    t   = field.fk
    tbl = t["table"]
    lbl = t.get("label", "nom")
    idd = t.get("id", "id")
    cls = t.get("class")
    if cls:
        inst = f"new {cls}()"
    else:
        inst = f'new TypeObjet("{tbl}")'
    return (f'        {liste_var}[{idx_var}++] = '
            f'new Liste("{field.name}", {inst}, "{lbl}", "{idd}");')


def _fk_imports(fields, sp):
    """Imports Java necessaires pour les FK ayant un attribut class."""
    imports = []
    seen = set()
    for f in fields:
        if not f.fk or not f.fk.get("class"):
            continue
        cls = f.fk["class"]
        cls_pkg = cls.rsplit(".", 1)[0] if "." in cls else ""
        # pas d'import si la classe est dans le meme package que le FH
        if cls_pkg == sp.fh:
            continue
        if cls not in seen:
            imports.append(f"import {cls};")
            seen.add(cls)
    return imports


def gen_fh_saisie(sp, e):
    """
    Saisie calquee sur :
      - BCmdInsertFH (mere + fille -> PageInsertMultiple)
      - BRInsertFH   (simple -> PageInsert)
    """
    cls   = e.name + "SaisieFH"
    multi = e.kind == "mere"

    # ============================================================
    # CAS MERE : PageInsertMultiple
    # ============================================================
    if multi:
        fille = sp.by[e.fille]

        # ---- 1. libelles + defauts mere ----
        mere_lib = []
        for f in e.fields:
            if f.name == "idmere":
                continue
            mere_lib.append(f'        pi.getFormu().getChamp("{f.name}").setLibelle("{f.label}");')
        if e.has_etat:
            mere_lib.append('        if (pi.getFormu().getChamp("etat") != null) pi.getFormu().getChamp("etat").setVisible(false);')
        for f in e.fields:
            if f.type in ("number", "int"):
                mere_lib.append(f'        pi.getFormu().getChamp("{f.name}").setDefaut("0");')

        # ---- 2. listes deroulantes mere (var: i, listes) ----
        mere_fks = [f for f in e.fields if f.fk]
        if mere_fks:
            mere_liste = [f'        Liste[] listes = new Liste[{len(mere_fks)}];',
                          '        int i = 0;']
            for f in mere_fks:
                mere_liste.append(_fk_liste_setup(f, idx_var="i", liste_var="listes"))
            mere_liste.append('        pi.getFormu().changerEnChamp(listes);')
        else:
            mere_liste = ['        // (aucune FK mere)']

        # ---- 3. listes deroulantes fille (var: j, listesFille) ----
        fill_fks = [f for f in fille.fields if f.fk]
        if fill_fks:
            fill_liste = [f'        Liste[] listesFille = new Liste[{len(fill_fks)}];',
                          '        int j = 0;']
            for f in fill_fks:
                fill_liste.append(_fk_liste_setup(f, idx_var="j", liste_var="listesFille"))
            fill_liste.append('        pi.getFormufle().changerEnChamp(listesFille);')
        else:
            fill_liste = ['        // (aucune FK fille)']

        # ---- 4. libelles fille (_0) ----
        fill_lib = []
        for f in fille.fields:
            if f.name in ("id", "idmere"):
                continue
            fill_lib.append(f'        pi.getFormufle().getChamp("{f.name}_0").setLibelle("{f.label}");')
        fill_lib.append('        pi.getFormufle().getChampMulitple("id").setVisible(false);')
        fill_lib.append('        pi.getFormufle().getChampMulitple("idmere").setVisible(false);')

        # ---- 5. defauts fille ----
        num_fields = [f.name for f in fille.fields if f.type in ("number", "int")]
        if num_fields:
            fill_def = [f"        for (int k = 0; k < {e.nbligne}; k++) {{"]
            for nf in num_fields:
                fill_def.append(f'            pi.getFormufle().getChamp("{nf}_" + k).setDefaut("0");')
            fill_def.append("        }")
        else:
            fill_def = ["        // (aucune valeur par defaut)"]

        # ---- 6. ordre des colonnes filles ----
        ordre = [f.name for f in fille.fields if f.name not in ("id", "idmere")]
        ordre_str = ", ".join(f'"{x}"' for x in ordre)

        fk_imports = _fk_imports(e.fields + fille.fields, sp)
        fk_import_block = ("\n".join(fk_imports) + "\n") if fk_imports else ""

        return f'''package {sp.fh};

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import {sp.pkg}.{e.name};
import {sp.pkg}.{fille.name};
import affichage.Liste;
import affichage.PageInsertMultiple;
import bean.TypeObjet;
{fk_import_block}
public class {cls} {{

    public static final String PAGE_ACTUEL = "{sp.jsp}/{e.slug}-saisie.jsp";
    public static final String PAGE_LISTE  = "{sp.jsp}/{e.slug}-liste.jsp";
    public static final String PAGE_FICHE  = "{sp.jsp}/{e.slug}-fiche.jsp";

    public static PageInsertMultiple getPageInsert(HttpServletRequest request,
                                                   HttpSession session) throws Exception {{
        user.UserEJB u = (user.UserEJB) session.getValue("u");
        if (u == null) throw new Exception("Session expiree. Reconnectez-vous.");

        {e.name} mere = new {e.name}();
        {fille.name} fille = new {fille.name}();
        PageInsertMultiple pi = new PageInsertMultiple(mere, fille, request, {e.nbligne}, u);

        pi.setLien((String) session.getValue("lien"));
        pi.setTitre("Saisie : {e.title}");

        // ---- 1. libelles + defauts mere ----
{chr(10).join(mere_lib)}

        // ---- 2. listes deroulantes mere ----
{chr(10).join(mere_liste)}

        // ---- 3. listes deroulantes fille ----
{chr(10).join(fill_liste)}

        // ---- 4. preparation ----
        pi.preparerDataFormu();

        // ---- 5. libelles + defauts fille ----
{chr(10).join(fill_lib)}

{chr(10).join(fill_def)}

        // ---- 6. ordre des colonnes fille ----
        pi.getFormufle().setColOrdre(new String[] {{ {ordre_str} }});

        // ---- 7. HTML ----
        pi.getFormu().makeHtmlInsertTabIndex();
        pi.getFormufle().makeHtmlInsertTableauIndex();

        return pi;
    }}
}}
'''

    # ============================================================
    # CAS SIMPLE : PageInsert  (map / etat)  -- calque sur BRInsertFH
    # ============================================================
    lib_setup = []
    for f in e.fields:
        if f.name == "idmere":
            continue
        lib_setup.append(f'        setLabel(pi, "{f.name}", "{f.label}");')
    if e.has_etat:
        lib_setup.append('        hide(pi, "etat");')

    default_setup = []
    for f in e.fields:
        if f.type in ("number", "int"):
            default_setup.append(f'        setDefaut(pi, "{f.name}", "0");')
    if not default_setup:
        default_setup = ["        // (aucune valeur par defaut)"]

    fks = [f for f in e.fields if f.fk]
    if fks:
        liste_block = [f'        Liste[] listes = new Liste[{len(fks)}];',
                       '        int i = 0;']
        for f in fks:
            liste_block.append(_fk_liste_setup(f, idx_var="i", liste_var="listes"))
        liste_block.append('        pi.getFormu().changerEnChamp(listes);')
    else:
        liste_block = ['        // (aucune FK)']

    fk_imports = _fk_imports(e.fields, sp)
    fk_import_block = ("\n".join(fk_imports) + "\n") if fk_imports else ""

    return f'''package {sp.fh};

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import {sp.pkg}.{e.name};
import affichage.Champ;
import affichage.Liste;
import affichage.PageInsert;
import bean.TypeObjet;
{fk_import_block}
public class {cls} {{

    public static final String PAGE_ACTUEL = "{sp.jsp}/{e.slug}-saisie.jsp";
    public static final String PAGE_LISTE  = "{sp.jsp}/{e.slug}-liste.jsp";
    public static final String PAGE_FICHE  = "{sp.jsp}/{e.slug}-fiche.jsp";

    // -------- Helpers null-safe --------
    private static void setLabel(PageInsert pi, String nom, String libelle) {{
        Champ c = pi.getFormu().getChamp(nom);
        if (c != null) c.setLibelle(libelle);
    }}
    private static void setDefaut(PageInsert pi, String nom, String valeur) {{
        Champ c = pi.getFormu().getChamp(nom);
        if (c != null) c.setDefaut(valeur);
    }}
    private static void hide(PageInsert pi, String nom) {{
        Champ c = pi.getFormu().getChamp(nom);
        if (c != null) c.setVisible(false);
    }}

    // -------- Config --------
    private static void prepareLibelle(PageInsert pi) {{
{chr(10).join(lib_setup) if lib_setup else "        // (aucun libelle a definir)"}
    }}

    private static void prepareDefaultValues(PageInsert pi) {{
{chr(10).join(default_setup)}
    }}

    private static void changeChampIntoListe(PageInsert pi) throws Exception {{
{chr(10).join(liste_block)}
    }}

    // -------- Point d'entree --------
    public static PageInsert getPageInsert(HttpServletRequest request,
                                           HttpSession session) throws Exception {{
        user.UserEJB u = (user.UserEJB) session.getValue("u");
        if (u == null) throw new Exception("Session expiree. Reconnectez-vous.");

        {e.name} obj = new {e.name}();
        PageInsert pi = new PageInsert(obj, request, u);
        pi.setLien((String) session.getValue("lien"));
        pi.setTitre("Saisie : {e.title}");

        prepareLibelle(pi);
        prepareDefaultValues(pi);
        changeChampIntoListe(pi);

        pi.preparerDataFormu();
        pi.getFormu().makeHtmlInsertTabIndex();
        return pi;
    }}
}}
'''


def gen_fh_fiche(sp, e):
    cls = e.name + "FicheFH"
    return f'''package {sp.fh};

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import {sp.pkg}.{e.name}Lib;
import affichage.PageConsulte;

public class {cls} {{

    public static final String PAGE_LISTE  = "{sp.jsp}/{e.slug}-liste.jsp";
    public static final String PAGE_SAISIE = "{sp.jsp}/{e.slug}-saisie.jsp";

    public static PageConsulte getPageConsulte(HttpServletRequest request,
                                               HttpSession session) throws Exception {{
        {e.name}Lib f = new {e.name}Lib();
        PageConsulte pc = new PageConsulte(f, request, (user.UserEJB) session.getValue("u"));
        pc.setLien((String) session.getValue("lien"));
        pc.setTitre("Fiche : {e.title}");
        pc.setPageSaisie(PAGE_SAISIE);
        return pc;
    }}
}}
'''


# ---------------------------------------------------------------- JSP
JSP_HEAD = '<%@page contentType="text/html" pageEncoding="UTF-8"%>\n'


def gen_jsp_list(sp, e):
    return f'''<%@page import="affichage.PageRecherche"%>
<%@page import="{sp.fh}.{e.name}ListFH"%>
{JSP_HEAD}<% try {{
    PageRecherche pr = {e.name}ListFH.getPageRechercheForList(request, session);
%>
<div class="content-wrapper">
  <h1><%= pr.getTitre() %></h1>
  <p><a class="btn btn-primary" href="<%= pr.getLien() %>?but={sp.jsp}/{e.slug}-saisie.jsp">Nouveau</a></p>
  <form action="<%= pr.getLien() %>?but=<%= pr.getApres() %>" method="post" name="filtre">
    <% out.println(pr.getFormu().getHtmlEnsemble()); %>
  </form>
  <% out.println(pr.getTableauRecap().getHtml()); %>
  <% out.println(pr.getTableau().getHtml()); %>
  <% out.println(pr.getBasPage()); %>
</div>
<% }} catch (Exception e) {{
     out.println("<pre style='color:red'>");
     e.printStackTrace(new java.io.PrintWriter(out));
     out.println("</pre>");
   }} %>
'''


def gen_jsp_saisie(sp, e):
    if e.kind == "mere":
        fille  = sp.by[e.fille]
        typ    = "PageInsertMultiple"
        action = "apresMultiple.jsp"
        formu  = ('    <% out.println(pi.getFormu().getHtmlInsert()); %>\n'
                  '    <% out.println(pi.getFormufle().getHtmlTableauInsert()); %>\n')
        hidden = (
            f'    <input type="hidden" name="acte"        value="insert" />\n'
            f'    <input type="hidden" name="bute"        value="{sp.jsp}/{e.slug}-fiche.jsp" />\n'
            f'    <input type="hidden" name="classe"      value="{sp.pkg}.{e.name}" />\n'
            f'    <input type="hidden" name="classefille" value="{sp.pkg}.{fille.name}" />\n'
            f'    <input type="hidden" name="nombreLigne" value="{e.nbligne}" />\n'
            f'    <input type="hidden" name="colonneMere" value="idmere" />\n'
            f'    <input type="hidden" name="nomtable"    value="{fille.table}" />\n'
        )
    else:
        typ    = "PageInsert"
        action = "apresTarif.jsp"
        formu  = '    <% out.println(pi.getFormu().getHtmlInsert()); %>\n'
        hidden = (
            f'    <input type="hidden" name="acte"   value="insert" />\n'
            f'    <input type="hidden" name="bute"   value="{sp.jsp}/{e.slug}-liste.jsp" />\n'
            f'    <input type="hidden" name="classe" value="{sp.pkg}.{e.name}" />\n'
        )

    return f'''<%@page import="affichage.{typ}"%>
<%@page import="{sp.fh}.{e.name}SaisieFH"%>
{JSP_HEAD}<% try {{
    {typ} pi = {e.name}SaisieFH.getPageInsert(request, session);
%>
<div class="content-wrapper">
  <h1><%= pi.getTitre() %></h1>

  <form action="<%= pi.getLien() %>?but={action}" method="post" name="saisie">
{formu}
{hidden}  </form>
</div>
<% }} catch (Exception ex) {{
     out.println("<pre style='color:red'>");
     ex.printStackTrace(new java.io.PrintWriter(out));
     out.println("</pre>");
   }} %>
'''


def gen_jsp_fiche(sp, e):
    return f'''<%@page import="affichage.PageConsulte"%>
<%@page import="{sp.fh}.{e.name}FicheFH"%>
{JSP_HEAD}<% try {{
    PageConsulte pc = {e.name}FicheFH.getPageConsulte(request, session);
%>
<div class="content-wrapper">
  <h1><%= pc.getTitre() %></h1>
  <% out.println(pc.getHtml()); %>
  <p><a class="btn btn-secondary" href="<%= pc.getLien() %>?but={sp.jsp}/{e.slug}-liste.jsp">Retour a la liste</a></p>
</div>
<% }} catch (Exception e) {{
     out.println("<pre style='color:red'>");
     e.printStackTrace(new java.io.PrintWriter(out));
     out.println("</pre>");
   }} %>
'''


# ---------------------------------------------------------------- main
def write(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as fh:
        fh.write(content)
    print("  +", path)


def main():
    ap = argparse.ArgumentParser(
        description=__doc__,
        formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("spec")
    a = ap.parse_args()

    with open(a.spec, encoding="utf-8") as fh:
        sp = Spec(json.load(fh))

    pk  = sp.pkg.replace(".", "/")
    fk  = sp.fh.replace(".", "/")
    nom = sp.pkg.split('.')[-1]

    # 1. SQL : drop puis create
    write(f"{SQL_DIR}/{nom}_drop.sql", gen_drop_sql(sp))
    write(f"{SQL_DIR}/{nom}.sql",      gen_sql(sp))

    # 2. Java + JSP
    for e in sp.entities:
        write(f"{SRC_JAVA_DIR}/{pk}/{e.name}.java",    gen_bean(sp, e))
        write(f"{SRC_JAVA_DIR}/{pk}/{e.name}Lib.java", gen_lib(sp, e))

        if e.kind == "fille":
            continue

        write(f"{SRC_JAVA_DIR}/{fk}/{e.name}ListFH.java",   gen_fh_list(sp, e))
        write(f"{SRC_JAVA_DIR}/{fk}/{e.name}SaisieFH.java", gen_fh_saisie(sp, e))
        write(f"{SRC_JAVA_DIR}/{fk}/{e.name}FicheFH.java",  gen_fh_fiche(sp, e))

        write(f"{WEB_PAGES_DIR}/{sp.jsp}/{e.slug}-liste.jsp",  gen_jsp_list(sp, e))
        write(f"{WEB_PAGES_DIR}/{sp.jsp}/{e.slug}-saisie.jsp", gen_jsp_saisie(sp, e))
        write(f"{WEB_PAGES_DIR}/{sp.jsp}/{e.slug}-fiche.jsp",  gen_jsp_fiche(sp, e))


if __name__ == "__main__":
    main()