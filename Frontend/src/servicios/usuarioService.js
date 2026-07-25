import { apiFetch } from "./api.js";

export async function listar(page = 0, size = 50, credencial) {
  return apiFetch(`/usuarios?page=${page}&size=${size}`, { credencial });
}

export async function buscar(texto, page = 0, size = 50, credencial) {
  return apiFetch(
    `/usuarios/buscar?texto=${encodeURIComponent(texto)}&page=${page}&size=${size}`,
    { credencial },
  );
}

export async function actualizar(id, { nombre, correo }, credencial) {
  return apiFetch(`/usuarios/${id}`, {
    method: "PUT",
    body: { nombre, correo },
    credencial,
  });
}

export async function actualizarRol(id, rol, credencial) {
  return apiFetch(`/usuarios/${id}/rol`, {
    method: "PATCH",
    body: { rol },
    credencial,
  });
}

export async function eliminar(id, credencial) {
  return apiFetch(`/usuarios/${id}`, { method: "DELETE", credencial });
}

export async function reactivar(id, credencial) {
  return apiFetch(`/usuarios/${id}/reactivar`, { method: "PATCH", credencial });
}
