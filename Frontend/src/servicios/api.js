import { API_URL } from "../config.js";

export function headerBasic(credencial) {
  return credencial ? { Authorization: `Basic ${credencial}` } : {};
}

export function generarCredencial(correo, password) {
  return btoa(`${correo}:${password}`);
}

async function procesarRespuesta(response) {
  if (response.status === 204) return null;

  let cuerpo = null;
  const texto = await response.text();
  if (texto) {
    try {
      cuerpo = JSON.parse(texto);
    } catch {
      cuerpo = null;
    }
  }

  if (!response.ok) {
    const mensaje = cuerpo?.message || `Error ${response.status}`;
    const error = new Error(mensaje);
    error.status = response.status;
    throw error;
  }

  return cuerpo;
}

export async function apiFetch(
  ruta,
  { method = "GET", body, credencial } = {},
) {
  const headers = {
    "Content-Type": "application/json",
    ...headerBasic(credencial),
  };

  let response;
  try {
    response = await fetch(`${API_URL}${ruta}`, {
      method,
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    });
  } catch {
    const error = new Error(
      "No se pudo conectar con el servidor. Verifica que el backend esté corriendo en el puerto 8080.",
    );
    error.status = 0;
    throw error;
  }

  return procesarRespuesta(response);
}
