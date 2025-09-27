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
    private List<UsuarioDto> usuarios;
    private UsuarioDto usuario = new UsuarioDto();



    public void add(UsuarioDto dto) {
        try {
            service.addUser(dto);
            SuccessMessage("Guardado exitosamente");

        } catch (Exception e) {
            ErrorMessage(e.getMessage());
            e.printStackTrace();

        }
    }

    /* ===== READ ALL ===== */
    public void loadUsers() {
        if (usuarios!= null) {
            usuarios.clear();
        }
        try {
            usuarios = service.getUsers();
        } catch (Exception e) {
            e.printStackTrace();
            usuarios = new ArrayList<>();
        }
    }

    //actualizar
    public String update() {
        try {
            service.updateUser(usuario);
            SuccessMessage("Actualizado exitosamente");
            loadUsers();
            // Ojo: la ruta debe coincidir con tu archivo list-usuario.xhtml
            return "/Usuario/list-usuario.xhtml?faces-redirect=true";
        } catch (Exception e) {
            ErrorMessage(e.getMessage());
            e.printStackTrace();
            return null; // si hay error, no navega
        }
    }


    //eliminar
    public void delete(String username) {
        try {
            service.deleteUser(username);
            SuccessMessage("Usuario eliminado correctamente");
            loadUsers();
        } catch (Exception e) {
            ErrorMessage(e.getMessage());
        }
    }


    private void ErrorMessage(String msg) {
        FacesMessage msgF = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null);
        FacesContext.getCurrentInstance().addMessage(null, msgF);
    }

    private void SuccessMessage(String msg) {
        FacesMessage msgF = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null);
        FacesContext.getCurrentInstance().addMessage(null, msgF);
    }


    public List<UsuarioDto> getUsuarios() { return usuarios; }


    public UsuarioDto getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDto usuario) {
        this.usuario = usuario;
    }

    public void loadUserByUsername() {
        String username = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("username");

        if (username != null && !username.isEmpty()) {
            usuario = service.getUser(username); // este método debe estar en tu UsuarioService
        }
    }
}