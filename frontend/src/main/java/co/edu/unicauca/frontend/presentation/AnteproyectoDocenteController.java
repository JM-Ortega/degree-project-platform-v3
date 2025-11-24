package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.FrontendServices;
import co.edu.unicauca.frontend.dto.SessionInfo;
import co.edu.unicauca.frontend.infra.dto.AnteproyectoDTO;
import co.edu.unicauca.frontend.infra.session.SessionData;
import co.edu.unicauca.frontend.infra.session.SessionManager;
import co.edu.unicauca.frontend.services.DocenteService;
import co.edu.unicauca.frontend.services.EstudianteService;
import co.edu.unicauca.frontend.services.ProyectoService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class AnteproyectoDocenteController implements Initializable {
    @FXML
    private Label nombreDocente;
    @FXML
    private TextField txtEstudianteCorreo;
    @FXML
    private Button btnBuscarEstudiante;
    @FXML
    private Label lblEstudianteNombre;
    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtDescripcion;
    @FXML
    private TextField txtBuscar;

    @FXML
    private Button btnSeleccionarPdf;
    @FXML
    private Button btnCrearAnteproyecto;
    @FXML
    private Label lblPdfNombre;
    @FXML
    private Label lblNuevoAnteproyectoMsg;
    @FXML
    private Button btnRefrescar;

    //Tabla
    @FXML
    private TableView<AnteproyectoDocenteController.RowVM> tblAnteproyectos;
    @FXML
    private TableColumn<AnteproyectoDocenteController.RowVM, String> colTitulo;
    @FXML
    private TableColumn<AnteproyectoDocenteController.RowVM, String> colEstudianteNombre;
    @FXML
    private TableColumn<AnteproyectoDocenteController.RowVM, String> colEstudianteCorreo;
    @FXML
    private TableColumn<AnteproyectoDocenteController.RowVM, AnteproyectoDocenteController.RowVM> colAccion;
    @FXML
    private Label lblTablaMsg;

    private DocenteService docenteService;
    private EstudianteService estudianteService;
    private ProyectoService proyectoService;
    private byte[] anteproyectoBytes;
    private String anteproyectoNombre;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            this.docenteService = FrontendServices.docenteService();
            this.proyectoService = FrontendServices.proyectoService();
            this.estudianteService = FrontendServices.estudianteService();
        } catch (IllegalStateException e) {
            System.err.println("Error: servicios no disponibles. Asegúrate de llamar FrontendServices.init() antes.");
            return;
        }
        configurarTabla();
        tblAnteproyectos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private static boolean isEmailLike(String s) {
        if (s == null) return false;
        String v = s.trim().toLowerCase();
        int at = v.indexOf('@');
        if (at <= 0 || at == v.length() - 1) return false;
        return v.matches("^[A-Za-z0-9._%+-]+@unicauca\\.edu\\.co$");
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

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

    public void setServices(DocenteService docenteServiceService, ProyectoService  proyectoService, EstudianteService estudianteService) {
        this.docenteService = docenteServiceService;
        this.proyectoService = proyectoService;
        this.estudianteService = estudianteService;
        carcarDatos();
    }

    public void carcarDatos(){
        SessionData data = SessionManager.getInstance().getCurrentSession();
        SessionInfo docente = (data != null) ? data.getSessionInfo() : null;

        if (docente == null) {
            System.err.println("No hay sesión activa");
            return;
        }

        nombreDocente.setText(docente.nombres());

        if (lblPdfNombre != null) lblPdfNombre.setText("Ningún archivo seleccionado");
        cargarTabla();
    }

    @FXML
    private void onBuscarEstudiante(ActionEvent event) {
        lblEstudianteNombre.setStyle("");
        lblNuevoAnteproyectoMsg.setText("");

        String correo = safeText(txtEstudianteCorreo);
        if (!isEmailLike(correo)) {
            setError(lblEstudianteNombre, "Ingrese el correo institucional del estudiante");
            return;
        }
        try {
            if (!estudianteService.estudianteExistePorCorreo(correo)) {
                setError(lblEstudianteNombre, "El estudiante con el correo ingresado no existe");
                return;
            }
            boolean tieneProyecto = estudianteService.estudianteTieneProyectoEnTramitePorCorreo(correo);
            if (!tieneProyecto) {
                setError(lblEstudianteNombre, "El estudiante no tiene proyectos asociados");
                return;
            }
            boolean formatoAprobado = estudianteService.estudianteTieneFormatoAAprobado(correo);
            if (!formatoAprobado) {
                setError(lblEstudianteNombre, "El Formato A del estudiante no está en estado APROBADO");
                return;
            }
            boolean tieneAnteproyecto = estudianteService.estudianteTieneAnteproyectoAsociado(correo);
            if (tieneAnteproyecto) {
                setError(lblEstudianteNombre, "El estudiante ya tiene un anteproyecto asociado");
                return;
            }

            setOk(lblEstudianteNombre, "El estudiante tiene un proyecto asociado con Formato A aprobado");

        } catch (IllegalArgumentException ex) {
            setError(lblEstudianteNombre, ex.getMessage());
        } catch (Exception ex) {
            setError(lblEstudianteNombre, "Error al consultar el estudiante");
        }
    }

    @FXML
    private void onSeleccionarPdf(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar AnteproyectoDTO (PDF)");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File f = fc.showOpenDialog(btnSeleccionarPdf.getScene().getWindow());
        if (f != null) {
            try {
                anteproyectoBytes = Files.readAllBytes(f.toPath());
                anteproyectoNombre = f.getName();
                lblPdfNombre.setText(anteproyectoNombre);
            } catch (Exception ex) {
                lblPdfNombre.setText("Error leyendo el archivo");
            }
        }
    }

    @FXML
    private void onCrearAnteproyecto(ActionEvent event) {
        lblNuevoAnteproyectoMsg.setText("");
        SessionData data = SessionManager.getInstance().getCurrentSession();
        SessionInfo docente = (data != null) ? data.getSessionInfo() : null;

        if (docente == null) {
            setError(lblNuevoAnteproyectoMsg, "Sesión no válida");
            return;
        }
        String correo = safeText(txtEstudianteCorreo);
        String titulo = safeText(txtTitulo);
        String descripcion = safeText(txtDescripcion);
        if (!isEmailLike(correo)) {
            setError(lblNuevoAnteproyectoMsg, "Ingrese el correo institucional válido del estudiante.");
            return;
        }
        if (titulo.isEmpty()) {
            setError(lblNuevoAnteproyectoMsg, "Ingrese el título del anteproyecto.");
            return;
        }
        if (descripcion.isEmpty()) {
            setError(lblNuevoAnteproyectoMsg, "Ingrese la descripcion del anteproyecto.");
            return;
        }
        if (anteproyectoBytes == null || anteproyectoNombre == null) {
            setError(lblNuevoAnteproyectoMsg, "Adjunte el AnteproyectoDTO (PDF).");
            return;
        }
        try{
            AnteproyectoDTO a = new AnteproyectoDTO();
            a.setTitulo(titulo);
            a.setDescripcion(descripcion);
            a.setBlob(anteproyectoBytes);
            a.setNombreArchivo(anteproyectoNombre);
            a.setEstudianteCorreo(correo);
            a.setFechaCreacion(LocalDate.now());

            if (isBlank(a.getTitulo()) || isBlank(a.getDescripcion()))
                throw new IllegalArgumentException("Título y descripcion son obligatorios");

            // 🔹 Log en consola antes de la llamada
            System.out.println("[CrearAnteproyecto] Enviando anteproyecto: "
                    + a.getTitulo() + " del estudiante " + a.getEstudianteCorreo());

            estudianteService.setAntepAProyectoEst(a);

            // 🔹 Log en consola después de la llamada
            System.out.println("[CrearAnteproyecto] ✔ Llamada a setAntepAProyectoEst() ejecutada correctamente.");


            setOk(lblNuevoAnteproyectoMsg, "Proyecto creado correctamente.");
            limpiarNuevoAnteproyecto();
            cargarTabla();
        }catch (Exception ex) {
            setError(lblNuevoAnteproyectoMsg, ex.getMessage() != null ? ex.getMessage() : "Error al crear el anteproyecto");
        }
    }

    private void limpiarNuevoAnteproyecto() {
        lblEstudianteNombre.setText("");
        lblPdfNombre.setText("");
        lblNuevoAnteproyectoMsg.setText("");
        txtTitulo.clear();
        txtDescripcion.clear();
        txtEstudianteCorreo.clear();
        anteproyectoBytes = null;
        anteproyectoNombre = null;
    }

    private void cargarTabla() {
        lblTablaMsg.setText("");

        SessionData data = SessionManager.getInstance().getCurrentSession();
        SessionInfo usuario = (data != null) ? data.getSessionInfo() : null;

        if (usuario == null) return;

        String filtro = safeText(txtBuscar);
        List<AnteproyectoDTO> proyectos = proyectoService.listarAnteproyectosDocente(usuario.email(), filtro);


        ObservableList<AnteproyectoDocenteController.RowVM> rows = FXCollections.observableArrayList();
        for (AnteproyectoDTO p : proyectos) {
            long id = p.getId();
            String titulo = p.getTitulo();

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

            rows.add(new AnteproyectoDocenteController.RowVM(id, titulo, estNombre, estCorreo));
        }

        tblAnteproyectos.setItems(rows);
        tblAnteproyectos.refresh();
    }

    private void configurarTabla() {
        colTitulo.setCellValueFactory(d -> d.getValue().tituloProperty());
        colEstudianteNombre.setCellValueFactory(d -> d.getValue().estudianteNombreProperty());
        colEstudianteCorreo.setCellValueFactory(d -> d.getValue().estudianteCorreoProperty());


        colAccion.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        colAccion.setCellFactory(col -> new TableCell<>() {
            private final Button btnObs = new Button("Descargar");
            private final VBox box = new VBox(6, btnObs);

            {
                btnObs.setMaxWidth(Double.MAX_VALUE);
                box.setFillWidth(true);
                btnObs.setOnAction(e -> {
                    AnteproyectoDocenteController.RowVM r = getItem();
                    if (r != null) descargarAnteproyecto(r);
                });
            }

            @Override
            protected void updateItem(RowVM row, boolean empty) {
                super.updateItem(row, empty);
                if (empty || row == null) {
                    setGraphic(null);
                    return;
                }

                btnObs.setVisible(true);
                btnObs.setManaged(true);

                setGraphic(box);
            }
        });
    }

    @FXML
    private void onRefrescar() {
        cargarTabla();
    }

    private void descargarAnteproyecto(RowVM row) {
        try{
            var form = proyectoService.obtenerAnteproyecto(row.proyectoId());
            if (form == null) {
                setError(lblTablaMsg, "No hay Anteproyecto.");
                return;
            }

            FileChooser fc = new FileChooser();
            fc.setTitle("Guardar Formato A con observaciones");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
            fc.setInitialFileName(form.getNombreArchivo());
            File dest = fc.showSaveDialog(tblAnteproyectos.getScene().getWindow());
            if (dest == null) return;

            Files.write(dest.toPath(), form.getBlob());
            setOk(lblTablaMsg, "Anteproyecto descargado.");
            cargarTabla();
        }catch (Exception ex) {
            setError(lblTablaMsg, ex.getMessage() != null ? ex.getMessage() : "Error al descargar");
        }
    }

    // =================== ViewModel ===================
    public static class RowVM {
        private final long proyectoId;
        private final StringProperty titulo = new SimpleStringProperty();
        private final StringProperty estudianteNombre = new SimpleStringProperty();
        private final StringProperty estudianteCorreo = new SimpleStringProperty();

        public RowVM(long proyectoId, String titulo, String estudianteNombre, String estudianteCorreo) {
            this.proyectoId = proyectoId;
            this.titulo.set(titulo);
            this.estudianteNombre.set(estudianteNombre);
            this.estudianteCorreo.set(estudianteCorreo);
        }

        public long proyectoId() {
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
    }
}
