package repository;

import dto.RentasDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import model.Renta;

import java.util.List;

public class RentaRepository {

    @PersistenceContext(unitName = "avisrent-pu")
    private EntityManager em;

    public void addRenta(RentasDto r) {
        try {
            em.getTransaction().begin();
            String sql = "INSERT INTO administracion_rentas " +
                    "(clienteNombre, vehiculoAsignado, sucursal, fechaInicio, fechafin, precioTotal, estado) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            em.createNativeQuery(sql)
                    .setParameter(1, r.getClienteNombre())
                    .setParameter(2, r.getVehiculoPlaca())
                    .setParameter(3, r.getSucursalCodigo())
                    .setParameter(4, r.getFechaInicio())
                    .setParameter(5, r.getFechaFin())
                    .setParameter(6, r.getPrecioTotal())
                    .setParameter(7, r.getEstado())
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }

    public void updateRenta(RentasDto r) {
        em.getTransaction().begin();
        String sql = "UPDATE administracion_rentas SET " +
                "clienteNombre = ?, vehiculoAsignado = ?, sucursal = ?, fechaInicio = ?, " +
                "fechafin = ?, precioTotal = ?, estado = ? WHERE numeroRenta = ?";
        em.createNativeQuery(sql)
                .setParameter(1, r.getClienteNombre())
                .setParameter(2, r.getVehiculoPlaca())
                .setParameter(3, r.getSucursalCodigo())
                .setParameter(4, r.getFechaInicio())
                .setParameter(5, r.getFechaFin())
                .setParameter(6, r.getPrecioTotal())
                .setParameter(7, r.getEstado())
                .setParameter(8, r.getNumeroRenta())
                .executeUpdate();
        em.getTransaction().commit();
    }

    public void deleteRenta(int numeroRenta) {
        em.getTransaction().begin();
        em.createNativeQuery("DELETE FROM administracion_rentas WHERE numeroRenta = ?")
                .setParameter(1, numeroRenta)
                .executeUpdate();
        em.getTransaction().commit();
    }

    @SuppressWarnings("unchecked")
    public List<Renta> getRenta() {
        return em.createNativeQuery(
                "SELECT numeroRenta, clienteNombre, vehiculoAsignado, sucursal, fechaInicio, fechafin, precioTotal, estado " +
                        "FROM administracion_rentas",
                Renta.class
        ).getResultList();
    }

    public Renta findRentaByNumeroRenta(int numeroRenta) {
        return (Renta) em.createNativeQuery(
                "SELECT numeroRenta, clienteNombre, vehiculoAsignado, sucursal, fechaInicio, fechafin, precioTotal, estado " +
                        "FROM administracion_rentas WHERE numeroRenta = ?",
                Renta.class
        ).setParameter(1, numeroRenta).getSingleResult();
    }

    public boolean existsBynumeroRenta(int numeroRenta) {
        Number count = (Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM administracion_rentas WHERE numeroRenta = ?"
        ).setParameter(1, numeroRenta).getSingleResult();
        return count != null && count.intValue() > 0;
    }
}
