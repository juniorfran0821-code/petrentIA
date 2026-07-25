package com.petrent.api.service;

import com.petrent.api.dto.*;
import com.petrent.api.entity.Usuario;
import com.petrent.api.exception.BusinessRuleException;
import com.petrent.api.exception.DuplicateResourceException;
import com.petrent.api.exception.ResourceNotFoundException;
import com.petrent.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;  

    
    @Transactional
    public UsuarioResponse registrar(UsuarioRegisterRequest request) {
        log.info("Intentando registrar nuevo usuario con correo '{}'", request.getCorreo());

        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new DuplicateResourceException(
                    "Ya existe un usuario registrado con el correo: " + request.getCorreo());
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .correo(request.getCorreo())
                .password(passwordEncoder.encode(request.getPassword())) // BCrypt hash
                .rol("usuario")
                .activo(true)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario registrado correctamente con id={}", guardado.getId());
        return toResponse(guardado);
    }

   
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerActual(Authentication authentication) {
        log.debug("Obteniendo perfil del usuario autenticado '{}'", authentication.getName());
        return toResponse(buscarPorCorreoOFallar(authentication.getName()));
    }

   
    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listar(Pageable pageable) {
        log.debug("Listando usuarios — página {}", pageable.getPageNumber());
        return usuarioRepository.findAll(pageable).map(this::toResponse);
    }

   
    @Transactional(readOnly = true)
    public Page<UsuarioResponse> buscarPorTexto(String texto, Pageable pageable) {
        log.debug("Buscando usuarios con texto='{}'", texto);
        return usuarioRepository
                .findByNombreContainingIgnoreCaseOrCorreoContainingIgnoreCase(texto, texto, pageable)
                .map(this::toResponse);
    }

   
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorId(Long id) {
        log.debug("Consultando usuario id={}", id);
        return toResponse(buscarOFallar(id));
    }

   
    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request, Authentication authentication) {
        log.info("Actualizando usuario id={} por '{}'", id, authentication.getName());
        Usuario usuario = buscarOFallar(id);
        Usuario actual = buscarPorCorreoOFallar(authentication.getName());

        boolean esAdmin = "admin".equalsIgnoreCase(actual.getRol());
        if (!esAdmin && !actual.getId().equals(id)) {
            throw new BusinessRuleException("No puede modificar los datos de otro usuario");
        }

        if (!usuario.getCorreo().equalsIgnoreCase(request.getCorreo())
                && usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new DuplicateResourceException(
                    "Ya existe un usuario registrado con el correo: " + request.getCorreo());
        }

        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        return toResponse(usuario);
    }


    @Transactional
    public void cambiarPassword(Long id, CambiarPasswordRequest request, Authentication authentication) {
        log.info("Cambio de contraseña para usuario id={}", id);
        Usuario usuario = buscarOFallar(id);
        Usuario actual = buscarPorCorreoOFallar(authentication.getName());

        if (!actual.getId().equals(id)) {
            throw new BusinessRuleException("No puede cambiar la contraseña de otro usuario");
        }

        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())) {
            throw new BusinessRuleException("La contraseña actual no es correcta");
        }

        usuario.setPassword(passwordEncoder.encode(request.getPasswordNueva()));
        log.info("Contraseña actualizada para usuario id={}", id);
    }

    
    @Transactional
    public UsuarioResponse actualizarRol(Long id, UsuarioRolRequest request) {
        log.info("Cambiando rol del usuario id={} a '{}'", id, request.getRol());
        Usuario usuario = buscarOFallar(id);
        usuario.setRol(request.getRol());
        return toResponse(usuario);
    }

    
    @Transactional
    public void eliminar(Long id) {
        log.info("Dando de baja usuario id={} (eliminación lógica)", id);
        Usuario usuario = buscarOFallar(id);
        usuario.setActivo(false);
    }

    
    @Transactional
    public void reactivar(Long id) {
        log.info("Reactivando usuario id={}", id);
        Usuario usuario = buscarOFallar(id);
        usuario.setActivo(true);
    }


    private Usuario buscarOFallar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un usuario con id: " + id));
    }

    private Usuario buscarPorCorreoOFallar(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
    }

    private UsuarioResponse toResponse(Usuario u) {
        return UsuarioResponse.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .correo(u.getCorreo())
                .rol(u.getRol())
                .activo(u.getActivo())
                .fechaCreacion(u.getFechaCreacion())
                .fechaActualizacion(u.getFechaActualizacion())
                .build();
    }
}
