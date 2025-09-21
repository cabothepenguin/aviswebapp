package service;

import dto.RentasDto;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import model.Renta;
import repository.RentaRepository;


import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Stateless
public class RentaService {

    private static final Set<String> ESTADOS_VALIDOS =
            Set.of("activa", "finalizada", "cancelada");

    @Inject
    private RentaRepository repository;

    /* ================== CREAR ================== */
    public void crear(RentasDto dto) {
        validar(dto, false);
        repository.addRenta(dto);
    }

    /* ================== ACTUALIZAR ================== */
    public void actualizar(RentasDto dto) {
        if (dto.getNumeroRenta() == null) {
            throw new IllegalArgumentException("numeroRenta es obligatorio para actualizar.");
        }
        validar(dto, true);


        if (!repository.existsBynumeroRenta(dto.getNumeroRenta())) {
            throw new IllegalArgumentException("No existe la renta #" + dto.getNumeroRenta());
        }

        repository.updateRenta(dto);
    }

    /* ================== ELIMINAR ================== */
    public void eliminar(int numeroRenta) {
        if (!repository.existsBynumeroRenta(numeroRenta)) {
            throw new IllegalArgumentException("No existe la renta #" + numeroRenta);
        }
        repository.deleteRenta(numeroRenta);
    }

    /* ================== CONSULTAS ================== */
    public List<Renta> listar() {
        return repository.getRenta();
    }

    public Optional<Renta> buscarPorNumero(int numeroRenta) {
        try {
            return Optional.ofNullable(repository.findRentaByNumeroRenta(numeroRenta));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean existe(int numeroRenta) {
        return repository.existsBynumeroRenta(numeroRenta);
    }

    /* ================== VALIDACIONES ================== */
    private void validar(RentasDto dto, boolean esActualizacion) {
        if (dto == null) throw new IllegalArgumentException("La renta es requerida.");

        if (dto.getClienteNombre() == null || dto.getClienteNombre().isBlank())
            throw new IllegalArgumentException("clienteNombre es obligatorio.");

        if (dto.getVehiculo() == null || dto.getVehiculo().getPlaca() == null
                || dto.getVehiculo().getPlaca().isBlank())
            throw new IllegalArgumentException("vehiculoAsignado (placa) es obligatorio.");

        if (dto.getSucursal() == null || dto.getSucursal().getCodigo() == null)
            throw new IllegalArgumentException("sucursal es obligatoria.");

        validarFechas(dto.getFechaInicio(), dto.getFechaFin());

        if (dto.getPrecioTotal() < 0)
            throw new IllegalArgumentException("precioTotal no puede ser negativo.");

        if (dto.getEstado() == null || dto.getEstado().isBlank()
                || !ESTADOS_VALIDOS.contains(dto.getEstado().toLowerCase())) {
            throw new IllegalArgumentException("estado inválido. Valores permitidos: " + ESTADOS_VALIDOS);
        }
    }

    private void validarFechas(Date inicio, Date fin) {
        if (inicio == null) throw new IllegalArgumentException("fechaInicio es obligatoria.");
        if (fin == null) throw new IllegalArgumentException("fechafin es obligatoria.");
        if (inicio.after(fin))
            throw new IllegalArgumentException("fechaInicio no puede ser posterior a fechafin.");
    }
}
