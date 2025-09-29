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

    public String update() {
        try {
            service.update(selectedSucursal);
            success("Sucursal actualizada correctamente.");
            loadSucursales();
            return "/Sucursales/list-sucursal.xhtml?faces-redirect=true";
        }catch(IllegalArgumentException e) {
            error(e.getMessage());
        }catch(Exception e) {
            LOG.severe(e.getMessage());
            error("ocurrio un error al actualizar la sucursal");
        }
        return null;
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

    public void buscarSucursalPorCodigo() {
        if (codigoBusqueda == null) {
            error("Debe ingresar un código.");
            return;
        }
        service.buscarPorId(codigoBusqueda).ifPresentOrElse(
                s -> selectedSucursal = s,
                () -> {
                    selectedSucursal = null;
                    error("No se encontró la sucursal con código " + codigoBusqueda);
                }
        );
    }

    public void cancelarEdicion() {
        selectedSucursal = null;
        codigoBusqueda = null;
    }

    public void loadSelectedSucursal() {
        if (selectedSucursal != null && selectedSucursal.getCodigo() != null) {
            service.buscarPorId(selectedSucursal.getCodigo())
                    .ifPresentOrElse(
                            s -> selectedSucursal = s,
                            () -> error("No se encontró la sucursal con código " + selectedSucursal.getCodigo())
                    );
        }
    }






    /* ===== Getters/Setters ===== */
    public Sucursal getNewSucursal() {
        return newSucursal; }

    public void setNewSucursal(Sucursal newSucursal) {
        this.newSucursal = newSucursal; }

    public Sucursal getSelectedSucursal() {
        return selectedSucursal; }

    public void setSelectedSucursal(Sucursal selectedSucursal) {
        this.selectedSucursal = selectedSucursal; }

    public List<Sucursal> getSucursales() {
        return sucursales; }


    private Integer codigoBusqueda;

    public Integer getCodigoBusqueda() {
        return codigoBusqueda; }

    public void setCodigoBusqueda(Integer codigoBusqueda) {
        this.codigoBusqueda = codigoBusqueda; }



}
