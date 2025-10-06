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

@Named
@ViewScoped
public class UsuarioController implements Serializable {

    @Inject
    private UsuarioService service;

    // ==== Lo que ya tenías ====
    private List<UsuarioDto> usuarios;
    private UsuarioDto usuario = new UsuarioDto();

    // ==== NUEVO: lo que tu XHTML espera ====
    private String usernameQuery;        // #{usuarioController.usernameQuery}
    private UsuarioDto usuarioEdit;      // #{usuarioController.usuarioEdit}

    // ----------------- CRUD existente -----------------
    public void add(UsuarioDto dto) {
        try {
            service.addUser(dto);
            SuccessMessage("Guardado exitosamente");
        } catch (Exception e) {
            ErrorMessage(e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadUsers() {
        if (usuarios != null) usuarios.clear();
        try {
            usuarios = service.getUsers();
        } catch (Exception e) {
            e.printStackTrace();
            usuarios = new ArrayList<UsuarioDto>();

        }
    }



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





    public void delete(String username) {
        try {
            service.deleteUser(username);
            SuccessMessage("Usuario eliminado correctamente");
            loadUsers();
        } catch (Exception e) {
            ErrorMessage(e.getMessage());
        }
    }

    public void loadUserByUsername() {
        String username = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("username");

        if (username != null && !username.trim().isEmpty()) {
            usuario = service.getUser(username);
        }
    }

    // ----------------- NUEVO: métodos que usa el XHTML -----------------


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


    /** Botón "Limpiar" del formulario (f:ajax) */
    public void limpiarBusqueda() {
        usernameQuery = null;
        usuarioEdit = null;
        InfoMessage("Búsqueda y formulario reiniciados.");
    }

    /** Botón "Guardar cambios" en el panel de edición (f:ajax) */
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


    /** Buscar usuario para ELIMINAR (puedes reutilizar buscarPorUsername si quieres el mismo comportamiento) */
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

    /** Eliminar al usuario actualmente cargado */
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
    public List<UsuarioDto> getUsuarios() { return usuarios; }

    public UsuarioDto getUsuario() { return usuario; }
    public void setUsuario(UsuarioDto usuario) { this.usuario = usuario; }

    // NUEVO
    public String getUsernameQuery() { return usernameQuery; }
    public void setUsernameQuery(String usernameQuery) { this.usernameQuery = usernameQuery; }

    public UsuarioDto getUsuarioEdit() { return usuarioEdit; }
    public void setUsuarioEdit(UsuarioDto usuarioEdit) { this.usuarioEdit = usuarioEdit; }
}
