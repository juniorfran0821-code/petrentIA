<script>

  import { onMount } from 'svelte';
  import { Router, Route } from 'svelte-routing';
  import { temaOscuro } from './tienda/tienda.js';

  import Navbar     from './componentes/Navbar.svelte';
  import Footer     from './componentes/Footer.svelte';

  import Inicio     from './rutas/Inicio.svelte';
  import Catalogo   from './rutas/Catalogo.svelte';
  import Detalle    from './rutas/Detalle.svelte';
  import Carrito    from './rutas/Carrito.svelte';
  import Confirmado from './rutas/Confirmado.svelte';
  import Login      from './rutas/Login.svelte';
  import Registro   from './rutas/Registro.svelte';
  import Admin       from './rutas/Admin.svelte';
  import MiCuenta    from './rutas/MiCuenta.svelte';
  import AsistenteIA from './rutas/AsistenteIA.svelte';

  onMount(() => {
    temaOscuro.inicializar();
  });
</script>

<Router>
  <Navbar />

  <Route path="/"            component={Inicio} />
  <Route path="/login"       component={Login} />
  <Route path="/registro"    component={Registro} />
  <Route path="/perros"      component={Catalogo} />
  <Route path="/perros/:id"  component={Detalle} />
  <Route path="/carrito"     component={Carrito} />
  <Route path="/confirmado"  component={Confirmado} />
  <Route path="/admin"       component={Admin} />    
  <Route path="/mi-cuenta"   component={MiCuenta} /> 
  <Route path="/ia"          component={AsistenteIA} /> 

  <Footer />
</Router>
