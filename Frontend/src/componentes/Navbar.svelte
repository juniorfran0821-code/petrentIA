<script>
  import { Link, navigate } from 'svelte-routing';
  import {
    cantidadCarrito,  
    sesion,          
    estaAutenticado,  
    esAdmin,         
    cerrarSesion,     
    temaOscuro        
  } from '../tienda/tienda.js';

  let menuAbierto = false;
  function cerrarMenu() { menuAbierto = false; }

  function manejarCerrarSesion() {
    cerrarSesion();
    navigate('/');
    cerrarMenu();
  }
</script>

<nav class="navbar">
  <div class="navbar-inner">
    <!-- Logo: enlace al inicio -->
    <Link to="/" class="logo" on:click={cerrarMenu}>
      <span class="logo-icon">🐾</span>
      <span class="logo-text">PetRent</span>
    </Link>

    <!-- Links de navegación -->
    <ul class="nav-links" class:abierto={menuAbierto}>
      <li>
        <Link to="/" on:click={cerrarMenu} class="nav-link">Inicio</Link>
      </li>
      <li>
        <Link to="/perros" on:click={cerrarMenu} class="nav-link">Explorar perritos</Link>
      </li>
      <li>
        <Link to="/ia" on:click={cerrarMenu} class="nav-link nav-ia">🤖 Asistente IA</Link>
      </li>

      <!-- Enlace al carrito con badge de cantidad -->
      <li>
        <Link to="/carrito" on:click={cerrarMenu} class="nav-link carrito-link">
          <span class="carrito-icon">🛒</span>
          <span>Carrito</span>
          {#if $cantidadCarrito > 0}
            <!-- Badge: muestra cuántos perros hay en el carrito -->
            <span class="badge-carrito">{$cantidadCarrito}</span>
          {/if}
        </Link>
      </li>

      <!-- Links condicionales según el estado de autenticación -->
      {#if $estaAutenticado}
        {#if $esAdmin}
          <!-- Admin: enlace al panel de administración -->
          <li>
            <Link to="/admin" on:click={cerrarMenu} class="nav-link nav-admin">
              ⚙️ Panel Admin
            </Link>
          </li>
        {:else}
          <!-- Usuario normal: enlace a su cuenta y reservas -->
          <li>
            <Link to="/mi-cuenta" on:click={cerrarMenu} class="nav-link">
              👤 Mi cuenta
            </Link>
          </li>
        {/if}

        <!-- Chip con el nombre y rol del usuario autenticado -->
        <li class="usuario-chip">
          <div class="avatar-mini">{($sesion?.nombre ?? 'U')[0].toUpperCase()}</div>
          <div class="usuario-info">
            <span class="usuario-nombre">{$sesion?.nombre?.split(' ')[0]}</span>
            <span class="usuario-rol" class:admin={$sesion?.rol === 'admin'}>{$sesion?.rol}</span>
          </div>
        </li>

        <!-- Botón de cierre de sesión -->
        <li>
          <button class="btn-logout" on:click={manejarCerrarSesion}>
            <span>Salir</span>
          </button>
        </li>

      {:else}
        <!-- Sin sesión: opciones para registrarse o iniciar sesión -->
        <li>
          <Link to="/registro" on:click={cerrarMenu} class="nav-link">Crear cuenta</Link>
        </li>
        <li>
          <Link to="/login" on:click={cerrarMenu} class="btn-login">Iniciar sesión</Link>
        </li>
      {/if}
    </ul>

    <!-- Controles del extremo derecho tema y hamburguesa -->
    <div class="nav-controles">
      <!-- Botón de modo oscuro/claro -->
      <button
        class="btn-tema"
        class:oscuro={$temaOscuro}
        on:click={temaOscuro.alternar}
        title={$temaOscuro ? 'Cambiar a modo claro' : 'Cambiar a modo oscuro'}
        aria-label="Alternar tema"
      >
        <span class="tema-icon">{$temaOscuro ? '☀️' : '🌙'}</span>
        <span class="tema-label">{$temaOscuro ? 'Claro' : 'Oscuro'}</span>
      </button>

      <!-- Botón hamburguesa para menú móvil -->
      <button
        class="btn-hamburger"
        on:click={() => (menuAbierto = !menuAbierto)}
        aria-label="Menú"
        aria-expanded={menuAbierto}
      >
        <span class="ham-bar" class:open={menuAbierto}></span>
        <span class="ham-bar" class:open={menuAbierto}></span>
        <span class="ham-bar" class:open={menuAbierto}></span>
      </button>
    </div>
  </div>
</nav>

{#if menuAbierto}
  <div class="overlay-menu" on:click={cerrarMenu}></div>
{/if}

<style>
  .navbar {
    position: sticky;
    top: 0;
    z-index: 200;
    background: var(--tarjeta);
    border-bottom: 1px solid var(--borde);
    box-shadow: var(--sombra-sm);
    transition: background 0.25s, border-color 0.25s;
  }

  .navbar-inner {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 1.5rem;
    height: 68px;
    display: flex;
    align-items: center;
    gap: 1.5rem;
  }

  /* Logo */
  .navbar :global(a.logo) {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    text-decoration: none;
    flex-shrink: 0;
  }
  .logo-icon { font-size: 1.5rem; line-height: 1; }
  .logo-text {
    font-size: 1.3rem;
    font-weight: 900;
    color: var(--color-primario);
    letter-spacing: -0.02em;
  }

  /* Nav links */
  .nav-links {
    display: flex;
    align-items: center;
    gap: 0.25rem;
    list-style: none;
    flex: 1;
    justify-content: flex-end;
  }

  .navbar :global(.nav-link) {
    display: flex;
    align-items: center;
    gap: 0.4rem;
    padding: 0.5rem 0.85rem;
    border-radius: var(--radio-md);
    font-size: 0.9rem;
    font-weight: 500;
    color: var(--texto-suave);
    text-decoration: none;
    transition: background 0.15s, color 0.15s;
    white-space: nowrap;
  }
  .navbar :global(.nav-link:hover),
  .navbar :global(.nav-link.active) {
    background: var(--naranja-suave);
    color: var(--color-primario);
  }

  .carrito-link { position: relative; }
  .badge-carrito {
    position: absolute;
    top: -4px;
    right: -4px;
    background: var(--color-primario);
    color: #fff;
    font-size: 0.65rem;
    font-weight: 800;
    width: 18px;
    height: 18px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 2px solid var(--tarjeta);
  }

  .navbar :global(.nav-admin) {
    background: var(--naranja-suave);
    color: var(--color-primario);
    font-weight: 600;
  }

  .navbar :global(.nav-ia) {
    background: var(--fondo-suave);
    color: var(--texto);
    font-weight: 600;
    border: 1.5px solid var(--borde);
  }
  .navbar :global(.nav-ia:hover) {
    background: var(--naranja-suave);
    color: var(--color-primario);
    border-color: var(--color-primario);
  }

  /* Chip de usuario autenticado */
  .usuario-chip {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.35rem 0.75rem;
    background: var(--fondo-suave);
    border-radius: var(--radio-full);
    border: 1px solid var(--borde);
    margin: 0 0.25rem;
  }
  .avatar-mini {
    width: 28px;
    height: 28px;
    border-radius: 50%;
    background: linear-gradient(135deg, var(--color-primario), #c9622a);
    color: #fff;
    font-size: 0.8rem;
    font-weight: 800;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }
  .usuario-info { display: flex; flex-direction: column; line-height: 1.2; }
  .usuario-nombre { font-size: 0.82rem; font-weight: 600; color: var(--texto); }
  .usuario-rol {
    font-size: 0.65rem;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    color: var(--verde-texto);
    background: var(--verde-suave);
    padding: 1px 5px;
    border-radius: 4px;
    width: fit-content;
  }
  .usuario-rol.admin { color: var(--color-primario); background: var(--naranja-suave); }

  /* Botón de cerrar sesión */
  .btn-logout {
    background: none;
    border: 1.5px solid var(--borde);
    color: var(--texto-suave);
    padding: 0.45rem 1rem;
    border-radius: var(--radio-md);
    font-size: 0.85rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.15s;
    white-space: nowrap;
  }
  .btn-logout:hover { background: var(--rojo-suave); color: var(--rojo-texto); border-color: var(--rojo-texto); }

  /* Botón de iniciar sesión */
  .navbar :global(.btn-login) {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    background: var(--color-primario);
    color: #fff !important;
    padding: 0.5rem 1.1rem;
    border-radius: var(--radio-md);
    font-size: 0.88rem;
    font-weight: 700;
    text-decoration: none;
    transition: background 0.2s, transform 0.15s, box-shadow 0.2s;
    box-shadow: 0 2px 8px rgba(var(--color-primario-rgb), 0.3);
  }
  .navbar :global(.btn-login:hover) {
    background: var(--color-primario-hover) !important;
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba(var(--color-primario-rgb), 0.4);
  }

  /* Controles: tema y hamburguesa */
  .nav-controles { display: flex; align-items: center; gap: 0.5rem; flex-shrink: 0; }

  .btn-tema {
    display: flex;
    align-items: center;
    gap: 0.4rem;
    padding: 0.45rem 0.9rem;
    border-radius: var(--radio-full);
    border: 1.5px solid var(--borde);
    background: var(--fondo-suave);
    color: var(--texto-suave);
    font-size: 0.82rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
    white-space: nowrap;
  }
  .btn-tema:hover { border-color: var(--color-primario); color: var(--color-primario); background: var(--naranja-suave); }
  .btn-tema.oscuro { background: var(--naranja-suave); color: var(--color-primario); border-color: var(--color-primario); }
  .tema-icon { font-size: 0.95rem; line-height: 1; }
  .tema-label { font-size: 0.78rem; }

  /* Hamburguesa (solo en móvil) */
  .btn-hamburger {
    display: none;
    flex-direction: column;
    justify-content: center;
    gap: 5px;
    width: 40px;
    height: 40px;
    padding: 8px;
    background: none;
    border: 1.5px solid var(--borde);
    border-radius: var(--radio-md);
    cursor: pointer;
  }
  .ham-bar {
    display: block;
    width: 100%;
    height: 2px;
    background: var(--texto);
    border-radius: 99px;
    transition: transform 0.25s, opacity 0.25s;
  }
  .ham-bar.open:nth-child(1) { transform: translateY(7px) rotate(45deg); }
  .ham-bar.open:nth-child(2) { opacity: 0; }
  .ham-bar.open:nth-child(3) { transform: translateY(-7px) rotate(-45deg); }

  .overlay-menu {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.4);
    z-index: 150;
    backdrop-filter: blur(2px);
  }

  /* ── Responsive ─── */
  @media (max-width: 900px) {
    .tema-label { display: none; }
    .btn-tema { padding: 0.45rem 0.6rem; }
  }

  @media (max-width: 768px) {
    .btn-hamburger { display: flex; }

    .nav-links {
      display: none;
      position: fixed;
      top: 68px;
      left: 0; right: 0; bottom: 0;
      flex-direction: column;
      align-items: stretch;
      justify-content: flex-start;
      padding: 1.5rem;
      gap: 0.35rem;
      background: var(--tarjeta);
      z-index: 180;
      overflow-y: auto;
      border-top: 1px solid var(--borde);
    }
    .nav-links.abierto { display: flex; }
    .nav-links li { width: 100%; }
    .navbar :global(.nav-link) { width: 100%; padding: 0.85rem 1rem; font-size: 1rem; border-radius: var(--radio-lg); }
    .navbar :global(.btn-login) { width: 100%; padding: 0.85rem 1rem; font-size: 1rem; border-radius: var(--radio-lg); }
    .usuario-chip { border-radius: var(--radio-lg); padding: 0.75rem 1rem; }
    .btn-logout { width: 100%; padding: 0.85rem 1rem; font-size: 1rem; border-radius: var(--radio-lg); text-align: center; }
    .badge-carrito { position: static; margin-left: auto; }
  }
</style>
