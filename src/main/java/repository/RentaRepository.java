package repository;

import dto.RentasDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import model.Renta;

import java.util.Date;
import java.util.List;

/**
 * Repositorio para la entidad {@link Renta}.
 * <p>
 * Combina SQL nativo para operaciones DML con JPQL para lecturas que
 * materializan relaciones ({@code JOIN FETCH}).
 * </p>
 */
@ApplicationScoped
public class RentaRepository {

    @PersistenceContext(unitName = "avisrent-pu")
    private EntityManager em;

    /**
     * Inserta una renta con SQL nativo.
     * @param r DTO con datos básicos, incluyendo referencias (placa, sucursal).
     */
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

    /** Actualiza una renta por número usando SQL nativo. */
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

    /** Elimina una renta por su número. */
    public void deleteRenta(int numeroRenta) {
        em.getTransaction().begin();
        em.createNativeQuery("DELETE FROM administracion_rentas WHERE numeroRenta = ?")
                .setParameter(1, numeroRenta)
                .executeUpdate();
        em.getTransaction().commit();
    }

    /**
     * Lista todas las rentas con sus relaciones (vehículo y sucursal).
     * @return lista ordenada por número de renta descendente.
     */
    @SuppressWarnings("unchecked")
    public List<Renta> getRenta() {
        return em.createQuery(
                "SELECT r FROM Renta r " +
                        "JOIN FETCH r.vehiculo v " +
                        "JOIN FETCH r.sucursal s " +
                        "ORDER BY r.numeroRenta DESC",
                Renta.class
        ).getResultList();
    }

    /**
     * Busca una renta por número con {@code JOIN FETCH}.
     * @param numeroRenta identificador.
     * @return entidad encontrada (lanza excepción si no existe).
     */
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

    /**
     * Verifica existencia por número (SQL nativo).
     * @return {@code true} si existe al menos un registro con ese número.
     */
    public boolean existsBynumeroRenta(int numeroRenta) {
        Number count = (Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM administracion_rentas WHERE numeroRenta = ?"
        ).setParameter(1, numeroRenta).getSingleResult();
        return count != null && count.intValue() > 0;
    }

    /**
     * Lista rentas filtradas por estado (insensible a mayúsculas).
     * @param estado valor del estado.
     */
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
