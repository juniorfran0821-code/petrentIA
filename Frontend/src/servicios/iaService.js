import { apiFetch } from "./api.js";

export async function consultarIA(consulta) {
  return apiFetch("/ia/consulta", {
    method: "POST",
    body: { consulta },
  });
}
