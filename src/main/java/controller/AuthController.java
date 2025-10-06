package controller;

import dto.UsuarioDto;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import service.UsuarioService;

import java.io.IOException;
import java.io.Serializable;
@Named("authController")
@SessionScoped
public class AuthController implements Serializable {

    private String username;
    private String password;
    private UsuarioDto currentUser;

    @Inject
    private UsuarioService service;

    public String login() {
        UsuarioDto dto = service.getByUsernameAndPassword(username, password);
        if (dto != null) {
            currentUser = dto;
            return "/home.xhtml?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Usuario o contraseña incorrectos", null));
            return null;
        }
    }

    public void requireLogin() {
        if (currentUser == null) {
            try {
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/index.xhtml");
            } catch (IOException ignored) { }
        }
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/index.xhtml?faces-redirect=true";
    }

    // Getters y setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public UsuarioDto getCurrentUser() { return currentUser; }
}
