package converter;

import dto.VehiculoDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import service.VehiculoService;



@ApplicationScoped
@FacesConverter(value = "vehiculoConverter", managed = true)
public class VehiculoConverter implements Converter<VehiculoDto> {

    @Inject
    private VehiculoService service;
    @Override
    public VehiculoDto getAsObject(FacesContext context, UIComponent uiComponent, String s){
        if(s!=null&& !s.isEmpty()){
            return service.findVehicleByPlaca((Integer.parseInt(s)));
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,VehiculoDto vehiculoDto){
        return vehiculoDto.getPlaca()== null? null : vehiculoDto.getPlaca().toString();
    }


}
