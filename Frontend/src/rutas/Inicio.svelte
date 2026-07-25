<script>
  import { onMount } from 'svelte';
  import { navigate } from 'svelte-routing';
  import TarjetaPerro from '../componentes/TarjetaPerro.svelte';
  import * as perroService from '../servicios/perroService.js';

  let perrosDestacados = [];

  onMount(async () => {
    try {
      const data = await perroService.listar(0, 3);
      perrosDestacados = data.content.filter(p => p.disponible);
    } catch {
      perrosDestacados = [];
    }
  });
</script>

<main>
  <!-- ===== HERO ===== -->
  <section class="hero">
    <div class="hero-contenido">
      <div class="hero-badge">
        <span class="badge-dot"></span>
        Más de 1,000 perritos disponibles
      </div>
      <h1 class="hero-titulo">
        Alquila al mejor<br />
        <span class="texto-acento">compañero peludo</span>
      </h1>
      <p class="hero-desc">Pasea con el perro perfecto para ti. Seguros, cariñosos y listos para aventuras. ¡Tu próxima caminata ya tiene amigo!</p>
      <div class="hero-acciones">
        <button class="btn-hero-primario" on:click={() => navigate('/perros')}>
          Ver perritos disponibles
          <span class="btn-arrow">→</span>
        </button>
        <button class="btn-hero-secundario" on:click={() => navigate('/registro')}>
          Crear cuenta gratis
        </button>
      </div>
      <div class="hero-trust">
        <div class="trust-avatares">
          <div class="trust-av" style="background: #e07b39">A</div>
          <div class="trust-av" style="background: #c9622a">M</div>
          <div class="trust-av" style="background: #a04d1f">C</div>
        </div>
        <p>+2,500 clientes satisfechos este mes</p>
      </div>
    </div>
    <div class="hero-imagen-wrap">
      <div class="hero-imagen-deco"></div>
      <img src="/inicio.jpg" alt="Persona paseando un perro" class="hero-img" />
    </div>
  </section>

  <!-- ===== ESTADÍSTICAS ===== -->
  <section class="stats-band">
    <div class="stat-item"><strong>200+</strong><span>Paseos completados</span></div>
    <div class="stat-item"><strong>1,000+</strong><span>Perritos disponibles</span></div>
    <div class="stat-item"><strong>4.8★</strong><span>Calificación promedio</span></div>
    <div class="stat-item"><strong>100%</strong><span>Clientes satisfechos</span></div>
  </section>

  <!-- ===== CÓMO FUNCIONA ===== -->
  <section class="como-funciona">
    <div class="seccion-header">
      <span class="seccion-etiqueta">Proceso simple</span>
      <h2>¿Cómo funciona?</h2>
      <p>En tres sencillos pasos</p>
    </div>
    <div class="pasos-grid">
      <div class="paso-card">
        <div class="paso-numero">1</div>
        <div class="paso-icono">🔍</div>
        <h3>Elige tu perrito</h3>
        <p>Navega por nuestro catálogo y encuentra el compañero ideal.</p>
        <div class="paso-flecha">→</div>
      </div>
      <div class="paso-card">
        <div class="paso-numero">2</div>
        <div class="paso-icono">🛒</div>
        <h3>Agrega al carrito</h3>
        <p>Selecciona las horas y agrega tu reserva al carrito.</p>
        <div class="paso-flecha">→</div>
      </div>
      <div class="paso-card">
        <div class="paso-numero">3</div>
        <div class="paso-icono">🐾</div>
        <h3>¡A pasear!</h3>
        <p>Confirma la reserva y disfruta de una caminata increíble.</p>
      </div>
    </div>
  </section>

  <!-- ===== PERRITOS DESTACADOS ===== -->
  {#if perrosDestacados.length > 0}
    <section class="destacados">
      <div class="seccion-header">
        <span class="seccion-etiqueta">Disponibles ahora</span>
        <h2>Perritos destacados</h2>
        <p>Estos adorables compañeros están listos para salir contigo hoy</p>
      </div>
      <div class="cuadricula-destacados">
        {#each perrosDestacados as perro}
          <TarjetaPerro {perro} />
        {/each}
      </div>
      <div class="ver-todos-wrap">
        <button class="btn-ver-todos" on:click={() => navigate('/perros')}>
          Ver todos los perritos disponibles
          <span>→</span>
        </button>
      </div>
    </section>
  {/if}

  <!-- ===== BENEFICIOS ===== -->
  <section class="beneficios">
    <div class="beneficios-inner">
      <div class="beneficios-texto">
        <span class="seccion-etiqueta">¿Por qué PetRent?</span>
        <h2>La forma más fácil de disfrutar con un perrito</h2>
        <p>No tienes que adoptar ni comprar un perro para disfrutar de su compañía. Con PetRent puedes reservar un paseo cuando quieras.</p>
        <div class="beneficios-lista">
          <div class="beneficio">
            <span class="beneficio-icon">🛡️</span>
            <div>
              <strong>Seguridad garantizada</strong>
              <p>Todos los perritos están verificados y sus dueños identificados.</p>
            </div>
          </div>
          <div class="beneficio">
            <span class="beneficio-icon">⚡</span>
            <div>
              <strong>Reserva en segundos</strong>
              <p>Elige, selecciona las horas y confirma con tu método de pago favorito.</p>
            </div>
          </div>
          <div class="beneficio">
            <span class="beneficio-icon">💰</span>
            <div>
              <strong>Precios transparentes</strong>
              <p>Sin costos ocultos. Sabes exactamente cuánto pagarás antes de confirmar.</p>
            </div>
          </div>
          <div class="beneficio">
            <span class="beneficio-icon">⭐</span>
            <div>
              <strong>Reseñas verificadas</strong>
              <p>Lee opiniones reales de otros usuarios antes de elegir tu compañero.</p>
            </div>
          </div>
        </div>
        <button class="btn-hero-primario" on:click={() => navigate('/perros')}>
          Explorar perritos
          <span class="btn-arrow">→</span>
        </button>
      </div>
      <div class="beneficios-imagen">
        <img src="/inicio.jpg" alt="Beneficios PetRent" />
      </div>
    </div>
  </section>

  <!-- ===== CTA FINAL ===== -->
  <section class="cta-final">
    <div class="cta-inner">
      <span class="cta-emoji">🐾</span>
      <h2>¿Listo para el mejor paseo de tu vida?</h2>
      <p>Únete a cientos de personas que ya disfrutan de nuestros perritos.</p>
      <div class="cta-acciones">
        <button class="btn-cta-primario" on:click={() => navigate('/registro')}>
          Comenzar gratis
        </button>
        <button class="btn-cta-secundario" on:click={() => navigate('/perros')}>
          Ver perritos →
        </button>
      </div>
    </div>
  </section>
</main>

<style>
  main { background: var(--fondo); overflow-x: hidden; }

  /* ===== HERO ===== */
  .hero {
    max-width: 1200px;
    margin: 0 auto;
    padding: 5rem 1.5rem 4rem;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 4rem;
    align-items: center;
    min-height: calc(100vh - 68px - 80px);
  }

  .hero-badge {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    background: var(--naranja-suave);
    color: var(--color-primario);
    border: 1px solid var(--naranja-mid);
    padding: 0.35rem 0.9rem;
    border-radius: var(--radio-full);
    font-size: 0.8rem;
    font-weight: 700;
    margin-bottom: 1.5rem;
    letter-spacing: 0.02em;
  }
  .badge-dot {
    width: 7px;
    height: 7px;
    background: var(--color-primario);
    border-radius: 50%;
    animation: pulse 2s ease-in-out infinite;
  }
  @keyframes pulse {
    0%, 100% { opacity: 1; transform: scale(1); }
    50% { opacity: 0.6; transform: scale(1.3); }
  }

  .hero-titulo {
    font-size: 3.2rem;
    font-weight: 900;
    line-height: 1.1;
    color: var(--texto);
    margin-bottom: 1.25rem;
    letter-spacing: -0.03em;
  }
  .texto-acento { color: var(--color-primario); }

  .hero-desc {
    font-size: 1.05rem;
    color: var(--texto-suave);
    line-height: 1.7;
    max-width: 460px;
    margin-bottom: 2rem;
  }

  .hero-acciones {
    display: flex;
    gap: 1rem;
    flex-wrap: wrap;
    margin-bottom: 2rem;
  }

  .btn-hero-primario {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    background: var(--color-primario);
    color: #fff;
    border: none;
    padding: 0.85rem 1.75rem;
    border-radius: var(--radio-lg);
    font-size: 1rem;
    font-weight: 700;
    cursor: pointer;
    transition: background 0.2s, transform 0.15s, box-shadow 0.2s;
    box-shadow: 0 4px 16px rgba(var(--color-primario-rgb), 0.4);
    letter-spacing: 0.01em;
  }
  .btn-hero-primario:hover {
    background: var(--color-primario-hover);
    transform: translateY(-2px);
    box-shadow: 0 6px 24px rgba(var(--color-primario-rgb), 0.5);
  }
  .btn-arrow { transition: transform 0.2s; }
  .btn-hero-primario:hover .btn-arrow { transform: translateX(3px); }

  .btn-hero-secundario {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    background: transparent;
    color: var(--texto);
    border: 2px solid var(--borde-fuerte);
    padding: 0.82rem 1.5rem;
    border-radius: var(--radio-lg);
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
  }
  .btn-hero-secundario:hover {
    border-color: var(--color-primario);
    color: var(--color-primario);
  }

  .hero-trust {
    display: flex;
    align-items: center;
    gap: 0.75rem;
  }
  .trust-avatares {
    display: flex;
    margin-right: 0.25rem;
  }
  .trust-av {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    border: 2px solid var(--tarjeta);
    color: #fff;
    font-size: 0.75rem;
    font-weight: 800;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-left: -8px;
  }
  .trust-av:first-child { margin-left: 0; }
  .hero-trust p {
    font-size: 0.82rem;
    color: var(--texto-suave);
    font-weight: 500;
  }

  /* Hero imagen */
  .hero-imagen-wrap {
    position: relative;
  }
  .hero-imagen-deco {
    position: absolute;
    inset: -20px;
    background: linear-gradient(135deg,
      rgba(var(--color-primario-rgb), 0.08) 0%,
      rgba(var(--color-primario-rgb), 0.04) 50%,
      transparent 100%
    );
    border-radius: 32px;
    z-index: 0;
  }
  .hero-img {
    width: 100%;
    height: 460px;
    object-fit: cover;
    border-radius: 28px;
    position: relative;
    z-index: 1;
    box-shadow: var(--sombra-xl);
  }

  .hero-chip {
    position: absolute;
    z-index: 2;
    background: var(--tarjeta);
    border: 1px solid var(--borde);
    border-radius: var(--radio-xl);
    padding: 0.7rem 1rem;
    display: flex;
    align-items: center;
    gap: 0.7rem;
    box-shadow: var(--sombra-lg);
    font-size: 1.25rem;
    white-space: nowrap;
  }
  .hero-chip.top { top: 24px; left: -28px; }
  .hero-chip.bottom { bottom: 32px; right: -20px; }
  .hero-chip div { display: flex; flex-direction: column; line-height: 1.2; }
  .hero-chip strong { font-size: 0.82rem; font-weight: 700; color: var(--texto); }
  .hero-chip small { font-size: 0.72rem; color: var(--texto-suave); }

  /* ===== STATS BAND ===== */
  .stats-band {
    background: var(--color-primario);
    padding: 2.5rem 1.5rem;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
    gap: 0;
  }
  .stat-item {
    text-align: center;
    padding: 0.5rem 1rem;
    border-right: 1px solid rgba(255,255,255,0.2);
  }
  .stat-item:last-child { border-right: none; }
  .stat-item strong {
    display: block;
    font-size: 2.2rem;
    font-weight: 900;
    color: #fff;
    letter-spacing: -0.03em;
    line-height: 1.1;
  }
  .stat-item span {
    font-size: 0.82rem;
    color: rgba(255,255,255,0.75);
    font-weight: 500;
    margin-top: 0.2rem;
    display: block;
  }

  /* ===== SECCIÓN HEADER REUTILIZABLE ===== */
  .seccion-header {
    text-align: center;
    margin-bottom: 3rem;
  }
  .seccion-etiqueta {
    display: inline-block;
    background: var(--naranja-suave);
    color: var(--color-primario);
    border: 1px solid var(--naranja-mid);
    padding: 0.25rem 0.85rem;
    border-radius: var(--radio-full);
    font-size: 0.78rem;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    margin-bottom: 0.85rem;
  }
  .seccion-header h2 {
    font-size: 2rem;
    font-weight: 800;
    color: var(--texto);
    margin-bottom: 0.6rem;
    letter-spacing: -0.02em;
  }
  .seccion-header p {
    font-size: 1rem;
    color: var(--texto-suave);
    max-width: 520px;
    margin: 0 auto;
    line-height: 1.6;
  }

  /* ===== CÓMO FUNCIONA ===== */
  .como-funciona {
    padding: 5rem 1.5rem;
    background: var(--fondo-suave);
  }
  .pasos-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
    gap: 1.5rem;
    max-width: 1000px;
    margin: 0 auto;
    position: relative;
  }
  .paso-card {
    background: var(--tarjeta);
    border: 1px solid var(--borde);
    border-radius: var(--radio-xl);
    padding: 2rem 1.5rem;
    text-align: center;
    box-shadow: var(--sombra-xs);
    transition: transform 0.2s, box-shadow 0.2s;
    position: relative;
  }
  .paso-card:hover {
    transform: translateY(-4px);
    box-shadow: var(--sombra-md);
  }
  .paso-numero {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: var(--color-primario);
    color: #fff;
    font-size: 0.9rem;
    font-weight: 800;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 1rem;
  }
  .paso-icono {
    font-size: 2.5rem;
    margin-bottom: 0.75rem;
    line-height: 1;
  }
  .paso-card h3 {
    font-size: 1rem;
    font-weight: 700;
    color: var(--texto);
    margin-bottom: 0.4rem;
  }
  .paso-card p {
    font-size: 0.87rem;
    color: var(--texto-suave);
    line-height: 1.6;
  }
  .paso-flecha {
    position: absolute;
    top: 50%;
    right: -24px;
    transform: translateY(-50%);
    font-size: 1.5rem;
    color: var(--color-primario);
    font-weight: 700;
    z-index: 1;
  }

  /* ===== DESTACADOS ===== */
  .destacados {
    padding: 5rem 1.5rem;
    background: var(--fondo);
    max-width: 1200px;
    margin: 0 auto;
  }
  .cuadricula-destacados {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 1.5rem;
    margin-bottom: 2.5rem;
  }
  .ver-todos-wrap { text-align: center; }
  .btn-ver-todos {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    background: var(--fondo-suave);
    color: var(--texto);
    border: 2px solid var(--borde-fuerte);
    padding: 0.8rem 1.75rem;
    border-radius: var(--radio-lg);
    font-size: 0.95rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
  }
  .btn-ver-todos:hover {
    border-color: var(--color-primario);
    color: var(--color-primario);
    background: var(--naranja-suave);
  }

  /* ===== BENEFICIOS ===== */
  .beneficios {
    background: var(--fondo-suave);
    padding: 5rem 1.5rem;
  }
  .beneficios-inner {
    max-width: 1100px;
    margin: 0 auto;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 5rem;
    align-items: center;
  }
  .beneficios-texto .seccion-etiqueta { margin-bottom: 0.75rem; }
  .beneficios-texto h2 {
    font-size: 2rem;
    font-weight: 800;
    color: var(--texto);
    margin-bottom: 0.75rem;
    letter-spacing: -0.02em;
    line-height: 1.2;
  }
  .beneficios-texto > p {
    font-size: 0.97rem;
    color: var(--texto-suave);
    line-height: 1.7;
    margin-bottom: 2rem;
  }
  .beneficios-lista {
    display: flex;
    flex-direction: column;
    gap: 1.25rem;
    margin-bottom: 2rem;
  }
  .beneficio {
    display: flex;
    gap: 1rem;
    align-items: flex-start;
  }
  .beneficio-icon {
    width: 40px;
    height: 40px;
    background: var(--naranja-suave);
    border: 1px solid var(--naranja-mid);
    border-radius: var(--radio-md);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.1rem;
    flex-shrink: 0;
  }
  .beneficio div { flex: 1; }
  .beneficio strong {
    display: block;
    font-size: 0.92rem;
    font-weight: 700;
    color: var(--texto);
    margin-bottom: 0.15rem;
  }
  .beneficio p {
    font-size: 0.85rem;
    color: var(--texto-suave);
    line-height: 1.5;
  }
  .beneficios-imagen {
    position: relative;
  }
  .beneficios-imagen img {
    width: 100%;
    height: 420px;
    object-fit: cover;
    border-radius: var(--radio-2xl);
    box-shadow: var(--sombra-xl);
  }
  .float-card {
    position: absolute;
    bottom: -20px;
    left: -20px;
    background: var(--tarjeta);
    border: 1px solid var(--borde);
    border-radius: var(--radio-xl);
    padding: 0.85rem 1.1rem;
    display: flex;
    align-items: center;
    gap: 0.75rem;
    box-shadow: var(--sombra-lg);
    font-size: 1.3rem;
    white-space: nowrap;
  }
  .float-card div { display: flex; flex-direction: column; line-height: 1.3; }
  .float-card strong { font-size: 0.85rem; font-weight: 700; color: var(--texto); }
  .float-card small { font-size: 0.75rem; color: var(--texto-suave); }

  /* ===== CTA FINAL ===== */
  .cta-final {
    background: linear-gradient(135deg, #1c1917 0%, #292524 100%);
    padding: 6rem 1.5rem;
    position: relative;
    overflow: hidden;
  }
  .cta-final::before {
    content: '';
    position: absolute;
    top: -100px; right: -100px;
    width: 400px; height: 400px;
    background: radial-gradient(circle, rgba(224,123,57,0.12) 0%, transparent 70%);
    border-radius: 50%;
  }
  .cta-inner {
    text-align: center;
    max-width: 600px;
    margin: 0 auto;
    position: relative;
    z-index: 1;
  }
  .cta-emoji {
    font-size: 3rem;
    display: block;
    margin-bottom: 1rem;
  }
  .cta-inner h2 {
    font-size: 2.4rem;
    font-weight: 900;
    color: #fff;
    margin-bottom: 0.75rem;
    letter-spacing: -0.02em;
    line-height: 1.15;
  }
  .cta-inner p {
    font-size: 1rem;
    color: #a8a29e;
    line-height: 1.7;
    margin-bottom: 2.25rem;
  }
  .cta-acciones {
    display: flex;
    gap: 1rem;
    justify-content: center;
    flex-wrap: wrap;
  }
  .btn-cta-primario {
    background: var(--color-primario);
    color: #fff;
    border: none;
    padding: 0.9rem 2rem;
    border-radius: var(--radio-lg);
    font-size: 1rem;
    font-weight: 700;
    cursor: pointer;
    transition: background 0.2s, transform 0.15s;
    box-shadow: 0 4px 20px rgba(var(--color-primario-rgb), 0.5);
  }
  .btn-cta-primario:hover {
    background: var(--color-primario-hover);
    transform: translateY(-2px);
  }
  .btn-cta-secundario {
    background: transparent;
    color: #e8e5e0;
    border: 2px solid rgba(255,255,255,0.2);
    padding: 0.88rem 1.75rem;
    border-radius: var(--radio-lg);
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
  }
  .btn-cta-secundario:hover {
    border-color: rgba(255,255,255,0.5);
    color: #fff;
  }

  /* ===== RESPONSIVE ===== */
  @media (max-width: 1024px) {
    .hero { gap: 2.5rem; }
    .hero-titulo { font-size: 2.6rem; }
    .hero-chip.top { left: -10px; }
    .hero-chip.bottom { right: -4px; }
    .beneficios-inner { gap: 3rem; }
  }

  @media (max-width: 768px) {
    .hero {
      grid-template-columns: 1fr;
      padding: 3rem 1.5rem;
      text-align: center;
      min-height: unset;
    }
    .hero-titulo { font-size: 2.2rem; }
    .hero-desc { max-width: 100%; margin: 0 auto 1.75rem; }
    .hero-acciones { justify-content: center; }
    .hero-trust { justify-content: center; }
    .hero-chip.top { display: none; }
    .hero-chip.bottom { bottom: 16px; right: 16px; }
    .hero-img { height: 300px; }
    .paso-flecha { display: none; }
    .beneficios-inner { grid-template-columns: 1fr; gap: 2rem; }
    .beneficios-imagen { display: none; }
    .cta-inner h2 { font-size: 1.8rem; }
    .stat-item { border-right: none; border-bottom: 1px solid rgba(255,255,255,0.2); }
    .stat-item:last-child { border-bottom: none; }
  }

  @media (max-width: 480px) {
    .hero-titulo { font-size: 1.9rem; }
    .btn-hero-primario, .btn-hero-secundario { width: 100%; justify-content: center; }
  }
</style>
