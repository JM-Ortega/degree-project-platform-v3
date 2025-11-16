package co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.entity;

import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import co.edu.unicauca.shared.contracts.model.TipoProyecto;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "proyecto")
public class Proyecto {
    @Id
    private UUID id;

    private String titulo;

    @ManyToMany
    @JoinTable(
            name = "trabajo_estudiantes",
            joinColumns = @JoinColumn(name = "trabajo_id"),
            inverseJoinColumns = @JoinColumn(name = "estudiante_id")
    )
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Estudiante> estudiantes;

    @ManyToOne
    @JoinColumn(name = "director_id", nullable = false)
    private Docente director;

    @ManyToOne
    @JoinColumn(name = "codirector_id")
    private Docente codirector;

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FormatoA> formatosA = new ArrayList<>();

    private byte[] cartaLaboral;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "anteproyecto_id")
    private Anteproyecto anteproyecto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_proyecto", nullable = false)
    private TipoProyecto tipoProyecto;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_proyecto", nullable = false)
    private EstadoProyecto estadoProyecto;

    public Proyecto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) {this.id = id;}

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public List<Estudiante> getEstudiantes() { return estudiantes; }
    public void setEstudiantes(List<Estudiante> estudiantes) { this.estudiantes = estudiantes; }

    public Docente getDirector() { return director; }
    public void setDirector(Docente director) { this.director = director; }

    public Docente getCodirector() { return codirector; }
    public void setCodirector(Docente codirector) { this.codirector = codirector; }

    public TipoProyecto getTipoProyecto() { return tipoProyecto; }
    public void setTipoProyecto(TipoProyecto tipoProyecto) { this.tipoProyecto = tipoProyecto; }

    public byte[] getCartaLaboral() { return cartaLaboral; }
    public void setCartaLaboral(byte[] cartaLaboral) { this.cartaLaboral = cartaLaboral; }

    public EstadoProyecto getEstadoProyecto() { return estadoProyecto; }
    public void setEstadoProyecto(EstadoProyecto estadoProyecto) { this.estadoProyecto = estadoProyecto; }

    public List<FormatoA> getFormatosA() {return formatosA;}
    public void addFormato(FormatoA formato) {this.formatosA.add(formato);}

    public Anteproyecto getAnteproyecto() {return anteproyecto;}
    public void setAnteproyecto(Anteproyecto anteproyecto) {this.anteproyecto = anteproyecto;}
}
