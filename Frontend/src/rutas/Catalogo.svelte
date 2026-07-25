<script>

  import { onMount } from 'svelte';
  import TarjetaPerro  from '../componentes/TarjetaPerro.svelte';
  import RutaProtegida from '../componentes/RutaProtegida.svelte';
  import * as perroService from '../servicios/perroService.js';

  let busqueda         = '';           
  let filtroTamaño     = 'todos';      
  let filtroDisponible = false;       

  const TAMAÑOS = ['todos', 'Pequeño', 'Mediano', 'Grande'];

  let perros = [];         
  let cargando = true;
  let error = '';

  const POR_PAGINA = 9;   
  let paginaActual = 0;
  let totalPaginas = 1;
  let totalElementos = 0;

  async function cargar() {
    cargando = true;
    error = '';
    try {
      const data = busqueda.trim()
        ? await perroService.buscar(busqueda.trim(), paginaActual, POR_PAGINA)  
        : await perroService.listar(paginaActual, POR_PAGINA);                  
      perros = data.content;
      totalPaginas = data.totalPages || 1;
      totalElementos = data.totalElements || 0;
    } catch (e) {
      error = e.message || 'No se pudo cargar el catálogo.';
    } finally {
      cargando = false;
    }
  }

  onMount(cargar);

  $: perrosFiltrados = perros.filter(p => {
    const coincideTamaño     = filtroTamaño === 'todos' || p.tamaño === filtroTamaño;
    const coincideDisponible = !filtroDisponible || p.disponible;
    return coincideTamaño && coincideDisponible;
  });

  function buscarAhora() {
    paginaActual = 0;
    cargar();
  }

  function irPagina(n) {
    if (n < 0 || n >= totalPaginas) return;
    paginaActual = n;
    cargar();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  function limpiarFiltros() {
    busqueda = '';
    filtroTamaño = 'todos';
    filtroDisponible = false;
    paginaActual = 0;
    cargar();
  }

  $: hayFiltrosActivos = busqueda || filtroTamaño !== 'todos' || filtroDisponible;
</script>

<RutaProtegida rutaActual="/perros">
  <main>
    <!-- Encabezado hero pequeño -->
    <div class="catalogo-hero">
      <div class="catalogo-hero-inner">
        <span class="hero-etiqueta">🐾 Catálogo completo</span>
        <h1>Nuestros perritos</h1>
        <p>Elige al compañero perfecto para tu próximo paseo</p>
      </div>
    </div>

    <!-- Barra de filtros -->
    <div class="filtros-wrap">
      <div class="filtros-inner">
        <!-- Búsqueda -->
        <div class="busqueda-wrap">
          <span class="busqueda-icon">🔍</span>
          <input
            class="campo-busqueda"
            type="text"
            placeholder="Buscar por nombre o raza…"
            bind:value={busqueda}
            on:keydown={e => e.key === 'Enter' && buscarAhora()}
          />
          {#if busqueda}
            <button class="limpiar-input" on:click={() => { busqueda = ''; buscarAhora(); }}>✕</button>
          {/if}
        </div>

        <!-- Filtros de tamaño -->
        <div class="filtros-grupo">
          <span class="filtros-label">Tamaño:</span>
          <div class="chips-grupo">
            {#each TAMAÑOS as t}
              <button
                class="chip"
                class:activo={filtroTamaño === t}
                on:click={() => (filtroTamaño = t)}
              >
                {t === 'todos' ? 'Todos' : t}
              </button>
            {/each}
          </div>
        </div>

        <!-- Toggle disponibles -->
        <label class="toggle-wrap">
          <div class="toggle-switch" class:activo={filtroDisponible}>
            <input type="checkbox" bind:checked={filtroDisponible} class="sr-only" />
            <div class="toggle-thumb"></div>
          </div>
          <span>Solo disponibles</span>
        </label>

        <!-- Limpiar filtros -->
        {#if hayFiltrosActivos}
          <button class="btn-limpiar" on:click={limpiarFiltros}>
            ✕ Limpiar filtros
          </button>
        {/if}

        <button class="btn-buscar" on:click={buscarAhora}>Buscar</button>
      </div>
    </div>

    <!-- Contenido principal -->
    <div class="contenido-wrap">

      <!-- Info de resultados -->
      {#if !cargando && !error}
        <div class="resultados-info">
          <p class="resultados-texto">
            {#if hayFiltrosActivos}
              <strong>{perrosFiltrados.length}</strong> resultados filtrados de <strong>{totalElementos}</strong> total
            {:else}
              <strong>{totalElementos}</strong> perritos disponibles en el catálogo
            {/if}
          </p>
          <span class="pagina-info">Página {paginaActual + 1} de {totalPaginas}</span>
        </div>
      {/if}

      <!-- Estado: cargando -->
      {#if cargando}
        <div class="estado-pantalla">
          <div class="loader-wrap">
            <div class="loader-ring"></div>
            <p>Cargando perritos…</p>
          </div>
        </div>

      <!-- Estado: error -->
      {:else if error}
        <div class="estado-pantalla">
          <div class="estado-card">
            <span class="estado-icon">⚠️</span>
            <h3>No se pudo cargar el catálogo</h3>
            <p>{error}</p>
            <button class="btn-reintentar" on:click={cargar}>Reintentar</button>
          </div>
        </div>

      <!-- Estado: sin resultados -->
      {:else if perrosFiltrados.length === 0}
        <div class="estado-pantalla">
          <div class="estado-card">
            <span class="estado-icon">🐾</span>
            <h3>No se encontraron perritos</h3>
            <p>Prueba cambiando los filtros o la búsqueda.</p>
            <button class="btn-reintentar" on:click={limpiarFiltros}>Limpiar filtros</button>
          </div>
        </div>

      <!-- Grid de resultados -->
      {:else}
        <div class="cuadricula">
          {#each perrosFiltrados as perro (perro.id)}
            <TarjetaPerro {perro} mostrarNoDisponible={true} />
          {/each}
        </div>

        <!-- Paginador -->
        {#if totalPaginas > 1}
          <div class="paginador">
            <button
              class="pag-btn pag-nav"
              disabled={paginaActual === 0}
              on:click={() => irPagina(paginaActual - 1)}
            >
              ← Anterior
            </button>

            <div class="pag-numeros">
              {#each Array(totalPaginas) as _, i}
                {#if Math.abs(i - paginaActual) <= 2 || i === 0 || i === totalPaginas - 1}
                  {#if i !== 0 && Math.abs(i - paginaActual) > 2 && i !== totalPaginas - 1}
                    <span class="pag-ellipsis">…</span>
                  {:else}
                    <button
                      class="pag-btn pag-num"
                      class:activo={paginaActual === i}
                      on:click={() => irPagina(i)}
                    >
                      {i + 1}
                    </button>
                  {/if}
                {/if}
              {/each}
            </div>

            <button
              class="pag-btn pag-nav"
              disabled={paginaActual === totalPaginas - 1}
              on:click={() => irPagina(paginaActual + 1)}
            >
              Siguiente →
            </button>
          </div>
        {/if}
      {/if}
    </div>
  </main>
</RutaProtegida>

<style>
  main { min-height: calc(100vh - 68px); background: var(--fondo); }

  /* Hero */
  .catalogo-hero {
    background: linear-gradient(to bottom, var(--fondo-suave), var(--fondo));
    border-bottom: 1px solid var(--borde);
    padding: 3rem 1.5rem 2rem;
    text-align: center;
  }
  .catalogo-hero-inner { max-width: 600px; margin: 0 auto; }
  .hero-etiqueta {
    display: inline-block;
    background: var(--naranja-suave);
    color: var(--color-primario);
    border: 1px solid var(--naranja-mid);
    padding: 0.25rem 0.85rem;
    border-radius: var(--radio-full);
    font-size: 0.78rem;
    font-weight: 700;
    letter-spacing: 0.05em;
    margin-bottom: 0.85rem;
  }
  .catalogo-hero h1 {
    font-size: 2.2rem;
    font-weight: 800;
    color: var(--texto);
    letter-spacing: -0.02em;
    margin-bottom: 0.5rem;
  }
  .catalogo-hero p {
    font-size: 1rem;
    color: var(--texto-suave);
    line-height: 1.6;
  }

  /* Filtros */
  .filtros-wrap {
    position: sticky;
    top: 68px;
    z-index: 50;
    background: var(--tarjeta);
    border-bottom: 1px solid var(--borde);
    box-shadow: var(--sombra-sm);
  }
  .filtros-inner {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0.9rem 1.5rem;
    display: flex;
    flex-wrap: wrap;
    gap: 0.75rem;
    align-items: center;
  }

  /* Campo búsqueda */
  .busqueda-wrap {
    position: relative;
    display: flex;
    align-items: center;
    flex: 1;
    min-width: 220px;
  }
  .busqueda-icon {
    position: absolute;
    left: 0.9rem;
    font-size: 0.9rem;
    pointer-events: none;
    z-index: 1;
  }
  .campo-busqueda {
    width: 100%;
    padding: 0.6rem 2.5rem 0.6rem 2.5rem;
    border: 1.5px solid var(--borde);
    border-radius: var(--radio-md);
    font-size: 0.9rem;
    background: var(--fondo);
    color: var(--texto);
    transition: border-color 0.2s, box-shadow 0.2s;
    outline: none;
  }
  .campo-busqueda:focus {
    border-color: var(--color-primario);
    box-shadow: 0 0 0 3px rgba(var(--color-primario-rgb), 0.1);
  }
  .limpiar-input {
    position: absolute;
    right: 0.6rem;
    background: none;
    border: none;
    color: var(--texto-suave);
    font-size: 0.85rem;
    cursor: pointer;
    padding: 3px;
    border-radius: 4px;
    transition: color 0.15s;
  }
  .limpiar-input:hover { color: var(--texto); }

  /* Chips de tamaño */
  .filtros-grupo {
    display: flex;
    align-items: center;
    gap: 0.4rem;
  }
  .filtros-label {
    font-size: 0.8rem;
    font-weight: 600;
    color: var(--texto-suave);
    white-space: nowrap;
  }
  .chips-grupo {
    display: flex;
    gap: 0.3rem;
    flex-wrap: wrap;
  }
  .chip {
    padding: 5px 13px;
    border-radius: var(--radio-full);
    border: 1.5px solid var(--borde);
    background: var(--tarjeta);
    color: var(--texto-suave);
    font-size: 0.8rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.15s;
    white-space: nowrap;
  }
  .chip:hover { border-color: var(--color-primario); color: var(--color-primario); }
  .chip.activo {
    background: var(--color-primario);
    color: #fff;
    border-color: var(--color-primario);
  }

  /* Toggle */
  .toggle-wrap {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    cursor: pointer;
    font-size: 0.85rem;
    font-weight: 600;
    color: var(--texto-suave);
    white-space: nowrap;
    user-select: none;
  }
  .toggle-switch {
    width: 38px;
    height: 22px;
    background: var(--borde-fuerte);
    border-radius: var(--radio-full);
    position: relative;
    transition: background 0.2s;
    flex-shrink: 0;
  }
  .toggle-switch.activo { background: var(--color-primario); }
  .toggle-thumb {
    width: 16px;
    height: 16px;
    background: #fff;
    border-radius: 50%;
    position: absolute;
    top: 3px;
    left: 3px;
    transition: transform 0.2s;
    box-shadow: 0 1px 4px rgba(0,0,0,0.2);
  }
  .toggle-switch.activo .toggle-thumb { transform: translateX(16px); }
  .sr-only { position: absolute; width: 1px; height: 1px; overflow: hidden; clip: rect(0,0,0,0); }

  .btn-limpiar {
    padding: 5px 13px;
    border-radius: var(--radio-md);
    border: 1.5px solid var(--borde);
    background: none;
    color: var(--texto-suave);
    font-size: 0.8rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.15s;
    white-space: nowrap;
  }
  .btn-limpiar:hover {
    background: var(--rojo-suave);
    color: var(--rojo-texto);
    border-color: var(--rojo-texto);
  }

  .btn-buscar {
    padding: 0.55rem 1.1rem;
    border-radius: var(--radio-md);
    background: var(--color-primario);
    color: #fff;
    border: none;
    font-size: 0.88rem;
    font-weight: 700;
    cursor: pointer;
    transition: background 0.2s;
    white-space: nowrap;
  }
  .btn-buscar:hover { background: var(--color-primario-hover); }

  /* Contenido */
  .contenido-wrap {
    max-width: 1200px;
    margin: 0 auto;
    padding: 1.5rem 1.5rem 4rem;
  }

  /* Info de resultados */
  .resultados-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.25rem;
    flex-wrap: wrap;
    gap: 0.5rem;
  }
  .resultados-texto { font-size: 0.85rem; color: var(--texto-suave); }
  .resultados-texto strong { color: var(--texto); font-weight: 700; }
  .pagina-info {
    font-size: 0.8rem;
    color: var(--texto-muy-suave);
    font-weight: 500;
  }

  /* Estados */
  .estado-pantalla {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 5rem 1rem;
  }
  .estado-card {
    text-align: center;
    max-width: 340px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 0.75rem;
  }
  .estado-icon { font-size: 3.5rem; line-height: 1; }
  .estado-card h3 {
    font-size: 1.15rem;
    font-weight: 700;
    color: var(--texto);
  }
  .estado-card p { font-size: 0.9rem; color: var(--texto-suave); line-height: 1.5; }
  .btn-reintentar {
    padding: 0.65rem 1.4rem;
    border-radius: var(--radio-md);
    background: var(--color-primario);
    color: #fff;
    border: none;
    font-size: 0.9rem;
    font-weight: 700;
    cursor: pointer;
    transition: background 0.2s;
    margin-top: 0.25rem;
  }
  .btn-reintentar:hover { background: var(--color-primario-hover); }

  /* Loader */
  .loader-wrap {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 1rem;
    color: var(--texto-suave);
    font-size: 0.9rem;
  }
  .loader-ring {
    width: 44px;
    height: 44px;
    border: 3px solid var(--borde);
    border-top-color: var(--color-primario);
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
  }
  @keyframes spin { to { transform: rotate(360deg); } }

  /* Grid */
  .cuadricula {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(290px, 1fr));
    gap: 1.5rem;
  }

  /* Paginador */
  .paginador {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 0.5rem;
    margin-top: 3rem;
    flex-wrap: wrap;
  }
  .pag-btn {
    padding: 7px 14px;
    border-radius: var(--radio-md);
    border: 1.5px solid var(--borde);
    background: var(--tarjeta);
    color: var(--texto-suave);
    font-size: 0.88rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.15s;
    min-width: 40px;
    text-align: center;
  }
  .pag-btn:hover:not(:disabled) {
    border-color: var(--color-primario);
    color: var(--color-primario);
    background: var(--naranja-suave);
  }
  .pag-btn:disabled { opacity: 0.4; cursor: not-allowed; }
  .pag-btn.activo {
    background: var(--color-primario);
    color: #fff;
    border-color: var(--color-primario);
  }
  .pag-nav { padding: 7px 18px; }
  .pag-numeros { display: flex; gap: 0.3rem; align-items: center; }
  .pag-ellipsis { color: var(--texto-muy-suave); padding: 0 4px; }

  @media (max-width: 768px) {
    .catalogo-hero h1 { font-size: 1.7rem; }
    .filtros-inner { padding: 0.75rem 1rem; }
    .filtros-grupo { width: 100%; }
    .cuadricula { grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); }
  }
</style>
