package service;

import dto.CategoriaVehiculoDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import model.CategoriaVehiculo;
import repository.CategoriaRepository;

import java.util.List;

/**
 * Capa de servicios para operaciones de negocio sobre categorías.
 * <p>Valida reglas mínimas (duplicados) y delega al repositorio.</p>
 */
@ApplicationScoped
public class CategoriaService {

    @Inject
    private CategoriaRepository repository;

    /**
     * Crea una nueva categoría validando duplicado por descripción.
     * @throws IllegalArgumentException si ya existe la descripción.
     */
    public void createCategoria(CategoriaVehiculoDto dto) {
        if (repository.existsByDescripcion(dto.getDescripcion())) {
            throw new IllegalArgumentException(
                    "Ya existe una categoría con la descripción: " + dto.getDescripcion()
            );
        }
        repository.addCategory(dto);
    }

    /**
     * Actualiza la categoría validando existencia y duplicado en descripción.
     * @throws IllegalArgumentException si no existe o hay conflicto de descripción.
     */
    public void updateCategoria(CategoriaVehiculoDto dto) {
        CategoriaVehiculo existente = repository.findCategoryById(dto.getCodigo());
        if (existente == null) {
            throw new IllegalArgumentException("La categoría con código " + dto.getCodigo() + " no existe.");
        }
        CategoriaVehiculo otra = repository.findCategoryByDescripcion(dto.getDescripcion());
        if (otra != null && !otra.getCodigo().equals(dto.getCodigo())) {
            throw new IllegalArgumentException(
                    "Ya existe otra categoría con la descripción: " + dto.getDescripcion()
            );
        }
        repository.updateCategory(dto);
    }

    /** Elimina una categoría por código. */
    public void deleteCategoria(Integer codigo) { repository.deleteCategory(codigo); }

    /** Mapea entidad a DTO. */
    public CategoriaVehiculoDto toDto(CategoriaVehiculo model) {
        CategoriaVehiculoDto categoriaVehiculoDto = new CategoriaVehiculoDto();
        categoriaVehiculoDto.setCodigo(model.getCodigo());
        categoriaVehiculoDto.setDescripcion(model.getDescripcion());
        categoriaVehiculoDto.setEstado(model.getEstado());
        return categoriaVehiculoDto;
    }

    /** Lista todas las categorías. */
    public List<CategoriaVehiculo> listarCategorias() { return repository.getCategories(); }

    /** Busca por descripción. */
    public CategoriaVehiculo getByDescripcion(String desc) { return repository.findCategoryByDescripcion(desc); }

    /** Obtiene por id. */
    public CategoriaVehiculo getById(Integer id) { return repository.getById(id); }

    /** Alias de {@link CategoriaRepository#findCategoryById(Integer)}. */
    public CategoriaVehiculo findById(Integer id) { return repository.findCategoryById(id); }

    /** Actualiza entidad administrada (vía merge). */
    public void updateCategoria(CategoriaVehiculo categoria) { repository.update(categoria); }

    /** Alias de {@link #listarCategorias()}. */
    public List<CategoriaVehiculo> getCategorias() { return listarCategorias(); }
}
