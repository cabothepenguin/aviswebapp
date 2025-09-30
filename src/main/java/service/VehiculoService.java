package service;

import dto.VehiculoDto;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.view.ViewScoped;
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


    public void addVehiculo(VehiculoDto vehiculoDto) {
        try{
            repository.addVehiculo(vehiculoDto);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /* ================== ACTUALIZAR ================== */
    public void actualizar(VehiculoDto dto) {
        if (dto.getPlaca() == null) {
            throw new IllegalArgumentException("La placa es obligatoria para actualizar.");
        }
        validarObligatorios(dto);  // valida resto de campos

        if (!repository.existsByPlaca(dto.getPlaca())) {
            throw new IllegalArgumentException("No existe un vehículo con la placa: " + dto.getPlaca());
        }
        repository.updateVehiculo(dto);
    }

    /* ================== ELIMINAR ================== */
    public void eliminar(Integer placa) {
        if (placa == null ) {
            throw new IllegalArgumentException("La placa es obligatoria para eliminar.");
        }
        repository.deleteVehiculo(placa);
    }


    private VehiculoDto toDto(Vehiculo model) {
        VehiculoDto vehiculosDto = new VehiculoDto();
        vehiculosDto.setPlaca(model.getPlaca());
        vehiculosDto.setModelo(model.getModelo());
        vehiculosDto.setMarca(model.getMarca());
        vehiculosDto.setEstado(model.getEstado());
        vehiculosDto.setAnio(Integer.valueOf(model.getAnio()));
        vehiculosDto.setPrecio(Integer.valueOf(model.getPrecio()));
        if(model.getImage() != null) {
            vehiculosDto.setImage(model.getImage());
            vehiculosDto.setImageName(model.getImageName());
        }
        return vehiculosDto;
    }


    /* ================== CONSULTAS ================== */
    public List<Vehiculo> listar() {
        return repository.getVehiculos();
    }

    public Optional<Vehiculo> buscarPorPlaca(Integer placa) {
        if (placa == null ) return Optional.empty();
        try {
            return Optional.ofNullable(repository.findVehiculoByPlaca(placa));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean existePlaca(Integer placa) {
        return placa != null  && repository.existsByPlaca(placa);
    }

    /* ================== VALIDACIONES ================== */
    private void validarObligatorios(VehiculoDto v) {
        if (v == null) throw new IllegalArgumentException("El vehículo es requerido.");

        // placa / modelo / marca
        if (v.getPlaca() == null )
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

    public VehiculoDto findVehicleByPlaca(Integer id) {
        return toDto(repository.findVehiculoByPlaca(id));
    }
}
