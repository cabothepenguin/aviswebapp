package service;

import dto.UsuarioDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import jakarta.inject.Named;
import model.Usuario;
import repository.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;
@Named
@RequestScoped
public class UsuarioService {

    @Inject
    private UsuarioRepository repository;

    /* ===== CREATE ===== */
    public void addUser(UsuarioDto dto) {
        try {
            repository.addUser(dto);
        } catch (Exception e) {
            throw new RuntimeException("No se puede agregar el usuario", e);
        }
    }

    /* ===== READ ALL ===== */
    public List<UsuarioDto> getUsers() {
        List<UsuarioDto> out = new ArrayList<>();
        repository.getUsers().forEach(obj -> {          // repo devuelve lista cruda, igual que tu StudentService
            out.add(toDto((Usuario) obj));
        });
        return out;
    }

    /* ===== READ ONE ===== */
    public UsuarioDto getUser(String username) {
        Usuario u = repository.getUser(username);
        return (u == null) ? null : toDto(u);
    }

    /* ===== UPDATE ===== */

    public void updateUser(UsuarioDto dto) {
        try {
            repository.updateUser(dto);                  // o repository.update(toEntity(dto));
        } catch (Exception e) {
            throw new RuntimeException("No se puede actualizar el usuario", e);
        }
    }

    /* ===== DELETE ===== */
    public void deleteUser(String username) {
        try {
            repository.deleteUser(username);
        } catch (Exception e) {
            throw new RuntimeException("No se puede eliminar el usuario", e);
        }
    }

    /* ===== LOGIN / VALIDACIÓN OPCIONAL ===== */
    public boolean validateCredentials(String username, String plainPassword) {
        Usuario u = repository.getUser(username);
        if (u == null) return false;
        return plainPassword != null && plainPassword.equals(u.getPassword());
    }


    private UsuarioDto toDto(Usuario u) {
        UsuarioDto dto = new UsuarioDto();
        dto.setUsername(u.getUsername());
        dto.setPassword(u.getPassword());

        dto.setNombre(u.getNombre());
        dto.setApellido(u.getApellido());
        dto.setCorreo(u.getCorreo());
        return dto;
    }

    @SuppressWarnings("unused")
    private Usuario toEntity(UsuarioDto dto) {
        Usuario u = new Usuario();
        u.setUsername(dto.getUsername());
        u.setPassword(dto.getPassword()); // Ideal: aquí poner el hash
        u.setNombre(dto.getNombre());
        u.setApellido(dto.getApellido());
        u.setCorreo(dto.getCorreo());
        return u;
    }


}
