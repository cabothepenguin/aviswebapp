package repository;

import dto.ProfessorDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import model.Professor;

import java.util.List;

@ApplicationScoped
public class ProfessorRepository {
    @PersistenceContext(unitName = "unaweb-pu")
    private EntityManager em;
    public void addProfessor(ProfessorDto professorDto){
        try {
            em.getTransaction().begin();
            String sql = "insert into profesores (id,nombre,apellidos,correo,imagen,nombre_imagen) values (?,?,?,?,?,?,?)";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1,professorDto.getId());
            query.setParameter(2,professorDto.getName());
            query.setParameter(3,professorDto.getLastName());
            query.setParameter(4,professorDto.getEmail());
            query.setParameter(5,professorDto.getImage());
            query.setParameter(6,professorDto.getImageName());
            query.executeUpdate();
            em.getTransaction().commit();
        }catch(Exception e){
            em.getTransaction().rollback();
            throw e;
        }
    }

    public List getProfessor(){
        return em.createNativeQuery("select * from profesores", Professor.class).getResultList();
    }

    public Professor getProfessor(Integer id){
        return (Professor) em.createNativeQuery("select * from profesores Where id=?", Professor.class)
                .setParameter(1, id).getSingleResult();
    }
}
