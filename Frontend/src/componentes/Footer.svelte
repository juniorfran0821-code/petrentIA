<script>
  const anio = new Date().getFullYear();
</script>

<footer>
  <div class="footer-inner">
    <div class="footer-logo">
      <span class="logo-icon">🐾</span>
      <span class="logo-text">PetRent</span>
    </div>
    <p class="footer-desc">
      La plataforma líder en alquiler de perros para paseos.<br />
      Conectamos a amantes de los perritos con dueños responsables.
    </p>
    <div class="footer-divider"></div>
    <p class="footer-copy">© {anio} <strong>PetRent</strong> — Desarrollado con Svelte + Spring Boot</p>
  </div>
</footer>

<style>
  footer {
    background: var(--footer-fondo);
    color: var(--footer-texto);
    padding: 3rem 1.5rem 2rem;
  }

  .footer-inner {
    max-width: 480px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: 1rem;
  }

  .footer-logo {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }
  .logo-icon { font-size: 1.6rem; line-height: 1; }
  .logo-text {
    font-size: 1.4rem;
    font-weight: 900;
    color: var(--color-primario);
    letter-spacing: -0.02em;
  }

  .footer-desc {
    font-size: 0.88rem;
    line-height: 1.75;
    color: var(--footer-texto);
  }

  .footer-divider {
    width: 48px;
    height: 2px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 99px;
  }

  .footer-copy {
    font-size: 0.8rem;
    color: var(--footer-texto);
    opacity: 0.75;
  }
  .footer-copy strong {
    color: var(--color-primario);
    opacity: 1;
  }
</style>
