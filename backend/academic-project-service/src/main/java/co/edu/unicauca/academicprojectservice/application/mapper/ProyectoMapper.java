package co.edu.unicauca.academicprojectservice.application.mapper;

import co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity.Estudiante;
import co.edu.unicauca.academicprojectservice.domain.model.DocenteId;
import co.edu.unicauca.academicprojectservice.domain.model.EstudianteId;
import co.edu.unicauca.academicprojectservice.domain.model.Proyecto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring", uses = { FormatoAMapper.class, AnteproyectoMapper.class })
public interface ProyectoMapper {
    Proyecto entityToDomain(co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity.Proyecto proyecto);

    default EstudianteId map(
            co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity.Estudiante e
    ) {
        return e == null ? null : new EstudianteId(e.getId());
    }

    default DocenteId map(
            co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity.Docente d
    ) {
        return d == null ? null : new DocenteId(d.getId());
    }

    default List<EstudianteId> mapEstudiantes(
            List<Estudiante> estudiantes
    ) {
        if (estudiantes == null) return List.of();
        return estudiantes.stream().map(this::map).toList();
    }
}
