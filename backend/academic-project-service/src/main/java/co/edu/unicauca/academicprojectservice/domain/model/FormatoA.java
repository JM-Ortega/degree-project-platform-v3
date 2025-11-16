package co.edu.unicauca.academicprojectservice.domain.model;

import co.edu.unicauca.academicprojectservice.domain.exceptions.DomainException;
import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;

import java.time.LocalDate;
import java.util.UUID;


public class FormatoA {

    private final UUID id;

    private final int nroVersion;

    private final String nombreFormato;

    private final LocalDate fechaCreacion;

    private final byte[] blob;

    private EstadoFormatoA estado;


    public FormatoA(int nroVersion, String nombreFormato, byte[] blob) {
        if (nroVersion <= 0) {
            throw new DomainException("El numero de version del FormatoA debe ser positivo.");
        }
        if (nombreFormato == null || nombreFormato.isBlank()) {
            throw new DomainException("El nombre del FormatoA es obligatorio.");
        }
        if (blob == null || blob.length == 0) {
            throw new DomainException("El archivo del FormatoA es obligatorio.");
        }

        this.id = UUID.randomUUID();
        this.nroVersion = nroVersion;
        this.nombreFormato = nombreFormato;
        this.fechaCreacion = LocalDate.now();
        this.blob = blob;
        this.estado = EstadoFormatoA.PENDIENTE;
    }

    public static FormatoA crearInicial(String nombreFormato, byte[] blob) {
        return new FormatoA(1, nombreFormato, blob);
    }

    public static FormatoA crearNuevaVersion(int nuevaVersion, String nombreFormato, byte[] blob) {
        return new FormatoA(nuevaVersion, nombreFormato, blob);
    }

    public void cambiarEstado(EstadoFormatoA nuevoEstado) {
        if (nuevoEstado == null) {
            throw new DomainException("El estado del FormatoA no puede ser nulo.");
        }
        this.estado = nuevoEstado;
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

