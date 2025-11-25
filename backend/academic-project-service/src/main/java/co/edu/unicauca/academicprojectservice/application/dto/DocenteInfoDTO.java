package co.edu.unicauca.academicprojectservice.application.dto;

import java.util.UUID;

public class DocenteInfoDTO {

    private UUID id;
    private String nombres;
    private String apellidos;
    private String celular;
    private String correo;
    private String departamento;

    public DocenteInfoDTO() {
    }

    public DocenteInfoDTO(UUID id,
                          String nombres,
                          String apellidos,
                          String celular,
                          String correo,
                          String departamento) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.celular = celular;
        this.correo = correo;
        this.departamento = departamento;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
}
