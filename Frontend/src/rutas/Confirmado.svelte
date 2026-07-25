<script>
  import { navigate } from 'svelte-routing';
  import RutaProtegida from '../componentes/RutaProtegida.svelte';
</script>

<RutaProtegida rutaActual="/confirmado">
  <main>
    <div class="confirmado-wrap">
      <!-- Icono animado -->
      <div class="icono-exito">
        <div class="check-ring"></div>
        <span class="check-icon">✓</span>
      </div>

      <!-- Contenido -->
      <div class="confirmado-contenido">
        <span class="confirmado-etiqueta">¡Reserva exitosa!</span>
        <h1>Tu reserva fue confirmada</h1>
        <p>
          Tu paseo ha sido registrado correctamente. Pronto recibirás la confirmación y podrás ver el estado de tu reserva desde <strong>Mi cuenta</strong>.
        </p>

        <!-- Detalles destacados -->
        <div class="detalles-card">
          <div class="detalle-item">
            <span class="detalle-icon">📅</span>
            <div>
              <strong>Reserva confirmada</strong>
              <span>Estado: CONFIRMADA</span>
            </div>
          </div>
          <div class="detalle-item">
            <span class="detalle-icon">🐾</span>
            <div>
              <strong>Paseo registrado</strong>
              <span>Tu perrito te espera</span>
            </div>
          </div>
          <div class="detalle-item">
            <span class="detalle-icon">💳</span>
            <div>
              <strong>Pago en proceso</strong>
              <span>Se procesará en breve</span>
            </div>
          </div>
        </div>

        <!-- Acciones -->
        <div class="confirmado-acciones">
          <button class="btn-cuenta" on:click={() => navigate('/mi-cuenta')}>
            👤 Ver mis reservas
          </button>
          <button class="btn-mas" on:click={() => navigate('/perros')}>
            🐾 Reservar otro perrito
          </button>
        </div>

        <button class="btn-inicio" on:click={() => navigate('/')}>
          ← Volver al inicio
        </button>
      </div>
    </div>
  </main>
</RutaProtegida>

<style>
  main {
    min-height: calc(100vh - 68px);
    background: var(--fondo);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 2rem 1.5rem;
  }

  .confirmado-wrap {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    max-width: 520px;
    width: 100%;
  }

  /* Icono animado */
  .icono-exito {
    position: relative;
    width: 100px;
    height: 100px;
    margin-bottom: 2rem;
  }
  .check-ring {
    position: absolute;
    inset: 0;
    border-radius: 50%;
    border: 4px solid #22c55e;
    animation: ringIn 0.5s ease-out forwards;
    background: #dcfce7;
  }
  .check-icon {
    position: absolute;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 2.5rem;
    color: #166534;
    font-weight: 900;
    animation: checkIn 0.4s ease-out 0.3s both;
  }
  @keyframes ringIn {
    from { transform: scale(0.5); opacity: 0; }
    to { transform: scale(1); opacity: 1; }
  }
  @keyframes checkIn {
    from { transform: scale(0.3); opacity: 0; }
    to { transform: scale(1); opacity: 1; }
  }

  /* Contenido */
  .confirmado-contenido {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 1.1rem;
    width: 100%;
  }
  .confirmado-etiqueta {
    display: inline-block;
    background: var(--verde-suave);
    color: var(--verde-texto);
    border: 1px solid var(--verde-texto);
    padding: 0.25rem 0.9rem;
    border-radius: var(--radio-full);
    font-size: 0.78rem;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.08em;
  }
  .confirmado-contenido h1 {
    font-size: 2rem;
    font-weight: 900;
    color: var(--texto);
    letter-spacing: -0.02em;
    line-height: 1.15;
  }
  .confirmado-contenido p {
    font-size: 0.97rem;
    color: var(--texto-suave);
    line-height: 1.7;
    max-width: 420px;
  }
  .confirmado-contenido strong { color: var(--texto); }

  /* Detalles card */
  .detalles-card {
    background: var(--tarjeta);
    border: 1px solid var(--borde);
    border-radius: var(--radio-xl);
    padding: 1.25rem 1.5rem;
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 0.85rem;
    box-shadow: var(--sombra-xs);
    margin: 0.5rem 0;
  }
  .detalle-item {
    display: flex;
    align-items: center;
    gap: 0.85rem;
    text-align: left;
  }
  .detalle-icon {
    font-size: 1.5rem;
    width: 42px;
    height: 42px;
    background: var(--fondo-suave);
    border-radius: var(--radio-md);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }
  .detalle-item div {
    display: flex;
    flex-direction: column;
    line-height: 1.3;
  }
  .detalle-item strong {
    font-size: 0.9rem;
    font-weight: 700;
    color: var(--texto);
  }
  .detalle-item span {
    font-size: 0.8rem;
    color: var(--texto-suave);
  }

  /* Acciones */
  .confirmado-acciones {
    display: flex;
    gap: 0.85rem;
    width: 100%;
    flex-wrap: wrap;
  }
  .btn-cuenta {
    flex: 1;
    min-width: 180px;
    padding: 0.85rem;
    border-radius: var(--radio-lg);
    background: var(--color-primario);
    color: #fff;
    border: none;
    font-size: 0.95rem;
    font-weight: 700;
    cursor: pointer;
    transition: background 0.2s, transform 0.15s;
    box-shadow: 0 4px 14px rgba(var(--color-primario-rgb), 0.35);
  }
  .btn-cuenta:hover {
    background: var(--color-primario-hover);
    transform: translateY(-1px);
  }
  .btn-mas {
    flex: 1;
    min-width: 180px;
    padding: 0.83rem;
    border-radius: var(--radio-lg);
    background: transparent;
    color: var(--color-primario);
    border: 2px solid var(--color-primario);
    font-size: 0.95rem;
    font-weight: 700;
    cursor: pointer;
    transition: all 0.2s;
  }
  .btn-mas:hover {
    background: var(--color-primario);
    color: #fff;
  }
  .btn-inicio {
    background: none;
    border: none;
    color: var(--texto-suave);
    font-size: 0.87rem;
    font-weight: 500;
    cursor: pointer;
    padding: 0.4rem;
    transition: color 0.15s;
  }
  .btn-inicio:hover { color: var(--texto); }
</style>
