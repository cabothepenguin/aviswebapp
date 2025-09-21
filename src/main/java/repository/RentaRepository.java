package repository;

import dto.RentasDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import model.Renta;

import java.util.List;

public class RentaRepository {
    @PersistenceContext(unitName = "unaweb-pu")
    private EntityManager em;

    @Transactional
    public void addRenta(RentasDto rentas) {
        try {
            String sql = "INSERT INTO administracion_rentas " +
                    "(clienteNombre, vehiculoAsignado , sucursal, fechaInicio, fechafin,precioTotal, estado) " +
                    "VALUES (?, ?, ?, ?, ?, ?,?)";
            em.createNativeQuery(sql)
                    .setParameter(1, rentas.getClienteNombre())
                    .setParameter(2, rentas.getVehiculo().getPlaca())
                    .setParameter(3, rentas.getSucursal().getCodigo())
                    .setParameter(4, rentas.getFechaInicio())
                    .setParameter(5, rentas.getFechaFin())
                    .setParameter(6, rentas.getPrecioTotal())
                    .setParameter(7, rentas.getEstado())
                    .executeUpdate();
        }catch(Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
            throw  new RuntimeException(e);
        }


    }


    // ===== Actualizar buscando el numero de renta
    @Transactional
    public void updateRenta(RentasDto rentas) {
        String sql = "UPDATE administracion_rentas SET clienteNombre = ?, vehiculoAsignado  = ?, sucursal  = ?, fechaInicio = ?, " +
                "fechafin = ?, precioTotal = ?, estado = ? WHERE numeroRenta = ?";
        em.createNativeQuery(sql)
                .setParameter(1, rentas.getClienteNombre())
                .setParameter(2, rentas.getVehiculo().getPlaca())
                .setParameter(3, rentas.getSucursal().getCodigo())
                .setParameter(4, rentas.getFechaInicio())
                .setParameter(5, rentas.getFechaFin())
                .setParameter(6, rentas.getPrecioTotal())
                .setParameter(7, rentas.getEstado())
                .setParameter(8, rentas.getNumeroRenta())
                .executeUpdate();
    }

    // ===== Eliminar por numero renta
    @Transactional
    public void deleteRenta(int numeroRenta) {
        em.createNativeQuery("DELETE FROM administracion_rentas WHERE numeroRenta = ?")
                .setParameter(1, numeroRenta)
                .executeUpdate();
    }

    // ===== Listar
    @SuppressWarnings("unchecked")
    public List<Renta> getRenta() {
        return em.createNativeQuery(
                "SELECT numeroRenta, clienteNombre, vehiculoAsignado , sucursal , fechaInicio, fechafin, precioTotal, estado " +
                        "FROM administracion_rentas",
                Renta.class
        ).getResultList();
    }

    // ===== Buscar por numeroRenta

    public Renta findRentaByNumeroRenta(int numeroRenta) {
        return (Renta) em.createNativeQuery(
                "SELECT numeroRenta, clienteNombre, vehiculoAsignado , sucursal , fechaInicio, fechafin, precioTotal, estado " +
                        "FROM administracion_rentas WHERE numeroRenta = ?",
                Renta.class
        ).setParameter(1, numeroRenta).getSingleResult();
    }

    // ===== Verificar existencia por numeroRenta
    public boolean existsBynumeroRenta(int numeroRenta) {
        Number count = (Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM administracion_rentas WHERE numeroRenta = ?"
        ).setParameter(1, numeroRenta).getSingleResult();
        return count != null && count.intValue() > 0;
    }

}
