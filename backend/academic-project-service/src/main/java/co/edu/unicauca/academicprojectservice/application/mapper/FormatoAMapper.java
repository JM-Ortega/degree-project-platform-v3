package co.edu.unicauca.academicprojectservice.application.mapper;

import co.edu.unicauca.academicprojectservice.domain.model.FormatoA;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FormatoAMapper {

    // --- Dominio → Persistencia ---
    List<co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity.FormatoA> toEntityList(List<FormatoA> formatos);

    // --- Persistencia → Dominio ---
    FormatoA toDomain(co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity.FormatoA entity);
}