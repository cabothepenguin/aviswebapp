package service;

import dto.UsuarioDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import model.Usuario;
import repository.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Servicio de usuarios: orquesta operaciones CRUD y mapping DTO <-> entidad.
 * Incluye validaciones y normalización.
 */
@Named
@RequestScoped
public class UsuarioService {

    @Inject
    private UsuarioRepository repository;

    /** Crea un usuario con validaciones básicas. */
    public void addUser(UsuarioDto dto) {
        try {
            requireDto(dto);
            normalize(dto);

            if (isBlank(dto.getUsername())) throw new IllegalArgumentException("El username es obligatorio");
            if (isBlank(dto.getPassword())) throw new IllegalArgumentException("La contraseña es obligatoria");
            if (repository.existsByUsername(dto.getUsername())) {
                throw new IllegalStateException("Ya existe un usuario con username: " + dto.getUsername());
            }
            repository.addUser(dto);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("No se puede agregar el usuario", e);
        }
    }

    /** Lista usuarios como DTOs. */
    public List<UsuarioDto> getUsers() {
        List<UsuarioDto> out = new ArrayList<>();
        repository.getUsers().forEach(obj -> out.add(toDto((Usuario) obj)));
        return out;
    }

    /** Obtiene un usuario por username como DTO. */
    public UsuarioDto getUser(String username) {
        if (isBlank(username)) return null;
        Usuario u = repository.getUser(username.trim());
        return (u == null) ? null : toDto(u);
    }

    /** Actualiza un usuario desde DTO (requiere existencia previa). */
    public void updateUser(UsuarioDto dto) {
        try {
            requireDto(dto);
            normalize(dto);
            if (isBlank(dto.getUsername()))
                throw new IllegalArgumentException("El username es obligatorio para actualizar");
            if (!repository.existsByUsername(dto.getUsername()))
                throw new IllegalStateException("No existe el usuario: " + dto.getUsername());
            repository.updateUser(dto);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("No se puede actualizar el usuario", e);
        }
    }

    /** Elimina un usuario por su username. */
    public void deleteUser(String username) {
        try {
            if (isBlank(username)) throw new IllegalArgumentException("El username es obligatorio para eliminar");
            username = username.trim();
            repository.deleteUser(username);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("No se puede eliminar el usuario", e);
        }
    }

    /** Validación simple (plaintext). */
    public boolean validateCredentials(String username, String plainPassword) {
        if (isBlank(username) || isBlank(plainPassword)) return false;
        username = username.trim();
        plainPassword = plainPassword.trim();
        Usuario u = repository.findByUsernameAndPassword(username, plainPassword);
        return u != null;
    }

    /** Login que devuelve DTO si matchea. */
    public UsuarioDto getByUsernameAndPassword(String username, String plainPassword) {
        if (isBlank(username) || isBlank(plainPassword)) return null;
        username = username.trim();
        plainPassword = plainPassword.trim();
        Usuario u = repository.findByUsernameAndPassword(username, plainPassword);
        return (u == null) ? null : toDto(u);
    }

    /** Actualiza preservando password si viene vacío/nulo. */
    public void updatePreservandoPassword(UsuarioDto dto) {
        requireDto(dto);
        if (isBlank(dto.getUsername())) {
            throw new IllegalArgumentException("El username es obligatorio para actualizar");
        }
        dto.setUsername(dto.getUsername().trim());

        UsuarioDto actual = getUser(dto.getUsername());
        if (actual == null) {
            throw new RuntimeException("El usuario no existe: " + dto.getUsername());
        }
        if (isBlank(dto.getPassword())) {
            dto.setPassword(actual.getPassword());
        } else {
            dto.setPassword(dto.getPassword().trim());
        }

        normalize(dto);
        updateUser(dto);
    }

    // ---- helpers ----
    private void requireDto(UsuarioDto dto) {
        Objects.requireNonNull(dto, "El DTO de usuario no puede ser nulo"); }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty(); }

    private void normalize(UsuarioDto dto) {
        if (dto.getUsername() != null) dto.setUsername(dto.getUsername().trim());
        if (dto.getPassword() != null) dto.setPassword(dto.getPassword().trim());
        if (dto.getNombre() != null)   dto.setNombre(dto.getNombre().trim());
        if (dto.getApellido() != null) dto.setApellido(dto.getApellido().trim());
        if (dto.getCorreo() != null)   dto.setCorreo(dto.getCorreo().trim());
    }

    /** Mapea entidad a DTO. */
    private UsuarioDto toDto(Usuario u) {
        UsuarioDto dto = new UsuarioDto();
        dto.setUsername(u.getUsername());
        dto.setPassword(u.getPassword());
        dto.setNombre(u.getNombre());
        dto.setApellido(u.getApellido());
        dto.setCorreo(u.getCorreo());
        return dto;
    }
}
