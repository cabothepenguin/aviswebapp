package dto;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import model.CategoriaVehiculo;

import java.io.Serializable;
import java.util.Objects;


@Named
@ViewScoped
public class VehiculoDto implements Serializable {


    private Integer placa;        // PK
    private String modelo;
    private String marca;
    private CategoriaVehiculo categoria;
    private String estado;
    private Integer anio;
    private Integer precio;
    private byte[] image;
    private String imageName;

    public VehiculoDto() {

    }

    public VehiculoDto(Integer placa, String modelo, String marca, CategoriaVehiculo categoria, String estado, Integer anio,
                       Integer precio, byte[] image, String imageName) {
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

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getPrecio() {
        return precio;
    }

    public void setPrecio(Integer precio) {
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


    @Override
    public boolean equals(Object o) {
        if(o == null  || getClass() != o.getClass()) return false;
        VehiculoDto that = (VehiculoDto) o;
        return Objects.equals(placa, that.placa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placa);
    }
}
