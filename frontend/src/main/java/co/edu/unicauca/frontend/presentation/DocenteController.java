package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.FrontendServices;
import co.edu.unicauca.frontend.dto.SessionInfo;
import co.edu.unicauca.frontend.infra.session.SessionData;
import co.edu.unicauca.frontend.infra.session.SessionManager;
import co.edu.unicauca.frontend.presentation.navigation.ViewNavigator;
import co.edu.unicauca.frontend.services.academic.DocenteService;
import co.edu.unicauca.frontend.services.academic.EstudianteService;
import co.edu.unicauca.frontend.services.academic.ProyectoService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DocenteController implements Initializable {

    @FXML
    private Button btnPrincipal;
    @FXML
    private Button btnFormatoA;
    @FXML
    private Button btnAnteproyecto;
    @FXML
    private Button btnSalir;
    @FXML
    private Label nombreDocente;
    @FXML
    private BorderPane bp;
    @FXML
    private AnchorPane ap;

    public static boolean estadisticasAbiertas = false;
    public static Stage estadisticasStage = null;

    private DocenteService docenteService;
    private ProyectoService proyectoService;
    private EstudianteService estudianteService;

    public void setServices(DocenteService docenteService, ProyectoService proyectoService, EstudianteService estudianteService) {
        this.docenteService = docenteService;
        this.proyectoService = proyectoService;
        this.estudianteService = estudianteService;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            this.docenteService = FrontendServices.docenteService();
            this.proyectoService = FrontendServices.proyectoService();
            this.estudianteService = FrontendServices.estudianteService();
        } catch (IllegalStateException e) {
            System.err.println("Error: servicios no disponibles. Asegúrate de llamar FrontendServices.init() antes.");
            return;
        }
        activarBoton(btnPrincipal, btnFormatoA, btnSalir, btnAnteproyecto);
        cargarDatos();
    }

    @FXML
    void switchToLogin(ActionEvent event) {
        ViewNavigator.goTo("/co/edu/unicauca/frontend/view/SignIn.fxml", "Inicio de sesión");
    }

    @FXML
    private void showInfoPrincipal(ActionEvent event) {
        activarBoton(btnPrincipal, btnFormatoA, btnSalir, btnAnteproyecto);
        if (FormatoADocenteController.estadisticasAbiertas && FormatoADocenteController.estadisticasStage != null) {
            FormatoADocenteController.estadisticasStage.close();
            FormatoADocenteController.estadisticasStage = null;
            FormatoADocenteController.estadisticasAbiertas = false;
        }
        bp.setCenter(ap);
    }

    @FXML
    private void showInfoFormatoA(ActionEvent event) {
        activarBoton(btnFormatoA, btnPrincipal, btnSalir, btnAnteproyecto);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/co/edu/unicauca/frontend/view/FormatoADocente.fxml"
            ));

            Parent vista = loader.load();
            FormatoADocenteController formatoAController = loader.getController();
            formatoAController.setServices(docenteService, proyectoService, estudianteService);
            formatoAController.cargarDatos();
            bp.setCenter(vista);

            if (!FormatoADocenteController.estadisticasAbiertas) {

                FXMLLoader loaderEst = new FXMLLoader(getClass().getResource(
                        "/co/edu/unicauca/frontend/view/EstadisticasDocente.fxml"
                ));

                Parent vistaEst = loaderEst.load();
                EstadisticasDocenteController eController = loaderEst.getController();
                eController.setServices(docenteService, proyectoService, estudianteService);

                Stage stage = new Stage();
                stage.setTitle("Estadísticas - Docente");
                stage.setScene(new Scene(vistaEst));

                FormatoADocenteController.estadisticasAbiertas = true;
                FormatoADocenteController.estadisticasStage = stage;

                stage.setOnCloseRequest(ev -> {
                    FormatoADocenteController.estadisticasAbiertas = false;
                    FormatoADocenteController.estadisticasStage = null;
                });

                stage.show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void funcAnteproyecto(ActionEvent event) {
        activarBoton(btnAnteproyecto, btnPrincipal, btnFormatoA, btnSalir);
        if (FormatoADocenteController.estadisticasAbiertas && FormatoADocenteController.estadisticasStage != null) {
            FormatoADocenteController.estadisticasStage.close();
            FormatoADocenteController.estadisticasStage = null;
            FormatoADocenteController.estadisticasAbiertas = false;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/co/edu/unicauca/frontend/view/AnteproyectoDocente.fxml"
            ));

            Parent vista = loader.load();
            AnteproyectoDocenteController antePController = loader.getController();
            antePController.setServices(docenteService, proyectoService, estudianteService);
            bp.setCenter(vista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cargarDatos() {
        // Antes: SessionInfo docente = SessionManager.getInstance().getCurrentSession();
        SessionData data = SessionManager.getInstance().getCurrentSession();
        SessionInfo docente = (data != null) ? data.getSessionInfo() : null;

        if (docente != null) {
            nombreDocente.setText(docente.nombres());
        } else {
            System.err.println("No hay sesión activa");
        }
    }

    private void activarBoton(Button botonActivo, Button... otros) {
        botonActivo.getStyleClass().remove("btn-default");
        if (!botonActivo.getStyleClass().contains("btn-pressed")) {
            botonActivo.getStyleClass().add("btn-pressed");
        }

        for (Button b : otros) {
            b.getStyleClass().remove("btn-pressed");
            if (!b.getStyleClass().contains("btn-default")) {
                b.getStyleClass().add("btn-default");
            }
        }
    }
}
