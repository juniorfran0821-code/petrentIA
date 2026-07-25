<script>
  import { onMount } from 'svelte';
  import { navigate } from 'svelte-routing';
  import { estaAutenticado, esAdmin } from '../tienda/tienda.js';


  export let rutaActual = '/';

  export let soloAdmin = false;

  let verificado = false;

  let permitido  = false;

  onMount(() => {
    let autenticado;
    let admin;
    const unsubAuth  = estaAutenticado.subscribe(v => (autenticado = v));
    const unsubAdmin = esAdmin.subscribe(v => (admin = v));
    unsubAuth();   
    unsubAdmin();

    if (!autenticado) {
      navigate(`/login?redirigir=${encodeURIComponent(rutaActual)}`, { replace: true });

    } else if (soloAdmin && !admin) {
      navigate('/', { replace: true });

    } else {
      permitido = true;
    }

    verificado = true; 
  });
</script>

{#if verificado && permitido}
  <slot />
{/if}
