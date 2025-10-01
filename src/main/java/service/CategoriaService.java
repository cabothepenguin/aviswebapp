package service;

import dto.CategoriaVehiculoDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import model.CategoriaVehiculo;
import repository.CategoriaRepository;

import java.util.List;

@ApplicationScoped
public class CategoriaService {

    @Inject
    private CategoriaRepository repository;

    // ===== Crear categoría con validación =====
    public void createCategoria(CategoriaVehiculoDto dto) {
        if (repository.existsByDescripcion(dto.getDescripcion())) {
            throw new IllegalArgumentException(
                    "Ya existe una categoría con la descripción: " + dto.getDescripcion()
            );
        }
        repository.addCategory(dto);
    }

    // ===== Actualizar categoría con validación =====
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

    // ===== Otros métodos =====
    public void deleteCategoria(Integer codigo) {
        repository.deleteCategory(codigo);
    }

    public CategoriaVehiculoDto toDto(CategoriaVehiculo model) {
        CategoriaVehiculoDto categoriaVehiculoDto = new CategoriaVehiculoDto();
        categoriaVehiculoDto.setCodigo(model.getCodigo());
        categoriaVehiculoDto.setDescripcion(model.getDescripcion());
        categoriaVehiculoDto.setEstado(model.getEstado());
        return categoriaVehiculoDto;
    }

    public List<CategoriaVehiculo> listarCategorias() {
        return repository.getCategories();
    }

    public CategoriaVehiculo getByDescripcion(String desc) {
        return repository.findCategoryByDescripcion(desc);
    }

    public CategoriaVehiculo getById(Integer id) {
        return repository.getById(id);
    }

    public CategoriaVehiculo findById(Integer id) {
        return repository.findCategoryById(id);
    }

    public void updateCategoria(CategoriaVehiculo categoria) {
        repository.update(categoria);
    }

    public List<CategoriaVehiculo> getCategorias() {
        return listarCategorias();
    }

}