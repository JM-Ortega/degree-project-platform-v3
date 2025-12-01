package co.edu.unicauca.departmentheadservice.services;

import co.edu.unicauca.departmentheadservice.access.AnteproyectoRepository;
import co.edu.unicauca.departmentheadservice.access.DocenteRepository;
import co.edu.unicauca.departmentheadservice.entities.Anteproyecto;
import co.edu.unicauca.departmentheadservice.entities.Docente;
import co.edu.unicauca.departmentheadservice.infra.messaging.DepartmentHeadEventsPublisher;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnteproyectoService {

    private final AnteproyectoRepository anteproyectoRepository;
    private final DocenteRepository docenteRepository;
    private final MesaggingService mesaggingService;
    private final NotificationService notificationService;

    public AnteproyectoService(AnteproyectoRepository anteproyectoRepository, DocenteRepository docenteRepository,
                               MesaggingService mesaggingService, NotificationService notificationService) {
        this.anteproyectoRepository = anteproyectoRepository;
        this.docenteRepository = docenteRepository;
        this.mesaggingService = mesaggingService;
        this.notificationService = notificationService;
    }

    public List<Anteproyecto> obtenerAnteproyectosSinEvaluadores() {
        return anteproyectoRepository.findByEvaluadoresIsEmpty();
    }

    /**
     * Busca los Anteproyectos sin evaluadores, filtrando por título o ID.
     *
     * @param nombre el título del anteproyecto (puede ser null o vacío)
     * @param id     el ID del anteproyecto como String (puede ser null o vacío)
     * @return lista de Anteproyectos sin evaluadores que coinciden con el título o ID.
     */
    public List<Anteproyecto> buscarPorNombreOIdSinEvaluadores(String nombre, String id) {

        Set<Anteproyecto> resultado = new HashSet<>();

        boolean buscarPorNombre = nombre != null && !nombre.trim().isEmpty();
        boolean buscarPorId = id != null && !id.trim().isEmpty();

        // Si no hay criterios, devolver todos
        if (!buscarPorNombre && !buscarPorId) {
            return anteproyectoRepository.findByEvaluadoresIsEmpty();
        }

        // Buscar por nombre
        if (buscarPorNombre) {
            List<Anteproyecto> porNombre =
                    anteproyectoRepository.findByEvaluadoresIsEmptyAndTituloContainingIgnoreCase(nombre.trim());

            resultado.addAll(porNombre);
        }

        // Buscar por ID (UUID)
        if (buscarPorId) {
            String idBusqueda = id.trim();
            try {
                UUID uuid = UUID.fromString(idBusqueda);

                // Buscar por el campo de dominio anteproyectoId (no el internalId)
                List<Anteproyecto> porId =
                        anteproyectoRepository.findByEvaluadoresIsEmptyAndAnteproyectoId(uuid);

                resultado.addAll(porId);

            } catch (IllegalArgumentException e) {
                System.err.println("ID proporcionado no es un UUID válido: " + idBusqueda);
            }
        }

        return new ArrayList<>(resultado);
    }

    public void asignarEvaluadores (String correoE1, String correoE2, UUID idAnteproyecto){
        Anteproyecto anteproyecto = anteproyectoRepository.findAnteproyectoByAnteproyectoId(idAnteproyecto);
        List<Docente> evaluadores = new ArrayList<>();
        evaluadores.add(docenteRepository.findByEmail(correoE1)
                .orElseThrow(() -> new IllegalArgumentException("Este correo no pertenece a un docente: " + correoE1)));
        evaluadores.add(docenteRepository.findByEmail(correoE2)
                .orElseThrow(() -> new IllegalArgumentException("Este correo no pertenece a un docente: " + correoE2)));
        anteproyecto.setEvaluadores(evaluadores);

        anteproyectoRepository.save(anteproyecto);
        notificationService.notificarEvaluadores(anteproyecto);
        mesaggingService.publicarMensaje(anteproyecto);
    }
}
