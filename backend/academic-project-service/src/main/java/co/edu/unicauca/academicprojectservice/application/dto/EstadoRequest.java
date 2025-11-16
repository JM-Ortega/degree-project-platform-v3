package co.edu.unicauca.academicprojectservice.application.dto;

import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;

public class EstadoRequest {
    private EstadoFormatoA estado;

    public EstadoFormatoA getEstado() {
        return estado;
    }

    public void setEstado(EstadoFormatoA estado) {
        this.estado = estado;
    }
}

