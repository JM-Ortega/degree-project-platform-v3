package co.edu.unicauca.academicprojectservice.domain.model;

import co.edu.unicauca.academicprojectservice.domain.exceptions.DomainException;

import java.time.LocalDate;
import java.util.*;

public class Anteproyecto {

    private final UUID id;
    private final String nombreArchivo;
    private final String descripcion;
    private final String titulo;
    private byte[] blob;
    private final LocalDate fechaCreacion;
    private List<DocenteId> evaluadores;

    private static final int MAX_EVALUADORES = 2;

    public Anteproyecto(String nombreArchivo,
                         String descripcion,
                         String titulo,
                         byte[] blob) {

        if (nombreArchivo == null || nombreArchivo.isBlank()) {
            throw new DomainException("El nombre de archivo del anteproyecto es obligatorio.");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new DomainException("La descripción del anteproyecto es obligatoria.");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new DomainException("El título del anteproyecto es obligatorio.");
        }
        if (blob == null || blob.length == 0) {
            throw new DomainException("El archivo del anteproyecto es obligatorio.");
        }

        this.id = UUID.randomUUID();
        this.nombreArchivo = nombreArchivo;
        this.descripcion = descripcion;
        this.titulo = titulo;
        this.blob = blob;
        this.fechaCreacion = LocalDate.now();
        this.evaluadores = new ArrayList<>();
    }

    public static Anteproyecto crear(String nombreArchivo,
                                     String descripcion,
                                     String titulo,
                                     byte[] blob) {
        return new Anteproyecto(nombreArchivo, descripcion, titulo, blob);
    }

    public void asignarEvaluadores(List<DocenteId> nuevosEvaluadores) {

        if (nuevosEvaluadores == null || nuevosEvaluadores.isEmpty()) {
            throw new DomainException("El anteproyecto debe tener al menos un evaluador.");
        }

        if (nuevosEvaluadores.size() > MAX_EVALUADORES) {
            throw new DomainException("Un anteproyecto no puede tener más de " + MAX_EVALUADORES + " evaluadores.");
        }

        if (nuevosEvaluadores.stream().anyMatch(Objects::isNull)) {
            throw new DomainException("Los evaluadores del anteproyecto no pueden ser nulos.");
        }

        Set<DocenteId> sinDuplicados = new HashSet<>(nuevosEvaluadores);
        if (sinDuplicados.size() != nuevosEvaluadores.size()) {
            throw new DomainException("Los evaluadores del anteproyecto no pueden repetirse.");
        }

        this.evaluadores = List.copyOf(nuevosEvaluadores);
    }

    public boolean tieneCantidadValidaDeEvaluadores() {
        return evaluadores != null
                && !evaluadores.isEmpty()
                && evaluadores.size() <= MAX_EVALUADORES;
    }

    public void actualizarArchivo(byte[] nuevoBlob) {
        if (nuevoBlob == null || nuevoBlob.length == 0) {
            throw new DomainException("El archivo del anteproyecto no puede ser vacío.");
        }
        this.blob = nuevoBlob;
    }

    public UUID getId() {
        return id;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public byte[] getBlob() {
        return blob;
    }

    public List<DocenteId> getEvaluadores() {
        return Collections.unmodifiableList(evaluadores);
    }
}
