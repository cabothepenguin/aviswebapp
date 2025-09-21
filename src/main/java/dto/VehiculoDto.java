package dto;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;


@Named
@ViewScoped
public class VehiculoDto implements Serializable {


    private String placa;        // PK
    private String modelo;
    private String marca;
    CategoriaVehiculoDto categoria;
    private String estado;
    private Integer anio;
    private Integer precio;
    private byte[] image;
    private String imageName;


    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public CategoriaVehiculoDto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaVehiculoDto categoria) {}

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getPrecio() { return precio; }
    public void setPrecio(Integer precio) { this.precio = precio; }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
