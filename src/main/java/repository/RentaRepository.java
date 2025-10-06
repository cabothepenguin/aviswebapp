package repository;

import dto.RentasDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import model.Renta;

import java.util.Date;
import java.util.List;
@ApplicationScoped
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
        // JPQL, no SQL nativo -> las relaciones se materializan
        return em.createQuery(
                "SELECT r FROM Renta r " +
                        "JOIN FETCH r.vehiculo v " +
                        "JOIN FETCH r.sucursal s " +
                        "ORDER BY r.numeroRenta DESC",
                Renta.class
        ).getResultList();
    }

    public Renta findRentaByNumeroRenta(int numeroRenta) {
        return em.createQuery(
                        "SELECT r FROM Renta r " +
                                "JOIN FETCH r.vehiculo v " +
                                "JOIN FETCH r.sucursal s " +
                                "WHERE r.numeroRenta = :n",
                        Renta.class
                ).setParameter("n", numeroRenta)
                .getSingleResult();
    }



    public boolean existsBynumeroRenta(int numeroRenta) {
        Number count = (Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM administracion_rentas WHERE numeroRenta = ?"
        ).setParameter(1, numeroRenta).getSingleResult();
        return count != null && count.intValue() > 0;
    }

    public List<Renta> findByEstado(String estado) {
        return em.createQuery(
                        "SELECT r FROM Renta r " +
                                "JOIN FETCH r.vehiculo v " +
                                "JOIN FETCH r.sucursal s " +
                                "WHERE LOWER(r.estado) = LOWER(:estado) " +
                                "ORDER BY r.fechaInicio DESC",
                        Renta.class
                ).setParameter("estado", estado)
                .getResultList();
    }


}
