package co.edu.unicauca.academicprojectservice.domain.model;

import co.edu.unicauca.academicprojectservice.domain.exceptions.DomainException;
import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;

import java.time.LocalDate;
import java.util.UUID;


public class FormatoA {

    private final UUID id;

    private final int nroVersion;

    private String nombreFormato;

    private final LocalDate fechaCreacion;

    private byte[] blob;

    private EstadoFormatoA estado;


    public FormatoA(UUID id, int nroVersion, String nombreFormato, LocalDate fechaCreacion, byte[] blob, EstadoFormatoA estado) {
        this.id = id;
        this.nroVersion = nroVersion;
        this.nombreFormato = nombreFormato;
        this.fechaCreacion = fechaCreacion;
        this.blob = blob;
        this.estado = estado;
    }

    public static FormatoA crearInicial(String nombreFormato, byte[] blob) {
        if (nombreFormato == null || nombreFormato.isBlank()) {
            throw new DomainException("El nombre del FormatoA es obligatorio.");
        }
        if (blob == null || blob.length == 0) {
            throw new DomainException("El archivo del FormatoA es obligatorio.");
        }
        return new FormatoA(UUID.randomUUID(), 1, nombreFormato, LocalDate.now(), blob, EstadoFormatoA.PENDIENTE);
    }

    public static FormatoA crearNuevaVersion(int nuevaVersion, String nombreFormato, byte[] blob) {
        if (nombreFormato == null || nombreFormato.isBlank()) {
            throw new DomainException("El nombre del FormatoA es obligatorio.");
        }
        if (blob == null || blob.length == 0) {
            throw new DomainException("El archivo del FormatoA es obligatorio.");
        }
        if (nuevaVersion <= 0) {
            throw new DomainException("El numero de version del FormatoA debe ser positivo.");
        }
        return new FormatoA(UUID.randomUUID(), nuevaVersion, nombreFormato, LocalDate.now(), blob, EstadoFormatoA.PENDIENTE);
    }

    public void cambiarEstado(EstadoFormatoA nuevoEstado) {
        if (nuevoEstado == null) {
            throw new DomainException("El estado del FormatoA no puede ser nulo.");
        }
        this.estado = nuevoEstado;
    }

    public void aplicarResultadoRevision(EstadoFormatoA nuevoEstado, byte[] archivoRevisado, String nombreFormatoNuevo) {
        if (nuevoEstado == null) {
            throw new DomainException("El estado del FormatoA no puede ser nulo.");
        }
        this.estado = nuevoEstado;
        if (archivoRevisado != null && archivoRevisado.length > 0) {
            this.blob = archivoRevisado;
        }
        if (nombreFormatoNuevo != null && !nombreFormatoNuevo.isBlank()) {
            this.nombreFormato = nombreFormatoNuevo;
        }
    }


    public UUID getId() {
        return id;
    }

    public int getNroVersion() {
        return nroVersion;
    }

    public String getNombreFormato() {
        return nombreFormato;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public byte[] getBlob() {
        return blob;
    }

    public EstadoFormatoA getEstado() {
        return estado;
    }

}

