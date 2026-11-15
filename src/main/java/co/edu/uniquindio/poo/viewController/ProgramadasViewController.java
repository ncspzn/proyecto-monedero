package co.edu.uniquindio.poo.viewController;

import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Frecuencia;
import co.edu.uniquindio.poo.model.Monedero;
import co.edu.uniquindio.poo.model.TipoTransaccion;
import co.edu.uniquindio.poo.model.TransaccionProgramada;
import co.edu.uniquindio.poo.model.servicio.Validador;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.List;

import static co.edu.uniquindio.poo.viewController.Utilidades.col;
import static co.edu.uniquindio.poo.viewController.Utilidades.ejecutar;
import static co.edu.uniquindio.poo.viewController.Utilidades.money;
import static co.edu.uniquindio.poo.viewController.Utilidades.recargar;

public class ProgramadasViewController {
    @FXML private ComboBox<TipoTransaccion> cmbTipo;
    @FXML private ComboBox<Monedero> cmbOrigen;
    @FXML private ComboBox<Cliente> cmbDestino;
    @FXML private ComboBox<Frecuencia> cmbFrecuencia;
    @FXML private TextField txtMonto;
    @FXML private DatePicker dpFecha;
    @FXML private DatePicker dpFechaSimulada;
    @FXML private TableView<TransaccionProgramada> tblProgramadas;
    @FXML private TextArea txtLog;

    private final Contexto ctx = Contexto.get();

    @FXML
    private void initialize() {
        tblProgramadas.getColumns().add(col("Próxima fecha", tp -> tp.getProximaEjecucion().toString()));
        tblProgramadas.getColumns().add(col("Tipo", tp -> tp.getTipo().toString()));
        tblProgramadas.getColumns().add(col("Monto", tp -> money(tp.getMonto())));
        tblProgramadas.getColumns().add(col("Frecuencia", tp -> tp.getFrecuencia().toString()));
        tblProgramadas.getColumns().add(col("Origen", tp -> tp.getOrigen().getNombre()));
        tblProgramadas.getColumns().add(col("Destino",
                tp -> tp.getDestino() == null ? "—" : tp.getDestino().getNombre()));

        cmbTipo.getItems().setAll(TipoTransaccion.DEPOSITO, TipoTransaccion.TRANSFERENCIA);
        cmbTipo.getSelectionModel().selectFirst();
        cmbFrecuencia.getItems().setAll(Frecuencia.values());
        cmbFrecuencia.getSelectionModel().select(Frecuencia.MENSUAL);
        dpFecha.setValue(LocalDate.now().plusDays(1));
        dpFechaSimulada.setValue(LocalDate.now());
        ctx.suscribir(this::refrescar);
    }

    @FXML
    private void programar() {
        ejecutar(() -> {
            Cliente destino = cmbDestino.getValue();
            Monedero monederoDestino = cmbTipo.getValue() == TipoTransaccion.TRANSFERENCIA && destino != null
                    ? destino.getMonederoPrincipal() : null;
            ctx.getPlanificador().programar(ctx.getClienteActual(), cmbTipo.getValue(),
                    Validador.parsearMonto(txtMonto.getText()), cmbOrigen.getValue(), monederoDestino,
                    cmbFrecuencia.getValue(), dpFecha.getValue());
            return null;
        }, "Transacción programada.");
    }

    @FXML
    private void procesar() {
        LocalDate fecha = dpFechaSimulada.getValue();
        List<String> resultado = ctx.getPlanificador().procesar(fecha);
        long recordatorios = ctx.getPlanificador().enviarRecordatorios(fecha, 3);
        txtLog.setText(resultado.isEmpty() ? "Nada vencido." : String.join("\n", resultado));
        txtLog.appendText("\nRecordatorios enviados: " + recordatorios);
        ctx.refrescar();
    }

    private void refrescar() {
        Cliente actual = ctx.getClienteActual();
        recargar(cmbOrigen, actual.getMonederos());
        recargar(cmbDestino, ctx.getBanco().getClientes().stream().filter(c -> c != actual).toList());
        tblProgramadas.getItems().setAll(ctx.getPlanificador().ordenadasDe(actual));
    }
}
