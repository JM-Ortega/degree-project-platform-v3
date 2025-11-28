package co.edu.unicauca.coordinatorservice.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import co.edu.unicauca.shared.contracts.model.Programa;

@Entity
@Table(name = "coordinador")
public class Coordinador implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombres;
    private String correo;
    @Enumerated(EnumType.STRING)
    private Programa programa;

    public Coordinador() {
        this.id= null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }
}
