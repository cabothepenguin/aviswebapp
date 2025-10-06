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
@Named
@ApplicationScoped
public class VehiculoRepository {

    @PersistenceContext(unitName = "avisrent-pu")
    private EntityManager em;

    // ===== agregar

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


    // ===== Actualizar por placas
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
        q.setParameter(9, vehiculoDto.getPlaca()); //

        q.executeUpdate();
        em.getTransaction().commit();
    }


    // ===== Eliminar por placa
    public void deleteVehiculo(String placa) {  // Cambiar de Integer a String
        em.getTransaction().begin();
        em.createNativeQuery("DELETE FROM administracion_vehiculos WHERE placa = ?")
                .setParameter(1, placa)
                .executeUpdate();
        em.getTransaction().commit();
    }

    // ===== Listar
    @SuppressWarnings("unchecked")
    public List<Vehiculo> getVehiculos() {
        return em.createQuery("SELECT v FROM Vehiculo v", Vehiculo.class)
                .getResultList();
    }

    // ===== Buscar por placa
    public Vehiculo findVehiculoByPlaca(String placa) {  // Cambiar de Integer a String
        return em.find(Vehiculo.class, placa);
    }

    // ===== Verificar existencia por placa
    public boolean existsByPlaca(String placa) {  // Cambiar de Integer a String
        try {
            Long count = em.createQuery("SELECT COUNT(v) FROM Vehiculo v WHERE v.placa = :placa", Long.class)
                    .setParameter("placa", placa)
                    .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }



    public List<Vehiculo> findByCategoriaCodigo(Integer codigo) {
        // Si tu entidad Vehiculo tiene relación @ManyToOne categoria
        return em.createQuery(
                "SELECT v FROM Vehiculo v WHERE v.categoria.codigo = :cod",
                Vehiculo.class
        ).setParameter("cod", codigo).getResultList();
    }

    public List<Vehiculo> findByCategoriaDescripcion(String desc) {
        return em.createQuery(
                "SELECT v FROM Vehiculo v WHERE v.categoria.descripcion = :desc",
                Vehiculo.class
        ).setParameter("desc", desc).getResultList();
    }

    // VehiculoRepository.java

    @SuppressWarnings("unchecked")
    public List<Vehiculo> findByEstado(String estado) {
        return em.createQuery(
                        "SELECT v FROM Vehiculo v " +
                                "WHERE LOWER(v.estado) = :estado " +
                                "ORDER BY v.marca, v.modelo", Vehiculo.class)
                .setParameter("estado", estado.toLowerCase())
                .getResultList();
    }

    // Atajos convenientes (opcional)
    public List<Vehiculo> findDisponibles()     { return findByEstado("disponible"); }
    public List<Vehiculo> findMantenimiento()   { return findByEstado("mantenimiento"); }
    public List<Vehiculo> findRentados()        { return findByEstado("rentado"); }


}