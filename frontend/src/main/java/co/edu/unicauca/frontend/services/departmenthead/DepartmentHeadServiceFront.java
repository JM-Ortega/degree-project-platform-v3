package co.edu.unicauca.frontend.services.departmenthead;

import co.edu.unicauca.frontend.dto.AnteproyectoDto;
import co.edu.unicauca.frontend.infra.dto.DocenteDTO;
import co.edu.unicauca.frontend.infra.dto.UsuarioDTO;
import co.edu.unicauca.frontend.infra.http.HttpClientException;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de aplicación del frontend para interactuar con el microservicio de "Department Head".
 *
 * Este servicio se encarga de obtener todos los anteproyectos sin evaluadores o buscar anteproyectos por nombre o ID.
 */
public class DepartmentHeadServiceFront {

    private final DepartmentHeadApi departmentHeadApi;

    /**
     * Crea una nueva instancia del servicio de "Department Head" del frontend.
     *
     * @param departmentHeadApi implementación concreta usada para llamar al backend.
     */
    public DepartmentHeadServiceFront(DepartmentHeadApi departmentHeadApi) {
        this.departmentHeadApi = departmentHeadApi;
    }

    /**
     * Obtiene los Anteproyectos sin evaluadores.
     *
     * @return lista de Anteproyectos sin evaluadores.
     * @throws Exception si ocurre un error de comunicación o deserialización.
     */
    public List<AnteproyectoDto> obtenerAnteproyectosSinEvaluadores() throws Exception {
        try {
            return departmentHeadApi.obtenerAnteproyectosSinEvaluadores();
        } catch (HttpClientException ex) {
            throw new Exception("Error al obtener Anteproyectos sin evaluadores: " + ex.getResponseBody(), ex);
        }
    }

    /**
     * Busca Anteproyectos por nombre o ID.
     *
     * @param nombre el nombre del Anteproyecto a buscar.
     * @param id     el ID del Anteproyecto a buscar como String.
     * @return lista de Anteproyectos que coinciden con el nombre o ID.
     * @throws Exception si ocurre un error de comunicación o deserialización.
     */
    public List<AnteproyectoDto> buscarAnteproyectos(String nombre, String id) throws Exception {
        try {
            return departmentHeadApi.buscarPorNombreOIdSinEvaluadores(nombre, id);
        } catch (HttpClientException ex) {
            throw new Exception("Error al buscar Anteproyectos: " + ex.getResponseBody(), ex);
        }
    }

    public  List<DocenteDTO> obtenerDocentes(String correoJefe) throws Exception{
        try {
            return departmentHeadApi.obtenerDocentesPorCorreoJefe(correoJefe);
        } catch (HttpClientException ex) {
            throw new Exception("Error al obtener docentes: " + ex.getResponseBody(), ex);
        }
    }

    public void asignarEvaluadores(String correoDocente1, String correoDocente2, UUID idAnteproyecto) throws Exception {
        try {
            departmentHeadApi.asignarEvaluadores(correoDocente1, correoDocente2, idAnteproyecto);
        } catch (HttpClientException ex) {
            throw new Exception("Error al obtener docentes: " + ex.getResponseBody(), ex);
        }
    }
}