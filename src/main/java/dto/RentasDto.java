package dto;

import model.Sucursal;
import model.Vehiculo;

import java.util.Date;

public class RentasDto {

    private Integer numeroRenta; // o el PK que uses
    private String clienteNombre;

    // Ahora trabajan con Entities en lugar de DTOs
    private Vehiculo vehiculo;
    private Sucursal sucursal;

    private String vehiculoPlaca;
    private Integer sucursalCodigo;

    private Date fechaInicio;
    private Date fechaFin;
    private Integer precioTotal;
    private String estado;

    // ===== Getters/Setters =====


    public Integer getNumeroRenta() {
        return numeroRenta;
    }

    public void setNumeroRenta(Integer numeroRenta) {
        this.numeroRenta = numeroRenta;
    }

    public String getClienteNombre() {
        return clienteNombre; }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre; }

    public Vehiculo getVehiculo() {
        return vehiculo; }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo; }

    public Sucursal getSucursal() { return sucursal; }
    public void setSucursal(Sucursal sucursal) { this.sucursal = sucursal; }

    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }

    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }

    public Integer getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(Integer precioTotal) { this.precioTotal = precioTotal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getVehiculoPlaca() {
        return vehiculoPlaca;
    }

    public void setVehiculoPlaca(String vehiculoPlaca) {
        this.vehiculoPlaca = vehiculoPlaca;
    }

    public Integer getSucursalCodigo() {
        return sucursalCodigo;
    }

    public void setSucursalCodigo(Integer sucursalCodigo) {
        this.sucursalCodigo = sucursalCodigo;
    }
}
