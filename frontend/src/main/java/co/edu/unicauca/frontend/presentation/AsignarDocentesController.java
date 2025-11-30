package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.FrontendServices;
import co.edu.unicauca.frontend.dto.AnteproyectoDto;
import co.edu.unicauca.frontend.dto.SessionInfo;
import co.edu.unicauca.frontend.infra.dto.DocenteDTO;
import co.edu.unicauca.frontend.infra.session.SessionData;
import co.edu.unicauca.frontend.infra.session.SessionManager;
import co.edu.unicauca.frontend.services.departmenthead.DepartmentHeadServiceFront;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;


public class AsignarDocentesController{
    @FXML private Button btnAsignar;
    @FXML private Button btnCancelar;
    @FXML private Button btnRefrescar;
    @FXML private TableColumn<DocenteDTO, String> colEmail;
    @FXML private TableColumn<DocenteDTO, String> colNombre;
    @FXML private TableView<DocenteDTO> tblDocentes;
    @FXML private Label lblDescripcion;
    @FXML private Label lblEvaluador1;
    @FXML private Label lblEvaluador2;
    @FXML private Label lblTitulo;
    @FXML private Label nombreDocente;
    @FXML private Label lblTablaMsg;
    @FXML private Label lblAsignar;

    private DepartmentHeadServiceFront service;
    private DepartmentHeadController parent;
    private AnteproyectoDto anteproyecto;

    private DocenteDTO seleccion1 = null;
    private DocenteDTO seleccion2 = null;

    private String correoDocente1 = null;
    private String correoDocente2 = null;

    private ObservableList<DocenteDTO> listaDocentes;

    // Si se usa para el llamado del padre
    public void setParentController(DepartmentHeadController parent) {
        this.parent = parent;
    }

    @FXML
    public void initialize() {
        try {
            this.service = FrontendServices.departmentHeadService();
        } catch (IllegalStateException e) {
            if (lblTablaMsg != null) {
                lblTablaMsg.setText("Error: Servicios no disponibles");
            }
            return;
        }

        // Cargar información del usuario logueado
        cargarInformacionUsuario();

        configurarColumnas();

        listaDocentes = FXCollections.observableArrayList();
        tblDocentes.setItems(listaDocentes);

        cargarDatos();
        cargarDocentes();

        configurarSeleccionDocentes();

        tblDocentes.getSortOrder().clear();
    }


    // No borrar se usa apra cargar los datos de la fila seleccionada, aunque diga que no tiene usos
    public void receiveData(Object data) {
        if (data instanceof AnteproyectoDto dto) {
            this.anteproyecto = dto;
            cargarDatos();
        }
    }

    private void cargarDatos() {
        if (anteproyecto == null) return;
        lblTitulo.setText(anteproyecto.getTitulo());
        lblDescripcion.setText(anteproyecto.getDescripcion());
    }

    /**
     * Carga la información del usuario desde la sesión activa
     */
    private void cargarInformacionUsuario() {
        SessionData data = SessionManager.getInstance().getCurrentSession();
        SessionInfo session = (data != null) ? data.getSessionInfo() : null;

        if (session != null && nombreDocente != null) {
            nombreDocente.setText(session.nombres());
        } else if (nombreDocente != null) {
            nombreDocente.setText("Usuario no identificado");
        }
    }

    private void configurarColumnas() {
        colEmail.setCellValueFactory(cellData -> {
            DocenteDTO dto = cellData.getValue();
            String valor = dto != null && dto.getEmail() != null ? dto.getEmail().toString() : "";
            return new SimpleStringProperty(valor);
        });

        colNombre.setCellValueFactory(cellData -> {
            DocenteDTO dto = cellData.getValue();
            String valor = dto != null && dto.getNombre() != null ? dto.getNombre() : "";
            return new SimpleStringProperty(valor);
        });

        colNombre.setSortable(false);
        colEmail.setSortable(false);
        tblDocentes.getSortOrder().clear();
    }

    /**
     * Carga todos los docentes del departamento de jefe en la tabla
     */
    private void cargarDocentes() {
        if (service == null) {
            return;
        }

        SessionData data = SessionManager.getInstance().getCurrentSession();
        String correoJefe = (data != null) ? data.getSessionInfo().email() : null;

        try {
            List<DocenteDTO> docentes = service.obtenerDocentes(correoJefe);

            if (docentes == null) {
                docentes = List.of();
            }

            listaDocentes.setAll(docentes);

            if (lblTablaMsg != null) {
                if (docentes.isEmpty()) {
                    lblTablaMsg.setText("No hay docentes disponibles");
                } else {
                    lblTablaMsg.setText("");
                }
            }
        } catch (Exception e) {
            if (lblTablaMsg != null) {
                showAlert("Error al cargar los docentes", "Ocurrió un error inesperado.", e.getMessage());
                lblTablaMsg.setText("Error al cargar los docentes: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void configurarSeleccionDocentes() {

        tblDocentes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // RowFactory para colorear filas seleccionadas
        tblDocentes.setRowFactory(tv -> new TableRow<DocenteDTO>() {
            @Override
            protected void updateItem(DocenteDTO item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle("");
                    return;
                }

                // Colores si corresponde a una de las selecciones
                if (item.equals(seleccion1)) {
                    setStyle("-fx-background-color: #B3E5FC;"); // Azul suave
                } else if (item.equals(seleccion2)) {
                    setStyle("-fx-background-color: #C8E6C9;"); // Verde suave
                } else {
                    setStyle("");
                }
            }
        });

        // Manejador del clic
        tblDocentes.setOnMouseClicked(event -> {
            DocenteDTO item = tblDocentes.getSelectionModel().getSelectedItem();
            if (item == null) return;

            // --------------- LÓGICA DE DESELECCIÓN ---------------
            if (item.equals(seleccion1)) {
                seleccion1 = null;
                correoDocente1 = null;
                lblEvaluador1.setText("");
                tblDocentes.refresh();
                return;
            }

            if (item.equals(seleccion2)) {
                seleccion2 = null;
                correoDocente2 = null;
                lblEvaluador2.setText("");
                tblDocentes.refresh();
                return;
            }

            // --------------- EVITAR MÁS DE 2 SELECCIONES ---------------
            if (seleccion1 != null && seleccion2 != null) {
                lblTablaMsg.setText("Solo puedes seleccionar 2 docentes");
                // Quitar la selección automática de JavaFX
                tblDocentes.getSelectionModel().clearSelection();
                // Restaurar las dos verdaderas selecciones
                if (seleccion1 != null) tblDocentes.getSelectionModel().select(seleccion1);
                if (seleccion2 != null) tblDocentes.getSelectionModel().select(seleccion2);
                return;
            }

            // --------------- NUEVA SELECCIÓN ---------------
            if (seleccion1 == null) {
                seleccion1 = item;
                correoDocente1 = item.getEmail();
                lblEvaluador1.setText(item.getNombre());
            } else if (seleccion2 == null) {
                seleccion2 = item;
                correoDocente2 = item.getEmail();
                lblEvaluador2.setText(item.getNombre());
            }

            tblDocentes.refresh();
        });
    }


    /**
     * Maneja el evento de refrescar la tabla
     * Limpia el campo de búsqueda y recarga todos los anteproyectos
     */
    @FXML
    private void onRefrescar(ActionEvent event) {
        // --- Limpieza total de selecciones ---
        seleccion1 = null;
        seleccion2 = null;
        correoDocente1 = null;
        correoDocente2 = null;

        if (lblEvaluador1 != null) lblEvaluador1.setText("");
        if (lblEvaluador2 != null) lblEvaluador2.setText("");
        if (lblAsignar != null) lblAsignar.setText("");

        tblDocentes.getSelectionModel().clearSelection();
        tblDocentes.refresh();

        cargarDocentes();
    }

    @FXML
    private void onCancelar (ActionEvent event) {
        parent.cargarVistaEnBorderPane("/co/edu/unicauca/frontend/view/AnteproyectoJefeDepartamento.fxml");
    }

    @FXML
    private void onAsignar(ActionEvent event) {

        if (correoDocente1 == null || correoDocente2 == null) {
            lblAsignar.setText("Debes seleccionar 2 docentes para asignar.");
            return;
        }

        lblAsignar.setText(""); // limpiar mensajes

        try {
            service.asignarEvaluadores(correoDocente1, correoDocente2, anteproyecto.getId());
            showAlert("Exito", "Se asignaron los evaluadores", "¡Evaluadores asignados correctamente!");
            parent.cargarVistaEnBorderPane("/co/edu/unicauca/frontend/view/AnteproyectoJefeDepartamento.fxml");
        } catch (Exception e) {
            showAlert("Error al asignar los evaluadores", "Ocurrió un error inesperado.", e.getMessage());
        }
    }
}
