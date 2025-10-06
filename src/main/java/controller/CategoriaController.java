package controller;

import dto.CategoriaVehiculoDto;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import model.CategoriaVehiculo;
import model.Vehiculo;
import service.CategoriaService;
import service.VehiculoService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class CategoriaController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CategoriaController.class.getName());

    @Inject private CategoriaService service;
    @Inject private VehiculoService vehiculoService;

    // Buscar/editar categorías
    private Integer codigoBuscar;
    private CategoriaVehiculoDto newCategoria = new CategoriaVehiculoDto();
    private CategoriaVehiculoDto selectedCategoria = new CategoriaVehiculoDto();
    private List<CategoriaVehiculo> categorias = new ArrayList<>();

    // -------- Vehículos por categoría (para el combo) --------
    private Integer codigoCategoriaSeleccionada;
    private List<Vehiculo> vehiculosPorCategoria = new ArrayList<>();

    @PostConstruct
    public void init() { loadCategorias(); }

    /* ===== CREATE ===== */
    public void add() {
        try {
            service.createCategoria(newCategoria);
            success("Categoría creada correctamente.");
            newCategoria = new CategoriaVehiculoDto();
            loadCategorias();
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error creando categoría", e);
            error("Ocurrió un error al crear la categoría.");
        }
    }

    /* ===== READ ===== */
    public void loadCategorias() {
        try {
            categorias = service.listarCategorias();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error cargando categorías", e);
            categorias = new ArrayList<>();
            error("No se pudieron cargar las categorías.");
        }
    }

    public Optional<CategoriaVehiculo> findByDescripcion(String desc) {
        return Optional.ofNullable(service.getByDescripcion(desc));
    }

    /* ===== UPDATE ===== */
    public String update() {
        try {
            service.updateCategoria(selectedCategoria);
            success("Categoría actualizada correctamente.");
            loadCategorias();
            return "/Categorias/list-categorias.xhtml?faces-redirect=true";
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error actualizando categoría", e);
            error("Ocurrió un error al actualizar la categoría.");
        }
        return null;
    }

    public void loadCategoriaByCodigo() {
        try {
            if (codigoBuscar != null) {
                var encontrada = service.getById(codigoBuscar);
                if (encontrada != null) {
                    selectedCategoria.setCodigo(encontrada.getCodigo());
                    selectedCategoria.setDescripcion(encontrada.getDescripcion());
                    selectedCategoria.setEstado(encontrada.getEstado());
                    success("Categoría cargada para edición.");
                } else {
                    error("No se encontró ninguna categoría con el código: " + codigoBuscar);
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error buscando categoría", e);
            error("Error al buscar la categoría.");
        }
    }

    /* ===== DELETE ===== */
    public void delete(Integer codigo) {
        try {
            service.deleteCategoria(codigo);
            success("Categoría eliminada correctamente.");
            loadCategorias();
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error eliminando categoría", e);
            error("Ocurrió un error al eliminar la categoría.");
        }
    }

    public List<CategoriaVehiculo> getListaCategorias() {
        if (categorias == null || categorias.isEmpty()) {
            categorias = service.listarCategorias();
        }
        return categorias;
    }

    /* ===== Vehículos por categoría ===== */
    public void buscarVehiculosPorCategoria() {
        if (codigoCategoriaSeleccionada == null) {
            error("Seleccione una categoría.");
            vehiculosPorCategoria.clear();
            return;
        }
        try {
            vehiculosPorCategoria = vehiculoService.listarPorCategoria(codigoCategoriaSeleccionada);
            if (vehiculosPorCategoria.isEmpty()) success("No hay vehículos para la categoría seleccionada.");
            else success("Vehículos cargados.");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error cargando vehículos por categoría", e);
            error("Ocurrió un error al cargar los vehículos.");
        }
    }

    public void limpiarVehiculosPorCategoria() {
        codigoCategoriaSeleccionada = null;
        vehiculosPorCategoria.clear();
    }

    /* ===== Helpers ===== */
    private void error(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    private void success(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }

    /* ===== Getters/Setters ===== */
    public CategoriaVehiculoDto getNewCategoria() { return newCategoria; }
    public void setNewCategoria(CategoriaVehiculoDto newCategoria) { this.newCategoria = newCategoria; }

    public CategoriaVehiculoDto getSelectedCategoria() { return selectedCategoria; }
    public void setSelectedCategoria(CategoriaVehiculoDto selectedCategoria) { this.selectedCategoria = selectedCategoria; }

    public List<CategoriaVehiculo> getCategorias() { return categorias; }

    public Integer getCodigoBuscar() { return codigoBuscar; }
    public void setCodigoBuscar(Integer codigoBuscar) { this.codigoBuscar = codigoBuscar; }

    public Integer getCodigoCategoriaSeleccionada() { return codigoCategoriaSeleccionada; }
    public void setCodigoCategoriaSeleccionada(Integer codigoCategoriaSeleccionada) { this.codigoCategoriaSeleccionada = codigoCategoriaSeleccionada; }

    public List<Vehiculo> getVehiculosPorCategoria() { return vehiculosPorCategoria; }
}
