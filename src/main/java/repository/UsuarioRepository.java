package repository;

import dto.UsuarioDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import model.Usuario;

import java.util.List;

@ApplicationScoped
public class UsuarioRepository {
    @PersistenceContext(unitName = "avisrent-pu")
    private EntityManager em;

    public void addUser(UsuarioDto usuario ) {
        try {
            em.getTransaction().begin();
            String sql="insert into administracion_usuarios (username,password,nombre,apellido,correo) values (?,?,?,?,?)";
            Query query=em.createNativeQuery(sql);
            query.setParameter(1,usuario.getUsername());
            query.setParameter(2,usuario.getPassword());
            query.setParameter(3,usuario.getNombre());
            query.setParameter(4,usuario.getApellido());
            query.setParameter(5,usuario.getCorreo());

            query.executeUpdate();
            em.getTransaction().commit();

        }catch(Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
            throw  new RuntimeException(e);

        }


    }
//--------------actualizar usando username
    @Transactional
    public void updateUser(UsuarioDto usuario) {
        String sql = "UPDATE administracion_usuarios SET password = ?," +
                " nombre = ?, apellido = ?, correo = ? WHERE username = ?";
        Query q = em.createNativeQuery(sql);
        q.setParameter(1, usuario.getPassword());   // <-- ideal: hash
        q.setParameter(2, usuario.getNombre());
        q.setParameter(3, usuario.getApellido());
        q.setParameter(4, usuario.getCorreo());
        q.setParameter(5, usuario.getUsername());
        q.executeUpdate();
    }
//-----elimiar usuario...usando usernam
    @Transactional
    public void deleteUser(String username ) {
        em.createNativeQuery("DELETE FROM administracion_usuarios WHERE username = ?")
                .setParameter(1, username)
                .executeUpdate();

    }
    //----listar usuarios

    @SuppressWarnings("unchecked")
    public List<Usuario> getUsers() {
        return em.createNativeQuery(
                "SELECT username, password, nombre, apellido, correo FROM administracion_usuarios",
                Usuario.class
        ).getResultList();
    }



    //-----por si las moscas-- mas adelante se use--para saber si existe el nombre usuario
    //  y asi no tener errores/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//
    public boolean existsByUsername(String username) {
        Number count = (Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM administracion_usuarios WHERE username = ?"
        ).setParameter(1, username).getSingleResult();
        return count != null && count.intValue() > 0;
    }
//----

    // ===== Buscar usuario por username =====
    public Usuario getUser(String username) {
        try {
            return (Usuario) em.createNativeQuery(
                            "SELECT username, password, nombre, apellido, correo " +
                                    "FROM administracion_usuarios WHERE username = ?",
                            Usuario.class
                    )
                    .setParameter(1, username)
                    .getSingleResult();
        } catch (Exception e) {
            return null; // Si no existe el usuario, devolvemos null
        }
    }

}
