package service;

import dto.RentasDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import model.Renta;
import repository.RentaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Servicio de negocio para la gestión de rentas.
 * Valida estados, fechas, montos y existencia; normaliza estado.
 */
@ApplicationScoped
public class RentaService {

    private static final Set<String> ESTADOS_VALIDOS = Set.of("activa", "finalizada", "cancelada");

    @Inject
    private RentaRepository repository;

    /** Crea una renta luego de validar reglas de negocio. */
    public void crear(RentasDto dto) {
        validar(dto, false);
        normalize(dto);
        repository.addRenta(dto);
    }

    /** Actualiza una renta validando existencia previa y reglas de negocio. */
    public void actualizar(RentasDto dto) {
        if (dto == null || dto.getNumeroRenta() == null) {
            throw new IllegalArgumentException("numeroRenta es obligatorio para actualizar.");
        }
        validar(dto, true);
        normalize(dto);
        if (!repository.existsBynumeroRenta(dto.getNumeroRenta())) {
            throw new IllegalArgumentException("No existe la renta #" + dto.getNumeroRenta());
        }
        repository.updateRenta(dto);
    }

    /** Elimina una renta por su número validando existencia. */
    public void eliminar(int numeroRenta) {
        if (!repository.existsBynumeroRenta(numeroRenta)) {
            throw new IllegalArgumentException("No existe la renta #" + numeroRenta);
        }
        repository.deleteRenta(numeroRenta);
    }

    /** Lista todas las rentas. */
    public List<Renta> listar() { return repository.getRenta(); }

    /** Busca una renta por su número. */
    public Optional<Renta> buscarPorNumero(int numeroRenta) {
        try {
            return Optional.ofNullable(repository.findRentaByNumeroRenta(numeroRenta));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /** Lista rentas filtradas por estado (valida valor). */
    public List<Renta> listarPorEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException("Debe indicar un estado válido (activa, finalizada o cancelada).");
        }
        return repository.findByEstado(estado.trim().toLowerCase());
    }

    // ---- validaciones ----
    private void validar(RentasDto dto, boolean esActualizacion) {
        if (dto == null) throw new IllegalArgumentException("La renta es requerida.");
        if (isBlank(dto.getClienteNombre())) throw new IllegalArgumentException("clienteNombre es obligatorio.");
        if (isBlank(dto.getVehiculoPlaca())) throw new IllegalArgumentException("vehiculo (placa) es obligatorio.");
        if (dto.getSucursalCodigo() == null) throw new IllegalArgumentException("sucursal es obligatoria.");
        validarFechas(dto.getFechaInicio(), dto.getFechaFin());
        if (dto.getPrecioTotal() == null || dto.getPrecioTotal() < 0)
            throw new IllegalArgumentException("precioTotal no puede ser negativo.");
        if (isBlank(dto.getEstado()) || !ESTADOS_VALIDOS.contains(dto.getEstado().trim().toLowerCase())) {
            throw new IllegalArgumentException("estado inválido. Valores permitidos: " + ESTADOS_VALIDOS);
        }
    }

    private void validarFechas(Date inicio, Date fin) {
        if (inicio == null) throw new IllegalArgumentException("fechaInicio es obligatoria.");
        if (fin == null) throw new IllegalArgumentException("fechafin es obligatoria.");
        if (inicio.after(fin)) throw new IllegalArgumentException("fechaInicio no puede ser posterior a fechafin.");
    }

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private void normalize(RentasDto dto) {
        if (dto.getClienteNombre() != null) dto.setClienteNombre(dto.getClienteNombre().trim());
        if (dto.getVehiculoPlaca() != null) dto.setVehiculoPlaca(dto.getVehiculoPlaca().trim());
        if (dto.getEstado() != null) dto.setEstado(dto.getEstado().trim().toLowerCase());
    }
}
