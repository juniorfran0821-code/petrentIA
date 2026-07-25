import { apiFetch } from "./api.js";

export function adaptarPerro(dto) {
  return {
    id: dto.id,
    idPropietario: dto.idPropietario,
    nombre: dto.nombre,
    raza: dto.raza,
    tamaño: dto.tamano,
    precio: Number(dto.precio),
    descripcion: dto.descripcion,
    etiquetas: dto.etiquetas
      ? dto.etiquetas
          .split(",")
          .map((e) => e.trim())
          .filter(Boolean)
      : [],
    imagen: dto.imagen,
    disponible: dto.disponible,
    activo: dto.activo,
    calificacion: Number(dto.calificacion ?? 0),
    reseñas: dto.resenasTotales ?? 0,
  };
}

function adaptarRequest({
  nombre,
  raza,
  tamaño,
  precio,
  descripcion,
  etiquetas,
  imagen,
}) {
  return {
    nombre,
    raza,
    tamano: tamaño,
    precio: Number(precio),
    descripcion,
    etiquetas: Array.isArray(etiquetas) ? etiquetas.join(", ") : etiquetas,
    imagen,
  };
}

export async function listar(page = 0, size = 8, credencial) {
  const data = await apiFetch(`/perros?page=${page}&size=${size}`, {
    credencial,
  });
  return { ...data, content: data.content.map(adaptarPerro) };
}

export async function buscar(texto, page = 0, size = 8, credencial) {
  const data = await apiFetch(
    `/perros/buscar?texto=${encodeURIComponent(texto)}&page=${page}&size=${size}`,
    { credencial },
  );
  return { ...data, content: data.content.map(adaptarPerro) };
}

export async function listarDisponibles(page = 0, size = 50, credencial) {
  const data = await apiFetch(`/perros/disponibles?page=${page}&size=${size}`, {
    credencial,
  });
  return { ...data, content: data.content.map(adaptarPerro) };
}

export async function obtenerPorId(id, credencial) {
  const dto = await apiFetch(`/perros/${id}`, { credencial });
  return adaptarPerro(dto);
}

export async function crear(datosPerro, credencial) {
  const dto = await apiFetch("/perros", {
    method: "POST",
    body: adaptarRequest(datosPerro),
    credencial,
  });
  return adaptarPerro(dto);
}

export async function actualizar(id, datosPerro, credencial) {
  const dto = await apiFetch(`/perros/${id}`, {
    method: "PUT",
    body: adaptarRequest(datosPerro),
    credencial,
  });
  return adaptarPerro(dto);
}

export async function actualizarDisponibilidad(id, disponible, credencial) {
  const dto = await apiFetch(`/perros/${id}/disponibilidad`, {
    method: "PATCH",
    body: { disponible },
    credencial,
  });
  return adaptarPerro(dto);
}

export async function eliminar(id, credencial) {
  return apiFetch(`/perros/${id}`, { method: "DELETE", credencial });
}

export async function reactivar(id, credencial) {
  return apiFetch(`/perros/${id}/reactivar`, { method: "PATCH", credencial });
}
