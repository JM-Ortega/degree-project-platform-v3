package co.edu.unicauca.coordinatorservice.infra.DTOSInternos;

import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormatoAResumenDTO {
    private UUID id; // para luego usarlo en la descarga
    private String nombreProyecto;
    private String nombreDirector;
    private String tipoProyecto;
    private LocalDate fechaSubida;
    private EstadoFormatoA estadoFormatoA;
    private int nroVersion ;
    private String nombreFormatoA;
}

