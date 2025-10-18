package service;

import dto.CategoriaVehiculoDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import model.CategoriaVehiculo;
import repository.CategoriaRepository;

import java.util.List;
import java.util.Objects;

/**
 * Capa de servicios para operaciones de negocio sobre categorías.
 * Valida duplicados y normaliza entradas.
 */
@ApplicationScoped
public class CategoriaService {

    @Inject
    private CategoriaRepository repository;

    /** Crea una nueva categoría validando descripción obligatoria y duplicado (case-insensitive). */
    public void createCategoria(CategoriaVehiculoDto dto) {
        requireDto(dto);
        normalize(dto);
        if (isBlank(dto.getDescripcion())) {
            throw new IllegalArgumentException("La descripción de la categoría es obligatoria.");
        }
        // Duplicado por descripción (case-insensitive)
        CategoriaVehiculo existente = repository.findCategoryByDescripcion(dto.getDescripcion());
        if (existente != null) {
            throw new IllegalArgumentException("Ya existe una categoría con la descripción: " + dto.getDescripcion());
        }
        repository.addCategory(dto);
    }

    /** Actualiza validando existencia por código y conflicto de descripción. */
    public void updateCategoria(CategoriaVehiculoDto dto) {
        requireDto(dto);
        if (dto.getCodigo() == null) {
            throw new IllegalArgumentException("El código es obligatorio para actualizar la categoría.");
        }
        normalize(dto);

        CategoriaVehiculo actual = repository.findCategoryById(dto.getCodigo());
        if (actual == null) {
            throw new IllegalArgumentException("La categoría con código " + dto.getCodigo() + " no existe.");
        }
        if (!isBlank(dto.getDescripcion())) {
            CategoriaVehiculo otra = repository.findCategoryByDescripcion(dto.getDescripcion());
            if (otra != null && !otra.getCodigo().equals(dto.getCodigo())) {
                throw new IllegalArgumentException("Ya existe otra categoría con la descripción: " + dto.getDescripcion());
            }
        }
        repository.updateCategory(dto);
    }

    /** Elimina una categoría por código. */
    public void deleteCategoria(Integer codigo) {
        if (codigo == null) throw new IllegalArgumentException("El código es obligatorio para eliminar.");
        repository.deleteCategory(codigo);
    }

    /** Mapea entidad a DTO. */
    public CategoriaVehiculoDto toDto(CategoriaVehiculo model) {
        if (model == null) return null;
        CategoriaVehiculoDto dto = new CategoriaVehiculoDto();
        dto.setCodigo(model.getCodigo());
        dto.setDescripcion(model.getDescripcion());
        dto.setEstado(model.getEstado());
        return dto;
    }

    /** Lista todas las categorías. */
    public List<CategoriaVehiculo> listarCategorias() { return repository.getCategories(); }

    /** Busca por descripción. */
    public CategoriaVehiculo getByDescripcion(String desc) {
        if (isBlank(desc)) return null;
        return repository.findCategoryByDescripcion(desc.trim());
    }

    /** Obtiene por id. */
    public CategoriaVehiculo getById(Integer id) { return repository.getById(id); }

    /** Alias. */
    public CategoriaVehiculo findById(Integer id) { return repository.findCategoryById(id); }

    /** Actualiza entidad administrada (vía merge). */
    public void updateCategoria(CategoriaVehiculo categoria) {
        if (categoria == null || categoria.getCodigo() == null) {
            throw new IllegalArgumentException("Categoría/código requeridos para actualizar.");
        }
        repository.update(categoria);
    }

    /** Alias de {@link #listarCategorias()}. */
    public List<CategoriaVehiculo> getCategorias() { return listarCategorias(); }

    // ---- helpers ----
    private void requireDto(CategoriaVehiculoDto dto) { Objects.requireNonNull(dto, "El DTO de categoría no puede ser nulo"); }
    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private void normalize(CategoriaVehiculoDto dto) {
        if (dto.getDescripcion() != null) dto.setDescripcion(dto.getDescripcion().trim());
        if (dto.getEstado() != null) dto.setEstado(dto.getEstado().trim());
    }
}
