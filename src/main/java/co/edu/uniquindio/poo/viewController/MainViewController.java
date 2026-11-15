package co.edu.uniquindio.poo.viewController;

import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.excepciones.MonederoException;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class MainViewController {
    @FXML private ComboBox<Cliente> cmbCliente;

    private final Contexto ctx = Contexto.get();

    @FXML
    private void initialize() {
        cmbCliente.getItems().setAll(ctx.getBanco().getClientes());
        cmbCliente.valueProperty().addListener((obs, anterior, nuevo) -> ctx.setClienteActual(nuevo));
        cmbCliente.getSelectionModel().selectFirst();
    }

    @FXML
    private void nuevoCliente() {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Nuevo cliente");
        TextField txtNombre = new TextField();
        TextField txtCorreo = new TextField();
        TextField txtTelefono = new TextField();
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Nombre:"), txtNombre);
        grid.addRow(1, new Label("Correo:"), txtCorreo);
        grid.addRow(2, new Label("Teléfono:"), txtTelefono);
        dialogo.getDialogPane().setContent(grid);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialogo.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            try {
                Cliente nuevo = ctx.getBanco().registrarCliente(txtNombre.getText(), txtCorreo.getText(), txtTelefono.getText());
                cmbCliente.getItems().setAll(ctx.getBanco().getClientes());
                cmbCliente.setValue(nuevo);
            } catch (MonederoException ex) {
                Utilidades.error(ex.getMessage());
            }
        });
    }
}
