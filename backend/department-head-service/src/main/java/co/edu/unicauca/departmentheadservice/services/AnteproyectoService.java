package co.edu.unicauca.departmentheadservice.services;

import co.edu.unicauca.departmentheadservice.access.AnteproyectoRepository;
import co.edu.unicauca.departmentheadservice.entities.Anteproyecto;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnteproyectoService {

    private final AnteproyectoRepository anteproyectoRepository;

    public AnteproyectoService(AnteproyectoRepository anteproyectoRepository) {
        this.anteproyectoRepository = anteproyectoRepository;
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
}
