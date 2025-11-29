package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.dto.SessionInfo;
import co.edu.unicauca.frontend.infra.session.SessionData;
import co.edu.unicauca.frontend.infra.session.SessionManager;
import co.edu.unicauca.frontend.presentation.navigation.ViewNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class DepartmentHeadController {

    @FXML
    private Button btnPrincipal;
    @FXML
    private Button btnAnteproyectos;
    @FXML
    private Button btnSalir;
    @FXML
    private BorderPane bp;
    @FXML
    private Label NombreJefeDepartamento; // Asegúrate de que el fx:id coincida con el FXML

    private Pane contenidoOriginal;

    @FXML
    public void initialize() {
        // Cargar información de la sesión
        cargarInformacionUsuario();

        if (bp != null && bp.getCenter() != null) {
            contenidoOriginal = (Pane) bp.getCenter();
        }

        if (btnPrincipal != null) {
            activarBoton(btnPrincipal, btnAnteproyectos, btnSalir);
        }
    }

    private void cargarInformacionUsuario() {
        SessionData data = SessionManager.getInstance().getCurrentSession();
        SessionInfo session = (data != null) ? data.getSessionInfo() : null;

        if (session != null && NombreJefeDepartamento != null) {
            NombreJefeDepartamento.setText(session.nombres());
        } else if (NombreJefeDepartamento != null) {
            NombreJefeDepartamento.setText("Usuario no identificado");
        }
    }


    @FXML
    private void showInfoPrincipal(ActionEvent event) {
        activarBoton(btnPrincipal, btnAnteproyectos, btnSalir);
        restaurarContenidoOriginal();
    }

    @FXML
    private void showInfoAnteproyectos(ActionEvent event) {
        activarBoton(btnAnteproyectos, btnPrincipal, btnSalir);
        cargarVistaEnBorderPane("/co/edu/unicauca/frontend/view/AnteproyectoJefeDepartamento.fxml");
    }

    @FXML
    private void switchToLogin(ActionEvent event) {
        // Limpiar la sesión antes de cerrar
        SessionManager.getInstance().clear();
        ViewNavigator.goTo("/co/edu/unicauca/frontend/view/SignIn.fxml", "Inicio de sesión");
    }

    private void restaurarContenidoOriginal() {
        if (bp != null && contenidoOriginal != null) {
            bp.setCenter(contenidoOriginal);
        }
    }

    public void cargarVistaEnBorderPane(String rutaFxml, Object data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Pane vista = loader.load();

            Object controller = loader.getController();

            // Si el controlador tiene el metodo setParentController, lo llamas
            try {
                controller.getClass()
                        .getMethod("setParentController", DepartmentHeadController.class)
                        .invoke(controller, this);
            } catch (NoSuchMethodException ignored) {
                // El controlador no lo implementa, no pasa nada
            }

            // Si el controlador tiene el metodo receiveData(Object data), lo llamas
            if (data != null) {
                try {
                    controller.getClass()
                            .getMethod("receiveData", Object.class)
                            .invoke(controller, data);
                } catch (NoSuchMethodException ignored) {
                    // No lo implementa, no pasa nada
                }
            }

            bp.setCenter(vista);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cargarVistaEnBorderPane(String rutaFxml) {
        cargarVistaEnBorderPane(rutaFxml, null);
    }

    private void activarBoton(Button botonActivo, Button... otros) {
        if (botonActivo != null) {
            botonActivo.getStyleClass().remove("btn-default");
            if (!botonActivo.getStyleClass().contains("btn-pressed")) {
                botonActivo.getStyleClass().add("btn-pressed");
            }
        }

        for (Button b : otros) {
            if (b != null) {
                b.getStyleClass().remove("btn-pressed");
                if (!b.getStyleClass().contains("btn-default")) {
                    b.getStyleClass().add("btn-default");
                }
            }
        }
    }
}