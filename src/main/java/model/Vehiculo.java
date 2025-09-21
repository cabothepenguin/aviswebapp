package model;

import jakarta.persistence.*;

@Entity(name = "administracion_Vehiculos")
public class Vehiculo {

    @Id
    private String placa;

    @Column(name = "modelo")
    private String modelo;

    @Column(name = "marca")
    private String marca;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria", referencedColumnName = "codigo", nullable = false)
    private CategoriaVehiculo categoria;

    @Column(name = "estado")
    private String nombre;

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

    public Vehiculo(String placa, String modelo, String marca,
                    CategoriaVehiculo categoria, String nombre,
                    String anio, String precio, byte[] image, String imageName) {
        this.placa = placa;
        this.modelo = modelo;
        this.marca = marca;
        this.categoria = categoria;
        this.nombre = nombre;
        this.anio = anio;
        this.precio = precio;
        this.image = image;
        this.imageName = imageName;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
