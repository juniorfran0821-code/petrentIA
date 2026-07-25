<script>

  import { consultarIA } from "../servicios/iaService.js";

  /** @type {{ rol: 'usuario'|'asistente'|'error', texto: string }[]} */
  let mensajes = [];
  let inputTexto  = "";
  let cargando    = false;
  let inputEl;      

  const SUGERENCIAS = [
    "¿Qué raza de perro me recomiendas para un apartamento pequeño?",
    "¿Cuánto cuesta en promedio alquilar un perro por un día?",
    "¿Qué cuidados básicos necesita un perro durante el alquiler?",
    "¿Qué diferencia hay entre un perro pequeño, mediano y grande?",
    "¿Puedo alquilar un perro si tengo niños pequeños en casa?",
  ];

  async function enviar() {
    const texto = inputTexto.trim();
    if (!texto || cargando) return;

    mensajes = [...mensajes, { rol: "usuario", texto }];
    inputTexto = "";
    cargando = true;

    await scrollAlFondo();

    try {
      const data = await consultarIA(texto);
      mensajes = [...mensajes, { rol: "asistente", texto: data.respuesta }];
    } catch (e) {
      const msg = e.message || "No se pudo obtener una respuesta. Intenta de nuevo.";
      mensajes = [...mensajes, { rol: "error", texto: msg }];
    } finally {
      cargando = false;
      await scrollAlFondo();
      inputEl?.focus();
    }
  }

  function manejarTecla(e) {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      enviar();
    }
  }

  function usarSugerencia(sugerencia) {
    inputTexto = sugerencia;
    inputEl?.focus();
  }

  async function scrollAlFondo() {
    await new Promise(r => setTimeout(r, 50));
    const contenedor = document.getElementById("chat-mensajes");
    if (contenedor) contenedor.scrollTop = contenedor.scrollHeight;
  }
</script>

<main class="ia-page">

  <div class="ia-hero">
    <div class="ia-hero-inner">
      <span class="hero-badge">🤖 IA Local · Ollama</span>
      <h1>Asistente PetRent</h1>
      <p>Pregúntame sobre razas, cuidados, costos de alquiler o cualquier duda sobre nuestros perritos.</p>
    </div>
  </div>

  <div class="ia-contenido">

    {#if mensajes.length === 0}
      <div class="sugerencias-wrap">
        <p class="sugerencias-titulo">¿No sabes por dónde empezar? Prueba una de estas:</p>
        <div class="sugerencias-grid">
          {#each SUGERENCIAS as s}
            <button class="chip-sugerencia" on:click={() => usarSugerencia(s)}>
              {s}
            </button>
          {/each}
        </div>
      </div>
    {/if}

    <div class="chat-wrap" id="chat-mensajes" class:vacio={mensajes.length === 0}>
      {#each mensajes as m}
        <div class="mensaje" class:usuario={m.rol === 'usuario'}
                             class:asistente={m.rol === 'asistente'}
                             class:error={m.rol === 'error'}>

          <div class="mensaje-avatar">
            {#if m.rol === 'usuario'}
              👤
            {:else if m.rol === 'error'}
              ⚠️
            {:else}
              🐾
            {/if}
          </div>

          <div class="mensaje-burbuja">
            <span class="mensaje-rol">
              {m.rol === 'usuario' ? 'Tú' : m.rol === 'error' ? 'Error' : 'Asistente'}
            </span>
            <p class="mensaje-texto">{m.texto}</p>
          </div>
        </div>
      {/each}

      {#if cargando}
        <div class="mensaje asistente">
          <div class="mensaje-avatar">🐾</div>
          <div class="mensaje-burbuja">
            <span class="mensaje-rol">Asistente</span>
            <div class="typing">
              <span></span><span></span><span></span>
            </div>
          </div>
        </div>
      {/if}
    </div>

    <form class="chat-form" on:submit|preventDefault={enviar}>
      <div class="input-wrap">
        <textarea
          bind:this={inputEl}
          bind:value={inputTexto}
          on:keydown={manejarTecla}
          placeholder="Escribe tu pregunta aquí… (Enter para enviar)"
          rows="2"
          disabled={cargando}
          maxlength="1000"
          aria-label="Consulta al asistente"
        ></textarea>
        <button
          type="submit"
          class="btn-enviar"
          disabled={cargando || !inputTexto.trim()}
          aria-label="Enviar consulta"
        >
          {#if cargando}
            <span class="spinner"></span>
          {:else}
            ➤
          {/if}
        </button>
      </div>
      <p class="input-hint">
        {inputTexto.length}/1000 caracteres · Enter para enviar · Shift+Enter para nueva línea
      </p>
    </form>

  </div>
</main>

<style>
  /* ── Página principal ─────────────────────────────────────────────────── */
  .ia-page {
    min-height: calc(100vh - 68px);
    background: var(--fondo);
  }

  /* ── Hero ────────────────────────────────────────────────────────────── */
  .ia-hero {
    background: linear-gradient(135deg, var(--color-primario) 0%, #c9622a 100%);
    padding: 3rem 1.5rem 2.5rem;
    text-align: center;
    color: #fff;
  }
  .ia-hero-inner { max-width: 640px; margin: 0 auto; }
  .hero-badge {
    display: inline-block;
    background: rgba(255,255,255,0.2);
    border: 1px solid rgba(255,255,255,0.35);
    border-radius: 99px;
    padding: 0.25rem 1rem;
    font-size: 0.82rem;
    font-weight: 700;
    letter-spacing: 0.04em;
    margin-bottom: 1rem;
  }
  .ia-hero h1 {
    font-size: clamp(1.8rem, 4vw, 2.5rem);
    font-weight: 900;
    margin: 0 0 0.75rem;
    line-height: 1.15;
  }
  .ia-hero p { font-size: 1rem; opacity: 0.92; margin: 0; line-height: 1.6; }

  /* ── Contenido ───────────────────────────────────────────────────────── */
  .ia-contenido {
    max-width: 780px;
    margin: 0 auto;
    padding: 2rem 1.5rem 3rem;
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  /* ── Sugerencias ─────────────────────────────────────────────────────── */
  .sugerencias-wrap { text-align: center; }
  .sugerencias-titulo {
    font-size: 0.9rem;
    color: var(--texto-suave);
    margin: 0 0 1rem;
  }
  .sugerencias-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 0.6rem;
    justify-content: center;
  }
  .chip-sugerencia {
    background: var(--tarjeta);
    border: 1.5px solid var(--borde);
    border-radius: var(--radio-full);
    padding: 0.5rem 1rem;
    font-size: 0.85rem;
    color: var(--texto);
    cursor: pointer;
    transition: all 0.15s;
    text-align: left;
    line-height: 1.4;
  }
  .chip-sugerencia:hover {
    border-color: var(--color-primario);
    color: var(--color-primario);
    background: var(--naranja-suave);
  }

  /* ── Chat ────────────────────────────────────────────────────────────── */
  .chat-wrap {
    display: flex;
    flex-direction: column;
    gap: 1rem;
    max-height: 460px;
    overflow-y: auto;
    padding: 1rem;
    background: var(--tarjeta);
    border: 1px solid var(--borde);
    border-radius: var(--radio-lg);
    scroll-behavior: smooth;
  }
  .chat-wrap.vacio { display: none; }

  /* scrollbar sutil */
  .chat-wrap::-webkit-scrollbar { width: 5px; }
  .chat-wrap::-webkit-scrollbar-track { background: transparent; }
  .chat-wrap::-webkit-scrollbar-thumb { background: var(--borde); border-radius: 99px; }

  /* ── Mensajes ─────────────────────────────────────────────────────────── */
  .mensaje {
    display: flex;
    align-items: flex-start;
    gap: 0.75rem;
  }
  .mensaje.usuario { flex-direction: row-reverse; }

  .mensaje-avatar {
    font-size: 1.3rem;
    line-height: 1;
    flex-shrink: 0;
    margin-top: 2px;
  }

  .mensaje-burbuja {
    max-width: 78%;
    background: var(--fondo-suave);
    border: 1px solid var(--borde);
    border-radius: var(--radio-lg);
    padding: 0.75rem 1rem;
  }
  .mensaje.usuario .mensaje-burbuja {
    background: var(--naranja-suave);
    border-color: var(--color-primario);
  }
  .mensaje.error .mensaje-burbuja {
    background: var(--rojo-suave);
    border-color: var(--rojo-texto);
  }

  .mensaje-rol {
    display: block;
    font-size: 0.72rem;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    color: var(--texto-suave);
    margin-bottom: 0.35rem;
  }
  .mensaje.usuario .mensaje-rol { color: var(--color-primario); text-align: right; }
  .mensaje.error   .mensaje-rol { color: var(--rojo-texto); }

  .mensaje-texto {
    font-size: 0.93rem;
    line-height: 1.7;
    color: var(--texto);
    margin: 0;
    white-space: pre-wrap;   /* respeta los saltos de línea del modelo */
    word-break: break-word;
  }

  /* ── Indicador "escribiendo..." ──────────────────────────────────────── */
  .typing {
    display: flex;
    gap: 4px;
    align-items: center;
    padding: 4px 0;
  }
  .typing span {
    width: 7px;
    height: 7px;
    border-radius: 50%;
    background: var(--color-primario);
    animation: bounce 1.2s infinite ease-in-out;
  }
  .typing span:nth-child(2) { animation-delay: 0.2s; }
  .typing span:nth-child(3) { animation-delay: 0.4s; }
  @keyframes bounce {
    0%, 80%, 100% { transform: translateY(0); opacity: 0.5; }
    40%           { transform: translateY(-6px); opacity: 1; }
  }

  /* ── Formulario de entrada ───────────────────────────────────────────── */
  .chat-form { display: flex; flex-direction: column; gap: 0.5rem; }

  .input-wrap {
    display: flex;
    gap: 0.75rem;
    align-items: flex-end;
  }

  textarea {
    flex: 1;
    resize: none;
    padding: 0.85rem 1rem;
    border: 1.5px solid var(--borde);
    border-radius: var(--radio-lg);
    background: var(--tarjeta);
    color: var(--texto);
    font-size: 0.93rem;
    font-family: inherit;
    line-height: 1.5;
    transition: border-color 0.15s;
  }
  textarea:focus {
    outline: none;
    border-color: var(--color-primario);
    box-shadow: 0 0 0 3px rgba(var(--color-primario-rgb), 0.12);
  }
  textarea:disabled { opacity: 0.6; cursor: not-allowed; }

  .btn-enviar {
    width: 48px;
    height: 48px;
    border-radius: var(--radio-md);
    border: none;
    background: var(--color-primario);
    color: #fff;
    font-size: 1.1rem;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    transition: background 0.15s, transform 0.1s;
  }
  .btn-enviar:hover:not(:disabled) {
    background: var(--color-primario-hover);
    transform: translateY(-1px);
  }
  .btn-enviar:disabled { opacity: 0.5; cursor: not-allowed; }

  /* Spinner de carga dentro del botón */
  .spinner {
    width: 18px;
    height: 18px;
    border: 2.5px solid rgba(255,255,255,0.4);
    border-top-color: #fff;
    border-radius: 50%;
    animation: girar 0.7s linear infinite;
  }
  @keyframes girar { to { transform: rotate(360deg); } }

  .input-hint {
    font-size: 0.75rem;
    color: var(--texto-suave);
    margin: 0;
    text-align: right;
  }

  /* ── Responsive ──────────────────────────────────────────────────────── */
  @media (max-width: 600px) {
    .ia-contenido { padding: 1.5rem 1rem 2.5rem; }
    .mensaje-burbuja { max-width: 90%; }
    .chat-wrap { max-height: 360px; }
  }
</style>
