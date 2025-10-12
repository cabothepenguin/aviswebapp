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

/**
 * Controlador responsable de la autenticación de usuarios.
 * <p>
 * Mantiene el estado de sesión del usuario autenticado, expone el flujo
 * de inicio/cierre de sesión y restringe el acceso a vistas protegidas.
 * </p>
 */
@Named("authController")
@SessionScoped
public class AuthController implements Serializable {

    private String username;
    private String password;
    private UsuarioDto currentUser;

    @Inject
    private UsuarioService service;

    /**
     * Intenta autenticar al usuario con las credenciales proporcionadas.
     *
     * @return navegación a <code>/home.xhtml</code> con redirect en caso de éxito;
     *         <code>null</code> si las credenciales no son válidas (se añade mensaje Faces).
     */
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

    /**
     * Requiere que exista un usuario autenticado para continuar.
     * <p>Si no hay sesión activa, redirige a <code>/index.xhtml</code>.</p>
     */
    public void requireLogin() {
        if (currentUser == null) {
            try {
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/index.xhtml");
            } catch (IOException ignored) { }
        }
    }

    /**
     * Cierra la sesión actual e invalida el contexto.
     *
     * @return navegación a <code>/index.xhtml</code> con redirect.
     */
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/index.xhtml?faces-redirect=true";
    }

    // ---------------- Getters y setters ----------------

    /** @return nombre de usuario ingresado en el formulario. */
    public String getUsername() { return username; }
    /** @param username nombre de usuario a establecer. */
    public void setUsername(String username) { this.username = username; }

    /** @return contraseña ingresada en el formulario. */
    public String getPassword() { return password; }
    /** @param password contraseña a establecer. */
    public void setPassword(String password) { this.password = password; }

    /** @return DTO del usuario actualmente autenticado; <code>null</code> si no hay sesión. */
    public UsuarioDto getCurrentUser() { return currentUser; }
}
