package repository;

import dto.VehiculoDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import model.Vehiculo;

import java.util.List;

/**
 * Repositorio para la entidad {@link Vehiculo}.
 * <p>
 * Inserciones/actualizaciones/borrados con SQL nativo y consultas con JPQL
 * donde se requiere materializar relaciones o mapear a entidad.
 * </p>
 */
@Named
@ApplicationScoped
public class VehiculoRepository {

    @PersistenceContext(unitName = "avisrent-pu")
    private EntityManager em;

    /**
     * Inserta un vehículo desde un {@link VehiculoDto}.
     * <p>Persisten la categoría como código y la imagen como bytes/nombre.</p>
     */
    public void addVehiculo(VehiculoDto vehiculoDto) {
        try {
            em.getTransaction().begin();
            String sql = "INSERT INTO administracion_vehiculos " +
                    "(placa, modelo, marca, categoria, estado, anio, precio, imagen, nombre_imagen) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            Query query = em.createNativeQuery(sql);
            query.setParameter(1, vehiculoDto.getPlaca());
            query.setParameter(2, vehiculoDto.getModelo());
            query.setParameter(3, vehiculoDto.getMarca());
            query.setParameter(4, vehiculoDto.getCategoria().getCodigo());
            query.setParameter(5, vehiculoDto.getEstado());
            query.setParameter(6, vehiculoDto.getAnio());
            query.setParameter(7, vehiculoDto.getPrecio());
            query.setParameter(8, vehiculoDto.getImage());
            query.setParameter(9, vehiculoDto.getImageName());

            query.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error insertando vehículo", e);
        }
    }

    /** Actualiza un vehículo por su placa usando SQL nativo. */
    public void updateVehiculo(VehiculoDto vehiculoDto) {
        em.getTransaction().begin();
        String sql = "UPDATE administracion_vehiculos " +
                "SET modelo = ?, " +
                "    marca = ?, " +
                "    categoria = ?, " +
                "    estado = ?, " +
                "    anio = ?, " +
                "    precio = ?, " +
                "    imagen = ?, " +
                "    nombre_imagen = ? " +
                "WHERE placa = ?";

        Query q = em.createNativeQuery(sql);
        q.setParameter(1, vehiculoDto.getModelo());
        q.setParameter(2, vehiculoDto.getMarca());
        q.setParameter(3, vehiculoDto.getCategoria() != null ? vehiculoDto.getCategoria().getCodigo() : null);
        q.setParameter(4, vehiculoDto.getEstado());
        q.setParameter(5, vehiculoDto.getAnio());
        q.setParameter(6, vehiculoDto.getPrecio());
        q.setParameter(7, vehiculoDto.getImage());
        q.setParameter(8, vehiculoDto.getImageName());
        q.setParameter(9, vehiculoDto.getPlaca());
        q.executeUpdate();
        em.getTransaction().commit();
    }

    /** Elimina un vehículo por su placa. */
    public void deleteVehiculo(String placa) {
        em.getTransaction().begin();
        em.createNativeQuery("DELETE FROM administracion_vehiculos WHERE placa = ?")
                .setParameter(1, placa)
                .executeUpdate();
        em.getTransaction().commit();
    }

    /** Lista todos los vehículos con JPQL mapeando a entidad. */
    @SuppressWarnings("unchecked")
    public List<Vehiculo> getVehiculos() {
        return em.createQuery("SELECT v FROM Vehiculo v", Vehiculo.class)
                .getResultList();
    }

    /** Busca un vehículo por su placa (PK). */
    public Vehiculo findVehiculoByPlaca(String placa) {
        return em.find(Vehiculo.class, placa);
    }

    /**
     * Verifica si existe un vehículo con la placa indicada.
     * @return {@code true} si el conteo es mayor que cero.
     */
    public boolean existsByPlaca(String placa) {
        try {
            Long count = em.createQuery("SELECT COUNT(v) FROM Vehiculo v WHERE v.placa = :placa", Long.class)
                    .setParameter("placa", placa)
                    .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /** Lista vehículos por código de categoría. */
    public List<Vehiculo> findByCategoriaCodigo(Integer codigo) {
        return em.createQuery(
                "SELECT v FROM Vehiculo v WHERE v.categoria.codigo = :cod",
                Vehiculo.class
        ).setParameter("cod", codigo).getResultList();
    }

    /** Lista vehículos por descripción de categoría. */
    public List<Vehiculo> findByCategoriaDescripcion(String desc) {
        return em.createQuery(
                "SELECT v FROM Vehiculo v WHERE v.categoria.descripcion = :desc",
                Vehiculo.class
        ).setParameter("desc", desc).getResultList();
    }

    /**
     * Lista vehículos por estado normalizado (minúsculas).
     * @param estado estado objetivo (disponible, mantenimiento, rentado).
     */
    @SuppressWarnings("unchecked")
    public List<Vehiculo> findByEstado(String estado) {
        return em.createQuery(
                        "SELECT v FROM Vehiculo v " +
                                "WHERE LOWER(v.estado) = :estado " +
                                "ORDER BY v.marca, v.modelo", Vehiculo.class)
                .setParameter("estado", estado.toLowerCase())
                .getResultList();
    }

    // Atajos convenientes
    public List<Vehiculo> findDisponibles()   { return findByEstado("disponible"); }
    public List<Vehiculo> findMantenimiento() { return findByEstado("mantenimiento"); }
    public List<Vehiculo> findRentados()      { return findByEstado("rentado"); }
}
