package co.edu.unicauca.frontend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY) // no serializa "" ni listas vacías
public class RegistroPersonaDto {

    private String nombres;
    private String apellidos;
    private String email;
    private String password;
    private String celular;
    private String programa;       // nombre exacto del enum en shared-contracts
    private List<String> roles;    // p.ej. ["Estudiante","Docente"]
    private String departamento;   // nombre exacto del enum en shared-contracts

    public RegistroPersonaDto() {
    }

    // MISMA firma, pero delega a setters para normalizar
    public RegistroPersonaDto(String nombres,
                              String apellidos,
                              String email,
                              String password,
                              String celular,
                              String programa,
                              List<String> roles,
                              String departamento) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.password = password;
        this.celular = celular;
        setPrograma(programa);
        setRoles(roles);
        setDepartamento(departamento);
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getPrograma() {
        return programa;
    }
    public void setPrograma(String programa) {
        this.programa = (programa == null || programa.isBlank()) ? null : programa;
    }

    public List<String> getRoles() {
        return roles;
    }
    public void setRoles(List<String> roles) {
        this.roles = (roles == null || roles.isEmpty()) ? null : roles;
    }

    public String getDepartamento() {
        return departamento;
    }
    public void setDepartamento(String departamento) {
        this.departamento = (departamento == null || departamento.isBlank()) ? null : departamento;
    }
}
