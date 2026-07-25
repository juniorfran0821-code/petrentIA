<script>
  import { navigate } from 'svelte-routing';

  export let perro;
  export let mostrarNoDisponible = false;

  function verDetalle() {
    if (perro.disponible) navigate(`/perros/${perro.id}`);
  }
</script>

<article
  class="tarjeta"
  class:no-disponible={!perro.disponible}
  on:click={verDetalle}
  on:keydown={e => e.key === 'Enter' && verDetalle()}
  role="button"
  tabindex="0"
  aria-label="Ver detalle de {perro.nombre}"
>
  <!-- Imagen -->
  <div class="tarjeta-imagen">
    <img src={perro.imagen} alt={perro.nombre} loading="lazy" />

    {#if !perro.disponible && mostrarNoDisponible}
      <div class="overlay-no-disponible">
        <span>No disponible</span>
      </div>
    {/if}

    <!-- Badges sobre la imagen -->
    <div class="imagen-badges">
      <span class="badge-tamano">{perro.tamaño}</span>
      {#if perro.disponible}
        <span class="badge-disponible">● Disponible</span>
      {/if}
    </div>

    {#if perro.calificacion > 0}
      <div class="rating-chip">
        ⭐ {perro.calificacion}
      </div>
    {/if}
  </div>

  <!-- Cuerpo -->
  <div class="tarjeta-cuerpo">
    <div class="tarjeta-top">
      <div class="tarjeta-id">
        <h3 class="tarjeta-nombre">{perro.nombre}</h3>
        <p class="tarjeta-raza">{perro.raza}</p>
      </div>
      <div class="precio-wrap">
        <span class="precio">${perro.precio.toFixed(2)}</span>
        <span class="precio-unit">/hora</span>
      </div>
    </div>

    {#if perro.etiquetas?.length > 0}
      <div class="etiquetas">
        {#each perro.etiquetas.slice(0, 3) as etiqueta}
          <span class="etiqueta">{etiqueta}</span>
        {/each}
      </div>
    {/if}

    {#if perro.calificacion > 0}
      <div class="resenas-row">
        <span class="estrellas">{'★'.repeat(Math.round(perro.calificacion))}{'☆'.repeat(5 - Math.round(perro.calificacion))}</span>
        <span class="resenas-count">{perro.reseñas} reseñas</span>
      </div>
    {:else}
      <div class="resenas-row sin-resenas">🆕 Recién registrado</div>
    {/if}

    <button
      class="btn-ver"
      class:btn-no-disp={!perro.disponible}
      disabled={!perro.disponible}
      on:click|stopPropagation={verDetalle}
    >
      {#if perro.disponible}
        Ver detalle <span class="btn-arr">→</span>
      {:else}
        No disponible
      {/if}
    </button>
  </div>
</article>

<style>
  .tarjeta {
    background: var(--tarjeta);
    border-radius: var(--radio-xl);
    border: 1px solid var(--borde);
    box-shadow: var(--sombra-xs);
    overflow: hidden;
    cursor: pointer;
    transition: transform 0.25s ease, box-shadow 0.25s ease, border-color 0.2s;
    display: flex;
    flex-direction: column;
  }
  .tarjeta:hover {
    transform: translateY(-6px);
    box-shadow: var(--sombra-lg);
    border-color: var(--borde-fuerte);
  }
  .tarjeta:focus-visible {
    outline: 2px solid var(--color-primario);
    outline-offset: 3px;
  }
  .tarjeta.no-disponible {
    cursor: default;
    opacity: 0.75;
  }
  .tarjeta.no-disponible:hover {
    transform: none;
    box-shadow: var(--sombra-xs);
  }

  /* Imagen */
  .tarjeta-imagen {
    position: relative;
    height: 210px;
    overflow: hidden;
    background: var(--fondo-suave);
  }
  .tarjeta-imagen img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.4s ease;
    display: block;
  }
  .tarjeta:hover .tarjeta-imagen img {
    transform: scale(1.06);
  }

  .overlay-no-disponible {
    position: absolute;
    inset: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    backdrop-filter: blur(2px);
  }
  .overlay-no-disponible span {
    background: rgba(0,0,0,0.7);
    color: #fff;
    font-size: 0.85rem;
    font-weight: 700;
    padding: 0.4rem 1rem;
    border-radius: var(--radio-full);
    border: 1px solid rgba(255,255,255,0.2);
  }

  .imagen-badges {
    position: absolute;
    top: 10px;
    left: 10px;
    display: flex;
    gap: 0.4rem;
    flex-wrap: wrap;
  }
  .badge-tamano {
    background: rgba(255, 255, 255, 0.92);
    backdrop-filter: blur(8px);
    color: var(--color-primario);
    font-size: 0.7rem;
    font-weight: 800;
    padding: 3px 9px;
    border-radius: var(--radio-full);
    letter-spacing: 0.03em;
    text-transform: uppercase;
  }
  .badge-disponible {
    background: rgba(22, 101, 52, 0.85);
    backdrop-filter: blur(8px);
    color: #86efac;
    font-size: 0.7rem;
    font-weight: 700;
    padding: 3px 9px;
    border-radius: var(--radio-full);
  }

  .rating-chip {
    position: absolute;
    bottom: 10px;
    right: 10px;
    background: rgba(0,0,0,0.65);
    backdrop-filter: blur(8px);
    color: #fff;
    font-size: 0.78rem;
    font-weight: 700;
    padding: 3px 9px;
    border-radius: var(--radio-full);
  }

  /* Cuerpo */
  .tarjeta-cuerpo {
    padding: 1.1rem 1.2rem 1.25rem;
    display: flex;
    flex-direction: column;
    gap: 0.65rem;
    flex: 1;
  }

  .tarjeta-top {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 0.5rem;
  }
  .tarjeta-nombre {
    font-size: 1.05rem;
    font-weight: 700;
    color: var(--texto);
    line-height: 1.2;
    letter-spacing: -0.01em;
  }
  .tarjeta-raza {
    font-size: 0.8rem;
    color: var(--texto-suave);
    margin-top: 2px;
  }
  .precio-wrap {
    text-align: right;
    flex-shrink: 0;
  }
  .precio {
    font-size: 1.2rem;
    font-weight: 800;
    color: var(--color-primario);
    letter-spacing: -0.02em;
    line-height: 1;
    display: block;
  }
  .precio-unit {
    font-size: 0.72rem;
    color: var(--texto-suave);
    font-weight: 500;
  }

  .etiquetas {
    display: flex;
    flex-wrap: wrap;
    gap: 0.3rem;
  }
  .etiqueta {
    background: var(--naranja-suave);
    color: var(--color-primario);
    font-size: 0.7rem;
    font-weight: 600;
    padding: 2px 8px;
    border-radius: var(--radio-full);
    border: 1px solid var(--naranja-mid);
  }

  .resenas-row {
    font-size: 0.8rem;
    color: var(--texto-suave);
    display: flex;
    align-items: center;
    gap: 0.4rem;
  }
  .estrellas {
    color: #f59e0b;
    font-size: 0.82rem;
    letter-spacing: 1px;
  }
  .resenas-count { color: var(--texto-muy-suave); font-size: 0.77rem; }
  .sin-resenas { color: var(--texto-muy-suave); font-size: 0.8rem; }

  /* Botón */
  .btn-ver {
    width: 100%;
    padding: 0.7rem 1rem;
    border-radius: var(--radio-md);
    background: var(--color-primario);
    color: #fff;
    border: none;
    font-size: 0.9rem;
    font-weight: 700;
    cursor: pointer;
    transition: background 0.2s, transform 0.15s;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.4rem;
    margin-top: auto;
  }
  .btn-ver:hover:not(:disabled) {
    background: var(--color-primario-hover);
    transform: translateY(-1px);
  }
  .btn-ver.btn-no-disp {
    background: var(--fondo-suave2);
    color: var(--texto-suave);
    cursor: not-allowed;
  }
  .btn-arr {
    transition: transform 0.2s;
  }
  .btn-ver:hover:not(:disabled) .btn-arr {
    transform: translateX(3px);
  }
</style>
