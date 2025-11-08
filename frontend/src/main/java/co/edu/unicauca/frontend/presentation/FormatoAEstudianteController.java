package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.FrontendServices;
import co.edu.unicauca.frontend.infra.dto.ProyectoEstudianteDTO;
import co.edu.unicauca.frontend.services.ProyectoEstudianteService;
import co.edu.unicauca.frontend.services.ProyectoService;
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
import java.util.ResourceBundle;

public class FormatoAEstudianteController implements Initializable {
    @FXML private TableView<ProyectoEstudianteDTO> tabla;
    @FXML private TableColumn<ProyectoEstudianteDTO, String> colTitulo;
    @FXML private TableColumn<ProyectoEstudianteDTO, String> colTipo;
    @FXML private TableColumn<ProyectoEstudianteDTO, String> colDirector;
    @FXML private TableColumn<ProyectoEstudianteDTO, String> colEstado;
    @FXML private TableColumn<ProyectoEstudianteDTO, Void> colDescarga;

    private final ProyectoEstudianteService proyectoEstService = new ProyectoEstudianteService();
    private ProyectoService proyectoService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.proyectoService = FrontendServices.proyectoService();

        configurarColumnas();
        configurarColumnaEstado();
        agregarBotonDescargar();
        cargarDatos();
    }

    public void configurarColumnas() {
        colTitulo.setCellValueFactory(cell -> cell.getValue().tituloProperty());

        colTipo.setCellValueFactory(cellData -> {
            String tipo = cellData.getValue().getTipoProyecto();
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

        colDirector.setCellValueFactory(cell -> cell.getValue().nombreDirectorProperty());

        colEstado.setCellValueFactory(cellData -> {
            String tipo = cellData.getValue().getEstadoProyecto(); // EN_TRAMITE | RECHAZADO | TERMINADO
            String estP;
            switch (tipo) {
                case "EN_TRAMITE":
                    estP = "En trámite";
                    break;
                case "RECHAZADO":
                    estP = "Rechazado";
                    break;
                case "TERMINADO":
                    estP = "Terminado";
                    break;
                default:
                    estP = tipo;
                    break;
            }
            return new ReadOnlyStringWrapper(estP);
        });
    }

    /**
     * Alineo los íconos con los estados del proyecto.
     */
    private void configurarColumnaEstado() {
        colEstado.setCellFactory(col -> new TableCell<ProyectoEstudianteDTO, String>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String estadoLegible, boolean empty) {
                super.updateItem(estadoLegible, empty);
                if (empty || estadoLegible == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                // Mapeo: EN_TRAMITE -> pendiente, RECHAZADO -> observado, TERMINADO -> aprobado
                String raw = getTableView().getItems().get(getIndex()).getEstadoProyecto();
                String key = raw == null ? "" : raw.trim().toUpperCase();

                Image img = switch (key) {
                    case "EN_TRAMITE" -> loadImage("/co/unicauca/workflow/degree_project/images/pendiente.png");
                    case "RECHAZADO" -> loadImage("/co/unicauca/workflow/degree_project/images/observado.png");
                    case "TERMINADO" -> loadImage("/co/unicauca/workflow/degree_project/images/aprobado.png");
                    default -> null;
                };

                if (img != null) {
                    imageView.setImage(img);
                    imageView.setFitWidth(20);
                    imageView.setFitHeight(20);
                    setGraphic(imageView);
                    setText(null);
                } else {
                    setGraphic(null);
                    setText(estadoLegible);
                }
            }
        });
    }

    private Image loadImage(String resourcePath) {
        var is = getClass().getResourceAsStream(resourcePath);
        return (is == null) ? null : new Image(is);
    }

    public void cargarDatos() {
        try {
            var proyectos = proyectoEstService.obtenerProyectosEstudiante();
            tabla.setItems(FXCollections.observableArrayList(proyectos));
        } catch (Exception e) {
            e.printStackTrace();
            alerta(Alert.AlertType.ERROR, "Error", null, "Error al cargar los datos: " + e.getMessage());
        }
    }

    private void alerta(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void agregarBotonDescargar() {
        colDescarga.setCellFactory(col -> new TableCell<>() {
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
                btnDescargar.setTooltip(new Tooltip("Descargar Formato A con observaciones"));

                btnDescargar.setOnAction(event -> {
                    ProyectoEstudianteDTO proyecto = getTableView().getItems().get(getIndex());
                    if (proyecto == null) return;

                    if (proyectoService == null) {
                        alerta(Alert.AlertType.ERROR, "Error", null, "Servicio no inicializado.");
                        return;
                    }

                    try {
                        var form = proyectoService.obtenerUltimoFormatoAConObservaciones(proyecto.getId());
                        if (form == null || form.getBlob() == null) {
                            alerta(Alert.AlertType.INFORMATION, "Sin archivo", null, "No hay Formato A con observaciones.");
                            return;
                        }

                        FileChooser fc = new FileChooser();
                        fc.setTitle("Guardar Formato A con observaciones");
                        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
                        String nombre = (form.getNombreFormato() != null && !form.getNombreFormato().isBlank())
                                ? form.getNombreFormato() : "formatoA.pdf";
                        fc.setInitialFileName(nombre);

                        File dest = fc.showSaveDialog(tabla.getScene().getWindow());
                        if (dest == null) return;

                        Files.write(dest.toPath(), form.getBlob());
                    } catch (Exception e) {
                        e.printStackTrace();
                        alerta(Alert.AlertType.ERROR, "Error", null, "No se pudo descargar: " + e.getMessage());
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
}
