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

/**
 * Controlador JSF para administración del catálogo de sucursales.
 * <p>Incluye operaciones CRUD, búsqueda por código y utilidades de edición.</p>
 */
@Named
@ViewScoped
public class SucursalController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(SucursalController.class.getName());

    @Inject
    private SucursalService service;

    /** Entidad utilizada para el formulario de creación. */
    private Sucursal newSucursal = new Sucursal();
    /** Entidad seleccionada para edición/eliminación. */
    private Sucursal selectedSucursal;
    /** Listado de sucursales para tablas/vistas. */
    private List<Sucursal> sucursales = new ArrayList<>();

    /** Código utilizado para búsquedas puntuales. */
    private Integer codigoBusqueda;

    /** Carga el listado de sucursales al inicializar el bean. */
    @PostConstruct
    public void init() {
        loadSucursales();
    }

    /* ===== CREATE ===== */

    /** Crea una nueva sucursal y recarga el listado. */
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

    /** Carga todas las sucursales desde el servicio. */
    public void loadSucursales() {
        try {
            sucursales = service.listar();
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            sucursales = new ArrayList<>();
            error("No se pudieron cargar las sucursales.");
        }
    }

    /**
     * Busca una sucursal por su identificador.
     * @param codigo id de sucursal.
     * @return {@link Optional} con la entidad si existe.
     */
    public Optional<Sucursal> findById(Integer codigo) {
        return service.buscarPorId(codigo);
    }

    /**
     * Actualiza la sucursal actualmente seleccionada.
     *
     * @return navegación a la lista con redirect al finalizar; <code>null</code> si hay errores.
     */
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

    /**
     * Elimina una sucursal por su código.
     * @param codigo id de la sucursal a eliminar.
     */
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

    /* ===== Helpers (Mensajes y utilidades de búsqueda/edición) ===== */

    /** Muestra un mensaje de error en el contexto JSF. */
    private void error(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    /** Muestra un mensaje informativo en el contexto JSF. */
    private void success(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }

    /**
     * Busca una sucursal por {@link #codigoBusqueda} y la coloca en
     * {@link #selectedSucursal} para edición; informa si no se encuentra.
     */
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

    /** Limpia la edición y el código de búsqueda. */
    public void cancelarEdicion() {
        selectedSucursal = null;
        codigoBusqueda = null;
    }

    /** Recarga la entidad seleccionada desde el servicio si existe. */
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
    public Sucursal getNewSucursal() { return newSucursal; }
    public void setNewSucursal(Sucursal newSucursal) { this.newSucursal = newSucursal; }

    public Sucursal getSelectedSucursal() { return selectedSucursal; }
    public void setSelectedSucursal(Sucursal selectedSucursal) { this.selectedSucursal = selectedSucursal; }

    public List<Sucursal> getSucursales() { return sucursales; }

    public Integer getCodigoBusqueda() { return codigoBusqueda; }
    public void setCodigoBusqueda(Integer codigoBusqueda) { this.codigoBusqueda = codigoBusqueda; }
}
