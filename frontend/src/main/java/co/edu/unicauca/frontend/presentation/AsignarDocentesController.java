package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.FrontendServices;
import co.edu.unicauca.frontend.dto.AnteproyectoDto;
import co.edu.unicauca.frontend.dto.SessionInfo;
import co.edu.unicauca.frontend.infra.dto.UsuarioDTO;
import co.edu.unicauca.frontend.infra.session.SessionData;
import co.edu.unicauca.frontend.infra.session.SessionManager;
import co.edu.unicauca.frontend.services.departmenthead.DepartmentHeadServiceFront;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;


public class AsignarDocentesController{
    @FXML private Button btnAsignar;
    @FXML private Button btnCancelar;
    @FXML private Button btnRefrescar;
    @FXML private TableColumn<UsuarioDTO, String> colEmail;
    @FXML private TableColumn<UsuarioDTO, String> colNombre;
    @FXML private TableView<UsuarioDTO> tblDocentes;
    @FXML private Label lblDescripcion;
    @FXML private Label lblEvaluador1;
    @FXML private Label lblEvaluador2;
    @FXML private Label lblTitulo;
    @FXML private Label nombreDocente;
    @FXML private TextField txtBuscar;
    @FXML private Label lblTablaMsg;

    private DepartmentHeadServiceFront service;
    private DepartmentHeadController parent;
    private AnteproyectoDto anteproyecto;

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
        cargarDatos();

    }

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

    /**
     * Carga todos los docentes del departamento de jefe en la tabla
     */
//    private void cargarDocentes() {
//        if (service == null) {
//            return;
//        }
//
//        try {
//            List<> anteproyectos = service.obtenerAnteproyectosSinEvaluadores();
//
//            if (anteproyectos == null) {
//                anteproyectos = List.of();
//            }
//
//            listaAnteproyectos.setAll(anteproyectos);
//
//            if (lblTablaMsg != null) {
//                if (anteproyectos.isEmpty()) {
//                    lblTablaMsg.setText("No hay anteproyectos disponibles");
//                } else {
//                    lblTablaMsg.setText("");
//                }
//            }
//        } catch (Exception e) {
//            if (lblTablaMsg != null) {
//                lblTablaMsg.setText("Error al cargar los anteproyectos: " + e.getMessage());
//            }
//        }
//    }

    /**
     * Maneja el evento de refrescar la tabla
     * Limpia el campo de búsqueda y recarga todos los anteproyectos
     */
    @FXML
    private void onRefrescar(ActionEvent event) {
        if (txtBuscar != null) {
            txtBuscar.clear();
        }
        //cargarDocentes();
    }

    @FXML
    private void onCancelar (ActionEvent event) {
        parent.cargarVistaEnBorderPane("/co/edu/unicauca/frontend/view/AnteproyectoJefeDepartamento.fxml");
    }
}
