package repository;

import dto.VehiculoDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import model.Vehiculo;
import model.CategoriaVehiculo;

import java.util.List;

@Named
@ApplicationScoped
public class VehiculoRepository {

    @PersistenceContext(unitName = "avisrent-pu")
    private EntityManager em;

    // --------- CREATE ---------
    @Transactional
    public void addVehiculo(VehiculoDto dto) {
        em.persist(toEntity(dto));
    }

    // --------- UPDATE ---------
    @Transactional
    public void updateVehiculo(VehiculoDto dto) {
        Vehiculo actual = em.find(Vehiculo.class, dto.getPlaca());
        if (actual == null) throw new IllegalStateException("No existe vehículo con placa: " + dto.getPlaca());

        actual.setModelo(dto.getModelo());
        actual.setMarca(dto.getMarca());
        if (dto.getCategoria() != null && dto.getCategoria().getCodigo() != null) {
            CategoriaVehiculo cat = em.find(CategoriaVehiculo.class, dto.getCategoria().getCodigo());
            actual.setCategoria(cat);
        } else {
            actual.setCategoria(null);
        }
        actual.setEstado(dto.getEstado());
        actual.setAnio(dto.getAnio());
        actual.setPrecio(dto.getPrecio());
        actual.setImage(dto.getImage());
        actual.setImageName(dto.getImageName());

        em.merge(actual);
    }

    // --------- DELETE ---------
    @Transactional
    public void deleteVehiculo(String placa) {
        Vehiculo ref = em.find(Vehiculo.class, placa);
        if (ref != null) em.remove(ref);
    }

    // --------- READ ---------
    public List<Vehiculo> getVehiculos() {
        return em.createQuery("SELECT v FROM Vehiculo v", Vehiculo.class).getResultList();
    }

    public Vehiculo findVehiculoByPlaca(String placa) {
        return em.find(Vehiculo.class, placa);
    }

    public boolean existsByPlaca(String placa) {
        Long count = em.createQuery(
                        "SELECT COUNT(v) FROM Vehiculo v WHERE v.placa = :placa", Long.class)
                .setParameter("placa", placa)
                .getSingleResult();
        return count != null && count > 0;
    }

    public List<Vehiculo> findByCategoriaCodigo(Integer codigo) {
        return em.createQuery(
                        "SELECT v FROM Vehiculo v WHERE v.categoria.codigo = :cod", Vehiculo.class)
                .setParameter("cod", codigo)
                .getResultList();
    }

    public List<Vehiculo> findByCategoriaDescripcion(String desc) {
        return em.createQuery(
                        "SELECT v FROM Vehiculo v WHERE v.categoria.descripcion = :desc", Vehiculo.class)
                .setParameter("desc", desc)
                .getResultList();
    }

    public List<Vehiculo> findByEstado(String estado) {
        return em.createQuery(
                        "SELECT v FROM Vehiculo v " +
                                "WHERE LOWER(v.estado) = :estado " +
                                "ORDER BY v.marca, v.modelo", Vehiculo.class)
                .setParameter("estado", estado.toLowerCase())
                .getResultList();
    }

    public List<Vehiculo> findDisponibles()   { return findByEstado("disponible"); }
    public List<Vehiculo> findMantenimiento() { return findByEstado("mantenimiento"); }
    public List<Vehiculo> findRentados()      { return findByEstado("rentado"); }

    // --------- Mapper ---------
    private Vehiculo toEntity(VehiculoDto dto) {
        Vehiculo v = new Vehiculo();
        v.setPlaca(dto.getPlaca());
        v.setModelo(dto.getModelo());
        v.setMarca(dto.getMarca());
        v.setEstado(dto.getEstado());
        v.setAnio(dto.getAnio());
        v.setPrecio(dto.getPrecio());
        v.setImage(dto.getImage());
        v.setImageName(dto.getImageName());
        if (dto.getCategoria() != null && dto.getCategoria().getCodigo() != null) {
            CategoriaVehiculo cat = em.find(CategoriaVehiculo.class, dto.getCategoria().getCodigo());
            v.setCategoria(cat);
        }
        return v;
    }
}
