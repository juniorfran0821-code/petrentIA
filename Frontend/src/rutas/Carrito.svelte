<script>
  import { navigate } from 'svelte-routing';
  import RutaProtegida from '../componentes/RutaProtegida.svelte';
  import { carrito, totalCarrito, quitarDelCarrito, sesion, cargarCatalogo } from '../tienda/tienda.js';
  import * as reservaService from '../servicios/reservaService.js';

  const METODOS = [
    { valor: 'TARJETA', label: 'Tarjeta de crédito/débito', icon: '💳' },
    { valor: 'EFECTIVO', label: 'Efectivo', icon: '💵' },
    { valor: 'TRANSFERENCIA', label: 'Transferencia bancaria', icon: '🏦' },
    { valor: 'PAYPAL', label: 'PayPal', icon: '🅿️' }
  ];
  let metodoPago = 'TARJETA';
  let confirmando = false;
  let error = '';

  let credencial;
  const unsub = sesion.subscribe(s => (credencial = s?.credencial));

  async function confirmar() {
    error = '';
    confirmando = true;
    try {
      for (const item of $carrito) {
        await reservaService.crear({ idPerro: item.id, horas: item.horas, metodoPago }, credencial);
      }
      carrito.set([]);
      await cargarCatalogo();
      navigate('/confirmado');
    } catch (e) {
      error = e.message || 'No se pudo confirmar la reserva. Intenta de nuevo.';
    } finally {
      confirmando = false;
    }
  }
</script>

<RutaProtegida rutaActual="/carrito">
  <main>
    <div class="carrito-layout">

      <!-- Encabezado -->
      <div class="carrito-header">
        <button class="btn-volver" on:click={() => navigate('/perros')}>← Seguir explorando</button>
        <h1>Mi carrito {#if $carrito.length > 0}<span class="count-badge">{$carrito.length}</span>{/if}</h1>
      </div>

      {#if $carrito.length === 0}
        <!-- Carrito vacío -->
        <div class="carrito-vacio">
          <div class="vacio-icono">🛒</div>
          <h2>Tu carrito está vacío</h2>
          <p>Explora nuestro catálogo y agrega los perritos que quieras reservar.</p>
          <button class="btn-explorar" on:click={() => navigate('/perros')}>
            Explorar perritos disponibles →
          </button>
        </div>

      {:else}
        <div class="carrito-grid">
          <!-- Lista de items -->
          <div class="items-col">
            {#if error}
              <div class="alerta-error-card" role="alert">
                <span>⚠️</span>
                <div>
                  <strong>Error al confirmar</strong>
                  <p>{error}</p>
                </div>
              </div>
            {/if}

            <div class="items-lista">
              {#each $carrito as item (item.id)}
                <div class="item-card">
                  <div class="item-imagen">
                    <img src={item.imagen} alt={item.nombre} />
                  </div>
                  <div class="item-info">
                    <div class="item-top">
                      <div>
                        <h3>{item.nombre}</h3>
                        <p class="item-raza">{item.raza} · {item.tamaño}</p>
                      </div>
                      <button class="btn-quitar" on:click={() => quitarDelCarrito(item.id)} title="Quitar del carrito">
                        ✕
                      </button>
                    </div>
                    <div class="item-bottom">
                      <div class="item-horas">
                        <span class="horas-icon">⏱️</span>
                        <span>{item.horas} {item.horas === 1 ? 'hora' : 'horas'}</span>
                        <span class="item-precio-unit">× ${item.precio.toFixed(2)}/h</span>
                      </div>
                      <div class="item-subtotal">
                        ${(item.precio * item.horas).toFixed(2)}
                      </div>
                    </div>
                  </div>
                </div>
              {/each}
            </div>
          </div>

          <!-- Panel resumen -->
          <div class="resumen-col">
            <div class="resumen-card">
              <h2>Resumen del pedido</h2>

              <!-- Desglose -->
              <div class="resumen-items">
                {#each $carrito as item}
                  <div class="resumen-linea">
                    <span>{item.nombre} ({item.horas}h)</span>
                    <span>${(item.precio * item.horas).toFixed(2)}</span>
                  </div>
                {/each}
              </div>

              <div class="resumen-divider"></div>

              <div class="resumen-total">
                <span>Total</span>
                <strong>${$totalCarrito.toFixed(2)}</strong>
              </div>

              <!-- Método de pago -->
              <div class="metodo-seccion">
                <h3>Método de pago</h3>
                <div class="metodos-grid">
                  {#each METODOS as m}
                    <button
                      class="metodo-btn"
                      class:activo={metodoPago === m.valor}
                      on:click={() => (metodoPago = m.valor)}
                    >
                      <span class="metodo-icon">{m.icon}</span>
                      <span class="metodo-label">{m.label}</span>
                      {#if metodoPago === m.valor}
                        <span class="metodo-check">✓</span>
                      {/if}
                    </button>
                  {/each}
                </div>
              </div>

              <button
                class="btn-confirmar"
                on:click={confirmar}
                disabled={confirmando}
              >
                {#if confirmando}
                  <span class="spinner"></span>
                  Procesando…
                {:else}
                  🐾 Confirmar reserva — ${$totalCarrito.toFixed(2)}
                {/if}
              </button>

              <p class="seguridad-nota">
                🔒 Pago seguro · Puedes cancelar tu reserva en cualquier momento
              </p>
            </div>
          </div>
        </div>
      {/if}
    </div>
  </main>
</RutaProtegida>

<style>
  main { min-height: calc(100vh - 68px); background: var(--fondo); }

  .carrito-layout {
    max-width: 1100px;
    margin: 0 auto;
    padding: 2rem 1.5rem 4rem;
  }

  /* Header */
  .carrito-header {
    display: flex;
    align-items: center;
    gap: 1.25rem;
    margin-bottom: 2rem;
    flex-wrap: wrap;
  }
  .btn-volver {
    background: none; border: none;
    color: var(--texto-suave); font-size: 0.88rem; font-weight: 500;
    cursor: pointer; transition: color 0.15s;
    padding: 0;
  }
  .btn-volver:hover { color: var(--color-primario); }
  .carrito-header h1 {
    font-size: 1.9rem; font-weight: 800;
    color: var(--texto); letter-spacing: -0.02em;
    display: flex; align-items: center; gap: 0.75rem;
    margin: 0;
  }
  .count-badge {
    display: inline-flex; align-items: center; justify-content: center;
    width: 28px; height: 28px;
    background: var(--color-primario); color: #fff;
    font-size: 0.8rem; font-weight: 800;
    border-radius: 50%;
  }

  /* Vacío */
  .carrito-vacio {
    text-align: center;
    padding: 5rem 1rem;
    display: flex; flex-direction: column; align-items: center; gap: 1rem;
    max-width: 420px; margin: 0 auto;
  }
  .vacio-icono { font-size: 5rem; line-height: 1; }
  .carrito-vacio h2 { font-size: 1.5rem; font-weight: 800; color: var(--texto); }
  .carrito-vacio p { font-size: 0.95rem; color: var(--texto-suave); line-height: 1.6; }
  .btn-explorar {
    padding: 0.85rem 1.75rem;
    border-radius: var(--radio-lg);
    background: var(--color-primario); color: #fff;
    border: none; font-size: 0.95rem; font-weight: 700;
    cursor: pointer; transition: background 0.2s, transform 0.15s;
    box-shadow: 0 4px 16px rgba(var(--color-primario-rgb), 0.35);
    margin-top: 0.5rem;
  }
  .btn-explorar:hover {
    background: var(--color-primario-hover);
    transform: translateY(-1px);
  }

  /* Grid */
  .carrito-grid {
    display: grid;
    grid-template-columns: 1fr 380px;
    gap: 2rem;
    align-items: start;
  }

  /* Alerta error */
  .alerta-error-card {
    display: flex; gap: 0.85rem; align-items: flex-start;
    background: var(--rojo-suave);
    border: 1px solid var(--rojo-texto);
    border-left: 4px solid var(--rojo-texto);
    border-radius: var(--radio-lg);
    padding: 1rem 1.1rem;
    margin-bottom: 1rem;
    font-size: 1.1rem;
  }
  .alerta-error-card div { flex: 1; }
  .alerta-error-card strong { display: block; font-size: 0.9rem; font-weight: 700; color: var(--rojo-texto); }
  .alerta-error-card p { font-size: 0.85rem; color: var(--rojo-texto); margin-top: 0.2rem; opacity: 0.85; }

  /* Items */
  .items-lista { display: flex; flex-direction: column; gap: 0.85rem; }
  .item-card {
    display: flex; gap: 1rem;
    background: var(--tarjeta);
    border: 1px solid var(--borde);
    border-radius: var(--radio-xl);
    padding: 1rem 1.1rem;
    transition: box-shadow 0.2s;
  }
  .item-card:hover { box-shadow: var(--sombra-sm); }
  .item-imagen {
    width: 80px; height: 80px; flex-shrink: 0;
    border-radius: var(--radio-lg); overflow: hidden;
  }
  .item-imagen img {
    width: 100%; height: 100%; object-fit: cover;
  }
  .item-info { flex: 1; display: flex; flex-direction: column; gap: 0.6rem; }
  .item-top {
    display: flex; justify-content: space-between; align-items: flex-start; gap: 0.5rem;
  }
  .item-info h3 {
    font-size: 0.97rem; font-weight: 700; color: var(--texto); line-height: 1.2;
  }
  .item-raza { font-size: 0.8rem; color: var(--texto-suave); margin-top: 2px; }
  .btn-quitar {
    background: none; border: none;
    color: var(--texto-muy-suave); font-size: 0.9rem;
    cursor: pointer; padding: 4px 6px;
    border-radius: var(--radio-sm);
    transition: all 0.15s; flex-shrink: 0;
  }
  .btn-quitar:hover {
    background: var(--rojo-suave);
    color: var(--rojo-texto);
  }
  .item-bottom {
    display: flex; justify-content: space-between; align-items: center;
  }
  .item-horas {
    display: flex; align-items: center; gap: 0.4rem;
    font-size: 0.83rem; color: var(--texto-suave);
  }
  .horas-icon { font-size: 0.88rem; }
  .item-precio-unit { color: var(--texto-muy-suave); }
  .item-subtotal {
    font-size: 1.05rem; font-weight: 800;
    color: var(--color-primario);
  }

  /* Resumen */
  .resumen-card {
    background: var(--tarjeta);
    border: 1px solid var(--borde);
    border-radius: var(--radio-2xl);
    padding: 1.75rem;
    box-shadow: var(--sombra-sm);
    position: sticky;
    top: 88px;
    display: flex; flex-direction: column; gap: 1.25rem;
  }
  .resumen-card h2 {
    font-size: 1.15rem; font-weight: 800; color: var(--texto);
    letter-spacing: -0.01em;
  }
  .resumen-items { display: flex; flex-direction: column; gap: 0.5rem; }
  .resumen-linea {
    display: flex; justify-content: space-between;
    font-size: 0.85rem; color: var(--texto-suave);
  }
  .resumen-divider { height: 1px; background: var(--borde); }
  .resumen-total {
    display: flex; justify-content: space-between; align-items: baseline;
    font-size: 1rem; font-weight: 600; color: var(--texto);
  }
  .resumen-total strong {
    font-size: 1.6rem; font-weight: 900;
    color: var(--color-primario); letter-spacing: -0.02em;
  }

  /* Método pago */
  .metodo-seccion h3 {
    font-size: 0.88rem; font-weight: 700; color: var(--texto);
    margin-bottom: 0.75rem;
  }
  .metodos-grid { display: flex; flex-direction: column; gap: 0.4rem; }
  .metodo-btn {
    display: flex; align-items: center; gap: 0.65rem;
    padding: 0.65rem 0.85rem;
    border: 1.5px solid var(--borde);
    border-radius: var(--radio-md);
    background: var(--fondo);
    cursor: pointer; transition: all 0.15s;
    text-align: left;
  }
  .metodo-btn:hover { border-color: var(--color-primario); background: var(--naranja-suave); }
  .metodo-btn.activo {
    border-color: var(--color-primario);
    background: var(--naranja-suave);
  }
  .metodo-icon { font-size: 1.1rem; flex-shrink: 0; }
  .metodo-label { flex: 1; font-size: 0.83rem; font-weight: 500; color: var(--texto); }
  .metodo-check {
    font-size: 0.85rem; font-weight: 700;
    color: var(--color-primario);
  }

  /* Botón confirmar */
  .btn-confirmar {
    width: 100%; padding: 0.9rem;
    border-radius: var(--radio-lg);
    background: var(--color-primario); color: #fff;
    border: none; font-size: 0.97rem; font-weight: 700;
    cursor: pointer;
    transition: background 0.2s, transform 0.15s, box-shadow 0.2s;
    box-shadow: 0 4px 16px rgba(var(--color-primario-rgb), 0.4);
    display: flex; align-items: center; justify-content: center; gap: 0.5rem;
  }
  .btn-confirmar:hover:not(:disabled) {
    background: var(--color-primario-hover);
    transform: translateY(-2px);
    box-shadow: 0 6px 24px rgba(var(--color-primario-rgb), 0.5);
  }
  .btn-confirmar:disabled {
    background: var(--borde-fuerte); cursor: not-allowed;
    box-shadow: none; color: var(--texto-suave);
  }

  .seguridad-nota {
    font-size: 0.76rem; color: var(--texto-muy-suave);
    text-align: center; line-height: 1.5;
  }

  .spinner {
    width: 16px; height: 16px;
    border: 2.5px solid rgba(255,255,255,0.4);
    border-top-color: #fff; border-radius: 50%;
    animation: spin 0.7s linear infinite;
  }
  @keyframes spin { to { transform: rotate(360deg); } }

  @media (max-width: 860px) {
    .carrito-grid { grid-template-columns: 1fr; }
    .resumen-card { position: static; }
  }
  @media (max-width: 480px) {
    .item-card { flex-direction: column; }
    .item-imagen { width: 100%; height: 160px; }
  }
</style>
