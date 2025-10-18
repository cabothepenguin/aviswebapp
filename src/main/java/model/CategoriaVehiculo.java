package model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "administracion_categorias")
public class CategoriaVehiculo {

    @Id
    @Column(name = "codigo", nullable = false)
    private Integer codigo;

    @Column(name = "descripcion", length = 100, nullable = false)
    private String descripcion;

    @Column(name = "estado", length = 20)
    private String estado;

    public CategoriaVehiculo() {}

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

    /**
     * equals/hashCode por ID (opcional pero recomendado)
     */
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
