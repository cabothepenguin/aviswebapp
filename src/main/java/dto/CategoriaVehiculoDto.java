package dto;

import java.io.Serializable;

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
}
