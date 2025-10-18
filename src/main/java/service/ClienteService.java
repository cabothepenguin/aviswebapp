package service;

import dto.ClienteDto;
import jakarta.inject.Inject;
import model.Cliente;
import repository.ClienteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.System.out;

public class ClienteService {

    @Inject
    private ClienteRepository repository;

    /**
     * Crea un cliente con validaciones basicas
     */
    public void addClient(ClienteDto dto) {
        try {
            requireDto(dto);
            normalize(dto);

            if(isBlank(dto.getCedula())) throw new IllegalArgumentException("Cedula no puede ser vacia");
            if(isBlank(dto.getNombre())) throw new IllegalArgumentException("Nombre no puede ser vacia");
            if(repository.existsByCedula(dto.getCedula())){
                throw new IllegalArgumentException("Cedula ya existe"+dto.getCedula());
            }
            repository.addClient(dto);
        }catch (RuntimeException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException("No se pudo registrar un cliente");
        }
    }

    /**
     * Lista de Clientes
     */
    public List<ClienteDto> getClients() {
        List<ClienteDto> out = new ArrayList<>();
        repository.getClients().forEach(obj-> out.add(toDto((Cliente)obj)));
        return out;
    }

    /**
     * Actualiza Cliente desde Dto requiere existencia previa
     */
    public void updateClient(ClienteDto dto) {
        try {
            requireDto(dto);
            normalize(dto);
            if(isBlank(dto.getCedula())) throw new IllegalArgumentException("Cedula no puede ser vacia para actualizar");
            if(!repository.exisByCedula(dto.getCedula())) throw new IllegalArgumentException("Cedula no existe"+dto.getCedula());
            repository.updateClient(dto);
        }catch(RuntimeException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException("No se pudo actualizar un cliente");
        }
    }

    public void deleteClient(String cedula) {
        try {
            if (isBlank(cedula)) throw new IllegalArgumentException("la cedula es obligatorio para eliminar");
            cedula = cedula.trim();
            repository.deleteClient(cedula);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("No se puede eliminar el cliente", e);
        }
    }

    /**
     * Helpers
     */
    private void requireDto(ClienteDto dto) {
        Objects.requireNonNull(dto, "El DTO del cliente no puede ser nulo.");
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private void normalize(ClienteDto dto) {
        if(dto.getCedula() != null) dto.setCedula(dto.getCedula().trim());
        if(dto.getNombre() != null) dto.setNombre(dto.getNombre().trim());
        if(dto.getTelefono()!= null)dto.setTelefono(dto.getTelefono());
        if(dto.getCorreo() != null)dto.setCorreo(dto.getCorreo().trim());
    }

    /**
     * Mapea la entidad a DTO
     */
    private ClienteDto toDto(Cliente c) {
        ClienteDto dto = new ClienteDto();
        dto.setCedula(c.getCedula());
        dto.setNombre(c.getNombre());
        dto.setTelefono(c.getTelefono());
        dto.setCorreo(c.getCorreo());
        return dto;
    }

}
