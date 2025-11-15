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

@Component
public class DepartmentHeadDataLoader implements CommandLineRunner {

    private final AnteproyectoRepository anteproyectoRepository;
    private final DocenteRepository docenteRepository;

    @Value("${seed.enabled:true}")        // puedes apagar semillas con seed.enabled=false
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
        // --- Crear/obtener docentes de prueba (idempotente por email) ---
        Docente docente1 = getOrCreateDocente("Juan Pérez", "juan.perez@unicauca.edu.co");
        Docente docente2 = getOrCreateDocente("Ana Gómez", "ana.gomez@unicauca.edu.co");
        Docente docente3 = getOrCreateDocente("Carlos Ruiz", "carlos.ruiz@unicauca.edu.co");

        // --- Crear lista de anteproyectos (solo los que falten) ---
        List<Anteproyecto> nuevos = new ArrayList<>();

        // 15 sin evaluadores
        for (int i = 1; i <= 15; i++) {
            long apId = 100L + i;
            if (!anteproyectoRepository.existsByAnteproyectoId(apId)) {
                nuevos.add(new Anteproyecto(
                        apId,
                        200L + i,
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

        // 3 con dos evaluadores
        for (int i = 16; i <= 18; i++) {
            long apId = 100L + i;
            if (!anteproyectoRepository.existsByAnteproyectoId(apId)) {
                nuevos.add(new Anteproyecto(
                        apId,
                        200L + i,
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

        // 2 con un evaluador
        for (int i = 19; i <= 20; i++) {
            long apId = 100L + i;
            if (!anteproyectoRepository.existsByAnteproyectoId(apId)) {
                nuevos.add(new Anteproyecto(
                        apId,
                        200L + i,
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
