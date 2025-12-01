//package co.edu.unicauca.departmentheadservice;
//
//import co.edu.unicauca.departmentheadservice.access.AnteproyectoRepository;
//import co.edu.unicauca.departmentheadservice.access.DocenteRepository;
//import co.edu.unicauca.departmentheadservice.entities.Anteproyecto;
//import co.edu.unicauca.departmentheadservice.entities.Docente;
//import co.edu.unicauca.shared.contracts.model.Departamento;
//import co.edu.unicauca.shared.contracts.model.Rol;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//@Component
//public class DepartmentHeadDataLoader implements CommandLineRunner {
//
//    private final AnteproyectoRepository anteproyectoRepository;
//    private final DocenteRepository docenteRepository;
//
//    @Value("${seed.enabled:true}")
//    private boolean seedEnabled;
//
//    public DepartmentHeadDataLoader(AnteproyectoRepository anteproyectoRepository,
//                                    DocenteRepository docenteRepository) {
//        this.anteproyectoRepository = anteproyectoRepository;
//        this.docenteRepository = docenteRepository;
//    }
//
//    @Override
//    @Transactional
//    public void run(String... args) {
//        if (!seedEnabled) return;
//        loadData();
//    }
//
//    private void loadData() {
//        List<Rol> roles = new ArrayList<Rol>() {{
//            add(Rol.DOCENTE);
//        }};
//        Docente docente1 = getOrCreateDocente("Juan Pérez", "juan.perez@unicauca.edu.co", Departamento.SISTEMAS, roles);
//        Docente docente2 = getOrCreateDocente("Ana Gómez", "ana.gomez@unicauca.edu.co", Departamento.SISTEMAS, roles);
//        Docente docente3 = getOrCreateDocente("Carlos Ruiz", "carlos.ruiz@unicauca.edu.co",  Departamento.SISTEMAS, roles);
//        Docente docente4 = getOrCreateDocente("María Fernanda López", "maria.lopez@unicauca.edu.co", Departamento.SISTEMAS, roles);
//        Docente docente5 = getOrCreateDocente("Luis Alberto Mendoza", "luis.mendoza@unicauca.edu.co", Departamento.SISTEMAS, roles);
//        Docente docente6 = getOrCreateDocente("Patricia Ramírez", "patricia.ramirez@unicauca.edu.co", Departamento.SISTEMAS, roles);
//        Docente docente7 = getOrCreateDocente("Andrés Felipe Torres", "andres.torres@unicauca.edu.co", Departamento.TELEMATICA, roles);
//        Docente docente8 = getOrCreateDocente("Diana Marcela Castillo", "diana.castillo@unicauca.edu.co", Departamento.ELECTRONICA_INSTRUMENTACION_Y_CONTROL, roles);
//        Docente docente9 = getOrCreateDocente("Jorge Enrique Salazar", "jorge.salazar@unicauca.edu.co", Departamento.ELECTRONICA_INSTRUMENTACION_Y_CONTROL, roles);
//        Docente docente10 = getOrCreateDocente("Carolina Herrera", "carolina.herrera@unicauca.edu.co", Departamento.SISTEMAS, roles);
//        Docente docente11 = getOrCreateDocente("Santiago Rivas", "santiago.rivas@unicauca.edu.co", Departamento.TELEMATICA, roles);
//        Docente docente12 = getOrCreateDocente("Marcela Quiñones", "marcela.quinones@unicauca.edu.co", Departamento.SISTEMAS, roles);
//        Docente docente13 = getOrCreateDocente("Héctor Julio Valencia", "hector.valencia@unicauca.edu.co", Departamento.ELECTRONICA_INSTRUMENTACION_Y_CONTROL, roles);
//
//        List<Anteproyecto> nuevos = new ArrayList<>();
//
//        for (int i = 1; i <= 15; i++) {
//            UUID apId = UUID.nameUUIDFromBytes(("ap-" + i).getBytes());
//            UUID proyectoId = UUID.nameUUIDFromBytes(("proj-" + i).getBytes());
//
//            if (!anteproyectoRepository.existsByAnteproyectoId(apId)) {
//                nuevos.add(new Anteproyecto(
//                        apId,
//                        proyectoId,
//                        "Anteproyecto sin evaluadores " + i,
//                        "Descripción del anteproyecto sin evaluadores " + i,
//                        LocalDate.now(),
//                        List.of(),
//                        "estudiante" + i + "@unicauca.edu.co",
//                        "director" + i + "@unicauca.edu.co",
//                        "SISTEMAS"
//                ));
//            }
//        }
//
//        for (int i = 16; i <= 18; i++) {
//            UUID apId = UUID.nameUUIDFromBytes(("ap-" + i).getBytes());
//            UUID proyectoId = UUID.nameUUIDFromBytes(("proj-" + i).getBytes());
//
//            if (!anteproyectoRepository.existsByAnteproyectoId(apId)) {
//                nuevos.add(new Anteproyecto(
//                        apId,
//                        proyectoId,
//                        "Anteproyecto con 2 evaluadores " + i,
//                        "Descripción del anteproyecto con 2 evaluadores " + i,
//                        LocalDate.now(),
//                        List.of(docente1, docente2),
//                        "estudiante" + i + "@unicauca.edu.co",
//                        "director" + i + "@unicauca.edu.co",
//                        "SISTEMAS"
//                ));
//            }
//        }
//
//        for (int i = 19; i <= 20; i++) {
//            UUID apId = UUID.nameUUIDFromBytes(("ap-" + i).getBytes());
//            UUID proyectoId = UUID.nameUUIDFromBytes(("proj-" + i).getBytes());
//
//            if (!anteproyectoRepository.existsByAnteproyectoId(apId)) {
//                nuevos.add(new Anteproyecto(
//                        apId,
//                        proyectoId,
//                        "Anteproyecto con 1 evaluador " + i,
//                        "Descripción del anteproyecto con 1 evaluador " + i,
//                        LocalDate.now(),
//                        List.of(docente3),
//                        "estudiante" + i + "@unicauca.edu.co",
//                        "director" + i + "@unicauca.edu.co",
//                        "SISTEMAS"
//                ));
//            }
//        }
//
//        if (!nuevos.isEmpty()) {
//            anteproyectoRepository.saveAll(nuevos);
//        }
//
//        System.out.println("Seed idempotente ejecutado en DepartmentHeadService");
//    }
//
//    private Docente getOrCreateDocente(String nombre, String email, Departamento departamento, List<Rol> roles) {
//        return docenteRepository.findByEmail(email)
//                .orElseGet(() -> {
//                    UUID personaId = UUID.nameUUIDFromBytes(email.getBytes());
//                    Docente nuevo = new Docente(personaId, nombre, email, departamento, roles);
//                    return docenteRepository.save(nuevo);
//                });
//    }
//}
