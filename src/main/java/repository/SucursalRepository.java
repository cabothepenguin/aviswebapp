package repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import model.Sucursal;

import java.util.List;

@ApplicationScoped
public class SucursalRepository {

    @PersistenceContext(unitName = "avisrent-pu")
    private EntityManager em;

    // CREATE
    @Transactional
    public void addSucursal(Sucursal sucursal) {
        em.persist(sucursal);
    }

    // UPDATE
    @Transactional
    public void updateSucursal(Sucursal sucursal) {
        em.merge(sucursal);
    }

    // DELETE
    @Transactional
    public void deleteSucursal(Integer codigo) {
        Sucursal ref = em.find(Sucursal.class, codigo);
        if (ref != null) em.remove(ref);
    }

    // READ
    public List<Sucursal> getSucursales() {
        return em.createQuery("SELECT s FROM Sucursal s", Sucursal.class).getResultList();
    }

    public Sucursal findSucursalById(Integer codigo) {
        return em.find(Sucursal.class, codigo);
    }

    public boolean existsByNombre(String nombre) {
        Long count = em.createQuery(
                        "SELECT COUNT(s) FROM Sucursal s WHERE s.nombre = :n", Long.class)
                .setParameter("n", nombre)
                .getSingleResult();
        return count != null && count > 0;
    }

    public Sucursal getById(Integer id) {
        return em.find(Sucursal.class, id);
    }

    @Transactional
    public void update(Sucursal sucursal) {
        em.merge(sucursal);
    }

    public Sucursal findByCodigo(Integer codigo) {
        return em.find(Sucursal.class, codigo);
    }
}
