package co.edu.uniquindio.poo.viewController;

import co.edu.uniquindio.poo.model.Transaccion;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.util.List;

public class ReportesViewController {
    @FXML private TextArea txtInforme;

    private final Contexto ctx = Contexto.get();

    @FXML
    private void initialize() {
        ctx.suscribir(this::refrescar);
    }

    private void refrescar() {
        List<Transaccion> historial = ctx.getBanco().historialCompleto(ctx.getClienteActual());
        txtInforme.setText(ctx.getReportes().informe(ctx.getClienteActual(), historial, ctx.getBanco().getDetector()));
    }
}
