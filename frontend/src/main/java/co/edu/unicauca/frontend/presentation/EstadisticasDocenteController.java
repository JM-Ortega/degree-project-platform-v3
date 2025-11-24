package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.FrontendServices;
import co.edu.unicauca.frontend.dto.SessionInfo;
import co.edu.unicauca.frontend.infra.session.SessionData;
import co.edu.unicauca.frontend.infra.session.SessionManager;
import co.edu.unicauca.frontend.services.DocenteService;
import co.edu.unicauca.frontend.services.EstudianteService;
import co.edu.unicauca.frontend.services.Observer;
import co.edu.unicauca.frontend.services.ProyectoService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.ResourceBundle;

public class EstadisticasDocenteController implements Initializable, Observer {

    @FXML private BarChart<String, Number> BarChartEstadisticas;
    private XYChart.Series<String, Number> seriesTesis;
    private XYChart.Series<String, Number> seriesPractica;

    private ProyectoService proyectoService;
    private EstudianteService estudianteService;
    private DocenteService docenteService;

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

        seriesTesis = new XYChart.Series<>();
        seriesTesis.setName("TESIS");

        seriesPractica = new XYChart.Series<>();
        seriesPractica.setName("PRACTICA PROFESIONAL");

        BarChartEstadisticas.getData().addAll(seriesTesis, seriesPractica);
    }

    private void cargarEstadisticas() {

        // limpiar datos anteriores para que no se dupliquen
        seriesTesis.getData().clear();
        seriesPractica.getData().clear();

        seriesTesis.getData().add(new XYChart.Data<>("TERMINADOS", obtenerCantidad("TRABAJO_DE_INVESTIGACION", "TERMINADO")));
        seriesTesis.getData().add(new XYChart.Data<>("RECHAZADOS", obtenerCantidad("TRABAJO_DE_INVESTIGACION", "RECHAZADO")));
        seriesTesis.getData().add(new XYChart.Data<>("EN TRAMITE", obtenerCantidad("TRABAJO_DE_INVESTIGACION", "EN_TRAMITE")));

        seriesPractica.getData().add(new XYChart.Data<>("TERMINADOS", obtenerCantidad("PRACTICA_PROFESIONAL", "TERMINADO")));
        seriesPractica.getData().add(new XYChart.Data<>("RECHAZADOS", obtenerCantidad("PRACTICA_PROFESIONAL", "RECHAZADO")));
        seriesPractica.getData().add(new XYChart.Data<>("EN TRAMITE", obtenerCantidad("PRACTICA_PROFESIONAL", "EN_TRAMITE")));
    }

    private int obtenerCantidad(String tipo, String estado) {

        SessionData data = SessionManager.getInstance().getCurrentSession();
        SessionInfo docente = (data != null) ? data.getSessionInfo() : null;

        if (docente == null) {
            System.err.println("No hay sesión activa");
            return 0;
        }

        return proyectoService.countProyectosByEstadoYTipo(tipo, estado, docente.email());
    }

    public void setServices(DocenteService docenteService, ProyectoService proyectoService, EstudianteService estudianteService) {
        this.proyectoService = proyectoService;
        this.docenteService = docenteService;
        this.estudianteService = estudianteService;

        this.proyectoService.addObserver(this);
        cargarEstadisticas();
    }

    @Override
    public void update() {
        cargarEstadisticas();
    }
}