package repository;

import dto.VehiculoDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import model.Vehiculo;

import java.util.List;

@ApplicationScoped
public class VehiculoRepository {
    @PersistenceContext(unitName = "avisrent-pu")
    private EntityManager em;


    // ===== agregar

    public void addVehiculo(VehiculoDto vehiculo) {
        try {
            em.getTransaction().begin();

            String sql = "INSERT INTO administracion_vehiculos " +
                    "(placa, modelo, marca, categoria, estado, año, precio, imagen, nombre_imagen) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            em.createNativeQuery(sql)
                    .setParameter(1, vehiculo.getPlaca())
                    .setParameter(2, vehiculo.getModelo())
                    .setParameter(3, vehiculo.getMarca())
                    .setParameter(4, vehiculo.getCategoria().getCodigo())  // FK a categoria
                    .setParameter(5, vehiculo.getEstado())
                    .setParameter(6, vehiculo.getAnio())
                    .setParameter(7, vehiculo.getPrecio())
                    .setParameter(8, vehiculo.getImage())
                    .setParameter(9, vehiculo.getImageName())
                    .executeUpdate();
            em.getTransaction().commit();
        }catch(Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
            throw  new RuntimeException(e);

        }

    }

    // ===== Actualizar por placa

    public void updateVehiculo(VehiculoDto vehiculo) {
        em.getTransaction().begin();
        String sql = "UPDATE administracion_vehiculos SET modelo = ?, marca = ?, categoria = ?, estado = ?, " +
                "año = ?, precio = ?, imagen = ?, nombre_imagen = ? WHERE placa = ?";
        em.createNativeQuery(sql)
                .setParameter(1, vehiculo.getModelo())
                .setParameter(2, vehiculo.getMarca())
                .setParameter(3, vehiculo.getCategoria().getCodigo())
                .setParameter(4, vehiculo.getEstado())
                .setParameter(5, vehiculo.getAnio())
                .setParameter(6, vehiculo.getPrecio())
                .setParameter(7, vehiculo.getImage())
                .setParameter(8, vehiculo.getImageName())
                .setParameter(9, vehiculo.getPlaca())
                .executeUpdate();
        em.getTransaction().commit();
    }

    // ===== Eliminar por placa

    public void deleteVehiculo(Integer placa) {
        em.getTransaction().begin();
        em.createNativeQuery("DELETE FROM administracion_vehiculos WHERE placa = ?")
                .setParameter(1, placa)
                .executeUpdate();
        em.getTransaction().commit();
    }

    // ===== Listar
    @SuppressWarnings("unchecked")
    public List<Vehiculo> getVehiculos() {
        return em.createNativeQuery(
                "SELECT placa, modelo, marca, categoria, estado, año, precio, imagen, nombre_imagen " +
                        "FROM administracion_vehiculos",
                Vehiculo.class
        ).getResultList();
    }

    // ===== Buscar por placa
    public Vehiculo findVehiculoByPlaca(Integer placa) {
        return (Vehiculo) em.createNativeQuery(
                "SELECT placa, modelo, marca, categoria, estado, año, precio, imagen, nombre_imagen " +
                        "FROM administracion_vehiculos WHERE placa = ?",
                Vehiculo.class
        ).setParameter(1, placa).getSingleResult();
    }

    // ===== Verificar existencia por placa
    public boolean existsByPlaca(Integer placa) {
        Number count = (Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM administracion_vehiculos WHERE placa = ?"
        ).setParameter(1, placa).getSingleResult();
        return count != null && count.intValue() > 0;
    }

}
