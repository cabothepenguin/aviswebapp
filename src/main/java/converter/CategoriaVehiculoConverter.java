package converter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

import model.CategoriaVehiculo;
import service.CategoriaService;

/**
 * Converter JSF para {@link CategoriaVehiculo}.
 * <p>
 * Permite convertir entre el identificador (String) mostrado en componentes JSF
 * (p.ej., {@code <h:selectOneMenu>}) y la entidad {@link CategoriaVehiculo}
 * que utiliza la capa de negocio.
 * </p>
 *
 */
@ApplicationScoped
@FacesConverter(value = "categoriaVehiculoConverter", managed = true)
public class CategoriaVehiculoConverter implements Converter<CategoriaVehiculo> {

    @Inject
    private CategoriaService service;

    /**
     * Convierte el valor String del componente (id) a {@link CategoriaVehiculo}.
     *
     * @param context   contexto JSF actual.
     * @param component componente que invoca la conversión.
     * @param value     identificador de la categoría (String).
     * @return la entidad correspondiente, o {@code null} si el valor es vacío o no numérico.
     */
    @Override
    public CategoriaVehiculo getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && !value.isEmpty()) {
            try {
                Integer id = Integer.valueOf(value);
                return service.findById(id);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Convierte una entidad {@link CategoriaVehiculo} a su representación String (id).
     *
     * @param context   contexto JSF.
     * @param component componente que invoca.
     * @param categoria entidad a convertir.
     * @return id de la categoría como String o cadena vacía si no aplica.
     */
    @Override
    public String getAsString(FacesContext context, UIComponent component, CategoriaVehiculo categoria) {
        if (categoria != null && categoria.getCodigo() != null) {
            return categoria.getCodigo().toString();
        }
        return "";
    }
}
