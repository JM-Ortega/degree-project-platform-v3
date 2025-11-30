package co.edu.unicauca.frontend.services;

public interface ObservableService {
    void addObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers();
}
