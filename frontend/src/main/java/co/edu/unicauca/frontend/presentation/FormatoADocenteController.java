package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.FrontendServices;
import co.edu.unicauca.frontend.dto.SessionInfo;
import co.edu.unicauca.frontend.entities.EstadoFormatoA;
import co.edu.unicauca.frontend.entities.EstadoProyecto;
import co.edu.unicauca.frontend.entities.TipoProyecto;
import co.edu.unicauca.frontend.infra.dto.CartaLaboralDTO;
import co.edu.unicauca.frontend.infra.dto.FormatoADTO;
import co.edu.unicauca.frontend.infra.dto.ProyectoDTO;
import co.edu.unicauca.frontend.infra.dto.ProyectoInfoDTO;
import co.edu.unicauca.frontend.infra.session.SessionData;
import co.edu.unicauca.frontend.infra.session.SessionManager;
import co.edu.unicauca.frontend.services.DocenteService;
import co.edu.unicauca.frontend.services.EstudianteService;
import co.edu.unicauca.frontend.services.ProyectoService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class FormatoADocenteController implements Initializable {

    // Header / acciones
    @FXML
    private Label nombreDocente;
    @FXML
    private Button btnIniciarNuevoProyecto;
    @FXML
    private Button btnVerEstadisticas;
    @FXML
    private Label lblCupoDocente;
    @FXML
    private TextField txtBuscar;
    @FXML
    private Button btnRefrescar;

    // Panel NUEVO PROYECTO (RF-2)
    @FXML
    private TitledPane pnNuevoProyecto;
    @FXML
    private TextField txtEstudiante1Correo;
    @FXML
    private TextField txtEstudiante2Correo;
    @FXML
    private Button btnBuscarEstudiante1;
    @FXML
    private Button btnBuscarEstudiante2;
    @FXML
    private Label lblEstudiante1Nombre;
    @FXML
    private Label lblEstudiante2Nombre;
    @FXML
    private TextField txtTitulo;

    // Tipo trabajo + archivos
    @FXML
    private ComboBox<TipoProyecto> cbTipoTrabajo;
    @FXML
    private Button btnSeleccionarPdf;
    @FXML
    private Label lblPdfNombre;
    @FXML
    private HBox rowCarta;
    @FXML
    private Button btnSeleccionarCarta;
    @FXML
    private Label lblCartaNombre;

    @FXML
    private Button btnCrearProyecto;
    @FXML
    private Button btnCancelarNuevo;
    @FXML
    private Label lblNuevoProyectoMsg;

    // Tabla
    @FXML
    private TableView<RowVM> tblProyectos;
    @FXML
    private TableColumn<RowVM, String> colTitulo;
    @FXML
    private TableColumn<RowVM, String> colEstudianteNombre;
    @FXML
    private TableColumn<RowVM, String> colEstudianteCorreo;
    @FXML
    private TableColumn<RowVM, Number> colVersion;
    @FXML
    private TableColumn<RowVM, String> colEstadoProyecto;
    @FXML
    private TableColumn<RowVM, RowVM> colAccion;
    @FXML
    private Label lblTablaMsg;


    // Estado de archivos para nuevo proyecto
    private byte[] formatoABytes;
    private String formatoANombre;
    private byte[] cartaBytes;
    private String cartaNombre;

    private DocenteService docenteService;
    private EstudianteService estudianteService;
    private ProyectoService proyectoService;

    private static boolean isEmailLike(String s) {
        if (s == null) return false;
        String v = s.trim().toLowerCase();
        int at = v.indexOf('@');
        if (at <= 0 || at == v.length() - 1) return false;
        return v.matches("^[A-Za-z0-9._%+-]+@unicauca\\.edu\\.co$");
    }

    // =================== Utiles UI ===================
    private static String safeText(TextField tf) {
        return tf.getText() == null ? "" : tf.getText().trim();
    }

    private static void setError(Label lbl, String msg) {
        lbl.setStyle("-fx-text-fill:#D84315;");
        lbl.setText(msg);
    }

    private static void setOk(Label lbl, String msg) {
        lbl.setStyle("-fx-text-fill:#2E7D32;");
        lbl.setText(msg);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            this.docenteService = FrontendServices.docenteService();
            this.proyectoService = FrontendServices.proyectoService();
            this.estudianteService = FrontendServices.estudianteService();
        } catch (IllegalStateException e) {
            System.err.println("Error: servicios no disponibles. Asegúrate de llamar FrontendServices.init() antes.");
            return;
        }
        configurarTabla();
        tblProyectos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        if (cbTipoTrabajo != null) {
            cbTipoTrabajo.getItems().setAll(TipoProyecto.TRABAJO_DE_INVESTIGACION, TipoProyecto.PRACTICA_PROFESIONAL);
            cbTipoTrabajo.valueProperty().addListener((obs, old, val) -> {
                boolean requiereCarta = (val == TipoProyecto.PRACTICA_PROFESIONAL);
                if (rowCarta != null) {
                    rowCarta.setVisible(requiereCarta);
                    rowCarta.setManaged(requiereCarta);
                }
                if (!requiereCarta) {
                    cartaBytes = null;
                    cartaNombre = null;
                    if (lblCartaNombre != null) lblCartaNombre.setText("Ningún archivo seleccionado");
                }
            });
        }
    }

    // =================== Carga inicial ===================
public void cargarDatos() {
    SessionData data = SessionManager.getInstance().getCurrentSession();
    SessionInfo docente = (data != null) ? data.getSessionInfo() : null;

    if (docente == null) {
        System.err.println("No hay sesión activa");
        mostrarAlertaError(
                "Sesión no encontrada",
                "No se encontró información de sesión para el docente.\n" +
                "Por favor, inicie sesión nuevamente."
        );
        return;
    }

    nombreDocente.setText(docente.nombres());
    ocultarPanelNuevo();
    if (lblPdfNombre != null) lblPdfNombre.setText("Ningún archivo seleccionado");
    if (lblCartaNombre != null) lblCartaNombre.setText("Ningún archivo seleccionado");

    try {

        actualizarCupo();
        cargarTabla();
    } catch (RuntimeException ex) {

        System.err.println("[FormatoADocenteController] Error al cargar datos del docente");
        ex.printStackTrace();

        mostrarAlertaError(
                "Error al cargar datos",
                "Ocurrió un error al cargar la información del docente y sus proyectos.\n" +
                "Detalle: " + ex.getMessage()
        );

    }
}


    // =================== Eventos ===================
    @FXML
    private void onIniciarNuevoProyecto() {
        lblNuevoProyectoMsg.setText("");
        mostrarPanelNuevo();
        limpiarNuevoProyecto();
    }

    @FXML
    private void onVerEstadisticas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/co/edu/unicauca/frontend/view/EstadisticasDocente.fxml"
            ));

            Parent vista = loader.load();
            EstadisticasDocenteController estadisticasController = loader.getController();
            estadisticasController.setServices(this.docenteService, this.proyectoService, this.estudianteService);

            Stage stage = new Stage();
            stage.setTitle("Estadísticas - Docente");
            stage.setScene(new Scene(vista));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar EstadisticasDocente.fxml: " + e.getMessage());
        }
    }


    @FXML
    private void onCancelarNuevo() {
        ocultarPanelNuevo();
        limpiarNuevoProyecto();
    }

    @FXML
    private void onBuscarEstudiante1() {
        lblEstudiante1Nombre.setStyle("");
        lblNuevoProyectoMsg.setText("");

        String correo = safeText(txtEstudiante1Correo);
        if (!isEmailLike(correo)) {
            setError(lblEstudiante1Nombre, "Ingrese el correo institucional del estudiante");
            return;
        }
        try {
            boolean libre = estudianteService.estudianteLibrePorCorreo(correo);
            if (libre) setOk(lblEstudiante1Nombre, "Estudiante disponible");
            else setError(lblEstudiante1Nombre, "Estudiante con proyecto en curso");
        } catch (IllegalArgumentException ex) {
            setError(lblEstudiante1Nombre, ex.getMessage());
        } catch (Exception ex) {
            setError(lblEstudiante1Nombre, "Error al validar estudiante");
        }
    }

    @FXML
    private void onBuscarEstudiante2() {
        lblEstudiante2Nombre.setStyle("");
        lblNuevoProyectoMsg.setText("");

        String correo = safeText(txtEstudiante2Correo);
        if (!isEmailLike(correo)) {
            setError(lblEstudiante2Nombre, "Ingrese el correo institucional del estudiante");
            return;
        }
        try {
            boolean libre = estudianteService.estudianteLibrePorCorreo(correo);
            if (libre) setOk(lblEstudiante2Nombre, "Estudiante disponible");
            else setError(lblEstudiante2Nombre, "Estudiante con proyecto en curso");
        } catch (IllegalArgumentException ex) {
            setError(lblEstudiante2Nombre, ex.getMessage());
        } catch (Exception ex) {
            setError(lblEstudiante2Nombre, "Error al validar estudiante");
        }
    }

    @FXML
    private void onSeleccionarPdf() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar Formato A (PDF)");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File f = fc.showOpenDialog(btnSeleccionarPdf.getScene().getWindow());
        if (f != null) {
            try {
                formatoABytes = Files.readAllBytes(f.toPath());
                formatoANombre = f.getName();
                lblPdfNombre.setText(formatoANombre);
            } catch (Exception ex) {
                lblPdfNombre.setText("Error leyendo el archivo");
            }
        }
    }

    @FXML
    private void onSeleccionarCarta() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar Carta de aceptación (PDF)");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File f = fc.showOpenDialog(btnSeleccionarCarta.getScene().getWindow());
        if (f != null) {
            try {
                cartaBytes = Files.readAllBytes(f.toPath());
                cartaNombre = f.getName();
                lblCartaNombre.setText(cartaNombre);
            } catch (Exception ex) {
                lblCartaNombre.setText("Error leyendo el archivo");
            }
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    @FXML
    private void onCrearProyecto() {
        lblNuevoProyectoMsg.setText("");
        SessionData data = SessionManager.getInstance().getCurrentSession();
        SessionInfo docente = (data != null) ? data.getSessionInfo() : null;

        if (docente == null) {
            setError(lblNuevoProyectoMsg, "Sesión no válida");
            return;
        }

        String correo1 = safeText(txtEstudiante1Correo);
        String correo2 = safeText(txtEstudiante2Correo);
        String titulo = safeText(txtTitulo);
        TipoProyecto tipoTrabajo = cbTipoTrabajo != null ? cbTipoTrabajo.getValue() : TipoProyecto.PRACTICA_PROFESIONAL;

        if (tipoTrabajo == null) {
            setError(lblNuevoProyectoMsg, "Seleccione el tipo de trabajo.");
            return;
        }
        if (!isEmailLike(correo1)) {
            setError(lblNuevoProyectoMsg, "Ingrese el correo institucional válido del estudiante.");
            return;
        }
        if (!correo2.equals("") && !isEmailLike(correo2)) {
            setError(lblNuevoProyectoMsg, "Ingrese el correo institucional válido del estudiante.");
            return;
        }
        if (titulo.isEmpty()) {
            setError(lblNuevoProyectoMsg, "Ingrese el título del proyecto.");
            return;
        }
        if (formatoABytes == null || formatoANombre == null) {
            setError(lblNuevoProyectoMsg, "Adjunte el Formato A (PDF).");
            return;
        }
        if (tipoTrabajo == TipoProyecto.PRACTICA_PROFESIONAL && (cartaBytes == null || cartaNombre == null)) {
            setError(lblNuevoProyectoMsg, "Adjunte la Carta de aceptación (PDF).");
            return;
        }
        if (tipoTrabajo == TipoProyecto.PRACTICA_PROFESIONAL && !correo2.equals("")) {
            setError(lblNuevoProyectoMsg, "No se admiten 2 estudiantes para un proyecto de tipo PRACTICA PROFESIONAL.");
            return;
        }

        try {
            ProyectoDTO p = new ProyectoDTO();
            p.setTipoProyecto(tipoTrabajo);
            p.setTitulo(titulo);
            p.setDirector(docente.email());
            List<String> correosEst = new ArrayList<>();
            correosEst.add(correo1);
            correosEst.add(correo2);
            p.setEstudiantes(correosEst);

            FormatoADTO formatoADTO = new FormatoADTO();
            formatoADTO.setNombreFormato(formatoANombre);
            formatoADTO.setBlob(formatoABytes);
            formatoADTO.setEstado(EstadoFormatoA.PENDIENTE);
            formatoADTO.setFechaCreacion(LocalDate.now());
            formatoADTO.setNroVersion(1);

            if (isBlank(p.getTitulo()) || isBlank(p.getEstudiantes().getFirst()) || isBlank(p.getDirector()))
                throw new IllegalArgumentException("Título, estudiante y docente son obligatorios");

            if (tipoTrabajo == TipoProyecto.PRACTICA_PROFESIONAL) {
                CartaLaboralDTO carta = new CartaLaboralDTO();
                carta.setNombreCartaLaboral(cartaNombre);
                carta.setBlob(cartaBytes);
                carta.setFechaCreacion(LocalDate.now());

                p.setFormatoA(formatoADTO);
                p.setCartaLaboral(carta);

                if (p.getCartaLaboral() == null || p.getFormatoA() == null) {
                    throw new IllegalArgumentException("Datos incompletos");
                }

                proyectoService.crearProyecto(p);
            } else {
                p.setFormatoA(formatoADTO);

                if (p.getFormatoA() == null) {
                    throw new IllegalArgumentException("Datos incompletos");
                }

                proyectoService.crearProyecto(p);
            }

            setOk(lblNuevoProyectoMsg, "Proyecto creado correctamente.");
            ocultarPanelNuevo();
            limpiarNuevoProyecto();
            actualizarCupo();
            cargarTabla();
        } catch (Exception ex) {
            setError(lblNuevoProyectoMsg, ex.getMessage() != null ? ex.getMessage() : "Error al crear el proyecto");
        }
    }

    @FXML
    private void onRefrescar() {
        cargarTabla();
        actualizarCupo();
    }

    // =================== Tabla ===================
    private void configurarTabla() {
        colTitulo.setCellValueFactory(d -> d.getValue().tituloProperty());
        colEstudianteNombre.setCellValueFactory(d -> d.getValue().estudianteNombreProperty());
        colEstudianteCorreo.setCellValueFactory(d -> d.getValue().estudianteCorreoProperty());
        colVersion.setCellValueFactory(d -> d.getValue().versionProperty());
        colEstadoProyecto.setCellValueFactory(d -> d.getValue().estadoProperty());


        colAccion.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        colAccion.setCellFactory(col -> new TableCell<>() {
            private final Button btnSubir = new Button("Subir nueva versión");
            private final Button btnObs = new Button("Descargar obs.");
            private final VBox box = new VBox(6, btnSubir, btnObs);

            {
                btnSubir.setMaxWidth(Double.MAX_VALUE);
                btnObs.setMaxWidth(Double.MAX_VALUE);
                box.setFillWidth(true);

                btnSubir.setOnAction(e -> {
                    RowVM r = getItem();
                    if (r != null) subirNuevaVersion(r);
                });
                btnObs.setOnAction(e -> {
                    RowVM r = getItem();
                    if (r != null) descargarObservaciones(r);
                });
            }

            @Override
            protected void updateItem(RowVM row, boolean empty) {
                super.updateItem(row, empty);
                if (empty || row == null) {
                    setGraphic(null);
                    return;
                }

                boolean puedeSubir = proyectoService.canResubmit(row.proyectoId());
                btnSubir.setVisible(puedeSubir);
                btnSubir.setManaged(puedeSubir);

                boolean hayObs = proyectoService.tieneObservacionesFormatoA(row.proyectoId());
                btnObs.setVisible(hayObs);
                btnObs.setManaged(hayObs);

                setGraphic(box);
            }
        });
    }

    private void cargarTabla() {
        lblTablaMsg.setText("");

        SessionData data = SessionManager.getInstance().getCurrentSession();
        SessionInfo usuario = (data != null) ? data.getSessionInfo() : null;

        if (usuario == null) return;

        String filtro = safeText(txtBuscar);
        List<ProyectoInfoDTO> proyectos = proyectoService.listarProyectosDocente(usuario.email(), filtro);

        ObservableList<RowVM> rows = FXCollections.observableArrayList();
        for (ProyectoInfoDTO p : proyectos) {
            UUID id = p.getId();
            String titulo = p.getTitulo();

            EstadoProyecto estadoFinal = proyectoService.enforceAutoCancelIfNeeded(id);

            int version = proyectoService.maxVersionFormatoA(id);

            String estNombre = p.getEstudianteNombre();
            String estCorreo = p.getEstudianteCorreo();
            if (estNombre != null) {
                estNombre = estNombre.trim();
            } else {
                estNombre = "";
            }
            if (estCorreo != null) {
                estCorreo = estCorreo.trim();
            } else {
                estCorreo = "";
            }

            rows.add(new RowVM(id, titulo, estNombre, estCorreo, version, estadoFinal.name()));
        }

        tblProyectos.setItems(rows);
        tblProyectos.refresh();
    }

    private void subirNuevaVersion(RowVM row) {
        try {
            FileChooser fc = new FileChooser();
            fc.setTitle("Seleccionar nueva versión (PDF)");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
            File f = fc.showOpenDialog(tblProyectos.getScene().getWindow());
            if (f == null) return;

            byte[] bytes = Files.readAllBytes(f.toPath());

            FormatoADTO a = new FormatoADTO();
            a.setNombreFormato(f.getName());
            a.setBlob(bytes);

            var res = proyectoService.subirNuevaVersionFormatoA(row.proyectoId(), a);

            cargarTabla();
            setOk(lblTablaMsg, "Se subió la versión " + res.getNroVersion());
        } catch (Exception ex) {
            setError(lblTablaMsg, ex.getMessage() != null ? ex.getMessage() : "Error al subir la versión");
        }
    }

    // =================== Helpers ===================
    private void descargarObservaciones(RowVM row) {
        try {
            proyectoService.enforceAutoCancelIfNeeded(row.proyectoId());
            var form = proyectoService.obtenerUltimoFormatoAConObservaciones(row.proyectoId());
            if (form == null) {
                setError(lblTablaMsg, "No hay Formato A con observaciones.");
                return;
            }

            FileChooser fc = new FileChooser();
            fc.setTitle("Guardar Formato A con observaciones");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
            fc.setInitialFileName(form.getNombreFormato());
            File dest = fc.showSaveDialog(tblProyectos.getScene().getWindow());
            if (dest == null) return;

            Files.write(dest.toPath(), form.getBlob());
            setOk(lblTablaMsg, "Observaciones descargadas.");
            cargarTabla();
        } catch (Exception ex) {
            setError(lblTablaMsg, ex.getMessage() != null ? ex.getMessage() : "Error al descargar observaciones");
        }
    }

    private void actualizarCupo() {
        SessionData data = SessionManager.getInstance().getCurrentSession();
        SessionInfo auth = (data != null) ? data.getSessionInfo() : null;

        if (auth == null) {
            btnIniciarNuevoProyecto.setDisable(true);
            lblCupoDocente.setText("Sesión no válida");
            return;
        }

        boolean cupo = docenteService.docenteTieneCupo(auth.email());

        btnIniciarNuevoProyecto.setDisable(!cupo);
        lblCupoDocente.setText(cupo ? "" : "Límite de 7 proyectos en curso alcanzado");
    }

    private void mostrarPanelNuevo() {
        pnNuevoProyecto.setVisible(true);
        pnNuevoProyecto.setManaged(true);
        pnNuevoProyecto.setExpanded(true);
    }

    private void ocultarPanelNuevo() {
        pnNuevoProyecto.setVisible(false);
        pnNuevoProyecto.setManaged(false);
        pnNuevoProyecto.setExpanded(false);
    }

    private void limpiarNuevoProyecto() {
        txtEstudiante1Correo.clear();
        txtEstudiante2Correo.clear();
        lblEstudiante1Nombre.setText("");
        lblEstudiante2Nombre.setText("No es obligatorio");
        txtTitulo.clear();
        if (cbTipoTrabajo != null) cbTipoTrabajo.getSelectionModel().clearSelection();
        lblPdfNombre.setText("Ningún archivo seleccionado");
        if (lblCartaNombre != null) lblCartaNombre.setText("Ningún archivo seleccionado");
        lblNuevoProyectoMsg.setText("");
        formatoABytes = null;
        formatoANombre = null;
        cartaBytes = null;
        cartaNombre = null;
        if (rowCarta != null) {
            rowCarta.setVisible(false);
            rowCarta.setManaged(false);
        }
    }

    public void setServices(DocenteService docenteService, ProyectoService proyectoService, EstudianteService estudianteService) {
        this.docenteService = docenteService;
        this.proyectoService = proyectoService;
        this.estudianteService = estudianteService;
    }

    // =================== ViewModel ===================
    public static class RowVM {
        private final UUID proyectoId;
        private final StringProperty titulo = new SimpleStringProperty();
        private final StringProperty estudianteNombre = new SimpleStringProperty();
        private final StringProperty estudianteCorreo = new SimpleStringProperty();
        private final IntegerProperty version = new SimpleIntegerProperty();
        private final StringProperty estado = new SimpleStringProperty();

        public RowVM(UUID proyectoId, String titulo, String estudianteNombre, String estudianteCorreo, int version, String estado) {
            this.proyectoId = proyectoId;
            this.titulo.set(titulo);
            this.estudianteNombre.set(estudianteNombre);
            this.estudianteCorreo.set(estudianteCorreo);
            this.version.set(version);
            this.estado.set(estado);
        }

        public UUID proyectoId() {
            return proyectoId;
        }

        public StringProperty tituloProperty() {
            return titulo;
        }

        public StringProperty estudianteNombreProperty() {
            return estudianteNombre;
        }

        public StringProperty estudianteCorreoProperty() {
            return estudianteCorreo;
        }

        public IntegerProperty versionProperty() {
            return version;
        }

        public StringProperty estadoProperty() {
            return estado;
        }
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
