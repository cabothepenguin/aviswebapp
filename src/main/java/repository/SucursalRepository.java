package repository;

import dto.SucursalDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import model.Sucursal;

import java.util.List;

@ApplicationScoped
public class SucursalRepository {

    @PersistenceContext(unitName = "avisrent-pu")
    private EntityManager em;

    // agregar


    public void addSucursal(Sucursal sucursal) {
        em.getTransaction().begin();
        String sql = "INSERT INTO administracion_sucursales (nombre, encargado, direccion, telefono, correo) " +
                "VALUES (?, ?, ?, ?, ?)";
        em.createNativeQuery(sql)
                .setParameter(1, sucursal.getNombre())
                .setParameter(2, sucursal.getEncargado())
                .setParameter(3, sucursal.getDireccion())
                .setParameter(4, sucursal.getTelefono())
                .setParameter(5, sucursal.getCorreo())
                .executeUpdate();
        em.getTransaction().commit();
    }

    //----Actualizar por codigo

    public void updateSucursal(Sucursal sucursal) {
         em.getTransaction().begin();
        String sql = "UPDATE administracion_sucursales SET nombre = ?, encargado = ?, direccion = ?, telefono = ?, correo = ? " +
                "WHERE codigo = ?";
        em.createNativeQuery(sql)
                .setParameter(1, sucursal.getNombre())
                .setParameter(2, sucursal.getEncargado())
                .setParameter(3, sucursal.getDireccion())
                .setParameter(4, sucursal.getTelefono())
                .setParameter(5, sucursal.getCorreo())
                .setParameter(6, sucursal.getCodigo())
                .executeUpdate();
        em.getTransaction().commit();

    }

    // eliminar por codigo

    public void deleteSucursal(Integer codigo) {
        em.getTransaction().begin();
        em.createNativeQuery("DELETE FROM administracion_sucursales WHERE codigo = ?")
                .setParameter(1, codigo)
                .executeUpdate();
        em.getTransaction().commit();

    }

    // ===== Listar todas (SELECT) =====
    @SuppressWarnings("unchecked")
    public List<Sucursal> getSucursales() {
        return em.createNativeQuery(
                "SELECT codigo, nombre, encargado, direccion, telefono, correo FROM administracion_sucursales",
                Sucursal.class
        ).getResultList();
    }

    // ===== Buscar por ID (SELECT) =====
    public Sucursal findSucursalById(Integer codigo) {
        return (Sucursal) em.createNativeQuery(
                "SELECT codigo, nombre, encargado, direccion, telefono, correo FROM administracion_sucursales WHERE codigo = ?",
                Sucursal.class
        ).setParameter(1, codigo).getSingleResult();
    }

    // ===== Verificar existencia por nombre
    public boolean existsByNombre(String nombre) {
        Number count = (Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM administracion_sucursales WHERE nombre = ?"
        ).setParameter(1, nombre).getSingleResult();
        return count != null && count.intValue() > 0;
    }

    public Sucursal getById(Integer id) {
        return em.find(Sucursal.class, id);
    }

    @Transactional
    public void update(Sucursal sucursal) {
        em.merge(sucursal);
    }

    public Sucursal findByCodigo(Integer codigo) {
        return em.find(Sucursal.class, codigo);
    }
}
