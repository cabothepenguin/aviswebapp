package service;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import model.Sucursal;
import repository.SucursalRepository;

import java.util.List;
import java.util.Optional;

@Stateless   // administra transacciones si las mueves al service
public class SucursalService {

    @Inject
    private SucursalRepository repository;

    //  (valida que no exista el nombre)
    public void add(Sucursal s) {
        validarObligatorios(s);
        if (repository.existsByNombre(s.getNombre())) {
            throw new IllegalArgumentException("Ya existe una sucursal con el nombre: " + s.getNombre());
        }
        repository.addSucursal(s);
    }
    //valida que el nombre no esté usado por otra sucursal
    // Actualizar (valida duplicado por nombre si cambió)
    public void update(Sucursal s) {
        validarObligatorios(s);

        if (repository.existsByNombre(s.getNombre())) {
            // podrías traer la actual y comparar ids para permitir mismo nombre en el mismo código
            Sucursal actual = repository.findSucursalById(s.getCodigo());
            if (actual == null || !actual.getNombre().equalsIgnoreCase(s.getNombre())) {
                throw new IllegalArgumentException("El nombre ya está en uso: " + s.getNombre());
            }
        }
        repository.updateSucursal(s);
    }

    // Eliminar
    public void eliminar(Integer codigo) {
        repository.deleteSucursal(codigo);
    }

    // Obtener todas
    public List<Sucursal> listar() {
        return repository.getSucursales();
    }

    // Buscar por ID (envolviendo en Optional)
    public Optional<Sucursal> buscarPorId(Integer codigo) {
        try {
            return Optional.ofNullable(repository.findSucursalById(codigo));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // Reglas mínimas de negocio / validaciones
    private void validarObligatorios(Sucursal s) {
        if (s == null) throw new IllegalArgumentException("La sucursal es requerida.");
        if (s.getNombre() == null || s.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre de la sucursal es obligatorio.");

    }



}
