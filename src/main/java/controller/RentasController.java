package controller;

import dto.RentasDto;
import dto.VehiculoDto;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import model.Renta;
import model.Sucursal;
import model.Vehiculo;
import service.RentaService;
import service.SucursalService;
import service.VehiculoService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Named("rentasBean")
@ViewScoped
public class RentasController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(RentasController.class.getName());

    @Inject private RentaService rentaService;
    @Inject private VehiculoService vehiculoService;
    @Inject private SucursalService sucursalService;

    private RentasDto newRenta = new RentasDto();
    private RentasDto selectedRenta = new RentasDto();

    private List<Renta> rentas = new ArrayList<>();
    private List<VehiculoDto> vehiculos = new ArrayList<>();
    private List<Sucursal> sucursales = new ArrayList<>();

    @PostConstruct
    public void init() {
        loadRentas();
        vehiculos = vehiculoService.listar();      // ya tienes este método
        sucursales = sucursalService.listar();     // debe devolver List<Sucursal>
        newRenta.setEstado("activa");
    }

    public void add() {
        try {
            rentaService.crear(newRenta);
            success("Renta creada correctamente.");
            newRenta = new RentasDto();
            newRenta.setEstado("activa");
            loadRentas();
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            error("Ocurrió un error al crear la renta.");
        }
    }

    public void loadRentas() {
        try {
            rentas = rentaService.listar();
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            rentas = new ArrayList<>();
            error("No se pudieron cargar las rentas.");
        }
    }

    public Optional<Renta> findByNumero(int numeroRenta) {
        return rentaService.buscarPorNumero(numeroRenta);
    }

    public void update() {
        try {
            rentaService.actualizar(selectedRenta);
            success("Renta actualizada correctamente.");
            loadRentas();
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            error("Ocurrió un error al actualizar la renta.");
        }
    }

    public void delete(int numeroRenta) {
        try {
            rentaService.eliminar(numeroRenta);
            success("Renta eliminada correctamente.");
            loadRentas();
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            error("Ocurrió un error al eliminar la renta.");
        }
    }

    private void error(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    private void success(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }

    // Getters/Setters
    public RentasDto getNewRenta() { return newRenta; }
    public void setNewRenta(RentasDto newRenta) { this.newRenta = newRenta; }

    public RentasDto getSelectedRenta() { return selectedRenta; }
    public void setSelectedRenta(RentasDto selectedRenta) { this.selectedRenta = selectedRenta; }

    public List<Renta> getRentas() { return rentas; }
    public List<VehiculoDto> getVehiculos() { return vehiculos; }
    public List<Sucursal> getSucursales() { return sucursales; }
}
