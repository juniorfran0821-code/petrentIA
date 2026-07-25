<script>

  import { navigate } from 'svelte-routing';
  import { iniciarSesion, estaAutenticado } from '../tienda/tienda.js';

  let redirigir = '/';
  const params = new URLSearchParams(window.location.search);
  if (params.get('redirigir')) redirigir = decodeURIComponent(params.get('redirigir'));

  let yaAutenticado;
  const unsub = estaAutenticado.subscribe(v => (yaAutenticado = v));
  unsub();
  if (yaAutenticado) navigate(redirigir, { replace: true });

  let correo     = '';
  let contrasena = '';
  let error      = '';      
  let cargando   = false;   
  let mostrarPass = false;  

  async function manejarLogin() {
    error = '';
    if (!correo || !contrasena) { error = 'Por favor completa todos los campos.'; return; }
    cargando = true;
    const resultado = await iniciarSesion(correo, contrasena); // llama al backend
    cargando = false;
    if (resultado.ok) {
      navigate(redirigir, { replace: true }); 
    } else {
      error = resultado.error; 
    }
  }

  function manejarTecla(e) {
    if (e.key === 'Enter') manejarLogin();
  }
</script>

<main class="auth-layout">
  <!-- Panel decorativo izquierdo -->
  <div class="auth-panel-deco">
    <div class="deco-contenido">
      <span class="deco-emoji">🐾</span>
      <h2>Bienvenido de vuelta</h2>
      <p>Encuentra al compañero perfecto para tu próximo paseo.</p>
      <div class="deco-features">
        <div class="feature">✅ Más de 1,000 perritos disponibles</div>
        <div class="feature">✅ Reservas seguras y rápidas</div>
        <div class="feature">✅ Pagos protegidos</div>
      </div>
    </div>
    <div class="deco-imagen">
      <img src="/inicio.jpg" alt="Perros felices" />
    </div>
  </div>

  <!-- Formulario -->
  <div class="auth-form-wrap">
    <div class="auth-card">
      <div class="auth-header">
        <button class="logo-btn" on:click={() => navigate('/')}>
          <span>🐾</span>
          <span class="logo-name">PetRent</span>
        </button>
        <h1>Iniciar sesión</h1>
        <p>Ingresa tus credenciales para continuar</p>
      </div>

      {#if error}
        <div class="alerta alerta-error" role="alert">
          <span class="alerta-icon">⚠️</span>
          {error}
        </div>
      {/if}

      <div class="campo-grupo">
        <label for="correo">Correo electrónico</label>
        <div class="input-wrap">
          <span class="input-icon">✉️</span>
          <input
            id="correo"
            type="email"
            placeholder="tu@correo.com"
            bind:value={correo}
            on:keydown={manejarTecla}
            autocomplete="email"
          />
        </div>
      </div>

      <div class="campo-grupo">
        <label for="contrasena">Contraseña</label>
        <div class="input-wrap">
          <span class="input-icon">🔒</span>
          {#if mostrarPass}
            <input
              id="contrasena"
              type="text"
              placeholder="Tu contraseña"
              bind:value={contrasena}
              on:keydown={manejarTecla}
              autocomplete="current-password"
            />
          {:else}
            <input
              id="contrasena"
              type="password"
              placeholder="Tu contraseña"
              bind:value={contrasena}
              on:keydown={manejarTecla}
              autocomplete="current-password"
            />
          {/if}
          <button type="button" class="toggle-pass" on:click={() => (mostrarPass = !mostrarPass)}>
            {mostrarPass ? 'Ocultar' : 'Ver'}
          </button>
        </div>
      </div>

      <button class="btn-submit" on:click={manejarLogin} disabled={cargando}>
        {#if cargando}
          <span class="spinner"></span>
          Verificando…
        {:else}
          Entrar a mi cuenta
        {/if}
      </button>

      <div class="auth-divider"><span>¿No tienes cuenta?</span></div>

      <button class="btn-outline" on:click={() => navigate('/registro')}>
        Crear cuenta gratis
      </button>

      <button class="btn-back" on:click={() => navigate('/')}>← Volver al inicio</button>
    </div>
  </div>
</main>

<style>
  .auth-layout {
    min-height: calc(100vh - 68px);
    display: grid;
    grid-template-columns: 1fr 1fr;
  }

  /* Panel decorativo */
  .auth-panel-deco {
    background: linear-gradient(135deg, #1c1917 0%, #292524 50%, #1c1917 100%);
    display: flex;
    flex-direction: column;
    justify-content: center;
    padding: 3rem;
    position: relative;
    overflow: hidden;
  }
  .auth-panel-deco::before {
    content: '';
    position: absolute;
    top: -60px;
    right: -60px;
    width: 300px;
    height: 300px;
    background: radial-gradient(circle, rgba(224, 123, 57, 0.15) 0%, transparent 70%);
    border-radius: 50%;
  }
  .auth-panel-deco::after {
    content: '';
    position: absolute;
    bottom: -80px;
    left: -40px;
    width: 250px;
    height: 250px;
    background: radial-gradient(circle, rgba(224, 123, 57, 0.1) 0%, transparent 70%);
    border-radius: 50%;
  }
  .deco-contenido {
    position: relative;
    z-index: 1;
  }
  .deco-emoji {
    font-size: 2.5rem;
    display: block;
    margin-bottom: 1rem;
  }
  .deco-contenido h2 {
    font-size: 2rem;
    font-weight: 900;
    color: #fff;
    margin-bottom: 0.6rem;
    line-height: 1.2;
  }
  .deco-contenido p {
    font-size: 1rem;
    color: #a8a29e;
    margin-bottom: 2rem;
    line-height: 1.6;
  }
  .deco-features {
    display: flex;
    flex-direction: column;
    gap: 0.65rem;
  }
  .feature {
    font-size: 0.9rem;
    color: #d6d3d1;
  }
  .deco-imagen {
    margin-top: 2.5rem;
    border-radius: var(--radio-xl);
    overflow: hidden;
    position: relative;
    z-index: 1;
  }
  .deco-imagen img {
    width: 100%;
    height: 220px;
    object-fit: cover;
    border-radius: var(--radio-xl);
    opacity: 0.7;
  }

  /* Formulario */
  .auth-form-wrap {
    background: var(--fondo);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 2rem 1.5rem;
  }

  .auth-card {
    width: 100%;
    max-width: 420px;
    display: flex;
    flex-direction: column;
    gap: 1.1rem;
  }

  .auth-header {
    text-align: center;
    margin-bottom: 0.5rem;
  }
  .logo-btn {
    display: inline-flex;
    align-items: center;
    gap: 0.4rem;
    background: none;
    border: none;
    cursor: pointer;
    margin-bottom: 1.25rem;
  }
  .logo-btn span:first-child { font-size: 1.5rem; }
  .logo-name {
    font-size: 1.2rem;
    font-weight: 900;
    color: var(--color-primario);
    letter-spacing: -0.02em;
  }
  .auth-header h1 {
    font-size: 1.75rem;
    font-weight: 800;
    color: var(--texto);
    margin-bottom: 0.3rem;
    letter-spacing: -0.02em;
  }
  .auth-header p {
    font-size: 0.9rem;
    color: var(--texto-suave);
  }

  /* Alertas */
  .alerta {
    display: flex;
    align-items: center;
    gap: 0.6rem;
    padding: 0.8rem 1rem;
    border-radius: var(--radio-md);
    font-size: 0.87rem;
    font-weight: 500;
  }
  .alerta-error {
    background: var(--rojo-suave);
    color: var(--rojo-texto);
    border-left: 3px solid var(--rojo-texto);
  }
  .alerta-icon { font-size: 1rem; flex-shrink: 0; }

  /* Campos */
  .campo-grupo {
    display: flex;
    flex-direction: column;
    gap: 0.4rem;
  }
  .campo-grupo label {
    font-size: 0.85rem;
    font-weight: 600;
    color: var(--texto);
  }
  .input-wrap {
    position: relative;
    display: flex;
    align-items: center;
  }
  .input-icon {
    position: absolute;
    left: 0.9rem;
    font-size: 0.95rem;
    pointer-events: none;
    z-index: 1;
  }
  .input-wrap input {
    width: 100%;
    padding: 0.75rem 1rem 0.75rem 2.75rem;
    border: 1.5px solid var(--borde);
    border-radius: var(--radio-md);
    font-size: 0.95rem;
    background: var(--tarjeta);
    color: var(--texto);
    transition: border-color 0.2s, box-shadow 0.2s;
    outline: none;
    box-sizing: border-box;
  }
  .input-wrap input:focus {
    border-color: var(--color-primario);
    box-shadow: 0 0 0 3px rgba(var(--color-primario-rgb), 0.12);
  }
  .toggle-pass {
    position: absolute;
    right: 0.75rem;
    background: none;
    border: none;
    font-size: 1rem;
    cursor: pointer;
    padding: 4px;
    border-radius: 4px;
    color: var(--texto-suave);
    transition: color 0.15s;
  }
  .toggle-pass:hover { color: var(--texto); }

  /* Botones */
  .btn-submit {
    width: 100%;
    padding: 0.85rem;
    border-radius: var(--radio-lg);
    background: var(--color-primario);
    color: #fff;
    border: none;
    font-size: 1rem;
    font-weight: 700;
    cursor: pointer;
    transition: background 0.2s, transform 0.15s, box-shadow 0.2s;
    box-shadow: 0 2px 8px rgba(var(--color-primario-rgb), 0.35);
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    letter-spacing: 0.01em;
    margin-top: 0.25rem;
  }
  .btn-submit:hover:not(:disabled) {
    background: var(--color-primario-hover);
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba(var(--color-primario-rgb), 0.45);
  }
  .btn-submit:disabled {
    background: var(--borde-fuerte);
    cursor: not-allowed;
    box-shadow: none;
    color: var(--texto-suave);
  }

  .auth-divider {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    font-size: 0.82rem;
    color: var(--texto-muy-suave);
    text-align: center;
    justify-content: center;
  }

  .btn-outline {
    width: 100%;
    padding: 0.8rem;
    border-radius: var(--radio-lg);
    background: transparent;
    color: var(--color-primario);
    border: 2px solid var(--color-primario);
    font-size: 0.95rem;
    font-weight: 700;
    cursor: pointer;
    transition: all 0.2s;
  }
  .btn-outline:hover {
    background: var(--color-primario);
    color: #fff;
  }

  .btn-back {
    background: none;
    border: none;
    color: var(--texto-suave);
    font-size: 0.87rem;
    font-weight: 500;
    cursor: pointer;
    text-align: center;
    padding: 0.4rem;
    transition: color 0.15s;
  }
  .btn-back:hover { color: var(--texto); }

  /* Spinner */
  .spinner {
    width: 16px;
    height: 16px;
    border: 2.5px solid rgba(255,255,255,0.4);
    border-top-color: #fff;
    border-radius: 50%;
    animation: spin 0.7s linear infinite;
  }
  @keyframes spin { to { transform: rotate(360deg); } }

  /* Responsive */
  @media (max-width: 768px) {
    .auth-layout {
      grid-template-columns: 1fr;
    }
    .auth-panel-deco {
      display: none;
    }
    .auth-form-wrap {
      align-items: flex-start;
      padding-top: 3rem;
    }
  }
</style>
