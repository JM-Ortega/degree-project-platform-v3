package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.entities.FormatoAResumen;
import co.edu.unicauca.frontend.services.coordinator.FormatoService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class CoObservacionesController implements Initializable {

    @FXML
    private Label volver;
    @FXML
    private ComboBox<String> cbxValoracion;
    @FXML
    private Label lblFechaEntrega, lblNombreProyecto, lblVersionArchivo, lblTipoProyecto, lblArchivo;
    @FXML
    private Button btnArchivo, btnEnviar;

    private FormatoAResumen formatoSeleccionado;
    private CoordinadorController parent;
    private File archivoSeleccionado;

    private final FormatoService formatoService = new FormatoService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        volver.setOnMouseClicked(e -> {
            if (parent != null) parent.loadUI("Coordinador_Proyectos");
        });

        cbxValoracion.getItems().addAll("ACEPTADO", "RECHAZADO");
        cbxValoracion.setCellFactory(lv -> crearCeldaValoracion());
        cbxValoracion.setButtonCell(crearCeldaValoracion());

        cbxValoracion.setOnAction(e -> actualizarEstadoBotonEnviar());
        btnEnviar.setDisable(true);
    }

    public void setParentController(CoordinadorController parent) {
        this.parent = parent;
    }

    public void setFormatoSeleccionado(FormatoAResumen formato) {
        this.formatoSeleccionado = formato;
        if (formato != null) {
            lblNombreProyecto.setText(formato.getNombreProyecto());
            lblTipoProyecto.setText(formato.getTipoProyecto());
            lblFechaEntrega.setText(String.valueOf(formato.getFechaSubida()));
            lblVersionArchivo.setText(String.valueOf(formato.getNroVersion()));
            lblArchivo.setText("");
        }
    }

    private ListCell<String> crearCeldaValoracion() {
        return new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    getStyleClass().removeAll("aceptado", "rechazado");
                } else {
                    setText(item);
                    getStyleClass().removeAll("aceptado", "rechazado");
                    getStyleClass().add(item.equals("ACEPTADO") ? "aceptado" : "rechazado");
                }
            }
        };
    }

    private void actualizarEstadoBotonEnviar() {
        btnEnviar.setDisable(archivoSeleccionado == null || cbxValoracion.getValue() == null);
    }

    @FXML
    private void seleccionarArchivo(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar archivo PDF");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));

        File file = chooser.showOpenDialog(null);
        if (file != null) {
            if (!Files.isReadable(file.toPath())) {
                new Alert(Alert.AlertType.ERROR, "No se puede leer el archivo.").show();
                return;
            }

            // Validar que realmente sea PDF (por MIME type)
            try {
                String mimeType = Files.probeContentType(file.toPath());
                if (mimeType == null || !mimeType.equals("application/pdf")) {
                    new Alert(Alert.AlertType.ERROR, "El archivo seleccionado no es un PDF válido.").show();
                    return;
                }
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Error al validar el archivo.").show();
                return;
            }

            archivoSeleccionado = file;
            lblArchivo.setText(file.getName());
            actualizarEstadoBotonEnviar();
        }
    }

    @FXML
    private void enviarFormato(ActionEvent event) {
        if (archivoSeleccionado == null || cbxValoracion.getValue() == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona un archivo y una valoración antes de enviar.").show();
            return;
        }

        btnEnviar.setDisable(true);

        Task<Void> envioTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String id = formatoSeleccionado.getId();
                String estado = cbxValoracion.getValue();
                String nombreArchivo = archivoSeleccionado.getName();
                String horaActual = java.time.LocalDateTime.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


                HttpResponse<String> response = formatoService.actualizarFormato(id, estado, archivoSeleccionado, nombreArchivo, horaActual);

                if (response.statusCode() == 200) {
                    Platform.runLater(() ->
                            new Alert(Alert.AlertType.INFORMATION, "Formato enviado correctamente.").show());
                } else {
                    Platform.runLater(() ->
                            new Alert(Alert.AlertType.ERROR, "Error al enviar el formato: " + response.body()).show());
                }
                return null;
            }

            @Override
            protected void failed() {
                Platform.runLater(() ->
                        new Alert(Alert.AlertType.ERROR, "Error al conectar con el servidor.").show());
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    if (parent != null) parent.loadUI("Coordinador_Proyectos");
                });
            }
        };

        new Thread(envioTask).start();
    }
}

