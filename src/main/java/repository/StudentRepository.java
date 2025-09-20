package repository;

import dto.StudentDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import model.Student;

import java.util.List;


@ApplicationScoped
public class StudentRepository {
    @PersistenceContext(unitName = "unaweb-pu")
    private EntityManager em;

    public void addStudent(StudentDto student) {
        try{
            em.getTransaction().begin();
            String sql = "INSERT INTO estudiantes (id, nombre, apellidos, telefono, correo) VALUES (?, ?, ?, ?, ?)";
            Query query=em.createNativeQuery(sql);
            query.setParameter(1,student.getId());
            query.setParameter(2,student.getName());
            query.setParameter(3,student.getLastName());
            query.setParameter(4,student.getPhone());
            query.setParameter(5,student.getEmail());
            query.executeUpdate();
            em.getTransaction().commit();
        }catch(Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
            throw  new RuntimeException(e);
        }

    }
    public List getStudents(){
        return em.createNativeQuery("Select * from estudiantes", Student.class).getResultList();
    }
}
