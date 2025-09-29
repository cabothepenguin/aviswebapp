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


    public void addCategory(CategoriaVehiculoDto categoria) {
        em.getTransaction().begin();
        em.createNativeQuery(
                        "INSERT INTO administracion_categorias (descripcion, estado) VALUES (?, ?)")
                .setParameter(1, categoria.getDescripcion())
                .setParameter(2, categoria.getEstado())
                .executeUpdate();
        em.getTransaction().commit();
    }




    // --actualizar por codigo

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
        }catch(Exception e){

            em.getTransaction().rollback();
            e.printStackTrace();
            throw  new RuntimeException(e);
        }

    }

    // ---eliminar por codigo

    public void deleteCategory(Integer codigo) {
        em.getTransaction().begin();
        String sql = "DELETE FROM administracion_categorias WHERE codigo = ?";
        em.createNativeQuery(sql)
                .setParameter(1, codigo)
                .executeUpdate();
        em.getTransaction().commit();
    }

    // ----listar
    @SuppressWarnings("unchecked")
    public List<CategoriaVehiculo> getCategories() {
        return em.createNativeQuery(
                "SELECT codigo, descripcion, estado FROM administracion_categorias",
                CategoriaVehiculo.class
        ).getResultList();
    }

    // ------- Buscar por descripción
    public CategoriaVehiculo findCategoryByDescripcion(String descripcion) {
        try {
            return em.createQuery(
                            "FROM administracion_Categorias c WHERE c.descripcion = :desc", CategoriaVehiculo.class)
                    .setParameter("desc", descripcion)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null; // o Optional.empty()
        }
    }
    public CategoriaVehiculo findCategoryById(Integer codigo) {
        return em.find(CategoriaVehiculo.class, codigo);
    }




    // Verificar existencia por descripcion
    public boolean existsByDescripcion(String descripcion) {
        Number count = (Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM administracion_categorias WHERE descripcion = ?"
        ).setParameter(1, descripcion).getSingleResult();
        return count != null && count.intValue() > 0;
    }

    public CategoriaVehiculo getById(Integer id) {
        return em.find(CategoriaVehiculo.class, id);
    }

    @Transactional
    public void update(CategoriaVehiculo categoria) {
        em.merge(categoria);
    }


}
