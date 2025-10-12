package converter;

import dto.VehiculoDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import service.VehiculoService;

/**
 * Converter JSF para {@link VehiculoDto}.
 * <p>
 * Realiza la conversión entre la placa (String) y el DTO de vehículo
 * utilizado por la capa de presentación.
 * </p>
 */
@ApplicationScoped
@FacesConverter(value = "vehiculoConverter", managed = true)
public class VehiculoConverter implements Converter<VehiculoDto> {

    @Inject
    private VehiculoService service;

    /**
     * Convierte la placa (String) al {@link VehiculoDto} correspondiente.
     *
     * @param context      contexto JSF.
     * @param uiComponent  componente que solicita la conversión.
     * @param value        placa del vehículo.
     * @return DTO encontrado o {@code null} si el valor es vacío o no existe.
     */
    @Override
    public VehiculoDto getAsObject(FacesContext context, UIComponent uiComponent, String value) {
        if (value != null && !value.isEmpty()) {
            return service.findVehicleByPlaca(value);
        }
        return null;
    }

    /**
     * Convierte un {@link VehiculoDto} a su representación String (placa).
     *
     * @param context     contexto JSF.
     * @param component   componente que solicita la conversión.
     * @param vehiculoDto DTO a convertir.
     * @return placa del vehículo o cadena vacía si no aplica.
     */
    @Override
    public String getAsString(FacesContext context, UIComponent component, VehiculoDto vehiculoDto) {
        if (vehiculoDto != null && vehiculoDto.getPlaca() != null) {
            return vehiculoDto.getPlaca();
        }
        return "";
    }
}
