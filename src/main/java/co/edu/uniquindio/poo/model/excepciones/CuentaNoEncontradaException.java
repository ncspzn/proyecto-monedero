package co.edu.uniquindio.poo.model.excepciones;

public class CuentaNoEncontradaException extends MonederoException {
    public CuentaNoEncontradaException(String recurso, String id) {
        super("No se encontró " + recurso + " con id: " + id);
    }
}
