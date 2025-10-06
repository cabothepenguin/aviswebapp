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

/**
 * Controlador JSF para la gestión del catálogo de categorías de vehículo.
 * <p>
 * Provee operaciones CRUD, búsqueda por descripción y carga de vehículos
 * asociados a una categoría seleccionada para su uso en combos/listas.
 * </p>
 */
@Named
@ViewScoped
public class CategoriaController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CategoriaController.class.getName());

    @Inject private CategoriaService service;
    @Inject private VehiculoService vehiculoService;

    // Buscar/editar categorías
    /** Código para búsqueda directa de una categoría. */
    private Integer codigoBuscar;
    /** DTO utilizado para crear una nueva categoría. */
    private CategoriaVehiculoDto newCategoria = new CategoriaVehiculoDto();
    /** DTO utilizado para editar una categoría existente. */
    private CategoriaVehiculoDto selectedCategoria = new CategoriaVehiculoDto();
    /** Lista cacheada de categorías para mostrar en tablas/combos. */
    private List<CategoriaVehiculo> categorias = new ArrayList<>();

    // -------- Vehículos por categoría (para el combo) --------
    /** Código de la categoría seleccionada para listar sus vehículos. */
    private Integer codigoCategoriaSeleccionada;
    /** Lista de vehículos filtrados por la categoría seleccionada. */
    private List<Vehiculo> vehiculosPorCategoria = new ArrayList<>();

    /** Inicializa el backing bean cargando el listado de categorías. */
    @PostConstruct
    public void init() { loadCategorias(); }

    /**
     * Crea una nueva categoría a partir de {@link #newCategoria}.
     * <p>Tras crear, limpia el formulario y recarga el listado.</p>
     */
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

    /** Carga todas las categorías disponibles desde el servicio. */
    public void loadCategorias() {
        try {
            categorias = service.listarCategorias();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error cargando categorías", e);
            categorias = new ArrayList<>();
            error("No se pudieron cargar las categorías.");
        }
    }

    /**
     * Busca una categoría por su descripción exacta.
     * @param desc descripción a buscar.
     * @return {@link Optional} con la entidad encontrada o vacío.
     */
    public Optional<CategoriaVehiculo> findByDescripcion(String desc) {
        return Optional.ofNullable(service.getByDescripcion(desc));
    }

    /* ===== UPDATE ===== */

    /**
     * Actualiza la categoría seleccionada.
     *
     * @return navegación a la lista con redirect al completar; <code>null</code> si hay error.
     */
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

    /**
     * Carga una categoría a partir del código ingresado en {@link #codigoBuscar}
     * y la coloca en {@link #selectedCategoria} para edición.
     */
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

    /**
     * Elimina una categoría por su código.
     * @param codigo identificador de la categoría a eliminar.
     */
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

    /**
     * Provee la lista de categorías, recargándola si está vacía.
     * @return lista de entidades {@link CategoriaVehiculo}.
     */
    public List<CategoriaVehiculo> getListaCategorias() {
        if (categorias == null || categorias.isEmpty()) {
            categorias = service.listarCategorias();
        }
        return categorias;
    }

    /* ===== Vehículos por categoría ===== */

    /**
     * Llena {@link #vehiculosPorCategoria} con los vehículos asociados a
     * {@link #codigoCategoriaSeleccionada}. Si no hay selección, muestra error.
     */
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

    /** Limpia la selección y el resultado de vehículos por categoría. */
    public void limpiarVehiculosPorCategoria() {
        codigoCategoriaSeleccionada = null;
        vehiculosPorCategoria.clear();
    }

    /* ===== Helpers (Mensajes Faces) ===== */

    /** Muestra un mensaje de error en el contexto JSF. */
    private void error(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    /** Muestra un mensaje informativo en el contexto JSF. */
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
