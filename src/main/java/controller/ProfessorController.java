package controller;

import dto.ProfessorDto;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import service.ProfessorService;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.List;

@Named("professorBean")
@SessionScoped
public class ProfessorController implements Serializable {
    //inyectar el service
    @Inject
    ProfessorService service;
    private Part uploadedFile;
    private String fileName;
    private byte[] file;
    private List<ProfessorDto> professors;
    //carga el archivo a los elementos temporales (fiename y file )
    public void upload(){
      this.fileName= Paths.get(uploadedFile.getName()).getFileName().toString();
      try{
         InputStream in = uploadedFile.getInputStream();
         file= in.readAllBytes();
      }catch(IOException e){
          e.printStackTrace();
          //metodo error
      }
    }

    public void add(ProfessorDto professorDto){
        if(this.file!=null){
            professorDto.setImageName(this.fileName);
            professorDto.setImage(this.file);
        }

        try {
            service.addProfessor(professorDto);
        }catch(Exception e){
            e.printStackTrace();
            //enviar
        }
    }

    public void loadProfessors(){
        if(professors!=null){
            this.professors.clear();
        }
        try {
            this.professors=service.getProfessors();
        }catch(Exception e){
            e.printStackTrace();
            //presentar el error al usuario
        }
    }

    public Part getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(Part uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public List<ProfessorDto> getProfessors() {
        return professors;
    }
}
