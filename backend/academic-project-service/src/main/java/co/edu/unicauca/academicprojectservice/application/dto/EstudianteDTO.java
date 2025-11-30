package co.edu.unicauca.academicprojectservice.application.dto;

public class EstudianteDTO {

    private String nombres;
    private String apellidos;
    private String celular;
    private String correo;
    private String programa;

    public EstudianteDTO() {}

    public EstudianteDTO(String apellidos, String celular, String correo, String nombres, String programa) {
        this.apellidos = apellidos;
        this.celular = celular;

        this.correo = correo;
        this.nombres = nombres;
        this.programa = programa;
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

    public String getPrograma() {
        return programa;
    }

    public void setPrograma(String programa) {
        this.programa = programa;
    }
}
