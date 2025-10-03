package service;

import dto.VehiculoDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
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


    public void addVehiculo(VehiculoDto dto) {
        try {
            repository.addVehiculo(dto);
        } catch (Exception e) {
            throw new RuntimeException("No se puede agregar el vehiculo", e);

        }
    }


    public void actualizar(VehiculoDto dto) {
        if (dto.getPlaca() == null) {
            throw new IllegalArgumentException("La placa es obligatoria para actualizar.");
        }
        validarObligatorios(dto);

        if (!repository.existsByPlaca(dto.getPlaca())) {
            throw new IllegalArgumentException("No existe un vehículo con la placa: " + dto.getPlaca());
        }
        repository.updateVehiculo(dto);
    }




    public void eliminar(String placa) {  // Cambiar de Integer a String
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("La placa es obligatoria para eliminar.");
        }

        // Verificamos que el vehículo exista antes de eliminar
        if (!repository.existsByPlaca(placa)) {
            throw new IllegalArgumentException("No existe un vehículo con la placa: " + placa);
        }

        repository.deleteVehiculo(placa);
    }


    private VehiculoDto toDto(Vehiculo model) {
        VehiculoDto dto = new VehiculoDto();
        dto.setPlaca(model.getPlaca());
        dto.setModelo(model.getModelo());
        dto.setMarca(model.getMarca());
        dto.setEstado(model.getEstado());
        dto.setAnio(model.getAnio());
        dto.setPrecio(model.getPrecio());
        dto.setCategoria(model.getCategoria()); // 🔑 Muy importante
        if (model.getImage() != null) {
            dto.setImage(model.getImage());
            dto.setImageName(model.getImageName());
        }
        return dto;
    }

    private Vehiculo toEntity(VehiculoDto dto) {
        Vehiculo v = new Vehiculo();
        v.setPlaca(dto.getPlaca());
        v.setModelo(dto.getModelo());
        v.setMarca(dto.getMarca());
        v.setEstado(dto.getEstado());
        v.setAnio(dto.getAnio());
        v.setPrecio(dto.getPrecio());
        v.setCategoria(dto.getCategoria()); // 🔑 Muy importante
        v.setImage(dto.getImage());
        v.setImageName(dto.getImageName());
        return v;
    }


    /* ================== CONSULTAS ================== */


    public List<VehiculoDto> listar() {
        return repository.getVehiculos()
                .stream()
                .map(this::toDto)
                .toList();
    }



    public Optional<VehiculoDto> buscarPorPlaca(String placa) {
        if (placa == null) return Optional.empty();
        return Optional.ofNullable(repository.findVehiculoByPlaca(placa))
                .map(this::toDto);
    }



    public boolean existePlaca(String placa) {  // Cambiar de Integer a String
        return placa != null && repository.existsByPlaca(placa);
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

        // estado (si es null, se pone "disponible" por defecto)
        if (v.getEstado() == null || v.getEstado().isBlank()) {
            v.setEstado("disponible");
        } else if (!ESTADOS_VALIDOS.contains(v.getEstado().toLowerCase())) {
            throw new IllegalArgumentException(
                    "Estado inválido. Solo se permiten: " + ESTADOS_VALIDOS
            );
        }

        // año y precio
        if (v.getAnio() == null) throw new IllegalArgumentException("El año es obligatorio.");
        int current = Year.now().getValue() + 1;
        if (v.getAnio() < 1900 || v.getAnio() > current)
            throw new IllegalArgumentException("El año debe estar entre 1900 y " + current + ".");

        if (v.getPrecio() == null) throw new IllegalArgumentException("El precio es obligatorio.");
        if (v.getPrecio() < 0) throw new IllegalArgumentException("El precio no puede ser negativo.");
    }

    public VehiculoDto findVehicleByPlaca(String placa) {  // Cambiar de Integer a String
        return toDto(repository.findVehiculoByPlaca(placa));
    }
}