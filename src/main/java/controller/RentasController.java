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

/**
 * Controlador para la gestión de rentas (reservas/alquileres).
 * <p>
 * Expone operaciones CRUD, carga de catálogos (vehículos/sucursales),
 * filtrado por estado y utilidades para precargar la edición vía parámetros.
 * </p>
 */
@Named("rentasBean")
@ViewScoped
public class RentasController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(RentasController.class.getName());

    @Inject private RentaService rentaService;
    @Inject private VehiculoService vehiculoService;
    @Inject private SucursalService sucursalService;

    /** DTO para creación de una nueva renta. */
    private RentasDto newRenta = new RentasDto();
    /** DTO seleccionado para edición. */
    private RentasDto selectedRenta = new RentasDto();

    /** Listado completo de rentas. */
    private List<Renta> rentasActivas = new ArrayList<>();
    private List<Renta> rentas = new ArrayList<>();
    private List<VehiculoDto> vehiculos = new ArrayList<>();
    private List<Sucursal> sucursales = new ArrayList<>();
    /** Parámetro de navegación para cargar una renta específica. */
    private Integer numeroRentaParam;

    /**
     * Inicializa el bean: carga listas base y procesa parámetro de edición.
     * <p>Establece el estado por defecto de nuevas rentas como "activa".</p>
     */
    @PostConstruct
    public void init() {
        loadRentas();
        vehiculos = vehiculoService.listar();    // List<Vehiculo>
        sucursales = sucursalService.listar();   // List<Sucursal>
        newRenta.setEstado("activa");

        // Si viene numeroRenta en el request, carga para edición:
        var params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        if (params.containsKey("numeroRenta")) {
            int nro = Integer.parseInt(params.get("numeroRenta"));
            rentaService.buscarPorNumero(nro).ifPresent(r -> {
                selectedRenta = toDto(r);
                selectedRenta.setVehiculoPlaca(r.getVehiculo().getPlaca());
                selectedRenta.setSucursalCodigo(r.getSucursal().getCodigo());
            });
        }
    }

    /**
     * Carga catálogos de sucursales y vehículos para combos/selects.
     * <p>Invocar antes de mostrar formularios dependientes de estos datos.</p>
     */
    public void loadChoices() {
        try{
            sucursales = sucursalService.listar();
            vehiculos = vehiculoService.listar();
        }catch(Exception ex){
            error("no se encontro el catalogo");
        }
    }

    /**
     * Precarga una renta para edición usando {@link #numeroRentaParam}.
     * <p>
     * Nota: si el número no existe, informa mediante mensaje Faces.
     * </p>
     */
    public void preloadForEdit(){
        if(numeroRentaParam == null)return;
        var opt = rentaService.buscarPorNumero(numeroRentaParam);
        if(opt.isPresent()){
            // Nota: el comportamiento actual muestra error cuando sí existe.
            // Se mantiene sin cambios por requerimiento de "solo documentación".
            error("no existe la renta #"+numeroRentaParam+"`");
            return;
        }
        selectedRenta = toDto(opt.get());
    }

    /**
     * Convierte una entidad {@link Renta} a su DTO equivalente {@link RentasDto}.
     * @param r entidad origen.
     * @return DTO poblado.
     */
    private RentasDto toDto(Renta r) {
        var dto = new RentasDto();
        dto.setNumeroRenta(r.getNumeroRenta());
        dto.setClienteNombre(r.getClienteNombre());
        dto.setVehiculo(r.getVehiculo());
        dto.setSucursal(r.getSucursal());
        dto.setFechaInicio(r.getFechaInicio());
        dto.setFechaFin(r.getFechaFin());
        dto.setPrecioTotal(r.getPrecioTotal());
        dto.setEstado(r.getEstado());
        return dto;
    }

    /**
     * Construye la URL de navegación a la vista de edición de rentas con redirect.
     * @param numeroRentaParam número de renta a editar.
     * @return cadena de navegación JSF.
     */
    public String goToEdit(Integer numeroRentaParam) {
        return "/Rentas/update-rentas.xhtm?faces-redirect=true&numeroRentaParam="+numeroRentaParam;
    }

    /**
     * Crea una nueva renta a partir de {@link #newRenta}.
     * <p>Resuelve y valida vehículo y sucursal previamente.</p>
     */
    public void add() {
        try {
            var vehiculo = vehiculoService.buscarEntidadPorPlaca(newRenta.getVehiculoPlaca())
                    .orElseThrow(() -> new IllegalArgumentException("Vehículo no existe: " + newRenta.getVehiculoPlaca()));
            var sucursal = sucursalService.buscarPorCodigo(newRenta.getSucursalCodigo())
                    .orElseThrow(() -> new IllegalArgumentException("Sucursal no existe: " + newRenta.getSucursalCodigo()));

            newRenta.setVehiculo(vehiculo);   // ahora SI es model.Vehiculo
            newRenta.setSucursal(sucursal);

            rentaService.crear(newRenta);
            success("Renta creada correctamente.");
            newRenta = new RentasDto();
            newRenta.setEstado("activa");
            loadRentas();
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    /** Carga el listado completo de rentas. */
    public void loadRentas() {
        try {
            rentas = rentaService.listar();
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            rentas = new ArrayList<>();
            error("No se pudieron cargar las rentas.");
        }
    }

    /**
     * Busca una renta por su número.
     * @param numeroRenta identificador numérico de la renta.
     * @return {@link Optional} con la entidad si existe.
     */
    public Optional<Renta> findByNumero(int numeroRenta) {
        return rentaService.buscarPorNumero(numeroRenta);
    }

    /**
     * Actualiza la renta actualmente seleccionada en {@link #selectedRenta}.
     * <p>Resuelve entidad de vehículo y sucursal antes de persistir.</p>
     */
    public void update() {
        try {
            var vehiculo = vehiculoService.buscarEntidadPorPlaca(selectedRenta.getVehiculoPlaca())
                    .orElseThrow(() -> new IllegalArgumentException("Vehículo no existe: " + selectedRenta.getVehiculoPlaca()));
            var sucursal = sucursalService.buscarPorCodigo(selectedRenta.getSucursalCodigo())
                    .orElseThrow(() -> new IllegalArgumentException("Sucursal no existe: " + selectedRenta.getSucursalCodigo()));

            selectedRenta.setVehiculo(vehiculo);  // ENTIDAD
            selectedRenta.setSucursal(sucursal);

            rentaService.actualizar(selectedRenta);
            success("Renta actualizada correctamente.");
            loadRentas();
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    /**
     * Elimina una renta por su número.
     * @param numeroRenta identificador de la renta a eliminar.
     */
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

    /**
     * Carga una renta por su número desde {@link #selectedRenta} y
     * rellena el DTO para su edición (incluyendo claves de selectOneMenu).
     * @return <code>null</code> para permanecer en la misma vista.
     */
    public Object loadRentaByNumero() {
        try {
            // Validación básica
            if (selectedRenta == null || selectedRenta.getNumeroRenta() == null) {
                error("Indique el número de renta.");
                return null;
            }

            var opt = rentaService.buscarPorNumero(selectedRenta.getNumeroRenta());
            if (opt.isEmpty()) {
                error("No existe la renta #" + selectedRenta.getNumeroRenta());
                return null;
            }

            Renta r = opt.get();

            RentasDto dto = new RentasDto();
            dto.setNumeroRenta(r.getNumeroRenta());
            dto.setClienteNombre(r.getClienteNombre());
            dto.setFechaInicio(r.getFechaInicio());
            dto.setFechaFin(r.getFechaFin());

            if (r.getPrecioTotal() != null) {
                dto.setPrecioTotal(r.getPrecioTotal());
            } else {
                dto.setPrecioTotal(null);
            }
            dto.setEstado(r.getEstado());

            // ENTIDADES (por si tu repo/servicio las necesita en actualizar)
            dto.setVehiculo(r.getVehiculo());
            dto.setSucursal(r.getSucursal());

            // CLAVES para los selectOneMenu (sin converters)
            if (r.getVehiculo() != null) {
                dto.setVehiculoPlaca(r.getVehiculo().getPlaca());
            }
            if (r.getSucursal() != null) {
                dto.setSucursalCodigo(r.getSucursal().getCodigo());
            }

            // Dejar el DTO como seleccionado para edición
            selectedRenta = dto;

            // Asegurar catálogos cargados
            loadChoices();

            return null; // permanecer en la misma página
        } catch (Exception e) {
            error("Error al cargar la renta: " + e.getMessage());
            return null;
        }
    }

    // -------- Filtrado por estado --------

    /** Valor del estado para filtrar rentas (p.ej. "activa", "finalizada"). */
    private String estadoFiltro;
    /** Resultado del filtrado por estado. */
    private List<Renta> rentasFiltradas = new ArrayList<>();

    /**
     * Filtra las rentas por el valor actual de {@link #estadoFiltro}
     * y deja el resultado en {@link #rentasFiltradas}.
     */
    public void filtrarPorEstado() {
        try {
            rentasFiltradas = rentaService.listarPorEstado(estadoFiltro);
            success("Rentas filtradas por estado: " + estadoFiltro);
        } catch (Exception e) {
            rentasFiltradas = new ArrayList<>();
            error("Error al filtrar: " + e.getMessage());
        }
    }

    // ---------------- Helpers de mensajes ----------------

    /** Muestra un mensaje de error. */
    private void error(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    /** Muestra un mensaje informativo/éxito. */
    private void success(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }

    // ---------------- Getters/Setters ----------------
    public String getEstadoFiltro() { return estadoFiltro; }
    public void setEstadoFiltro(String estadoFiltro) { this.estadoFiltro = estadoFiltro; }
    public List<Renta> getRentasFiltradas() { return rentasFiltradas; }

    public RentasDto getNewRenta() { return newRenta; }
    public void setNewRenta(RentasDto newRenta) { this.newRenta = newRenta; }

    public RentasDto getSelectedRenta() { return selectedRenta; }
    public void setSelectedRenta(RentasDto selectedRenta) { this.selectedRenta = selectedRenta; }

    public List<Renta> getRentas() { return rentas; }
    public List<VehiculoDto> getVehiculos() { return vehiculos; }
    public List<Sucursal> getSucursales() { return sucursales; }

    public Integer getNumeroRentaParam() { return numeroRentaParam; }
    public void setNumeroRentaParam(Integer n) { this.numeroRentaParam = n; }
}
