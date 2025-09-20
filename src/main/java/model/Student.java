package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "estudiantes")
public class Student {
    @Id
    private Integer id;
    @Column(name = "nombre")
    private String name;
    @Column(name = "apellidos")
    private String lastName;
    @Column(name = "correo")
    private String email;
    @Column(name = "telefono")
    private String phone;
    public Student() {}
    public Student(Integer id, String name, String lastName, String email, String phone) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
