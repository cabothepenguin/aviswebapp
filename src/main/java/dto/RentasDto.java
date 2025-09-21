package dto;

import java.io.Serializable;
import java.util.Date;

public class RentasDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer numeroRenta;
    private String clienteNombre;
    VehiculoDto vehiculo;
    SucursalDto sucursal;
    private Date fechaInicio;
    private Date fechaFin;
    private int precioTotal;
    private String estado;

    public Integer getNumeroRenta() {
        return numeroRenta;
    }

    public void setNumeroRenta(Integer numeroRenta) {
        this.numeroRenta = numeroRenta;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public VehiculoDto getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(VehiculoDto vehiculo) {
        this.vehiculo = vehiculo;
    }

    public SucursalDto getSucursal() {
        return sucursal;
    }

    public void setSucursal(SucursalDto sucursal) {
        this.sucursal = sucursal;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public int getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(int precioTotal) {
        this.precioTotal = precioTotal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
