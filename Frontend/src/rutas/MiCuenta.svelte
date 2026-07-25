<script>

  import { onMount } from 'svelte';
  import { navigate } from 'svelte-routing';
  import RutaProtegida from '../componentes/RutaProtegida.svelte';
  import { sesion, cerrarSesion } from '../tienda/tienda.js';
  import * as perroService from '../servicios/perroService.js';
  import * as reservaService from '../servicios/reservaService.js';
  import * as usuarioService from '../servicios/usuarioService.js';
  import { apiFetch } from '../servicios/api.js';

  let credencial, idUsuario;
  sesion.subscribe(s => { credencial = s?.credencial; idUsuario = s?.id; });

  let tabActiva = 'reservas';

  let perrosDisponibles = [];
  async function cargarPerrosDisponibles() {
    try {
      const data = await perroService.listarDisponibles(0, 500, credencial);
      perrosDisponibles = data.content;
    } catch { perrosDisponibles = []; }
  }

  let historialReservas = [];
  let cargandoHistorial = true;
  let errorHistorial = '';
  let busquedaHistorial = '';

  $: historialFiltrado = historialReservas.filter(r =>
    (r.nombrePerro ?? '').toLowerCase().includes(busquedaHistorial.toLowerCase())
  );
  $: totalGastado = historialReservas.reduce((s, r) => s + Number(r.precioTotal ?? 0), 0);

  async function cargarHistorial() {
    cargandoHistorial = true; errorHistorial = '';
    try {
      const data = await reservaService.listarPorUsuario(idUsuario, 0, 50, credencial);
      historialReservas = data.content;
    } catch (e) { errorHistorial = e.message || 'No se pudo cargar tu historial.'; }
    finally { cargandoHistorial = false; }
  }

  async function cancelarMiReserva(r) {
    if (!confirm('¿Cancelar esta reserva?')) return;
    try {
      await reservaService.actualizarEstado(r.id, 'CANCELADA', credencial);
      await cargarHistorial();
    } catch (e) { errorHistorial = e.message || 'No se pudo cancelar.'; }
  }

  let perroSeleccionado = '';
  let horasReserva = 1;
  let metodoPagoReserva = 'TARJETA';
  const METODOS = ['TARJETA', 'EFECTIVO', 'TRANSFERENCIA', 'PAYPAL'];
  let erroresReserva = {};
  let exitoReserva = false;
  let errorReserva = '';
  let guardandoReserva = false;

  function validarReserva() {
    const e = {};
    if (!perroSeleccionado) e.perro = 'Selecciona un perrito.';
    if (!horasReserva || isNaN(horasReserva) || horasReserva < 1) e.horas = 'Mínimo 1 hora.';
    else if (horasReserva > 8) e.horas = 'Máximo 8 horas.';
    return e;
  }

  async function hacerReserva() {
    exitoReserva = false; errorReserva = '';
    erroresReserva = validarReserva();
    if (Object.keys(erroresReserva).length > 0) return;
    guardandoReserva = true;
    try {
      await reservaService.crear(
        { idPerro: parseInt(perroSeleccionado), horas: parseInt(horasReserva), metodoPago: metodoPagoReserva },
        credencial
      );
      await cargarPerrosDisponibles();
      await cargarHistorial();
      perroSeleccionado = ''; horasReserva = 1; erroresReserva = {};
      exitoReserva = true;
      setTimeout(() => (exitoReserva = false), 4000);
    } catch (e) { errorReserva = e.message || 'No se pudo registrar la reserva.'; }
    finally { guardandoReserva = false; }
  }

  let nombrePerfil = '';
  let correoPerfil = '';
  let errorPerfil = '';
  let exitoPerfil = false;
  let guardandoPerfil = false;

  $: if ($sesion) { nombrePerfil = $sesion.nombre; correoPerfil = $sesion.correo; }

  async function guardarPerfil() {
    errorPerfil = '';
    if (!nombrePerfil.trim() || !correoPerfil.trim()) { errorPerfil = 'Nombre y correo son obligatorios.'; return; }
    guardandoPerfil = true;
    try {
      const actualizado = await usuarioService.actualizar(
        idUsuario, { nombre: nombrePerfil.trim(), correo: correoPerfil.trim() }, credencial
      );
      sesion.update(s => ({ ...s, nombre: actualizado.nombre, correo: actualizado.correo }));
      exitoPerfil = true;
      setTimeout(() => (exitoPerfil = false), 4000);
    } catch (e) { errorPerfil = e.message || 'No se pudo actualizar el perfil.'; }
    finally { guardandoPerfil = false; }
  }

  let passActual = '';
  let passNueva = '';
  let passConfirmar = '';
  let errorPass = '';
  let exitoPass = false;
  let guardandoPass = false;

  async function cambiarPassword() {
    errorPass = '';
    if (!passActual || !passNueva || !passConfirmar) { errorPass = 'Completa todos los campos.'; return; }
    if (passNueva.length < 6) { errorPass = 'La nueva contraseña debe tener al menos 6 caracteres.'; return; }
    if (passNueva !== passConfirmar) { errorPass = 'Las contraseñas nuevas no coinciden.'; return; }
    guardandoPass = true;
    try {
      await apiFetch(`/usuarios/${idUsuario}/password`, {
        method: 'PATCH',
        body: { passwordActual: passActual, passwordNueva: passNueva },
        credencial
      });
      passActual = ''; passNueva = ''; passConfirmar = '';
      exitoPass = true;
      setTimeout(() => (exitoPass = false), 4000);
    } catch (e) { errorPass = e.message || 'No se pudo cambiar la contraseña.'; }
    finally { guardandoPass = false; }
  }

  function formatearFecha(iso) {
    if (!iso) return '—';
    return new Date(iso).toLocaleString('es-EC', { dateStyle: 'medium', timeStyle: 'short' });
  }

  function manejarSalir() { cerrarSesion(); navigate('/'); }

  onMount(async () => {
    await cargarPerrosDisponibles();
    await cargarHistorial();
  });
</script>

<RutaProtegida rutaActual="/mi-cuenta">
  <main>
    <div class="cuenta-layout">

      <!-- ── Sidebar izquierdo ── -->
      <aside class="cuenta-sidebar">
        <!-- Avatar y nombre -->
        <div class="sidebar-perfil">
          <div class="avatar-grande">{($sesion?.nombre ?? 'U')[0].toUpperCase()}</div>
          <div class="sidebar-datos">
            <strong>{$sesion?.nombre}</strong>
            <span class="sidebar-correo">{$sesion?.correo}</span>
            <span class="rol-chip" class:admin={$sesion?.rol === 'admin'}>{$sesion?.rol}</span>
          </div>
        </div>

        <!-- Stats rápidas -->
        <div class="sidebar-stats">
          <div class="sidebar-stat">
            <strong>{historialReservas.length}</strong>
            <span>Reservas</span>
          </div>
          <div class="sidebar-stat">
            <strong>${totalGastado.toFixed(0)}</strong>
            <span>Gastado</span>
          </div>
          <div class="sidebar-stat">
            <strong>{perrosDisponibles.length}</strong>
            <span>Disponibles</span>
          </div>
        </div>

        <!-- Navegación tabs -->
        <nav class="sidebar-nav">
          <button class="nav-item" class:activo={tabActiva === 'reservas'} on:click={() => (tabActiva = 'reservas')}>
            <span class="nav-icon">📋</span> Mis reservas
          </button>
          <button class="nav-item" class:activo={tabActiva === 'nueva'} on:click={() => (tabActiva = 'nueva')}>
            <span class="nav-icon">➕</span> Nueva reserva
          </button>
          <button class="nav-item" class:activo={tabActiva === 'perfil'} on:click={() => (tabActiva = 'perfil')}>
            <span class="nav-icon">👤</span> Editar perfil
          </button>
          <button class="nav-item" class:activo={tabActiva === 'password'} on:click={() => (tabActiva = 'password')}>
            <span class="nav-icon">🔒</span> Contraseña
          </button>
        </nav>

        <!-- Acciones rápidas -->
        <div class="sidebar-acciones">
          <button class="btn-catalogo-side" on:click={() => navigate('/perros')}>🐾 Ver catálogo</button>
          <button class="btn-salir-side" on:click={manejarSalir}>Cerrar sesión</button>
        </div>
      </aside>

      <!-- ── Contenido principal ── -->
      <div class="cuenta-contenido">

        <!-- TAB: RESERVAS -->
        {#if tabActiva === 'reservas'}
          <div class="tab-header">
            <div>
              <h1>Mis reservas</h1>
              <p>Historial completo de todos tus paseos con nuestros perritos.</p>
            </div>
          </div>

          <div class="busqueda-wrap">
            <span class="busq-icon">🔍</span>
            <input type="text" placeholder="Buscar por nombre de perrito…" bind:value={busquedaHistorial} />
            {#if busquedaHistorial}
              <button class="limpiar-busq" on:click={() => (busquedaHistorial = '')}>✕</button>
            {/if}
            <span class="busq-count">{historialFiltrado.length} de {historialReservas.length}</span>
          </div>

          {#if errorHistorial}
            <div class="alerta error" role="alert"><span>⚠️</span>{errorHistorial}</div>
          {/if}

          {#if cargandoHistorial}
            <div class="loader-wrap"><div class="loader-ring"></div><p>Cargando…</p></div>
          {:else if historialFiltrado.length === 0}
            <div class="estado-vacio">
              <span>🐾</span>
              <p>{busquedaHistorial ? 'Sin resultados para esa búsqueda.' : 'Aún no tienes reservas. ¡Haz tu primera reserva!'}</p>
              {#if !busquedaHistorial}
                <button class="btn-primario-sm" on:click={() => (tabActiva = 'nueva')}>+ Nueva reserva</button>
              {/if}
            </div>
          {:else}
            <div class="tabla-wrap">
              <table>
                <thead>
                  <tr>
                    <th>#</th><th>Perrito</th><th>Horas</th><th>Total</th>
                    <th>Fecha</th><th>Estado</th><th>Pago</th><th></th>
                  </tr>
                </thead>
                <tbody>
                  {#each historialFiltrado as r (r.id)}
                    <tr>
                      <td class="td-id">{r.id}</td>
                      <td class="td-perro"><strong>{r.nombrePerro}</strong></td>
                      <td>{r.horas}h</td>
                      <td class="td-total">${Number(r.precioTotal).toFixed(2)}</td>
                      <td class="td-fecha">{formatearFecha(r.fechaCreacion)}</td>
                      <td>
                        <span class="estado-chip"
                          class:completada={r.estado === 'COMPLETADA'}
                          class:confirmada={r.estado === 'CONFIRMADA'}
                          class:cancelada={r.estado === 'CANCELADA'}>
                          {r.estado}
                        </span>
                      </td>
                      <td>
                        <span class="pago-chip"
                          class:pagado={r.estadoPago === 'PAGADO'}>
                          {r.estadoPago || '—'}
                        </span>
                      </td>
                      <td>
                        {#if r.estado === 'CONFIRMADA'}
                          <button class="btn-cancelar-res" on:click={() => cancelarMiReserva(r)}>Cancelar</button>
                        {/if}
                      </td>
                    </tr>
                  {/each}
                </tbody>
              </table>
            </div>
          {/if}

        <!-- TAB: NUEVA RESERVA -->
        {:else if tabActiva === 'nueva'}
          <div class="tab-header">
            <div>
              <h1>Nueva reserva</h1>
              <p>Selecciona un perrito disponible y la duración del paseo.</p>
            </div>
          </div>

          {#if exitoReserva}
            <div class="alerta exito" role="alert"><span>✅</span> ¡Reserva registrada correctamente!</div>
          {/if}
          {#if errorReserva}
            <div class="alerta error" role="alert"><span>⚠️</span> {errorReserva}</div>
          {/if}

          <div class="form-card">
            <div class="campo-grupo">
              <label for="r-perro">Perrito <span class="req">*</span></label>
              <select id="r-perro" bind:value={perroSeleccionado} class:campo-error={erroresReserva.perro}>
                <option value="">— Selecciona un perrito —</option>
                {#each perrosDisponibles as p}
                  <option value={p.id}>{p.nombre} ({p.raza}) — ${p.precio.toFixed(2)}/h</option>
                {/each}
              </select>
              {#if erroresReserva.perro}<span class="campo-msg-error">{erroresReserva.perro}</span>{/if}
            </div>

            <div class="campos-dobles">
              <div class="campo-grupo">
                <label for="r-horas">Horas (1–8) <span class="req">*</span></label>
                <input id="r-horas" type="number" min="1" max="8" bind:value={horasReserva} class:campo-error={erroresReserva.horas} />
                {#if erroresReserva.horas}<span class="campo-msg-error">{erroresReserva.horas}</span>{/if}
              </div>
              <div class="campo-grupo">
                <label for="r-metodo">Método de pago <span class="req">*</span></label>
                <select id="r-metodo" bind:value={metodoPagoReserva}>
                  {#each METODOS as m}<option value={m}>{m}</option>{/each}
                </select>
              </div>
            </div>

            {#if perroSeleccionado && horasReserva >= 1}
              {@const perro = perrosDisponibles.find(p => p.id === parseInt(perroSeleccionado))}
              {#if perro}
                <div class="total-preview">
                  <span>Total estimado</span>
                  <strong>${(perro.precio * horasReserva).toFixed(2)}</strong>
                </div>
              {/if}
            {/if}

            <button class="btn-form-submit" on:click={hacerReserva} disabled={guardandoReserva}>
              {#if guardandoReserva}<span class="spinner"></span>Registrando…{:else}🐾 Confirmar reserva{/if}
            </button>
          </div>

        <!-- TAB: PERFIL -->
        {:else if tabActiva === 'perfil'}
          <div class="tab-header">
            <div>
              <h1>Editar perfil</h1>
              <p>Actualiza tu nombre y correo electrónico.</p>
            </div>
          </div>

          {#if exitoPerfil}
            <div class="alerta exito" role="alert"><span>✅</span> Perfil actualizado correctamente.</div>
          {/if}

          <div class="form-card">
            {#if errorPerfil}<div class="alerta error" role="alert"><span>⚠️</span>{errorPerfil}</div>{/if}

            <div class="campo-grupo">
              <label for="p-nombre">Nombre completo</label>
              <input id="p-nombre" type="text" bind:value={nombrePerfil} />
            </div>
            <div class="campo-grupo">
              <label for="p-correo">Correo electrónico</label>
              <input id="p-correo" type="email" bind:value={correoPerfil} />
            </div>

            <button class="btn-form-submit" on:click={guardarPerfil} disabled={guardandoPerfil}>
              {#if guardandoPerfil}<span class="spinner"></span>Guardando…{:else}Guardar cambios{/if}
            </button>
          </div>

        <!-- TAB: CONTRASEÑA -->
        {:else if tabActiva === 'password'}
          <div class="tab-header">
            <div>
              <h1>Cambiar contraseña</h1>
              <p>Para proteger tu cuenta, ingresa tu contraseña actual antes de establecer una nueva.</p>
            </div>
          </div>

          {#if exitoPass}
            <div class="alerta exito" role="alert"><span>✅</span> Contraseña actualizada correctamente.</div>
          {/if}

          <div class="form-card">
            {#if errorPass}<div class="alerta error" role="alert"><span>⚠️</span>{errorPass}</div>{/if}

            <div class="campo-grupo">
              <label for="pass-actual">Contraseña actual</label>
              <input id="pass-actual" type="password" placeholder="Tu contraseña actual" bind:value={passActual} autocomplete="current-password" />
            </div>
            <div class="campo-grupo">
              <label for="pass-nueva">Nueva contraseña</label>
              <input id="pass-nueva" type="password" placeholder="Mínimo 6 caracteres" bind:value={passNueva} autocomplete="new-password" />
            </div>
            <div class="campo-grupo">
              <label for="pass-confirmar">Confirmar nueva contraseña</label>
              <input id="pass-confirmar" type="password" placeholder="Repite la nueva contraseña" bind:value={passConfirmar} autocomplete="new-password" />
              {#if passConfirmar && passNueva !== passConfirmar}
                <span class="campo-msg-error">Las contraseñas no coinciden</span>
              {:else if passConfirmar && passNueva === passConfirmar}
                <span class="campo-msg-ok">✓ Las contraseñas coinciden</span>
              {/if}
            </div>

            <div class="pass-tips">
              <p>💡 Recomendaciones para una contraseña segura:</p>
              <ul>
                <li>Mínimo 6 caracteres</li>
                <li>Mezcla letras, números y símbolos</li>
                <li>No uses datos personales obvios</li>
              </ul>
            </div>

            <button class="btn-form-submit" on:click={cambiarPassword} disabled={guardandoPass}>
              {#if guardandoPass}<span class="spinner"></span>Actualizando…{:else}🔒 Actualizar contraseña{/if}
            </button>
          </div>
        {/if}

      </div>
    </div>
  </main>
</RutaProtegida>

<style>
  main { min-height: calc(100vh - 68px); background: var(--fondo); }

  /* Layout */
  .cuenta-layout {
    max-width: 1200px;
    margin: 0 auto;
    padding: 2rem 1.5rem 4rem;
    display: grid;
    grid-template-columns: 280px 1fr;
    gap: 2rem;
    align-items: start;
  }

  /* ── Sidebar ── */
  .cuenta-sidebar {
    position: sticky;
    top: 88px;
    background: var(--tarjeta);
    border: 1px solid var(--borde);
    border-radius: var(--radio-2xl);
    padding: 1.5rem;
    box-shadow: var(--sombra-xs);
    display: flex;
    flex-direction: column;
    gap: 1.25rem;
  }
  .sidebar-perfil {
    display: flex;
    gap: 0.9rem;
    align-items: center;
    padding-bottom: 1.25rem;
    border-bottom: 1px solid var(--borde);
  }
  .avatar-grande {
    width: 52px; height: 52px;
    border-radius: 50%;
    background: linear-gradient(135deg, var(--color-primario), var(--color-primario-hover));
    color: #fff;
    font-size: 1.4rem; font-weight: 900;
    display: flex; align-items: center; justify-content: center;
    flex-shrink: 0;
    box-shadow: 0 4px 12px rgba(var(--color-primario-rgb), 0.4);
  }
  .sidebar-datos { display: flex; flex-direction: column; gap: 0.2rem; min-width: 0; }
  .sidebar-datos strong {
    font-size: 0.95rem; font-weight: 700; color: var(--texto);
    white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
  }
  .sidebar-correo { font-size: 0.77rem; color: var(--texto-suave); overflow: hidden; text-overflow: ellipsis; }
  .rol-chip {
    font-size: 0.65rem; font-weight: 700; text-transform: uppercase;
    letter-spacing: 0.05em; padding: 2px 7px; border-radius: var(--radio-full);
    background: var(--verde-suave); color: var(--verde-texto);
    width: fit-content;
  }
  .rol-chip.admin { background: var(--naranja-suave); color: var(--color-primario); }

  /* Stats sidebar */
  .sidebar-stats {
    display: grid; grid-template-columns: repeat(3, 1fr);
    gap: 0.5rem; text-align: center;
  }
  .sidebar-stat {
    background: var(--fondo-suave);
    border: 1px solid var(--borde);
    border-radius: var(--radio-lg);
    padding: 0.6rem 0.3rem;
  }
  .sidebar-stat strong { display: block; font-size: 1.1rem; font-weight: 800; color: var(--color-primario); }
  .sidebar-stat span { font-size: 0.65rem; color: var(--texto-suave); font-weight: 500; }

  /* Nav sidebar */
  .sidebar-nav { display: flex; flex-direction: column; gap: 0.25rem; }
  .nav-item {
    display: flex; align-items: center; gap: 0.65rem;
    padding: 0.65rem 0.9rem;
    border-radius: var(--radio-md);
    border: none; background: none;
    font-size: 0.9rem; font-weight: 500; color: var(--texto-suave);
    cursor: pointer; text-align: left;
    transition: background 0.15s, color 0.15s;
  }
  .nav-item:hover { background: var(--fondo-suave); color: var(--texto); }
  .nav-item.activo {
    background: var(--naranja-suave);
    color: var(--color-primario);
    font-weight: 700;
  }
  .nav-icon { font-size: 1rem; }

  /* Acciones sidebar */
  .sidebar-acciones { display: flex; flex-direction: column; gap: 0.4rem; padding-top: 0.75rem; border-top: 1px solid var(--borde); }
  .btn-catalogo-side {
    padding: 0.6rem; border-radius: var(--radio-md);
    background: var(--color-primario); color: #fff;
    border: none; font-size: 0.85rem; font-weight: 700;
    cursor: pointer; transition: background 0.15s; text-align: center;
  }
  .btn-catalogo-side:hover { background: var(--color-primario-hover); }
  .btn-salir-side {
    padding: 0.6rem; border-radius: var(--radio-md);
    background: none; border: 1.5px solid var(--borde);
    color: var(--texto-suave); font-size: 0.85rem; font-weight: 500;
    cursor: pointer; transition: all 0.15s; text-align: center;
  }
  .btn-salir-side:hover { background: var(--rojo-suave); color: var(--rojo-texto); border-color: var(--rojo-texto); }

  /* ── Contenido ── */
  .cuenta-contenido { display: flex; flex-direction: column; gap: 1.5rem; }
  .tab-header { padding-bottom: 1.25rem; border-bottom: 1px solid var(--borde); }
  .tab-header h1 {
    font-size: 1.7rem; font-weight: 800; color: var(--texto);
    letter-spacing: -0.02em; margin-bottom: 0.25rem;
  }
  .tab-header p { font-size: 0.9rem; color: var(--texto-suave); }

  /* Alertas */
  .alerta {
    display: flex; align-items: center; gap: 0.65rem;
    padding: 0.85rem 1rem; border-radius: var(--radio-md);
    font-size: 0.88rem; font-weight: 500;
  }
  .alerta.exito { background: var(--verde-suave); color: var(--verde-texto); border-left: 4px solid var(--verde-texto); }
  .alerta.error { background: var(--rojo-suave); color: var(--rojo-texto); border-left: 4px solid var(--rojo-texto); }

  /* Búsqueda */
  .busqueda-wrap {
    display: flex; align-items: center; gap: 0.6rem;
    background: var(--tarjeta); border: 1.5px solid var(--borde);
    border-radius: var(--radio-md); padding: 0.5rem 0.9rem;
    transition: border-color 0.2s;
  }
  .busqueda-wrap:focus-within { border-color: var(--color-primario); }
  .busq-icon { font-size: 0.9rem; color: var(--texto-muy-suave); flex-shrink: 0; }
  .busqueda-wrap input {
    flex: 1; border: none; background: none;
    font-size: 0.9rem; color: var(--texto); outline: none;
  }
  .busqueda-wrap input::placeholder { color: var(--texto-muy-suave); }
  .limpiar-busq {
    background: none; border: none; color: var(--texto-muy-suave);
    cursor: pointer; font-size: 0.85rem; padding: 2px; transition: color 0.15s;
  }
  .limpiar-busq:hover { color: var(--rojo-texto); }
  .busq-count { font-size: 0.78rem; color: var(--texto-muy-suave); white-space: nowrap; }

  /* Loader */
  .loader-wrap {
    display: flex; flex-direction: column; align-items: center; gap: 0.75rem;
    padding: 3rem; color: var(--texto-suave); font-size: 0.9rem;
  }
  .loader-ring {
    width: 40px; height: 40px;
    border: 3px solid var(--borde);
    border-top-color: var(--color-primario);
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
  }
  @keyframes spin { to { transform: rotate(360deg); } }

  /* Estado vacío */
  .estado-vacio {
    display: flex; flex-direction: column; align-items: center; gap: 0.75rem;
    padding: 3.5rem 1rem; text-align: center;
    color: var(--texto-suave); font-size: 0.95rem;
  }
  .estado-vacio span { font-size: 3rem; }
  .btn-primario-sm {
    padding: 0.6rem 1.2rem; border-radius: var(--radio-md);
    background: var(--color-primario); color: #fff;
    border: none; font-size: 0.88rem; font-weight: 700;
    cursor: pointer; transition: background 0.15s; margin-top: 0.25rem;
  }
  .btn-primario-sm:hover { background: var(--color-primario-hover); }

  /* Tabla */
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
    font-size: 0.75rem; font-weight: 700; color: var(--texto-suave);
    text-transform: uppercase; letter-spacing: 0.06em;
    border-bottom: 1px solid var(--borde); white-space: nowrap;
  }
  td {
    padding: 0.75rem 1rem; font-size: 0.87rem; color: var(--texto);
    border-bottom: 1px solid var(--borde); vertical-align: middle;
  }
  tr:last-child td { border-bottom: none; }
  tr:hover td { background: var(--fondo-suave); }
  .td-id { color: var(--texto-muy-suave); font-size: 0.78rem; }
  .td-total { font-weight: 700; color: var(--color-primario); }
  .td-fecha { font-size: 0.8rem; color: var(--texto-suave); white-space: nowrap; }

  /* Estado chips */
  .estado-chip {
    font-size: 0.72rem; font-weight: 700; padding: 3px 10px;
    border-radius: var(--radio-full); text-transform: uppercase; letter-spacing: 0.04em;
    background: var(--rojo-suave); color: var(--rojo-texto);
  }
  .estado-chip.completada { background: var(--verde-suave); color: var(--verde-texto); }
  .estado-chip.confirmada { background: var(--amarillo-suave); color: var(--amarillo-texto); }
  .estado-chip.cancelada { background: var(--rojo-suave); color: var(--rojo-texto); }
  .pago-chip {
    font-size: 0.72rem; font-weight: 600; padding: 2px 8px;
    border-radius: var(--radio-full);
    background: var(--fondo-suave2); color: var(--texto-suave);
  }
  .pago-chip.pagado { background: var(--verde-suave); color: var(--verde-texto); }

  .btn-cancelar-res {
    padding: 4px 10px; border-radius: var(--radio-sm);
    border: 1px solid var(--borde); background: none;
    color: var(--texto-suave); font-size: 0.78rem; font-weight: 600;
    cursor: pointer; transition: all 0.15s; white-space: nowrap;
  }
  .btn-cancelar-res:hover { background: var(--rojo-suave); color: var(--rojo-texto); border-color: var(--rojo-texto); }

  /* Formulario card */
  .form-card {
    background: var(--tarjeta);
    border: 1px solid var(--borde);
    border-radius: var(--radio-2xl);
    padding: 1.75rem;
    box-shadow: var(--sombra-xs);
    display: flex; flex-direction: column; gap: 1.1rem;
    max-width: 560px;
  }
  .campo-grupo { display: flex; flex-direction: column; gap: 0.4rem; }
  .campo-grupo label {
    font-size: 0.85rem; font-weight: 600; color: var(--texto);
    display: flex; align-items: center; gap: 0.3rem;
  }
  .req { color: var(--color-primario); }
  .campo-grupo input,
  .campo-grupo select {
    padding: 0.7rem 1rem;
    border: 1.5px solid var(--borde);
    border-radius: var(--radio-md);
    font-size: 0.95rem;
    background: var(--fondo);
    color: var(--texto);
    transition: border-color 0.2s, box-shadow 0.2s;
    outline: none;
    box-sizing: border-box;
  }
  .campo-grupo input:focus,
  .campo-grupo select:focus {
    border-color: var(--color-primario);
    box-shadow: 0 0 0 3px rgba(var(--color-primario-rgb), 0.12);
  }
  .campo-error { border-color: #e53e3e !important; }
  .campo-msg-error { font-size: 0.78rem; color: var(--rojo-texto); font-weight: 500; }
  .campo-msg-ok { font-size: 0.78rem; color: var(--verde-texto); font-weight: 600; }
  .campos-dobles { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }

  .total-preview {
    display: flex; justify-content: space-between; align-items: center;
    background: var(--naranja-suave);
    border: 1px solid var(--naranja-mid);
    border-radius: var(--radio-lg);
    padding: 0.85rem 1rem;
    font-size: 0.9rem; color: var(--texto-suave);
  }
  .total-preview strong { font-size: 1.3rem; font-weight: 900; color: var(--color-primario); }

  .btn-form-submit {
    padding: 0.85rem; border-radius: var(--radio-lg);
    background: var(--color-primario); color: #fff;
    border: none; font-size: 1rem; font-weight: 700;
    cursor: pointer;
    transition: background 0.2s, transform 0.15s, box-shadow 0.2s;
    box-shadow: 0 4px 14px rgba(var(--color-primario-rgb), 0.35);
    display: flex; align-items: center; justify-content: center; gap: 0.5rem;
    margin-top: 0.25rem;
  }
  .btn-form-submit:hover:not(:disabled) {
    background: var(--color-primario-hover);
    transform: translateY(-1px);
    box-shadow: 0 6px 20px rgba(var(--color-primario-rgb), 0.45);
  }
  .btn-form-submit:disabled { background: var(--borde-fuerte); cursor: not-allowed; box-shadow: none; color: var(--texto-suave); }

  .pass-tips {
    background: var(--azul-suave); border-left: 3px solid var(--azul-texto);
    border-radius: var(--radio-md); padding: 0.85rem 1rem;
  }
  .pass-tips p { font-size: 0.82rem; font-weight: 600; color: var(--azul-texto); margin-bottom: 0.4rem; }
  .pass-tips ul { padding-left: 1.2rem; display: flex; flex-direction: column; gap: 0.2rem; }
  .pass-tips li { font-size: 0.8rem; color: var(--azul-texto); }

  .spinner {
    width: 16px; height: 16px;
    border: 2.5px solid rgba(255,255,255,0.4);
    border-top-color: #fff; border-radius: 50%;
    animation: spin 0.7s linear infinite;
  }

  /* Responsive */
  @media (max-width: 900px) {
    .cuenta-layout { grid-template-columns: 1fr; }
    .cuenta-sidebar { position: static; }
    .sidebar-perfil { flex-wrap: wrap; }
  }
  @media (max-width: 600px) {
    .campos-dobles { grid-template-columns: 1fr; }
    .form-card { padding: 1.25rem; }
    .tabla-wrap { font-size: 0.82rem; }
  }
</style>
