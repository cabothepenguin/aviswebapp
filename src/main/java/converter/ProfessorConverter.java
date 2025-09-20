package converter;

import dto.ProfessorDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import service.ProfessorService;

@ApplicationScoped
@FacesConverter(value = "professorConverter" , managed=true)
public class ProfessorConverter implements Converter<ProfessorDto>{
    @Inject
    private ProfessorService service;

    @Override
    public ProfessorDto getAsObject(FacesContext context, UIComponent component, String s) {
        if(s!=null&& !s.isEmpty()){
            return service.getProfessor(Integer.parseInt(s));
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, ProfessorDto professorDto) {
        return professorDto.getId()== null? null  : professorDto.getId().toString();
    }
}
