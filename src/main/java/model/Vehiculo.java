package model;

import jakarta.persistence.*;

@Entity(name = "administracion_Vehiculos")
public class Vehiculo {

    @Id
    private Integer placa;

    @Column(name = "modelo")
    private String modelo;

    @Column(name = "marca")
    private String marca;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria", referencedColumnName = "codigo", nullable = false)
    private CategoriaVehiculo categoria;

    @Column(name = "estado")
    private String estado;

    @Column(name = "año")
    private String anio;

    @Column(name = "precio")
    private String precio;

    @Column(name = "imagen")
    private byte[] image;

    @Column(name = "nombre_imagen")
    private String imageName;

    public Vehiculo() {
    }

    public Vehiculo(Integer placa, String modelo, String marca,
                    CategoriaVehiculo categoria, String estado,
                    String anio, String precio, byte[] image, String imageName) {
        this.placa = placa;
        this.modelo = modelo;
        this.marca = marca;
        this.categoria = categoria;
        this.estado = estado;
        this.anio = anio;
        this.precio = precio;
        this.image = image;
        this.imageName = imageName;
    }

    public Integer getPlaca() {
        return placa;
    }

    public void setPlaca(Integer placa) {

        this.placa = placa;
    }

    public String getModelo() {

        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getMarca() {

        return marca;
    }

    public void setMarca(String marca) {

        this.marca = marca;
    }

    public CategoriaVehiculo getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaVehiculo categoria) {

        this.categoria = categoria;
    }

    public String getEstado() {

        return estado;
    }

    public void setEstado(String estado) {

        this.estado = estado;
    }

    public String getAnio() {

        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getPrecio() {

        return precio;
    }

    public void setPrecio(String precio) {

        this.precio = precio;
    }

    public byte[] getImage() {

        return image;
    }

    public void setImage(byte[] image) {

        this.image = image;
    }

    public String getImageName() {

        return imageName;
    }

    public void setImageName(String imageName) {

        this.imageName = imageName;
    }
}
