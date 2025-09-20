package controller;

import dto.StudentDto;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import service.StudentService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped

public class StudentController implements Serializable {
    @Inject
    StudentService service;

    private List<StudentDto> students;

    public void add(StudentDto dto){
        try{
            service.addStudent(dto);
            SuccessMessage("Guardado exitosamente");
        }catch(Exception e){
            ErrorMessage(e.getMessage());
        }
    }

    public void loadStudents(){
        if(this.students!=null){
            this.students.clear();
        }
        try{
            this.students=service.getStudents();
        }catch(Exception e){
            e.printStackTrace();
            this.students=new ArrayList<>();
        }
    }

    private void ErrorMessage(String msg){
        FacesMessage msgF = new FacesMessage(FacesMessage.SEVERITY_ERROR,msg,null);
        FacesContext.getCurrentInstance().addMessage(null, msgF);
    }
    private void SuccessMessage(String msg){
        FacesMessage msgF=new FacesMessage(FacesMessage.SEVERITY_INFO,msg,null);
        FacesContext.getCurrentInstance().addMessage(null, msgF);
    }

    public List<StudentDto> getStudents() {
        return students;
    }
}
