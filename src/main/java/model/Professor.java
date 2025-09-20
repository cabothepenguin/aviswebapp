package model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

public class Professor {
    @Id
    private Integer id;
    @Column(name = "nombre")
    private String name;
    @Column(name = "apellidos")
    private String lastName;
    @Column(name = "correo")
    private String email;
    @Column(name = "imagen ")
    private byte[] image;
    @Column(name = "nombre de imagen")
    private String imageName;



    public Professor(Integer id, String name,String lastName, String email, byte[] image, String imageName) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.image = image;
        this.imageName = imageName;
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
}
