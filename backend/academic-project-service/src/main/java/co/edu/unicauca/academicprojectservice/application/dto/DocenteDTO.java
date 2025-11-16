package co.edu.unicauca.academicprojectservice.Old.infra.dto;


public class DocenteDTO {
    private String nombres;
    private String apellidos;
    private String celular;
    private String correo;
    private String departamento;

    public DocenteDTO() {}

    public DocenteDTO(String apellidos, String celular, String correo, String departamento, String nombres) {
        this.apellidos = apellidos;
        this.celular = celular;
        this.correo = correo;
        this.departamento = departamento;
        this.nombres = nombres;
    }

    public String getApellidos() {return apellidos;}
    public void setApellidos(String apellidos) {this.apellidos = apellidos;}

    public String getNombres() {return nombres;}
    public void setNombres(String nombres) {this.nombres = nombres;}

    public String getCelular() {return celular;}
    public void setCelular(String celular) {this.celular = celular;}

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
}