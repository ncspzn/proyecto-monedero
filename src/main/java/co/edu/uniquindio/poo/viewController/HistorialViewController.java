package co.edu.uniquindio.poo.viewController;

import co.edu.uniquindio.poo.model.TipoTransaccion;
import co.edu.uniquindio.poo.model.Transaccion;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import static co.edu.uniquindio.poo.viewController.Utilidades.FECHA;
import static co.edu.uniquindio.poo.viewController.Utilidades.col;
import static co.edu.uniquindio.poo.viewController.Utilidades.error;
import static co.edu.uniquindio.poo.viewController.Utilidades.ejecutar;
import static co.edu.uniquindio.poo.viewController.Utilidades.money;

public class HistorialViewController {
    @FXML private ComboBox<TipoTransaccion> cmbFiltro;
    @FXML private TableView<Transaccion> tblHistorial;

    private final Contexto ctx = Contexto.get();

    @FXML
    private void initialize() {
        tblHistorial.getColumns().add(col("ID", t -> String.valueOf(t.getId())));
        tblHistorial.getColumns().add(col("Fecha", t -> t.getFecha().format(FECHA)));
        tblHistorial.getColumns().add(col("Tipo", t -> t.getTipo().toString()));
        tblHistorial.getColumns().add(col("Monto", t -> money(t.getMonto())));
        tblHistorial.getColumns().add(col("Comisión", t -> money(t.getComision())));
        tblHistorial.getColumns().add(col("Categoría", Transaccion::getCategoria));
        tblHistorial.getColumns().add(col("Puntos", t -> String.valueOf(t.getPuntosGenerados())));
        tblHistorial.getColumns().add(col("Estado",
                t -> t.isRevertida() ? "Revertida" : t.isSospechosa() ? "⚠ Sospechosa" : "OK"));

        tblHistorial.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Transaccion t, boolean vacio) {
                super.updateItem(t, vacio);
                setStyle(vacio || t == null ? ""
                        : t.isSospechosa() ? "-fx-background-color: #ffe0b2;"
                        : t.isRevertida() ? "-fx-text-fill: gray;" : "");
            }
        });
        cmbFiltro.getItems().setAll(TipoTransaccion.values());
        ctx.suscribir(this::cargar);
    }

    @FXML
    private void filtrar() { cargar(); }

    @FXML
    private void quitarFiltro() {
        cmbFiltro.setValue(null);
        cargar();
    }

    @FXML
    private void revertir() {
        Transaccion seleccionada = tblHistorial.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            error("Selecciona una transacción.");
            return;
        }
        ejecutar(() -> {
            ctx.getBanco().revertir(ctx.getClienteActual(), seleccionada.getId());
            return null;
        }, "Transacción revertida; los puntos fueron recalculados.");
    }

    private void cargar() {
        if (ctx.getClienteActual() == null) return;
        tblHistorial.getItems().setAll(ctx.getReportes().filtrar(
                ctx.getBanco().historialCompleto(ctx.getClienteActual()), cmbFiltro.getValue(), 0));
    }
}
