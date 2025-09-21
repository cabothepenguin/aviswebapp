package model;

import jakarta.persistence.*;

import java.util.Date;


@Entity
@Table(name = "administracion_rentas")

public class Renta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "numeroRenta")
    private Integer numeroRenta;

    @Column(name = "clienteNombre")
    private String clienteNombre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehiculoAsignado", referencedColumnName = "placa", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sucursal", referencedColumnName = "codigo", nullable = false)
    private Sucursal sucursal;


    @Temporal(TemporalType.DATE)  // usa DATE; si guardas hora, cambia a TIMESTAMP
    @Column(name = "fechaInicio", nullable = false)
    private Date fechaInicio;

    @Temporal(TemporalType.DATE)
    @Column(name = "fechafin", nullable = false)
    private Date fechaFin;

    @Column(name = "precioTotal")
    private Integer precioTotal;

    @Column(name = "estado")
    private String estado;

    public Renta() {

    }

    public Renta(Integer numeroRenta, String clienteNombre, Vehiculo vehiculo,
                 Sucursal sucursal, Date fechaInicio, Date fechaFin,
                 Integer precioTotal, String estado) {
        this.numeroRenta = numeroRenta;
        this.clienteNombre = clienteNombre;
        this.vehiculo = vehiculo;
        this.sucursal = sucursal;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.precioTotal = precioTotal;
        this.estado = estado;
    }

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

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
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

    public Integer getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(Integer precioTotal) {
        this.precioTotal = precioTotal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}