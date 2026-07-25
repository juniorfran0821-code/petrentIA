package com.petrent.api.service;

import com.petrent.api.dto.PerroDisponibilidadRequest;
import com.petrent.api.dto.PerroRequest;
import com.petrent.api.dto.PerroResponse;
import com.petrent.api.entity.Perro;
import com.petrent.api.entity.Usuario;
import com.petrent.api.exception.ResourceNotFoundException;
import com.petrent.api.repository.PerroRepository;
import com.petrent.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class PerroService {

    private final PerroRepository perroRepository;
    private final UsuarioRepository usuarioRepository;

   
    @Transactional
    public PerroResponse registrar(PerroRequest request, Authentication authentication) {
        log.info("Registrando nuevo perro '{}' por usuario '{}'", request.getNombre(), authentication.getName());

        Usuario propietario = usuarioRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));

        Perro perro = Perro.builder()
                .propietario(propietario)
                .nombre(request.getNombre())
                .raza(request.getRaza())
                .tamano(request.getTamano())
                .precio(request.getPrecio())
                .descripcion(request.getDescripcion())
                .etiquetas(request.getEtiquetas())
                .imagen(request.getImagen())
                .disponible(true)
                .activo(true)
                .build();

        Perro guardado = perroRepository.save(perro);
        log.info("Perro registrado con id={}", guardado.getId());
        return toResponse(guardado);
    }

    
    @Transactional(readOnly = true)
    public Page<PerroResponse> listar(Pageable pageable) {
        log.debug("Listando perros activos — página {}", pageable.getPageNumber());
        return perroRepository.findByActivoTrue(pageable).map(this::toResponse);
    }

   
    @Transactional(readOnly = true)
    public PerroResponse obtenerPorId(Long id) {
        log.debug("Consultando perro id={}", id);
        return toResponse(buscarOFallar(id));
    }

    
    @Transactional(readOnly = true)
    public Page<PerroResponse> buscarPorTexto(String texto, Pageable pageable) {
        log.debug("Buscando perros con texto='{}' — página {}", texto, pageable.getPageNumber());
        return perroRepository
                .findByActivoTrueAndNombreContainingIgnoreCaseOrActivoTrueAndRazaContainingIgnoreCase(
                        texto, texto, pageable)
                .map(this::toResponse);
    }

    
    @Transactional(readOnly = true)
    public Page<PerroResponse> listarDisponibles(Pageable pageable) {
        log.debug("Listando perros disponibles — página {}", pageable.getPageNumber());
        return perroRepository.findByActivoTrueAndDisponibleTrue(pageable).map(this::toResponse);
    }

    
    @Transactional
    public PerroResponse actualizar(Long id, PerroRequest request) {
        log.info("Actualizando datos del perro id={}", id);
        Perro perro = buscarOFallar(id);

        perro.setNombre(request.getNombre());
        perro.setRaza(request.getRaza());
        perro.setTamano(request.getTamano());
        perro.setPrecio(request.getPrecio());
        perro.setDescripcion(request.getDescripcion());
        perro.setEtiquetas(request.getEtiquetas());
        perro.setImagen(request.getImagen());

        return toResponse(perro);
    }

    
    @Transactional
    public PerroResponse actualizarDisponibilidad(Long id, PerroDisponibilidadRequest request) {
        log.info("Cambiando disponibilidad del perro id={} a {}", id, request.getDisponible());
        Perro perro = buscarOFallar(id);
        perro.setDisponible(request.getDisponible());
        return toResponse(perro);
    }

   
    @Transactional
    public void eliminar(Long id) {
        log.info("Dando de baja perro id={} (eliminación lógica)", id);
        Perro perro = buscarOFallar(id);
        perro.setActivo(false);
        perro.setDisponible(false);
    }

    
    @Transactional
    public void reactivar(Long id) {
        log.info("Reactivando perro id={}", id);
        Perro perro = buscarOFallar(id);
        perro.setActivo(true);
    }


    private Perro buscarOFallar(Long id) {
        return perroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un perro con id: " + id));
    }

    private PerroResponse toResponse(Perro p) {
        return PerroResponse.builder()
                .id(p.getId())
                .idPropietario(p.getPropietario().getId())
                .nombrePropietario(p.getPropietario().getNombre())
                .nombre(p.getNombre())
                .raza(p.getRaza())
                .tamano(p.getTamano())
                .precio(p.getPrecio())
                .descripcion(p.getDescripcion())
                .etiquetas(p.getEtiquetas())
                .imagen(p.getImagen())
                .disponible(p.getDisponible())
                .activo(p.getActivo())
                .calificacion(p.getCalificacion())
                .resenasTotales(p.getResenasTotales())
                .fechaCreacion(p.getFechaCreacion())
                .fechaActualizacion(p.getFechaActualizacion())
                .build();
    }
}
