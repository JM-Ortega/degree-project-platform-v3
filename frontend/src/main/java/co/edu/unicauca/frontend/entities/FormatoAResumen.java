package co.edu.unicauca.frontend.entities;

import javafx.beans.property.*;
import java.time.LocalDate;

public class FormatoAResumen {

    private StringProperty id = new SimpleStringProperty(); // UUID como String
    private StringProperty nombreProyecto = new SimpleStringProperty();
    private StringProperty nombreDirector = new SimpleStringProperty();
    private StringProperty tipoProyecto = new SimpleStringProperty();
    private ObjectProperty<LocalDate> fechaSubida = new SimpleObjectProperty<>();
    private StringProperty estadoFormatoA = new SimpleStringProperty();
    private IntegerProperty nroVersion = new SimpleIntegerProperty();
    private StringProperty nombreFormatoA = new SimpleStringProperty();

    public FormatoAResumen() {}

    public FormatoAResumen(
            String id,                   // 👈 antes era Long
            String nombreProyecto,
            String nombreDirector,
            String tipoProyecto,
            LocalDate fechaSubida,
            String estadoFormatoA,
            int nroVersion,
            String nombreFormatoA
    ) {
        this.id = new SimpleStringProperty(id);
        this.nombreProyecto = new SimpleStringProperty(nombreProyecto);
        this.nombreDirector = new SimpleStringProperty(nombreDirector);
        this.tipoProyecto = new SimpleStringProperty(tipoProyecto);
        this.fechaSubida = new SimpleObjectProperty<>(fechaSubida);
        this.estadoFormatoA = new SimpleStringProperty(estadoFormatoA);
        this.nroVersion = new SimpleIntegerProperty(nroVersion);
        this.nombreFormatoA.set(nombreFormatoA);
    }

    // --- Getters normales ---
    public String getId() { return id.get(); }
    public String getNombreProyecto() { return nombreProyecto.get(); }
    public String getNombreDirector() { return nombreDirector.get(); }
    public String getTipoProyecto() { return tipoProyecto.get(); }
    public LocalDate getFechaSubida() { return fechaSubida.get(); }
    public String getEstadoFormatoA() { return estadoFormatoA.get(); }
    public int getNroVersion() { return nroVersion.get(); }
    public String getNombreFormatoA() { return nombreFormatoA.get(); }

    // --- Properties para TableView ---
    public StringProperty idProperty() { return id; }
    public StringProperty nombreProyectoProperty() { return nombreProyecto; }
    public StringProperty nombreDirectorProperty() { return nombreDirector; }
    public StringProperty tipoProyectoProperty() { return tipoProyecto; }
    public ObjectProperty<LocalDate> fechaSubidaProperty() { return fechaSubida; }
    public StringProperty estadoFormatoAProperty() { return estadoFormatoA; }
    public IntegerProperty nroVersionProperty() { return nroVersion; }

    // --- Setter para nombre formato ---
    public void setNombreFormatoA(String value) {
        this.nombreFormatoA.set(value);
    }
}
