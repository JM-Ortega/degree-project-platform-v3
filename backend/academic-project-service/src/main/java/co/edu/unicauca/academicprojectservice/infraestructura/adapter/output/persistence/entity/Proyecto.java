package co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "proyecto")
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cartaLaboral_id")
    private CartaLaboral cartaLaboral;

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

    public Long getId() { return id; }
    public void setId(Long id) {this.id = id;}

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

    public CartaLaboral getCartaLaboral() { return cartaLaboral; }
    public void setCartaLaboral(CartaLaboral cartaLaboral) { this.cartaLaboral = cartaLaboral; }

    public EstadoProyecto getEstadoProyecto() { return estadoProyecto; }
    public void setEstadoProyecto(EstadoProyecto estadoProyecto) { this.estadoProyecto = estadoProyecto; }

    public List<FormatoA> getFormatosA() {return formatosA;}
    public void addFormato(FormatoA formato) {this.formatosA.add(formato);}

    public Anteproyecto getAnteproyecto() {return anteproyecto;}
    public void setAnteproyecto(Anteproyecto anteproyecto) {this.anteproyecto = anteproyecto;}
}
