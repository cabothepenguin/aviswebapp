package converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import model.Sucursal;
import service.SucursalService;

@FacesConverter(value = "sucursalConverter", managed = true)
public class SucursalConverter implements Converter<Sucursal> {

    @Inject
    private SucursalService sucursalService;

    @Override
    public Sucursal getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return sucursalService.buscarPorCodigo(Integer.valueOf(value)).orElse(null);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Sucursal sucursal) {
        if (sucursal == null) {
            return "";
        }
        return String.valueOf(sucursal.getCodigo()); // PK de sucursal
    }
}
