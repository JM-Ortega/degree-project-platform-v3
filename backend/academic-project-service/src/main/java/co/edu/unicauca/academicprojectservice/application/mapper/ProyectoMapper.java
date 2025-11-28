package co.edu.unicauca.academicprojectservice.application.mapper;

import co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity.Docente;
import co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity.Estudiante;
import co.edu.unicauca.academicprojectservice.domain.model.DocenteId;
import co.edu.unicauca.academicprojectservice.domain.model.EstudianteId;
import co.edu.unicauca.academicprojectservice.domain.model.Proyecto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = { FormatoAMapper.class, AnteproyectoMapper.class }
)
public interface ProyectoMapper {

    @Mapping(target = "estudiantesId", source = "estudiantes")   // usa mapEstudiantes(...)
    @Mapping(target = "directorId",    source = "director")      // usa map(Docente)
    @Mapping(target = "codirectorId",  source = "codirector")    // idem
    @Mapping(target = "formatosA",     source = "formatosA")     // usa FormatoAMapper
    Proyecto entityToDomain(
            co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity.Proyecto proyecto
    );

    // ===== helpers que MapStruct usa automáticamente =====

    default EstudianteId map(Estudiante e) {
        return e == null ? null : new EstudianteId(e.getId());
    }

    default DocenteId map(Docente d) {
        return d == null ? null : new DocenteId(d.getId());
    }

    // este se usa cuando hay List<Estudiante> -> List<EstudianteId>
    default List<EstudianteId> mapEstudiantes(List<Estudiante> estudiantes) {
        if (estudiantes == null) return List.of();
        return estudiantes.stream()
                .map(this::map)
                .toList();
    }
}
