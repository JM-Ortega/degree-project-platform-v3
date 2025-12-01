package co.edu.unicauca.frontend.presentation;

import co.edu.unicauca.frontend.FrontendServices;
import co.edu.unicauca.frontend.dto.SessionInfo;
import co.edu.unicauca.frontend.entities.EstadoProyecto;
import co.edu.unicauca.frontend.infra.session.SessionData;
import co.edu.unicauca.frontend.infra.session.SessionManager;
import co.edu.unicauca.frontend.services.academic.DocenteService;
import co.edu.unicauca.frontend.services.academic.EstudianteService;
import co.edu.unicauca.frontend.services.academic.Observer;
import co.edu.unicauca.frontend.services.academic.ProyectoService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
        List<String> estadosEnTramite = new ArrayList<>();
        estadosEnTramite.add(EstadoProyecto.PRIMERA_REVISION_FORMATOA.toString());
        estadosEnTramite.add(EstadoProyecto.SEGUNDA_REVISION_FORMATOA.toString());
        estadosEnTramite.add(EstadoProyecto.TERCERA_REVISION_FORMATOA.toString());
        estadosEnTramite.add(EstadoProyecto.FORMATOA_ACEPTADO.toString());
        estadosEnTramite.add(EstadoProyecto.ANTEPROYECTO_ENVIADO.toString());

        seriesTesis.getData().add(new XYChart.Data<>("TERMINADOS", obtenerCantidad("TRABAJO_DE_INVESTIGACION", "EN_REVISION_ANTEPROYECTO")));
        seriesTesis.getData().add(new XYChart.Data<>("RECHAZADOS", obtenerCantidad("TRABAJO_DE_INVESTIGACION", "FORMATOA_RECHAZADO")));
        seriesTesis.getData().add(new XYChart.Data<>("EN TRAMITE", obtenerCantidadVariosEstados("TRABAJO_DE_INVESTIGACION", estadosEnTramite)));

        seriesPractica.getData().add(new XYChart.Data<>("TERMINADOS", obtenerCantidad("PRACTICA_PROFESIONAL", "EN_REVISION_ANTEPROYECTO")));
        seriesPractica.getData().add(new XYChart.Data<>("RECHAZADOS", obtenerCantidad("PRACTICA_PROFESIONAL", "FORMATOA_RECHAZADO")));
        seriesPractica.getData().add(new XYChart.Data<>("EN TRAMITE", obtenerCantidadVariosEstados("PRACTICA_PROFESIONAL", estadosEnTramite)));
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

    private int obtenerCantidadVariosEstados(String tipo, List<String> estados) {
        SessionData data = SessionManager.getInstance().getCurrentSession();
        SessionInfo docente = (data != null) ? data.getSessionInfo() : null;

        if (docente == null) {
            System.err.println("No hay sesión activa");
            return 0;
        }

        int total = 0;
        for (String estado : estados) {
            int cantidad = proyectoService.countProyectosByEstadoYTipo(tipo, estado, docente.email());
            total += cantidad;
        }
        return total;
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