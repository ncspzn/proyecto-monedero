package co.edu.uniquindio.poo.viewController;

import co.edu.uniquindio.poo.model.Transaccion;
import co.edu.uniquindio.poo.model.excepciones.MonederoException;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public final class Utilidades {
    public static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FunctionalInterface
    public interface Accion {
        Object ejecutar() throws MonederoException;
    }

    private Utilidades() { }

    public static String money(double valor) { return String.format("$%,.2f", valor); }

    public static <T> TableColumn<T, String> col(String titulo, Function<T, String> extractor) {
        TableColumn<T, String> columna = new TableColumn<>(titulo);
        columna.setCellValueFactory(d -> new SimpleStringProperty(extractor.apply(d.getValue())));
        columna.setPrefWidth(130);
        return columna;
    }

    public static <T> void recargar(ComboBox<T> combo, List<T> items) {
        T seleccionado = combo.getValue();
        combo.getItems().setAll(items);
        int i = seleccionado == null ? -1 : items.indexOf(seleccionado);
        if (i >= 0) combo.getSelectionModel().select(i);
        else if (!items.isEmpty()) combo.getSelectionModel().selectFirst();
    }

    public static <T> List<T> invertida(List<T> lista) {
        List<T> copia = new ArrayList<>(lista);
        Collections.reverse(copia);
        return copia;
    }

    public static void info(String mensaje) { new Alert(Alert.AlertType.INFORMATION, mensaje).showAndWait(); }
    public static void advertencia(String mensaje) { new Alert(Alert.AlertType.WARNING, mensaje).showAndWait(); }
    public static void error(String mensaje) { new Alert(Alert.AlertType.ERROR, mensaje).showAndWait(); }

    public static void ejecutar(Accion accion, String exito) {
        try {
            Object resultado = accion.ejecutar();
            Contexto.get().refrescar();
            if (resultado instanceof Transaccion t && t.isSospechosa()) {
                advertencia(exito + "\n\n⚠ La operación fue marcada como potencialmente sospechosa.");
            } else {
                info(exito);
            }
        } catch (MonederoException ex) {
            error(ex.getMessage());
        }
    }
}
