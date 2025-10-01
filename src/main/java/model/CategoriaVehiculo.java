package model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity(name = "administracion_categorias")
public class CategoriaVehiculo {

    @Id
    private Integer codigo;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "estado")
    private String estado;



    public CategoriaVehiculo() {

    }

    public CategoriaVehiculo(Integer codigo, String descripcion, String estado) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {

        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoriaVehiculo)) return false;
        CategoriaVehiculo that = (CategoriaVehiculo) o;
        return codigo != null && codigo.equals(that.codigo);
    }

    @Override
    public int hashCode() {
        return codigo != null ? codigo.hashCode() : 0;
    }
}
