package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.FrontendServices;
import co.edu.unicauca.frontend.dto.LoginRequestDto;
import co.edu.unicauca.frontend.dto.SessionInfo;
import co.edu.unicauca.frontend.entities.enums.Rol;
import co.edu.unicauca.frontend.infra.operation.LoginValidator;
import co.edu.unicauca.frontend.infra.session.SessionData;
import co.edu.unicauca.frontend.infra.session.SessionManager;
import co.edu.unicauca.frontend.presentation.navigation.ViewNavigator;
import co.edu.unicauca.frontend.services.auth.AuthServiceFront;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Map;

public class SignInController {

    @FXML
    private TextField txtCorreo;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private ComboBox<Rol> cbRol;

    @FXML
    private Label errCorreo;

    @FXML
    private Label errContrasena;

    @FXML
    private Label errRol;

    @FXML
    private Label errGeneral;

    private AuthServiceFront authService;

    @FXML
    private void initialize() {
        this.authService = FrontendServices.authService();

        // Configurar ComboBox con enums
        if (cbRol != null) {
            cbRol.getItems().addAll(Rol.values());
            // JavaFX automáticamente usará toString() para mostrar los nombres legibles
        }

        clearAllErrors();
    }

    @FXML
    private void ingresar() {
        clearAllErrors();

        String rawEmail = textOrEmpty(txtCorreo);
        String password = textOrEmpty(txtContrasena);
        Rol rolSeleccionado = cbRol.getValue(); // Directamente el enum

        // normalizar correo en el front
        String email = rawEmail.trim().toLowerCase();

        // 1) validación en cliente - ENVIAR NOMBRE REAL DEL ENUM
        Map<String, String> localErrors = LoginValidator.validate(
                email,
                password,
                rolSeleccionado != null ? rolSeleccionado.name() : null // Envía "ESTUDIANTE", "DOCENTE"
        );

        if (!localErrors.isEmpty()) {
            if (localErrors.containsKey("email")) {
                showError(errCorreo, localErrors.get("email"));
            }
            if (localErrors.containsKey("password")) {
                showError(errContrasena, localErrors.get("password"));
            }
            if (localErrors.containsKey("rol")) {
                showError(errRol, localErrors.get("rol"));
            }
            return;
        }

        // DTO con el enum directamente - Jackson se encargará de serializarlo correctamente
        LoginRequestDto dto = new LoginRequestDto(
                email,
                password,
                rolSeleccionado // Enum Rol.ESTUDIANTE, Rol.DOCENTE, etc.
        );

        // 2) llamada al servicio (valida en backend y guarda sesión si todo va bien)
        Map<String, String> errors = authService.loginAndReturnErrors(dto);

        if (!errors.isEmpty()) {
            if (errors.containsKey("email")) {
                showError(errCorreo, errors.get("email"));
            }
            if (errors.containsKey("password")) {
                showError(errContrasena, errors.get("password"));
            }
            if (errors.containsKey("rol")) {
                showError(errRol, errors.get("rol"));
            }
            if (errors.containsKey("general")) {
                showError(errGeneral != null ? errGeneral : errContrasena, errors.get("general"));
            }
            return;
        }

        // 3) obtener SessionData desde el SessionManager
        SessionData data = SessionManager.getInstance().getCurrentSession();
        SessionInfo session = (data != null) ? data.getSessionInfo() : null;

        if (session == null) {
            // algo salió mal: no hay sesión aunque el login no devolvió errores
            showError(errGeneral != null ? errGeneral : errContrasena,
                    "No fue posible establecer la sesión. Intente de nuevo.");
            return;
        }


        // opcional: mostrar confirmación
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Inicio de sesión");
        alert.setHeaderText(null);
        alert.setContentText("Ingresando como " + session.nombres() + " (" +
                (session.rolActivo() != null ? session.rolActivo().toString() : "Sin rol") + ")");
        alert.showAndWait();

        // 4) navegar según el rol activo
        navigateByRole(session);
    }

    @FXML
    private void goToRegister() {
        ViewNavigator.goTo("/co/edu/unicauca/frontend/view/SignUpView.fxml", "Registro de usuario");
    }

    /**
     * Decide a qué vista ir según el rol que quedó en la sesión.
     *
     * @param session información de sesión guardada luego del login.
     */
    private void navigateByRole(SessionInfo session) {
        if (session == null || session.rolActivo() == null) {
            // si por alguna razón no hay sesión, volver al login
            ViewNavigator.goTo("/co/edu/unicauca/frontend/view/SignIn.fxml", "Inicio de sesión");
            return;
        }

        Rol rol = session.rolActivo();

        switch (rol) {
            case ESTUDIANTE -> ViewNavigator.goTo(
                    "/co/edu/unicauca/frontend/view/Estudiante.fxml",
                    "Panel del estudiante"
            );
            case DOCENTE -> ViewNavigator.goTo(
                    "/co/edu/unicauca/frontend/view/Docente.fxml",
                    "Panel del docente"
            );
            case COORDINADOR -> ViewNavigator.goTo(
                    "/co/edu/unicauca/frontend/view/Coordinador.fxml",
                    "Panel del coordinador"
            );
            case JEFE_DE_DEPARTAMENTO -> ViewNavigator.goTo(
                    "/co/edu/unicauca/frontend/view/DepartmentHead.fxml",
                    "Panel del jefe de departamento"
            );
            default -> ViewNavigator.goTo(
                    "/co/edu/unicauca/frontend/view/SignIn.fxml",
                    "Inicio de sesión"
            );
        }
    }

    // =========================================================
    // helpers
    // =========================================================

    private String textOrEmpty(TextField field) {
        String t = field.getText();
        return t == null ? "" : t.trim();
    }

    private String textOrEmpty(PasswordField field) {
        String t = field.getText();
        return t == null ? "" : t;
    }

    private void showError(Label label, String message) {
        if (label == null) {
            return;
        }
        label.setText(message);
        label.getStyleClass().remove("error-hidden");
        label.setStyle("-fx-text-fill: red;");
    }

    private void clearError(Label label) {
        if (label == null) {
            return;
        }
        label.setText(" ");
        if (!label.getStyleClass().contains("error-hidden")) {
            label.getStyleClass().add("error-hidden");
        }
    }

    private void clearAllErrors() {
        clearError(errCorreo);
        clearError(errContrasena);
        clearError(errRol);
        clearError(errGeneral);
    }
}