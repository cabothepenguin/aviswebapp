package repository;

import dto.UsuarioDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import model.Usuario;

import java.util.List;

@Named
@RequestScoped
public class UsuarioRepository {

    @PersistenceContext(unitName = "avisrent-pu")
    private EntityManager em;

    // CREATE
    @Transactional
    public void addUser(UsuarioDto dto) {
        em.persist(toEntity(dto));
    }

    // UPDATE
    @Transactional
    public void updateUser(UsuarioDto dto) {
        Usuario actual = em.find(Usuario.class, dto.getUsername());
        if (actual == null) throw new IllegalStateException("No existe usuario: " + dto.getUsername());

        actual.setPassword(dto.getPassword());
        actual.setNombre(dto.getNombre());
        actual.setApellido(dto.getApellido());
        actual.setCorreo(dto.getCorreo());

        em.merge(actual);
    }

    // DELETE
    @Transactional
    public void deleteUser(String username) {
        Usuario ref = em.find(Usuario.class, username);
        if (ref != null) em.remove(ref);
    }

    // READ
    public List<Usuario> getUsers() {
        return em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
    }

    public boolean existsByUsername(String username) {
        Long count = em.createQuery(
                        "SELECT COUNT(u) FROM Usuario u WHERE u.username = :u", Long.class)
                .setParameter("u", username)
                .getSingleResult();
        return count != null && count > 0;
    }

    public Usuario getUser(String username) {
        return em.find(Usuario.class, username);
    }

    public Usuario findByUsernameAndPassword(String username, String password) {
        List<Usuario> list = em.createQuery(
                        "SELECT u FROM Usuario u WHERE u.username = :u AND u.password = :p", Usuario.class)
                .setParameter("u", username)
                .setParameter("p", password)
                .getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    // Mapper
    private Usuario toEntity(UsuarioDto dto) {
        Usuario u = new Usuario();
        u.setUsername(dto.getUsername());
        u.setPassword(dto.getPassword());
        u.setNombre(dto.getNombre());
        u.setApellido(dto.getApellido());
        u.setCorreo(dto.getCorreo());
        return u;
    }
}
