package service;

import dto.ProfessorDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import model.Professor;
import repository.ProfessorRepository;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ProfessorService {
    @Inject
    ProfessorRepository repository;
    public void addProfessor(ProfessorDto professorDto){
        try{

        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public List<ProfessorDto> getProfessors(){
        List<ProfessorDto> list= new ArrayList<>();
        repository.getProfessor().forEach(obj->{
           list.add(toDto((Professor)obj));
        });
        return list;
    }

    private ProfessorDto toDto(Professor model){
        ProfessorDto professorDto = new ProfessorDto();
        professorDto.setName(model.getName());
        professorDto.setLastName(model.getLastName());
        professorDto.setEmail(model.getEmail());
        if (model.getImage() != null) {
            professorDto.setImage(model.getImage());
            professorDto.setImageName(model.getImageName());
        }
        return professorDto;
    }

    public ProfessorDto getProfessor(Integer id){
        return toDto(repository.getProfessor(id));
    }
}
