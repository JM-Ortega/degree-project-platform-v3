package co.edu.unicauca.frontend.services.departmenthead;

import co.edu.unicauca.frontend.dto.AnteproyectoDto;
import co.edu.unicauca.frontend.infra.dto.DocenteDTO;

import java.util.List;
import java.util.UUID;

/**
 * Contrato que define las operaciones HTTP disponibles en el microservicio de Jefe de Departamento (DepartmentHead).
 */
public interface DepartmentHeadApi {

    /**
     * Obtiene todos los Anteproyectos que no tienen evaluadores asignados.
     *
     * @return lista de Anteproyectos sin evaluadores.
     * @throws Exception si ocurre un error de conexión o si el backend devuelve un estado no exitoso.
     */
    List<AnteproyectoDto> obtenerAnteproyectosSinEvaluadores() throws Exception;

    /**
     * Busca Anteproyectos por nombre o código.
     *
     * @param nombre el nombre del anteproyecto a buscar (puede ser null).
     * @param id el código del anteproyecto a buscar como String (puede ser null).
     * @return lista de Anteproyectos que coinciden con los parámetros proporcionados.
     * @throws Exception si ocurre un error de conexión o si el backend devuelve un estado no exitoso.
     */
    List<AnteproyectoDto> buscarPorNombreOIdSinEvaluadores(String nombre, String id) throws Exception;

    List<DocenteDTO> obtenerDocentesPorCorreoJefe(String correoJefe) throws Exception;

    void asignarEvaluadores(String correoDocente1, String correoDocente2, UUID idAnteproyecto) throws Exception;
}