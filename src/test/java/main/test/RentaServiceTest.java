package service;

import dto.RentasDto;
import model.Sucursal;
import model.Vehiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import repository.RentaRepository;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RentaServiceTest {

    private RentaService service;
    private RentaRepository repo;

    @BeforeEach
    void setUp() {
        repo = mock(RentaRepository.class);
        service = new RentaService();
        // inyectamos el mock en el campo público
        service.repository = repo;
    }

    private RentasDto buildRentaValida() {
        RentasDto dto = new RentasDto();
        dto.setClienteNombre("John Doe");

        Vehiculo v = new Vehiculo();
        v.setPlaca("ABC123");
        dto.setVehiculo(v);

        Sucursal s = new Sucursal();
        s.setCodigo(10);
        dto.setSucursal(s);

        // fechas válidas: inicio <= fin
        Calendar cal = Calendar.getInstance();
        Date inicio = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 3);
        Date fin = cal.getTime();

        dto.setFechaInicio(inicio);
        dto.setFechaFin(fin);
        dto.setPrecioTotal(50000L);
        dto.setEstado("activa");
        return dto;
    }

    /** (1) crear() OK -> debe llamar repo.addRenta(dto) exactamente 1 vez */
    @Test
    void crear_ok_debeGuardar() {
        RentasDto dto = buildRentaValida();

        assertDoesNotThrow(() -> service.crear(dto));
        verify(repo, times(1)).addRenta(ArgumentMatchers.eq(dto));
    }

    /** (2) crear() con fechaInicio > fechaFin -> debe lanzar IllegalArgumentException */
    @Test
    void crear_fechaInicioPosteriorALaFin_lanzaExcepcion() {
        RentasDto dto = buildRentaValida();

        // forzamos inicio > fin
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 5);
        dto.setFechaInicio(cal.getTime()); // inicio futuro
        cal.add(Calendar.DAY_OF_MONTH, -10);
        dto.setFechaFin(cal.getTime()); // fin anterior

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.crear(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("no puede ser posterior"));
        verify(repo, never()).addRenta(any());
    }

    /** (3) eliminar() cuando no existe -> debe lanzar IllegalArgumentException y no llamar a delete */
    @Test
    void eliminar_noExiste_lanzaExcepcion() {
        int numero = 999;
        when(repo.existsBynumeroRenta(numero)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.eliminar(numero));
        assertTrue(ex.getMessage().contains(String.valueOf(numero)));

        verify(repo, never()).deleteRenta(anyInt());
    }
}
