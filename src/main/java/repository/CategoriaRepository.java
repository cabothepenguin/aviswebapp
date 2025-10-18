package repository;

import dto.CategoriaVehiculoDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import model.CategoriaVehiculo;

import java.util.List;

@ApplicationScoped
public class CategoriaRepository {

    @PersistenceContext(unitName = "avisrent-pu")
    private EntityManager em;

    // CREATE
    @Transactional
    public void addCategory(CategoriaVehiculoDto dto) {
        em.persist(toEntity(dto));
    }

    // UPDATE
    @Transactional
    public void updateCategory(CategoriaVehiculoDto dto) {
        CategoriaVehiculo actual = em.find(CategoriaVehiculo.class, dto.getCodigo());
        if (actual == null) throw new IllegalStateException("No existe categoría con código: " + dto.getCodigo());

        actual.setDescripcion(dto.getDescripcion());
        actual.setEstado(dto.getEstado());

        em.merge(actual);
    }

    // DELETE
    @Transactional
    public void deleteCategory(Integer codigo) {
        CategoriaVehiculo ref = em.find(CategoriaVehiculo.class, codigo);
        if (ref != null) em.remove(ref);
    }

    // READ
    public List<CategoriaVehiculo> getCategories() {
        return em.createQuery("SELECT c FROM CategoriaVehiculo c", CategoriaVehiculo.class)
                .getResultList();
    }

    public CategoriaVehiculo findCategoryByDescripcion(String descripcion) {
        try {
            return em.createQuery(
                            "SELECT c FROM CategoriaVehiculo c WHERE c.descripcion = :d", CategoriaVehiculo.class)
                    .setParameter("d", descripcion)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public CategoriaVehiculo findCategoryById(Integer codigo) {
        return em.find(CategoriaVehiculo.class, codigo);
    }

    public boolean existsByDescripcion(String descripcion) {
        Long count = em.createQuery(
                        "SELECT COUNT(c) FROM CategoriaVehiculo c WHERE c.descripcion = :d", Long.class)
                .setParameter("d", descripcion)
                .getSingleResult();
        return count != null && count > 0;
    }

    public CategoriaVehiculo getById(Integer id) {
        return em.find(CategoriaVehiculo.class, id);
    }

    @Transactional
    public void update(CategoriaVehiculo categoria) {
        em.merge(categoria);
    }

    // -------- Mapper --------
    private CategoriaVehiculo toEntity(CategoriaVehiculoDto dto) {
        CategoriaVehiculo c = new CategoriaVehiculo();
        c.setCodigo(dto.getCodigo());
        c.setDescripcion(dto.getDescripcion());
        c.setEstado(dto.getEstado());
        return c;
    }
}
