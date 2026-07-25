import { apiFetch } from "./api.js";


export async function crear({ idPerro, horas, metodoPago }, credencial) {
  return apiFetch("/reservas", {
    method: "POST",
    body: { idPerro, horas, metodoPago },
    credencial,
  });
}


export async function listarPorUsuario(idUsuario, page = 0, size = 20, credencial) {
  return apiFetch(`/reservas/usuario/${idUsuario}?page=${page}&size=${size}`, { credencial });
}


export async function listarTodas(page = 0, size = 10, credencial) {
  return apiFetch(`/reservas?page=${page}&size=${size}`, { credencial });
}


export async function actualizarEstado(id, estado, credencial) {
  return apiFetch(`/reservas/${id}/estado`, {
    method: "PATCH",
    body: { estado },
    credencial,
  });
}
