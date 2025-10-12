package controller;

import dto.UsuarioDto;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import service.UsuarioService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para administración de usuarios.
 * <p>
 * Provee operaciones CRUD y utilidades de búsqueda/edición utilizadas
 * por las vistas XHTML (por ejemplo, edición inline por username).
 * </p>
 */
@Named
@ViewScoped
public class UsuarioController implements Serializable {

    @Inject
    private UsuarioService service;

    // ==== Datos principales de la vista ====
    /** Lista cacheada de usuarios para la tabla. */
    private List<UsuarioDto> usuarios;
    /** DTO base utilizado por formularios genéricos. */
    private UsuarioDto usuario = new UsuarioDto();

    // ==== Parámetros esperados por el XHTML ====
    /** Criterio de búsqueda por nombre de usuario. */
    private String usernameQuery;
    /** Usuario actualmente cargado para edición o eliminación. */
    private UsuarioDto usuarioEdit;

    // ----------------- CRUD existente -----------------

    /**
     * Agrega un nuevo usuario usando el DTO proporcionado.
     * @param dto datos del usuario a crear.
     */
    public void add(UsuarioDto dto) {
        try {
            service.addUser(dto);
            SuccessMessage("Guardado exitosamente");
        } catch (Exception e) {
            ErrorMessage(e.getMessage());
            e.printStackTrace();
        }
    }

    /** Carga todos los usuarios del sistema en {@link #usuarios}. */
    public void loadUsers() {
        if (usuarios != null) usuarios.clear();
        try {
            usuarios = service.getUsers();
        } catch (Exception e) {
            e.printStackTrace();
            usuarios = new ArrayList<UsuarioDto>();
        }
    }

    /**
     * Actualiza el usuario mantenido en {@link #usuario}.
     *
     * @return navegación a la lista con redirect; <code>null</code> si hay error.
     */
    public String update() {
        try {
            service.updateUser(usuario);
            SuccessMessage("Actualizado exitosamente");
            loadUsers();
            return "/Usuario/list-usuario.xhtml?faces-redirect=true";
        } catch (Exception e) {
            ErrorMessage(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Elimina un usuario por su username.
     * @param username identificador único del usuario.
     */
    public void delete(String username) {
        try {
            service.deleteUser(username);
            SuccessMessage("Usuario eliminado correctamente");
            loadUsers();
        } catch (Exception e) {
            ErrorMessage(e.getMessage());
        }
    }

    /**
     * Carga un usuario a {@link #usuario} utilizando el parámetro
     * de request <code>username</code> proveniente de la vista.
     */
    public void loadUserByUsername() {
        String username = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("username");

        if (username != null && !username.trim().isEmpty()) {
            usuario = service.getUser(username);
        }
    }

    // ----------------- Métodos usados por el XHTML -----------------

    /**
     * Busca por username (campo {@link #usernameQuery}) y
     * carga el resultado en {@link #usuarioEdit} para edición.
     * @return <code>null</code> para permanecer en la vista.
     */
    public String buscarPorUsername() {
        if (usernameQuery == null || usernameQuery.trim().isEmpty()) {
            usuarioEdit = null;
            ErrorMessage("Ingrese un username");
            return null;
        }

        UsuarioDto dto = service.getUser(usernameQuery);
        if (dto == null) {
            usuarioEdit = null;
            WarningMessage("No se encontró el usuario.");
        } else {
            usuarioEdit = dto;
            InfoMessage("Usuario cargado para edición.");
        }

        return null; // permanecer en la vista
    }

    /** Limpia los campos de búsqueda y el panel de edición. */
    public void limpiarBusqueda() {
        usernameQuery = null;
        usuarioEdit = null;
        InfoMessage("Búsqueda y formulario reiniciados.");
    }

    /** Persiste los cambios realizados sobre {@link #usuarioEdit}. */
    public void guardarCambios() {
        if (usuarioEdit == null) {
            WarningMessage("No hay datos para guardar. Busque un usuario primero.");
            return;
        }
        try {
            service.updatePreservandoPassword(usuarioEdit);
            InfoMessage("Cambios guardados correctamente.");
            // opcional: refrescar la edición desde la BD
            usuarioEdit = service.getUser(usuarioEdit.getUsername());
        } catch (Exception e) {
            ErrorMessage("Error al guardar: " + e.getMessage());
        }
    }

    /**
     * Busca un usuario para eliminarlo (sin realizar aún la eliminación).
     * @return <code>null</code> para permanecer en la vista.
     */
    public String buscarParaEliminar() {
        if (usernameQuery == null || usernameQuery.trim().isEmpty()) {
            usuarioEdit = null;
            ErrorMessage("Ingrese un username");
            return null;
        }
        UsuarioDto dto = service.getUser(usernameQuery);
        if (dto == null) {
            usuarioEdit = null;
            WarningMessage("No se encontró el usuario.");
        } else {
            usuarioEdit = dto;
            InfoMessage("Usuario cargado para eliminación.");
        }
        return null;
    }

    /** Elimina el usuario actualmente cargado en {@link #usuarioEdit}. */
    public void eliminarUsuario() {
        if (usuarioEdit == null) {
            WarningMessage("Busque un usuario primero.");
            return;
        }
        try {
            service.deleteUser(usuarioEdit.getUsername());
            SuccessMessage("Usuario eliminado correctamente.");
            // limpiar el panel
            usuarioEdit = null;
            usernameQuery = null;
        } catch (Exception e) {
            ErrorMessage("No se pudo eliminar: " + e.getMessage());
        }
    }

    // ----------------- Helpers de mensajes -----------------
    private void ErrorMessage(String msg) {
        FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null);
        FacesContext.getCurrentInstance().addMessage(null, m);
    }
    private void WarningMessage(String msg) {
        FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_WARN, msg, null);
        FacesContext.getCurrentInstance().addMessage(null, m);
    }
    private void InfoMessage(String msg) {
        FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null);
        FacesContext.getCurrentInstance().addMessage(null, m);
    }
    private void SuccessMessage(String msg) {
        FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null);
        FacesContext.getCurrentInstance().addMessage(null, m);
    }

    // ----------------- Getters / Setters -----------------
    /** @return lista actual de usuarios para la tabla. */
    public List<UsuarioDto> getUsuarios() { return usuarios; }

    public UsuarioDto getUsuario() { return usuario; }
    public void setUsuario(UsuarioDto usuario) { this.usuario = usuario; }

    public String getUsernameQuery() { return usernameQuery; }
    public void setUsernameQuery(String usernameQuery) { this.usernameQuery = usernameQuery; }

    public UsuarioDto getUsuarioEdit() { return usuarioEdit; }
    public void setUsuarioEdit(UsuarioDto usuarioEdit) { this.usuarioEdit = usuarioEdit; }
}
