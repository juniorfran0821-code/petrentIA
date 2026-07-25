import { apiFetch, generarCredencial } from "./api.js";

export async function login(correo, password) {
  const credencial = generarCredencial(correo, password);
  const usuario = await apiFetch("/auth/login", { method: "POST", credencial });
  return { ...usuario, credencial };
}

export async function registrar(nombre, correo, password) {
  return apiFetch("/auth/registro", {
    method: "POST",
    body: { nombre, correo, password },
  });
}

export async function obtenerActual(credencial) {
  return apiFetch("/auth/me", { credencial });
}
