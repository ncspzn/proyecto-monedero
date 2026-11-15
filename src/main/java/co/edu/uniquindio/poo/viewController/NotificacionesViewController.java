package co.edu.uniquindio.poo.viewController;

import co.edu.uniquindio.poo.model.CanalNotificacion;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

import static co.edu.uniquindio.poo.viewController.Utilidades.FECHA;
import static co.edu.uniquindio.poo.viewController.Utilidades.invertida;

public class NotificacionesViewController {
    @FXML private ComboBox<CanalNotificacion> cmbCanal;
    @FXML private ListView<String> lstNotificaciones;

    private final Contexto ctx = Contexto.get();

    @FXML
    private void initialize() {
        cmbCanal.getItems().setAll(CanalNotificacion.values());
        ctx.suscribir(this::refrescar);
    }

    @FXML
    private void cambiarCanal() {
        if (ctx.getClienteActual() != null && cmbCanal.getValue() != null) {
            ctx.getClienteActual().setCanalPreferido(cmbCanal.getValue());
        }
    }

    private void refrescar() {
        cmbCanal.setValue(ctx.getClienteActual().getCanalPreferido());
        lstNotificaciones.getItems().setAll(invertida(ctx.getClienteActual().getNotificaciones()).stream()
                .map(n -> n.fecha().format(FECHA) + " [" + n.canal() + "] " + n.mensaje())
                .toList());
    }
}
