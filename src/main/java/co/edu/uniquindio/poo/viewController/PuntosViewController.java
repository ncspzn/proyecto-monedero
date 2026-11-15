package co.edu.uniquindio.poo.viewController;

import co.edu.uniquindio.poo.model.Beneficio;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Monedero;
import co.edu.uniquindio.poo.model.Rango;
import co.edu.uniquindio.poo.model.RegistroPuntos;
import co.edu.uniquindio.poo.model.Transaccion;
import co.edu.uniquindio.poo.model.servicio.Banco;
import co.edu.uniquindio.poo.model.servicio.ServicioPuntos;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import static co.edu.uniquindio.poo.viewController.Utilidades.FECHA;
import static co.edu.uniquindio.poo.viewController.Utilidades.col;
import static co.edu.uniquindio.poo.viewController.Utilidades.ejecutar;
import static co.edu.uniquindio.poo.viewController.Utilidades.invertida;
import static co.edu.uniquindio.poo.viewController.Utilidades.recargar;

public class PuntosViewController {
    @FXML private Label lblPuntos;
    @FXML private Label lblRango;
    @FXML private ProgressBar barraRango;
    @FXML private ComboBox<Monedero> cmbBono;
    @FXML private VBox boxCanjes;
    @FXML private TableView<RegistroPuntos> tblPuntos;

    private final Contexto ctx = Contexto.get();

    @FXML
    private void initialize() {
        tblPuntos.getColumns().add(col("Fecha", r -> r.fecha().format(FECHA)));
        tblPuntos.getColumns().add(col("Puntos", r -> (r.delta() > 0 ? "+" : "") + r.delta()));
        tblPuntos.getColumns().add(col("Motivo", RegistroPuntos::motivo));

        for (Beneficio beneficio : Beneficio.values()) {
            Button boton = new Button(String.format("Canjear (%d pts): %s",
                    ServicioPuntos.costoDe(beneficio), ServicioPuntos.descripcionDe(beneficio)));
            boton.setOnAction(e -> ejecutar(() -> {
                ctx.getBanco().canjear(ctx.getClienteActual(), beneficio, cmbBono.getValue());
                return null;
            }, "Canje realizado."));
            boxCanjes.getChildren().add(boton);
        }
        ctx.suscribir(this::refrescar);
    }

    private void refrescar() {
        Cliente cliente = ctx.getClienteActual();
        Rango rango = cliente.getRango();
        lblPuntos.setText("Puntos disponibles: " + cliente.getPuntosDisponibles()
                + "   (acumulados históricos: " + cliente.getPuntosAcumulados() + ")");
        lblRango.setText("Rango: " + rango + String.format(" | -%.0f%% comisión de transferencias | +%.0f%% puntos",
                Banco.descuentoComision(rango) * 100, Transaccion.bonoPuntos(rango) * 100));
        barraRango.setProgress(Cliente.siguienteRango(rango)
                .map(sig -> (double) (cliente.getPuntosAcumulados() - Cliente.puntosMinimos(rango))
                        / (Cliente.puntosMinimos(sig) - Cliente.puntosMinimos(rango)))
                .orElse(1.0));
        recargar(cmbBono, cliente.getMonederos());
        tblPuntos.getItems().setAll(invertida(cliente.getHistorialPuntos()));
    }
}
