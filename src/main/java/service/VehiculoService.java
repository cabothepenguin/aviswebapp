package service;

import dto.VehiculoDto;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import model.Vehiculo;
import repository.VehiculoRepository;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Stateless
public class VehiculoService {


    private static final Set<String> ESTADOS_VALIDOS =
            Set.of("disponible", "rentado", "mantenimiento");

    @Inject
    private VehiculoRepository repository;

    /* ================== CREAR ================== */
    public void crear(VehiculoDto dto) {
        validarObligatorios(dto);

        if (repository.existsByPlaca(dto.getPlaca())) {
            throw new IllegalArgumentException("Ya existe un vehículo con la placa: " + dto.getPlaca());
        }
        repository.addVehiculo(dto);
    }

    /* ================== ACTUALIZAR ================== */
    public void actualizar(VehiculoDto dto) {
        if (dto.getPlaca() == null || dto.getPlaca().isBlank()) {
            throw new IllegalArgumentException("La placa es obligatoria para actualizar.");
        }
        validarObligatorios(dto);  // valida resto de campos

        if (!repository.existsByPlaca(dto.getPlaca())) {
            throw new IllegalArgumentException("No existe un vehículo con la placa: " + dto.getPlaca());
        }
        repository.updateVehiculo(dto);
    }

    /* ================== ELIMINAR ================== */
    public void eliminar(String placa) {
        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("La placa es obligatoria para eliminar.");
        }
        repository.deleteVehiculo(placa);
    }

    /* ================== CONSULTAS ================== */
    public List<Vehiculo> listar() {
        return repository.getVehiculos();
    }

    public Optional<Vehiculo> buscarPorPlaca(String placa) {
        if (placa == null || placa.isBlank()) return Optional.empty();
        try {
            return Optional.ofNullable(repository.findVehiculoByPlaca(placa));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean existePlaca(String placa) {
        return placa != null && !placa.isBlank() && repository.existsByPlaca(placa);
    }

    /* ================== VALIDACIONES ================== */
    private void validarObligatorios(VehiculoDto v) {
        if (v == null) throw new IllegalArgumentException("El vehículo es requerido.");

        // placa / modelo / marca
        if (v.getPlaca() == null || v.getPlaca().isBlank())
            throw new IllegalArgumentException("La placa es obligatoria.");
        if (v.getModelo() == null || v.getModelo().isBlank())
            throw new IllegalArgumentException("El modelo es obligatorio.");
        if (v.getMarca() == null || v.getMarca().isBlank())
            throw new IllegalArgumentException("La marca es obligatoria.");

        // categoría (FK)
        if (v.getCategoria() == null || v.getCategoria().getCodigo() == null)
            throw new IllegalArgumentException("La categoría es obligatoria.");


        // estado (opcional: valida contra los valores de tu ENUM de BD)
        if (v.getEstado() == null || v.getEstado().isBlank())
            throw new IllegalArgumentException("El estado es obligatorio.");


        if (v.getEstado() == null || v.getEstado().isBlank())
            throw new IllegalArgumentException("El estado es obligatorio.");
        if (!ESTADOS_VALIDOS.contains(v.getEstado().toLowerCase())) {
            throw new IllegalArgumentException(
                    "Estado inválido. Solo se permiten: " + ESTADOS_VALIDOS
            );
        }


        // año y precio
        if (v.getAnio() == null) throw new IllegalArgumentException("El año es obligatorio.");
        int current = Year.now().getValue() + 1; // permitimos próximo año
        if (v.getAnio() < 1900 || v.getAnio() > current)
            throw new IllegalArgumentException("El año debe estar entre 1900 y " + current + ".");

        if (v.getPrecio() == null) throw new IllegalArgumentException("El precio es obligatorio.");
        if (v.getPrecio() < 0) throw new IllegalArgumentException("El precio no puede ser negativo.");
    }
}
