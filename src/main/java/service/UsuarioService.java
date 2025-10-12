package service;

import dto.UsuarioDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import model.Usuario;
import repository.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de usuarios: orquesta operaciones CRUD y mapping DTO &lt;-> entidad.
 * <p>Contiene utilidades simples de validación de credenciales.</p>
 */
@Named
@RequestScoped
public class UsuarioService {

    @Inject
    private UsuarioRepository repository;

    /** Crea un usuario delegando al repositorio. */
    public void addUser(UsuarioDto dto) {
        try {
            repository.addUser(dto);
        } catch (Exception e) {
            throw new RuntimeException("No se puede agregar el usuario", e);
        }
    }

    /** Lista usuarios como DTOs. */
    public List<UsuarioDto> getUsers() {
        List<UsuarioDto> out = new ArrayList<>();
        repository.getUsers().forEach(obj -> {
            out.add(toDto((Usuario) obj));
        });
        return out;
    }

    /** Obtiene un usuario por username como DTO. */
    public UsuarioDto getUser(String username) {
        Usuario u = repository.getUser(username);
        return (u == null) ? null : toDto(u);
    }

    /** Actualiza un usuario desde DTO. */
    public void updateUser(UsuarioDto dto) {
        try {
            repository.updateUser(dto);
        } catch (Exception e) {
            throw new RuntimeException("No se puede actualizar el usuario", e);
        }
    }

    /** Elimina un usuario por su username. */
    public void deleteUser(String username) {
        try {
            repository.deleteUser(username);
        } catch (Exception e) {
            throw new RuntimeException("No se puede eliminar el usuario", e);
        }
    }

    /**
     * Valida credenciales en texto plano (comparación directa).
     * <p><b>Nota:</b> para producción, usar hash seguro de contraseñas.</p>
     */
    public boolean validateCredentials(String username, String plainPassword) {
        Usuario u = repository.getUser(username);
        if (u == null) return false;
        return plainPassword != null && plainPassword.equals(u.getPassword());
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

    /** Mapea DTO a entidad (no usado actualmente). */
    @SuppressWarnings("unused")
    private Usuario toEntity(UsuarioDto dto) {
        Usuario u = new Usuario();
        u.setUsername(dto.getUsername());
        u.setPassword(dto.getPassword());
        u.setNombre(dto.getNombre());
        u.setApellido(dto.getApellido());
        u.setCorreo(dto.getCorreo());
        return u;
    }

    /**
     * Obtiene un DTO por username y password válida.
     * @return DTO si coincide, {@code null} en caso contrario.
     */
    public UsuarioDto getByUsernameAndPassword(String username, String plainPassword) {
        Usuario u = repository.getUser(username);
        if (u == null) return null;
        if (plainPassword != null && plainPassword.equals(u.getPassword())) {
            return toDto(u);
        }
        return null;
    }

    /**
     * Actualiza preservando password si viene vacío/nulo en el DTO.
     * <p>Recupera el actual y reusa {@link #updateUser(UsuarioDto)}.</p>
     */
    public void updatePreservandoPassword(UsuarioDto dto) {
        UsuarioDto actual = getUser(dto.getUsername());
        if (actual == null) {
            throw new RuntimeException("El usuario no existe: " + dto.getUsername());
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            dto.setPassword(actual.getPassword());
        }
        updateUser(dto);
    }
}
