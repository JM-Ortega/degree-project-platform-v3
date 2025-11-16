package co.edu.unicauca.academicprojectservice.application.dto;

import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;

import java.time.LocalDate;

public record FormatoADTO(
        String nombreFormato,
        byte[] blob,
        int nroVersion,
        LocalDate fechaCreacion,
        EstadoFormatoA estado
) {}
