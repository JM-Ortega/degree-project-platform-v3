package co.edu.unicauca.academicprojectservice.domain.model;

import co.edu.unicauca.academicprojectservice.domain.exceptions.DomainException;
import co.edu.unicauca.academicprojectservice.domain.exceptions.formatoa.FormatoANoObservadoException;
import co.edu.unicauca.academicprojectservice.domain.exceptions.formatoa.MaximoDeVersionesFormatoAException;
import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;
import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import co.edu.unicauca.shared.contracts.model.TipoProyecto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Proyecto {

    private final UUID id;
    private final String titulo;
    private final List<EstudianteId> estudiantesId;
    private final DocenteId directorId;
    private final List<FormatoA> formatosA;
    private final TipoProyecto tipoProyecto;
    private DocenteId codirectorId;
    private byte[] cartaLaboral;
    private Anteproyecto anteproyecto;
    private EstadoProyecto estadoProyecto;

    public Proyecto(UUID id, String titulo, List<EstudianteId> estudiantesId, DocenteId directorId, List<FormatoA> formatosA, TipoProyecto tipoProyecto, DocenteId codirectorId, byte[] cartaLaboral, Anteproyecto anteproyecto, EstadoProyecto estadoProyecto) {
        this.id = id;
        this.titulo = titulo;
        this.estudiantesId = List.copyOf(estudiantesId);
        this.directorId = directorId;
        this.formatosA = formatosA;
        this.tipoProyecto = tipoProyecto;
        this.codirectorId = codirectorId;
        this.cartaLaboral = cartaLaboral;
        this.anteproyecto = anteproyecto;
        this.estadoProyecto = estadoProyecto;
    }

    public static Proyecto crear(String titulo, List<EstudianteId> estudiantesId, DocenteId directorId, TipoProyecto tipoProyecto) {
        if (titulo == null || titulo.isBlank()) {
            throw new DomainException("El título del proyecto es obligatorio.");
        }
        if (estudiantesId == null || estudiantesId.isEmpty()) {
            throw new DomainException("El proyecto debe tener al menos un estudiante.");
        }
        if (tipoProyecto == TipoProyecto.TRABAJO_DE_INVESTIGACION && (estudiantesId.size() < 1 || estudiantesId.size() > 2)) {
            throw new DomainException("Un trabajo de investigación debe tener entre 1 y 2 estudiantes.");
        }
        if (tipoProyecto == TipoProyecto.PRACTICA_PROFESIONAL && estudiantesId.size() != 1) {
            throw new DomainException("Una práctica profesional debe tener exactamente 1 estudiante.");
        }
        if (directorId == null) {
            throw new DomainException("El proyecto debe tener director.");
        }
        if (tipoProyecto == null) {
            throw new DomainException("El tipo de proyecto es obligatorio.");
        }
        if (estudiantesId.size() > 2) {
            throw new DomainException("Un proyecto no puede tener más de 2 estudiantes.");
        }

        return new Proyecto(UUID.randomUUID(), titulo, estudiantesId, directorId, new ArrayList<>(), tipoProyecto, null, null, null, EstadoProyecto.PRIMERA_REVISION_FORMATOA);
    }

    public void asignarCodirector(DocenteId codirectorId) {
        if (codirectorId == null) {
            throw new DomainException("El codirector no puede ser nulo.");
        }
        this.codirectorId = codirectorId;
    }

    public void adjuntarCartaLaboral(byte[] cartaLaboral) {
        if (tipoProyecto == TipoProyecto.TRABAJO_DE_INVESTIGACION) {
            throw new DomainException("Los trabajos de investigación no deben tener carta laboral.");
        }
        if (cartaLaboral == null || cartaLaboral.length == 0) {
            throw new DomainException("La carta laboral no puede estar vacía.");
        }
        this.cartaLaboral = cartaLaboral;
    }

    public void agregarFormatoAInicial(String nombreFormato, byte[] archivo) {
        if (!formatosA.isEmpty()) {
            throw new DomainException("El Formato A inicial ya fue creado.");
        }
        if (estadoProyecto != EstadoProyecto.PRIMERA_REVISION_FORMATOA) {
            throw new DomainException("El Formato A inicial solo puede crearse en PRIMERA_REVISION_FORMATOA.");
        }
        FormatoA formato = FormatoA.crearInicial(nombreFormato, archivo);
        this.formatosA.add(formato);
    }

    public void agregarNuevaVersionFormatoA(String nombreFormato, byte[] archivo) {
        if (estadoProyecto != EstadoProyecto.SEGUNDA_REVISION_FORMATOA && estadoProyecto != EstadoProyecto.TERCERA_REVISION_FORMATOA) {
            throw new DomainException("Solo se puede crear una nueva versión de Formato A en segunda o tercera revisión.");
        }

        if (formatosA.isEmpty()) {
            throw new DomainException("No existe Formato A inicial para versionar.");
        }

        FormatoA ultimo = formatosA.getLast();

        if (ultimo.getEstado() != EstadoFormatoA.OBSERVADO) {
            throw new FormatoANoObservadoException();
        }

        if (formatosA.size() >= 3) {
            throw new MaximoDeVersionesFormatoAException();
        }

        int nuevaVersion = ultimo.getNroVersion() + 1;
        FormatoA nuevo = FormatoA.crearNuevaVersion(nuevaVersion, nombreFormato, archivo);
        this.formatosA.add(nuevo);
    }

    public void registrarResultadoRevisionFormatoA(EstadoFormatoA nuevoEstado) {
        if (nuevoEstado == null) {
            throw new DomainException("El nuevo estado del Formato A no puede ser nulo.");
        }

        if (formatosA.isEmpty()) {
            throw new DomainException("El proyecto no tiene Formato A para revisar.");
        }

        if (estadoProyecto == EstadoProyecto.FORMATOA_RECHAZADO
                || estadoProyecto == EstadoProyecto.ANTEPROYECTO_ENVIADO
                || estadoProyecto == EstadoProyecto.EN_REVISION_ANTEPROYECTO
                || estadoProyecto == EstadoProyecto.FORMATOA_ACEPTADO) {
            throw new DomainException("El estado del proyecto no permite revisar Formato A.");
        }

        if (nuevoEstado == EstadoFormatoA.PENDIENTE) {
            throw new DomainException("No se puede registrar revisión con estado PENDIENTE.");
        }

        FormatoA ultimo = formatosA.getLast();

        if (nuevoEstado == EstadoFormatoA.APROBADO) {
            if (tipoProyecto == TipoProyecto.PRACTICA_PROFESIONAL && cartaLaboral == null) {
                throw new DomainException("La práctica profesional requiere carta laboral antes de aprobar el Formato A.");
            }

            this.estadoProyecto = EstadoProyecto.FORMATOA_ACEPTADO;
        } else if (nuevoEstado == EstadoFormatoA.OBSERVADO) {
            if (estadoProyecto == EstadoProyecto.PRIMERA_REVISION_FORMATOA) {
                this.estadoProyecto = EstadoProyecto.SEGUNDA_REVISION_FORMATOA;
            } else if (estadoProyecto == EstadoProyecto.SEGUNDA_REVISION_FORMATOA) {
                this.estadoProyecto = EstadoProyecto.TERCERA_REVISION_FORMATOA;
            } else if (estadoProyecto == EstadoProyecto.TERCERA_REVISION_FORMATOA) {
                this.estadoProyecto = EstadoProyecto.FORMATOA_RECHAZADO;
            }
        }


        ultimo.cambiarEstado(nuevoEstado);
    }


    public void crearAnteproyecto(String nombreArchivo, String descripcion, String titulo, byte[] archivo) {
        if (estadoProyecto != EstadoProyecto.FORMATOA_ACEPTADO) {
            throw new DomainException("Solo se puede crear el anteproyecto cuando el Formato A está aceptado.");
        }
        if (this.anteproyecto != null) {
            throw new DomainException("El proyecto ya tiene anteproyecto.");
        }
        this.anteproyecto = Anteproyecto.crear(nombreArchivo, descripcion, titulo, archivo);
        this.estadoProyecto = EstadoProyecto.ANTEPROYECTO_ENVIADO;
    }

    public void marcarAnteproyectoEnRevision() {
        if (anteproyecto == null) {
            throw new DomainException("El proyecto no tiene anteproyecto.");
        }
        if (!anteproyecto.tieneCantidadValidaDeEvaluadores()) {
            throw new DomainException("El anteproyecto debe tener entre 1 y 2 evaluadores para entrar en revisión.");
        }
        if (estadoProyecto != EstadoProyecto.ANTEPROYECTO_ENVIADO) {
            throw new DomainException("El anteproyecto solo puede pasar a revisión desde ANTEPROYECTO_ENVIADO.");
        }
        this.estadoProyecto = EstadoProyecto.EN_REVISION_ANTEPROYECTO;
    }

    public UUID getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public List<EstudianteId> getEstudiantesId() {
        return estudiantesId;
    }

    public DocenteId getDirectorId() {
        return directorId;
    }

    public DocenteId getCodirectorId() {
        return codirectorId;
    }

    public List<FormatoA> getFormatosA() {
        return List.copyOf(formatosA);
    }

    public byte[] getCartaLaboral() {
        return cartaLaboral;
    }

    public Anteproyecto getAnteproyecto() {
        return anteproyecto;
    }

    public TipoProyecto getTipoProyecto() {
        return tipoProyecto;
    }

    public EstadoProyecto getEstadoProyecto() {
        return estadoProyecto;
    }
}
