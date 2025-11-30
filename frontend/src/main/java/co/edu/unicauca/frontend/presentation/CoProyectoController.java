package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.entities.FormatoAResumen;
import co.edu.unicauca.frontend.services.coordinator.FormatoService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class CoProyectoController implements Initializable {

    @FXML
    private TableView<FormatoAResumen> tabla;

    @FXML
    private TableColumn<FormatoAResumen, String> colNombreProyecto;
    @FXML
    private TableColumn<FormatoAResumen, String> colNombreProfesor;
    @FXML
    private TableColumn<FormatoAResumen, String> colTipoP;
    @FXML
    private TableColumn<FormatoAResumen, LocalDate> colFecha;
    @FXML
    private TableColumn<FormatoAResumen, String> colEstado;
    @FXML
    private TableColumn<FormatoAResumen, Number> colVersion;
    @FXML
    private TableColumn<FormatoAResumen, Void> colDescargar;

    private CoordinadorController parent;
    private final FormatoService formatoService = new FormatoService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar columnas
        colNombreProyecto.setCellValueFactory(cell -> cell.getValue().nombreProyectoProperty());
        colNombreProfesor.setCellValueFactory(cell -> cell.getValue().nombreDirectorProperty());
        colTipoP.setCellValueFactory(cellData -> {
            String tipo = cellData.getValue().getTipoProyecto(); // o .tipoProyectoProperty().get()
            String tipoP;
            switch (tipo) {
                case "TRABAJO_DE_INVESTIGACION":
                    tipoP = "Trabajo de investigación";
                    break;
                case "PRACTICA_PROFESIONAL":
                    tipoP = "Práctica profesional";
                    break;
                default:
                    tipoP = tipo;
                    break;
            }
            return new ReadOnlyStringWrapper(tipoP);
        });
        colFecha.setCellValueFactory(cell -> cell.getValue().fechaSubidaProperty());
        colEstado.setCellValueFactory(cell -> cell.getValue().estadoFormatoAProperty());
        colVersion.setCellValueFactory(cell -> cell.getValue().nroVersionProperty());

        configurarColumnaEstado();
        agregarBotonDescargar();
        cargarTabla();
    }

    public void setParentController(CoordinadorController parent) {
        this.parent = parent;
    }

    private void cargarTabla() {
        try {
            var formatos = formatoService.obtenerFormatosAResumen();
            tabla.setItems(FXCollections.observableArrayList(formatos));
        } catch (Exception e) {
            e.printStackTrace();
            alerta(Alert.AlertType.ERROR, "Error", null, "Error al cargar los datos: " + e.getMessage());
        }
    }


    // ----------------------------------------
    // Configuración columna Estado
    // ----------------------------------------
    private void configurarColumnaEstado() {
        colEstado.setCellFactory(col -> new TableCell<>() {
            private final Button estadoBtn = new Button();

            {
                estadoBtn.setOnAction(e -> {
                    FormatoAResumen formato = getTableView().getItems().get(getIndex());
                    if (formato == null) return;

                    if ("PENDIENTE".equalsIgnoreCase(formato.getEstadoFormatoA())) {
                        if (parent != null) {
                            parent.loadUI("Coordinador_Observaciones", formato);
                        }
                    } else {
                        alerta(Alert.AlertType.WARNING, "Acción no permitida", null,
                                "Este proyecto ya fue evaluado. No se puede volver a evaluar.");
                    }
                });
            }

            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setGraphic(null);
                    return;
                }

                estadoBtn.setText(estado.toUpperCase());
                estadoBtn.getStyleClass().clear();

                Image icon = null;
                if ("PENDIENTE".equalsIgnoreCase(estado)) {
                    estadoBtn.getStyleClass().add("estado-rojo");
                    icon = new Image(getClass().getResourceAsStream("/co/edu/unicauca/frontend/images/ojo_abierto.png"));
                } else if ("OBSERVADO".equalsIgnoreCase(estado) || "APROBADO".equalsIgnoreCase(estado)) {
                    estadoBtn.getStyleClass().add("estado-verde");
                    icon = new Image(getClass().getResourceAsStream("/co/edu/unicauca/frontend/images/ojo_cerrado.png"));
                } else {
                    estadoBtn.getStyleClass().add("estado-gris");
                }

                if (icon != null) {
                    ImageView iv = new ImageView(icon);
                    iv.setFitWidth(24);
                    iv.setFitHeight(22);
                    estadoBtn.setGraphic(iv);
                } else {
                    estadoBtn.setGraphic(null);
                }

                setGraphic(estadoBtn);
            }
        });
    }

    // ----------------------------------------
    // Configuración columna Descargar
    // ----------------------------------------
    private void agregarBotonDescargar() {
        colDescargar.setCellFactory(col -> new TableCell<>() {
            private final Button btnDescargar = new Button();
            private final ImageView imgView;

            {
                btnDescargar.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                imgView = new ImageView(new Image(
                        getClass().getResourceAsStream("/co/edu/unicauca/frontend/images/descargar.png")
                ));
                imgView.setFitWidth(20);
                imgView.setFitHeight(20);
                btnDescargar.setGraphic(imgView);

                btnDescargar.setOnAction(event -> {
                    FormatoAResumen formato = getTableView().getItems().get(getIndex());
                    if (formato != null) {
                        descargarArchivo(formato);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDescargar);
            }
        });
    }

    // ----------------------------------------
    // Función de descarga centralizada
    // ----------------------------------------
    private void descargarArchivo(FormatoAResumen formato) {
        try {
            byte[] data = formatoService.descargarFormatoA(formato.getId());

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar archivo PDF");
            fileChooser.setInitialFileName(formato.getNombreFormatoA());
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));

            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                Files.write(file.toPath(), data);

                alerta(Alert.AlertType.INFORMATION, "Descarga exitosa", null,
                        "El archivo se descargó en:\n" + file.getAbsolutePath());
            }

        } catch (Exception e) {
            e.printStackTrace();
            alerta(Alert.AlertType.ERROR, "Error al descargar", null,
                    "No se pudo descargar el archivo: " + e.getMessage());
        }
    }

    // ----------------------------------------
    // Función de alerta genérica
    // ----------------------------------------
    private void alerta(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
