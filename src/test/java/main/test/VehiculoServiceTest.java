package service;

import dto.VehiculoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.VehiculoRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VehiculoServiceTest {

    private VehiculoService service;
    private VehiculoRepository repo;

    @BeforeEach
    void setUp() {
        repo = mock(VehiculoRepository.class);
        service = new VehiculoService();

        // inyectar el mock usando reflexión porque el campo es private @Inject
        try {
            var f = VehiculoService.class.getDeclaredField("repository");
            f.setAccessible(true);
            f.set(service, repo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private VehiculoDto baseDtoValido() {
        VehiculoDto dto = new VehiculoDto();
        dto.setPlaca("ABC123");
        dto.setModelo("Corolla");
        dto.setMarca("Toyota");
        dto.setEstado("disponible");
        dto.setAnio(2024);
        dto.setPrecio(25000.0);

        // categoría con código (requisito de validarObligatorios)
        var cat = new model.CategoriaVehiculo();
        cat.setCodigo(10);
        dto.setCategoria(cat);

        return dto;
    }

    /** (4) actualizar() con placa null -> excepción inmediata */
    @Test
    void actualizar_conPlacaNull_lanzaExcepcion() {
        VehiculoDto dto = baseDtoValido();
        dto.setPlaca(null);

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> service.actualizar(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("placa es obligatoria"));
        verifyNoInteractions(repo);
    }

    /** (5) actualizar() cuando no existe la placa -> excepción “No existe un vehículo…” */
    @Test
    void actualizar_placaNoExiste_lanzaExcepcion() {
        VehiculoDto dto = baseDtoValido();
        when(repo.existsByPlaca("ABC123")).thenReturn(false);

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> service.actualizar(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("no existe un vehículo"));
        verify(repo, times(1)).existsByPlaca("ABC123");
        verify(repo, never()).updateVehiculo(any());
    }
}
