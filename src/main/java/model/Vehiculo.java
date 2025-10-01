package model;

import jakarta.persistence.*;

@Entity
@Table(name = "administracion_vehiculos")
public class Vehiculo {

    @Id
    @Column(name = "placa", length = 20)
    private String placa;  // Cambiar de Integer a String

    @Column(name = "modelo", length = 100, nullable = false)
    private String modelo;

    @Column(name = "marca", length = 100, nullable = false)
    private String marca;

    @ManyToOne
    @JoinColumn(name = "categoria", referencedColumnName = "codigo")
    private CategoriaVehiculo categoria;


    @Column(name = "estado", length = 20)
    private String estado;

    @Column(name = "anio", nullable = false)
    private Integer anio;

    @Column(name = "precio", nullable = false)
    private Integer precio;

    @Lob
    @Column(name = "imagen")
    private byte[] image;

    @Column(name = "nombre_imagen", length = 100)
    private String imageName;

    // Constructor por defecto
    public Vehiculo() {
    }

    // Getters y Setters
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public CategoriaVehiculo getCategoria() { return categoria; }
    public void setCategoria(CategoriaVehiculo categoria) { this.categoria = categoria; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public Integer getPrecio() { return precio; }
    public void setPrecio(Integer precio) { this.precio = precio; }

    public byte[] getImage() { return image; }
    public void setImage(byte[] image) { this.image = image; }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }


}