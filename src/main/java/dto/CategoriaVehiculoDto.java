package dto;

import java.io.Serializable;
import java.util.Objects;

public class CategoriaVehiculoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer codigo;
    private String descripcion;
    private String estado;



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

    public String getactivo() {

        return estado;
    }

    public void setHabilitada(String habilitada) {

        this.estado = habilitada;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CategoriaVehiculoDto that = (CategoriaVehiculoDto) o;
        return Objects.equals(codigo, that.codigo) && Objects.equals(descripcion, that.descripcion) && Objects.equals(estado, that.estado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo, descripcion, estado);
    }
}
