package co.edu.uniquindio.poo.viewController;

import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Monedero;
import co.edu.uniquindio.poo.model.excepciones.MonederoException;
import co.edu.uniquindio.poo.model.servicio.Validador;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import static co.edu.uniquindio.poo.viewController.Utilidades.ejecutar;
import static co.edu.uniquindio.poo.viewController.Utilidades.recargar;

public class OperacionesViewController {
    @FXML private ComboBox<Monedero> cmbMonedero;
    @FXML private ComboBox<Cliente> cmbDestino;
    @FXML private TextField txtMonto;
    @FXML private TextField txtCategoria;

    private final Contexto ctx = Contexto.get();

    @FXML
    private void initialize() {
        ctx.suscribir(this::refrescar);
    }

    @FXML
    private void depositar() {
        ejecutar(() -> ctx.getBanco().depositar(ctx.getClienteActual(), cmbMonedero.getValue(),
                Validador.parsearMonto(txtMonto.getText()), txtCategoria.getText()), "Depósito realizado.");
    }

    @FXML
    private void retirar() {
        ejecutar(() -> ctx.getBanco().retirar(ctx.getClienteActual(), cmbMonedero.getValue(),
                Validador.parsearMonto(txtMonto.getText()), txtCategoria.getText()), "Retiro realizado.");
    }

    @FXML
    private void transferir() {
        ejecutar(() -> {
            Cliente destino = cmbDestino.getValue();
            if (destino == null) {
                throw new MonederoException("Selecciona el cliente destino.");
            }
            return ctx.getBanco().transferir(ctx.getClienteActual(), cmbMonedero.getValue(),
                    destino.getMonederoPrincipal().getId(), Validador.parsearMonto(txtMonto.getText()),
                    txtCategoria.getText());
        }, "Transferencia realizada.");
    }

    private void refrescar() {
        Cliente actual = ctx.getClienteActual();
        recargar(cmbMonedero, actual.getMonederos());
        recargar(cmbDestino, ctx.getBanco().getClientes().stream().filter(c -> c != actual).toList());
    }
}
