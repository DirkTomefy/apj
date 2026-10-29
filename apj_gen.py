#!/usr/bin/env python3
"""
apj_gen.py - generateur de code pour le framework APJ (projet a_tahina).

A partir d'un fichier JSON (voir spec.json et docs/15-generateur.md), genere pour un module :

  SQL   data/gen/<module>/<module>.sql        installation : sequences, fonctions GETSEQ, tables,
                                              contraintes, index, vues <TABLE>_LIB, menu
        data/gen/<module>/<module>-drop.sql   desinstallation
  ORM   a_tahina/<module>/orm/<Entite>.java et <Entite>Lib.java (vue)
  FH    a_tahina/<module>/fh/<Entite>FH.java  (extends GeneraticFH : liste, fiche, saisie)
  JSP   a_tahina/v1/<module>/<entite>/<entite>-liste|saisie|fiche.jsp (+ inc/ pour les lignes)

Usage :
  python3 apj_gen.py spec.json                    ecrit dans le projet
  python3 apj_gen.py spec.json --apercu           liste les fichiers sans rien ecrire
  python3 apj_gen.py spec.json --out /tmp/essai   ecrit sous un autre dossier
  python3 apj_gen.py spec.json --seulement sql    sql | java | jsp
  python3 apj_gen.py spec.json --force            ecrase aussi les fichiers modifies a la main

Un fichier genere contient la marque "@generated apj_gen.py". Pour proteger un fichier que tu
modifies a la main, supprime cette ligne : le generateur ne l'ecrasera plus (sauf --force).
"""
import argparse
import html.entities
import json
import os
import re
import sys
import unicodedata
import zlib

# ============================================================================
# CHEMINS DE SORTIE (relatifs a la racine du projet, ou a --out)
# ============================================================================
SRC_JAVA_DIR  = "arbiochem-ejb/src/java"
WEB_PAGES_DIR = "arbiochem-war/web/pages"
SQL_DIR       = "data/gen"
# ============================================================================

MARQUE = "@generated apj_gen.py"

TYPES = {  # nom francais -> (type java, type sql)
    "texte":  ("String",        "VARCHAR2({taille})"),
    "entier": ("int",           "NUMBER(10)"),
    "nombre": ("double",        "NUMBER(15,2)"),
    "date":   ("java.sql.Date", "DATE"),
}
ALIAS_TYPES = {"string": "texte", "int": "entier", "number": "nombre"}

TYPES_ENTITE = {  # type -> (classe de base, a un etat, description)
    "simple":    ("ClassMAPTable", False, "table simple"),
    "avec_etat": ("ClassEtat",     True,  "table avec etat (cree / vise / annule) et boutons Viser / Annuler"),
    "maitre":    ("ClassMere",     True,  "entete d'un document avec des lignes (donne 'detail')"),
    "detail":    ("ClassFille",    False, "lignes d'un document (donne 'maitre')"),
}

RESERVES_ORACLE = set("""ACCESS ADD ALL ALTER AND ANY AS ASC AUDIT BETWEEN BY CHAR CHECK CLUSTER COLUMN COMMENT COMPRESS
CONNECT CREATE CURRENT DATE DECIMAL DEFAULT DELETE DESC DISTINCT DROP ELSE EXCLUSIVE EXISTS FILE FLOAT FOR FROM GRANT
GROUP HAVING IDENTIFIED IMMEDIATE IN INCREMENT INDEX INITIAL INSERT INTEGER INTERSECT INTO IS LEVEL LIKE LOCK LONG
MAXEXTENTS MINUS MLSLABEL MODE MODIFY NOAUDIT NOCOMPRESS NOT NOWAIT NULL NUMBER OF OFFLINE ON ONLINE OPTION OR ORDER
PCTFREE PRIOR PRIVILEGES PUBLIC RAW RENAME RESOURCE REVOKE ROW ROWID ROWNUM ROWS SELECT SESSION SET SHARE SIZE SMALLINT
START SUCCESSFUL SYNONYM SYSDATE TABLE THEN TO TRIGGER UID UNION UNIQUE UPDATE USER VALIDATE VALUES VARCHAR VARCHAR2
VIEW WHENEVER WHERE WITH""".split())
RESERVES_JAVA = set("""abstract boolean break byte case catch char class const continue default do double else enum extends
final finally float for goto if implements import instanceof int interface long native new package private protected
public return short static strictfp super switch synchronized this throw throws transient try void volatile while""".split())
RESERVES_APJ = {"mode", "nomtable", "fille", "mere", "iduser", "rolevalidation", "ishierarchique", "tuppleid"}


# ============================================================================
# Outils
# ============================================================================
def die(msg):
    sys.exit("ERREUR spec.json : " + msg)


def cap(s):
    return s[:1].upper() + s[1:]


def ascii_fold(s):
    """Sans accents (messages d'erreur Java, commentaires SQL)."""
    return "".join(c for c in unicodedata.normalize("NFKD", s) if not unicodedata.combining(c))


def entites_html(s):
    """Accents -> &eacute; ... (labels HTML, libelles de menu : independant de l'encodage)."""
    out = []
    for c in s:
        if ord(c) < 128:
            out.append(c)
        else:
            nom = html.entities.codepoint2name.get(ord(c))
            out.append("&%s;" % nom if nom else "&#%d;" % ord(c))
    return "".join(out)


def jstr(s):
    return s.replace("\\", "\\\\").replace('"', '\\"')


def sqlstr(s):
    return s.replace("'", "''")


def kebab(nom):
    return re.sub(r"(?<!^)(?=[A-Z])", "-", nom).lower()


def mots(nom):
    """BonSortie -> 'bon sortie'."""
    return re.sub(r"(?<!^)(?=[A-Z])", " ", nom).lower()


def snake_upper(nom):
    return re.sub(r"(?<!^)(?=[A-Z])", "_", nom).upper()


def ident(base, limite=30):
    """Nom d'objet Oracle <= 30 caracteres, stable (coupe + suffixe de hachage)."""
    base = base.lower()
    if len(base) <= limite:
        return base
    return base[:limite - 3] + "%03x" % (zlib.crc32(base.encode()) & 0xfff)


def lire(d, *cles, defaut=None):
    """Premiere cle presente (permet des alias)."""
    for c in cles:
        if c in d:
            return d[c]
    return defaut


# ============================================================================
# Modele
# ============================================================================
class Champ:
    def __init__(self, nom, d, ent):
        self.entite = ent
        self.nom = nom.lower()
        ctx = "%s.%s" % (ent.nom, nom)
        if not re.fullmatch(r"[a-z][a-z0-9]*", self.nom):
            die("%s : nom de champ invalide. Lettres minuscules et chiffres seulement, sans underscore "
                "(le nom est aussi celui de la colonne et de l'attribut Java). Ex: 'qtemax' et non 'qte_max'." % ctx)
        if self.nom.upper() in RESERVES_ORACLE:
            die("%s : '%s' est un mot reserve Oracle. Choisis un autre nom (ex: 'daty' au lieu de 'date')." % (ctx, self.nom))
        if self.nom in RESERVES_JAVA or self.nom in RESERVES_APJ:
            die("%s : '%s' est reserve (Java ou framework APJ). Choisis un autre nom." % (ctx, self.nom))
        if self.nom in ("id", "etat", "idmere"):
            die("%s : '%s' est ajoute automatiquement, ne le declare pas." % (ctx, self.nom))

        t = lire(d, "type", defaut="texte")
        t = ALIAS_TYPES.get(t, t)
        if t not in TYPES:
            die("%s : type '%s' inconnu. Types : %s" % (ctx, t, ", ".join(TYPES)))
        self.type = t
        self.taille = lire(d, "taille", "size", defaut=255)
        self.libelle = lire(d, "libelle", "label", defaut=cap(self.nom))
        self.obligatoire = bool(lire(d, "obligatoire", "required", defaut=False))
        self.defaut = lire(d, "defaut", "default")
        self.min = d.get("min")
        self.max = d.get("max")
        self.ref = lire(d, "reference")            # nom d'entite ou {table, classe, id, affiche}
        self.dico = lire(d, "dictionnaire")        # table id / val / desce
        self.affiche = d.get("affiche")            # champ du libelle (reference interne)
        self.dans_liste = lire(d, "liste", defaut=True)
        self.dans_fiche = lire(d, "fiche", defaut=True)
        self.dans_saisie = lire(d, "saisie", defaut=True)
        self.recherche = d.get("recherche")        # None = automatique
        self.intervalle = d.get("intervalle")

        if self.ref and self.dico:
            die("%s : 'reference' et 'dictionnaire' sont exclusifs." % ctx)
        if (self.ref or self.dico) and self.type != "texte":
            die("%s : une reference / un dictionnaire est de type texte (c'est l'identifiant)." % ctx)
        if (self.min is not None or self.max is not None) and self.type not in ("entier", "nombre"):
            die("%s : 'min' / 'max' seulement pour entier ou nombre." % ctx)
        if self.defaut == "aujourdhui" and self.type != "date":
            die("%s : defaut 'aujourdhui' seulement pour une date." % ctx)

    @property
    def est_lien(self):
        return bool(self.ref or self.dico)

    @property
    def jtype(self):
        return TYPES[self.type][0]

    @property
    def sqltype(self):
        if self.est_lien:
            taille = self.taille if self.taille != 255 else (30 if isinstance(self.ref, str) else 50)
            return "VARCHAR2(%d)" % taille
        return TYPES[self.type][1].format(taille=self.taille)

    @property
    def nomlib(self):
        return self.nom + "lib"

    @property
    def colonne_affichee(self):
        return self.nomlib if self.est_lien else self.nom


class Entite:
    def __init__(self, d, spec):
        self.spec = spec
        self.nom = lire(d, "nom", "name")
        if not self.nom or not re.fullmatch(r"[A-Z][A-Za-z0-9]*", self.nom):
            die("entite : 'nom' obligatoire, en MajusculeInitiale sans espace (ex: BonSortie). Recu : %r" % self.nom)
        self.type = lire(d, "type", "kind", defaut="simple")
        if self.type == "map":
            self.type = "simple"
        if self.type not in TYPES_ENTITE:
            die("%s : type '%s' inconnu. Types : %s" % (self.nom, self.type, ", ".join(TYPES_ENTITE)))
        self.table = lire(d, "table", defaut="%s_%s" % (spec.module.upper(), snake_upper(self.nom))).upper()
        if not re.fullmatch(r"[A-Z][A-Z0-9_]*", self.table):
            die("%s : nom de table invalide %r" % (self.nom, self.table))
        if len(self.table) > 24:
            die("%s : la table '%s' fait %d caracteres ; maximum 24 (Oracle limite a 30 les noms GETSEQ%s, "
                "SEQ_%s, %s_LIB). Donne un 'table' plus court." % (self.nom, self.table, len(self.table), self.table, self.table, self.table))
        self.prefixe = lire(d, "prefixe", "prefix", defaut=self.nom[:3]).upper()
        if not re.fullmatch(r"[A-Z]{1,8}", self.prefixe):
            die("%s : 'prefixe' = 1 a 8 lettres majuscules (debut des identifiants). Recu : %r" % (self.nom, self.prefixe))
        self.titre = lire(d, "titre", "title", defaut=self.nom + "s")
        self.singulier = lire(d, "singulier", defaut=mots(self.nom))
        self.icone = d.get("icone", "list")
        self.menu = d.get("menu", True)
        self.detail_nom = lire(d, "detail", "fille")
        self.maitre_nom = lire(d, "maitre", "mere")
        self.lignes = lire(d, "lignes", defaut=10)

        brut = lire(d, "champs", "fields", defaut={})
        if isinstance(brut, list):      # ancienne forme : [{"name": ...}]
            brut = {c.get("name") or c.get("nom"): c for c in brut}
        if not isinstance(brut, dict):
            die("%s : 'champs' doit etre un objet {\"nomchamp\": {...}}" % self.nom)
        self.champs = [Champ(n, c if isinstance(c, dict) else {"libelle": c}, self) for n, c in brut.items()]
        noms = [c.nom for c in self.champs]
        for n in set(noms):
            if noms.count(n) > 1:
                die("%s : champ '%s' declare deux fois." % (self.nom, n))
        lib = set(c.nomlib for c in self.champs if c.est_lien)
        for c in self.champs:
            if c.nom in lib:
                die("%s : '%s' entre en conflit avec la colonne de libelle d'une reference." % (self.nom, c.nom))

        if self.type == "maitre" and not self.detail_nom:
            die("%s : un 'maitre' doit nommer son 'detail'." % self.nom)
        if self.type == "detail" and not self.maitre_nom:
            die("%s : un 'detail' doit nommer son 'maitre'." % self.nom)
        if self.type != "maitre" and self.detail_nom:
            die("%s : 'detail' n'a de sens que pour un 'maitre'." % self.nom)
        if self.type != "detail" and self.maitre_nom:
            die("%s : 'maitre' n'a de sens que pour un 'detail'." % self.nom)

    @property
    def base(self):
        return TYPES_ENTITE[self.type][0]

    @property
    def a_etat(self):
        return TYPES_ENTITE[self.type][1]

    @property
    def seq(self):
        return "SEQ_" + self.table

    @property
    def fonction(self):
        return "GETSEQ" + self.table

    @property
    def vue(self):
        return self.table + "_LIB"

    @property
    def slug(self):
        return kebab(self.nom)

    @property
    def maitre(self):
        return self.spec.par_nom[self.maitre_nom] if self.maitre_nom else None

    @property
    def detail(self):
        return self.spec.par_nom[self.detail_nom] if self.detail_nom else None

    @property
    def liens(self):
        return [c for c in self.champs if c.est_lien]

    def cible(self, champ):
        """Entite visee par une reference interne, ou None."""
        return self.spec.par_nom.get(champ.ref) if isinstance(champ.ref, str) else None


class Spec:
    def __init__(self, d):
        if "entities" in d or "fhPackage" in d:
            die("ancienne syntaxe detectee (entities / fhPackage). Nouvelle syntaxe : voir spec.json et "
                "docs/15-generateur.md (module, menu, entites, champs).")
        self.module = d.get("module")
        if not self.module or not re.fullmatch(r"[a-z][a-z0-9]*", self.module):
            die("'module' obligatoire : un mot en minuscules (ex: \"stock\"). Il donne le package a_tahina.<module>.")
        self.racine = d.get("package", "a_tahina")
        self.pages = d.get("pages", "a_tahina/v1/%s" % self.module).strip("/")
        m = d.get("menu") or {}
        self.menu_titre = m.get("titre")
        self.menu_icone = m.get("icone", "folder")
        self.role = m.get("role", "resp")
        self.menu_id = m.get("id", "MENU" + self.module.upper())
        self.menu_rang = m.get("rang", 100)

        self.entites = [Entite(e, self) for e in d.get("entites", [])]
        if not self.entites:
            die("aucune entite dans 'entites'.")
        self.par_nom = {}
        for e in self.entites:
            if e.nom in self.par_nom:
                die("entite '%s' declaree deux fois." % e.nom)
            self.par_nom[e.nom] = e
        for attr, libelle in (("table", "table"), ("prefixe", "prefixe"), ("slug", "dossier de pages")):
            vus = {}
            for e in self.entites:
                v = getattr(e, attr)
                if v in vus:
                    die("%s identique pour %s et %s (%s) : ils doivent etre differents." % (libelle, vus[v], e.nom, v))
                vus[v] = e.nom
        self.valider_liens()

    @property
    def pkg(self):
        return "%s.%s" % (self.racine, self.module)

    @property
    def orm_pkg(self):
        return self.pkg + ".orm"

    @property
    def fh_pkg(self):
        return self.pkg + ".fh"

    def valider_liens(self):
        for e in self.entites:
            if e.type == "maitre":
                d = self.par_nom.get(e.detail_nom)
                if not d or d.type != "detail" or d.maitre_nom != e.nom:
                    die("%s : le 'detail' %r doit exister, etre de type 'detail' avec \"maitre\": \"%s\"." % (e.nom, e.detail_nom, e.nom))
            if e.type == "detail":
                m = self.par_nom.get(e.maitre_nom)
                if not m or m.type != "maitre" or m.detail_nom != e.nom:
                    die("%s : le 'maitre' %r doit exister, etre de type 'maitre' avec \"detail\": \"%s\"." % (e.nom, e.maitre_nom, e.nom))
            for c in e.liens:
                if c.dico:
                    if not re.fullmatch(r"[A-Za-z][A-Za-z0-9_]*", c.dico):
                        die("%s.%s : nom de dictionnaire invalide %r" % (e.nom, c.nom, c.dico))
                elif isinstance(c.ref, str):
                    cible = self.par_nom.get(c.ref)
                    if not cible:
                        die("%s.%s : reference a l'entite '%s' introuvable (entites : %s)." % (e.nom, c.nom, c.ref, ", ".join(self.par_nom)))
                    if cible is e:
                        die("%s.%s : reference a soi-meme non supportee." % (e.nom, c.nom))
                    if cible.type == "detail":
                        die("%s.%s : on ne reference pas les lignes d'un document." % (e.nom, c.nom))
                    if not c.affiche:
                        premier = next((x for x in cible.champs if x.type == "texte" and not x.est_lien), None)
                        if not premier:
                            die("%s.%s : precise \"affiche\" (champ de %s a montrer dans la liste)." % (e.nom, c.nom, cible.nom))
                        c.affiche = premier.nom
                    if c.affiche not in [x.nom for x in cible.champs]:
                        die("%s.%s : 'affiche' = %r n'est pas un champ de %s." % (e.nom, c.nom, c.affiche, cible.nom))
                elif isinstance(c.ref, dict):
                    for cle in ("table", "classe", "affiche"):
                        if cle not in c.ref:
                            die("%s.%s : reference externe incomplete, il manque %r (table, classe, affiche, id)." % (e.nom, c.nom, cle))
                else:
                    die("%s.%s : 'reference' = nom d'entite ou objet {table, classe, affiche}." % (e.nom, c.nom))
        self.ordre()

    def ordre(self):
        """Ordre de creation : une table apres celles qu'elle reference (maitre avant detail)."""
        dep = {e.nom: set() for e in self.entites}
        for e in self.entites:
            if e.type == "detail":
                dep[e.nom].add(e.maitre_nom)
            for c in e.liens:
                if isinstance(c.ref, str):
                    dep[e.nom].add(c.ref)
        res, etat = [], {}

        def visite(n, chemin):
            if etat.get(n) == 2:
                return
            if etat.get(n) == 1:
                die("references circulaires : %s" % " -> ".join(chemin + [n]))
            etat[n] = 1
            for m in sorted(dep[n]):
                visite(m, chemin + [n])
            etat[n] = 2
            res.append(self.par_nom[n])
        for e in self.entites:
            visite(e.nom, [])
        self.creation = res


# ============================================================================
# Colonnes (communes au SQL, a l'ORM, aux FH)
# ============================================================================
def colonnes_sql(e):
    """[(nom, type SQL, contraintes)] dans l'ordre de la table."""
    cols = []
    if e.type == "detail":
        cols.append(("idmere", "VARCHAR2(30)", "NOT NULL"))
    for c in e.champs:
        opt = ""
        if c.obligatoire:
            opt += " NOT NULL"
        if c.defaut is not None and c.type != "date":
            v = c.defaut
            opt = (" DEFAULT %s" % (v if c.type in ("entier", "nombre") else "'%s'" % sqlstr(str(v)))) + opt
        cols.append((c.nom, c.sqltype, opt.strip()))
    if e.a_etat:
        cols.append(("etat", "NUMBER(2)", "DEFAULT 1"))
    return cols


# ============================================================================
# SQL
# ============================================================================
def en_tete_sql(sp, titre):
    return ("-- %s\n-- %s\nSET DEFINE OFF\nSET SQLBLANKLINES ON\nSET SERVEROUTPUT ON\nSET FEEDBACK OFF\n"
            % (MARQUE, ascii_fold(titre)))


def externes(sp):
    """Tables de l'application de base referencees : {table: (colonne id, colonne affichee)}."""
    res = {}
    if any(e.a_etat for e in sp.entites):
        res["ETAT"] = ("id", "desce")      # liste des etats des ecrans de liste
    for e in sp.entites:
        for c in e.liens:
            if c.dico:
                res[c.dico.upper()] = ("id", "val")
            elif isinstance(c.ref, dict):
                res[c.ref["table"].upper()] = (c.ref.get("id", "id"), c.ref["affiche"])
    return res


def menus(sp):
    """[(id, libelle, icone, href, rang, niveau, pere)] ; le menu racine est facultatif."""
    res, racine = [], sp.menu_titre is not None
    vis = [e for e in sp.entites if e.type != "detail" and e.menu]
    if racine:
        premier = vis[0]
        res.append((sp.menu_id, sp.menu_titre, sp.menu_icone,
                    "module.jsp?but=%s/%s/%s-liste.jsp" % (sp.pages, premier.slug, premier.slug), sp.menu_rang, 1, None))
    for i, e in enumerate(vis, 1):
        mid = "%s_%s" % (sp.menu_id, e.nom.upper())
        niv = 2 if racine else 1
        res.append((mid, e.titre, e.icone, "module.jsp?but=%s/%s/%s-liste.jsp" % (sp.pages, e.slug, e.slug),
                    i if racine else sp.menu_rang + i - 1, niv, sp.menu_id if racine else None))
        res.append((mid + "_ADD", "Saisir : " + e.singulier, "add_box",
                    "module.jsp?but=%s/%s/%s-saisie.jsp" % (sp.pages, e.slug, e.slug), 1, niv + 1, mid))
        res.append((mid + "_LIST", "Liste : " + e.titre, "list",
                    "module.jsp?but=%s/%s/%s-liste.jsp" % (sp.pages, e.slug, e.slug), 2, niv + 1, mid))
    for m in res:
        if len(m[0]) > 50:
            die("identifiant de menu trop long (%d > 50) : %s. Raccourcis 'module' ou le nom de l'entite." % (len(m[0]), m[0]))
        if len(entites_html(m[1])) > 50:
            die("libelle de menu trop long (> 50 caracteres) : %r. Raccourcis 'titre' / 'singulier'." % m[1])
    return res


def gen_sql(sp, fichier_spec):
    o = [en_tete_sql(sp, "Installation du module %s. Genere depuis %s - ne pas modifier a la main." % (sp.module, fichier_spec)),
         "WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK", ""]

    # ---- prerequis
    ext = externes(sp)
    o.append("-- ============================================================================")
    o.append("-- 0. PREREQUIS")
    o.append("-- ============================================================================")
    o.append("DECLARE\n    n NUMBER;\n")
    o.append("    PROCEDURE exiger(p_objet VARCHAR2, p_colonnes VARCHAR2) IS\n    BEGIN\n"
             "        EXECUTE IMMEDIATE 'SELECT COUNT(*) FROM (SELECT ' || p_colonnes || ' FROM ' || p_objet || ' WHERE 1 = 0)' INTO n;\n"
             "    EXCEPTION\n        WHEN OTHERS THEN\n"
             "            RAISE_APPLICATION_ERROR(-20001, 'Prerequis manquant : ' || p_objet || ' (' || p_colonnes || ') -> ' || SQLERRM);\n"
             "    END;\nBEGIN")
    for t, (idc, aff) in sorted(ext.items()):
        o.append("    exiger('%s', '%s, %s');" % (t, idc, aff))
    if sp.menu_titre is not None or any(e.menu and e.type != "detail" for e in sp.entites):
        o.append("    exiger('MENUDYNAMIQUE', 'id, libelle, icone, href, rang, niveau, id_pere');")
        o.append("    exiger('USERMENU', 'id, idmenu, idrole, interdit');")
        o.append("    SELECT COUNT(*) INTO n FROM user_sequences WHERE sequence_name = 'SEQUSERMENU';\n"
                 "    IF n = 0 THEN\n        RAISE_APPLICATION_ERROR(-20001, 'Prerequis manquant : sequence SEQUSERMENU');\n    END IF;")
    prem = sp.creation[0]
    o.append("    SELECT COUNT(*) INTO n FROM user_tables WHERE table_name = '%s';\n"
             "    IF n > 0 THEN\n        RAISE_APPLICATION_ERROR(-20002, 'Deja installe (%s existe). Lancer %s-drop.sql avant de reinstaller.');\n    END IF;"
             % (prem.table, prem.table, sp.module))
    o.append("END;\n/\n")

    # ---- sequences + fonctions
    o.append("-- ============================================================================")
    o.append("-- 1. SEQUENCES ET FONCTIONS GETSEQ (utilisees par makePK de l'ORM : prefixe + 6 chiffres)")
    o.append("-- ============================================================================")
    for e in sp.creation:
        o.append("CREATE SEQUENCE %s START WITH 1;" % e.seq)
    o.append("")
    for e in sp.creation:
        o.append("CREATE OR REPLACE FUNCTION %s RETURN NUMBER IS\n    v NUMBER;\nBEGIN\n"
                 "    SELECT %s.NEXTVAL INTO v FROM DUAL;\n    RETURN v;\nEND;\n/\n" % (e.fonction, e.seq))

    # ---- tables
    o.append("-- ============================================================================")
    o.append("-- 2. TABLES (colonnes = attributs de l'ORM Java)")
    o.append("-- ============================================================================")
    for e in sp.creation:
        cols = [("id", "VARCHAR2(30)", "PRIMARY KEY")] + colonnes_sql(e)
        lignes = ["    %-18s %s%s" % (n, t, (" " + opt) if opt else "") for n, t, opt in cols]
        o.append("CREATE TABLE %s (\n%s\n);" % (e.table, ",\n".join(lignes)))
        p = e.prefixe.lower()
        if e.a_etat:
            o.append("ALTER TABLE %s\n    ADD CONSTRAINT %s CHECK (etat IN (0,1,11,9));" % (e.table, ident("ck_%s_etat" % p)))
        for c in e.champs:
            if c.min is not None or c.max is not None:
                cond = []
                if c.min is not None:
                    cond.append("%s >= %s" % (c.nom, c.min))
                if c.max is not None:
                    cond.append("%s <= %s" % (c.nom, c.max))
                o.append("ALTER TABLE %s\n    ADD CONSTRAINT %s CHECK (%s);" % (e.table, ident("ck_%s_%s" % (p, c.nom)), " AND ".join(cond)))
        o.append("")

    # ---- cles etrangeres + index
    o.append("-- ============================================================================")
    o.append("-- 3. CLES ETRANGERES ET INDEX")
    o.append("-- ============================================================================")
    for e in sp.creation:
        p = e.prefixe.lower()
        if e.type == "detail":
            m = e.maitre
            o.append("ALTER TABLE %s\n    ADD CONSTRAINT %s FOREIGN KEY (idmere)\n        REFERENCES %s (id);" % (e.table, ident("fk_%s_mere" % p), m.table))
            o.append("CREATE INDEX %s ON %s (idmere);" % (ident("idx_%s_mere" % p), e.table))
        for c in e.liens:
            if c.dico:
                tab, idc = c.dico.upper(), "id"
            elif isinstance(c.ref, dict):
                tab, idc = c.ref["table"].upper(), c.ref.get("id", "id")
            else:
                tab, idc = e.cible(c).table, "id"
            o.append("ALTER TABLE %s\n    ADD CONSTRAINT %s FOREIGN KEY (%s)\n        REFERENCES %s (%s);" % (e.table, ident("fk_%s_%s" % (p, c.nom)), c.nom, tab, idc))
            o.append("CREATE INDEX %s ON %s (%s);" % (ident("idx_%s_%s" % (p, c.nom)), e.table, c.nom))
    o.append("")

    # ---- vues
    o.append("-- ============================================================================")
    o.append("-- 4. VUES <TABLE>_LIB (libelles des references et de l'etat, lues par les listes et fiches)")
    o.append("-- ============================================================================")
    for e in sp.creation:
        sel = ["t.id"]
        if e.type == "detail":
            sel.append("t.idmere")
        sel += ["t.%s" % c.nom for c in e.champs]
        if e.a_etat:
            sel.append("t.etat")
        joins = []
        for i, c in enumerate(e.liens):
            a = "j%d" % i
            if c.dico:
                tab, idc, aff = c.dico.upper(), "id", "val"
            elif isinstance(c.ref, dict):
                tab, idc, aff = c.ref["table"].upper(), c.ref.get("id", "id"), c.ref["affiche"]
            else:
                tab, idc, aff = e.cible(c).table, "id", c.affiche
            sel.append("%s.%s AS %s" % (a, aff, c.nomlib))
            joins.append("LEFT JOIN %s %s ON %s.%s = t.%s" % (tab, a, a, idc, c.nom))
        if e.a_etat:
            sel.append("CASE t.etat WHEN 0 THEN 'Annule' WHEN 1 THEN 'Cree' WHEN 11 THEN 'Vise' "
                       "WHEN 9 THEN 'Cloture' ELSE 'Autre' END AS etatlib")
        o.append("CREATE OR REPLACE VIEW %s AS\nSELECT %s\nFROM %s t%s;\n"
                 % (e.vue, ",\n       ".join(sel), e.table, ("\n" + "\n".join(joins)) if joins else ""))

    # ---- menu
    ms = menus(sp)
    if ms:
        o.append("-- ============================================================================")
        o.append("-- 5. MENU + droits du role \"%s\" (redemarrer WildFly ensuite : menus mis en cache)" % sp.role)
        o.append("-- ============================================================================")
        for mid, lib, ic, href, rang, niv, pere in ms:
            o.append("INSERT INTO MENUDYNAMIQUE (ID, LIBELLE, ICONE, HREF, RANG, NIVEAU, ID_PERE)\n"
                     "SELECT '%s', '%s', '%s',\n       '%s',\n       %s, %s, %s\n"
                     "FROM dual WHERE NOT EXISTS (SELECT 1 FROM MENUDYNAMIQUE WHERE ID = '%s');\n"
                     % (mid, sqlstr(entites_html(lib)), ic, href, rang, niv, "NULL" if pere is None else "'%s'" % pere, mid))
        for mid, *_ in ms:
            o.append("INSERT INTO USERMENU (ID, IDMENU, IDROLE, INTERDIT)\nSELECT sequsermenu.NEXTVAL, '%s', '%s', 0\n"
                     "FROM dual WHERE NOT EXISTS (SELECT 1 FROM USERMENU WHERE IDMENU = '%s' AND IDROLE = '%s');\n"
                     % (mid, sp.role, mid, sp.role))
    o.append("COMMIT;\n")

    # ---- verification
    o.append("-- ============================================================================")
    o.append("-- 6. VERIFICATION")
    o.append("-- ============================================================================")
    noms = ", ".join("'%s'" % n for e in sp.creation for n in (e.table, e.vue))
    o.append("PROMPT >> Objets invalides (doit etre vide)\nSELECT object_type, object_name FROM user_objects\n"
             "WHERE status <> 'VALID' AND object_name IN (%s);\n" % noms)
    o.append("PROMPT >> Installation de %s terminee. Redemarrer WildFly." % sp.module)
    return "\n".join(o) + "\n"


def gen_drop(sp, fichier_spec):
    ordre_inverse = list(reversed(sp.creation))
    liste = lambda noms: ",\n        ".join("'%s'" % n for n in noms)
    ms = menus(sp)
    o = [en_tete_sql(sp, "Desinstallation du module %s. SUPPRIME les tables ET leurs donnees." % sp.module), ""]
    if ms:
        ids = ", ".join("'%s'" % m[0] for m in ms)
        o.append("DELETE FROM USERMENU WHERE IDMENU IN (%s);" % ids)
        for niv in (3, 2, 1):
            o.append("DELETE FROM MENUDYNAMIQUE WHERE NIVEAU = %d AND ID IN (%s);" % (niv, ids))
        o.append("COMMIT;\n")
    o.append("DECLARE\n    PROCEDURE supprimer(p_type VARCHAR2, p_nom VARCHAR2) IS\n    BEGIN\n"
             "        EXECUTE IMMEDIATE 'DROP ' || p_type || ' ' || p_nom\n"
             "                          || CASE p_type WHEN 'TABLE' THEN ' CASCADE CONSTRAINTS PURGE' ELSE '' END;\n"
             "    EXCEPTION\n        WHEN OTHERS THEN\n"
             "            IF SQLCODE NOT IN (-942, -4043, -2289) THEN   -- objet absent\n                RAISE;\n            END IF;\n    END;\nBEGIN")
    for type_, noms in (("VIEW", [e.vue for e in ordre_inverse]), ("TABLE", [e.table for e in ordre_inverse]),
                        ("FUNCTION", [e.fonction for e in ordre_inverse]), ("SEQUENCE", [e.seq for e in ordre_inverse])):
        o.append("    FOR r IN (SELECT column_value n FROM TABLE(sys.odcivarchar2list(\n        %s))) LOOP\n        supprimer('%s', r.n);\n    END LOOP;"
                 % (liste(noms), type_))
    o.append("END;\n/\n")
    o.append("PROMPT >> Module %s desinstalle." % sp.module)
    return "\n".join(o) + "\n"


# ============================================================================
# Java : ORM
# ============================================================================
def getset(typ, nom):
    c = cap(nom)
    return "    public %s get%s() { return %s; }\n    public void set%s(%s v) { this.%s = v; }" % (typ, c, nom, c, typ, nom)


def valeur_java(c):
    if c.defaut is None or c.defaut == "aujourdhui":
        return ""
    if c.type == "texte":
        return ' = "%s"' % jstr(str(c.defaut))
    return " = %s" % c.defaut


def gen_orm(sp, e):
    o = ["// " + MARQUE + " (retire cette ligne pour proteger le fichier contre la regeneration)",
         "package %s;" % sp.orm_pkg, "", "import java.sql.Connection;"]
    imports = ["bean.%s" % e.base]
    if e.type == "maitre":
        imports += ["bean.ClassFille", "bean.ClassMAPTable"]
    if e.type == "maitre" or any(c.min is not None or c.max is not None or
                                 (c.obligatoire and c.type in ("texte", "date")) for c in e.champs):
        imports.append("a_tahina.core.erreur.Verif")
    o += ["import %s;" % i for i in sorted(imports)]
    o += ["", "public class %s extends %s {" % (e.nom, e.base), "", "    private String id;"]
    if e.type == "detail":
        o.append("    private String idmere;")
    for c in e.champs:
        o.append("    private %s %s%s;" % (c.jtype, c.nom, valeur_java(c)))
    if e.a_etat:
        o.append("    private int etat = 1;")
    o += ["", "    public %s() throws Exception {" % e.nom, '        this.setNomTable("%s");' % e.table]
    if e.type == "maitre":
        o += ['        this.setLiaisonFille("idmere");', '        this.setNomClasseFille("%s.%s");' % (sp.orm_pkg, e.detail.nom)]
    if e.type == "detail":
        o += ['        this.setNomClasseMere("%s.%s");' % (sp.orm_pkg, e.maitre.nom), '        this.setLiaisonMere("idmere");']
    o += ["    }", "",
          "    @Override", "    public void construirePK(Connection c) throws Exception {",
          '        this.preparePk("%s", "%s");' % (e.prefixe, e.fonction), "        this.setId(makePK(c));", "    }", "",
          '    @Override public String getAttributIDName() { return "id"; }',
          "    @Override public String getTuppleID()       { return this.getId(); }"]
    if e.type == "detail":
        o += ['    @Override public String getNomClasseMere()  { return "%s.%s"; }' % (sp.orm_pkg, e.maitre.nom),
              '    @Override public String getLiaisonMere()    { return "idmere"; }']
    if e.type == "maitre":
        o += ["", "    @Override",
              "    public ClassMAPTable createObject(String u, Connection c) throws Exception {",
              "        ClassFille[] filles = this.getFille();",
              '        Verif.verifier(filles != null && filles.length > 0, "%s : au moins une ligne est obligatoire.");' % ascii_fold(cap(e.singulier)),
              "        return super.createObject(u, c);", "    }"]
    regles = []
    for c in e.champs:
        lib = jstr(ascii_fold(c.libelle))
        if c.obligatoire and c.type == "texte":
            regles.append('        Verif.obligatoire(this.%s, "%s");' % (c.nom, lib))
        if c.obligatoire and c.type == "date":
            regles.append('        Verif.obligatoire(this.%s, "%s");' % (c.nom, lib))
        if c.min is not None:
            regles.append('        Verif.verifier(this.%s >= %s, "%s doit etre superieur ou egal a %s.");' % (c.nom, c.min, lib, c.min))
        if c.max is not None:
            regles.append('        Verif.verifier(this.%s <= %s, "%s doit etre inferieur ou egal a %s.");' % (c.nom, c.max, lib, c.max))
    if regles:
        o += ["", "    @Override", "    public void controler(Connection c) throws Exception {", "        super.controler(c);"] + regles + ["    }"]
    o += ["", getset("String", "id")]
    if e.type == "detail":
        o.append(getset("String", "idmere"))
    for c in e.champs:
        o.append(getset(c.jtype, c.nom))
    if e.a_etat:
        o.append(getset("int", "etat"))
    o.append("}")
    return "\n".join(o) + "\n"


def gen_lib(sp, e):
    extra = [(c.nomlib, "String") for c in e.liens]
    if e.a_etat:
        extra.append(("etatlib", "String"))
    o = ["// " + MARQUE + " (retire cette ligne pour proteger le fichier contre la regeneration)",
         "package %s;" % sp.orm_pkg, "", "public class %sLib extends %s {" % (e.nom, e.nom), ""]
    o += ["    private %s %s;" % (t, n) for n, t in extra]
    o += ["", "    public %sLib() throws Exception {" % e.nom, "        super();", '        this.setNomTable("%s");' % e.vue]
    if e.type == "maitre":
        o += ["        this.setLiaisonFille(null);", "        this.setNomClasseFille(null);"]
    o += ["    }", ""]
    o += [getset(t, n) for n, t in extra]
    o.append("}")
    return "\n".join(o) + "\n"


# ============================================================================
# Java : FH
# ============================================================================
def champ_expr(c, mode):
    """champ("nom", "Libelle") + options. mode : saisie | liste | fiche | colonne."""
    lib = entites_html(c.libelle)
    nom = c.colonne_affichee if mode in ("liste", "fiche", "colonne") else c.nom
    s = 'champ("%s", "%s")' % (nom, jstr(lib))
    if mode == "saisie":
        if c.dico:
            s += '.typeObjet("%s")' % c.dico.upper()
        elif isinstance(c.ref, dict):
            s += '.liste(new %s(), "%s", "%s")' % (c.ref["classe"], c.ref["affiche"], c.ref.get("id", "id"))
        elif c.ref:
            s += '.liste(new %s(), "%s", "id")' % (c.ref, c.affiche)
        if c.defaut == "aujourdhui":
            s += ".defautAujourdhui()"
        elif c.defaut is not None:
            s += '.defaut("%s")' % jstr(str(c.defaut))
    if mode == "liste":
        auto = c.type == "texte" or c.est_lien
        if c.recherche if c.recherche is not None else auto:
            s += ".recherche()"
        if c.type == "date" and c.intervalle is not False:
            s += ".intervalle()"
    return s


def args_champs(lignes, indent):
    pad = " " * indent
    return (",\n" + pad).join(lignes)


def gen_fh(sp, e):
    d = e.detail
    classes = {e.nom, e.nom + "Lib"}
    if d:
        classes |= {d.nom, d.nom + "Lib"}
    externes_classes = set()
    for x in [e] + ([d] if d else []):
        for c in x.liens:
            if isinstance(c.ref, str):
                classes.add(c.ref)
            elif isinstance(c.ref, dict):
                externes_classes.add(c.ref["classe"])
    imports = ["a_tahina.core.GeneraticFH", "a_tahina.core.page.AtahinaPageConsulte",
               "a_tahina.core.page.AtahinaPageRecherche",
               "a_tahina.core.page.AtahinaPageInsertMultiple" if e.type == "maitre" else "a_tahina.core.page.AtahinaPageInsert"]
    imports += ["%s.%s" % (sp.orm_pkg, n) for n in classes]   # les classes externes sont ecrites en entier dans le code
    dossier = "%s/%s/" % (sp.pages, e.slug)

    # liste
    lc = ['champ("id", "Id").recherche().lien(PAGE_FICHE)']
    lc += [champ_expr(c, "liste") for c in e.champs if c.dans_liste]
    if e.a_etat:
        lc += ['champ("etat", "&Eacute;tat").filtreSeul().listeEtat()', 'champ("etatlib", "&Eacute;tat")']
    # fiche
    fc = ['champ("id", "Id")'] + [champ_expr(c, "fiche") for c in e.champs if c.dans_fiche]
    if e.a_etat:
        fc.append('champ("etatlib", "&Eacute;tat")')

    o = ["// " + MARQUE + " (retire cette ligne pour proteger le fichier contre la regeneration)",
         "package %s;" % sp.fh_pkg, "", "import static a_tahina.core.page.AtahinaChampHelper.champ;", "",
         "import javax.servlet.http.HttpServletRequest;", "import javax.servlet.http.HttpSession;", ""]
    o += ["import %s;" % i for i in sorted(set(imports))]
    o += ["", "public class %sFH extends GeneraticFH {" % e.nom, "",
          '    public static final String DOSSIER     = "%s";' % dossier,
          '    public static final String PAGE_LISTE  = DOSSIER + "%s-liste.jsp";' % e.slug,
          '    public static final String PAGE_FICHE  = DOSSIER + "%s-fiche.jsp";' % e.slug,
          '    public static final String PAGE_SAISIE = DOSSIER + "%s-saisie.jsp";' % e.slug,
          '    public static final String CLASSE      = "%s.%s";' % (sp.orm_pkg, e.nom)]
    if d:
        o += ['    public static final String CLASSE_FILLE = "%s.%s";' % (sp.orm_pkg, d.nom),
              "    public static final int    NOMBRE_LIGNE = %s;" % e.lignes]
    o += ["",
          "    @Override",
          "    public AtahinaPageRecherche getPageRechercheForList(HttpServletRequest request,",
          "                                                        HttpSession session) throws Exception {",
          '        return liste(new %sLib(), request, session,' % e.nom,
          '                PAGE_LISTE, "Liste : %s",' % jstr(entites_html(e.titre)),
          "                " + args_champs(lc, 16) + ")",
          "            .boutonSaisir(PAGE_SAISIE);", "    }", "",
          "    @Override",
          "    public AtahinaPageConsulte getPageConsulte(HttpServletRequest request,",
          "                                               HttpSession session) throws Exception {",
          '        return consulte(new %sLib(), request, session, "Fiche %s",' % (e.nom, jstr(entites_html(e.singulier))),
          "                " + args_champs(fc, 16) + ");", "    }", ""]

    saisie = [champ_expr(c, "saisie") for c in e.champs if c.dans_saisie]
    if e.type == "maitre":
        col = [champ_expr(c, "saisie") for c in d.champs if c.dans_saisie]
        o += ["    @Override",
              "    public AtahinaPageInsertMultiple getPageInsertMultiple(HttpServletRequest request,",
              "                                                           HttpSession session,",
              "                                                           int nombreLigne) throws Exception {",
              "        return new AtahinaPageInsertMultiple(new %s(), new %s(), request, session, nombreLigne)" % (e.nom, d.nom),
              "            .champs(", "                " + args_champs(saisie, 16) + ")",
              "            .colonnes(", "                " + args_champs(col, 16) + ")",
              "            .preparer();", "    }", ""]
        cc = [champ_expr(c, "colonne") for c in d.champs if c.dans_liste]
        o += ["    public static String ongletDetail(HttpServletRequest request, HttpSession session, String idMere) throws Exception {",
              '        return onglet(new %sLib(), request, session, PAGE_FICHE, idMere, "inc/%s-detail", "idmere", "Aucune ligne",' % (d.nom, e.slug),
              "                " + args_champs(cc, 16) + ");", "    }"]
    else:
        o += ["    @Override",
              "    public AtahinaPageInsert getPageInsert(HttpServletRequest request,",
              "                                           HttpSession session) throws Exception {",
              '        return new AtahinaPageInsert(new %s(), request, session, "Saisie %s")' % (e.nom, jstr(entites_html(e.singulier))),
              "            .champs(", "                " + args_champs(saisie, 16) + ")",
              "            .preparer();", "    }"]
    o.append("}")
    return "\n".join(o) + "\n"


# ============================================================================
# JSP (modeles avec marqueurs @FH@ @ORM@ @FHPKG@ ... remplaces par rendre())
# ============================================================================
JSP_MARQUE = "<%-- " + MARQUE + " (retire cette ligne pour proteger le fichier contre la regeneration) --%>\n"
ERREUR = "<% } catch (Exception e) {\n     out.println(AtahinaUtil.erreurHtml(e));\n   } %>\n"


def rendre(modele, **v):
    for cle, val in v.items():
        modele = modele.replace("@%s@" % cle, str(val))
    return modele


LISTE = JSP_MARQUE + """<%@page import="a_tahina.core.page.AtahinaUtil"%>
<%@page import="a_tahina.core.page.AtahinaPageRecherche"%>
<%@page import="@FHPKG@.@FH@"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    AtahinaPageRecherche pr = new @FH@().getPageRechercheForList(request, session);
    out.println(pr.getHtmlListe());
""" + ERREUR

SAISIE_SIMPLE = JSP_MARQUE + """<%@page import="a_tahina.core.page.*"%>
<%@page import="@FHPKG@.@FH@"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    AtahinaPageInsert pi = new @FH@().getPageInsert(request, session);
%>
<div class="content-wrapper">
  <h1><%= pi.getTitre() %></h1>

  <form action="<%= pi.getLien() %>?but=apresTarif.jsp" method="post">
    <% out.println(pi.getFormu().getHtmlInsert()); %>

    <input type="hidden" name="acte"   value="insert" />
    <input type="hidden" name="bute"   value="<%= @FH@.PAGE_LISTE %>" />
    <input type="hidden" name="classe" value="<%= @FH@.CLASSE %>" />
  </form>
</div>
""" + ERREUR

SAISIE_MAITRE = JSP_MARQUE + """<%@page import="a_tahina.core.page.*"%>
<%@page import="@FHPKG@.@FH@"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    int nombreLigne = @FH@.NOMBRE_LIGNE;
    AtahinaPageInsertMultiple pi = new @FH@().getPageInsertMultiple(request, session, nombreLigne);
%>
<div class="content-wrapper">
  <h1>Saisie @SINGULIER@</h1>

  <form action="<%= pi.getLien() %>?but=apresMultiple.jsp" method="post">
    <% out.println(pi.getFormu().getHtmlInsert()); %>
    <% out.println(pi.getFormufle().getHtmlTableauInsert()); %>

    <input type="hidden" name="acte"        value="insert" />
    <input type="hidden" name="bute"        value="<%= @FH@.PAGE_FICHE %>" />
    <input type="hidden" name="classe"      value="<%= @FH@.CLASSE %>" />
    <input type="hidden" name="classefille" value="<%= @FH@.CLASSE_FILLE %>" />
    <input type="hidden" name="nombreLigne" value="<%= nombreLigne %>" />
    <input type="hidden" name="colonneMere" value="idmere" />
    <input type="hidden" name="nomtable"    value="@TABLEDETAIL@" />
  </form>
</div>
""" + ERREUR

FICHE = JSP_MARQUE + """<%@page import="a_tahina.core.page.*"%>
<%@page import="@ORMPKG@.*"%>
<%@page import="@FHPKG@.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    AtahinaPageConsulte pc = new @FH@().getPageConsulte(request, session);
    String lien = pc.getLien();
    @ENT@Lib o = (@ENT@Lib) pc.getBase();
    String id = o.getTuppleID();
@INIT@%>
<div class="content-wrapper">
  <h1>
    <a href="<%= lien %>?but=<%= @FH@.PAGE_LISTE %>"><i class="fa fa-angle-left"></i></a>
    <%= pc.getTitre() %> #<%= id %>
  </h1>

  <div class="row m-0">
    <div class="col-md-3"></div>
    <div class="col-md-6">
      <div class="box-fiche">
        <div class="box">
          <div class="box-body">
            <% out.println(pc.getHtml()); %>
            <br/>
@BOUTONS@            <br/>
          </div>
        </div>
      </div>
    </div>
  </div>
@ONGLETS@</div>
""" + ERREUR

INIT_ETAT = "    int etat = o.getEtat();\n"
INIT_TABS = """
    String[] tabs   = {"inc/@SLUG@-detail"};
    String[] labels = {"D&eacute;tails"};
    String tab = request.getParameter("tab");
    if (!java.util.Arrays.asList(tabs).contains(tab)) tab = tabs[0];   // onglets connus uniquement
    String tabJsp = tab + ".jsp";
"""
BOUTONS_ETAT = """            <div class="box-footer">
              <%-- etat < 11 : Viser / Annuler ; pas de page de modification --%>
              <% out.println(pc.getBoutonsWorkflow(id, etat, @FH@.PAGE_FICHE, @FH@.CLASSE, null)); %>
              <% if (etat == 11) { %>
                <a class="btn btn-warning pull-left"
                   href="<%= pc.lienActe("annulerVisa", id, @FH@.PAGE_FICHE, @FH@.CLASSE) %>">Annuler le visa</a>
              <% } %>
            </div>
"""
ONGLETS = """
  <div class="row m-0">
    <div class="col-md-12 nopadding">
      <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
          <% for (int i = 0; i < tabs.length; i++) { %>
            <li class="<%= tabs[i].equals(tab) ? "active" : "" %>">
              <a href="<%= lien %>?but=<%= @FH@.PAGE_FICHE %>&id=<%= AtahinaUtil.url(id) %>&tab=<%= tabs[i] %>"><%= labels[i] %></a>
            </li>
          <% } %>
        </ul>
        <div class="tab-content">
          <jsp:include page="<%= tabJsp %>">
            <jsp:param name="idMere" value="<%= id %>" />
          </jsp:include>
        </div>
      </div>
    </div>
  </div>
"""

DETAIL = JSP_MARQUE + """<%@page import="a_tahina.core.page.AtahinaUtil"%>
<%@page import="@FHPKG@.@FH@"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% try {
    out.println(@FH@.ongletDetail(request, session, request.getParameter("idMere")));
""" + ERREUR


def gen_jsp_liste(sp, e):
    return rendre(LISTE, FHPKG=sp.fh_pkg, FH=e.nom + "FH")


def gen_jsp_saisie(sp, e):
    fh = e.nom + "FH"
    if e.type == "maitre":
        return rendre(SAISIE_MAITRE, FHPKG=sp.fh_pkg, FH=fh, SINGULIER=entites_html(e.singulier), TABLEDETAIL=e.detail.table)
    return rendre(SAISIE_SIMPLE, FHPKG=sp.fh_pkg, FH=fh)


def gen_jsp_fiche(sp, e):
    fh = e.nom + "FH"
    init = (INIT_ETAT if e.a_etat else "") + (rendre(INIT_TABS, SLUG=e.slug) if e.type == "maitre" else "")
    return rendre(FICHE, ORMPKG=sp.orm_pkg, FHPKG=sp.fh_pkg, FH=fh, ENT=e.nom, INIT=init,
                  BOUTONS=rendre(BOUTONS_ETAT, FH=fh) if e.a_etat else "",
                  ONGLETS=rendre(ONGLETS, FH=fh) if e.type == "maitre" else "")


def gen_jsp_detail(sp, e):
    return rendre(DETAIL, FHPKG=sp.fh_pkg, FH=e.nom + "FH")


# ============================================================================
# Ecriture
# ============================================================================
class Sortie:
    def __init__(self, racine, apercu, force):
        self.racine, self.apercu, self.force = racine, apercu, force
        self.n_ecrit = self.n_ignore = 0

    def ecrire(self, chemin, contenu):
        complet = os.path.join(self.racine, chemin)
        if os.path.exists(complet) and not self.force:
            with open(complet, encoding="utf-8") as f:
                if MARQUE not in f.read():
                    print("  ~ ignore (modifie a la main, marque retiree) :", chemin)
                    self.n_ignore += 1
                    return
        if self.apercu:
            print("  +", chemin, "(apercu)")
            self.n_ecrit += 1
            return
        os.makedirs(os.path.dirname(complet), exist_ok=True)
        with open(complet, "w", encoding="utf-8", newline="\n") as f:
            f.write(contenu)
        print("  +", chemin)
        self.n_ecrit += 1


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("spec")
    ap.add_argument("--out", default=".", help="racine de sortie (defaut : dossier courant = racine du projet)")
    ap.add_argument("--seulement", choices=("sql", "java", "jsp"), help="ne generer que cette partie")
    ap.add_argument("--apercu", action="store_true", help="liste les fichiers sans ecrire")
    ap.add_argument("--force", action="store_true", help="ecrase aussi les fichiers dont la marque a ete retiree")
    a = ap.parse_args()

    try:
        with open(a.spec, encoding="utf-8") as f:
            brut = json.load(f)
    except json.JSONDecodeError as ex:
        die("JSON invalide ligne %d colonne %d : %s" % (ex.lineno, ex.colno, ex.msg))
    sp = Spec(brut)
    s = Sortie(a.out, a.apercu, a.force)
    pk = sp.orm_pkg.replace(".", "/")
    fk = sp.fh_pkg.replace(".", "/")
    veut = lambda part: a.seulement in (None, part)

    if veut("sql"):
        s.ecrire("%s/%s/%s.sql" % (SQL_DIR, sp.module, sp.module), gen_sql(sp, os.path.basename(a.spec)))
        s.ecrire("%s/%s/%s-drop.sql" % (SQL_DIR, sp.module, sp.module), gen_drop(sp, os.path.basename(a.spec)))
    for e in sp.entites:
        if veut("java"):
            s.ecrire("%s/%s/%s.java" % (SRC_JAVA_DIR, pk, e.nom), gen_orm(sp, e))
            s.ecrire("%s/%s/%sLib.java" % (SRC_JAVA_DIR, pk, e.nom), gen_lib(sp, e))
            if e.type != "detail":
                s.ecrire("%s/%s/%sFH.java" % (SRC_JAVA_DIR, fk, e.nom), gen_fh(sp, e))
        if veut("jsp") and e.type != "detail":
            base = "%s/%s/%s/%s" % (WEB_PAGES_DIR, sp.pages, e.slug, e.slug)
            s.ecrire(base + "-liste.jsp", gen_jsp_liste(sp, e))
            s.ecrire(base + "-saisie.jsp", gen_jsp_saisie(sp, e))
            s.ecrire(base + "-fiche.jsp", gen_jsp_fiche(sp, e))
            if e.type == "maitre":
                s.ecrire("%s/%s/%s/inc/%s-detail.jsp" % (WEB_PAGES_DIR, sp.pages, e.slug, e.slug), gen_jsp_detail(sp, e))

    print("\n%d fichier(s) %s, %d ignore(s)." % (s.n_ecrit, "a ecrire" if a.apercu else "ecrit(s)", s.n_ignore))
    if veut("sql") and not a.apercu:
        print("SQL : ./import_sql.sh %s/%s/%s.sql   (puis redemarrer WildFly)" % (SQL_DIR, sp.module, sp.module))


if __name__ == "__main__":
    main()
