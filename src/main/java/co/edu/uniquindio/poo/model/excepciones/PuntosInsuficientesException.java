package co.edu.uniquindio.poo.model.excepciones;

public class PuntosInsuficientesException extends MonederoException {
    public PuntosInsuficientesException(int disponibles, int requeridos) {
        super("Puntos insuficientes: tienes " + disponibles + " y el beneficio cuesta " + requeridos + ".");
    }
}
