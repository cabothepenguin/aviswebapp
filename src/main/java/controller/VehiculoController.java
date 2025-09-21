package controller;

import dto.VehiculoDto;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import model.Vehiculo;
import service.VehiculoService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Named
@ViewScoped
public class VehiculoController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(VehiculoController.class.getName());

    @Inject
    private VehiculoService service;

    // Form de creación
    private VehiculoDto newVehiculo = new VehiculoDto();
    // Form de edición (p.ej. bind con un diálogo o tabla editable)
    private VehiculoDto selectedVehiculo = new VehiculoDto();

    // Para listar en la tabla (uso entity porque tu service expone listar() con entities)
    private List<Vehiculo> vehiculos = new ArrayList<>();

    @PostConstruct
    public void init() {
        loadVehiculos();
    }

    /* =================== CREATE =================== */
    public void add() {
        try {
            service.crear(newVehiculo);
            success("Vehículo creado correctamente.");
            newVehiculo = new VehiculoDto(); // limpiar form
            loadVehiculos();
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            error("Ocurrió un error al crear el vehículo.");
        }
    }

    /* =================== READ =================== */
    public void loadVehiculos() {
        try {
            vehiculos = service.listar();
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            vehiculos = new ArrayList<>();
            error("No se pudieron cargar los vehículos.");
        }
    }

    public Optional<Vehiculo> findByPlaca(String placa) {
        return service.buscarPorPlaca(placa);
    }

    /* =================== UPDATE =================== */
    public void update() {
        try {
            service.actualizar(selectedVehiculo);
            success("Vehículo actualizado correctamente.");
            loadVehiculos();
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            error("Ocurrió un error al actualizar el vehículo.");
        }
    }

    /* =================== DELETE =================== */
    public void delete(String placa) {
        try {
            service.eliminar(placa);
            success("Vehículo eliminado correctamente.");
            loadVehiculos();
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            error("Ocurrió un error al eliminar el vehículo.");
        }
    }

    /* =================== Helpers =================== */
    private void error(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    private void success(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }

    /* =================== Getters/Setters =================== */
    public VehiculoDto getNewVehiculo() { return newVehiculo; }
    public void setNewVehiculo(VehiculoDto newVehiculo) { this.newVehiculo = newVehiculo; }

    public VehiculoDto getSelectedVehiculo() { return selectedVehiculo; }
    public void setSelectedVehiculo(VehiculoDto selectedVehiculo) { this.selectedVehiculo = selectedVehiculo; }

    public List<Vehiculo> getVehiculos() { return vehiculos; }
}
