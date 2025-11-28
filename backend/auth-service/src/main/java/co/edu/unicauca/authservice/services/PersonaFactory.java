package co.edu.unicauca.authservice.services;

import co.edu.unicauca.authservice.domain.entities.*;
import co.edu.unicauca.shared.contracts.model.Rol;
import co.edu.unicauca.authservice.dto.RegistroPersonaDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Fábrica encargada de construir instancias concretas de {@link Persona}
 * (como {@link Estudiante}, {@link Docente}, {@link Coordinador} o
 * {@link JefeDeDepartamento}) a partir de la información recibida en
 * un {@link RegistroPersonaDto}.
 *
 * <p>El tipo de entidad concreta se determina según el rol principal
 * deducido a partir de la lista de roles proporcionada. El orden de
 * prioridad de roles va del más alto al más básico:</p>
 *
 * <ol>
 *   <li>{@link Rol#JEFE_DE_DEPARTAMENTO}</li>
 *   <li>{@link Rol#COORDINADOR}</li>
 *   <li>{@link Rol#DOCENTE}</li>
 *   <li>{@link Rol#ESTUDIANTE}</li>
 * </ol>
 *
 * <p>De esta forma, si un usuario tiene múltiples roles, se elegirá
 * el más alto según esta jerarquía para definir la subclase de persona
 * que se persistirá.</p>
 */
@Component
public class PersonaFactory {

    /** Orden de prioridad de roles (de mayor a menor privilegio). */
    private static final List<Rol> PRIORIDAD = List.of(
            Rol.JEFE_DE_DEPARTAMENTO,
            Rol.COORDINADOR,
            Rol.DOCENTE,
            Rol.ESTUDIANTE
    );

    /**
     * Crea una instancia concreta de {@link Persona} a partir del DTO recibido.
     *
     * @param dto      Datos de registro provenientes del cliente.
     * @param usuario  Entidad {@link Usuario} ya inicializada (con hash y roles).
     * @return Subclase concreta de {@link Persona} acorde al rol principal.
     * @throws IllegalArgumentException si la lista de roles está vacía.
     */
    public Persona crearDesdeDto(RegistroPersonaDto dto, Usuario usuario) {
        Rol rolPrincipal = elegirRolPrincipal(dto.roles());
        UUID id = UUID.randomUUID();

        return switch (rolPrincipal) {
            case ESTUDIANTE -> new Estudiante(
                    id, null,
                    dto.nombres(),
                    dto.apellidos(),
                    dto.celular(),
                    dto.programa(),
                    usuario
            );

            case DOCENTE -> new Docente(
                    id, null,
                    dto.nombres(),
                    dto.apellidos(),
                    dto.celular(),
                    dto.programa(),
                    usuario,
                    dto.departamento() // solo aplica a docentes
            );

            case COORDINADOR -> new Coordinador(
                    id, null,
                    dto.nombres(),
                    dto.apellidos(),
                    dto.celular(),
                    dto.programa(),
                    usuario,
                    dto.programa() // el coordinador tiene programa, no departamento
            );

            case JEFE_DE_DEPARTAMENTO -> new JefeDeDepartamento(
                    id, null,
                    dto.nombres(),
                    dto.apellidos(),
                    dto.celular(),
                    dto.programa(),
                    usuario,
                    dto.departamento()
            );
        };
    }

    /**
     * Determina el rol principal a partir de la lista recibida,
     * siguiendo la prioridad establecida internamente.
     *
     * @param roles Lista de roles asociados al usuario.
     * @return El rol con mayor prioridad dentro de la lista.
     */
    private Rol elegirRolPrincipal(List<Rol> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("Debe especificarse al menos un rol");
        }

        for (Rol rol : PRIORIDAD) {
            if (roles.contains(rol)) {
                return rol;
            }
        }
        // fallback, debería ser imposible llegar aquí
        return roles.get(0);
    }
}
