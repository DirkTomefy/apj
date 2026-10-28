/* Documentation APJ — application (aucune dépendance, fonctionne en file://) */
(function(){
'use strict';
var API = window.APJ_API || [], GUIDE = window.APJ_GUIDE || [];
var $ = function(s,r){return (r||document).querySelector(s)}, $$ = function(s,r){return Array.prototype.slice.call((r||document).querySelectorAll(s))};
var esc = function(s){return String(s==null?'':s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;')};
var byId = {}, byName = {}, pkgs = {};
API.forEach(function(c){byId[c.id]=c;(byName[c.n]=byName[c.n]||[]).push(c);(pkgs[c.pkg]=pkgs[c.pkg]||[]).push(c)});
var pkgNames = Object.keys(pkgs).sort();
var MODS = {'apj-core':'apj-core.jar','apj-core2':'apj-core2.jar','arbiochem':'arbiochem-ejb (contrat applicatif)'};

/* ---------- liens vers classes ---------- */
function resolveClass(name, fromPkg){
  name = name.replace(/<.*$/,'').replace(/\[\]/g,'').trim();
  var simple = name.split('.').pop(), l = byName[simple];
  if(!l) return null;
  if(l.length===1) return l[0];
  var f = l.filter(function(c){return c.pkg===fromPkg})[0];
  return f || l[0];
}
function typeLink(t, fromPkg){
  return esc(t).replace(/[A-Za-z_][\w.]*/g, function(w){
    var c = resolveClass(w, fromPkg);
    return c && /^[A-Z]/.test(w.split('.').pop()) ? '<a href="#/class/'+encodeURIComponent(c.id)+'">'+w+'</a>' : w;
  });
}
/* auto-lien des <code>Nom</code> dans les descriptions */
function linkify(html, fromPkg){
  return html.replace(/<code>([A-Z][A-Za-z0-9_]*)(\(\))?<\/code>/g, function(m,n,p){
    var c = resolveClass(n, fromPkg);
    return c ? '<a href="#/class/'+encodeURIComponent(c.id)+'"><code>'+n+(p||'')+'</code></a>' : m;
  });
}
function firstSentence(h){var m=h.match(/^(.*?[.!?:])(\s|$)/);return m&&m[1].length<260?m[1]:(h.length>240?h.slice(0,240)+'…':h)}

/* ---------- coloration syntaxique ---------- */
var JKW = 'abstract assert boolean break byte case catch char class const continue default do double else enum extends final finally float for if implements import instanceof int interface long native new package private protected public return short static super switch synchronized this throw throws try void volatile while null true false var'.split(' ');
function hl(code, lang){
  var src = code, out = '', i = 0;
  lang = (lang||'java').toLowerCase();
  var re;
  if(lang==='sql') re=/(--[^\n]*)|('(?:[^']|'')*')|\b(select|from|where|and|or|insert|into|values|update|set|delete|create|table|view|as|order|by|group|like|upper|null|not|primary|key|varchar2|number|date|nextval|sequence|function|return|begin|end|union|all|join|on|left)\b|(\b\d+(?:\.\d+)?\b)/gi;
  else if(lang==='html'||lang==='xml') re=/(&lt;!--[\s\S]*?--&gt;)|(&lt;\/?[\w:-]+)|("[^"]*")|(\b[\w-]+=)/g;
  else if(lang==='js') re=/(\/\/[^\n]*|\/\*[\s\S]*?\*\/)|("(?:\\.|[^"\\])*"|'(?:\\.|[^'\\])*'|`[^`]*`)|\b(function|var|let|const|return|if|else|for|while|new|this|true|false|null|async|await)\b|(\b\d+(?:\.\d+)?\b)/g;
  else if(lang==='text'||lang==='properties') return esc(src);
  else re=/(\/\*[\s\S]*?\*\/|\/\/[^\n]*|&lt;%--[\s\S]*?--%&gt;)|("(?:\\.|[^"\\])*")|(@\w+)|(&lt;%[@!=]?|%&gt;)|\b([A-Z][A-Za-z0-9_]*)\b|\b(\d+(?:\.\d+)?[dDfFL]?)\b|\b([a-z]+)\b/g;
  var e = esc(src);
  if(lang==='java'||lang==='jsp'){
    return e.replace(re, function(m,c,s,a,j,t,n,w){
      if(c) return '<span class="tk-c">'+m+'</span>';
      if(s) return '<span class="tk-s">'+m+'</span>';
      if(a) return '<span class="tk-a">'+m+'</span>';
      if(j) return '<span class="tk-j tk-k">'+m+'</span>';
      if(t) return (API.length && byName[t]) ? '<span class="tk-t">'+m+'</span>' : m;
      if(n) return '<span class="tk-n">'+m+'</span>';
      if(w && JKW.indexOf(w)>=0) return '<span class="tk-k">'+m+'</span>';
      return m;
    });
  }
  if(lang==='sql') return e.replace(re,function(m,c,s,k,n){return c?'<span class="tk-c">'+m+'</span>':s?'<span class="tk-s">'+m+'</span>':k?'<span class="tk-k">'+m+'</span>':'<span class="tk-n">'+m+'</span>'});
  if(lang==='html'||lang==='xml') return e.replace(re,function(m,c,tg,s,at){return c?'<span class="tk-c">'+m+'</span>':tg?'<span class="tk-k">'+m+'</span>':s?'<span class="tk-s">'+m+'</span>':'<span class="tk-a">'+m+'</span>'});
  return e.replace(re,function(m,c,s,k,n){return c?'<span class="tk-c">'+m+'</span>':s?'<span class="tk-s">'+m+'</span>':k?'<span class="tk-k">'+m+'</span>':'<span class="tk-n">'+m+'</span>'});
}
function decorate(root){
  $$('pre.code',root).forEach(function(p){
    if(p.dataset.done) return; p.dataset.done=1;
    var raw = p.textContent;
    p.innerHTML = hl(raw.replace(/^\n/,''), p.dataset.lang||'java');
    var b = document.createElement('button'); b.className='cp'; b.textContent='Copier';
    b.onclick=function(){navigator.clipboard&&navigator.clipboard.writeText(raw.replace(/^\n/,'')).then(function(){b.textContent='Copié ✓';setTimeout(function(){b.textContent='Copier'},1200)})};
    p.appendChild(b);
  });
}

/* ---------- rendu des sections ---------- */
function mods(a){return a.filter(function(x){return x!=='public'}).join(' ')}
function sigParams(p,pkg){return p?typeLink(p,pkg):''}
function renderClass(c){
  var h = '<h1><span class="sub">'+(c.pkg?'Paquet <a href="#/pkg/'+encodeURIComponent(c.pkg)+'">'+esc(c.pkg)+'</a>':'')+' · module '+esc(MODS[c.mod]||c.mod)+'</span>'
    + (c.k==='interface'?'Interface ':c.k==='enum'?'Enum ':(c.m.indexOf('abstract')>=0?'Classe abstraite ':'Classe '))+esc(c.n)
    + (c.bad?' <span class="badge r" title="Une partie du code décompilé est partielle">décompilation partielle</span>':'')+'</h1>';
  // hiérarchie
  var chain=[], cur=c, guard=0;
  while(cur && guard++<12){chain.unshift(cur); cur = cur.e ? resolveClass(cur.e,cur.pkg) : null; if(cur===chain[0])break}
  var lastExt = chain[0].e && !resolveClass(chain[0].e,chain[0].pkg) ? chain[0].e : (chain[0].e? '' : 'java.lang.Object');
  h += '<div class="hier">';
  var pad=0; if(lastExt){h+='<span class="ind" style="padding-left:0">'+esc(lastExt)+'</span>';pad=1}
  chain.forEach(function(x,i){h+='<span class="ind" style="padding-left:'+(pad+i)*22+'px">'+(pad+i>0?'└─ ':'')+(i===chain.length-1?'<b>'+esc(x.pkg+'.'+x.n)+'</b>':'<a href="#/class/'+encodeURIComponent(x.id)+'">'+esc(x.pkg+'.'+x.n)+'</a>')+'</span>'});
  h += '</div>';
  var subs = API.filter(function(x){return x.e && x.e.split('.').pop()===c.n && resolveClass(x.e,x.pkg)===c});
  if(c.i) h+='<dl class="meta"><dt>Interfaces implémentées</dt><dd>'+typeLink(c.i,c.pkg)+'</dd></dl>';
  if(subs.length) h+='<dl class="meta"><dt>Sous-classes directes</dt><dd>'+subs.map(function(s){return '<a href="#/class/'+encodeURIComponent(s.id)+'">'+esc(s.n)+'</a>'}).join(', ')+'</dd></dl>';
  if(c.an&&c.an.length) h+='<dl class="meta"><dt>Annotations</dt><dd><code>@WebServlet('+esc(c.an.join(' '))+')</code></dd></dl>';
  h += '<div class="sig-block">'+esc(c.m.join(' '))+' '+(c.k==='class'?'class':c.k)+' <b>'+esc(c.n)+'</b>'+(c.e?'\n    extends '+typeLink(c.e,c.pkg):'')+(c.i?'\n    implements '+typeLink(c.i,c.pkg):'')+'</div>';
  h += '<div>'+linkify(c.d,c.pkg)+(c.ad?' <span class="autotag">description générique</span>':'')+'</div>';
  h += '<p style="color:var(--muted);font-size:12px">'+c.ms.length+' méthode(s) · '+c.c.length+' constructeur(s) · '+c.f.length+' constante(s)/attribut(s) visibles · ≈ '+c.L+' lignes de code source décompilé</p>';
  if(c.f.length){
    h+='<table class="t"><caption>Résumé des champs</caption><tr><th>Modificateurs et type</th><th>Champ et valeur</th></tr>';
    c.f.forEach(function(f){h+='<tr><td class="mod">'+esc(mods(f.m))+' '+typeLink(f.t,c.pkg)+'</td><td class="sig"><b>'+esc(f.n)+'</b>'+(f.v?' = <span style="color:var(--s)">'+esc(f.v)+'</span>':'')+'</td></tr>'});
    h+='</table>';
  }
  if(c.c.length){
    h+='<table class="t"><caption>Résumé des constructeurs</caption><tr><th>Constructeur</th></tr>';
    c.c.forEach(function(k){h+='<tr><td class="sig"><b>'+esc(c.n)+'</b>('+sigParams(k.p,c.pkg)+')'+(k.t?' <span style="color:var(--muted)">throws '+esc(k.t)+'</span>':'')+'</td></tr>'});
    h+='</table>';
  }
  if(c.ms.length){
    h+='<table class="t" id="msum"><caption>Résumé des méthodes</caption><tr><th>Modificateurs et type</th><th>Méthode et description</th></tr>';
    c.ms.forEach(function(m,i){
      h+='<tr><td class="mod">'+esc(mods(m.m))+' '+typeLink(m.r,c.pkg)+'</td><td class="sig"><a href="#/class/'+encodeURIComponent(c.id)+'~m'+i+'"><b>'+esc(m.n)+'</b></a>('+sigParams(m.p,c.pkg)+')'
        + (m.d?'<div class="blk'+(m.a?' auto':'')+'">'+linkify(firstSentence(m.d),c.pkg)+'</div>':'')+'</td></tr>';
    });
    h+='</table>';
    // héritées
    var inh=[], p=c.e?resolveClass(c.e,c.pkg):null, g=0;
    while(p&&g++<10){inh.push(p);p=p.e?resolveClass(p.e,p.pkg):null}
    inh.forEach(function(x){
      var names={}; x.ms.forEach(function(m){if(m.m.indexOf('private')<0)names[m.n]=1});
      var own={}; c.ms.forEach(function(m){own[m.n]=1});
      var l=Object.keys(names).filter(function(n){return !own[n]}).sort();
      if(l.length) h+='<details style="margin:.4em 0"><summary>Méthodes héritées de <a href="#/class/'+encodeURIComponent(x.id)+'">'+esc(x.n)+'</a> ('+l.length+')</summary><p style="font-family:var(--mono);font-size:12px">'+l.map(function(n){return '<a href="#/class/'+encodeURIComponent(x.id)+'~m'+x.ms.findIndex(function(m){return m.n===n})+'">'+esc(n)+'</a>'}).join(', ')+'</p></details>';
    });
    h+='<h2>Détail des méthodes</h2>';
    c.ms.forEach(function(m,i){
      h+='<section class="detail" id="m'+i+'"><h4>'+esc(m.n)+'</h4><div class="sig-block">'+esc(mods(m.m))+(m.m.length?' ':'')+typeLink(m.r,c.pkg)+' <b>'+esc(m.n)+'</b>('+sigParams(m.p,c.pkg)+')'+(m.t?'\n    throws '+esc(m.t):'')+'</div>'
        + (m.d?'<div class="'+(m.a?'auto':'')+'">'+linkify(m.d,c.pkg)+'</div>':'<div style="color:var(--muted)">—</div>')+'</section>';
    });
  }
  h+='<footer class="ft">Documentation produite par analyse du code décompilé ('+esc(MODS[c.mod]||c.mod)+'). Les descriptions marquées « générique » sont déduites du nom ; les autres sont rédigées à partir de la lecture du code.</footer>';
  return h;
}
function renderPkg(p){
  var l = pkgs[p]; if(!l) return '<h1>Paquet introuvable</h1>';
  var h='<h1><span class="sub">Paquet</span>'+esc(p||'(défaut)')+'</h1><table class="t"><caption>Résumé des classes</caption><tr><th>Classe</th><th>Description</th></tr>';
  l.slice().sort(function(a,b){return a.n.localeCompare(b.n)}).forEach(function(c){
    h+='<tr><td class="sig"><a href="#/class/'+encodeURIComponent(c.id)+'">'+(c.k!=='class'?'<i>'+esc(c.n)+'</i>':esc(c.n))+'</a> <span class="badge'+(c.m.indexOf('abstract')>=0?' o':'')+'">'+(c.k==='interface'?'interface':c.m.indexOf('abstract')>=0?'abstraite':'classe')+'</span></td><td>'+linkify(firstSentence(c.d),p)+'</td></tr>';
  });
  return h+'</table>';
}
function renderApiHome(){
  var h='<h1><span class="sub">Référence</span>API du framework APJ</h1><p>Référence générée à partir des sources décompilées de <code>apj-core.jar</code> et <code>apj-core2.jar</code>, plus le contrat <code>UserEJB</code> du projet <i>arbiochem</i>. '+API.length+' types, '+API.reduce(function(a,c){return a+c.ms.length},0)+' méthodes.</p>';
  ['apj-core','apj-core2','arbiochem'].forEach(function(m){
    var ps = pkgNames.filter(function(p){return pkgs[p][0].mod===m});
    h+='<h2>'+esc(MODS[m])+'</h2><table class="t"><tr><th>Paquet</th><th>Types</th><th>Contenu principal</th></tr>';
    ps.forEach(function(p){h+='<tr><td class="sig"><a href="#/pkg/'+encodeURIComponent(p)+'">'+esc(p)+'</a></td><td>'+pkgs[p].length+'</td><td>'+pkgs[p].slice(0,6).map(function(c){return '<a href="#/class/'+encodeURIComponent(c.id)+'">'+esc(c.n)+'</a>'}).join(', ')+(pkgs[p].length>6?'…':'')+'</td></tr>'});
    h+='</table>';
  });
  return h;
}
function renderTree(){
  var kids={}, roots=[];
  API.forEach(function(c){var p=c.e?resolveClass(c.e,c.pkg):null; if(p&&p!==c)(kids[p.id]=kids[p.id]||[]).push(c); else roots.push(c)});
  function node(c){var k=(kids[c.id]||[]).sort(function(a,b){return a.n.localeCompare(b.n)});return '<li><a href="#/class/'+encodeURIComponent(c.id)+'">'+esc(c.n)+'</a> <span style="color:var(--muted)">'+esc(c.pkg)+'</span>'+(k.length?'<ul>'+k.map(node).join('')+'</ul>':'')+'</li>'}
  var big = roots.filter(function(r){return (kids[r.id]||[]).length}).sort(function(a,b){return (kids[b.id]||[]).length-(kids[a.id]||[]).length});
  var small = roots.filter(function(r){return !(kids[r.id]||[]).length});
  return '<h1><span class="sub">Navigation</span>Arborescence des classes</h1><div class="tree"><ul>'+big.map(node).join('')+'</ul></div><h2>Classes sans parent applicatif</h2><p>'+small.map(function(c){return '<a href="#/class/'+encodeURIComponent(c.id)+'">'+esc(c.n)+'</a>'}).join(' · ')+'</p>';
}
function renderIndex(letter){
  var items=[];
  API.forEach(function(c){items.push({n:c.n,k:'classe',c:c,i:-1,d:c.d});c.ms.forEach(function(m,i){items.push({n:m.n,k:'méthode',c:c,i:i,d:m.d})})});
  items.sort(function(a,b){return a.n.toLowerCase()<b.n.toLowerCase()?-1:a.n.toLowerCase()>b.n.toLowerCase()?1:0});
  var letters={}; items.forEach(function(x){letters[x.n[0].toUpperCase()]=1});
  letter = (letter||'A').toUpperCase();
  var h='<h1><span class="sub">Navigation</span>Index général</h1><div class="az">'+Object.keys(letters).sort().map(function(l){return '<a href="#/index/'+l+'"'+(l===letter?' style="background:var(--tab-active)"':'')+'>'+l+'</a>'}).join('')+'</div><dl class="idx">';
  items.filter(function(x){return x.n[0].toUpperCase()===letter}).forEach(function(x){
    h+='<dt><a href="#/class/'+encodeURIComponent(x.c.id)+(x.i>=0?'~m'+x.i:'')+'"><b>'+esc(x.n)+(x.i>=0?'()':'')+'</b></a> — '+x.k+(x.i>=0?' de <a href="#/class/'+encodeURIComponent(x.c.id)+'">'+esc(x.c.n)+'</a>':' <span style="color:var(--muted)">'+esc(x.c.pkg)+'</span>')+'</dt><dd>'+(x.d?linkify(firstSentence(x.d),x.c.pkg):'')+'</dd>';
  });
  return h+'</dl>';
}

/* ---------- guide ---------- */
function guideTitle(id){var g=GUIDE.filter(function(x){return x.id===id})[0];return g}
function renderGuide(id){
  var g = guideTitle(id||GUIDE[0].id); if(!g) return '<h1>Page introuvable</h1>';
  var i = GUIDE.indexOf(g), prev=GUIDE[i-1], next=GUIDE[i+1];
  var nav='<div style="display:flex;justify-content:space-between;margin-top:30px;border-top:1px solid var(--border);padding-top:10px">'+(prev?'<a href="#/guide/'+prev.id+'">← '+esc(prev.title)+'</a>':'<span></span>')+(next?'<a href="#/guide/'+next.id+'">'+esc(next.title)+' →</a>':'<span></span>')+'</div>';
  return '<h1><span class="sub">Guide · '+(i+1)+'/'+GUIDE.length+'</span>'+esc(g.title)+'</h1>'+linkify(g.html,'')+nav;
}
function renderHome(){
  var h='<h1><span class="sub">Documentation technique</span>APJ — Framework Java Enterprise « maison »</h1>'
  +'<p>Cette documentation décrit le framework interne <b>APJ</b> (<code>apj-core</code>, <code>apj-core2</code>) tel qu\'il est utilisé dans le projet <b>arbiochem</b> : de l\'entité <code>ClassMAPTable</code> jusqu\'aux pages JSP (<code>PageRecherche</code>, <code>PageInsertMultiple</code>, <code>PageConsulte</code>…) et aux fonctions d\'affichage.</p>'
  +'<div class="flow"><div class="st"><b>1. Base Oracle/PG</b>tables, vues <code>_lib</code>/<code>_cpl</code>, séquences</div><div class="ar">→</div><div class="st"><b>2. Entité</b><code>ClassMAPTable</code> / <code>ClassEtat</code> / <code>ClassMere</code></div><div class="ar">→</div><div class="st o"><b>3. Moteur</b><code>CGenUtil</code> + <code>ListeColonneTable</code></div><div class="ar">→</div><div class="st g"><b>4. Service</b><code>UserEJB</code> (transactions, droits)</div><div class="ar">→</div><div class="st p"><b>5. Pages</b><code>Page*</code>, <code>Formulaire</code>, <code>Champ</code>, <code>TableauRecherche</code></div><div class="ar">→</div><div class="st"><b>6. JSP + JS</b><code>module.jsp?but=…</code>, <code>apres*.jsp</code>, <code>apjplugins</code></div></div>'
  +'<div class="cards">'
  +GUIDE.map(function(g){return '<a class="card" href="#/guide/'+g.id+'"><b>'+esc(g.title)+'</b><small>'+esc(g.sum||'')+'</small></a>'}).join('')
  +'<a class="card" href="#/api"><b>Référence API (Javadoc)</b><small>'+API.length+' types et '+API.reduce(function(a,c){return a+c.ms.length},0)+' méthodes</small></a>'
  +'<a class="card" href="#/tree"><b>Arborescence des classes</b><small>Hiérarchie d\'héritage complète</small></a>'
  +'<a class="card" href="#/index"><b>Index A–Z</b><small>Classes et méthodes</small></a></div>'
  +'<div class="note"><b>Comment lire cette documentation ?</b>Commencez par <a href="#/guide/architecture">l\'architecture</a>, puis suivez le <a href="#/guide/flux-carton">flux complet</a> (exemple réel Carton). Le <a href="#/api">référentiel API</a> sert ensuite de Javadoc : chaque méthode est cliquable et recherchable avec la barre du haut (<kbd>/</kbd>).</div>';
  return h;
}

/* ---------- sidebar ---------- */
var sideMode='', sideFilter='';
function renderSide(section, activeId){
  var s=$('#side'), h='';
  if(section==='guide'){
    h+='<h3>Guide</h3>'+GUIDE.map(function(g,i){return '<a href="#/guide/'+g.id+'" class="'+(g.id===activeId?'active':'')+'">'+(i+1)+'. '+esc(g.title)+'</a>'}).join('');
  } else {
    h+='<div class="flt"><input id="flt" placeholder="Filtrer les classes…" value="'+esc(sideFilter)+'"></div><div id="tree">'+sideTree(activeId)+'</div>';
  }
  s.innerHTML=h;
  var f=$('#flt'); if(f) f.oninput=function(){sideFilter=f.value;$('#tree').innerHTML=sideTree(activeId)};
  var a=$('a.active',s); if(a) a.scrollIntoView({block:'center'});
}
function sideTree(active){
  var q=sideFilter.toLowerCase(), h='';
  var act = active && byId[active] ? byId[active].pkg : null;
  pkgNames.forEach(function(p){
    var l=pkgs[p].filter(function(c){return !q||c.n.toLowerCase().indexOf(q)>=0||p.toLowerCase().indexOf(q)>=0}).sort(function(a,b){return a.n.localeCompare(b.n)});
    if(!l.length) return;
    h+='<details'+((q||p===act)?' open':'')+'><summary><a href="#/pkg/'+encodeURIComponent(p)+'" style="padding:0">'+esc(p)+'</a><span class="cnt">'+l.length+'</span></summary>'
      +l.map(function(c){return '<a class="cls'+(c.id===active?' active':'')+'" href="#/class/'+encodeURIComponent(c.id)+'">'+(c.k!=='class'?'<i>'+esc(c.n)+'</i>':esc(c.n))+'</a>'}).join('')+'</details>';
  });
  return h||'<p style="padding:10px 14px;color:var(--muted)">Aucun résultat.</p>';
}

/* ---------- recherche ---------- */
var SIDX=[]; API.forEach(function(c){SIDX.push({k:'classe',n:c.n,c:c,i:-1,s:c.n.toLowerCase(),sub:c.pkg});c.ms.forEach(function(m,i){SIDX.push({k:'méthode',n:m.n+'()',c:c,i:i,s:m.n.toLowerCase(),sub:c.n})})});
GUIDE.forEach(function(g){SIDX.push({k:'guide',n:g.title,g:g,s:(g.title+' '+(g.sum||'')).toLowerCase(),sub:'Guide'})});
function doSearch(q){
  q=q.toLowerCase().trim(); var box=$('#sres'); if(!q){box.style.display='none';return}
  var r=[]; SIDX.forEach(function(x){var p=x.s.indexOf(q);if(p>=0)r.push({x:x,sc:(p===0?0:1)+(x.s===q?-2:0)+(x.k==='classe'?-1:0)+x.s.length/100})});
  r.sort(function(a,b){return a.sc-b.sc}); r=r.slice(0,40);
  box.innerHTML=r.length?r.map(function(o){var x=o.x;var href=x.g?'#/guide/'+x.g.id:'#/class/'+encodeURIComponent(x.c.id)+(x.i>=0?'~m'+x.i:'');return '<a href="'+href+'"><span class="k">'+x.k+'</span><b>'+esc(x.n)+'</b> <span style="color:var(--muted)">'+esc(x.sub)+'</span></a>'}).join(''):'<a>Aucun résultat</a>';
  box.style.display='block';
}

/* ---------- routeur ---------- */
function setTabs(t){$$('nav.tabs a').forEach(function(a){a.classList.toggle('active',a.dataset.t===t)})}
function mkToc(root){
  var hs=$$('h2,h3',root); var old=$('.toc-right'); if(old) old.remove();
  if(hs.length<3) return;
  var d=document.createElement('div'); d.className='toc-right';
  d.innerHTML=hs.map(function(h,i){h.id=h.id||('s'+i);return '<a class="'+h.tagName.toLowerCase()+'" href="#" data-id="'+h.id+'">'+esc(h.textContent)+'</a>'}).join('');
  document.body.appendChild(d);
  $$('a',d).forEach(function(a){a.onclick=function(e){e.preventDefault();var t=document.getElementById(a.dataset.id);t&&t.scrollIntoView({behavior:'smooth'})}});
}
function route(){
  var hash=decodeURIComponent(location.hash.replace(/^#\/?/,'')), parts=hash.split('/'), main=$('#main'), inner;
  var sec=parts[0]||'home', arg=parts.slice(1).join('/'), anchor=null;
  if(arg.indexOf('~')>=0){var sp=arg.split('~');arg=sp[0];anchor=sp[1]}
  var html='', tab='home', side='guide', active=null;
  if(sec==='home'||sec===''){html=renderHome();tab='home'}
  else if(sec==='guide'){active=arg||GUIDE[0].id;html=renderGuide(active);tab='guide';side='guide'}
  else if(sec==='class'){var c=byId[arg];html=c?renderClass(c):'<h1>Classe introuvable</h1>';tab='api';side='api';active=arg}
  else if(sec==='pkg'){html=renderPkg(arg);tab='api';side='api'}
  else if(sec==='api'){html=renderApiHome();tab='api';side='api'}
  else if(sec==='tree'){html=renderTree();tab='tree';side='api'}
  else if(sec==='index'){html=renderIndex(arg);tab='index';side='api'}
  main.innerHTML='<div class="inner">'+html+'</div>';
  inner=$('.inner',main);
  decorate(inner); renderSide(side,active); setTabs(tab); mkToc(inner);
  $('#side').classList.remove('open');
  if(anchor){var t=document.getElementById(anchor);if(t){t.scrollIntoView();t.style.background='rgba(248,152,29,.18)'}} else main.scrollTop=0;
  document.title='APJ — '+(sec==='class'&&byId[arg]?byId[arg].n:sec==='guide'&&guideTitle(active)?guideTitle(active).title:'Documentation');
}
window.addEventListener('hashchange',route);
document.addEventListener('DOMContentLoaded',function(){
  var si=$('#q'); si.addEventListener('input',function(){doSearch(si.value)});
  si.addEventListener('keydown',function(e){if(e.key==='Escape'){si.value='';doSearch('')}if(e.key==='Enter'){var f=$('#sres a');if(f&&f.href)location.href=f.href;doSearch('');si.blur()}});
  document.addEventListener('click',function(e){if(!e.target.closest('.search'))$('#sres').style.display='none'});
  document.addEventListener('keydown',function(e){if(e.key==='/'&&document.activeElement.tagName!=='INPUT'){e.preventDefault();si.focus()}});
  var th=localStorage.getItem('apj-theme')||'light'; document.documentElement.dataset.theme=th;
  $('#theme').onclick=function(){var n=document.documentElement.dataset.theme==='dark'?'light':'dark';document.documentElement.dataset.theme=n;try{localStorage.setItem('apj-theme',n)}catch(e){}};
  $('#burger').onclick=function(){$('#side').classList.toggle('open')};
  route();
});
})();
