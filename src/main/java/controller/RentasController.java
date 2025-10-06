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

    private List<Renta> rentasActivas = new ArrayList<>();
    private List<Renta> rentas = new ArrayList<>();
    private List<VehiculoDto> vehiculos = new ArrayList<>();
    private List<Sucursal> sucursales = new ArrayList<>();
    private Integer numeroRentaParam;

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


    public void loadChoices() {
        try{
            sucursales = sucursalService.listar();
            vehiculos = vehiculoService.listar();
        }catch(Exception ex){
            error("no se encontro el catalogo");
        }
    }

    public void preloadForEdit(){
        if(numeroRentaParam == null)return;
        var opt = rentaService.buscarPorNumero(numeroRentaParam);
        if(opt.isPresent()){
            error("no existe la renta #"+numeroRentaParam+"`");
            return;
        }
        selectedRenta = toDto(opt.get());
    }

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

    public String goToEdit(Integer numeroRentaParam) {
        return "/Rentas/update-rentas.xhtm?faces-redirect=true&numeroRentaParam="+numeroRentaParam;
    }

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






    private String estadoFiltro;
    private List<Renta> rentasFiltradas = new ArrayList<>();

    public void filtrarPorEstado() {
        try {
            rentasFiltradas = rentaService.listarPorEstado(estadoFiltro);
            success("Rentas filtradas por estado: " + estadoFiltro);
        } catch (Exception e) {
            rentasFiltradas = new ArrayList<>();
            error("Error al filtrar: " + e.getMessage());
        }
    }

    public String getEstadoFiltro() { return estadoFiltro; }
    public void setEstadoFiltro(String estadoFiltro) { this.estadoFiltro = estadoFiltro; }

    public List<Renta> getRentasFiltradas() { return rentasFiltradas; }


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

    public Integer getNumeroRentaParam() { return numeroRentaParam; }
    public void setNumeroRentaParam(Integer n) { this.numeroRentaParam = n; }


}
