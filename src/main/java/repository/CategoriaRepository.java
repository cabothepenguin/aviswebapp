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

    @PersistenceContext(unitName = "unaweb-pu")
    private EntityManager em;

    @Transactional
    public void addCategory(CategoriaVehiculoDto categoria) {
        em.createNativeQuery(
                        "INSERT INTO administracion_categorias (descripcion, estado) VALUES (?, ?)")
                .setParameter(1, categoria.getDescripcion())
                .setParameter(2, categoria.getEstado())
                .executeUpdate();
    }




    // --actualizar por codigo
    @Transactional
    public void updateCategory(CategoriaVehiculoDto categoria) {

        try {
            String sql = "UPDATE administracion_categorias SET descripcion = ?, estado = ? WHERE codigo = ?";
            em.createNativeQuery(sql)
                    .setParameter(1, categoria.getDescripcion())
                    .setParameter(2, categoria.getEstado())
                    .setParameter(3, categoria.getCodigo())
                    .executeUpdate();
        }catch(Exception e){

            em.getTransaction().rollback();
            e.printStackTrace();
            throw  new RuntimeException(e);
        }

    }

    // ---eliminar por codigo
    @Transactional
    public void deleteCategory(Integer codigo) {
        String sql = "DELETE FROM administracion_categorias WHERE codigo = ?";
        em.createNativeQuery(sql)
                .setParameter(1, codigo)
                .executeUpdate();
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





}
