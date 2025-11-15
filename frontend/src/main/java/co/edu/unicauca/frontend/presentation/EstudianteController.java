package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.FrontendApp;
import co.edu.unicauca.frontend.dto.SessionInfo;
import co.edu.unicauca.frontend.infra.session.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EstudianteController implements Initializable {
    @FXML private AnchorPane contentArea;
    @FXML private Button btnPrincipal;
    @FXML private Button btnFormatoA;
    @FXML private Button btnSalir;
    @FXML private Label nombreEstudiante;

    private Button selectedButton = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnSalir.setOnAction(this::onSalir);

        btnPrincipal.setOnMouseEntered(e -> btnPrincipal.getStyleClass().add("hoverable"));
        btnPrincipal.setOnMouseExited(e -> btnPrincipal.getStyleClass().remove("hoverable"));

        btnFormatoA.setOnMouseEntered(e -> btnFormatoA.getStyleClass().add("hoverable"));
        btnFormatoA.setOnMouseExited(e -> btnFormatoA.getStyleClass().remove("hoverable"));

        btnSalir.setOnMouseEntered(e -> btnSalir.getStyleClass().add("hoverable"));
        btnSalir.setOnMouseExited(e -> btnSalir.getStyleClass().remove("hoverable"));

        // Configurar eventos para cada botón
        btnPrincipal.setOnAction(e -> {
            loadUI("EstudianteHome");
            selectButton(btnPrincipal);
        });

        // Configurar eventos para cada botón
        btnFormatoA.setOnAction(e -> {
            loadUI("FormatoAEstudiante");
            selectButton(btnFormatoA);
        });

        cargarDatos();
        loadUI("EstudianteHome");
        selectButton(btnPrincipal);
    }

    void cargarDatos() {
        SessionInfo estudiante = SessionManager.getInstance().getCurrentSession();
        if (estudiante != null) {
            nombreEstudiante.setText(estudiante.nombres());
        } else {
            System.err.println("No hay sesión activa");
        }
    }

    public void loadUI(String fxml) {
        try {
            FXMLLoader loader = FrontendApp.newLoader("/co/edu/unicauca/frontend/view/" + fxml + ".fxml");
            Parent root = loader.load();

            contentArea.getChildren().setAll(root);
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void selectButton(Button button) {
        // Si ya hay un botón seleccionado, quitarle la clase CSS
        if (selectedButton != null) {
            selectedButton.getStyleClass().remove("selected");
        }
        // Agregar clase al nuevo botón seleccionado
        if (!button.getStyleClass().contains("selected")) {
            button.getStyleClass().add("selected");
        }
        selectedButton = button;
    }

    @FXML
    public void onSalir(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = FrontendApp.newLoader("/co/edu/unicauca/frontend/view/SignIn.fxml");
            Parent root = loader.load();

            // Obtener la ventana actual (Stage)
            Stage stage = (Stage) btnSalir.getScene().getWindow();

            // Cambiar la escena por la del inicio de sesión
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
