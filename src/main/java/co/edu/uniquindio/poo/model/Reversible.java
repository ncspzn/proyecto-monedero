package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.model.excepciones.MonederoException;

public interface Reversible {
    void revertir() throws MonederoException;
    boolean isRevertida();
}
