package converter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import model.Sucursal;
import service.SucursalService;

@ApplicationScoped
@FacesConverter(value = "sucursalConverter", managed = true)
public class SucursalConverter implements Converter<Sucursal> {

    @Inject
    private SucursalService sucursalService;

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

    @Override
    public String getAsString(FacesContext context, UIComponent component, Sucursal sucursal) {
        if (sucursal != null && sucursal.getCodigo() != null) {
            return sucursal.getCodigo().toString();
        }
        return "";
    }
}
