package repository;

import dto.UsuarioDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import model.Usuario;

import java.util.List;

/**
 * Repositorio de usuarios ({@link Usuario}).
 * <p>Opera principalmente con SQL nativo para DML y lecturas.</p>
 */
@Named
@RequestScoped
public class UsuarioRepository {

    @PersistenceContext(unitName = "avisrent-pu")
    private EntityManager em;

    /**
     * Inserta un usuario (SQL nativo).
     * @param usuario DTO con datos básicos.
     */
    public void addUser(UsuarioDto usuario) {
        try {
            em.getTransaction().begin();
            String sql = "INSERT INTO administracion_usuarios (username,password,nombre,apellido,correo) VALUES (?,?,?,?,?)";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, usuario.getUsername());
            query.setParameter(2, usuario.getPassword());
            query.setParameter(3, usuario.getNombre());
            query.setParameter(4, usuario.getApellido());
            query.setParameter(5, usuario.getCorreo());
            query.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /** Actualiza un usuario identificado por username. */
    public void updateUser(UsuarioDto usuario) {
        em.getTransaction().begin();
        String sql = "UPDATE administracion_usuarios SET password = ?," +
                " nombre = ?, apellido = ?, correo = ? WHERE username = ?";
        Query q = em.createNativeQuery(sql);
        q.setParameter(1, usuario.getPassword());
        q.setParameter(2, usuario.getNombre());
        q.setParameter(3, usuario.getApellido());
        q.setParameter(4, usuario.getCorreo());
        q.setParameter(5, usuario.getUsername());
        q.executeUpdate();
        em.getTransaction().commit();
    }

    /** Elimina un usuario por su username. */
    public void deleteUser(String username ) {
        em.getTransaction().begin();
        em.createNativeQuery("DELETE FROM administracion_usuarios WHERE username = ?")
                .setParameter(1, username)
                .executeUpdate();
        em.getTransaction().commit();
    }

    /**
     * Lista todos los usuarios mapeando a la entidad {@link Usuario}.
     * @return lista de entidades.
     */
    @SuppressWarnings("unchecked")
    public List<Usuario> getUsers() {
        return em.createNativeQuery(
                "SELECT username, password, nombre, apellido, correo FROM administracion_usuarios",
                Usuario.class
        ).getResultList();
    }

    /**
     * Verifica existencia por username (SQL nativo).
     * @param username nombre de usuario.
     */
    public boolean existsByUsername(String username) {
        Number count = (Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM administracion_usuarios WHERE username = ?"
        ).setParameter(1, username).getSingleResult();
        return count != null && count.intValue() > 0;
    }

    /**
     * Obtiene un usuario por username.
     * @return entidad o {@code null} si no existe.
     */
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
            return null;
        }
    }

    /**
     * Busca un usuario por username y password (lectura simple).
     * @return entidad o {@code null} si no coincide.
     */
    public Usuario findByUsernameAndPassword(String username, String password) {
        try {
            return (Usuario) em.createNativeQuery(
                            "SELECT username, password, nombre, apellido, correo " +
                                    "FROM administracion_usuarios WHERE username = ? AND password = ?",
                            Usuario.class
                    )
                    .setParameter(1, username)
                    .setParameter(2, password)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
