package controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import model.Sucursal;
import service.SucursalService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Named
@ViewScoped
public class SucursalController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(SucursalController.class.getName());

    @Inject
    private SucursalService service;

    private Sucursal newSucursal = new Sucursal(); // para crear
    private Sucursal selectedSucursal;             // para editar/eliminar
    private List<Sucursal> sucursales = new ArrayList<>();

    @PostConstruct
    public void init() {
        loadSucursales();
    }

    /* ===== CREATE ===== */
    public void add() {
        try {
            service.add(newSucursal);
            success("Sucursal creada correctamente.");
            newSucursal = new Sucursal(); // limpiar form
            loadSucursales();
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            error("Ocurrió un error al crear la sucursal.");
        }
    }

    /* ===== READ ===== */
    public void loadSucursales() {
        try {
            sucursales = service.listar();
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            sucursales = new ArrayList<>();
            error("No se pudieron cargar las sucursales.");
        }
    }

    public Optional<Sucursal> findById(Integer codigo) {
        return service.buscarPorId(codigo);
    }

    /* ===== UPDATE ===== */
    public void update() {
        if (selectedSucursal == null) {
            error("Seleccione una sucursal para actualizar.");
            return;
        }
        try {
            service.update(selectedSucursal);
            success("Sucursal actualizada correctamente.");
            loadSucursales();
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            error("Ocurrió un error al actualizar la sucursal.");
        }
    }

    /* ===== DELETE ===== */
    public void delete(Integer codigo) {
        try {
            service.eliminar(codigo);
            success("Sucursal eliminada correctamente.");
            loadSucursales();
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            error("Ocurrió un error al eliminar la sucursal.");
        }
    }

    /* ===== Helpers ===== */
    private void error(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    private void success(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }

    /* ===== Getters/Setters ===== */
    public Sucursal getNewSucursal() { return newSucursal; }
    public void setNewSucursal(Sucursal newSucursal) { this.newSucursal = newSucursal; }

    public Sucursal getSelectedSucursal() { return selectedSucursal; }
    public void setSelectedSucursal(Sucursal selectedSucursal) { this.selectedSucursal = selectedSucursal; }

    public List<Sucursal> getSucursales() { return sucursales; }
}
