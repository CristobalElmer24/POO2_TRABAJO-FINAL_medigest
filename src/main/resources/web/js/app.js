// MediGest web: esta página solo muestra datos y manda acciones; todas las reglas viven en el servidor Java (/api/*).
const $=s=>document.querySelector(s),S={v:'login',t:0,hc:'receta',hq:''},D={},sel={};
const e=s=>String(s??'').replace(/[&<>"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c]));
const fd=s=>s.slice(8,10)+'/'+s.slice(5,7)+'/'+s.slice(0,4)+' '+s.slice(11);
const NV={SIN_STOCK:['SIN STOCK','#7f8c8d'],CRITICO:['CRÍTICO','#c0392b'],BAJO:['BAJO','#d35400'],NORMAL:['NORMAL','#2980b9'],ALTO:['ALTO','#27ae60']};
const EL={VIGENTE:['VIGENTE','#27ae60'],POR_VENCER:['POR VENCER','#d35400'],VENCIDO:['VENCIDO','#c0392b'],AGOTADO:['AGOTADO','#7f8c8d']};
const bd=(t,c)=>`<span class="bd" style="background:${c}">${e(t)}</span>`;
async function api(m,u,d){const o={method:m,headers:{'X-MediGest':'1'}};if(d){o.headers['Content-Type']='application/x-www-form-urlencoded';o.body=new URLSearchParams(d)}
const r=await fetch(u,o),j=await r.json().catch(()=>({}));if(!r.ok){if(r.status===401&&S.user){S.user=null;S.v='login'}throw new Error(j.error||'Error '+r.status)}return j}
const toast=(m,err)=>{const t=$('#t');t.textContent=m;t.style.background=err?'var(--rj)':'var(--vd)';t.hidden=false;clearTimeout(toast.h);toast.h=setTimeout(()=>t.hidden=true,4000)};
// ---------- componentes
function pick(id,i){sel[id]=i;document.querySelectorAll('#'+id+' tr[data-i]').forEach(r=>r.classList.toggle('sel',+r.dataset.i===i))}
const tbl=(id,h,rows)=>`<div class="tw"><table id="${id}"><thead><tr>${h.map(x=>`<th>${x}</th>`).join('')}</tr></thead><tbody>${rows.map((r,i)=>`<tr data-i="${i}" onclick="pick('${id}',${i})" class="${sel[id]===i?'sel':''}">${r.map(c=>`<td>${c}</td>`).join('')}</tr>`).join('')||`<tr><td colspan="${h.length}" class="mu">Sin datos</td></tr>`}</tbody></table></div>`;
const cl=()=>$('#m').hidden=true,open=h=>{$('#mb').innerHTML=h;$('#m').hidden=false};
function frm(t,f,ok){open(`<h3>${t}</h3>${f.map(([id,l,v,ty])=>`<label>${l}<input id="f_${id}" type="${ty||'text'}" value="${e(v)}"></label>`).join('')}<div class="ac"><button class="b gr" onclick="cl()">Cancelar</button><button class="b az" id="ok">Aceptar</button></div>`);
$('#ok').onclick=async()=>{const v={};f.forEach(([id])=>v[id]=$('#f_'+id).value.trim());if(await ok(v))cl()}}
function ask(msg,fn){open(`<h3>Confirmar</h3><p>${msg}</p><div class="ac"><button class="b gr" onclick="cl()">Cancelar</button><button class="b vd" id="ok">Sí, continuar</button></div>`);$('#ok').onclick=()=>{cl();fn()}}
const verCons=t=>open(`<h3>Constancia de dispensación</h3><pre>${e(t)}</pre><div class="ac"><button class="b az" onclick="cl()">Cerrar</button></div>`);
const need=(id,l)=>{if(sel[id]===undefined||!(l||[])[sel[id]]){toast('Seleccione una fila de la tabla.',1);return null}return l[sel[id]]};
const bar=t=>`<div class="top"><b>${t}</b><small>${e(S.user.usuario)} (${S.user.rol})</small><button class="b gr" style="margin:0" onclick="go('menu')">Menú</button></div>`;
const tabs=n=>`<div class="tabs">${n.map((x,i)=>`<button class="${S.t===i?'on':''}" onclick="S.t=${i};render()">${x}</button>`).join('')}</div>`;
const go=v=>{S.v=v;S.t=0;render()};
// Ejecuta una acción en el servidor; si falla muestra el mensaje del servidor (reglas de negocio, permisos...)
async function act(fn,ok){try{const r=await fn();if(ok)toast(ok);await render();return r||true}catch(x){toast(x.message,1);return false}}
// ---------- carga de datos según la pantalla
async function cargar(){const v=S.v,t=S.t;
if(v==='inv'){if(t===0)D.pr=await api('GET','/api/productos');if(t===1)D.lo=await api('GET','/api/lotes');if(t===2)D.mv=await api('GET','/api/movimientos');if(t===3)D.al=await api('GET','/api/alertas')}
if(v==='far'){if(t===1)D.pr=await api('GET','/api/productos');if(t===2)D.tk=await api('GET','/api/tickets?criterio='+S.hc+'&texto='+encodeURIComponent(S.hq));
if(t===0&&S.q)D.rec=await api('GET','/api/receta?codigo='+encodeURIComponent(S.q))}
if(v==='bit')D.bi=await api('GET','/api/bitacora')}
// ---------- pantallas
const V={
login:()=>`<div class="login card"><h1>MediGest</h1><p class="mu" style="text-align:center">Sistema de Gestión Clínica</p><label>Usuario<input id="lu" autofocus></label><label>Contraseña<input id="lp" type="password"></label>
<button class="b az" style="width:100%" onclick="entrar()">Ingresar</button><p class="mu" style="font-size:.8rem">Prototipo con datos ficticios. Usuarios: admin, farmacia, inventario (contraseña igual al usuario).</p></div>`,
menu:()=>`${bar('Sistema de Gestión Clínica')}<main><div class="card"><h2 style="margin-top:0">Bienvenido, ${e(S.user.usuario)}</h2><p class="mu">Rol: ${S.user.rol}. Elija un módulo.</p>
<div class="menu"><button class="b vd" onclick="go('far')">Farmacia</button><button class="b az" onclick="go('inv')">Inventario</button>${S.user.puedeVerBitacora?'<button class="b na" onclick="go(\'bit\')">Bitácora</button>':''}<button class="b rj" onclick="salir()">Cerrar sesión</button></div></div></main>`,
inv(){let h=bar('Inventario')+'<main>'+tabs(['Productos','Lotes','Movimientos','Alertas']);
if(S.t===0)h+=`<div class="row"><button class="b vd" onclick="fProd()">Nuevo producto</button><button class="b az" onclick="fProd(1)">Editar</button><button class="b rj" onclick="delProd()">Eliminar</button><button class="b na" onclick="fEntrada()">Registrar entrada de lote</button></div>`+
tbl('tp',['Código','Nombre','Presentación','Mínimo','Total','Utilizable','Nivel'],D.pr.map(p=>[e(p.codigo),e(p.nombre),e(p.presentacion),p.stockMinimo,p.stockTotal,p.stockUtilizable,bd(...NV[p.nivel])]));
if(S.t===1)h+=`<div class="row"><button class="b na" onclick="fAjuste()">Ajustar stock del lote</button></div>`+tbl('tl',['Producto','Lote','Vence','Disponible','Estado'],D.lo.map(l=>[e(l.producto),e(l.nroLote),l.vence,l.cantidad,bd(...EL[l.estado])]));
if(S.t===2)h+=`<p class="mu">Últimos 100 movimientos de entrada, salida y ajuste</p>`+tbl('tm',['Fecha','Tipo','Lote','Cantidad','Motivo'],D.mv.map(m=>[fd(m.fecha),m.tipo,e(m.lote),m.cantidad,e(m.motivo)]));
if(S.t===3)h+=`<p class="mu">Alertas de stock mínimo y vencimiento (se actualizan solas cada 15 segundos)</p><button class="b az" onclick="render()">Revisar ahora</button>`+tbl('ta',['Tipo','Producto','Lote','Mensaje'],D.al.map(a=>[bd(a.tipo.replace('_',' '),a.tipo==='VENCIDO'?'#c0392b':a.tipo==='STOCK_BAJO'?'#d35400':'#2980b9'),e(a.producto),e(a.lote),e(a.mensaje)]));
return h+'</main>'},
far(){let h=bar('Farmacia')+'<main>'+tabs(['Dispensar','Medicamentos','Historial']);
if(S.t===0){const r=D.rec;h+=`<div class="row"><input id="rc" placeholder="Código de receta (ej. REC-001)" value="${e(S.q||'')}" onkeydown="if(event.key==='Enter')buscarRec()"><button class="b az" onclick="buscarRec()">Buscar</button></div>`;
if(!S.q||!r)h+='<p class="mu">Busque una receta para comenzar.</p>';else if(!r.existe)h+=`<div class="card" style="border-color:var(--rj)">${e(r.motivos.join(' '))}</div>`;
else h+=`<div class="card"><b>${e(r.codigo)}</b> · ${e(r.paciente)} (DNI ${e(r.documento)}) · emitida ${r.fecha} · ${bd(r.estado,r.estado==='EMITIDA'?'#27ae60':'#7f8c8d')}</div>`+tbl('td',['Medicamento','Cantidad pedida','Disponible'],r.detalles.map(d=>[e(d.medicamento),d.cantidad,d.disponible]))+
(r.valida?`<button class="b vd" onclick="confirmarDisp()">Confirmar dispensación</button>`:`<div class="card" style="border-color:var(--rj)">${e(r.motivos.join(' '))}</div>`)}
if(S.t===1)h+=tbl('tmed',['Código','Medicamento','Presentación','Unidades dispensables','Nivel'],D.pr.map(p=>[e(p.codigo),e(p.nombre),e(p.presentacion),p.stockUtilizable,bd(...NV[p.nivel])]));
if(S.t===2)h+=`<div class="row"><select id="hc"><option value="receta"${S.hc==='receta'?' selected':''}>Por receta</option><option value="paciente"${S.hc==='paciente'?' selected':''}>Por paciente</option></select><input id="hq" placeholder="(vacío = últimos tickets)" value="${e(S.hq)}" onkeydown="if(event.key==='Enter')buscarTk()"><button class="b az" onclick="buscarTk()">Buscar</button></div>
<div class="row"><button class="b az" onclick="const t=need('th',D.tk);t&&verCons(t.constancia)">Ver constancia</button><button class="b rj" onclick="anularSel()">Anular ticket</button></div>`+
tbl('th',['N°','Fecha','Receta','Paciente','Atendido por','Unidades','Estado'],D.tk.map(t=>[t.id,fd(t.fecha),e(t.receta),e(t.paciente),e(t.usuario),t.unidades,bd(t.estado,t.estado==='ANULADO'?'#c0392b':'#27ae60')]));
return h+'</main>'},
bit:()=>bar('Bitácora de auditoría')+`<main><button class="b az" onclick="render()">Actualizar</button>`+tbl('tb',['Fecha','Usuario','Acción','Entidad','Resultado'],D.bi.map(b=>{const k=b.resultado.split(':')[0];
return[fd(b.fecha),e(b.usuario),e(b.accion),e(b.entidad),bd(k,k==='EXITOSO'?'#27ae60':k==='DENEGADO'?'#d35400':'#c0392b')+' '+e(b.resultado.includes(': ')?b.resultado.split(': ').slice(1).join(': '):'')]}))+'</main>'};
async function render(){try{await cargar()}catch(x){toast(x.message,1);if(!S.user)S.v='login'}
const sc=scrollY;$('#app').innerHTML=V[S.v]();scrollTo(0,sc)}
// ---------- acciones
async function entrar(){try{S.user=await api('POST','/api/login',{usuario:$('#lu').value.trim(),clave:$('#lp').value});go('menu')}catch(x){toast(x.message,1)}}
document.addEventListener('keydown',ev=>{if(S.v==='login'&&ev.key==='Enter')entrar()});
async function salir(){try{await api('POST','/api/logout')}catch(x){}S.user=null;S.q='';D.rec=null;go('login')}
function fProd(ed){const p=ed?need('tp',D.pr):null;if(ed&&!p)return;
frm(ed?'Editar producto':'Nuevo producto',[['c','Código',p?.codigo],['n','Nombre',p?.nombre],['p','Presentación',p?.presentacion],['m','Stock mínimo',p?.stockMinimo??10,'number']],
v=>act(()=>api('POST','/api/productos',{id:p?p.id:'',codigo:v.c,nombre:v.n,presentacion:v.p,stockMinimo:v.m}),'Producto guardado.'))}
function delProd(){const p=need('tp',D.pr);if(p)ask(`¿Eliminar el producto ${e(p.nombre)}?`,()=>act(()=>api('POST','/api/productos/eliminar',{id:p.id}),'Producto eliminado.'))}
function fEntrada(){const p=need('tp',D.pr);if(!p)return;const m=new Date();m.setFullYear(m.getFullYear()+1);
frm('Entrada de '+e(p.nombre),[['n','Número de lote'],['v','Vence',m.toISOString().slice(0,10),'date'],['c','Cantidad','','number']],
v=>act(()=>api('POST','/api/entradas',{productoId:p.id,nroLote:v.n,vence:v.v,cantidad:v.c}),'Entrada registrada.'))}
function fAjuste(){const l=need('tl',D.lo);if(!l)return;frm('Ajuste del lote '+e(l.nroLote),[['d','Diferencia (+5 o -3)','','number'],['m','Motivo']],
v=>act(()=>api('POST','/api/ajustes',{loteId:l.id,diferencia:v.d,motivo:v.m}),'Ajuste registrado.'))}
async function buscarRec(){S.q=$('#rc').value.trim();if(!S.q){D.rec=null;return render()}await render()}
function confirmarDisp(){const r=D.rec;ask(`¿Dispensar la receta ${e(r.codigo)} a ${e(r.paciente)}?`,async()=>{const t=await act(()=>api('POST','/api/dispensar',{codigo:r.codigo}));if(t&&t!==true)verCons(t.constancia)})}
async function buscarTk(){S.hc=$('#hc').value;S.hq=$('#hq').value.trim();await render()}
function anularSel(){const t=need('th',D.tk);if(t)ask(`¿Anular el ticket ${t.id}? El stock será devuelto.`,()=>act(()=>api('POST','/api/tickets/anular',{id:t.id}),'Ticket anulado. El stock fue devuelto.'))}
setInterval(()=>{if(S.v==='inv'&&S.t===3&&$('#m').hidden)render()},15000);
(async()=>{try{S.user=await api('GET','/api/sesion');S.v='menu'}catch(x){S.v='login'}render()})();
