package co.edu.unicauca.academicprojectservice.application.services;

import co.edu.unicauca.academicprojectservice.domain.model.Proyecto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ProyectoMapper {
     Proyecto toEntity(co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity.Proyecto proyecto);
     co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity.Proyecto toDomain(Proyecto proyecto);
}
