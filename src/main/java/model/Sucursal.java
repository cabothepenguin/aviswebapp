package model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "administracion_sucursales")
public class Sucursal {

    @Id
    @Column(name = "codigo", nullable = false)
    private Integer codigo;

    @Column(name = "nombre", length = 120, nullable = false)
    private String nombre;

    @Column(name = "encargado", length = 120)
    private String encargado;

    @Column(name = "direccion", length = 255)
    private String direccion;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "correo", length = 150)
    private String correo;

    public Sucursal() {}

    public Sucursal(Integer codigo, String nombre, String encargado,
                    String direccion, String telefono, String correo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.encargado = encargado;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correo = correo;
    }

    public Integer getCodigo() {

        return codigo;
    }

    public void setCodigo(Integer codigo) {

        this.codigo = codigo;
    }

    public String getNombre() {

        return nombre;
    }

    public void setNombre(String nombre) {

        this.nombre = nombre;
    }

    public String getEncargado() {

        return encargado;
    }

    public void setEncargado(String encargado) {

        this.encargado = encargado;
    }

    public String getDireccion() {

        return direccion;
    }

    public void setDireccion(String direccion) {

        this.direccion = direccion;
    }

    public String getTelefono() {

        return telefono;
    }

    public void setTelefono(String telefono) {

        this.telefono = telefono;
    }

    public String getCorreo() {

        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
