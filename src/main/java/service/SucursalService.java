package service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import model.Sucursal;
import repository.SucursalRepository;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio para sucursales.
 * Valida obligatorios, normaliza nombre y controla duplicados por nombre.
 */
@ApplicationScoped
public class SucursalService {

    @Inject
    private SucursalRepository repository;

    /** Agrega una sucursal validando obligatorios y nombre duplicado. */
    public void add(Sucursal s) {
        validarObligatorios(s);
        normalize(s);
        if (repository.existsByNombre(s.getNombre())) {
            throw new IllegalArgumentException("Ya existe una sucursal con el nombre: " + s.getNombre());
        }
        repository.addSucursal(s);
    }

    /** Actualiza una sucursal validando datos y conflicto de nombre. */
    public void update(Sucursal s) {
        validarObligatorios(s);
        if (s.getCodigo() == null) {
            throw new IllegalArgumentException("El código es obligatorio para actualizar la sucursal.");
        }
        normalize(s);

        if (repository.existsByNombre(s.getNombre())) {
            Sucursal actual = repository.findSucursalById(s.getCodigo());
            if (actual == null || !actual.getNombre().equalsIgnoreCase(s.getNombre())) {
                throw new IllegalArgumentException("El nombre ya está en uso: " + s.getNombre());
            }
        }
        repository.updateSucursal(s);
    }

    /** Elimina una sucursal por su código. */
    public void eliminar(Integer codigo) {
        if (codigo == null) throw new IllegalArgumentException("El código es obligatorio para eliminar.");
        repository.deleteSucursal(codigo);
    }

    /** Lista todas las sucursales. */
    public List<Sucursal> listar() { return repository.getSucursales(); }

    /** Busca por id con Optional. */
    public Optional<Sucursal> buscarPorId(Integer codigo) {
        try { return Optional.ofNullable(repository.findSucursalById(codigo)); }
        catch (Exception e) { return Optional.empty(); }
    }

    /** Obtiene por id (alias). */
    public Sucursal getById(Integer id) { return repository.getById(id); }

    /** Busca por código con Optional. */
    public Optional<Sucursal> buscarPorCodigo(Integer codigo) {
        if (codigo == null) return Optional.empty();
        try { return Optional.ofNullable(repository.findByCodigo(codigo)); }
        catch (Exception e) { return Optional.empty(); }
    }

    // ---- helpers ----
    private void validarObligatorios(Sucursal s) {
        if (s == null) throw new IllegalArgumentException("La sucursal es requerida.");
        if (s.getNombre() == null || s.getNombre().trim().isEmpty())
            throw new IllegalArgumentException("El nombre de la sucursal es obligatorio.");
    }
    private void normalize(Sucursal s) {
        if (s.getNombre() != null) s.setNombre(s.getNombre().trim());
    }
}
