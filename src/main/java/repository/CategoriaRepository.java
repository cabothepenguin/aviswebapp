package repository;

import dto.CategoriaVehiculoDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import model.CategoriaVehiculo;

import java.util.List;

/**
 * Repositorio de acceso a datos para {@link CategoriaVehiculo}.
 * <p>
 * Usa SQL nativo para operaciones de inserción/actualización/borrado y
 * consultas simples. Algunas operaciones gestionan manualmente transacciones
 * mediante {@code em.getTransaction()}.
 * </p>
 *
 */
@ApplicationScoped
public class CategoriaRepository {

    @PersistenceContext(unitName = "avisrent-pu")
    private EntityManager em;

    /**
     * Inserta una nueva categoría usando SQL nativo.
     * @param categoria DTO con descripción y estado.
     */
    public void addCategory(CategoriaVehiculoDto categoria) {
        em.getTransaction().begin();
        em.createNativeQuery(
                        "INSERT INTO administracion_categorias (descripcion, estado) VALUES (?, ?)")
                .setParameter(1, categoria.getDescripcion())
                .setParameter(2, categoria.getEstado())
                .executeUpdate();
        em.getTransaction().commit();
    }

    /** Actualiza una categoría por su código usando SQL nativo. */
    public void updateCategory(CategoriaVehiculoDto categoria) {
        try {
            em.getTransaction().begin();
            String jpql = "UPDATE administracion_categorias SET descripcion = ?, estado = ? WHERE codigo = ?";
            em.createNativeQuery(jpql)
                    .setParameter(1, categoria.getDescripcion())
                    .setParameter(2, categoria.getEstado())
                    .setParameter(3, categoria.getCodigo())
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /** Elimina una categoría por su código. */
    public void deleteCategory(Integer codigo) {
        em.getTransaction().begin();
        String sql = "DELETE FROM administracion_categorias WHERE codigo = ?";
        em.createNativeQuery(sql)
                .setParameter(1, codigo)
                .executeUpdate();
        em.getTransaction().commit();
    }

    /**
     * Lista todas las categorías.
     * @return lista de entidades mapeadas a {@link CategoriaVehiculo}.
     */
    @SuppressWarnings("unchecked")
    public List<CategoriaVehiculo> getCategories() {
        return em.createNativeQuery(
                "SELECT codigo, descripcion, estado FROM administracion_categorias",
                CategoriaVehiculo.class
        ).getResultList();
    }

    /**
     * Busca una categoría por descripción exacta.
     * @param descripcion valor a buscar.
     * @return entidad encontrada o {@code null} si no existe.
     */
    public CategoriaVehiculo findCategoryByDescripcion(String descripcion) {
        try {
            return em.createQuery(
                            "FROM administracion_categorias c WHERE c.descripcion = :desc", CategoriaVehiculo.class)
                    .setParameter("desc", descripcion)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /** Busca una categoría por su id (clave primaria). */
    public CategoriaVehiculo findCategoryById(Integer codigo) {
        return em.find(CategoriaVehiculo.class, codigo);
    }

    /**
     * Verifica existencia por descripción (insensible a mayúsculas según BD).
     * @param descripcion descripción a validar.
     * @return {@code true} si existe al menos un registro.
     */
    public boolean existsByDescripcion(String descripcion) {
        Number count = (Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM administracion_categorias WHERE descripcion = ?"
        ).setParameter(1, descripcion).getSingleResult();
        return count != null && count.intValue() > 0;
    }

    /** Obtiene una categoría por id usando {@link EntityManager#find}. */
    public CategoriaVehiculo getById(Integer id) {
        return em.find(CategoriaVehiculo.class, id);
    }

    /**
     * Actualiza una entidad administrada mediante {@link EntityManager#merge(Object)}.
     * Requiere contexto transaccional activo (anotado con {@link Transactional}).
     */
    @Transactional
    public void update(CategoriaVehiculo categoria) {
        em.merge(categoria);
    }
}
