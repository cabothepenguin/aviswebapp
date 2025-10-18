package repository;

import dto.RentasDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import model.Renta;
import model.Vehiculo;
import model.Sucursal;

import java.util.List;

@ApplicationScoped
public class RentaRepository {

    @PersistenceContext(unitName = "avisrent-pu")
    private EntityManager em;

    // CREATE
    @Transactional
    public void addRenta(RentasDto r) {
        em.persist(toEntity(r));
    }

    // UPDATE
    @Transactional
    public void updateRenta(RentasDto r) {
        Renta actual = em.find(Renta.class, r.getNumeroRenta());
        if (actual == null) throw new IllegalStateException("No existe la renta #" + r.getNumeroRenta());

        actual.setClienteNombre(r.getClienteNombre());

        Vehiculo v = em.find(Vehiculo.class, r.getVehiculoPlaca());
        actual.setVehiculo(v);

        Sucursal s = em.find(Sucursal.class, r.getSucursalCodigo());
        actual.setSucursal(s);

        actual.setFechaInicio(r.getFechaInicio());
        actual.setFechaFin(r.getFechaFin());
        actual.setPrecioTotal(r.getPrecioTotal());
        actual.setEstado(r.getEstado());

        em.merge(actual);
    }

    // DELETE
    @Transactional
    public void deleteRenta(int numeroRenta) {
        Renta ref = em.find(Renta.class, numeroRenta);
        if (ref != null) em.remove(ref);
    }

    // READ
    public List<Renta> getRenta() {
        return em.createQuery(
                        "SELECT r FROM Renta r " +
                                "JOIN FETCH r.vehiculo v " +
                                "JOIN FETCH r.sucursal s " +
                                "ORDER BY r.numeroRenta DESC", Renta.class)
                .getResultList();
    }

    public Renta findRentaByNumeroRenta(int numeroRenta) {
        List<Renta> list = em.createQuery(
                        "SELECT r FROM Renta r " +
                                "JOIN FETCH r.vehiculo v " +
                                "JOIN FETCH r.sucursal s " +
                                "WHERE r.numeroRenta = :n", Renta.class)
                .setParameter("n", numeroRenta)
                .getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    public boolean existsBynumeroRenta(int numeroRenta) {
        Long count = em.createQuery(
                        "SELECT COUNT(r) FROM Renta r WHERE r.numeroRenta = :n", Long.class)
                .setParameter("n", numeroRenta)
                .getSingleResult();
        return count != null && count > 0;
    }

    public List<Renta> findByEstado(String estado) {
        return em.createQuery(
                        "SELECT r FROM Renta r " +
                                "JOIN FETCH r.vehiculo v " +
                                "JOIN FETCH r.sucursal s " +
                                "WHERE LOWER(r.estado) = LOWER(:estado) " +
                                "ORDER BY r.fechaInicio DESC", Renta.class)
                .setParameter("estado", estado)
                .getResultList();
    }

    // -------- Mapper --------
    private Renta toEntity(RentasDto dto) {
        Renta r = new Renta();
        r.setClienteNombre(dto.getClienteNombre());

        Vehiculo v = em.find(Vehiculo.class, dto.getVehiculoPlaca());
        r.setVehiculo(v);

        Sucursal s = em.find(Sucursal.class, dto.getSucursalCodigo());
        r.setSucursal(s);

        r.setFechaInicio(dto.getFechaInicio());
        r.setFechaFin(dto.getFechaFin());
        r.setPrecioTotal(dto.getPrecioTotal());
        r.setEstado(dto.getEstado());
        // Si numeroRenta lo genera la BD, no lo toques; si no, setéalo:
        r.setNumeroRenta(dto.getNumeroRenta());
        return r;
    }
}
