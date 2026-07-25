<script>

  import RutaProtegida from '../componentes/RutaProtegida.svelte';
  import { navigate } from 'svelte-routing';
  import { sesion } from '../tienda/tienda.js';
  import * as perroService from '../servicios/perroService.js';
  import * as usuarioService from '../servicios/usuarioService.js';
  import * as reservaService from '../servicios/reservaService.js';

  let credencial;
  sesion.subscribe(s => (credencial = s?.credencial));

  let pestanaActiva = 'perros';


  let perros = [], cargandoPerros = true, errorPerros = '';
  const PERROS_POR_PAGINA = 8;
  let paginaPerros = 0, totalPaginasPerros = 1, totalPerros = 0;
  let busquedaPerros = '';

  let mostrarFormulario = false;
  let nombre = '', raza = '', tamaño = 'Mediano', precio = '', descripcion = '', etiquetas = '', imagen = '';
  let errores = {}, exito = false, errorGuardar = '', guardando = false, editandoId = null;
  const TAMAÑOS = ['Pequeño', 'Mediano', 'Grande'];


  async function cargarPerros(pagina = 0) {
    cargandoPerros = true; errorPerros = '';
    try {
      const data = busquedaPerros.trim()
        ? await perroService.buscar(busquedaPerros.trim(), pagina, PERROS_POR_PAGINA, credencial)
        : await perroService.listar(pagina, PERROS_POR_PAGINA, credencial);
      perros = data.content; paginaPerros = data.number;
      totalPaginasPerros = data.totalPages || 1; totalPerros = data.totalElements || 0;
    } catch (e) { errorPerros = e.message || 'No se pudo cargar el inventario.'; }
    finally { cargandoPerros = false; }
  }

  function validar() {
    const e = {};
    if (!nombre.trim()) e.nombre = 'El nombre es obligatorio.';
    else if (nombre.trim().length < 2) e.nombre = 'Mínimo 2 caracteres.';
    if (!raza.trim()) e.raza = 'La raza es obligatoria.';
    else if (raza.trim().length < 3) e.raza = 'Mínimo 3 caracteres.';
    if (!precio) e.precio = 'El precio es obligatorio.';
    else if (isNaN(Number(precio)) || Number(precio) <= 0) e.precio = 'Debe ser un número positivo.';
    else if (Number(precio) > 100) e.precio = 'No puede superar $100/hora.';
    if (!descripcion.trim()) e.descripcion = 'La descripción es obligatoria.';
    else if (descripcion.trim().length < 20) e.descripcion = 'Mínimo 20 caracteres.';
    if (!etiquetas.trim()) e.etiquetas = 'Ingresa al menos una etiqueta.';
    if (!imagen.trim()) e.imagen = 'La ruta de imagen es obligatoria.';
    else if (!imagen.trim().startsWith('/')) e.imagen = 'La ruta debe comenzar con / (ej: /perro.jpg).';
    return e;
  }

  function limpiarFormulario() {
    nombre = raza = precio = descripcion = etiquetas = imagen = '';
    tamaño = 'Mediano'; errores = {}; editandoId = null;
    mostrarFormulario = false; errorGuardar = '';
  }

  function editarPerro(p) {
    editandoId = p.id; nombre = p.nombre; raza = p.raza; tamaño = p.tamaño;
    precio = p.precio; descripcion = p.descripcion;
    etiquetas = p.etiquetas.join(', '); imagen = p.imagen;
    mostrarFormulario = true; errores = {};
    document.getElementById('formulario-perro')?.scrollIntoView({ behavior: 'smooth' });
  }

  async function guardarPerro() {
    exito = false; errorGuardar = '';
    errores = validar();
    if (Object.keys(errores).length > 0) return;
    guardando = true;
    try {
      const datos = { nombre: nombre.trim(), raza: raza.trim(), tamaño, precio, descripcion: descripcion.trim(), etiquetas: etiquetas.trim(), imagen: imagen.trim() };
      if (editandoId) await perroService.actualizar(editandoId, datos, credencial); // PUT /api/perros/{id}
      else await perroService.crear(datos, credencial);                              // POST /api/perros
      limpiarFormulario(); exito = true;
      setTimeout(() => (exito = false), 4000);
      await cargarPerros(paginaPerros);
    } catch (e) { errorGuardar = e.message || 'No se pudo guardar el perrito.'; }
    finally { guardando = false; }
  }

  async function alternarDisponibilidad(p) {
    try { await perroService.actualizarDisponibilidad(p.id, !p.disponible, credencial); await cargarPerros(paginaPerros); }
    catch (e) { errorPerros = e.message || 'No se pudo actualizar.'; }
  }

  async function darDeBaja(p) {
    if (!confirm(`¿Dar de baja a ${p.nombre}?`)) return;
    try { await perroService.eliminar(p.id, credencial); await cargarPerros(paginaPerros); }
    catch (e) { errorPerros = e.message || 'No se pudo dar de baja.'; }
  }

  async function reactivarPerro(p) {
    try { await perroService.reactivar(p.id, credencial); await cargarPerros(paginaPerros); }
    catch (e) { errorPerros = e.message || 'No se pudo reactivar.'; }
  }


  let usuarios = [], cargandoUsuarios = true, errorUsuarios = '';
  const USUARIOS_POR_PAGINA = 8;
  let paginaUsuarios = 0, totalPaginasUsuarios = 1, totalUsuarios = 0;
  let busquedaUsuarios = '';

  async function cargarUsuarios(pagina = 0) {
    cargandoUsuarios = true; errorUsuarios = '';
    try {
      const data = busquedaUsuarios.trim()
        ? await usuarioService.buscar(busquedaUsuarios.trim(), pagina, USUARIOS_POR_PAGINA, credencial)
        : await usuarioService.listar(pagina, USUARIOS_POR_PAGINA, credencial);
      usuarios = data.content; paginaUsuarios = data.number;
      totalPaginasUsuarios = data.totalPages || 1; totalUsuarios = data.totalElements || 0;
    } catch (e) { errorUsuarios = e.message || 'No se pudo cargar los usuarios.'; }
    finally { cargandoUsuarios = false; }
  }

  async function cambiarRol(u, nuevoRol) {
    try { await usuarioService.actualizarRol(u.id, nuevoRol, credencial); await cargarUsuarios(paginaUsuarios); }
    catch (e) { errorUsuarios = e.message || 'No se pudo actualizar el rol.'; }
  }

  async function darDeBajaUsuario(u) {
    if (!confirm(`¿Dar de baja a ${u.nombre}?`)) return;
    try { await usuarioService.eliminar(u.id, credencial); await cargarUsuarios(paginaUsuarios); }
    catch (e) { errorUsuarios = e.message || 'No se pudo dar de baja.'; }
  }

  async function reactivarUsuario(u) {
    try { await usuarioService.reactivar(u.id, credencial); await cargarUsuarios(paginaUsuarios); }
    catch (e) { errorUsuarios = e.message || 'No se pudo reactivar.'; }
  }


  let reservas = [], cargandoReservas = true, errorReservas = '';
  const RESERVAS_POR_PAGINA = 8;
  let paginaReservas = 0, totalPaginasReservas = 1, totalReservas = 0;

  async function cargarReservas(pagina = 0) {
    cargandoReservas = true; errorReservas = '';
    try {
      const data = await reservaService.listarTodas(pagina, RESERVAS_POR_PAGINA, credencial);
      reservas = data.content; paginaReservas = data.number;
      totalPaginasReservas = data.totalPages || 1; totalReservas = data.totalElements || 0;
    } catch (e) { errorReservas = e.message || 'No se pudo cargar las reservas.'; }
    finally { cargandoReservas = false; }
  }

  async function completarReservaAdmin(r) {
    try { await reservaService.actualizarEstado(r.id, 'COMPLETADA', credencial); await cargarReservas(paginaReservas); }
    catch (e) { errorReservas = e.message || 'No se pudo completar.'; }
  }

  async function cancelarReservaAdmin(r) {
    try { await reservaService.actualizarEstado(r.id, 'CANCELADA', credencial); await cargarReservas(paginaReservas); }
    catch (e) { errorReservas = e.message || 'No se pudo cancelar.'; }
  }


  function cambiarPestana(p) {
    pestanaActiva = p;
    if (p === 'perros' && perros.length === 0) cargarPerros(0);
    if (p === 'usuarios' && usuarios.length === 0) cargarUsuarios(0);
    if (p === 'reservas' && reservas.length === 0) cargarReservas(0);
  }

  cargarPerros(0);
</script>

<RutaProtegida rutaActual="/admin" soloAdmin={true}>
  <main>
    <div class="admin-layout">

      <aside class="admin-sidebar">
        <div class="sidebar-brand">
          <span class="brand-icon">⚙️</span>
          <div>
            <strong>Panel Admin</strong>
            <span>PetRent</span>
          </div>
        </div>

        <nav class="admin-nav">
          <button class="admin-nav-item" class:activo={pestanaActiva === 'perros'} on:click={() => cambiarPestana('perros')}>
            <span class="nav-icon">🐾</span>
            <div class="nav-label">
              <span>Perritos</span>
              <span class="nav-count">{totalPerros}</span>
            </div>
          </button>
          <button class="admin-nav-item" class:activo={pestanaActiva === 'usuarios'} on:click={() => cambiarPestana('usuarios')}>
            <span class="nav-icon">👥</span>
            <div class="nav-label">
              <span>Usuarios</span>
              <span class="nav-count">{totalUsuarios}</span>
            </div>
          </button>
          <button class="admin-nav-item" class:activo={pestanaActiva === 'reservas'} on:click={() => cambiarPestana('reservas')}>
            <span class="nav-icon">📋</span>
            <div class="nav-label">
              <span>Reservas</span>
              <span class="nav-count">{totalReservas}</span>
            </div>
          </button>
        </nav>

        <div class="sidebar-footer-admin">
          <button class="btn-volver-admin" on:click={() => navigate('/')}>← Volver al sitio</button>
        </div>
      </aside>

      <div class="admin-contenido">

        {#if pestanaActiva === 'perros'}
          <div class="admin-tab-header">
            <div>
              <h1>Inventario de perritos</h1>
              <p>Gestiona el catálogo completo de perritos disponibles para alquilar.</p>
            </div>
            {#if !mostrarFormulario}
              <button class="btn-agregar-nuevo" on:click={() => (mostrarFormulario = true)}>
                + Agregar perrito
              </button>
            {/if}
          </div>

          {#if exito}
            <div class="alerta exito"><span>✅</span> ¡Perrito guardado correctamente!</div>
          {/if}

          {#if mostrarFormulario}
            <div class="form-card" id="formulario-perro">
              <div class="form-card-header">
                <h2>{editandoId ? '✏️ Editar perrito' : '➕ Nuevo perrito'}</h2>
                <button class="btn-cerrar-form" on:click={limpiarFormulario}>✕</button>
              </div>
              <p class="form-hint">Los campos con <span class="req">*</span> son obligatorios.</p>

              {#if errorGuardar}<div class="alerta error"><span>⚠️</span>{errorGuardar}</div>{/if}

              <div class="campos-2col">
                <div class="campo-f">
                  <label for="f-nombre">Nombre <span class="req">*</span></label>
                  <input id="f-nombre" type="text" placeholder="Ej: Bruno" bind:value={nombre} class:campo-error={errores.nombre} />
                  {#if errores.nombre}<span class="err-msg">{errores.nombre}</span>{/if}
                </div>
                <div class="campo-f">
                  <label for="f-raza">Raza <span class="req">*</span></label>
                  <input id="f-raza" type="text" placeholder="Ej: Labrador" bind:value={raza} class:campo-error={errores.raza} />
                  {#if errores.raza}<span class="err-msg">{errores.raza}</span>{/if}
                </div>
                <div class="campo-f">
                  <label for="f-tamaño">Tamaño <span class="req">*</span></label>
                  <select id="f-tamaño" bind:value={tamaño}>
                    {#each TAMAÑOS as t}<option value={t}>{t}</option>{/each}
                  </select>
                </div>
                <div class="campo-f">
                  <label for="f-precio">Precio/hora ($) <span class="req">*</span></label>
                  <input id="f-precio" type="number" min="0.5" max="100" step="0.5" placeholder="Ej: 6.50" bind:value={precio} class:campo-error={errores.precio} />
                  {#if errores.precio}<span class="err-msg">{errores.precio}</span>{/if}
                </div>
              </div>

              <div class="campo-f">
                <label for="f-desc">Descripción <span class="req">*</span> <span class="char-count">({descripcion.length}/20 mín.)</span></label>
                <textarea id="f-desc" rows="3" placeholder="Describe la personalidad del perrito…" bind:value={descripcion} class:campo-error={errores.descripcion}></textarea>
                {#if errores.descripcion}<span class="err-msg">{errores.descripcion}</span>{/if}
              </div>

              <div class="campos-2col">
                <div class="campo-f">
                  <label for="f-etiquetas">Etiquetas <span class="req">*</span></label>
                  <input id="f-etiquetas" type="text" placeholder="Juguetón, Amigable, Energético" bind:value={etiquetas} class:campo-error={errores.etiquetas} />
                  {#if errores.etiquetas}<span class="err-msg">{errores.etiquetas}</span>{/if}
                </div>
                <div class="campo-f">
                  <label for="f-imagen">Ruta de imagen <span class="req">*</span></label>
                  <input id="f-imagen" type="text" placeholder="/nombre-perro.jpg" bind:value={imagen} class:campo-error={errores.imagen} />
                  {#if errores.imagen}<span class="err-msg">{errores.imagen}</span>{/if}
                </div>
              </div>

              <div class="form-acciones">
                <button class="btn-guardar" on:click={guardarPerro} disabled={guardando}>
                  {#if guardando}<span class="spinner"></span>Guardando…
                  {:else}{editandoId ? '✅ Guardar cambios' : '✅ Registrar perrito'}{/if}
                </button>
                <button class="btn-cancelar-form" on:click={limpiarFormulario}>Cancelar</button>
              </div>
            </div>
          {/if}

          <div class="tabla-seccion">
            <div class="tabla-controles">
              <div class="busqueda-admin">
                <span>🔍</span>
                <input type="text" placeholder="Buscar por nombre o raza…" bind:value={busquedaPerros}
                       on:keydown={e => e.key === 'Enter' && cargarPerros(0)} />
                {#if busquedaPerros}
                  <button on:click={() => { busquedaPerros = ''; cargarPerros(0); }}>✕</button>
                {/if}
              </div>
              <button class="btn-buscar-admin" on:click={() => cargarPerros(0)}>Buscar</button>
              <span class="total-label">{totalPerros} registros</span>
            </div>

            {#if errorPerros}<div class="alerta error"><span>⚠️</span>{errorPerros}</div>{/if}

            <div class="tabla-wrap">
              <table>
                <thead>
                  <tr>
                    <th>ID</th><th>Nombre</th><th>Raza</th><th>Tamaño</th>
                    <th>Precio/h</th><th>Disponible</th><th>Estado</th><th>Cal.</th><th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {#if cargandoPerros}
                    <tr><td colspan="9" class="td-estado"><div class="loader-ring-sm"></div> Cargando…</td></tr>
                  {:else if perros.length === 0}
                    <tr><td colspan="9" class="td-estado">🐾 No se encontraron perritos.</td></tr>
                  {:else}
                    {#each perros as p (p.id)}
                      <tr class:tr-inactivo={!p.activo}>
                        <td class="td-id">{p.id}</td>
                        <td><strong>{p.nombre}</strong></td>
                        <td class="td-suave">{p.raza}</td>
                        <td><span class="chip-tamano">{p.tamaño}</span></td>
                        <td class="td-precio">${p.precio.toFixed(2)}</td>
                        <td>
                          <button class="toggle-disp" class:on={p.disponible}
                            on:click={() => alternarDisponibilidad(p)} disabled={!p.activo}>
                            {p.disponible ? 'Sí' : 'No'}
                          </button>
                        </td>
                        <td>
                          <span class="estado-chip" class:activo={p.activo}>{p.activo ? 'Activo' : 'Baja'}</span>
                        </td>
                        <td class="td-suave">{p.calificacion > 0 ? `⭐ ${p.calificacion}` : '—'}</td>
                        <td class="td-acciones">
                          <button class="btn-accion editar" on:click={() => editarPerro(p)}>Editar</button>
                          {#if p.activo}
                            <button class="btn-accion baja" on:click={() => darDeBaja(p)}>Dar de baja</button>
                          {:else}
                            <button class="btn-accion editar" on:click={() => reactivarPerro(p)}>Reactivar</button>
                          {/if}
                        </td>
                      </tr>
                    {/each}
                  {/if}
                </tbody>
              </table>
            </div>

            {#if totalPaginasPerros > 1}
              <div class="paginador">
                <button class="pag-btn" disabled={paginaPerros === 0} on:click={() => cargarPerros(paginaPerros - 1)}>← Ant.</button>
                <span class="pag-info">Pág. {paginaPerros + 1} / {totalPaginasPerros}</span>
                <button class="pag-btn" disabled={paginaPerros >= totalPaginasPerros - 1} on:click={() => cargarPerros(paginaPerros + 1)}>Sig. →</button>
              </div>
            {/if}
          </div>

        {:else if pestanaActiva === 'usuarios'}
          <div class="admin-tab-header">
            <div>
              <h1>Gestión de usuarios</h1>
              <p>Administra cuentas, roles y estados de los usuarios registrados.</p>
            </div>
          </div>

          <div class="tabla-seccion">
            <div class="tabla-controles">
              <div class="busqueda-admin">
                <span>🔍</span>
                <input type="text" placeholder="Buscar por nombre o correo…" bind:value={busquedaUsuarios}
                       on:keydown={e => e.key === 'Enter' && cargarUsuarios(0)} />
                {#if busquedaUsuarios}
                  <button on:click={() => { busquedaUsuarios = ''; cargarUsuarios(0); }}>✕</button>
                {/if}
              </div>
              <button class="btn-buscar-admin" on:click={() => cargarUsuarios(0)}>Buscar</button>
              <span class="total-label">{totalUsuarios} registros</span>
            </div>

            {#if errorUsuarios}<div class="alerta error"><span>⚠️</span>{errorUsuarios}</div>{/if}

            <div class="tabla-wrap">
              <table>
                <thead>
                  <tr><th>ID</th><th>Nombre</th><th>Correo</th><th>Rol</th><th>Estado</th><th>Acciones</th></tr>
                </thead>
                <tbody>
                  {#if cargandoUsuarios}
                    <tr><td colspan="6" class="td-estado"><div class="loader-ring-sm"></div> Cargando…</td></tr>
                  {:else if usuarios.length === 0}
                    <tr><td colspan="6" class="td-estado">👥 No se encontraron usuarios.</td></tr>
                  {:else}
                    {#each usuarios as u (u.id)}
                      <tr class:tr-inactivo={!u.activo}>
                        <td class="td-id">{u.id}</td>
                        <td><strong>{u.nombre}</strong></td>
                        <td class="td-suave">{u.correo}</td>
                        <td>
                          <select class="select-rol" value={u.rol} on:change={e => cambiarRol(u, e.target.value)}>
                            <option value="usuario">usuario</option>
                            <option value="admin">admin</option>
                          </select>
                        </td>
                        <td>
                          <span class="estado-chip" class:activo={u.activo}>{u.activo ? 'Activo' : 'Baja'}</span>
                        </td>
                        <td class="td-acciones">
                          {#if u.activo}
                            <button class="btn-accion baja" on:click={() => darDeBajaUsuario(u)}>Dar de baja</button>
                          {:else}
                            <button class="btn-accion editar" on:click={() => reactivarUsuario(u)}>Reactivar</button>
                          {/if}
                        </td>
                      </tr>
                    {/each}
                  {/if}
                </tbody>
              </table>
            </div>

            {#if totalPaginasUsuarios > 1}
              <div class="paginador">
                <button class="pag-btn" disabled={paginaUsuarios === 0} on:click={() => cargarUsuarios(paginaUsuarios - 1)}>← Ant.</button>
                <span class="pag-info">Pág. {paginaUsuarios + 1} / {totalPaginasUsuarios}</span>
                <button class="pag-btn" disabled={paginaUsuarios >= totalPaginasUsuarios - 1} on:click={() => cargarUsuarios(paginaUsuarios + 1)}>Sig. →</button>
              </div>
            {/if}
          </div>

        {:else if pestanaActiva === 'reservas'}
          <div class="admin-tab-header">
            <div>
              <h1>Todas las reservas</h1>
              <p>Consulta y gestiona el estado de todas las reservas del sistema.</p>
            </div>
          </div>

          <div class="tabla-seccion">
            {#if errorReservas}<div class="alerta error"><span>⚠️</span>{errorReservas}</div>{/if}

            <div class="tabla-wrap">
              <table>
                <thead>
                  <tr><th>ID</th><th>Usuario</th><th>Perro</th><th>Horas</th><th>Total</th><th>Estado</th><th>Pago</th><th>Acciones</th></tr>
                </thead>
                <tbody>
                  {#if cargandoReservas}
                    <tr><td colspan="8" class="td-estado"><div class="loader-ring-sm"></div> Cargando…</td></tr>
                  {:else if reservas.length === 0}
                    <tr><td colspan="8" class="td-estado">📋 No se encontraron reservas.</td></tr>
                  {:else}
                    {#each reservas as r (r.id)}
                      <tr>
                        <td class="td-id">{r.id}</td>
                        <td class="td-suave">{r.nombreUsuario}</td>
                        <td><strong>{r.nombrePerro}</strong></td>
                        <td>{r.horas}h</td>
                        <td class="td-precio">${Number(r.precioTotal).toFixed(2)}</td>
                        <td>
                          <span class="estado-chip"
                            class:activo={r.estado === 'COMPLETADA'}
                            class:confirmada={r.estado === 'CONFIRMADA'}
                            class:cancelada={r.estado === 'CANCELADA'}>
                            {r.estado}
                          </span>
                        </td>
                        <td>
                          <span class="pago-chip" class:pagado={r.estadoPago === 'PAGADO'}>{r.estadoPago || '—'}</span>
                        </td>
                        <td class="td-acciones">
                          {#if r.estado === 'CONFIRMADA'}
                            <button class="btn-accion completar" on:click={() => completarReservaAdmin(r)}>Completar</button>
                            <button class="btn-accion baja" on:click={() => cancelarReservaAdmin(r)}>Cancelar</button>
                          {/if}
                        </td>
                      </tr>
                    {/each}
                  {/if}
                </tbody>
              </table>
            </div>

            {#if totalPaginasReservas > 1}
              <div class="paginador">
                <button class="pag-btn" disabled={paginaReservas === 0} on:click={() => cargarReservas(paginaReservas - 1)}>← Ant.</button>
                <span class="pag-info">Pág. {paginaReservas + 1} / {totalPaginasReservas}</span>
                <button class="pag-btn" disabled={paginaReservas >= totalPaginasReservas - 1} on:click={() => cargarReservas(paginaReservas + 1)}>Sig. →</button>
              </div>
            {/if}
          </div>
        {/if}

      </div>
    </div>
  </main>
</RutaProtegida>

<style>
  main { min-height: calc(100vh - 68px); background: var(--fondo); }

  .admin-layout {
    display: grid;
    grid-template-columns: 260px 1fr;
    min-height: calc(100vh - 68px);
  }

  /* ── Sidebar ── */
  .admin-sidebar {
    background: var(--fondo-suave);
    padding: 1.5rem 1rem;
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
    border-right: 1px solid var(--borde);
    position: sticky;
    top: 68px;
    height: calc(100vh - 68px);
    overflow-y: auto;
  }
  .sidebar-brand {
    display: flex; align-items: center; gap: 0.75rem;
    padding: 0.5rem 0.5rem 1rem;
    border-bottom: 1px solid var(--borde);
  }
  .brand-icon { font-size: 1.5rem; }
  .sidebar-brand strong { display: block; font-size: 0.95rem; font-weight: 800; color: var(--texto); }
  .sidebar-brand span { font-size: 0.75rem; color: var(--texto-suave); }

  .admin-nav { display: flex; flex-direction: column; gap: 0.25rem; }
  .admin-nav-item {
    display: flex; align-items: center; gap: 0.75rem;
    padding: 0.75rem 1rem;
    border-radius: var(--radio-md);
    border: none; background: none;
    cursor: pointer; text-align: left;
    transition: background 0.15s;
    width: 100%;
  }
  .admin-nav-item:hover { background: var(--fondo-suave2); }
  .admin-nav-item.activo { background: var(--naranja-suave); }
  .admin-nav-item .nav-icon { font-size: 1.1rem; flex-shrink: 0; }
  .nav-label {
    display: flex; justify-content: space-between; align-items: center;
    flex: 1;
  }
  .nav-label span:first-child { font-size: 0.9rem; font-weight: 600; color: var(--texto-suave); }
  .admin-nav-item.activo .nav-label span:first-child { color: var(--color-primario); }
  .nav-count {
    font-size: 0.72rem; font-weight: 700; padding: 2px 7px;
    border-radius: var(--radio-full); background: var(--borde); color: var(--texto-suave);
  }
  .admin-nav-item.activo .nav-count { background: var(--naranja-mid); color: var(--color-primario); }

  .sidebar-footer-admin { margin-top: auto; }
  .btn-volver-admin {
    width: 100%; padding: 0.65rem;
    border-radius: var(--radio-md);
    background: none; border: 1px solid var(--borde);
    color: var(--texto-suave); font-size: 0.85rem; font-weight: 500;
    cursor: pointer; transition: all 0.15s; text-align: center;
  }
  .btn-volver-admin:hover { border-color: var(--borde-fuerte); color: var(--texto); }

  /* ── Contenido ── */
  .admin-contenido {
    padding: 2rem 2rem 4rem;
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .admin-tab-header {
    display: flex; justify-content: space-between; align-items: flex-start;
    flex-wrap: wrap; gap: 1rem;
    padding-bottom: 1.25rem;
    border-bottom: 1px solid var(--borde);
  }
  .admin-tab-header h1 {
    font-size: 1.7rem; font-weight: 800; color: var(--texto);
    letter-spacing: -0.02em; margin-bottom: 0.2rem;
  }
  .admin-tab-header p { font-size: 0.9rem; color: var(--texto-suave); }

  .btn-agregar-nuevo {
    padding: 0.7rem 1.4rem; border-radius: var(--radio-lg);
    background: var(--color-primario); color: #fff;
    border: none; font-size: 0.9rem; font-weight: 700;
    cursor: pointer; transition: background 0.15s, transform 0.15s;
    box-shadow: 0 4px 12px rgba(var(--color-primario-rgb), 0.35);
    white-space: nowrap; flex-shrink: 0;
  }
  .btn-agregar-nuevo:hover { background: var(--color-primario-hover); transform: translateY(-1px); }

  /* Alertas */
  .alerta {
    display: flex; align-items: center; gap: 0.65rem;
    padding: 0.8rem 1rem; border-radius: var(--radio-md);
    font-size: 0.88rem; font-weight: 500;
  }
  .alerta.exito { background: var(--verde-suave); color: var(--verde-texto); border-left: 4px solid var(--verde-texto); }
  .alerta.error { background: var(--rojo-suave); color: var(--rojo-texto); border-left: 4px solid var(--rojo-texto); }

  /* Form card */
  .form-card {
    background: var(--tarjeta); border: 1px solid var(--borde);
    border-radius: var(--radio-2xl); padding: 1.75rem;
    box-shadow: var(--sombra-sm);
    display: flex; flex-direction: column; gap: 1rem;
  }
  .form-card-header {
    display: flex; justify-content: space-between; align-items: center;
  }
  .form-card-header h2 { font-size: 1.1rem; font-weight: 700; color: var(--texto); }
  .btn-cerrar-form {
    width: 32px; height: 32px; border-radius: 50%;
    border: 1px solid var(--borde); background: var(--fondo-suave);
    color: var(--texto-suave); font-size: 0.85rem;
    cursor: pointer; transition: all 0.15s;
    display: flex; align-items: center; justify-content: center;
  }
  .btn-cerrar-form:hover { background: var(--rojo-suave); color: var(--rojo-texto); border-color: var(--rojo-texto); }
  .form-hint { font-size: 0.82rem; color: var(--texto-suave); }
  .req { color: var(--color-primario); }
  .char-count { font-size: 0.72rem; color: var(--texto-muy-suave); font-weight: 400; }

  .campos-2col { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
  .campo-f { display: flex; flex-direction: column; gap: 0.35rem; }
  .campo-f label { font-size: 0.83rem; font-weight: 600; color: var(--texto); display: flex; align-items: center; gap: 0.3rem; }
  .campo-f input, .campo-f select, .campo-f textarea {
    padding: 0.65rem 0.9rem;
    border: 1.5px solid var(--borde);
    border-radius: var(--radio-md);
    font-size: 0.9rem;
    background: var(--fondo); color: var(--texto);
    transition: border-color 0.2s, box-shadow 0.2s;
    outline: none; box-sizing: border-box;
  }
  .campo-f input:focus, .campo-f select:focus, .campo-f textarea:focus {
    border-color: var(--color-primario);
    box-shadow: 0 0 0 3px rgba(var(--color-primario-rgb), 0.1);
  }
  .campo-error { border-color: #e53e3e !important; }
  .campo-f textarea { resize: vertical; min-height: 85px; }
  .err-msg { font-size: 0.77rem; color: var(--rojo-texto); font-weight: 500; }

  .form-acciones { display: flex; gap: 0.75rem; flex-wrap: wrap; }
  .btn-guardar {
    padding: 0.75rem 1.5rem; border-radius: var(--radio-lg);
    background: var(--color-primario); color: #fff;
    border: none; font-size: 0.9rem; font-weight: 700;
    cursor: pointer; transition: background 0.15s;
    display: flex; align-items: center; gap: 0.4rem;
  }
  .btn-guardar:hover:not(:disabled) { background: var(--color-primario-hover); }
  .btn-guardar:disabled { background: var(--borde-fuerte); cursor: not-allowed; }
  .btn-cancelar-form {
    padding: 0.72rem 1.25rem; border-radius: var(--radio-lg);
    background: var(--fondo-suave); border: 1px solid var(--borde);
    color: var(--texto-suave); font-size: 0.9rem; font-weight: 600;
    cursor: pointer; transition: background 0.15s;
  }
  .btn-cancelar-form:hover { background: var(--fondo-suave2); }

  /* Tabla */
  .tabla-seccion { display: flex; flex-direction: column; gap: 0.85rem; }
  .tabla-controles {
    display: flex; align-items: center; gap: 0.75rem; flex-wrap: wrap;
  }
  .busqueda-admin {
    display: flex; align-items: center; gap: 0.5rem;
    background: var(--tarjeta); border: 1.5px solid var(--borde);
    border-radius: var(--radio-md); padding: 0.45rem 0.8rem;
    flex: 1; min-width: 200px; transition: border-color 0.2s;
  }
  .busqueda-admin:focus-within { border-color: var(--color-primario); }
  .busqueda-admin input {
    flex: 1; border: none; background: none;
    font-size: 0.88rem; color: var(--texto); outline: none;
  }
  .busqueda-admin button {
    background: none; border: none; color: var(--texto-muy-suave);
    cursor: pointer; font-size: 0.82rem; padding: 2px; transition: color 0.15s;
  }
  .busqueda-admin button:hover { color: var(--rojo-texto); }
  .btn-buscar-admin {
    padding: 0.5rem 1rem; border-radius: var(--radio-md);
    background: var(--color-primario); color: #fff;
    border: none; font-size: 0.85rem; font-weight: 700;
    cursor: pointer; transition: background 0.15s; white-space: nowrap;
  }
  .btn-buscar-admin:hover { background: var(--color-primario-hover); }
  .total-label {
    font-size: 0.78rem; font-weight: 700; color: var(--color-primario);
    background: var(--naranja-suave); padding: 3px 10px;
    border-radius: var(--radio-full); white-space: nowrap;
  }

  .tabla-wrap {
    overflow-x: auto;
    border: 1px solid var(--borde);
    border-radius: var(--radio-xl);
    box-shadow: var(--sombra-xs);
  }
  table { width: 100%; border-collapse: collapse; background: var(--tarjeta); }
  thead { background: var(--fondo-suave); }
  th {
    padding: 0.7rem 1rem; text-align: left;
    font-size: 0.73rem; font-weight: 700; color: var(--texto-suave);
    text-transform: uppercase; letter-spacing: 0.06em;
    border-bottom: 1px solid var(--borde); white-space: nowrap;
  }
  td {
    padding: 0.72rem 1rem; font-size: 0.87rem; color: var(--texto);
    border-bottom: 1px solid var(--borde); vertical-align: middle;
  }
  tr:last-child td { border-bottom: none; }
  tr:hover td { background: var(--fondo-suave); }
  .tr-inactivo td { opacity: 0.5; }
  .td-id { font-size: 0.75rem; color: var(--texto-muy-suave); }
  .td-suave { color: var(--texto-suave); font-size: 0.84rem; }
  .td-precio { font-weight: 700; color: var(--color-primario); }
  .td-estado { text-align: center; color: var(--texto-suave); padding: 2.5rem; font-size: 0.9rem; }
  .td-acciones { display: flex; gap: 0.35rem; flex-wrap: wrap; }

  /* Chips tabla */
  .chip-tamano {
    font-size: 0.72rem; font-weight: 600; padding: 2px 8px;
    border-radius: var(--radio-full);
    background: var(--azul-suave); color: var(--azul-texto);
  }
  .estado-chip {
    font-size: 0.72rem; font-weight: 700; padding: 3px 10px;
    border-radius: var(--radio-full); text-transform: uppercase;
    background: var(--rojo-suave); color: var(--rojo-texto);
    white-space: nowrap;
  }
  .estado-chip.activo { background: var(--verde-suave); color: var(--verde-texto); }
  .estado-chip.confirmada { background: var(--amarillo-suave); color: var(--amarillo-texto); }
  .estado-chip.cancelada { background: var(--rojo-suave); color: var(--rojo-texto); }
  .pago-chip {
    font-size: 0.72rem; font-weight: 600; padding: 2px 8px;
    border-radius: var(--radio-full);
    background: var(--fondo-suave2); color: var(--texto-suave);
  }
  .pago-chip.pagado { background: var(--verde-suave); color: var(--verde-texto); }

  .toggle-disp {
    font-size: 0.72rem; font-weight: 700; padding: 3px 12px;
    border-radius: var(--radio-full); border: none; cursor: pointer;
    background: var(--rojo-suave); color: var(--rojo-texto);
    transition: all 0.15s;
  }
  .toggle-disp.on { background: var(--verde-suave); color: var(--verde-texto); }
  .toggle-disp:disabled { opacity: 0.4; cursor: not-allowed; }

  .select-rol {
    padding: 4px 8px; border-radius: var(--radio-sm);
    border: 1.5px solid var(--borde); background: var(--fondo);
    color: var(--texto); font-size: 0.83rem; cursor: pointer;
    transition: border-color 0.15s; outline: none;
  }
  .select-rol:focus { border-color: var(--color-primario); }

  .btn-accion {
    padding: 4px 10px; border-radius: var(--radio-sm);
    border: 1px solid var(--borde); background: none;
    font-size: 0.77rem; font-weight: 600; cursor: pointer;
    transition: all 0.15s; white-space: nowrap;
    color: var(--texto-suave);
  }
  .btn-accion.editar:hover { background: var(--azul-suave); color: var(--azul-texto); border-color: var(--azul-texto); }
  .btn-accion.baja:hover { background: var(--rojo-suave); color: var(--rojo-texto); border-color: var(--rojo-texto); }
  .btn-accion.completar:hover { background: var(--verde-suave); color: var(--verde-texto); border-color: var(--verde-texto); }

  /* Paginador */
  .paginador {
    display: flex; justify-content: center; align-items: center;
    gap: 1rem; padding: 0.5rem;
  }
  .pag-btn {
    padding: 7px 14px; border-radius: var(--radio-md);
    border: 1.5px solid var(--borde); background: var(--tarjeta);
    color: var(--texto-suave); font-size: 0.85rem; font-weight: 600;
    cursor: pointer; transition: all 0.15s;
  }
  .pag-btn:hover:not(:disabled) { border-color: var(--color-primario); color: var(--color-primario); background: var(--naranja-suave); }
  .pag-btn:disabled { opacity: 0.4; cursor: not-allowed; }
  .pag-info { font-size: 0.82rem; color: var(--texto-suave); }

  /* Loader inline */
  .loader-ring-sm {
    display: inline-block;
    width: 18px; height: 18px;
    border: 2px solid var(--borde);
    border-top-color: var(--color-primario);
    border-radius: 50%;
    animation: spin 0.7s linear infinite;
    vertical-align: middle; margin-right: 0.4rem;
  }
  @keyframes spin { to { transform: rotate(360deg); } }

  .spinner {
    width: 14px; height: 14px;
    border: 2px solid rgba(255,255,255,0.4);
    border-top-color: #fff; border-radius: 50%;
    animation: spin 0.7s linear infinite;
  }

  /* Responsive */
  @media (max-width: 1024px) {
    .admin-layout { grid-template-columns: 220px 1fr; }
  }
  @media (max-width: 768px) {
    .admin-layout { grid-template-columns: 1fr; }
    .admin-sidebar {
      position: static; height: auto;
      flex-direction: row; flex-wrap: wrap;
      padding: 1rem; gap: 0.75rem;
    }
    .admin-nav { flex-direction: row; flex-wrap: wrap; }
    .admin-nav-item { flex: 1; min-width: 100px; justify-content: center; }
    .nav-label { justify-content: center; gap: 0.4rem; }
    .sidebar-footer-admin { width: 100%; }
    .btn-volver-admin { text-align: center; }
    .admin-contenido { padding: 1.25rem 1rem 3rem; }
    .campos-2col { grid-template-columns: 1fr; }
  }
</style>
