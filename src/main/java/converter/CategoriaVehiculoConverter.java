package converter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

import model.CategoriaVehiculo;
import service.CategoriaService;


@ApplicationScoped
@FacesConverter(value = "categoriaVehiculoConverter", managed = true)
public class CategoriaVehiculoConverter implements Converter<CategoriaVehiculo> {

    @Inject
    private CategoriaService service;

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

    @Override
    public String getAsString(FacesContext context, UIComponent component, CategoriaVehiculo categoria) {
        if (categoria != null && categoria.getCodigo() != null) {
            return categoria.getCodigo().toString();
        }
        return "";
    }
}


