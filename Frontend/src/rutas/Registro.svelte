<script>

  import { navigate } from 'svelte-routing';
  import * as authService from '../servicios/authService.js';
  import { iniciarSesion } from '../tienda/tienda.js';

  let nombre = '';
  let correo = '';
  let password = '';
  let confirmarPassword = '';
  let error = '';
  let cargando = false;
  let mostrarPass = false;    
  let mostrarConfirm = false; 

  function validar() {
    if (!nombre.trim() || !correo.trim() || !password) return 'Completa todos los campos.';
    if (nombre.trim().length < 2) return 'El nombre debe tener al menos 2 caracteres.';
    if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(correo)) return 'Ingresa un correo válido.';
    if (password.length < 6) return 'La contraseña debe tener al menos 6 caracteres.';
    if (password !== confirmarPassword) return 'Las contraseñas no coinciden.';
    return '';
  }


  async function manejarRegistro() {
    error = validar();
    if (error) return;
    cargando = true;
    try {
      await authService.registrar(nombre.trim(), correo.trim(), password);
      const resultado = await iniciarSesion(correo.trim(), password);
      cargando = false;
      if (resultado.ok) {
        navigate('/', { replace: true }); 
      } else {
        navigate('/login', { replace: true }); 
      }
    } catch (e) {
      cargando = false;
      error = e.message || 'No se pudo completar el registro.';
    }
  }


</script>

<main class="auth-layout">
  <!-- Panel decorativo -->
  <div class="auth-panel-deco">
    <div class="deco-contenido">
      <span class="deco-emoji">🐾</span>
      <h2>Únete a PetRent</h2>
      <p>Crea tu cuenta en segundos y empieza a disfrutar de paseos increíbles con nuestros perritos.</p>
      <div class="deco-steps">
        <div class="step">
          <span class="step-num">1</span>
          <span>Crea tu cuenta gratis</span>
        </div>
        <div class="step">
          <span class="step-num">2</span>
          <span>Elige tu perrito favorito</span>
        </div>
        <div class="step">
          <span class="step-num">3</span>
          <span>¡Disfruta el paseo!</span>
        </div>
      </div>
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
        <h1>Crear cuenta</h1>
        <p>Regístrate para empezar a reservar</p>
      </div>

      {#if error}
        <div class="alerta alerta-error" role="alert">
          <span>⚠️</span> {error}
        </div>
      {/if}

      <div class="campo-grupo">
        <label for="nombre">Nombre completo</label>
        <div class="input-wrap">
          <span class="input-icon">👤</span>
          <input id="nombre" type="text" placeholder="Tu nombre completo" bind:value={nombre} autocomplete="name" />
        </div>
      </div>

      <div class="campo-grupo">
        <label for="correo">Correo electrónico</label>
        <div class="input-wrap">
          <span class="input-icon">✉️</span>
          <input id="correo" type="email" placeholder="tu@correo.com" bind:value={correo} autocomplete="email" />
        </div>
      </div>

      <div class="campo-grupo">
        <label for="password">Contraseña</label>
        <div class="input-wrap">
          <span class="input-icon">🔒</span>
          {#if mostrarPass}
            <input id="password" type="text" placeholder="Mínimo 6 caracteres" bind:value={password} autocomplete="new-password" />
          {:else}
            <input id="password" type="password" placeholder="Mínimo 6 caracteres" bind:value={password} autocomplete="new-password" />
          {/if}
          <button type="button" class="toggle-pass" on:click={() => (mostrarPass = !mostrarPass)}>
            {mostrarPass ? 'Ocultar' : 'Ver'}
          </button>
        </div>
      </div>

      <div class="campo-grupo">
        <label for="confirmar">Confirmar contraseña</label>
        <div class="input-wrap">
          <span class="input-icon">🔒</span>
          {#if mostrarConfirm}
            <input id="confirmar" type="text" placeholder="Repite tu contraseña" bind:value={confirmarPassword} autocomplete="new-password" />
          {:else}
            <input id="confirmar" type="password" placeholder="Repite tu contraseña" bind:value={confirmarPassword} autocomplete="new-password" />
          {/if}
          <button type="button" class="toggle-pass" on:click={() => (mostrarConfirm = !mostrarConfirm)}>
            {mostrarConfirm ? 'Ocultar' : 'Ver'}
          </button>
        </div>
        {#if confirmarPassword && password !== confirmarPassword}
          <span class="hint-error">Las contraseñas no coinciden</span>
        {:else if confirmarPassword && password === confirmarPassword}
          <span class="hint-ok">✓ Las contraseñas coinciden</span>
        {/if}
      </div>

      <button class="btn-submit" on:click={manejarRegistro} disabled={cargando}>
        {#if cargando}
          <span class="spinner"></span>
          Creando cuenta…
        {:else}
          Crear mi cuenta
        {/if}
      </button>

      <div class="auth-divider">¿Ya tienes cuenta?</div>

      <button class="btn-outline" on:click={() => navigate('/login')}>
        Iniciar sesión
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
    background: linear-gradient(135deg, #1c1917 0%, #292524 100%);
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
    top: -80px; right: -80px;
    width: 300px; height: 300px;
    background: radial-gradient(circle, rgba(224,123,57,0.18) 0%, transparent 70%);
    border-radius: 50%;
  }
  .deco-contenido { position: relative; z-index: 1; }
  .deco-emoji { font-size: 2.5rem; display: block; margin-bottom: 1rem; }
  .deco-contenido h2 {
    font-size: 2rem; font-weight: 900; color: #fff;
    margin-bottom: 0.6rem; line-height: 1.2;
  }
  .deco-contenido p {
    font-size: 0.95rem; color: #a8a29e;
    margin-bottom: 2rem; line-height: 1.7;
  }
  .deco-steps { display: flex; flex-direction: column; gap: 0.9rem; }
  .step {
    display: flex; align-items: center; gap: 0.85rem;
    font-size: 0.9rem; color: #d6d3d1;
  }
  .step-num {
    width: 28px; height: 28px; border-radius: 50%;
    background: var(--color-primario);
    color: #fff; font-size: 0.8rem; font-weight: 800;
    display: flex; align-items: center; justify-content: center;
    flex-shrink: 0;
  }

  /* Formulario */
  .auth-form-wrap {
    background: var(--fondo);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 2rem 1.5rem;
    overflow-y: auto;
  }
  .auth-card {
    width: 100%;
    max-width: 420px;
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }
  .auth-header { text-align: center; margin-bottom: 0.25rem; }
  .logo-btn {
    display: inline-flex; align-items: center; gap: 0.4rem;
    background: none; border: none; cursor: pointer; margin-bottom: 1rem;
  }
  .logo-btn span:first-child { font-size: 1.5rem; }
  .logo-name { font-size: 1.2rem; font-weight: 900; color: var(--color-primario); letter-spacing: -0.02em; }
  .auth-header h1 {
    font-size: 1.75rem; font-weight: 800; color: var(--texto);
    margin-bottom: 0.3rem; letter-spacing: -0.02em;
  }
  .auth-header p { font-size: 0.9rem; color: var(--texto-suave); }

  .alerta {
    display: flex; align-items: center; gap: 0.6rem;
    padding: 0.75rem 1rem; border-radius: var(--radio-md);
    font-size: 0.87rem; font-weight: 500;
    background: var(--rojo-suave); color: var(--rojo-texto);
    border-left: 3px solid var(--rojo-texto);
  }
  .alerta-error {}

  .campo-grupo { display: flex; flex-direction: column; gap: 0.4rem; }
  .campo-grupo label {
    font-size: 0.85rem; font-weight: 600; color: var(--texto);
    display: flex; align-items: center; gap: 0.4rem;
  }

  .input-wrap { position: relative; display: flex; align-items: center; }
  .input-icon {
    position: absolute; left: 0.9rem;
    font-size: 0.95rem; pointer-events: none; z-index: 1;
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
    position: absolute; right: 0.75rem;
    background: none; border: none; font-size: 1rem;
    cursor: pointer; padding: 4px; color: var(--texto-suave); transition: color 0.15s;
  }
  .toggle-pass:hover { color: var(--texto); }

  .hint-error { font-size: 0.78rem; color: var(--rojo-texto); font-weight: 500; }
  .hint-ok { font-size: 0.78rem; color: var(--verde-texto); font-weight: 600; }

  .btn-submit {
    width: 100%; padding: 0.85rem;
    border-radius: var(--radio-lg);
    background: var(--color-primario); color: #fff;
    border: none; font-size: 1rem; font-weight: 700;
    cursor: pointer; transition: background 0.2s, transform 0.15s, box-shadow 0.2s;
    box-shadow: 0 2px 8px rgba(var(--color-primario-rgb), 0.35);
    display: flex; align-items: center; justify-content: center; gap: 0.5rem;
    letter-spacing: 0.01em; margin-top: 0.25rem;
  }
  .btn-submit:hover:not(:disabled) {
    background: var(--color-primario-hover);
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba(var(--color-primario-rgb), 0.45);
  }
  .btn-submit:disabled { background: var(--borde-fuerte); cursor: not-allowed; box-shadow: none; color: var(--texto-suave); }

  .auth-divider {
    text-align: center; font-size: 0.82rem; color: var(--texto-muy-suave);
  }
  .btn-outline {
    width: 100%; padding: 0.8rem; border-radius: var(--radio-lg);
    background: transparent; color: var(--color-primario);
    border: 2px solid var(--color-primario);
    font-size: 0.95rem; font-weight: 700; cursor: pointer; transition: all 0.2s;
  }
  .btn-outline:hover { background: var(--color-primario); color: #fff; }

  .btn-back {
    background: none; border: none; color: var(--texto-suave);
    font-size: 0.87rem; font-weight: 500; cursor: pointer;
    text-align: center; padding: 0.4rem; transition: color 0.15s;
  }
  .btn-back:hover { color: var(--texto); }

  .spinner {
    width: 16px; height: 16px;
    border: 2.5px solid rgba(255,255,255,0.4);
    border-top-color: #fff; border-radius: 50%;
    animation: spin 0.7s linear infinite;
  }
  @keyframes spin { to { transform: rotate(360deg); } }

  @media (max-width: 768px) {
    .auth-layout { grid-template-columns: 1fr; }
    .auth-panel-deco { display: none; }
    .auth-form-wrap { align-items: flex-start; padding-top: 2rem; }
  }
</style>
