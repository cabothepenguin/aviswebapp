package dto;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@ViewScoped
public class CourseDto implements Serializable {
    private Integer nrc;
    private String name;
    private String credits;
    private String code;
    private ProfessorDto professor;


    public Integer getNrc() {
        return nrc;
    }

    public void setNrc(Integer nrc) {
        this.nrc = nrc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ProfessorDto getProfessor() {
        return professor;
    }

    public void setProfessor(ProfessorDto professor) {
        this.professor = professor;
    }
}
