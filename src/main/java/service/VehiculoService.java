package service;

import dto.VehiculoDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import model.Vehiculo;
import repository.VehiculoRepository;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class VehiculoService {

    private static final Set<String> ESTADOS_VALIDOS =
            Set.of("disponible", "rentado", "mantenimiento");

    @Inject
    private VehiculoRepository repository;

    /* ===== Consultas por categoría ===== */
    public List<Vehiculo> listarPorCategoria(Integer codigoCategoria) {
        if (codigoCategoria == null) return List.of();
        return repository.findByCategoriaCodigo(codigoCategoria);
    }

    public List<Vehiculo> listarPorCategoriaDesc(String descripcion) {
        return repository.findByCategoriaDescripcion(descripcion);
    }

    /* ===== CRUD ===== */
    public void addVehiculo(VehiculoDto dto) {
        try { repository.addVehiculo(dto); }
        catch (Exception e) { throw new RuntimeException("No se puede agregar el vehículo", e); }
    }

    public void actualizar(VehiculoDto dto) {
        if (dto.getPlaca() == null) throw new IllegalArgumentException("La placa es obligatoria para actualizar.");
        validarObligatorios(dto);
        if (!repository.existsByPlaca(dto.getPlaca())) {
            throw new IllegalArgumentException("No existe un vehículo con la placa: " + dto.getPlaca());
        }
        repository.updateVehiculo(dto);
    }

    public void eliminar(String placa) {
        if (placa == null || placa.trim().isEmpty())
            throw new IllegalArgumentException("La placa es obligatoria para eliminar.");
        if (!repository.existsByPlaca(placa))
            throw new IllegalArgumentException("No existe un vehículo con la placa: " + placa);
        repository.deleteVehiculo(placa);
    }

    public List<VehiculoDto> listar() {
        return repository.getVehiculos().stream().map(this::toDto).toList();
    }

    public Optional<VehiculoDto> buscarPorPlaca(String placa) {
        if (placa == null) return Optional.empty();
        return Optional.ofNullable(repository.findVehiculoByPlaca(placa)).map(this::toDto);
    }

    public Optional<Vehiculo> buscarEntidadPorPlaca(String placa) {
        if (placa == null || placa.isBlank()) return Optional.empty();
        try { return Optional.ofNullable(repository.findVehiculoByPlaca(placa)); }
        catch (Exception e) { return Optional.empty(); }
    }

    public boolean existePlaca(String placa) {
        return placa != null && repository.existsByPlaca(placa);
    }

    /* ===== Mapping ===== */
    private VehiculoDto toDto(Vehiculo model) {
        VehiculoDto dto = new VehiculoDto();
        dto.setPlaca(model.getPlaca());
        dto.setModelo(model.getModelo());
        dto.setMarca(model.getMarca());
        dto.setEstado(model.getEstado());
        dto.setAnio(model.getAnio());
        dto.setPrecio(model.getPrecio());
        dto.setCategoria(model.getCategoria());
        if (model.getImage() != null) {
            dto.setImage(model.getImage());
            dto.setImageName(model.getImageName());
        }
        return dto;
    }

    /* ===== Validaciones ===== */
    private void validarObligatorios(VehiculoDto v) {
        if (v == null) throw new IllegalArgumentException("El vehículo es requerido.");
        if (v.getPlaca() == null || v.getPlaca().isBlank())
            throw new IllegalArgumentException("La placa es obligatoria.");
        if (v.getModelo() == null || v.getModelo().isBlank())
            throw new IllegalArgumentException("El modelo es obligatorio.");
        if (v.getMarca() == null || v.getMarca().isBlank())
            throw new IllegalArgumentException("La marca es obligatoria.");
        if (v.getCategoria() == null || v.getCategoria().getCodigo() == null)
            throw new IllegalArgumentException("La categoría es obligatoria.");

        if (v.getEstado() == null || v.getEstado().isBlank()) {
            v.setEstado("disponible");
        } else if (!ESTADOS_VALIDOS.contains(v.getEstado().toLowerCase())) {
            throw new IllegalArgumentException("Estado inválido. Solo se permiten: " + ESTADOS_VALIDOS);
        }

        if (v.getAnio() == null) throw new IllegalArgumentException("El año es obligatorio.");
        int current = Year.now().getValue() + 1;
        if (v.getAnio() < 1900 || v.getAnio() > current)
            throw new IllegalArgumentException("El año debe estar entre 1900 y " + current + ".");

        if (v.getPrecio() == null) throw new IllegalArgumentException("El precio es obligatorio.");
        if (v.getPrecio() < 0) throw new IllegalArgumentException("El precio no puede ser negativo.");
    }

    public VehiculoDto findVehicleByPlaca(String placa) {
        return toDto(repository.findVehiculoByPlaca(placa));
    }

    // VehiculoService.java
// ...
    // VehiculoService.java
    public List<VehiculoDto> listarPorEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return listar(); // todos
        }
        String normalized = estado.trim().toLowerCase();

        if (!ESTADOS_VALIDOS.contains(normalized)) {
            throw new IllegalArgumentException(
                    "Estado inválido. Solo se permiten: " + ESTADOS_VALIDOS
            );
        }

        return repository.findByEstado(normalized)
                .stream()
                .map(this::toDto)
                .toList();
    }


    // Atajos (opcional)
    public List<VehiculoDto> listarDisponibles()   { return listarPorEstado("disponible"); }
    public List<VehiculoDto> listarMantenimiento() { return listarPorEstado("mantenimiento"); }
    public List<VehiculoDto> listarRentados()      { return listarPorEstado("rentado"); }

}
