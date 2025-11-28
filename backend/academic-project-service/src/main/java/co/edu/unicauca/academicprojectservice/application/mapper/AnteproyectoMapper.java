package co.edu.unicauca.academicprojectservice.application.mapper;

import co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity.Docente;
import co.edu.unicauca.academicprojectservice.domain.model.Anteproyecto;
import co.edu.unicauca.academicprojectservice.domain.model.DocenteId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnteproyectoMapper {

    @Mapping(target = "evaluadores", source = "evaluadores")
    Anteproyecto entityToDomain(
            co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity.Anteproyecto entity
    );

    @Mapping(target = "evaluadores", ignore = true)
    co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity.Anteproyecto domainToEntity(
            Anteproyecto domain
    );

    default List<DocenteId> mapEvaluadores(List<Docente> docentes) {
        if (docentes == null) return List.of();
        return docentes.stream()
                .map(d -> new DocenteId(d.getId()))
                .toList();
    }
}
