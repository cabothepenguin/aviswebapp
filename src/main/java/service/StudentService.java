package service;

import dto.StudentDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import model.Student;
import repository.StudentRepository;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class StudentService {
    @Inject
    private StudentRepository repository;

    public void addStudent(StudentDto dto){
       try{
           repository.addStudent(dto);
       } catch (Exception e) {
           throw new RuntimeException("No se puede agregar el registro",e);
       }
    }

    public List<StudentDto> getStudents() {
        List<StudentDto> list=new ArrayList<>();
        repository.getStudents().forEach(student -> {
            list.add(this.toDto((Student) student));
        });
        return list;
    }

    private StudentDto toDto(Student entity){
        StudentDto dto = new StudentDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        return dto;
    }
}
