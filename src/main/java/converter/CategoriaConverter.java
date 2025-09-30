package converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import model.CategoriaVehiculo;
import service.CategoriaService;
import jakarta.inject.Inject;

@FacesConverter(value = "categoriaConverter", managed = true)
public class CategoriaConverter implements Converter<CategoriaVehiculo> {

    @Inject
    private CategoriaService categoriaService;

    @Override
    public CategoriaVehiculo getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return categoriaService.getById(Integer.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, CategoriaVehiculo categoria) {
        if (categoria == null || categoria.getCodigo() == null) {
            return "";
        }
        return String.valueOf(categoria.getCodigo());
    }
}
