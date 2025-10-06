package converter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import model.Sucursal;
import service.SucursalService;

/**
 * Converter JSF para {@link Sucursal}.
 * <p>
 * Convierte entre el valor String (código) utilizado por los componentes JSF
 * y la entidad {@link Sucursal} gestionada por la capa de servicios.
 * </p>
 *
 */
@ApplicationScoped
@FacesConverter(value = "sucursalConverter", managed = true)
public class SucursalConverter implements Converter<Sucursal> {

    @Inject
    private SucursalService sucursalService;

    /**
     * Convierte el código en String a la entidad {@link Sucursal}.
     *
     * @param context   contexto JSF.
     * @param component componente que solicita la conversión.
     * @param value     código de sucursal (String).
     * @return entidad encontrada o {@code null} si el valor es inválido/no numérico/no existe.
     */
    @Override
    public Sucursal getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && !value.isBlank()) {
            try {
                Integer id = Integer.valueOf(value);
                return sucursalService.buscarPorCodigo(id).orElse(null);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Convierte una {@link Sucursal} en su representación String (código).
     *
     * @param context  contexto JSF.
     * @param component componente que solicita la conversión.
     * @param sucursal entidad a convertir.
     * @return el código como String o cadena vacía si no aplica.
     */
    @Override
    public String getAsString(FacesContext context, UIComponent component, Sucursal sucursal) {
        if (sucursal != null && sucursal.getCodigo() != null) {
            return sucursal.getCodigo().toString();
        }
        return "";
    }
}
