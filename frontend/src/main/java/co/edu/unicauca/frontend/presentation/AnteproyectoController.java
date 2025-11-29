package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.FrontendServices;
import co.edu.unicauca.frontend.dto.AnteproyectoDto;
import co.edu.unicauca.frontend.dto.SessionInfo;
import co.edu.unicauca.frontend.infra.session.SessionData;
import co.edu.unicauca.frontend.infra.session.SessionManager;
import co.edu.unicauca.frontend.services.departmenthead.DepartmentHeadServiceFront;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

public class AnteproyectoController {

    @FXML private TableView<AnteproyectoDto> tblProyectos;
    @FXML private TableColumn<AnteproyectoDto, String> colTitulo;
    @FXML private TableColumn<AnteproyectoDto, String> colEstudianteNombre;
    @FXML private TableColumn<AnteproyectoDto, String> colEstudianteCorreo;
    @FXML private TableColumn<AnteproyectoDto, String> colVersion;
    @FXML private TextField txtBuscar;
    @FXML private Label lblTablaMsg;
    @FXML private Label nombreDocente;
    @FXML private Label lblAsignarMsg;

    private DepartmentHeadServiceFront service;
    private ObservableList<AnteproyectoDto> listaAnteproyectos;
    private DepartmentHeadController parent;

    // Si se usa para el llamado del padre
    public void setParentController(DepartmentHeadController parent) {
        this.parent = parent;
    }

    public AnteproyectoController() {}

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

        // Verificar que los componentes críticos estén inicializados
        if (tblProyectos == null || colTitulo == null) {
            return;
        }

        configurarColumnas();

        listaAnteproyectos = FXCollections.observableArrayList();
        tblProyectos.setItems(listaAnteproyectos);

        cargarAnteproyectos();

        // Configurar listener para búsqueda en tiempo real
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && !newValue.equals(oldValue)) {
                    buscarAnteproyectos();
                }
            });
        }
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


    /**
     * Configura las columnas de la tabla para mostrar los datos de los anteproyectos
     */
    private void configurarColumnas() {
        colTitulo.setCellValueFactory(cellData -> {
            AnteproyectoDto dto = cellData.getValue();
            String valor = dto != null && dto.getId() != null ? dto.getId().toString() : "";
            return new SimpleStringProperty(valor);
        });

        colEstudianteNombre.setCellValueFactory(cellData -> {
            AnteproyectoDto dto = cellData.getValue();
            String valor = dto != null && dto.getTitulo() != null ? dto.getTitulo() : "";
            return new SimpleStringProperty(valor);
        });

        colEstudianteCorreo.setCellValueFactory(cellData -> {
            AnteproyectoDto dto = cellData.getValue();
            String valor = dto != null && dto.getDescripcion() != null ? dto.getDescripcion() : "";
            return new SimpleStringProperty(valor);
        });

        colVersion.setCellValueFactory(cellData -> {
            AnteproyectoDto dto = cellData.getValue();
            String valor = dto != null && dto.getFechaCreacion() != null ? dto.getFechaCreacion() : "";
            return new SimpleStringProperty(valor);
        });
    }

    /**
     * Carga todos los anteproyectos sin evaluadores en la tabla
     */
    private void cargarAnteproyectos() {
        if (service == null) {
            return;
        }

        try {
            List<AnteproyectoDto> anteproyectos = service.obtenerAnteproyectosSinEvaluadores();

            if (anteproyectos == null) {
                anteproyectos = List.of();
            }

            listaAnteproyectos.setAll(anteproyectos);

            if (lblTablaMsg != null) {
                if (anteproyectos.isEmpty()) {
                    lblTablaMsg.setText("No hay anteproyectos disponibles");
                } else {
                    lblTablaMsg.setText("");
                }
            }
        } catch (Exception e) {
            if (lblTablaMsg != null) {
                lblTablaMsg.setText("Error al cargar los anteproyectos: " + e.getMessage());
            }
        }
    }

    /**
     * Realiza la búsqueda de anteproyectos por nombre o ID
     * Determina automáticamente el tipo de búsqueda basado en el formato del texto
     */
    private void buscarAnteproyectos() {
        if (txtBuscar == null || service == null) {
            return;
        }

        String searchQuery = txtBuscar.getText();
        if (searchQuery == null) {
            searchQuery = "";
        }
        searchQuery = searchQuery.trim();

        try {
            // Si la búsqueda está vacía, cargar todos los anteproyectos
            if (searchQuery.isEmpty()) {
                cargarAnteproyectos();
                return;
            }

            List<AnteproyectoDto> anteproyectos;

            // Determinar si es búsqueda por ID (numérico) o por nombre (texto)
            boolean esNumerico = searchQuery.matches("\\d+");

            if (esNumerico) {
                // Buscar por ID
                anteproyectos = service.buscarAnteproyectos(null, searchQuery);
            } else {
                // Buscar por nombre
                anteproyectos = service.buscarAnteproyectos(searchQuery, null);
            }

            if (anteproyectos == null) {
                anteproyectos = List.of();
            }

            listaAnteproyectos.setAll(anteproyectos);

            if (lblTablaMsg != null) {
                if (anteproyectos.isEmpty()) {
                    lblTablaMsg.setText("No se encontraron resultados para: " + searchQuery);
                } else {
                    lblTablaMsg.setText("Resultados encontrados: " + anteproyectos.size());
                }
            }
        } catch (Exception e) {
            if (lblTablaMsg != null) {
                lblTablaMsg.setText("Error al buscar: " + e.getMessage());
            }
        }
    }

    /**
     * Maneja el evento de refrescar la tabla
     * Limpia el campo de búsqueda y recarga todos los anteproyectos
     */
    @FXML
    private void onRefrescar(ActionEvent event) {
        if (txtBuscar != null) {
            txtBuscar.clear();
        }
        cargarAnteproyectos();
    }

    /**
     * Maneja el evento para cambiar la vista a la asignacion de evaluadores
     * @param event
     */
    @FXML
    private void onAsignar(ActionEvent event) {

        AnteproyectoDto seleccionado = tblProyectos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            lblAsignarMsg.setText("Debe seleccionar un anteproyecto para asignar evaluadores.");
            return;
        }

        lblAsignarMsg.setText("");

        parent.cargarVistaEnBorderPane(
                "/co/edu/unicauca/frontend/view/AsignarDocentes.fxml",
                seleccionado
        );
    }
}