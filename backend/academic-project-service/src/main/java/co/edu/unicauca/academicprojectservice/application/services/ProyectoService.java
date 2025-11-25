package co.edu.unicauca.academicprojectservice.application.services;


import co.edu.unicauca.academicprojectservice.application.dto.ProyectoDTO;
import co.edu.unicauca.academicprojectservice.application.dto.ProyectoInfoDTO;
import co.edu.unicauca.academicprojectservice.application.port.output.DbPort;
import co.edu.unicauca.academicprojectservice.application.port.output.messaging.MessagingPort;
import co.edu.unicauca.academicprojectservice.application.port.output.notification.NotificationPort;
import co.edu.unicauca.academicprojectservice.domain.model.DocenteId;
import co.edu.unicauca.academicprojectservice.domain.model.EstudianteId;
import co.edu.unicauca.academicprojectservice.domain.model.FormatoA;


import co.edu.unicauca.academicprojectservice.domain.model.Proyecto;
import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
// @RequiredArgsConstructor
public class ProyectoService {

    private final DbPort dbPort;
    private final MessagingPort messagingPort;
    private final NotificationPort notificationPort;


    public ProyectoService(DbPort dbPort, MessagingPort messagingPort, NotificationPort notificationPort) {
        this.dbPort = dbPort;
        this.messagingPort = messagingPort;
        this.notificationPort = notificationPort;
    }


    // ==================================== inicio migrado ======================================
    public List<ProyectoInfoDTO> listarInfoPorCorreoDocente(String correoDocente, String filtro) {
        return dbPort.listarInfoProyectosPorCorreoDocente(correoDocente, filtro);
    }

    private FormatoA getUltimoFormatoA(UUID proyectoId) {
        return dbPort.obtenerUltimoFormatoA(proyectoId);
    }


    public EstadoProyecto enforceAutoCancelIfNeeded(UUID proyectoId) {

        int observados = dbPort.contarFormatoAObservados(proyectoId);

        if (observados >= 3) {
            dbPort.actualizarEstadoProyecto(proyectoId, EstadoProyecto.FORMATOA_RECHAZADO);
        }

        return dbPort.obtenerEstadoProyecto(proyectoId);
    }


// ==================================== fin migrado ======================================


    public int getMaxVersionFormatoA(UUID proyectoId) {
        return dbPort.getMaxVersionFormatoA(proyectoId);
    }


    public void crearProyectoConArchivos(ProyectoDTO dto) {
        List<EstudianteId> estudiantes = dto.getEstudiantes().stream()
                .map(correo -> dbPort.buscarEstudianteIdPorCorreo(correo)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "No existe un estudiante con el correo: " + correo)))
                .toList();


        DocenteId docenteId = dbPort.buscarDocenteIdPorCorreo(dto.getDirector())
                .orElseThrow(() -> new IllegalArgumentException("No existe un docente con ese correo"));

        Proyecto proyecto = new Proyecto(dto.getTitulo(), estudiantes, docenteId, dto.getTipoProyecto());

        proyecto.agregarFormatoAInicial(dto.getFormatoA().getNombreFormato(), dto.getFormatoA().getBlob());

        if (dto.getCartaLaboral() != null) {
            proyecto.adjuntarCartaLaboral(dto.getCartaLaboral());
        }

        Proyecto proyectoGuardado = dbPort.guardarProyecto(proyecto);

        messagingPort.publicarMensajeRMQ(proyectoGuardado);
        notificationPort.notificarACoordinadores(proyectoGuardado);
    }
}
