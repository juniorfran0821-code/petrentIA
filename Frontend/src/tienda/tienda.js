import { writable, derived } from "svelte/store";
import * as authService from "../servicios/authService.js";
import * as perroService from "../servicios/perroService.js";

function crearStoreTema() {
  const guardado = localStorage.getItem("petrent_tema") === "oscuro";
  const { subscribe, set, update } = writable(guardado);

  return {
    subscribe,

    alternar() {
      update((oscuro) => {
        const nuevo = !oscuro;
        localStorage.setItem("petrent_tema", nuevo ? "oscuro" : "claro");
        // data-tema es leído por las variables CSS en app.css para cambiar los colores
        document.documentElement.setAttribute(
          "data-tema",
          nuevo ? "oscuro" : "claro",
        );
        return nuevo;
      });
    },

    inicializar() {
      update((oscuro) => {
        document.documentElement.setAttribute(
          "data-tema",
          oscuro ? "oscuro" : "claro",
        );
        return oscuro;
      });
    },
  };
}

export const temaOscuro = crearStoreTema();

function cargarSesion() {
  try {
    const guardado = localStorage.getItem("petrent_sesion");
    return guardado ? JSON.parse(guardado) : null;
  } catch {
    return null;
  }
}

export const sesion = writable(cargarSesion());

sesion.subscribe((usuario) => {
  try {
    if (usuario)
      localStorage.setItem("petrent_sesion", JSON.stringify(usuario));
    else localStorage.removeItem("petrent_sesion");
  } catch {}
});

export const estaAutenticado = derived(sesion, ($s) => $s !== null);

export const esAdmin = derived(sesion, ($s) => $s?.rol === "admin");

export async function iniciarSesion(correo, password) {
  try {
    const usuario = await authService.login(correo, password);
    sesion.set(usuario);
    return { ok: true, usuario };
  } catch (error) {
    const mensaje =
      error.status === 401
        ? "Correo o contraseña incorrectos."
        : error.message || "No se pudo iniciar sesión.";
    return { ok: false, error: mensaje };
  }
}

export function cerrarSesion() {
  sesion.set(null);
  carrito.set([]);
}

export const catalogoPerros = writable([]);

export async function cargarCatalogo(page = 0, size = 100) {
  const data = await perroService.listar(page, size);
  catalogoPerros.set(data.content);
  return data;
}

function cargarCarrito() {
  try {
    const guardado = localStorage.getItem("petrent_carrito");
    return guardado ? JSON.parse(guardado) : [];
  } catch {
    return [];
  }
}

export const carrito = writable(cargarCarrito());

carrito.subscribe((items) => {
  try {
    localStorage.setItem("petrent_carrito", JSON.stringify(items));
  } catch {}
});

export function agregarAlCarrito(perro, horas) {
  carrito.update((items) => {
    const idx = items.findIndex((i) => i.id === perro.id);
    if (idx !== -1) {
      // Perro ya en el carrito: sumar horas
      const copia = [...items];
      copia[idx] = { ...copia[idx], horas: copia[idx].horas + horas };
      return copia;
    }
    return [...items, { ...perro, horas }];
  });
}

export function quitarDelCarrito(id) {
  carrito.update((items) => items.filter((i) => i.id !== id));
}

export const totalCarrito = derived(carrito, ($c) =>
  $c.reduce((s, i) => s + i.precio * i.horas, 0),
);

export const cantidadCarrito = derived(carrito, ($c) => $c.length);
