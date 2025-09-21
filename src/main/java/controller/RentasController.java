package controller;

import dto.RentasDto;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import model.Renta;
import service.RentaService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Named
@ViewScoped
public class RentasController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(RentasController.class.getName());

    @Inject
    private RentaService service;

    // Form de creación
    private RentasDto newRenta = new RentasDto();
    // Form de edición (p.ej. bind con diálogo o fila seleccionada)
    private RentasDto selectedRenta = new RentasDto();

    // Tabla: el service expone entidades (Renta)
    private List<Renta> rentas = new ArrayList<>();

    @PostConstruct
    public void init() {
        loadRentas();
    }

    /* ================== CREATE ================== */
    public void add() {
        try {
            service.crear(newRenta);
            success("Renta creada correctamente.");
            newRenta = new RentasDto(); // limpiar form
            loadRentas();
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            error("Ocurrió un error al crear la renta.");
        }
    }

    /* ================== READ ================== */
    public void loadRentas() {
        try {
            rentas = service.listar();
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            rentas = new ArrayList<>();
            error("No se pudieron cargar las rentas.");
        }
    }

    public Optional<Renta> findByNumero(int numeroRenta) {
        return service.buscarPorNumero(numeroRenta);
    }

    /* ================== UPDATE ================== */
    public void update() {
        try {
            service.actualizar(selectedRenta);
            success("Renta actualizada correctamente.");
            loadRentas();
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            error("Ocurrió un error al actualizar la renta.");
        }
    }

    /* ================== DELETE ================== */
    public void delete(int numeroRenta) {
        try {
            service.eliminar(numeroRenta);
            success("Renta eliminada correctamente.");
            loadRentas();
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            error("Ocurrió un error al eliminar la renta.");
        }
    }

    /* ================== Helpers ================== */
    private void error(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    private void success(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }

    /* ================== Getters / Setters ================== */
    public RentasDto getNewRenta() { return newRenta; }
    public void setNewRenta(RentasDto newRenta) { this.newRenta = newRenta; }

    public RentasDto getSelectedRenta() { return selectedRenta; }
    public void setSelectedRenta(RentasDto selectedRenta) { this.selectedRenta = selectedRenta; }

    public List<Renta> getRentas() { return rentas; }
}
