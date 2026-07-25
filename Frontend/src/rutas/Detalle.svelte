<script>
  import { onMount } from 'svelte';
  import { navigate } from 'svelte-routing';
  import RutaProtegida from '../componentes/RutaProtegida.svelte';
  import { agregarAlCarrito } from '../tienda/tienda.js';
  import * as perroService from '../servicios/perroService.js';

  export let id;

  let perro = null;
  let cargando = true;
  let horas = 1;
  let agregado = false;

  onMount(async () => {
    cargando = true;
    try {
      perro = await perroService.obtenerPorId(id);
    } catch {
      perro = null;
    } finally {
      cargando = false;
    }
  });

  function agregar() {
    agregarAlCarrito(perro, horas);
    agregado = true;
    setTimeout(() => (agregado = false), 2000);
  }

  function irAlCarrito() {
    agregarAlCarrito(perro, horas);
    navigate('/carrito');
  }
</script>

<RutaProtegida rutaActual={`/perros/${id}`}>
  <main>
    {#if cargando}
      <div class="estado-pantalla">
        <div class="loader-ring"></div>
        <p>Cargando información…</p>
      </div>

    {:else if perro}
      <!-- Breadcrumb -->
      <div class="breadcrumb">
        <div class="breadcrumb-inner">
          <button class="bread-link" on:click={() => navigate('/')}>Inicio</button>
          <span class="bread-sep">›</span>
          <button class="bread-link" on:click={() => navigate('/perros')}>Catálogo</button>
          <span class="bread-sep">›</span>
          <span class="bread-actual">{perro.nombre}</span>
        </div>
      </div>

      <div class="detalle-wrap">
        <!-- Columna imagen -->
        <div class="col-imagen">
          <div class="imagen-contenedor">
            <img src={perro.imagen} alt={perro.nombre} />

            <div class="imagen-badges">
              <span class="badge-tam">{perro.tamaño}</span>
              <span class="badge-disp" class:no={!perro.disponible}>
                {perro.disponible ? '✓ Disponible' : '✗ No disponible'}
              </span>
            </div>
          </div>

          <!-- Etiquetas -->
          {#if perro.etiquetas?.length > 0}
            <div class="etiquetas-seccion">
              <p class="etiquetas-label">Características</p>
              <div class="etiquetas">
                {#each perro.etiquetas as etiqueta}
                  <span class="etiqueta">{etiqueta}</span>
                {/each}
              </div>
            </div>
          {/if}

          <!-- Info adicional -->
          <div class="info-grid">
            <div class="info-item">
              <span class="info-icon">📏</span>
              <div>
                <span class="info-label">Tamaño</span>
                <strong>{perro.tamaño}</strong>
              </div>
            </div>
            <div class="info-item">
              <span class="info-icon">⭐</span>
              <div>
                <span class="info-label">Calificación</span>
                <strong>{perro.calificacion > 0 ? `${perro.calificacion} / 5` : 'Sin reseñas'}</strong>
              </div>
            </div>
            <div class="info-item">
              <span class="info-icon">💬</span>
              <div>
                <span class="info-label">Reseñas</span>
                <strong>{perro.reseñas}</strong>
              </div>
            </div>
            <div class="info-item">
              <span class="info-icon">🏷️</span>
              <div>
                <span class="info-label">Precio</span>
                <strong class="precio-info">${perro.precio.toFixed(2)}/h</strong>
              </div>
            </div>
          </div>
        </div>

        <!-- Columna info -->
        <div class="col-info">
          <div class="col-info-sticky">
            <!-- Header -->
            <div class="detalle-header">
              <div>
                <h1 class="detalle-nombre">{perro.nombre}</h1>
                <p class="detalle-raza">{perro.raza}</p>
              </div>
              <div class="precio-badge">
                <span class="precio-valor">${perro.precio.toFixed(2)}</span>
                <span class="precio-unit">/ hora</span>
              </div>
            </div>

            <!-- Rating -->
            {#if perro.calificacion > 0}
              <div class="rating-row">
                <div class="estrellas">
                  {#each Array(5) as _, i}
                    <span class:llena={i < Math.round(perro.calificacion)}>★</span>
                  {/each}
                </div>
                <span class="rating-num">{perro.calificacion}</span>
                <span class="rating-count">({perro.reseñas} reseñas)</span>
              </div>
            {:else}
              <div class="nuevo-badge">🆕 Recién registrado — ¡sé el primero en reservarlo!</div>
            {/if}

            <!-- Estado disponibilidad -->
            <div class="estado-disp" class:disponible={perro.disponible}>
              <span class="estado-dot"></span>
              <span>{perro.disponible ? 'Disponible para reservar ahora' : 'No disponible actualmente'}</span>
            </div>

            <!-- Descripción -->
            <div class="descripcion-seccion">
              <h2>Sobre {perro.nombre}</h2>
              <p>{perro.descripcion}</p>
            </div>

            {#if perro.disponible}
              <!-- Control de horas -->
              <div class="horas-seccion">
                <div class="horas-header">
                  <h3>Duración del paseo</h3>
                  <span class="horas-hint">Mínimo 1 h · Máximo 8 h</span>
                </div>
                <div class="horas-control">
                  <button
                    class="horas-btn"
                    on:click={() => (horas = Math.max(1, horas - 1))}
                    disabled={horas <= 1}
                  >−</button>
                  <div class="horas-display">
                    <strong>{horas}</strong>
                    <span>{horas === 1 ? 'hora' : 'horas'}</span>
                  </div>
                  <button
                    class="horas-btn"
                    on:click={() => (horas = Math.min(8, horas + 1))}
                    disabled={horas >= 8}
                  >+</button>
                </div>
              </div>

              <!-- Total -->
              <div class="total-card">
                <div class="total-fila">
                  <span>${perro.precio.toFixed(2)} × {horas} hora{horas > 1 ? 's' : ''}</span>
                  <span class="total-valor">${(perro.precio * horas).toFixed(2)}</span>
                </div>
                <div class="total-divider"></div>
                <div class="total-fila total-grande">
                  <span>Total estimado</span>
                  <strong>${(perro.precio * horas).toFixed(2)}</strong>
                </div>
              </div>

              <!-- Acciones -->
              <div class="acciones">
                <button class="btn-reservar" on:click={irAlCarrito}>
                  🛒 Reservar ahora
                </button>
                <button
                  class="btn-agregar"
                  class:agregado
                  on:click={agregar}
                >
                  {#if agregado}
                    ✓ Agregado al carrito
                  {:else}
                    + Agregar al carrito
                  {/if}
                </button>
              </div>

              <p class="garantia">🔒 Reserva segura · Sin cargos ocultos · Cancela cuando quieras</p>
            {:else}
              <div class="no-disponible-aviso">
                <span class="aviso-icon">😔</span>
                <div>
                  <strong>Este perrito no está disponible ahora</strong>
                  <p>Prueba más tarde o explora otros perritos en el catálogo.</p>
                </div>
              </div>
              <button class="btn-catalogo" on:click={() => navigate('/perros')}>
                Ver otros perritos disponibles →
              </button>
            {/if}
          </div>
        </div>
      </div>

    {:else}
      <div class="estado-pantalla">
        <div class="estado-card">
          <span class="estado-icon">🐾</span>
          <h3>Perrito no encontrado</h3>
          <p>Es posible que este perrito ya no esté disponible.</p>
          <button class="btn-catalogo" on:click={() => navigate('/perros')}>
            Ver catálogo
          </button>
        </div>
      </div>
    {/if}
  </main>
</RutaProtegida>

<style>
  main { min-height: calc(100vh - 68px); background: var(--fondo); }

  /* Estados */
  .estado-pantalla {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 1rem;
    padding: 6rem 1.5rem;
    color: var(--texto-suave);
    font-size: 0.95rem;
  }
  .loader-ring {
    width: 48px; height: 48px;
    border: 3px solid var(--borde);
    border-top-color: var(--color-primario);
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
  }
  @keyframes spin { to { transform: rotate(360deg); } }

  .estado-card {
    text-align: center;
    display: flex; flex-direction: column; align-items: center; gap: 0.75rem;
    max-width: 340px;
  }
  .estado-icon { font-size: 3.5rem; }
  .estado-card h3 { font-size: 1.2rem; font-weight: 700; color: var(--texto); }
  .estado-card p { font-size: 0.9rem; color: var(--texto-suave); line-height: 1.5; }

  /* Breadcrumb */
  .breadcrumb {
    border-bottom: 1px solid var(--borde);
    background: var(--tarjeta);
  }
  .breadcrumb-inner {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0.75rem 1.5rem;
    display: flex;
    align-items: center;
    gap: 0.4rem;
    font-size: 0.82rem;
    flex-wrap: wrap;
  }
  .bread-link {
    background: none; border: none;
    color: var(--texto-suave); cursor: pointer;
    font-size: 0.82rem; font-weight: 500;
    padding: 0; transition: color 0.15s;
  }
  .bread-link:hover { color: var(--color-primario); }
  .bread-sep { color: var(--borde-fuerte); }
  .bread-actual { font-weight: 600; color: var(--texto); }

  /* Layout detalle */
  .detalle-wrap {
    max-width: 1200px;
    margin: 0 auto;
    padding: 2.5rem 1.5rem 4rem;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 4rem;
    align-items: start;
  }

  /* Columna imagen */
  .imagen-contenedor {
    position: relative;
    border-radius: var(--radio-2xl);
    overflow: hidden;
    box-shadow: var(--sombra-lg);
  }
  .imagen-contenedor img {
    width: 100%;
    height: 400px;
    object-fit: cover;
    display: block;
  }
  .imagen-badges {
    position: absolute;
    top: 14px;
    left: 14px;
    display: flex;
    flex-direction: column;
    gap: 0.4rem;
  }
  .badge-tam {
    background: rgba(255,255,255,0.92);
    backdrop-filter: blur(8px);
    color: var(--color-primario);
    font-size: 0.72rem; font-weight: 800;
    padding: 4px 10px; border-radius: var(--radio-full);
    text-transform: uppercase; letter-spacing: 0.05em;
    width: fit-content;
  }
  .badge-disp {
    background: rgba(22, 101, 52, 0.88);
    backdrop-filter: blur(8px);
    color: #86efac;
    font-size: 0.72rem; font-weight: 700;
    padding: 4px 10px; border-radius: var(--radio-full);
    width: fit-content;
  }
  .badge-disp.no {
    background: rgba(153, 27, 27, 0.88);
    color: #fca5a5;
  }

  .etiquetas-seccion { margin-top: 1.25rem; }
  .etiquetas-label {
    font-size: 0.78rem; font-weight: 700; text-transform: uppercase;
    letter-spacing: 0.07em; color: var(--texto-muy-suave); margin-bottom: 0.6rem;
  }
  .etiquetas { display: flex; flex-wrap: wrap; gap: 0.4rem; }
  .etiqueta {
    background: var(--naranja-suave);
    color: var(--color-primario);
    border: 1px solid var(--naranja-mid);
    font-size: 0.78rem; font-weight: 600;
    padding: 4px 12px; border-radius: var(--radio-full);
  }

  .info-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 0.75rem;
    margin-top: 1.25rem;
  }
  .info-item {
    display: flex; align-items: center; gap: 0.65rem;
    background: var(--tarjeta);
    border: 1px solid var(--borde);
    border-radius: var(--radio-lg);
    padding: 0.75rem;
  }
  .info-icon { font-size: 1.2rem; flex-shrink: 0; }
  .info-item div { display: flex; flex-direction: column; line-height: 1.3; }
  .info-label { font-size: 0.72rem; color: var(--texto-suave); font-weight: 500; }
  .info-item strong { font-size: 0.88rem; color: var(--texto); font-weight: 700; }
  .precio-info { color: var(--color-primario) !important; }

  /* Columna info sticky */
  .col-info-sticky {
    position: sticky;
    top: 88px;
    display: flex;
    flex-direction: column;
    gap: 1.25rem;
  }

  .detalle-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 1rem;
  }
  .detalle-nombre {
    font-size: 2.2rem;
    font-weight: 900;
    color: var(--texto);
    letter-spacing: -0.03em;
    line-height: 1.1;
    margin-bottom: 0.3rem;
  }
  .detalle-raza { font-size: 1rem; color: var(--texto-suave); font-weight: 500; }
  .precio-badge {
    text-align: right; flex-shrink: 0;
    background: var(--naranja-suave);
    border: 1px solid var(--naranja-mid);
    border-radius: var(--radio-lg);
    padding: 0.6rem 1rem;
  }
  .precio-valor {
    display: block;
    font-size: 1.9rem; font-weight: 900;
    color: var(--color-primario);
    letter-spacing: -0.03em; line-height: 1;
  }
  .precio-unit { font-size: 0.75rem; color: var(--texto-suave); font-weight: 500; }

  /* Rating */
  .rating-row {
    display: flex; align-items: center; gap: 0.5rem;
    font-size: 0.88rem;
  }
  .estrellas span { font-size: 1.1rem; color: var(--borde-fuerte); }
  .estrellas span.llena { color: #f59e0b; }
  .rating-num { font-weight: 700; color: var(--texto); }
  .rating-count { color: var(--texto-suave); }

  .nuevo-badge {
    font-size: 0.85rem; color: var(--texto-suave);
    background: var(--fondo-suave);
    border: 1px solid var(--borde);
    border-radius: var(--radio-md);
    padding: 0.5rem 0.85rem;
    display: inline-block;
  }

  /* Estado disponibilidad */
  .estado-disp {
    display: flex; align-items: center; gap: 0.5rem;
    font-size: 0.88rem; font-weight: 600;
    color: var(--rojo-texto);
  }
  .estado-disp.disponible { color: var(--verde-texto); }
  .estado-dot {
    width: 8px; height: 8px;
    border-radius: 50%; background: currentColor;
    animation: pulse 2s ease-in-out infinite;
  }
  @keyframes pulse {
    0%, 100% { opacity: 1; transform: scale(1); }
    50% { opacity: 0.5; transform: scale(1.4); }
  }

  /* Descripción */
  .descripcion-seccion h2 {
    font-size: 1rem; font-weight: 700; color: var(--texto);
    margin-bottom: 0.5rem;
  }
  .descripcion-seccion p {
    font-size: 0.93rem; color: var(--texto-suave); line-height: 1.75;
  }

  /* Horas */
  .horas-seccion {
    background: var(--tarjeta);
    border: 1px solid var(--borde);
    border-radius: var(--radio-xl);
    padding: 1.25rem;
  }
  .horas-header {
    display: flex; justify-content: space-between; align-items: center;
    margin-bottom: 1rem;
  }
  .horas-header h3 { font-size: 0.9rem; font-weight: 700; color: var(--texto); }
  .horas-hint { font-size: 0.75rem; color: var(--texto-suave); }
  .horas-control {
    display: flex; align-items: center; gap: 0; justify-content: center;
    border: 1.5px solid var(--borde); border-radius: var(--radio-lg);
    overflow: hidden; width: fit-content; margin: 0 auto;
  }
  .horas-btn {
    width: 48px; height: 48px;
    background: var(--fondo-suave); border: none;
    font-size: 1.4rem; font-weight: 700;
    color: var(--texto); cursor: pointer;
    transition: background 0.15s;
    display: flex; align-items: center; justify-content: center;
  }
  .horas-btn:hover:not(:disabled) { background: var(--naranja-suave); color: var(--color-primario); }
  .horas-btn:disabled { opacity: 0.35; cursor: not-allowed; }
  .horas-display {
    padding: 0 2rem;
    display: flex; flex-direction: column;
    align-items: center; line-height: 1.1;
    min-width: 90px;
  }
  .horas-display strong { font-size: 1.4rem; font-weight: 800; color: var(--texto); }
  .horas-display span { font-size: 0.75rem; color: var(--texto-suave); }

  /* Total */
  .total-card {
    background: var(--fondo-suave);
    border: 1px solid var(--borde);
    border-radius: var(--radio-xl);
    padding: 1.1rem 1.25rem;
    display: flex; flex-direction: column; gap: 0.65rem;
  }
  .total-fila {
    display: flex; justify-content: space-between; align-items: center;
    font-size: 0.9rem; color: var(--texto-suave);
  }
  .total-valor { font-weight: 600; color: var(--texto); }
  .total-divider { height: 1px; background: var(--borde); }
  .total-grande {
    font-size: 1rem; font-weight: 700; color: var(--texto);
  }
  .total-grande strong { font-size: 1.3rem; color: var(--color-primario); }

  /* Acciones */
  .acciones { display: flex; flex-direction: column; gap: 0.65rem; }
  .btn-reservar {
    width: 100%; padding: 0.9rem;
    border-radius: var(--radio-lg);
    background: var(--color-primario); color: #fff;
    border: none; font-size: 1rem; font-weight: 700;
    cursor: pointer;
    transition: background 0.2s, transform 0.15s, box-shadow 0.2s;
    box-shadow: 0 4px 16px rgba(var(--color-primario-rgb), 0.4);
  }
  .btn-reservar:hover {
    background: var(--color-primario-hover);
    transform: translateY(-2px);
    box-shadow: 0 6px 24px rgba(var(--color-primario-rgb), 0.5);
  }
  .btn-agregar {
    width: 100%; padding: 0.85rem;
    border-radius: var(--radio-lg);
    background: transparent;
    color: var(--color-primario);
    border: 2px solid var(--color-primario);
    font-size: 0.95rem; font-weight: 700;
    cursor: pointer; transition: all 0.2s;
  }
  .btn-agregar:hover:not(.agregado) {
    background: var(--color-primario); color: #fff;
  }
  .btn-agregar.agregado {
    background: var(--verde-suave);
    color: var(--verde-texto);
    border-color: var(--verde-texto);
  }

  .garantia {
    font-size: 0.78rem; color: var(--texto-muy-suave);
    text-align: center; line-height: 1.5;
  }

  /* No disponible */
  .no-disponible-aviso {
    display: flex; gap: 1rem; align-items: flex-start;
    background: var(--fondo-suave);
    border: 1px solid var(--borde);
    border-radius: var(--radio-xl);
    padding: 1.25rem;
  }
  .aviso-icon { font-size: 1.5rem; flex-shrink: 0; }
  .no-disponible-aviso strong { display: block; font-size: 0.9rem; font-weight: 700; color: var(--texto); margin-bottom: 0.2rem; }
  .no-disponible-aviso p { font-size: 0.85rem; color: var(--texto-suave); line-height: 1.5; }

  .btn-catalogo {
    width: 100%; padding: 0.85rem;
    border-radius: var(--radio-lg);
    background: var(--color-primario); color: #fff;
    border: none; font-size: 0.95rem; font-weight: 700;
    cursor: pointer; transition: background 0.2s;
    margin-top: 0.25rem;
  }
  .btn-catalogo:hover { background: var(--color-primario-hover); }

  /* Responsive */
  @media (max-width: 900px) {
    .detalle-wrap { grid-template-columns: 1fr; gap: 2rem; }
    .col-info-sticky { position: static; }
    .imagen-contenedor img { height: 300px; }
    .detalle-nombre { font-size: 1.8rem; }
  }
  @media (max-width: 480px) {
    .info-grid { grid-template-columns: 1fr 1fr; }
    .detalle-nombre { font-size: 1.5rem; }
  }
</style>
