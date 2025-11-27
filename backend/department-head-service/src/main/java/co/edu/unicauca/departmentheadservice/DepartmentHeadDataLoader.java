package co.edu.unicauca.departmentheadservice;

import co.edu.unicauca.departmentheadservice.access.AnteproyectoRepository;
import co.edu.unicauca.departmentheadservice.access.DocenteRepository;
import co.edu.unicauca.departmentheadservice.entities.Anteproyecto;
import co.edu.unicauca.departmentheadservice.entities.Docente;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DepartmentHeadDataLoader implements CommandLineRunner {

    private final AnteproyectoRepository anteproyectoRepository;
    private final DocenteRepository docenteRepository;

    @Value("${seed.enabled:true}")
    private boolean seedEnabled;

    public DepartmentHeadDataLoader(AnteproyectoRepository anteproyectoRepository,
                                    DocenteRepository docenteRepository) {
        this.anteproyectoRepository = anteproyectoRepository;
        this.docenteRepository = docenteRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedEnabled) return;
        loadData();
    }

    private void loadData() {
        Docente docente1 = getOrCreateDocente("Juan Pérez", "juan.perez@unicauca.edu.co");
        Docente docente2 = getOrCreateDocente("Ana Gómez", "ana.gomez@unicauca.edu.co");
        Docente docente3 = getOrCreateDocente("Carlos Ruiz", "carlos.ruiz@unicauca.edu.co");

        List<Anteproyecto> nuevos = new ArrayList<>();

        for (int i = 1; i <= 15; i++) {
            UUID apId = UUID.nameUUIDFromBytes(("ap-" + i).getBytes());
            UUID proyectoId = UUID.nameUUIDFromBytes(("proj-" + i).getBytes());

            if (!anteproyectoRepository.existsByAnteproyectoId(apId)) {
                nuevos.add(new Anteproyecto(
                        apId,
                        proyectoId,
                        "Anteproyecto sin evaluadores " + i,
                        "Descripción del anteproyecto sin evaluadores " + i,
                        LocalDate.now(),
                        List.of(),
                        "estudiante" + i + "@unicauca.edu.co",
                        "director" + i + "@unicauca.edu.co",
                        "SISTEMAS"
                ));
            }
        }

        for (int i = 16; i <= 18; i++) {
            UUID apId = UUID.nameUUIDFromBytes(("ap-" + i).getBytes());
            UUID proyectoId = UUID.nameUUIDFromBytes(("proj-" + i).getBytes());

            if (!anteproyectoRepository.existsByAnteproyectoId(apId)) {
                nuevos.add(new Anteproyecto(
                        apId,
                        proyectoId,
                        "Anteproyecto con 2 evaluadores " + i,
                        "Descripción del anteproyecto con 2 evaluadores " + i,
                        LocalDate.now(),
                        List.of(docente1, docente2),
                        "estudiante" + i + "@unicauca.edu.co",
                        "director" + i + "@unicauca.edu.co",
                        "SISTEMAS"
                ));
            }
        }

        for (int i = 19; i <= 20; i++) {
            UUID apId = UUID.nameUUIDFromBytes(("ap-" + i).getBytes());
            UUID proyectoId = UUID.nameUUIDFromBytes(("proj-" + i).getBytes());

            if (!anteproyectoRepository.existsByAnteproyectoId(apId)) {
                nuevos.add(new Anteproyecto(
                        apId,
                        proyectoId,
                        "Anteproyecto con 1 evaluador " + i,
                        "Descripción del anteproyecto con 1 evaluador " + i,
                        LocalDate.now(),
                        List.of(docente3),
                        "estudiante" + i + "@unicauca.edu.co",
                        "director" + i + "@unicauca.edu.co",
                        "SISTEMAS"
                ));
            }
        }

        if (!nuevos.isEmpty()) {
            anteproyectoRepository.saveAll(nuevos);
        }

        System.out.println("Seed idempotente ejecutado en DepartmentHeadService");
    }

    private Docente getOrCreateDocente(String nombre, String email) {
        return docenteRepository.findByEmail(email)
                .orElseGet(() -> {
                    Docente nuevo = new Docente(email, nombre, email);
                    return docenteRepository.save(nuevo);
                });
    }
}
