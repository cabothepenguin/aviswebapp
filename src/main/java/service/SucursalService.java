package service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import model.Sucursal;
import repository.SucursalRepository;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio para sucursales.
 * <p>Aplica validaciones mínimas y controla duplicados por nombre.</p>
 */
@ApplicationScoped
public class SucursalService {

    @Inject
    private SucursalRepository repository;

    /**
     * Agrega una sucursal validando obligatorios y nombre duplicado.
     * @throws IllegalArgumentException si el nombre ya existe.
     */
    public void add(Sucursal s) {
        validarObligatorios(s);
        if (repository.existsByNombre(s.getNombre())) {
            throw new IllegalArgumentException("Ya existe una sucursal con el nombre: " + s.getNombre());
        }
        repository.addSucursal(s);
    }

    /**
     * Actualiza una sucursal validando datos y conflicto de nombre.
     * <p>Permite el mismo nombre si corresponde a la propia sucursal.</p>
     */
    public void update(Sucursal s) {
        validarObligatorios(s);
        if (repository.existsByNombre(s.getNombre())) {
            Sucursal actual = repository.findSucursalById(s.getCodigo());
            if (actual == null || !actual.getNombre().equalsIgnoreCase(s.getNombre())) {
                throw new IllegalArgumentException("El nombre ya está en uso: " + s.getNombre());
            }
        }
        repository.updateSucursal(s);
    }

    /** Elimina una sucursal por su código. */
    public void eliminar(Integer codigo) { repository.deleteSucursal(codigo); }

    /** Lista todas las sucursales. */
    public List<Sucursal> listar() { return repository.getSucursales(); }

    /** Busca por id con {@link Optional}. */
    public Optional<Sucursal> buscarPorId(Integer codigo) {
        try {
            return Optional.ofNullable(repository.findSucursalById(codigo));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /** Valida campos obligatorios mínimos. */
    private void validarObligatorios(Sucursal s) {
        if (s == null) throw new IllegalArgumentException("La sucursal es requerida.");
        if (s.getNombre() == null || s.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre de la sucursal es obligatorio.");
    }

    /** Obtiene por id (alias de repositorio). */
    public Sucursal getById(Integer id) { return repository.getById(id); }

    /** Busca por código con {@link Optional}. */
    public Optional<Sucursal> buscarPorCodigo(Integer codigo) {
        if (codigo == null) return Optional.empty();
        try {
            return Optional.ofNullable(repository.findByCodigo(codigo));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
