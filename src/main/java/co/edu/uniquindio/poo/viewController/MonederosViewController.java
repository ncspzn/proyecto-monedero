package co.edu.uniquindio.poo.viewController;

import co.edu.uniquindio.poo.model.Monedero;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import static co.edu.uniquindio.poo.viewController.Utilidades.col;
import static co.edu.uniquindio.poo.viewController.Utilidades.ejecutar;
import static co.edu.uniquindio.poo.viewController.Utilidades.money;

public class MonederosViewController {
    @FXML private Label lblResumen;
    @FXML private TableView<Monedero> tblMonederos;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbTipo;

    private final Contexto ctx = Contexto.get();

    @FXML
    private void initialize() {
        tblMonederos.getColumns().add(col("Monedero", Monedero::getNombre));
        tblMonederos.getColumns().add(col("Tipo", Monedero::getTipo));
        tblMonederos.getColumns().add(col("Saldo", m -> money(m.getSaldo())));
        cmbTipo.getItems().setAll("Gastos diarios", "Ahorros");
        cmbTipo.getSelectionModel().selectFirst();
        ctx.suscribir(this::refrescar);
    }

    @FXML
    private void crearMonedero() {
        ejecutar(() -> {
            ctx.getBanco().crearMonedero(ctx.getClienteActual(), txtNombre.getText(), "Ahorros".equals(cmbTipo.getValue()));
            txtNombre.clear();
            return null;
        }, "Monedero creado.");
    }

    private void refrescar() {
        var cliente = ctx.getClienteActual();
        tblMonederos.getItems().setAll(cliente.getMonederos());
        lblResumen.setText("Saldo total: " + money(cliente.getSaldoTotal()) + "   |   Rango: " + cliente.getRango()
                + "   |   Puntos: " + cliente.getPuntosDisponibles());
    }
}
