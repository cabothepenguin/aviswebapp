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

/**
 * Servicio de negocio para vehículos.
 * <p>Valida obligatorios, rangos y estados permitidos; provee mapping DTO.</p>
 */
@ApplicationScoped
public class VehiculoService {

    /** Estados válidos admitidos por el sistema. */
    private static final Set<String> ESTADOS_VALIDOS =
            Set.of("disponible", "rentado", "mantenimiento");

    @Inject
    private VehiculoRepository repository;

    // -------- Consultas por categoría --------

    /** Lista vehículos por código de categoría. */
    public List<Vehiculo> listarPorCategoria(Integer codigoCategoria) {
        if (codigoCategoria == null) return List.of();
        return repository.findByCategoriaCodigo(codigoCategoria);
    }

    /** Lista vehículos por descripción de categoría. */
    public List<Vehiculo> listarPorCategoriaDesc(String descripcion) {
        return repository.findByCategoriaDescripcion(descripcion);
    }

    // -------- CRUD --------

    /** Crea un vehículo delegando al repositorio. */
    public void addVehiculo(VehiculoDto dto) {
        try { repository.addVehiculo(dto); }
        catch (Exception e) { throw new RuntimeException("No se puede agregar el vehículo", e); }
    }

    /**
     * Actualiza un vehículo validando existencia por placa y campos obligatorios.
     * @throws IllegalArgumentException si la placa no existe o los datos son inválidos.
     */
    public void actualizar(VehiculoDto dto) {
        if (dto.getPlaca() == null) throw new IllegalArgumentException("La placa es obligatoria para actualizar.");
        validarObligatorios(dto);
        if (!repository.existsByPlaca(dto.getPlaca())) {
            throw new IllegalArgumentException("No existe un vehículo con la placa: " + dto.getPlaca());
        }
        repository.updateVehiculo(dto);
    }

    /** Elimina un vehículo por su placa (valida existencia). */
    public void eliminar(String placa) {
        if (placa == null || placa.trim().isEmpty())
            throw new IllegalArgumentException("La placa es obligatoria para eliminar.");
        if (!repository.existsByPlaca(placa))
            throw new IllegalArgumentException("No existe un vehículo con la placa: " + placa);
        repository.deleteVehiculo(placa);
    }

    /** Lista vehículos como DTOs de presentación. */
    public List<VehiculoDto> listar() {
        return repository.getVehiculos().stream().map(this::toDto).toList();
    }

    /** Busca vehículo por placa como DTO. */
    public Optional<VehiculoDto> buscarPorPlaca(String placa) {
        if (placa == null) return Optional.empty();
        return Optional.ofNullable(repository.findVehiculoByPlaca(placa)).map(this::toDto);
    }

    /** Retorna la entidad por placa (cuando se necesita la entidad real). */
    public Optional<Vehiculo> buscarEntidadPorPlaca(String placa) {
        if (placa == null || placa.isBlank()) return Optional.empty();
        try { return Optional.ofNullable(repository.findVehiculoByPlaca(placa)); }
        catch (Exception e) { return Optional.empty(); }
    }

    /** Verifica existencia por placa. */
    public boolean existePlaca(String placa) { return placa != null && repository.existsByPlaca(placa); }

    // -------- Mapping --------

    /** Convierte entidad {@link Vehiculo} a {@link VehiculoDto}. */
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

    // -------- Validaciones --------

    /**
     * Valida campos obligatorios y rangos de {@link VehiculoDto}.
     * <ul>
     *   <li>Placa, modelo, marca y categoría obligatorios.</li>
     *   <li>Estado por defecto: {@code disponible} si viene vacío.</li>
     *   <li>Año entre 1900 y año actual + 1.</li>
     *   <li>Precio no negativo.</li>
     * </ul>
     */
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

    /** Devuelve un DTO a partir de la placa (atajo para vistas/converters). */
    public VehiculoDto findVehicleByPlaca(String placa) {
        return toDto(repository.findVehiculoByPlaca(placa));
    }

    // -------- Filtros por estado --------

    /**
     * Lista vehículos por estado (o todos si el estado es vacío).
     * @param estado uno de {@code disponible | mantenimiento | rentado}.
     */
    public List<VehiculoDto> listarPorEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return listar();
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

    // Atajos
    public List<VehiculoDto> listarDisponibles()   { return listarPorEstado("disponible"); }
    public List<VehiculoDto> listarMantenimiento() { return listarPorEstado("mantenimiento"); }
    public List<VehiculoDto> listarRentados()      { return listarPorEstado("rentado"); }
}
