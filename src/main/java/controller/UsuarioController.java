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

    private UsuarioDto newUser = new UsuarioDto();
    private List<UsuarioDto> usuarios;



    public void add() {
        try {
            service.addUser(newUser);
            SuccessMessage("Guardado exitosamente");
            loadUsers(); // refresca la lista
            newUser = new UsuarioDto(); // limpia formulario
        } catch (Exception e) {
            ErrorMessage(e.getMessage());
        }
    }

    /* ===== READ ALL ===== */
    public void loadUsers() {
        if (usuarios != null) {
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
    public void update(UsuarioDto dto) {
        try {
            service.updateUser(dto);
            SuccessMessage("Usuario actualizado correctamente");
            loadUsers();
        } catch (Exception e) {
            ErrorMessage(e.getMessage());
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

    public UsuarioDto getNewUser() { return newUser; }
    public void setNewUser(UsuarioDto newUser) { this.newUser = newUser; }

    public List<UsuarioDto> getUsuarios() { return usuarios; }

}