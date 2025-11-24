package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.FrontendServices;
import co.edu.unicauca.frontend.dto.SessionInfo;
import co.edu.unicauca.frontend.entities.CoordinadorResumen;
import co.edu.unicauca.frontend.entities.FormatoAResumen;
import co.edu.unicauca.frontend.infra.session.SessionData;
import co.edu.unicauca.frontend.infra.session.SessionManager;
import co.edu.unicauca.frontend.presentation.navigation.ViewLoader;
import co.edu.unicauca.frontend.presentation.navigation.ViewNavigator;
import co.edu.unicauca.frontend.services.coordinator.CoordinadorClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class CoordinadorController implements Initializable {
    @FXML
    private AnchorPane contentArea;
    @FXML
    private Button btnProyectos;
    @FXML
    private Button btnSalir;
    @FXML
    private Label lblNombreCompleto;
    @FXML
    private Label lblPrograma;

    private final CoordinadorClient client = FrontendServices.coordinadorClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private Button selectedButton = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnSalir.setOnAction(this::onSalir);

        btnProyectos.setOnMouseEntered(e -> btnProyectos.getStyleClass().add("hoverable"));
        btnProyectos.setOnMouseExited(e -> btnProyectos.getStyleClass().remove("hoverable"));

        btnSalir.setOnMouseEntered(e -> btnSalir.getStyleClass().add("hoverable"));
        btnSalir.setOnMouseExited(e -> btnSalir.getStyleClass().remove("hoverable"));

        btnProyectos.setOnAction(e -> {
            loadUI("Coordinador_Proyectos");
            selectButton(btnProyectos);
        });

        cargarDatos();
    }

    void cargarDatos() {
        try {
            // Ahora leemos SessionData y sacamos el SessionInfo de dentro
            SessionData data = SessionManager.getInstance().getCurrentSession();
            SessionInfo session = (data != null) ? data.getSessionInfo() : null;

            if (session == null || session.email() == null || session.email().isBlank()) {
                ViewNavigator.goTo("/co/edu/unicauca/frontend/view/SignIn.fxml", "Inicio de sesión");
                return;
            }

            // Mostrar el nombre localmente antes de cargar datos del backend
            lblNombreCompleto.setText(session.nombres());

            String email = session.email().trim().toLowerCase();
            String json = client.getCoordinadorInfo(email);

            if (json.contains("\"timestamp\"") || json.contains("\"error\"")) {
                System.err.println("Error del backend: " + json);
                return;
            }

            CoordinadorResumen info = mapper.readValue(json, CoordinadorResumen.class);
            String programa;
            switch (info.getPrograma()) {
                case "INGENIERIA_DE_SISTEMAS":
                    programa = "Ingenieria de Sistemas";
                    break;
                case "INGENIERIA_ELECTRONICA_Y_TELECOMUNICACIONES":
                    programa = "Ingenieria Electronica y Telecomunicaciones";
                    break;
                case "AUTOMATICA_INDUSTRIAL":
                    programa = "Automatica Industrial";
                    break;
                case "TECNOLOGIA_EN_TELEMATICA":
                    programa = "Tecnologia En Telematica";
                    break;
                default:
                    programa = "Unknown";
            }
            lblPrograma.setText(programa); // solo actualiza el programa

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void selectButton(Button button) {
        if (selectedButton != null) selectedButton.getStyleClass().remove("selected");
        if (!button.getStyleClass().contains("selected")) button.getStyleClass().add("selected");
        selectedButton = button;
    }

    public void loadUI(String fxml) {
        String path = "/co/edu/unicauca/frontend/view/" + fxml + ".fxml";
        Object controller = ViewLoader.loadIntoWithController(contentArea, path);

        if (controller instanceof CoProyectoController cpc) {
            cpc.setParentController(this);
        }

        // Asegurar anclaje (si tu FXML no trae AnchorPane.fitToAnchorPane)
        Parent root = (Parent) contentArea.getChildren().getFirst();
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);
        AnchorPane.setLeftAnchor(root, 0.0);
    }

    public void loadUI(String fxmlPath, FormatoAResumen formatoSeleccionado) {
        String path = "/co/edu/unicauca/frontend/view/" + fxmlPath + ".fxml";
        Object controller = ViewLoader.loadIntoWithController(contentArea, path);

        if (controller instanceof CoObservacionesController c) {
            c.setFormatoSeleccionado(formatoSeleccionado);
            c.setParentController(this);
        }
    }

    @FXML
    public void onSalir(javafx.event.ActionEvent event) {
        // Limpia sesión y vuelve al login
        SessionManager.getInstance().clear();
        ViewNavigator.goTo("/co/edu/unicauca/frontend/view/SignIn.fxml", "Inicio de sesión");
    }
}
