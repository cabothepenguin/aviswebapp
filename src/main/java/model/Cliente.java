package model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "administracion_clientes")
public class Cliente {

    @Id
    @Column(name = "cedula", nullable = false, length = 50)
    private String cedula;

    @Column(name = "nombreclc", length = 150, nullable = false)
    private String nombre;

    @Column(name = "telefono")
    private Integer telefono;

    @Column(name = "correo", length = 150)
    private String correo;

    public Cliente() {}

    public Cliente(String cedula, String nombre, Integer telefono, String correo) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getTelefono() {
        return telefono;
    }

    public void setTelefono(Integer telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
