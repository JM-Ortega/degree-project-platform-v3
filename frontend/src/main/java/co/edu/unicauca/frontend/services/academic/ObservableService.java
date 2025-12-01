package co.edu.unicauca.frontend.services.academic;

public interface ObservableService {
    void addObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers();
}
