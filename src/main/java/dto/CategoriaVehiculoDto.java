package dto;

import java.io.Serializable;

public class CategoriaVehiculoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer codigo;
    private String descripcion;
    private Boolean estado;  // true = activa y false = deshabilitada

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


    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Boolean getHabilitada() {
        return estado;
    }

    public void setHabilitada(Boolean habilitada) {
        this.estado = habilitada;
    }
}
