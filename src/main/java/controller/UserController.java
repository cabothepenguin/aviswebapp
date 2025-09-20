package controller;

import dto.UserDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named
@RequestScoped
public class UserController {
    public String login(UserDto user) {
        if(user.getUsername().equals("admin")&& user.getPassword().equals("1234")) {
            user.setPassword("");
            user.setRole("admin_role");
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("loggedUser", user);
            return "home.xhtml?faces-redirect=true";
        }else{
            FacesMessage msg=new FacesMessage("Usuario y/o contraseña incorrecto");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }
    }
}
