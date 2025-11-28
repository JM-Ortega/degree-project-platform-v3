package co.edu.unicauca.coordinatorservice.controller;

import co.edu.unicauca.coordinatorservice.entity.Docente;
import co.edu.unicauca.coordinatorservice.entity.Estudiante;
import co.edu.unicauca.coordinatorservice.entity.FormatoA;
import co.edu.unicauca.shared.contracts.events.academic.DTOs.EstudianteDTO;
import co.edu.unicauca.shared.contracts.events.academic.DTOs.ProyectoDTO;
import co.edu.unicauca.shared.contracts.model.TipoProyecto;
import co.edu.unicauca.coordinatorservice.repository.DocenteRepository;
import co.edu.unicauca.coordinatorservice.repository.EstudianteRepository;
import co.edu.unicauca.coordinatorservice.repository.FormatoARepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ProyectoListener {
    private final FormatoARepository formatoARepository;
    private final DocenteRepository docenteRepository;
    private final EstudianteRepository estudianteRepository;

    public ProyectoListener(FormatoARepository formatoARepository, DocenteRepository docenteRepository,
                            EstudianteRepository estudianteRepository) {
        this.formatoARepository = formatoARepository;
        this.docenteRepository = docenteRepository;
        this.estudianteRepository = estudianteRepository;
    }

    @RabbitListener(queues = "${messaging.queues.project}")
    @Transactional
    public void recibirProyecto(ProyectoDTO dto) {
        System.out.println("📩 [RabbitMQ] Mensaje recibido en CoordinatorService: " + dto.getTitulo());

        // Formato A
        Optional<FormatoA> existingFormato = formatoARepository.findByProyectoId(dto.getId());
        FormatoA formato = existingFormato.orElse(new FormatoA());

        formato.setProyectoId(dto.getId());
        formato.setNombreProyecto(dto.getTitulo());
        //Si vamos a dejar los enums compartidos no tengo que hacer esto
        formato.setTipoProyecto(TipoProyecto.valueOf(dto.getTipoProyecto().toString()));

        //Director
        Optional<Docente> existingDirector = docenteRepository.findByEmail(dto.getDirector().getEmail());
        Docente director = existingDirector.orElse(new Docente());

        director.setNombres(dto.getDirector().getNombres());
        director.setApellidos(dto.getDirector().getApellidos());
        director.setEmail(dto.getDirector().getEmail());
        director.setCelular(dto.getDirector().getCelular());

        docenteRepository.save(director);
        formato.setDirector(director);

        //Coodirector
        if(dto.getCodirector()!=null){
            Optional<Docente> existingCoodirector = docenteRepository.findByEmail(dto.getCodirector().getEmail());
            Docente coodirector = existingCoodirector.orElse(new Docente());

            coodirector.setNombres(dto.getDirector().getNombres());
            coodirector.setApellidos(dto.getDirector().getApellidos());
            coodirector.setEmail(dto.getDirector().getEmail());
            coodirector.setCelular(dto.getDirector().getCelular());

            docenteRepository.save(coodirector);
            formato.setCoodirector(coodirector);
        }

        //Estudiantes
        List<Estudiante> estudiantes = new ArrayList<>();
        for(EstudianteDTO dto2 : dto.getEstudiantes()){
            Optional<Estudiante> existingEstudiante = estudianteRepository.findByEmail(dto2.getEmail());
            Estudiante estudiante = existingEstudiante.orElse(new Estudiante());

            estudiante.setEmail(dto2.getEmail());
            estudiante.setCelular(dto2.getCelular());
            estudiante.setPrograma(dto2.getPrograma());
            estudiantes.add(estudiante);

            estudianteRepository.save(estudiante);
        }
        formato.setEstudiantes(estudiantes);

        formatoARepository.save(formato);

        System.out.println("[CoordinatorService] FormatoA guardado/actualizado correctamente: " + formato.getNombreProyecto()+ " Version: "+ formato.getNroVersion());
    }
}
